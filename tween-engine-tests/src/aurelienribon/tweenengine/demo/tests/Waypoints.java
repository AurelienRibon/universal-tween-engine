package aurelienribon.tweenengine.demo.tests;

import aurelienribon.accessors.SpriteAccessor;
import aurelienribon.launcher.Test;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.TweenPaths;
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
		return "";
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
		center(sprites[0], 0, 0);

		Tween.to(sprites[0], SpriteAccessor.CPOS_XY, 4.0f)
			.waypoint(2, 2)
			.waypoint(2, -2)
			.waypoint(-2, 2)
			.waypoint(-2, -2)
			.target(0, 0)
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

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		sprites[0].draw(batch);
		batch.end();
	}
}
