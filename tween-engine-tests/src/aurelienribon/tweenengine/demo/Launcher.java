package aurelienribon.tweenengine.demo;

import aurelienribon.accessors.SpriteAccessor;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Quart;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
	private final OrthographicCamera camera = new OrthographicCamera();
	private final SpriteBatch batch = new SpriteBatch();
	private final BitmapFont tileFont = new BitmapFont();
	private final Sprite background;
	private final Sprite title;
	private final Sprite titleLeft;
	private final Sprite titleRight;
	private final Sprite veil;
	private final float tileW, tileH, titleH;
	private Tile selectedTile;

	public Launcher(Test[] tests) {
		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();
		float wpw = 2;
		float wph = wpw * h / w;

		camera.viewportWidth = wpw;
		camera.viewportHeight = wph;
		camera.update();

		tileFont.setColor(Color.WHITE);
		tileFont.setScale(0.0025f);
		tileFont.setUseIntegerPositions(false);

		TextureAtlas atlas = Assets.inst().get("data/launcher/pack", TextureAtlas.class);
		background = atlas.createSprite("background");
		title = atlas.createSprite("title");
		titleLeft = atlas.createSprite("title-left");
		titleRight = atlas.createSprite("title-right");
		veil = atlas.createSprite("white");

		background.setSize(w, h);
		background.setPosition(0, 0);

		titleLeft.setPosition(0, h);
		titleRight.setPosition(w-titleRight.getWidth(), h);
		title.setSize(w, titleLeft.getHeight());
		title.setPosition(0, h);
		titleH = titleLeft.getHeight() * wph / h;

		veil.setSize(wpw, wph);
		veil.setPosition(-wpw/2, -wph/2);
		Tween.to(veil, SpriteAccessor.OPACITY, 0.7f).target(0).setCallback(veilEndCallback).start(tweenManager);

		Gdx.input.setInputProcessor(launcherInputProcessor);

		tileW = (wpw-TILES_PADDING)/TILES_PER_LINE - TILES_PADDING;
		tileH = tileW * 150 / 250;
		float tileX = -wpw/2 + TILES_PADDING;
		float tileY = wph/2 - tileH - TILES_PADDING - titleH;

		for (int i=0; i<tests.length; i++) {
			tiles.add(new Tile(tileX, tileY, tileW, tileH, tests[i], atlas, camera, tweenManager));

			tileX += tileW + TILES_PADDING;
			if (i > 0 && i%TILES_PER_LINE == TILES_PER_LINE-1) {
				tileX = -camera.viewportWidth/2 + TILES_PADDING;
				tileY += -tileH - TILES_PADDING;
			}
		}
	}

	public void dispose() {
		tweenManager.killAll();
		batch.dispose();
		tileFont.dispose();
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
			background.draw(batch);
			batch.end();

			batch.setProjectionMatrix(camera.combined);
			batch.begin();
			batch.enableBlending();
			for (int i=0; i<tiles.size(); i++) tiles.get(i).draw(batch, tileFont);
			if (veil.getColor().a > 0.1f) veil.draw(batch);
			batch.end();

			batch.getProjectionMatrix().setToOrtho2D(0, 0, w, h);
			batch.begin();
			batch.disableBlending();
			title.draw(batch);
			titleLeft.draw(batch);
			titleRight.draw(batch);
			batch.end();

		} else {
			selectedTile.getTest().render();
		}
	}

	private void showTitle(float delay) {
		float dy = -title.getHeight();
		Tween.to(title, SpriteAccessor.POS_XY, 0.5f).targetRelative(0, dy).delay(delay).ease(Quart.OUT).start(tweenManager);
		Tween.to(titleLeft, SpriteAccessor.POS_XY, 0.5f).targetRelative(0, dy).delay(delay).ease(Quart.OUT).start(tweenManager);
		Tween.to(titleRight, SpriteAccessor.POS_XY, 0.5f).targetRelative(0, dy).delay(delay).ease(Quart.OUT).start(tweenManager);
	}

	private void hideTitle(float delay) {
		float dy = title.getHeight();
		Tween.to(title, SpriteAccessor.POS_XY, 0.3f).targetRelative(0, dy).delay(delay).ease(Cubic.IN).start(tweenManager);
		Tween.to(titleLeft, SpriteAccessor.POS_XY, 0.3f).targetRelative(0, dy).delay(delay).ease(Cubic.IN).start(tweenManager);
		Tween.to(titleRight, SpriteAccessor.POS_XY, 0.3f).targetRelative(0, dy).delay(delay).ease(Cubic.IN).start(tweenManager);
	}

	// -------------------------------------------------------------------------
	// Callbacks
	// -------------------------------------------------------------------------

	private final TweenCallback veilEndCallback = new TweenCallback() {
		@Override
		public void onEvent(int type, BaseTween source) {
			showTitle(0);
			for (int i=0; i<tiles.size(); i++) {
				tiles.get(i).enter(i * 0.1f + 0.3f);
			}
		}
	};

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
		private float firstY;
		private float lastY;

		@Override
		public boolean touchDown(int x, int y, int pointer, int button) {
			firstY = lastY = y;
			isDragged = false;
			return true;
		}

		@Override
		public boolean touchDragged(int x, int y, int pointer) {
			float threshold = 1f * Gdx.graphics.getPpcY();
			if (Math.abs(y - firstY) > threshold && !isDragged) {
				isDragged = true;
				lastY = y;
			}

			if (isDragged) {
				float dy = (y - lastY) * camera.viewportHeight / Gdx.graphics.getHeight();
				camera.translate(0, dy, 0);
				trimCamera();
				camera.update();
				lastY = y;
			}

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
					hideTitle(0.4f);
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
			int linesCntMinusOne = Math.max(tiles.size()-1, 0) / TILES_PER_LINE;
			float min = -linesCntMinusOne * (tileH + TILES_PADDING) + camera.viewportHeight/2;
			float max = 0;

			camera.position.y = Math.max(camera.position.y, min);
			camera.position.y = Math.min(camera.position.y, max);
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
				showTitle(0.2f);
			}

			return false;
		}
	};
}
