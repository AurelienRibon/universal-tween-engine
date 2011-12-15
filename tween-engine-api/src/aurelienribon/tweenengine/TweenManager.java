package aurelienribon.tweenengine;

import java.util.ArrayList;

/**
 * A TweenManager lets you pack many tweens together, and update them at once.
 * Its main interest is that it handles the pooling complexity for you if you
 * decided to enable object pooling using "Tween.enablePooling()".
 * <br/><br/>
 *
 * Just give it a bunch of tweens or timelines to it and call update()
 * periodically.
 *
 * @see Tween
 * @see Timeline
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class TweenManager {
	final ArrayList<BaseTween> objects = new ArrayList<BaseTween>(20);

	/**
	 * Adds a tween to the manager and starts or restarts it.
	 * @return The manager, for instruction chaining.
	 */
	public TweenManager add(Tween tween) {
		if (!objects.contains(tween)) objects.add(tween);
		tween.start();
		return this;
	}

	/**
	 * Adds a timeline to the manager and starts or restarts it.
	 * @return The manager, for instruction chaining.
	 */
	public TweenManager add(Timeline timeline) {
		if (!objects.contains(timeline)) objects.add(timeline);
		timeline.start();
		return this;
	}

	/**
	 * Returns true if the manager contains any valid tween associated to the
	 * given target.
	 */
	public boolean containsTarget(Object target) {
		for (int i=0, n=objects.size(); i<n; i++) {
			BaseTween obj = objects.get(i);
			if (obj.containsTarget(target)) return true;
		}
		return false;
	}

	/**
	 * Returns true if the manager contains any valid tween associated to the
	 * given target and tween type.
	 */
	public boolean containsTarget(Object target, int tweenType) {
		for (int i=0, n=objects.size(); i<n; i++) {
			BaseTween obj = objects.get(i);
			if (obj.containsTarget(target, tweenType)) return true;
		}
		return false;
	}

	/**
	 * Clears the manager from every tween.
	 */
	public void killAll() {
		for (int i=0, n=objects.size(); i<n; i++) {
			BaseTween obj = objects.get(i);
			obj.kill();
		}
	}

	/**
	 * Kills every tween associated to the given target.
	 */
	public void killTarget(Object target) {
		for (int i=0, n=objects.size(); i<n; i++) {
			BaseTween obj = objects.get(i);
			obj.killTarget(target);
		}
	}

	/**
	 * Kills every tween associated to the given target and tween type.
	 */
	public void killTarget(Object target, int tweenType) {
		for (int i=0, n=objects.size(); i<n; i++) {
			BaseTween obj = objects.get(i);
			obj.killTarget(target, tweenType);
		}
	}

	/**
	 * Gets the number of tweens managed by this manager.
	 */
	public int size() {
		int cnt = 0;
		for (int i=0, n=objects.size(); i<n; i++) {
			BaseTween obj = objects.get(i);
			cnt += 1 + obj.getChildrenCount();
		}
		return cnt;
	}

	/**
	 * Increases the minimum capacity of the manager. Defaults to 20.
	 */
	public void ensureCapacity(int minCapacity) {
		objects.ensureCapacity(minCapacity);
	}

	/**
	 * Updates every tweens with a delta time. Handles the tween life-cycles
	 * automatically. If a tween is finished, it will be removed from the
	 * manager. Slow motion, fast motion and backwards play can be easily
	 * achieved by tweaking the deltaMillis given as parameter.
	 */
	public void update(int deltaMillis) {
		if (deltaMillis >= 0) {
			for (int i=0; i<objects.size(); i++) {
				BaseTween obj = objects.get(i);
				if (obj.isFinished()) {
					objects.remove(i);
					i -= 1;
					obj.free();
				} else {
					obj.update(deltaMillis);
				}
			}
		} else {
			for (int i=objects.size()-1; i>=0; i--) {
				BaseTween obj = objects.get(i);
				if (obj.isFinished()) {
					objects.remove(i);
					obj.free();
				} else {
					obj.update(deltaMillis);
				}
			}
		}
	}
}
