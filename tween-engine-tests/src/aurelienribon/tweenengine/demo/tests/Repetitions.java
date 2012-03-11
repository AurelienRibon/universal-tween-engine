package aurelienribon.tweenengine.demo.tests;

import aurelienribon.accessors.SpriteAccessor;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.demo.Test;
import aurelienribon.tweenengine.equations.Cubic;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class Repetitions extends Test {
	private final TweenManager tweenManager = new TweenManager();

	@Override
	public String getTitle() {
		return "Repetitions";
	}

	@Override
	public String getInfo() {
		return "Difference between a 'repeat' behavior and a 'repeat yoyo' behavior.";
	}

	@Override
	public String getImageName() {
		return "tile-repeat";
	}

	@Override
	public InputProcessor getInput() {
		return null;
	}

	@Override
	protected void initializeOverride() {
		createSprites(2);
		enableDots(0);
		enableDots(1);
		center(sprites[0], -3, +1);
		center(sprites[1], -3, -1);

		Tween.to(sprites[0], SpriteAccessor.CPOS_XY, 0.7f)
			.ease(Cubic.INOUT)
			.target(3, 1)
			.repeat(-1, 0.3f)
			.delay(0.5f)
			.start(tweenManager);

		Tween.to(sprites[1], SpriteAccessor.CPOS_XY, 0.7f)
			.ease(Cubic.INOUT)
			.target(3, -1)
			.repeatYoyo(-1, 0.3f)
			.delay(0.5f)
			.start(tweenManager);
	}

	@Override
	protected void disposeOverride() {
		tweenManager.killAll();
	}

	@Override
	protected void renderOverride() {
		tweenManager.update(Gdx.graphics.getDeltaTime());
	}
}
