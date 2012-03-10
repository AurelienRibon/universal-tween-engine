package aurelienribon.launcher;

import aurelienribon.accessors.SpriteAccessor;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class Launcher {
	private static final int TILES_PER_LINE = 3;
	private static final float TILES_PADDING = 0.04f;

	private final List<Tile> tiles = new ArrayList<Tile>();
	private final TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("data/launcher/pack"));
	private final TweenManager tweenManager = new TweenManager();
	private final OrthographicCamera camera = new OrthographicCamera();
	private final SpriteBatch batch = new SpriteBatch();
	private final BitmapFont tileFont = new BitmapFont();
	private final BitmapFont infoFont = new BitmapFont();
	private final Sprite veil = atlas.createSprite("white");
	private final TextureRegion backgroundTexture;
	private final TextureRegion tileTexture;
	private final float tileW, tileH;
	private Tile selectedTile;

	public Launcher(Test[] tests) {
		float wpw = 2;
		float wph = wpw * Gdx.graphics.getHeight() / Gdx.graphics.getWidth();

		camera.viewportWidth = wpw;
		camera.viewportHeight = wph;
		camera.update();

		tileFont.setColor(Color.WHITE);
		tileFont.setScale(0.0025f);
		tileFont.setUseIntegerPositions(false);

		backgroundTexture = atlas.findRegion("background");
		tileTexture = atlas.findRegion("white");

		veil.setSize(wpw, wph);
		veil.setPosition(-wpw/2, -wph/2);
		Tween.set(veil, SpriteAccessor.OPACITY).target(1).start(tweenManager);
		Tween.to(veil, SpriteAccessor.OPACITY, 0.7f).target(0).start(tweenManager);

		Gdx.input.setInputProcessor(launcherInputProcessor);

		tileW = (wpw-TILES_PADDING)/TILES_PER_LINE - TILES_PADDING;
		tileH = tileW * 0.6f;
		float tileX = -wpw/2 + TILES_PADDING;
		float tileY = wph/2 - tileH - TILES_PADDING;

		for (int i=0; i<tests.length; i++) {
			Tile tile = new Tile(tileX, tileY, tileW, tileH, tests[i], tileTexture, camera, tweenManager);
			tile.enter(i * 0.05f + 0.2f);
			tiles.add(tile);

			tileX += tileW + TILES_PADDING;
			if (i > 0 && i%TILES_PER_LINE == TILES_PER_LINE-1) {
				tileX = -camera.viewportWidth/2 + TILES_PADDING;
				tileY += -tileH - TILES_PADDING;
			}
		}
	}

	public void dispose() {
		tweenManager.killAll();
		atlas.dispose();
		batch.dispose();
		tileFont.dispose();
		infoFont.dispose();
	}

	public void render() {
		tweenManager.update(Gdx.graphics.getDeltaTime());

		GL10 gl = Gdx.gl10;
		gl.glClearColor(1, 1, 1, 1);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();

		if (selectedTile == null) {
			batch.getProjectionMatrix().setToOrtho2D(0, 0, w, h);
			batch.begin();
			batch.disableBlending();
			batch.draw(backgroundTexture, 0, 0, w, h);
			batch.end();

			batch.setProjectionMatrix(camera.combined);
			batch.begin();
			batch.enableBlending();
			for (int i=0; i<tiles.size(); i++) tiles.get(i).draw(batch, tileFont);
			if (veil.getColor().a > 0.1f) veil.draw(batch);
			batch.end();

		} else {
			selectedTile.getTest().render();

			if (selectedTile.getTest().getInfo() != null) {
				TextBounds bs = infoFont.getMultiLineBounds(selectedTile.getTest().getInfo());

				batch.getProjectionMatrix().setToOrtho2D(0, 0, w, h);
				batch.begin();
				infoFont.setColor(Color.GRAY);
				infoFont.drawMultiLine(batch, selectedTile.getTest().getInfo(), 10, bs.height + 10);
				batch.end();
			}
		}
	}

	// -------------------------------------------------------------------------
	// Callbacks
	// -------------------------------------------------------------------------

	private final TweenCallback maximizeCallback = new TweenCallback() {
		@Override
		public void onEvent(int type, BaseTween source) {
			selectedTile = (Tile) source.getUserData();
			selectedTile.getTest().initialize();
			Gdx.input.setInputProcessor(testInputMultiplexer);
			Gdx.input.setCatchBackKey(true);

			testInputMultiplexer.clear();
			testInputMultiplexer.addProcessor(testInputProcessor);
			if (selectedTile.getTest().getInput() != null) {
				testInputMultiplexer.addProcessor(selectedTile.getTest().getInput());
			}
		}
	};

	private final TweenCallback minimizeCallback = new TweenCallback() {
		@Override
		public void onEvent(int type, BaseTween source) {
			Tile tile = (Tile) source.getUserData();
			tile.getTest().dispose();
			Gdx.input.setInputProcessor(launcherInputProcessor);
			Gdx.input.setCatchBackKey(false);
		}
	};

	// -------------------------------------------------------------------------
	// Inputs
	// -------------------------------------------------------------------------

	private final InputProcessor launcherInputProcessor = new InputAdapter() {
		private boolean isDragged;
		private float lastY;

		@Override
		public boolean touchDown(int x, int y, int pointer, int button) {
			lastY = y;
			isDragged = false;
			return true;
		}

		@Override
		public boolean touchDragged(int x, int y, int pointer) {
			float dy = (y - lastY) * camera.viewportHeight / Gdx.graphics.getHeight();
			camera.translate(0, dy, 0);
			trimCamera();
			camera.update();

			lastY = y;
			isDragged = true;
			return true;
		}

		@Override
		public boolean touchUp(int x, int y, int pointer, int button) {
			if (!isDragged) {
				Vector3 v = new Vector3(x, y, 0);
				camera.unproject(v);

				Tile tile = getOverTile(v.x, v.y);

				if (tile != null) {
					tiles.remove(tile);
					tiles.add(tile);
					tile.maximize(maximizeCallback);
					Gdx.input.setInputProcessor(null);
				}
			}

			return true;
		}

		@Override
		public boolean scrolled(int amount) {
			camera.position.y += amount > 0 ? -0.1f : 0.1f;
			trimCamera();
			camera.update();
			return true;
		}

		private Tile getOverTile(float x, float y) {
			for (int i=0; i<tiles.size(); i++)
				if (tiles.get(i).isOver(x, y)) return tiles.get(i);
			return null;
		}

		private void trimCamera() {
			camera.position.y = Math.max(camera.position.y, -(tiles.size()/TILES_PER_LINE)*(tileH + TILES_PADDING) + camera.viewportHeight/2);
			camera.position.y = Math.min(camera.position.y, 0);
		}
	};

	private final InputMultiplexer testInputMultiplexer = new InputMultiplexer();
	private final InputProcessor testInputProcessor = new InputAdapter() {
		@Override
		public boolean keyDown(int keycode) {
			if ((keycode == Keys.BACK || keycode == Keys.ESCAPE) && selectedTile != null) {
				selectedTile.minimize(minimizeCallback);
				selectedTile = null;
				Gdx.input.setInputProcessor(null);
			}

			return false;
		}
	};
}
