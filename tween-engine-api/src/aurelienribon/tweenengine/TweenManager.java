package aurelienribon.tweenengine;

import java.util.ArrayList;

/**
 * A TweenManager lets you pack many tweens together, and update them at once.
 * Its main interest is that it handles the pooling complexity for you if you
 * decided to enable object pooling using "Tween.setPoolEnabled()".
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
	final ArrayList<Tween> tweens = new ArrayList<Tween>(20);

	/**
	 * Adds a tween to the manager and starts or restarts it.
	 * @return The manager, for instruction chaining.
	 */
	public TweenManager add(Tween tween) {
		tween.start(this);
		return this;
	}

	/**
	 * Adds a timeline to the manager and starts or restarts it.
	 * @return The manager, for instruction chaining.
	 */
	public TweenManager add(Timeline timeline) {
		timeline.start(this);
		return this;
	}

	/**
	 * Returns true if the manager contains any valid tween associated to the
	 * given target.
	 */
	public boolean contains(Object target) {
		for (int i=0, n=tweens.size(); i<n; i++) {
			Tween tween = tweens.get(i);
			if (tween.getTarget() == target && !tween.isFinished())
				return true;
		}
		return false;
	}

	/**
	 * Returns true if the manager contains any valid tween associated to the
	 * given target and tween type.
	 */
	public boolean contains(Object target, int tweenType) {
		for (int i=0, n=tweens.size(); i<n; i++) {
			Tween tween = tweens.get(i);
			if (tween.getTarget() == target && tween.getType() == tweenType && !tween.isFinished())
				return true;
		}
		return false;
	}

	/**
	 * Clears the manager from every tween.
	 */
	public void killAll() {
		tweens.clear();
	}

	/**
	 * Kills every tween associated to the given target.
	 */
	public void kill(Object target) {
		for (int i=0, n=tweens.size(); i<n; i++) {
			Tween t = tweens.get(i);
			if (t.getTarget() == target) t.kill();
		}
	}

	/**
	 * Kills every tween associated to the given target and tween type.
	 */
	public void kill(Object target, int tweenType) {
		for (int i=0, n=tweens.size(); i<n; i++) {
			Tween t = tweens.get(i);
			if (t.getTarget() == target && t.getType() == tweenType) t.kill();
		}
	}

	/**
	 * Gets the number of tweens managed by this manager.
	 */
	public int size() {
		return tweens.size();
	}

	/**
	 * Increases the minimum capacity of the manager. Defaults to 20.
	 */
	public void ensureCapacity(int minCapacity) {
		tweens.ensureCapacity(minCapacity);
	}

	/**
	 * Updates every tween with a delta time. Handles the tween life-cycle
	 * automatically. If a tween is finished, it will be removed from the
	 * manager.
	 */
	public void update(int deltaMillis) {
		if (deltaMillis >= 0) {
			for (int i=0; i<tweens.size(); i++) {
				Tween t = tweens.get(i);
				if (t.isFinished()) {tweens.remove(i); i -= 1;}
				t.update(deltaMillis);
			}
		} else {
			for (int i=tweens.size()-1; i>=0; i--) {
				Tween t = tweens.get(i);
				if (t.isFinished()) {tweens.remove(i);}
				t.update(deltaMillis);
			}
		}
	}
}
