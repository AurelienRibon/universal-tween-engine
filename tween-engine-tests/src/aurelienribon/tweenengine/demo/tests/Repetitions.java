package aurelienribon.tweenengine.demo.tests;

import aurelienribon.accessors.SpriteAccessor;
import aurelienribon.launcher.Test;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
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
		return "Repeat vs RepeatYoyo";
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
		center(sprites[0], -3, +1);
		center(sprites[1], -3, -1);

		Tween.to(sprites[0], SpriteAccessor.CPOS_XY, 1.5f)
			.ease(Cubic.INOUT)
			.target(3, 1)
			.repeat(-1, 0.5f)
			.delay(0.5f)
			.start(tweenManager);

		Tween.to(sprites[1], SpriteAccessor.CPOS_XY, 1.5f)
			.ease(Cubic.INOUT)
			.target(3, -1)
			.repeatYoyo(-1, 0.5f)
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
		sprites[1].draw(batch);
		batch.end();
	}
}
