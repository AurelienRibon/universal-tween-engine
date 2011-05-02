package aurelienribon.tweenengine;

import java.util.ArrayList;

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
 * @author Aurelien Ribon (aurelien.ribon@gmail.com)
 */
public class TweenManager {

	// -------------------------------------------------------------------------
	// Implementation
	// -------------------------------------------------------------------------

	private final ArrayList<Tween> tweens;

	/**
	 * Instantiates a new manager.
	 */
	public TweenManager() {
		this.tweens = new ArrayList<Tween>(20);
	}

	// -------------------------------------------------------------------------
	// API
	// -------------------------------------------------------------------------

	/**
	 * Adds a new tween to the manager.
	 * @param tween A tween. Does nothing if it is already present.
	 * @return The manager, for instruction chaining.
	 */
	public final TweenManager add(Tween tween) {
		tweens.add(tween);
		return this;
	}

	/**
	 * Adds every tween from a tween group to the manager. Note that the group
	 * will be cleared from its tweens, as says its specification. This is a
	 * mandatory operation for a better management of the memory.
	 * @param tweenGroup A tween group.
	 * @return The manager, for instruction chaining.
	 */
	public final TweenManager add(TweenGroup tweenGroup) {
		while (!tweenGroup.tweens.isEmpty())
			add(tweenGroup.tweens.remove(0));
		return this;
	}

	/**
	 * Clears the manager from every tween.
	 */
	public void clear() {
		tweens.clear();
	}

	/**
	 * Returns true if the manager contains any valid tween associated to the
	 * given target.
	 */
	public final boolean contains(Tweenable target) {
		for (int i=0; i<tweens.size(); i++) {
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
	public final boolean contains(Tweenable target, int tweenType) {
		for (int i=0; i<tweens.size(); i++) {
			Tween tween = tweens.get(i);
			if (tween.getTarget() == target && tween.getTweenType() == tweenType && !tween.isFinished())
				return true;
		}
		return false;
	}

	/**
	 * Kills every valid tween associated to the given target.
	 */
	public final void kill(Tweenable target) {
		for (int i=0; i<tweens.size(); i++) {
			Tween tween = tweens.get(i);
			if (tween.getTarget() == target && !tween.isFinished())
				tween.kill();
		}
	}

	/**
	 * Kills every valid tween associated to the given target and tween type.
	 */
	public final void kill(Tweenable target, int tweenType) {
		for (int i=0; i<tweens.size(); i++) {
			Tween tween = tweens.get(i);
			if (tween.getTarget() == target && tween.getTweenType() == tweenType && !tween.isFinished())
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
	 * Gets an array containing every tween in the manager.
	 * <b>Warning:</b> this method allocates an array.
	 */
	public Tween[] getTweens() {
		return tweens.toArray(new Tween[tweens.size()]);
	}

	/**
	 * Gets an array containing every tween in the manager dedicated to the
	 * given target.
	 * <b>Warning:</b> this method allocates an ArrayList and an array.
	 */
	public Tween[] getTweens(Tweenable target) {
		ArrayList<Tween> selectedTweens = new ArrayList<Tween>();
		for (int i=0; i<tweens.size(); i++) {
			Tween tween = tweens.get(i);
			if (tween.getTarget() == target && !tween.isFinished())
				selectedTweens.add(tween);
		}
		return selectedTweens.toArray(new Tween[selectedTweens.size()]);
	}

	/**
	 * Gets an array containing every tween in the manager dedicated to the
	 * given target and tween type.
	 * <b>Warning:</b> this method allocates an ArrayList and an array.
	 */
	public Tween[] getTweens(Tweenable target, int tweenType) {
		ArrayList<Tween> selectedTweens = new ArrayList<Tween>();
		for (int i=0; i<tweens.size(); i++) {
			Tween tween = tweens.get(i);
			if (tween.getTarget() == target && tween.getTweenType() == tweenType && !tween.isFinished())
				selectedTweens.add(tween);
		}
		return selectedTweens.toArray(new Tween[selectedTweens.size()]);
	}

	/**
	 * Updates every tween with the current time. Handles the tween life-cycle
	 * automatically. If a tween is finished, it will be removed from the
	 * manager.
	 */
	public final void update() {
		long currentMillis = System.currentTimeMillis();

		for (int i=0; i<tweens.size(); i++) {
			Tween tween = tweens.get(i);
			if (tween.isFinished()) {
				tweens.remove(i);
				i -= 1;
			}
			tween.update(currentMillis);
		}
	}
}
