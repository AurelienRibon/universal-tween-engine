package aurelienribon.tweenengine;

import java.util.ArrayList;

/**
 * A TweenManager updates all your tweens and timelines at once.
 * Its main interest is that it handles the tween/timeline life-cycles for you,
 * as well as the pooling constraints (if object pooling is enabled).
 * <br/><br/>
 *
 * Just give it a bunch of tweens or timelines and call update() periodically,
 * you don't need to care for anything else! Relax and enjoy your animations.
 *
 * @see Tween
 * @see Timeline
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class TweenManager {
	// -------------------------------------------------------------------------
	// Static API
	// -------------------------------------------------------------------------

	/**
	 * Disables or enables the "auto remove" mode of any tween manager for a
	 * particular tween or timeline. This mode is activated by default. The
	 * interest of desactivating it is to prevent some tweens or timelines from
	 * being automatically removed from a manager once they are finished.
	 * Therefore, if you update a manager backwards, the tweens or timelines
	 * will be played again, even if they were finished.
	 */
	public static void setAutoRemove(BaseTween object, boolean value) {
		object.isAutoRemoveEnabled = value;
	}

	/**
	 * Disables or enables the "auto free" mode of any tween manager for a
	 * particular tween or timeline. This mode is activated by default.
	 * Commonly, tweens and timelines are automatically freed and removed
	 * from the manager once they are finished. If "auto free" is disabled, a
	 * tween or timeline will be removed from the manager when finished, but not
	 * freed, allowing you to restart it later even if object pooling is
	 * enabled.
	 */
	public static void setAutoFree(BaseTween object, boolean value) {
		object.isAutoFreeEnabled = value;
	}

	/**
	 * Disables or enables the "auto start" mode of any tween manager for a
	 * particular tween or timeline. This mode is activated by default. If it
	 * is not enabled, add a tween or timeline to any manager won't start it
	 * automatically, and you'll need to call "start()" manually on your object.
	 */
	public static void setAutoStart(BaseTween object, boolean value) {
		object.isAutoStartEnabled = value;
	}

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	private final ArrayList<BaseTween> objects = new ArrayList<BaseTween>(20);
	private boolean isPaused = false;

	/**
	 * Adds a tween or timeline to the manager and starts or restarts it.
	 * @return The manager, for instruction chaining.
	 */
	public TweenManager add(BaseTween object) {
		if (!objects.contains(object)) objects.add(object);
		if (object.isAutoStartEnabled) object.start();
		return this;
	}

	/**
	 * Returns true if the manager contains any valid interpolation associated
	 * to the given target object.
	 */
	public boolean containsTarget(Object target) {
		for (int i=0, n=objects.size(); i<n; i++) {
			BaseTween obj = objects.get(i);
			if (obj.containsTarget(target)) return true;
		}
		return false;
	}

	/**
	 * Returns true if the manager contains any valid interpolation associated
	 * to the given target object and to the given tween type.
	 */
	public boolean containsTarget(Object target, int tweenType) {
		for (int i=0, n=objects.size(); i<n; i++) {
			BaseTween obj = objects.get(i);
			if (obj.containsTarget(target, tweenType)) return true;
		}
		return false;
	}

	/**
	 * Kills every managed tweens and timelines.
	 */
	public void killAll() {
		for (int i=0, n=objects.size(); i<n; i++) {
			BaseTween obj = objects.get(i);
			obj.kill();
		}
	}

	/**
	 * Kills every tweens associated to the given target. Will also kill every
	 * timelines containing a tween associated to the given target.
	 */
	public void killTarget(Object target) {
		for (int i=0, n=objects.size(); i<n; i++) {
			BaseTween obj = objects.get(i);
			obj.killTarget(target);
		}
	}

	/**
	 * Kills every tweens associated to the given target and tween type. Will
	 * also kill every timelines containing a tween associated to the given
	 * target and tween type.
	 */
	public void killTarget(Object target, int tweenType) {
		for (int i=0, n=objects.size(); i<n; i++) {
			BaseTween obj = objects.get(i);
			obj.killTarget(target, tweenType);
		}
	}

	/**
	 * Increases the minimum capacity of the manager. Defaults to 20.
	 */
	public void ensureCapacity(int minCapacity) {
		objects.ensureCapacity(minCapacity);
	}

	/**
	 * Pauses the manager. Further update calls won't have any effect.
	 */
	public void pause() {
		isPaused = true;
	}

	/**
	 * Resumes the manager. Has no effect is it was no already paused.
	 */
	public void resume() {
		isPaused = false;
	}

	/**
	 * Updates every tweens with a delta time. Handles the tween life-cycles
	 * automatically. If a tween is finished, it will be removed from the
	 * manager. Slow motion, fast motion and backwards play can be easily
	 * achieved by tweaking the deltaMillis given as parameter.
	 */
	public void update(int deltaMillis) {
		for (int i=objects.size()-1; i>=0; i--) {
			BaseTween obj = objects.get(i);

			if (obj.isFinished() && obj.isAutoRemoveEnabled) {
				objects.remove(i);
				if (obj.isAutoFreeEnabled) obj.free();
			}
		}

		if (isPaused) return;

		if (deltaMillis >= 0) {
			for (int i=0, n=objects.size(); i<n; i++) objects.get(i).update(deltaMillis);
		} else {
			for (int i=objects.size()-1; i>=0; i--) objects.get(i).update(deltaMillis);
		}
	}
}
