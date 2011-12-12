package aurelienribon.tweenengine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A TweenManager let you pack many tweens together, and update them at once.
 * Its main interest is that it handles the pooling complexity for you if you
 * decided to enable object pooling using "Tween.setPoolEnabled()".
 *
 * <br/><br/>
 * Just add a bunch of tweens or tween groups to it and call update()
 * periodically.
 *
 * @see Tween
 * @see TweenGroup
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class TweenManager {
	final ArrayList<Tween> tweens = new ArrayList<Tween>(20);
	private long lastUpdateMillis = -1;
	private boolean paused = false;
	private float speedFactor = 1;

	// -------------------------------------------------------------------------
	// API
	// -------------------------------------------------------------------------

	/**
	 * Adds a new tween to the manager and starts it.
	 * @param tween A tween. Does nothing if it is already present.
	 * @return The manager, for instruction chaining.
	 */
	public final TweenManager add(Tween tween) {
		tween.start(this);
		return this;
	}

	/**
	 * Adds every tween from a tween group to the manager, and starts them.
	 * Note that the group will be cleared, as says its specification.
	 * Therefore, only call this method as the last one!
	 * @param group A tween group.
	 * @return The manager, for instruction chaining.
	 */
	public final TweenManager add(TweenGroup group) {
		group.addToManager(this);
		return this;
	}

	/**
	 * Clears the manager from every tween.
	 */
	public void clear() {
		tweens.clear();
	}

	/**
	 * Increases the capacity of the manager directly. Defaults to 20.
	 * @param minCapacity The minimum capacity of the manager.
	 */
	public void ensureCapacity(int minCapacity) {
		tweens.ensureCapacity(minCapacity);
	}

	/**
	 * Returns true if the manager contains any valid tween associated to the
	 * given target.
	 */
	public final boolean contains(Object target) {
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
	public final boolean contains(Object target, int tweenType) {
		for (int i=0, n=tweens.size(); i<n; i++) {
			Tween tween = tweens.get(i);
			if (tween.getTarget() == target && tween.getType() == tweenType && !tween.isFinished())
				return true;
		}
		return false;
	}

	/**
	 * Kills every valid tween associated to the given target.
	 */
	public final void kill(Object target) {
		for (int i=0, n=tweens.size(); i<n; i++) {
			Tween tween = tweens.get(i);
			if (tween.getTarget() == target && !tween.isFinished())
				tween.kill();
		}
	}

	/**
	 * Kills every valid tween associated to the given target and tween type.
	 */
	public final void kill(Object target, int tweenType) {
		for (int i=0, n=tweens.size(); i<n; i++) {
			Tween tween = tweens.get(i);
			if (tween.getTarget() == target && tween.getType() == tweenType && !tween.isFinished())
				tween.kill();
		}
	}

	/**
	 * Gets the number of tweens managed by this manager.
	 * @return The number of tweens in the manager.
	 */
	public int getTweenCount() {
		return tweens.size();
	}

	/**
	 * Gets an unmodifiable list containing every tween in the manager.
	 */
	public List<Tween> getTweens() {
		return Collections.unmodifiableList(tweens);
	}

	/**
	 * Gets an unmodifiable list containing every tween in the manager
	 * dedicated to the given target.
	 */
	public List<Tween> getTweens(TweenAccessor target) {
		List<Tween> selectedTweens = new ArrayList<Tween>();
		for (int i=0, n=tweens.size(); i<n; i++) {
			Tween tween = tweens.get(i);
			if (tween.getTarget() == target && !tween.isFinished())
				selectedTweens.add(tween);
		}
		return Collections.unmodifiableList(selectedTweens);
	}

	/**
	 * Gets an unmodifiable list containing every tween in the manager
	 * dedicated to the given target and tween type.
	 */
	public List<Tween> getTweens(TweenAccessor target, int tweenType) {
		List<Tween> selectedTweens = new ArrayList<Tween>();
		for (int i=0, n=tweens.size(); i<n; i++) {
			Tween tween = tweens.get(i);
			if (tween.getTarget() == target && tween.getType() == tweenType && !tween.isFinished())
				selectedTweens.add(tween);
		}
		return Collections.unmodifiableList(selectedTweens);
	}

	/**
	 * Updates every tween with the current time. Handles the tween life-cycle
	 * automatically. If a tween is finished, it will be removed from the
	 * manager.
	 */
	public final void update() {
		long currentMillis = System.currentTimeMillis();
		int deltaMillis = lastUpdateMillis > 0
			? (int) (currentMillis - lastUpdateMillis)
			: 0;
		lastUpdateMillis = currentMillis;
		update(deltaMillis);
	}

	/**
	 * Updates every tween with a delta time. Handles the tween life-cycle
	 * automatically. If a tween is finished, it will be removed from the
	 * manager. The delta time will be modified according to the current
	 * speed factor.
	 * @return The delta time really applied to the tweens, in milliseconds.
	 */
	public final int update(int deltaMillis) {
		if (paused) return 0;
		deltaMillis *= speedFactor;

		if (deltaMillis >= 0) {
			for (int i=0; i<tweens.size(); i++) {
				Tween tween = tweens.get(i);
				if (tween.isFinished()) {
					tweens.remove(i);
					i -= 1;
				}
				tween.update(deltaMillis);
			}

		} else {
			for (int i=tweens.size()-1; i>=0; i--) {
				Tween tween = tweens.get(i);
				if (tween.isFinished()) {
					tweens.remove(i);
				}
				tween.update(deltaMillis);
			}
		}

		return deltaMillis;
	}

	/**
	 * Changes the speed of every tweens managed by this TweenManager. '1' is
	 * the default. '2' would make the animation run at twice its speed, and so
	 * on. Negative values are possible, they will make the animation go
	 * backwards.
	 * @param speedFactor A speed coefficient.
	 */
	public void setSpeed(float speedFactor) {
		this.speedFactor = speedFactor;
	}

	/**
	 * Pauses the engine. Next update() calls won't have any effect.
	 */
	public void pause() {
		paused = true;
	}

	/**
	 * Resumes the engine to its latest state.
	 */
	public void resume() {
		paused = false;
		lastUpdateMillis = System.currentTimeMillis();
	}
}
