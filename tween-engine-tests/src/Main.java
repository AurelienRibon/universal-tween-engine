
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.demo.App;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import java.util.Locale;


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

		float step = 0.0001f;

		System.out.println("-----------------------------------------------");
		System.out.println("Tween (v:value, lt:localTime, gt:globalTime)");
		System.out.println("-----------------------------------------------");
		testTween(step);

		System.out.println("-----------------------------------------------");
		System.out.println("Timeline (v:value, lt:localTime, gt:globalTime)");
		System.out.println("-----------------------------------------------");
		testTimeline(step);
	}

	private static void testTween(float step) {
		MutableFloat target = new MutableFloat(0);
		Tween t = Tween.to(target, 0, 1.0f)
			.target(1)
			.repeat(2, 1)
			.delay(1)
			.setCallback(buildCallback("t", target))
			.setCallbackTriggers(TweenCallback.ANY)
			.start();

		float acc = 0;
		while (acc < t.getFullDuration()+1) {
			t.update(step);
			acc += step;
		}
		System.out.println("-----------------------------------------------");
		while (acc > -1) {
			t.update(-step);
			acc -= step;
		}
		System.out.println("-----------------------------------------------");
		while (acc < t.getFullDuration()+1) {
			t.update(step);
			acc += step;
		}
	}

	private static void testTimeline(float step) {
		MutableFloat target1 = new MutableFloat(0);
		MutableFloat target2 = new MutableFloat(0);
		MutableFloat target3 = new MutableFloat(0);
		Tween t1 = Tween.call(buildCallback("t1"));
		Tween t2 = Tween.call(buildCallback("t2"));
		Tween t3 = Tween.call(buildCallback("t3"));

		Timeline tl = Timeline.createSequence()
			.push(t1)
			.pushPause(1)
			.push(t2)
			.pushPause(1)
			.push(t3)
			.repeat(2, 1)
			.setCallback(buildCallback("TL"))
			.setCallbackTriggers(TweenCallback.ANY)
			.start();

		float acc = 0;
		while (acc < tl.getFullDuration()+1) {
			tl.update(step);
			acc += step;
		}
		System.out.println("-----------------------------------------------");
		while (acc > -1) {
			tl.update(-step);
			acc -= step;
		}
		System.out.println("-----------------------------------------------");
		while (acc < tl.getFullDuration()+1) {
			tl.update(step);
			acc += step;
		}
	}

	private static TweenCallback buildCallback(final String name, final MutableFloat target) {
		return new TweenCallback() {
			@Override public void onEvent(int type, BaseTween<?> source) {
				String t = type == TweenCallback.BEGIN ? "BEGIN        "
					: type == TweenCallback.START ? "START        "
					: type == TweenCallback.END ? "END          "
					: type == TweenCallback.COMPLETE ? "COMPLETE     "
					: type == TweenCallback.BACK_BEGIN ? "BACK_BEGIN   "
					: type == TweenCallback.BACK_START ? "BACK_START   "
					: type == TweenCallback.BACK_END ? "BACK_END     "
					: type == TweenCallback.BACK_COMPLETE ? "BACK_COMPLETE"
					: "???";

				String str = String.format(Locale.US, "%s %s   lt %.2f   v %.2f", name, t, source.getCurrentTime(), target.floatValue());
				System.out.println(str);
			}
		};
	}

	private static TweenCallback buildCallback(final String name) {
		return new TweenCallback() {
			@Override public void onEvent(int type, BaseTween<?> source) {
				String t = type == TweenCallback.BEGIN ? "BEGIN        "
					: type == TweenCallback.START ? "START        "
					: type == TweenCallback.END ? "END          "
					: type == TweenCallback.COMPLETE ? "COMPLETE     "
					: type == TweenCallback.BACK_BEGIN ? "BACK_BEGIN   "
					: type == TweenCallback.BACK_START ? "BACK_START   "
					: type == TweenCallback.BACK_END ? "BACK_END     "
					: type == TweenCallback.BACK_COMPLETE ? "BACK_COMPLETE"
					: "???";

				String str = String.format(Locale.US, "%s %s   lt %.2f", name, t, source.getCurrentTime());
				System.out.println(str);
			}
		};
	}
}