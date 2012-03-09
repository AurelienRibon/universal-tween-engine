package aurelienribon.tweenengine.tests;

import aurelienribon.gdxtests.SpriteAccessor;
import aurelienribon.gdxtests.Test;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Quad;
import com.badlogic.gdx.InputProcessor;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class TimelineDemo extends Test {
	@Override
	public String getTitle() {
		return "Simple Timeline demo";
	}

	@Override
	public String getInfo() {
		return null;
	}

	@Override
	public InputProcessor getInput() {
		return null;
	}

	@Override
	protected void initializeOverride() {
		createSprites(1);
		center(sprites[0], -3, 0);

		Timeline.createSequence()
			.push(Tween.to(sprites[0], SpriteAccessor.CPOS_XY, 0.7f).target(3, 0).ease(Quad.IN).repeatYoyo(1, 0.2f))
			.beginParallel()
				.push(Tween.to(sprites[0], SpriteAccessor.ROTATION, 1.0f).target(360).ease(Quad.INOUT))
				.push(Tween.to(sprites[0], SpriteAccessor.OPACITY, 0.5f).target(0).ease(Quad.INOUT).repeatYoyo(1, 0))
				.repeat(1, 0.2f)
			.end()
			.push(Tween.set(sprites[0], SpriteAccessor.ROTATION).target(0))
			.push(Tween.to(sprites[0], SpriteAccessor.CPOS_XY, 0.7f).target(0, 0).ease(Quad.IN))
			.push(Tween.to(sprites[0], SpriteAccessor.ROTATION, 0.5f).target(360).ease(Quad.INOUT))
			.repeatYoyo(1, 0.2f)
			.delay(1.0f)
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
