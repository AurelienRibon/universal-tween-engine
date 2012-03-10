package aurelienribon.tweenengine.demo.tests;

import aurelienribon.accessors.SpriteAccessor;
import aurelienribon.launcher.Test;
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
import com.badlogic.gdx.graphics.g2d.Sprite;
import java.util.Locale;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class TimeManipulation extends Test {
	private final TweenManager tweenManager = new TweenManager();
	private boolean canBeRestarted = false;
	private boolean canControlSpeed = false;
	private String text = "";
	private int iterationCnt;

	@Override
	public String getTitle() {
		return "Time manipulation (interactive)";
	}

	@Override
	public String getInfo() {
		return "Drag to change the animation speed";
	}

	@Override
	public InputProcessor getInput() {
		return new InputAdapter() {
			@Override public boolean touchDown(int x, int y, int pointer, int button) {
				if (canBeRestarted) launchAnimation();
				return true;
			}
		};
	}

	@Override
	protected void initializeOverride() {
		createSprites(4);
		sprites[0].setColor(1, 1, 1, 0);
		sprites[1].setColor(1, 1, 1, 0);
		sprites[2].setColor(1, 1, 1, 0);
		sprites[3].setColor(1, 1, 1, 0);
		launchAnimation();
	}

	@Override
	protected void disposeOverride() {
		tweenManager.killAll();
		canBeRestarted = canControlSpeed = false;
	}

	@Override
	protected void renderOverride() {
		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();

		// Tween manager update (speed is based on touch position)

		boolean isTouched = Gdx.input.isButtonPressed(Buttons.LEFT);
		float speed = canControlSpeed && isTouched ? 4f * (Gdx.input.getX()-w/2)/w : 1;
		float delta = Gdx.graphics.getDeltaTime() * speed;
		tweenManager.update(delta);

		// Gdx stuff...

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
		font.draw(batch, String.format(Locale.US, "Current speed: %.2f", speed), 10, Gdx.graphics.getHeight() - 10);
		font.draw(batch, text, 10, Gdx.graphics.getHeight() - 30);
		batch.end();
	}

	// -------------------------------------------------------------------------
	// ANIMATION
	// -------------------------------------------------------------------------

	private void launchAnimation() {
		canControlSpeed = true;
		canBeRestarted = false;
		iterationCnt = 0;
		tweenManager.killAll();

		// The callback (to change the text at the right moments)

		TweenCallback callback = new TweenCallback() {
			@Override public void onEvent(int type, BaseTween source) {
				switch (type) {
					case START: text = "Iteration: " + (++iterationCnt) + " / " + 3; break;
					case BACK_START: text = "Iteration: " + (--iterationCnt) + " / " + 3; break;
					case COMPLETE: text = "Forwards play complete (touch to restart)"; canBeRestarted = true; break;
					case BACK_COMPLETE: text = "Backwards play complete (touch to restart)"; canBeRestarted = true; break;
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
			.repeat(2, 0)
			.start(tweenManager);
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
}
