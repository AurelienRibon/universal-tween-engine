package aurelienribon.tweenengine.demo.tests;

import aurelienribon.accessors.SpriteAccessor;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.TweenPaths;
import aurelienribon.tweenengine.demo.Test;
import aurelienribon.tweenengine.equations.Quad;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class Waypoints extends Test {
	private final TweenManager tweenManager = new TweenManager();

	@Override
	public String getTitle() {
		return "Bezier paths";
	}

	@Override
	public String getInfo() {
		return "Tweens can navigate through waypoints, which define a 'bezier' path (here "
			+ "using a Catmull-Rom spline).";
	}

	@Override
	public String getImageName() {
		return "tile-path";
	}

	@Override
	public InputProcessor getInput() {
		return null;
	}

	@Override
	protected void initializeOverride() {
		createSprites(1);
		enableDots(0);
		center(sprites[0], -3, 2);

		Tween.to(sprites[0], SpriteAccessor.CPOS_XY, 3.0f)
			.waypoint(1, 1)
			.waypoint(-1, -1)
			.waypoint(2, 0)
			.target(3, -1)
			.ease(Quad.INOUT)
			.path(TweenPaths.catmullRom)
			.repeatYoyo(-1, 0.2f)
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
