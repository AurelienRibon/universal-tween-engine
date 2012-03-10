package aurelienribon.tweenengine.demo.tests;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.demo.SplashScreen;
import aurelienribon.tweenengine.demo.Test;
import com.badlogic.gdx.InputProcessor;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class Intro extends Test {
	private SplashScreen splashScreen;

	@Override
	public String getTitle() {
		return "Replay intro";
	}

	@Override
	public String getInfo() {
		return null;
	}

	@Override
	public String getImageName() {
		return "tile-intro";
	}

	@Override
	public InputProcessor getInput() {
		return null;
	}

	@Override
	protected boolean isCustomDisplay() {
		return true;
	}

	@Override
	protected void initializeOverride() {
		splashScreen = new SplashScreen(new TweenCallback() {
			@Override public void onEvent(int type, BaseTween source) {
				forceClose();
			}
		});
	}

	@Override
	protected void disposeOverride() {
		splashScreen.dispose();
		splashScreen = null;
	}

	@Override
	protected void renderOverride() {
		splashScreen.render();
	}

}
