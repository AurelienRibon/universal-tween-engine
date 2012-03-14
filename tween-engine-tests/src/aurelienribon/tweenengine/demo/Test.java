package aurelienribon.tweenengine.demo;

import aurelienribon.accessors.SpriteAccessor;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public abstract class Test {
	private final TweenManager tweenManager = new TweenManager();
	private final TextureAtlas atlas;
	private final Sprite background;
	private final Sprite veil;
	private final Sprite infoBack;
	private final List<Sprite> dots = new ArrayList<Sprite>(50);
	private boolean[] useDots;
	private Callback callback;

	protected final OrthographicCamera camera = new OrthographicCamera();
	protected final SpriteBatch batch = new SpriteBatch();
	protected final Random rand = new Random();
	protected final BitmapFont font;
	protected final float wpw = 10;
	protected final float wph = 10 * Gdx.graphics.getHeight() / Gdx.graphics.getWidth();
	protected Sprite[] sprites;

	public Test() {
		atlas = Assets.inst().get("data/test/pack", TextureAtlas.class);
		background = atlas.createSprite("background");
		veil = atlas.createSprite("white");
		infoBack = atlas.createSprite("white");

		int w = Gdx.graphics.getWidth();
		if (w > 600) font = Assets.inst().get("data/arial-24.fnt", BitmapFont.class);
		else font = Assets.inst().get("data/arial-16.fnt", BitmapFont.class);
	}

	// -------------------------------------------------------------------------
	// Abstract API
	// -------------------------------------------------------------------------

	public abstract String getTitle();
	public abstract String getInfo();
	public abstract String getImageName();
	public abstract InputProcessor getInput();
	protected abstract void initializeOverride();
	protected abstract void disposeOverride();
	protected abstract void renderOverride();

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	public static interface Callback {
		public void closeRequested(Test source);
	}

	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	public void initialize() {
		if (isCustomDisplay()) {
			initializeOverride();
			return;
		}

		camera.viewportWidth = wpw;
		camera.viewportHeight = wph;
		camera.update();

		background.setSize(wpw, wpw * background.getHeight() / background.getWidth());
		background.setPosition(-wpw/2, -background.getHeight()/2);

		veil.setSize(wpw, wph);
		veil.setPosition(-wpw/2, -wph/2);

		infoBack.setColor(0, 0, 0, 0.3f);
		infoBack.setPosition(0, 0);

		initializeOverride();

		Tween.set(veil, SpriteAccessor.OPACITY).target(1).start(tweenManager);
		Tween.to(veil, SpriteAccessor.OPACITY, 0.5f).target(0).start(tweenManager);
	}

	public void dispose() {
		tweenManager.killAll();
		dots.clear();
		sprites = null;
		useDots = null;

		disposeOverride();
	}

	public void render() {
		if (isCustomDisplay()) {
			renderOverride();
			return;
		}

		// update

		tweenManager.update(Gdx.graphics.getDeltaTime());

		for (int i=0; i<dots.size(); i++) {
			if (dots.get(i).getScaleX() < 0.1f) {
				dots.remove(i);
			}
		}

		// render

		GLCommon gl = Gdx.gl;
		gl.glClearColor(1, 1, 1, 1);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.disableBlending();
		background.draw(batch);
		batch.enableBlending();
		for (int i=0; i<dots.size(); i++) dots.get(i).draw(batch);
		for (int i=0; i<sprites.length; i++) sprites[i].draw(batch);
		batch.end();

		renderOverride();

		if (getInfo() != null) {
			int padding = 15;
			BitmapFont.TextBounds bs = font.getWrappedBounds(getInfo(), w - padding*2);
			infoBack.setSize(w, bs.height + padding*2);
			font.setColor(Color.WHITE);

			batch.getProjectionMatrix().setToOrtho2D(0, 0, w, h);
			batch.begin();
			infoBack.draw(batch);
			font.drawWrapped(batch, getInfo(), padding, bs.height + padding, w - padding*2);
			batch.end();
		}

		if (veil.getColor().a > 0.1f) {
			batch.setProjectionMatrix(camera.combined);
			batch.begin();
			veil.draw(batch);
			batch.end();
		}
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	protected boolean isCustomDisplay() {
		return false;
	}

	protected void forceClose() {
		if (callback != null) callback.closeRequested(this);
	}

	protected void createSprites(int cnt) {
		sprites = new Sprite[cnt];
		useDots = new boolean[cnt];

		for (int i=0; i<cnt; i++) {
			int idx = rand.nextInt(400)/100 + 1;
			sprites[i] = atlas.createSprite("sprite" + idx);
			sprites[i].setSize(1f, 1f * sprites[i].getHeight() / sprites[i].getWidth());
			sprites[i].setOrigin(sprites[i].getWidth()/2, sprites[i].getHeight()/2);
			useDots[i] = false;
		}
	}

	protected void center(Sprite sp, float x, float y) {
		sp.setPosition(x - sp.getWidth()/2, y - sp.getHeight()/2);
	}

	protected void enableDots(int spriteId) {
		useDots[spriteId] = true;

		Tween.call(dotCallback)
			.delay(0.02f)
			.repeat(-1, 0.02f)
			.setUserData(spriteId)
			.start(tweenManager);
	}

	protected void disableDots(int spriteId) {
		useDots[spriteId] = false;
	}

	private final Vector2 v2 = new Vector2();
	private final Vector3 v3 = new Vector3();
	protected Vector2 touch2world(int x, int y) {
		v3.set(x, y, 0);
		camera.unproject(v3);
		return v2.set(v3.x, v3.y);
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private final TweenCallback dotCallback = new TweenCallback() {
		@Override
		public void onEvent(int type, BaseTween source) {
			int spriteId = (Integer) source.getUserData();

			if (useDots[spriteId] == false) source.kill();
			Sprite sp = sprites[spriteId];

			Sprite dot = atlas.createSprite("dot");
			dot.setSize(0.2f, 0.2f);
			dot.setOrigin(0.1f, 0.1f);
			dot.setPosition(sp.getX(), sp.getY());
			dot.translate(sp.getWidth()/2, sp.getHeight()/2);
			dot.translate(-dot.getWidth()/2, -dot.getHeight()/2);
			dots.add(dot);
			Tween.to(dot, SpriteAccessor.SCALE_XY, 1.0f).target(0, 0).start(tweenManager);
		}
	};

	// -------------------------------------------------------------------------
	// Dummy
	// -------------------------------------------------------------------------

	public static final Test dummy = new Test() {
		@Override public String getTitle() {return "Dummy test";}
		@Override public String getInfo() {return null;}
		@Override public String getImageName() {return null;}
		@Override public InputProcessor getInput() {return null;}
		@Override protected void initializeOverride() {}
		@Override protected void disposeOverride() {}
		@Override protected void renderOverride() {}
	};
}
