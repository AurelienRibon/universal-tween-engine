package aurelienribon.tweenengine.demo.tests;

import aurelienribon.accessors.SpriteAccessor;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.demo.Test;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Cubic;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class Types extends Test {
	private final TweenManager tweenManager = new TweenManager();

	@Override
	public String getTitle() {
		return "Attributes";
	}

	@Override
	public String getInfo() {
		return "It is up to you to define what attributes you want to animate, "
			+ "you just need imagination!";
	}

	@Override
	public String getImageName() {
		return "tile-types";
	}

	@Override
	public InputProcessor getInput() {
		return null;
	}

	@Override
	protected void initializeOverride() {
		createSprites(5);
		enableDots(2);
		center(sprites[0], -3, +1.5f);
		center(sprites[1], +0, +1.5f);
		center(sprites[2], -3, -1f);
		center(sprites[3], +0, -1f);
		center(sprites[4], +3, -1f);

		Tween.to(sprites[0], SpriteAccessor.SCALE_XY, 0.5f)
			.ease(Back.INOUT)
			.target(2, 2)
			.repeatYoyo(-1, 0.6f)
			.start(tweenManager);

		Tween.to(sprites[1], SpriteAccessor.OPACITY, 0.7f)
			.target(0)
			.repeatYoyo(-1, 0.5f)
			.start(tweenManager);

		Tween.to(sprites[2], SpriteAccessor.CPOS_XY, 1.0f)
			.ease(Back.INOUT)
			.target(3, 1.5f)
			.repeatYoyo(-1, 0.5f)
			.start(tweenManager);

		Tween.to(sprites[3], SpriteAccessor.ROTATION, 1.0f)
			.target(580)
			.ease(Cubic.INOUT)
			.repeatYoyo(-1, 0.7f)
			.start(tweenManager);

		Tween.to(sprites[4], SpriteAccessor.TINT, 2.5f)
			.waypoint(1, 0, 0)
			.waypoint(0, 1, 0)
			.waypoint(0, 0, 1)
			.target(1, 1, 1)
			.repeat(-1, 0.0f)
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
