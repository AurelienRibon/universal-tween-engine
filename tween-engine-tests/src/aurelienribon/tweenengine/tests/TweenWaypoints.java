package aurelienribon.tweenengine.tests;

import aurelienribon.gdxtests.SpriteAccessor;
import aurelienribon.gdxtests.Test;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenPaths;
import aurelienribon.tweenengine.equations.Quad;
import com.badlogic.gdx.InputProcessor;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class TweenWaypoints extends Test {
	@Override
	public String getTitle() {
		return "Tween bezier paths";
	}

	@Override
	public String getInfo() {
		return "";
	}

	@Override
	public InputProcessor getInput() {
		return null;
	}

	@Override
	protected void initializeOverride() {
		createSprites(1);

		Tween.to(sprites[0], SpriteAccessor.CPOS_XY, 2.0f)
			.waypoint(2, 2)
			.waypoint(2, -2)
			.target(0, 0)
			.ease(Quad.INOUT)
			.path(TweenPaths.catmullRom)
			.repeatYoyo(-1, 0.2f)
			.delay(0.5f)
			.start(tweenManager);
	}

	@Override
	protected void disposeOverride() {
		tweenManager.killTarget(sprites[0]);
	}

	@Override
	protected void renderOverride() {
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		sprites[0].draw(batch);
		batch.end();
	}
}
