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
 * <br/>
 * <pre>
 * TweenGroup.sequence(
 *     TweenGroup.parallel(
 *         Tween.set(myObject, OPACITY).target(0),
 *         Tween.set(myObject, SCALE).target(0, 0),
 *     ),
 *     TweenGroup.parallel(
 *          Tween.to(myObject, OPACITY, 500, Quad.INOUT).target(1),
 *          Tween.to(myObject, SCALE, 500, Quad.INOUT).target(1, 1),
 *     ),
 *     TweenGroup.tempo(1000),
 *     Tween.to(myObject, POSITION_X, 500, Quad.INOUT).target(100)
 *     Tween.to(myObject, ROTATION, 500, Quad.INOUT).target(360)
 * ).addToManager(myManager);
 * </pre>
 *
 * Note that you can call sequence(), parallel() and tempo() inside
 * any other sequence() or parallel() call.
 *
 * <br/><br/>
 * <b>Alike individual tweens, add the group to a TweenManager and update the
 * latter periodically. Also, note that groups are pooled if you set
 * "Tween.setPoolEnabled(true)".
 * </b>
 *
 * @see Tween
 * @see TweenManager
 * @author Aurelien Ribon (aurelien.ribon@gmail.com)
 */
public class TweenGroup implements Groupable {
	private static final Pool.Callback<TweenGroup> poolCallback = new Pool.Callback<TweenGroup>() {
		@Override public void act(TweenGroup obj) {
			obj.reset();
			obj.isPooled = Tween.isPoolEnabled();
		}
	};

	static final Pool<TweenGroup> pool = new Pool<TweenGroup>(15, poolCallback) {
		@Override protected TweenGroup getNew() {return new TweenGroup();}
	};
	
	// -------------------------------------------------------------------------
	// STATIC FACTORIES
	// -------------------------------------------------------------------------

	/**
	 * Creates an empty group. The only difference with a call to "new
	 * Tweengroup()" is that the group is got from the pool if tween pooling
	 * is enabled.
	 * @return The TweenGroup created.
	 */
	public static TweenGroup getNew() {
		TweenGroup group = pool.get();
		return group;
	}

	/**
	 * Convenience method to create a group with its elements running as a
	 * sequence. Both following calls lead to the same result:<br/>
	 * <pre>
	 * TweenGroup.sequence(...);
	 * TweenGroup.getNew().addInSequence(...);
	 * </pre>
	 * @param objs A list of objects made of Tweens and/or TweenGroups.
	 * @return The TweenGroup created.
	 */
	public static TweenGroup sequence(Groupable... objs) {
		TweenGroup group = getNew();
		for (int i=0, n=objs.length; i<n; i++)
			group.addInSequence(objs[i]);
		return group;
	}

	/**
	 * Convenience method to create a group with its elements running all in
	 * parallel. Both following calls lead to the same result:<br/>
	 * <pre>
	 * TweenGroup.parallel(...);
	 * TweenGroup.getNew().addInParallel(...);
	 * </pre>
	 * @param objs A list of objects made of Tweens and/or TweenGroups.
	 * @return The TweenGroup created.
	 */
	public static TweenGroup parallel(Groupable... objs) {
		TweenGroup group = getNew();
		for (int i=0, n=objs.length; i<n; i++)
			group.addInParallel(objs[i]);
		return group;
	}

	/**
	 * Convenience method to create a empty group that acts as a tempo. Both
	 * following calls lead to the same result:<br/>
	 * <pre>
	 * TweenGroup.tempo(1000);
	 * TweenGroup.getNew().delay(1000);
	 * </pre>
	 * @param millis The delay duration, in milliseconds.
	 * @return The TweenGroup created.
	 */
	public static TweenGroup tempo(int millis) {
		TweenGroup group = getNew();
		group.delay = millis;
		return group;
	}

	// -------------------------------------------------------------------------
	// IMPLEMENTATION
	// -------------------------------------------------------------------------

	final ArrayList<Groupable> groupables = new ArrayList<Groupable>(10);
	private int duration = 0;
	private int delay = 0;
	boolean isPooled = false;

	public void reset() {
		groupables.clear();
		duration = 0;
		delay = 0;
	}

	// -------------------------------------------------------------------------
	// PUCLIC API
	// -------------------------------------------------------------------------

	/**
	 * Adds a Tween or a TweenGroup and make it run after the current ones.
	 * @param obj A Tween or a TweenGroup.
	 * @return The group, for instruction chaining.
	 */
	public TweenGroup addInSequence(Groupable obj) {
		if (obj != null) {
			if (groupables.isEmpty()) {
				groupables.add(obj);
			} else {
				Groupable last = groupables.get(groupables.size()-1);
				obj.delay(last.getDelay() + last.getDuration());
				groupables.add(obj);
			}
			duration += obj.getDelay() + obj.getDuration();
		}
		return this;
	}

	/**
	 * Adds a list of Tweens and/or TweenGroups and make them run after the
	 * current ones, one after the other.
	 * @param objs A list of objects made of Tweens and/or TweenGroups.
	 * @return The group, for instruction chaining.
	 */
	public TweenGroup addInSequence(Groupable... objs) {
		for (int i=0, n=objs.length; i<n; i++)
			addInSequence(objs[i]);
		return this;
	}

	/**
	 * Adds a Tween or a TweenGroup and make it run in parallel to the current
	 * ones.
	 * @param obj A Tween or a TweenGroup.
	 * @return The group, for instruction chaining.
	 */
	public TweenGroup addInParallel(Groupable obj) {
		if (obj != null) {
			groupables.add(obj);
			duration = Math.max(duration, obj.getDelay() + obj.getDuration());
		}
		return this;
	}

	/**
	 * Adds a list of Tweens and/or TweenGroups and make them run in parallel
	 * to the current ones.
	 * @param objs A list of objects made of Tweens and/or TweenGroups.
	 * @return The group, for instruction chaining.
	 */
	public TweenGroup addInParallel(Groupable... objs) {
		for (int i=0, n=objs.length; i<n; i++)
			addInParallel(objs[i]);
		return this;
	}

	/**
	 * Gets the current duration of the group, in milliseconds.
	 */
	@Override
	public int getDuration() {
		return duration;
	}

	/**
	 * Gets the current delay of the group, in milliseconds.
	 */
	@Override
	public int getDelay() {
		return delay;
	}

	/**
	 * Adds a delay to the group.
	 */
	@Override
	public TweenGroup delay(int millis) {
		this.delay += millis;
		for (int i=0, n=groupables.size(); i<n; i++)
			groupables.get(i).delay(millis);
		return this;
	}
	
	/**
	 * Convenience method to add the group to a manager and start every tween
	 * at once. <b>Note that calling the method will empty the group. Be sure
	 * to call it as the latest method.</b>
	 * @param manager A TweenManager.
	 * @return The group, for instruction chaining.
	 */
	@Override
	public TweenGroup addToManager(TweenManager manager) {
		manager.add(this);
		return this;
	}
}
