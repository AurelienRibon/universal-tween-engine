package aurelienribon.tweenengine.tests;

import aurelienribon.gdxtests.SpriteAccessor;
import aurelienribon.gdxtests.Test;
import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class TweenDemo extends Test {
	@Override
	public String getTitle() {
		return "Simple Tween demo (interactive)";
	}

	@Override
	public String getInfo() {
		return "Click anywhere on the screen to move the sprite";
	}

	@Override
	public InputProcessor getInput() {
		return inputProcessor;
	}

	@Override
	protected void initializeOverride() {
		createSprites(1);
		center(sprites[0], 0, 0);
	}

	@Override
	protected void disposeOverride() {
		tweenManager.killTarget(sprites[0]);
	}

	@Override
	protected void renderOverride() {
		tweenManager.update(Gdx.graphics.getDeltaTime());

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		sprites[0].draw(batch);
		batch.end();
	}

	private final InputProcessor inputProcessor = new InputAdapter() {
		@Override
		public boolean touchDown(int x, int y, int pointer, int button) {
			Vector2 v = touch2world(x, y);

			tweenManager.killTarget(sprites[0]);

			Tween.to(sprites[0], SpriteAccessor.CPOS_XY, 1.0f)
				.target(v.x, v.y)
				.start(tweenManager);

			return true;
		}
	};
}
