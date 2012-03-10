package aurelienribon.tweenengine.demo;

import aurelienribon.accessors.SpriteAccessor;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.demo.tests.Repetitions;
import aurelienribon.tweenengine.demo.tests.SimpleTimeline;
import aurelienribon.tweenengine.demo.tests.SimpleTween;
import aurelienribon.tweenengine.demo.tests.TimeManipulation;
import aurelienribon.tweenengine.demo.tests.Waypoints;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class App implements ApplicationListener {
	private SplashScreen splashScreen;
	private Launcher launcherScreen;
	private boolean isLoaded = false;

	@Override
	public void create() {
		Tween.setWaypointsLimit(10);
		Tween.setCombinedAttributesLimit(3);
		Tween.registerAccessor(Sprite.class, new SpriteAccessor());

		FileHandleResolver resolver = new InternalFileHandleResolver();
		Assets.inst().setLoader(Texture.class, new TextureLoader(resolver));
		Assets.inst().setLoader(TextureAtlas.class, new TextureAtlasLoader(resolver));
		Assets.inst().load("data/splash/pack", TextureAtlas.class);
		Assets.inst().load("data/launcher/pack", TextureAtlas.class);
		Assets.inst().load("data/test/pack", TextureAtlas.class);
		Assets.inst().load("data/test/background.png", Texture.class);
	}

	@Override
	public void dispose() {
		Assets.inst().dispose();
		if (splashScreen != null) splashScreen.dispose();
		if (launcherScreen != null) launcherScreen.dispose();
	}

	@Override
	public void render() {
		if (isLoaded) {
			if (splashScreen != null) splashScreen.render();
			if (launcherScreen != null) launcherScreen.render();
		} else {
			if (Assets.inst().getProgress() < 1) {
				Assets.inst().update();
			} else {
				launch();
				isLoaded = true;
			}
		}
	}

	@Override public void resize(int width, int height) {}
	@Override public void pause() {}
	@Override public void resume() {}

	private void launch() {
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
				splashScreen = null;
				launcherScreen = new Launcher(tests);
			}
		});
	}
}
