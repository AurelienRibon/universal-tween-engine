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
	private OverwriteRules owrRule = OverwriteRules.NONE;

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
		if (!tweens.contains(tween)) {
			switch (owrRule) {
				case NONE:
					tweens.add(tween);
					break;

				case KEEP_NEW:
					for (int i=tweens.size()-1; i>=0; i--) {
						Tween oldTween = tweens.get(i);
						if (oldTween.getTarget() == tween.getTarget()
						&& oldTween.getTweenType() == tween.getTweenType()) {
							tweens.remove(i);
						}
					}
					tweens.add(tween);
					break;

				case KEEP_OLD:
					boolean canAdd = true;
					for (int i=tweens.size()-1; i>=0; i--) {
						Tween oldTween = tweens.get(i);
						if (oldTween.getTarget() == tween.getTarget()
						&& oldTween.getTweenType() == tween.getTweenType()) {
							canAdd = false;
							break;
						}
					}
					if (canAdd)
						tweens.add(tween);
					break;
			}
		}
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

	public TweenManager setOverwriteRule(OverwriteRules rule) {
		owrRule = rule;
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
	 * Gets an array containing all the tweens in the group.
	 * @return An array containing all the tweens in the group.
	 */
	public Tween[] getTweens() {
		return tweens.toArray(new Tween[tweens.size()]);
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
