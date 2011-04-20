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
	public TweenManager add(Tween tween) {
		if (!tweens.contains(tween))
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
	public TweenManager add(TweenGroup tweenGroup) {
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
	 * Returns true if the given tween is managed by this TweenManager.
	 * @param tween A tween.
	 * @return True if the tween is part of this manager.
	 */
	public boolean contains(Tween tween) {
		return tweens.contains(tween);
	}

	/**
	 * Gets the number of tweens managed by this manager.
	 * @return The number of tweens in the manager.
	 */
	public int getTweenCount() {
		return tweens.size();
	}

	/**
	 * Updates every tween with the current time.
	 */
	public final void update() {
		update(System.currentTimeMillis());
	}

	/**
	 * Updates every tween with a custom time. Handles the tween life-cycle
	 * automatically. If a tween is finished, it will be removed from the
	 * manager.
	 * @param currentMillis A time specified in milliseconds.
	 */
	public final void update(long currentMillis) {
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
