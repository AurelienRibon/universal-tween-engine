package aurelienribon.tweenengine.demo.tests;

import aurelienribon.accessors.SpriteAccessor;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.demo.Test;
import aurelienribon.tweenengine.equations.Linear;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class Info extends Test {
	private final TweenManager tweenManager = new TweenManager();
	private final String msg;

	public Info() {
		msg = "The Universal Tween Engine enables the interpolation of every "
			+ "attributes from any object in any Java project (being an opengl "
			+ "or java2d game, an android application, a swing/swt interface "
			+ "or even a console project).\n"
			+ "\n"
			+ ":: Project page\n"
			+ "http://code.google.com/p/java-universal-tween-engine/\n"
			+ "\n"
			+ ":: Developer's blog\n"
			+ "http://www.aurelienribon.com";
	}

	@Override
	public String getTitle() {
		return "Information";
	}

	@Override
	public String getInfo() {
		return null;
	}

	@Override
	public String getImageName() {
		return "tile-info";
	}

	@Override
	public InputProcessor getInput() {
		return null;
	}

	@Override
	protected void initializeOverride() {
		createSprites(1);
		center(sprites[0], wpw/2-0.8f, -wph/2+0.8f);
		Tween.to(sprites[0], SpriteAccessor.ROTATION, 3).target(360).ease(Linear.INOUT).repeat(-1, 0).start(tweenManager);
	}

	@Override
	protected void disposeOverride() {
		tweenManager.killAll();
	}

	@Override
	protected void renderOverride() {
		tweenManager.update(Gdx.graphics.getDeltaTime());

		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();
		batch.getProjectionMatrix().setToOrtho2D(0, 0, w, h);
		batch.begin();
		font.drawWrapped(batch, msg, 20, h-20, w-40);
		batch.end();
	}
}
