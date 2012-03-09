package aurelienribon.gdxtests;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.tests.ComplexDemo;
import aurelienribon.tweenengine.tests.TimelineDemo;
import aurelienribon.tweenengine.tests.TweenDemo;
import aurelienribon.tweenengine.tests.TweenRepetition;
import aurelienribon.tweenengine.tests.TweenWaypoints;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class App implements ApplicationListener {
	private Launcher launcherScreen;

	@Override
	public void create() {
		Tween.setWaypointsLimit(10);
		Tween.setCombinedAttributesLimit(3);
		Tween.registerAccessor(Sprite.class, new SpriteAccessor());

		Test[] tests = new Test[] {
			new TweenDemo(),
			new TweenRepetition(),
			new TimelineDemo(),
			new ComplexDemo(),
			new TweenWaypoints()
		};

		launcherScreen = new Launcher(tests);
	}

	@Override
	public void dispose() {
		launcherScreen.dispose();
	}

	@Override
	public void render() {
		launcherScreen.render();
	}

	@Override public void resize(int width, int height) {}
	@Override public void pause() {}
	@Override public void resume() {}
}
