package aurelienribon.tweenengine;

import java.util.ArrayList;

/**
 * A TweenGroup can be used to group multiple tweens and to act on all of them
 * at once. Its main use lies in the sequence() method, which automatically 
 * delays the tweens so they will be executed one after the other. Another
 * option you might want to give a look is the repeat() method. It allows the
 * repetition of a whole sequence when the last running tween reaches its end.
 *
 * <br/><br/>
 * The following example will move the target horizontal position from its
 * current location to x=200, then from x=200 to x=100, and finally from
 * x=100 to x=200, but this last transition will only occur 1000ms after the
 * previous one. Notice the ".sequence()" method call, if it has not been
 * called, the 3 tweens would have started together, and not one after the
 * other.
 *
 * <br/><br/>
 * <pre>
 * new TweenGroup().pack(
 *     Tween.to(myObject, POSITION_X, 500, Quad.INOUT).target(200),
 *     Tween.to(myObject, POSITION_X, 500, Quad.INOUT).target(100),
 *     Tween.to(myObject, POSITION_X, 500, Quad.INOUT).target(200).delay(1000)
 * ).sequence().start();
 * </pre>
 *
 * Note that you can reuse the same TweenGroup again and again since the pack()
 * method clears its content.
 *
 * <br/><br/>
 * Alike individual tweens, add the group to a TweenManager and update the
 * latter periodically.
 *
 * @see Tween
 * @see TweenManager
 * @author Aurelien Ribon (aurelien.ribon@gmail.com)
 */
public class TweenGroup {

	// -------------------------------------------------------------------------
	// TweenGroup Implementation
	// -------------------------------------------------------------------------

	final ArrayList<Tween> tweens;

	/**
	 * Creates a new TweenGroup.
	 */
	public TweenGroup() {
		tweens = new ArrayList<Tween>(10);
	}

	// -------------------------------------------------------------------------
	// API
	// -------------------------------------------------------------------------

	private static TweenGroup tmp;

	/**
	 * Convenience method to avoid useless allocation. The returned instance is
	 * always the same, be careful.
	 * @return A temporary group instance.
	 */
	public static TweenGroup tmp() {
		if (tmp == null)
			tmp = new TweenGroup();
		return tmp;
	}

	/**
	 * Adds the given tweens to the group. Please note that the internal storage
	 * is cleared at the beginning of the operation.
	 * @param tweens Some tweens to group.
	 * @return The group, for instruction chaining.
	 */
	public TweenGroup pack(Tween... tweens) {
		this.tweens.clear();
		for (int i=0; i<tweens.length; i++)
			this.tweens.add(tweens[i]);
		return this;
	}

	/**
	 * Modifies the delays of every tween in the group in order to sequence
	 * them one after the other.
	 * @return The group, for instruction chaining.
	 */
	public TweenGroup sequence() {
		for (int i=1; i<tweens.size(); i++) {
			Tween tween = tweens.get(i);
			Tween previousTween = tweens.get(i-1);
			tween.delay(previousTween.getDuration() + previousTween.getDelay());
		}
		return this;
	}

	/**
	 * Starts every tween in the group.
	 * @return The group, for instruction chaining.
	 */
	public TweenGroup start() {
		for (int i=0; i<tweens.size(); i++) {
			tweens.get(i).start();
		}
		return this;
	}

	/**
	 * Convenience method to add a delay to every tween in the group.
	 * @param millis A delay, in milliseconds.
	 * @return The group, for instruction chaining.
	 */
	public TweenGroup delay(int millis) {
		for (int i=0; i<tweens.size(); i++) {
			Tween tween = tweens.get(i);
			tween.delay(millis);
		}
		return this;
	}

	/**
	 * Repeats the tween group for a given number of times. For infinity
	 * repeats,use Tween.INFINITY.
	 * 
	 * <br/><br/>
	 * <b>Attention:</b> Using this method is different from setting a repeat
	 * count to every tween individually. Indeed, repetition delays are added
	 * to tweens such that every tween will repeat at the same time, and not
	 * right after each one has ended an iteration. This behavior is illustrated
	 * below:
	 *
	 * <br/><br/>
	 * <pre>
	 * With individual repeat() calls:
	 * Tween 1: -- -- -- -- -- end
	 * Tween 2: ----- ----- ----- ----- ----- end
	 *
	 * With TweenGroup repeat() call:
	 * Tween 1: --    --    --    --    --    end
	 * Tween 2: ----- ----- ----- ----- ----- end
	 * </pre>
	 *
	 * @param count The number of repetitions.
	 * @param delayMillis A delay, in milliseconds, before every repetition.
	 * @return The group, for instruction chaining.
	 */
	public TweenGroup repeat(int count, int delayMillis) {
		int totalDuration = computeDuration();
		for (int i=0; i<tweens.size(); i++) {
			Tween tween = tweens.get(i);
			int delay = totalDuration + delayMillis - (tween.getDuration() + tween.getDelay());
			tween.repeat(count, delay);
		}
		return this;
	}

	// -------------------------------------------------------------------------
	// Private methods
	// -------------------------------------------------------------------------

	private int computeDuration() {
		int duration = 0;
		for (int i=0; i<tweens.size(); i++) {
			Tween tween = tweens.get(i);
			duration = Math.max(duration, tween.getDelay() + tween.getDuration());
		}
		return duration;
	}
}
