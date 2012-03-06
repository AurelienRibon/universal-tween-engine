package aurelienribon.tweenengine.tests;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quart;
import aurelienribon.tweenaccessors.gdx.SpriteAccessor;
import aurelienribon.tweenengine.BaseTween;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.Locale;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class GdxComplexDemo implements ApplicationListener {
	public static void start() {
		new LwjglApplication(new GdxComplexDemo(), "", 500, 200, false);
	}

	private OrthographicCamera camera;
	private SpriteBatch sb;
	private BitmapFont font;

	private Sprite sprite1;
	private Sprite sprite2;
	private Sprite sprite3;
	private Sprite sprite4;

	private boolean canBeRestarted = false;
	private boolean canControlSpeed = false;
	private String text;
	private int iterationCnt;

	private TweenManager tweenManager;

	@Override
	public void create() {
		// GDX stuff...

		Gdx.input.setInputProcessor(inputProcessor);

		float ratio = (float)Gdx.graphics.getWidth() / (float)Gdx.graphics.getHeight();
		camera = new OrthographicCamera(6, 6/ratio);
		sb = new SpriteBatch();
		font = new BitmapFont();
		font.setColor(Color.BLACK);

		sprite1 = new Sprite(new Texture(Gdx.files.internal("data/logo1.png")));
		sprite1.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		sprite1.setSize(1, 1);
		sprite1.setOrigin(0.5f, 0.5f);
		sprite1.setColor(1, 1, 1, 0);

		sprite2 = new Sprite(new Texture(Gdx.files.internal("data/logo2.png")));
		sprite2.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		sprite2.setSize(1, 1);
		sprite2.setOrigin(0.5f, 0.5f);
		sprite2.setColor(1, 1, 1, 0);

		sprite3 = new Sprite(new Texture(Gdx.files.internal("data/logo3.png")));
		sprite3.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		sprite3.setSize(1, 1);
		sprite3.setOrigin(0.5f, 0.5f);
		sprite3.setColor(1, 1, 1, 0);

		sprite4 = new Sprite(new Texture(Gdx.files.internal("data/logo4.png")));
		sprite4.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		sprite4.setSize(1, 1);
		sprite4.setOrigin(0.5f, 0.5f);
		sprite4.setColor(1, 1, 1, 0);

		// Tween engine setup

		Tween.registerAccessor(Sprite.class, new SpriteAccessor());

		// Tween manager creation

		tweenManager = new TweenManager();

		// Demo of Tween.call(). It's just a timer :)

		text = "Idle (auto-start in 2 seconds)";
		Tween.call(new TweenCallback() {
			@Override public void onEvent(int type, BaseTween source) {
				launchAnimation();
				canControlSpeed = true;
			}
		}).delay(2000).start(tweenManager);
	}

	@Override
	public void render() {
		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();
		float x = Gdx.input.getX();
		boolean isLeftBtnHeld = Gdx.input.isButtonPressed(Buttons.LEFT);

		// Tween manager update (speed is based on mouse position)

		float speed = canControlSpeed && isLeftBtnHeld ? 4f * (x-w/2)/w : 1;
		int delta = (int) (Gdx.graphics.getDeltaTime() * 1000 * speed);
		tweenManager.update(delta);

		// Gdx stuff...

		GL10 gl = Gdx.gl10;
		gl.glClearColor(1, 1, 1, 1);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		sb.setProjectionMatrix(camera.combined);
		sb.begin();
		sprite1.draw(sb);
		sprite2.draw(sb);
		sprite3.draw(sb);
		sprite4.draw(sb);
		sb.end();

		sb.getProjectionMatrix().setToOrtho2D(0, 0, w, h);
		sb.begin();
		font.setColor(0.5f, 0.5f, 0.5f, 1);
		font.draw(sb, "Hold left button and move your mouse to change the animation speed", 5, Gdx.graphics.getHeight());
		font.draw(sb, String.format(Locale.US, "Current speed: %.2f", speed), 5, Gdx.graphics.getHeight() - 20);
		font.draw(sb, text, 5, 45);
		font.draw(sb, "Tweens in pool: " + (Tween.getPoolSize() + Timeline.getPoolSize()), 150, 25);
		font.draw(sb, "Running tweens: " + "x", 5, 25);
		sb.end();
	}

	@Override public void resize(int width, int height) {}
	@Override public void pause() {}
	@Override public void resume() {}
	@Override public void dispose() {}

	// -------------------------------------------------------------------------
	// ANIMATION
	// -------------------------------------------------------------------------

	private void launchAnimation() {
		final int repeatCnt = 2;
		iterationCnt = 0;

		// The callback (to change the text at the right moments)

		TweenCallback callback = new TweenCallback() {
			@Override public void onEvent(int type, BaseTween source) {
				switch (type) {
					case START: text = "Iteration: " + (++iterationCnt) + " / " + (repeatCnt+1); break;
					case BACK_START: text = "Iteration: " + (--iterationCnt) + " / " + (repeatCnt+1); break;
					case COMPLETE: text = "Forwards play complete (click to restart)"; canBeRestarted = true; break;
					case BACK_COMPLETE: text = "Backwards play complete (click to restart)"; canBeRestarted = true; break;
				}
			}
		};

		// The animation itself

		Timeline.createParallel()
			.push(buildSequence(sprite1, 1, 0, 1400))
			.push(buildSequence(sprite2, 2, 200, 1000))
			.push(buildSequence(sprite3, 3, 400, 600))
			.push(buildSequence(sprite4, 4, 600, 200))
			.setCallback(callback)
			.setCallbackTriggers(TweenCallback.START | TweenCallback.BACK_START | TweenCallback.COMPLETE | TweenCallback.BACK_COMPLETE)
			.repeat(repeatCnt, 0)
			.start(tweenManager);
	}

	private Timeline buildSequence(Sprite target, int id, int delay1, int delay2) {
		return Timeline.createSequence()
			.push(Tween.set(target, SpriteAccessor.POSITION_XY).target(-0.5f, -0.5f))
			.push(Tween.set(target, SpriteAccessor.SCALE_XY).target(10, 10))
			.push(Tween.set(target, SpriteAccessor.ROTATION).target(0))
			.push(Tween.set(target, SpriteAccessor.OPACITY).target(0))
			.pushPause(delay1)
			.beginParallel()
				.push(Tween.to(target, SpriteAccessor.OPACITY, 1000).target(1).ease(Quart.INOUT))
				.push(Tween.to(target, SpriteAccessor.SCALE_XY, 1000).target(1, 1).ease(Quart.INOUT))
			.end()
			.pushPause(-500)
			.push(Tween.to(target, SpriteAccessor.POSITION_XY, 1000).target((6f/5f)*id - 3 - 0.5f, -0.5f).ease(Back.OUT))
			.push(Tween.to(target, SpriteAccessor.ROTATION, 800).target(360).ease(Cubic.INOUT))
			.pushPause(delay2)
			.beginParallel()
				.push(Tween.to(target, SpriteAccessor.SCALE_XY, 300).target(3, 3).ease(Quad.IN))
				.push(Tween.to(target, SpriteAccessor.OPACITY, 300).target(0).ease(Quad.IN))
			.end();
	}

	// -------------------------------------------------------------------------
	// INPUT
	// -------------------------------------------------------------------------

	private InputProcessor inputProcessor = new InputAdapter() {
		@Override
		public boolean touchUp(int x, int y, int pointer, int button) {
			if (canBeRestarted) {
				canBeRestarted = false;
				tweenManager.killAll();
				launchAnimation();
			}
			return true;
		}
	};
}
