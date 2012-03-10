package aurelienribon.tweenengine.demo;

import aurelienribon.tweenengine.demo.tests.TimeManipulation;
import aurelienribon.tweenengine.demo.tests.SimpleTween;
import aurelienribon.tweenengine.demo.tests.Waypoints;
import aurelienribon.tweenengine.demo.tests.SimpleTimeline;
import aurelienribon.tweenengine.demo.tests.Repetitions;
import aurelienribon.accessors.SpriteAccessor;
import aurelienribon.launcher.Launcher;
import aurelienribon.launcher.Test;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class App implements ApplicationListener {
	private SplashScreen splashScreen;
	private Launcher launcherScreen;

	@Override
	public void create() {
		Tween.setWaypointsLimit(10);
		Tween.setCombinedAttributesLimit(3);
		Tween.registerAccessor(Sprite.class, new SpriteAccessor());

		splashScreen = new SplashScreen(new TweenCallback() {
			@Override public void onEvent(int type, BaseTween source) {
				Test[] tests = new Test[] {
					new SimpleTween(),
					new SimpleTimeline(),
					new Repetitions(),
					new TimeManipulation(),
					new Waypoints()
				};

				splashScreen.dispose();
				launcherScreen = new Launcher(tests);
			}
		});
	}

	@Override
	public void dispose() {
		if (launcherScreen != null) launcherScreen.dispose();
	}

	@Override
	public void render() {
		if (launcherScreen == null) {
			splashScreen.render();
		} else {
			launcherScreen.render();
		}
	}

	@Override public void resize(int width, int height) {}
	@Override public void pause() {}
	@Override public void resume() {}
}
