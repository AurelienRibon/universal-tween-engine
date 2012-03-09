package aurelienribon.gdxtests;

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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
	private final TweenManager tweenManager = new TweenManager();
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private BitmapFont tileFont;
	private BitmapFont infoFont;
	private Texture backgroundTexture;
	private Texture tileTexture;
	private Tile selectedTile;

	private float tileW, tileH;

	public Launcher(Test[] tests) {
		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();

		camera = new OrthographicCamera(2, 2 * h / w);
		batch = new SpriteBatch();

		tileFont = new BitmapFont();
		tileFont.setColor(Color.WHITE);
		tileFont.setScale(0.0025f);
		tileFont.setUseIntegerPositions(false);

		infoFont = new BitmapFont();

		Texture.setEnforcePotImages(false);
		backgroundTexture = new Texture(Gdx.files.internal("data/menu-background.png"));
		tileTexture = new Texture(Gdx.files.internal("data/white.png"));

		Gdx.input.setInputProcessor(launcherInputProcessor);

		tileW = (camera.viewportWidth-TILES_PADDING)/TILES_PER_LINE - TILES_PADDING;
		tileH = tileW * 0.6f;
		float tileX = -camera.viewportWidth/2 + TILES_PADDING;
		float tileY = camera.viewportHeight/2 - tileH - TILES_PADDING;

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
		backgroundTexture.dispose();
		tileTexture.dispose();
		batch.dispose();
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
			if (keycode == Keys.ESCAPE && selectedTile != null) {
				selectedTile.minimize(minimizeCallback);
				selectedTile = null;
				Gdx.input.setInputProcessor(null);
			}
			return false;
		}
	};
}
