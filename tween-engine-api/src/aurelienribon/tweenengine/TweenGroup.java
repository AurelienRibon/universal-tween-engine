package aurelienribon.tweenengine;

import aurelienribon.tweenengine.utils.Pool;
import java.util.ArrayList;

/**
 * A TweenGroup can be used to create complex animations made of sequences and
 * parallel sets of Tweens.
 *
 * <br/><br/>
 * The following example will create an animation sequence composed of 5 parts:
 * <br/>
 * 1. First, Opacity and Scale are reset to 0. <br/>
 * 2. Then, Opacity and Scale are tweened to 1. <br/>
 * 3. Then, the animation is paused for 1s. <br/>
 * 4. Then, Position is tweened to x=100. <br/>
 * 5. Then, Rotation is tweened to 360°. <br/>
 *
 * <br/><br/>
 * <pre>
 * TweenGroup.asSequence(
 *     TweenGroup.asParallel(
 *         Tween.set(myObject, OPACITY).target(0),
 *         Tween.set(myObject, SCALE).target(0, 0),
 *     ),
 *     TweenGroup.asParallel(
 *          Tween.to(myObject, OPACITY, 500, Quad.INOUT).target(1),
 *          Tween.to(myObject, SCALE, 500, Quad.INOUT).target(1, 1),
 *     ),
 *     TweenGroup.asDelay(1000),
 *     Tween.to(myObject, POSITION_X, 500, Quad.INOUT).target(100)
 *     Tween.to(myObject, ROTATION, 500, Quad.INOUT).target(360)
 * ).addToManager(myManager);
 * </pre>
 *
 * Note that you can call .asSequence(), .asParallel() and .asDelay() inside
 * any other .asSequence() or .asParallel() call.
 *
 * <br/><br/>
 * <b>Alike individual tweens, add the group to a TweenManager and update the
 * latter periodically.</b>
 *
 * @see Tween
 * @see TweenManager
 * @author Aurelien Ribon (aurelien.ribon@gmail.com)
 */
public class TweenGroup implements Groupable {
	static final Pool<TweenGroup> pool;

	static {
		pool = new Pool<TweenGroup>(15) {
			@Override protected TweenGroup getNew() {
				return new TweenGroup();
			}
		};
	}
	
	// -------------------------------------------------------------------------
	// STATIC FACTORIES
	// -------------------------------------------------------------------------

	/**
	 * Sequences the tweens given as parameters to call them one after the
	 * other. Note that you can call other .asSequence(), .asParallel() or
	 * .asDelay() methods inside this one.
	 * @param tweens A list of objects made of Tweens and TweenGroups.
	 * @return The TweenGroup created.
	 */
	public static TweenGroup asSequence(Groupable... tweens) {
		TweenGroup group = pool.get();
		group.reset();
		group.isPooled = Tween.isPoolEnabled();

		for (int i=0, n=tweens.length; i<n; i++)
			group.addInSequence(tweens[i]);
		return group;
	}

	/**
	 * Creates a group that will run its tweens at the same time. Note that you
	 * can call other .asSequence(), .asParallel() or .asDelay() methods inside
	 * this one.
	 * @param tweens A list of objects made of Tweens and TweenGroups.
	 * @return The TweenGroup created.
	 */
	public static TweenGroup asParallel(Groupable... tweens) {
		TweenGroup group = pool.get();
		group.reset();
		group.isPooled = Tween.isPoolEnabled();

		for (int i=0, n=tweens.length; i<n; i++)
			group.addInParallel(tweens[i]);
		return group;
	}

	/**
	 * Creates an empty group that acts as a delay. Only useful when called
	 * inside sequences.
	 * @param millis The delay duration, in milliseconds.
	 * @return The TweenGroup created.
	 */
	public static TweenGroup asDelay(int millis) {
		TweenGroup group = new TweenGroup();
		group.reset();
		group.isPooled = Tween.isPoolEnabled();
		group.duration = millis;
		return group;
	}

	// -------------------------------------------------------------------------
	// IMPLEMENTATION
	// -------------------------------------------------------------------------

	final ArrayList<Groupable> groupables = new ArrayList<Groupable>(10);
	boolean isPooled = false;
	private int duration = 0;
	private int delay = 0;

	private void reset() {
		groupables.clear();
		duration = 0;
		delay = 0;
		isPooled = false;
	}

	// -------------------------------------------------------------------------
	// API
	// -------------------------------------------------------------------------

	public void addInSequence(Groupable grp) {
		if (grp == null)
			return;
		if (groupables.isEmpty()) {
			groupables.add(grp);
		} else {
			Groupable last = groupables.get(groupables.size()-1);
			grp.delay(last.getDelay() + last.getDuration());
			groupables.add(grp);
		}
		duration += grp.getDelay() + grp.getDuration();
	}

	public void addInParallel(Groupable grp) {
		if (grp == null)
			return;
		groupables.add(grp);
		duration = Math.max(duration, grp.getDelay() + grp.getDuration());
	}

	/**
	 * Gets the duration of the group.
	 */
	@Override public int getDuration() {
		return duration;
	}

	/**
	 * Gets the delay of the group.
	 */
	@Override public int getDelay() {
		return delay;
	}

	/**
	 * Delays the group.
	 */
	@Override public TweenGroup delay(int millis) {
		this.delay += millis;
		for (int i=0, n=groupables.size(); i<n; i++)
			groupables.get(i).delay(millis);
		return this;
	}
	
	/**
	 * Convenience method to add a group to a manager and start every tween at
	 * once.
	 * @param manager A TweenManager.
	 * @return The group, for instruction chaining.
	 */
	public TweenGroup addToManager(TweenManager manager) {
		manager.add(this);
		return this;
	}
}
