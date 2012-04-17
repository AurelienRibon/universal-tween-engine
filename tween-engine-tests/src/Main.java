
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.demo.App;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;


public class Main {
    public static void main(String[] args) {
		// Demo

		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.resizable = false;
		cfg.vSyncEnabled = true;
		cfg.useGL20 = false;
		cfg.width = 800;
		cfg.height = 480;
		cfg.title = "Tween-Engine tests";
		new LwjglApplication(new App(), cfg);

		// Tests

		final MutableFloat target = new MutableFloat(0);
		TweenManager tweenManager = new TweenManager();

		TweenCallback cb = new TweenCallback() {
			@Override public void onEvent(int type, BaseTween<?> source) {
				System.out.println("tick " + type + " " + target.floatValue());
			}
		};

		Tween tween = Tween.set(target, 0).targetRelative(1)
			.setCallback(cb)
			.setCallbackTriggers(TweenCallback.BEGIN | TweenCallback.START | TweenCallback.END | TweenCallback.COMPLETE)
			.repeat(1, 0);

		Timeline.createSequence()
			.beginParallel()
				.beginSequence()
					.beginParallel()
						.push(tween)
					.end()
				.end()
			.end()
			.start(tweenManager);

		tweenManager.update(10f);
	}
}