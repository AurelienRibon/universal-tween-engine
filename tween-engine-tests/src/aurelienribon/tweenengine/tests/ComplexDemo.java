package aurelienribon.tweenengine.tests;

import aurelienribon.gdxtests.SpriteAccessor;
import aurelienribon.gdxtests.Test;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quart;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.Sprite;
import java.util.Locale;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class ComplexDemo extends Test {
	private final TweenManager manager = new TweenManager();
	private boolean canBeRestarted = false;
	private boolean canControlSpeed = false;
	private String text = "";
	private int iterationCnt;

	@Override
	public String getTitle() {
		return "Complex demo";
	}

	@Override
	public String getInfo() {
		return null;
	}

	@Override
	public InputProcessor getInput() {
		return inputProcessor;
	}

	@Override
	protected void initializeOverride() {
		createSprites(4);
		sprites[0].setColor(1, 1, 1, 0);
		sprites[1].setColor(1, 1, 1, 0);
		sprites[2].setColor(1, 1, 1, 0);
		sprites[3].setColor(1, 1, 1, 0);

		Tween.call(new TweenCallback() {
			@Override public void onEvent(int type, BaseTween source) {
				launchAnimation();
				canControlSpeed = true;
			}
		}).delay(1.0f).start(manager);
	}

	@Override
	protected void disposeOverride() {
		tweenManager.killTarget(sprites[0]);
		tweenManager.killTarget(sprites[1]);
		tweenManager.killTarget(sprites[2]);
		tweenManager.killTarget(sprites[3]);
	}

	@Override
	protected void renderOverride() {
		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();

		// Tween manager update (speed is based on touch position)

		boolean isTouched = Gdx.input.isButtonPressed(Buttons.LEFT);
		float speed = canControlSpeed && isTouched ? 4f * (Gdx.input.getX()-w/2)/w : 1;
		float delta = Gdx.graphics.getDeltaTime() * speed;
		manager.update(delta);

		// Gdx stuff...

		GL10 gl = Gdx.gl10;
		gl.glClearColor(1, 1, 1, 1);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		sprites[0].draw(batch);
		sprites[1].draw(batch);
		sprites[2].draw(batch);
		sprites[3].draw(batch);
		batch.end();

		batch.getProjectionMatrix().setToOrtho2D(0, 0, w, h);
		batch.begin();
		font.setColor(0.5f, 0.5f, 0.5f, 1);
		font.draw(batch, "Drag to change the animation speed", 5, Gdx.graphics.getHeight());
		font.draw(batch, String.format(Locale.US, "Current speed: %.2f", speed), 5, Gdx.graphics.getHeight() - 20);
		font.draw(batch, text, 5, 45);
		font.draw(batch, "Tweens in pool: " + (Tween.getPoolSize() + Timeline.getPoolSize()), 150, 25);
		font.draw(batch, "Running tweens: " + "x", 5, 25);
		batch.end();
	}

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
			.push(buildSequence(sprites[0], 1, 0.0f, 1.4f))
			.push(buildSequence(sprites[1], 2, 0.2f, 1.0f))
			.push(buildSequence(sprites[2], 3, 0.4f, 0.6f))
			.push(buildSequence(sprites[3], 4, 0.6f, 0.2f))
			.setCallback(callback)
			.setCallbackTriggers(TweenCallback.START | TweenCallback.BACK_START | TweenCallback.COMPLETE | TweenCallback.BACK_COMPLETE)
			.repeat(repeatCnt, 0)
			.start(manager);
	}

	private Timeline buildSequence(Sprite target, int id, float delay1, float delay2) {
		return Timeline.createSequence()
			.push(Tween.set(target, SpriteAccessor.POS_XY).target(-0.5f, -0.5f))
			.push(Tween.set(target, SpriteAccessor.SCALE_XY).target(10, 10))
			.push(Tween.set(target, SpriteAccessor.ROTATION).target(0))
			.push(Tween.set(target, SpriteAccessor.OPACITY).target(0))
			.pushPause(delay1)
			.beginParallel()
				.push(Tween.to(target, SpriteAccessor.OPACITY, 1.0f).target(1).ease(Quart.INOUT))
				.push(Tween.to(target, SpriteAccessor.SCALE_XY, 1.0f).target(1, 1).ease(Quart.INOUT))
			.end()
			.pushPause(-0.5f)
			.push(Tween.to(target, SpriteAccessor.POS_XY, 1.0f).target((6f/5f)*id - 3 - 0.5f, -0.5f).ease(Back.OUT))
			.push(Tween.to(target, SpriteAccessor.ROTATION, 0.8f).target(360).ease(Cubic.INOUT))
			.pushPause(delay2)
			.beginParallel()
				.push(Tween.to(target, SpriteAccessor.SCALE_XY, 0.3f).target(3, 3).ease(Quad.IN))
				.push(Tween.to(target, SpriteAccessor.OPACITY, 0.3f).target(0).ease(Quad.IN))
			.end();
	}

	// -------------------------------------------------------------------------
	// INPUT
	// -------------------------------------------------------------------------

	private InputProcessor inputProcessor = new InputAdapter() {
		@Override
		public boolean touchDown(int x, int y, int pointer, int button) {
			if (canBeRestarted) {
				canBeRestarted = false;
				tweenManager.killAll();
				launchAnimation();
			}
			return true;
		}
	};
}
