package aurelienribon.tweenengine;

import java.util.ArrayList;
import java.util.List;

/**
 * Core class of the Tween Engine. It contains many static factory methods to
 * create and instantiate new interpolations.
 *
 * <br/><br/>
 * The common way to create a Tween is by using one of the static constructor,
 * like:
 *
 * <br/><br/>
 * -- Tween.to(...);<br/>
 * -- Tween.from(...);<br/>
 * -- Tween.set(...);<br/>
 * -- Tween.call(...);
 *
 * <br/><br/>
 * The following example will move the target horizontal position from its
 * current value to x=200 and y=300, during 500ms, but only after a delay of
 * 1000ms. The transition will also be repeated 2 times (the starting position
 * is registered at the end of the delay, so the animation will automatically 
 * restart from this registered position).
 *
 * <br/><br/>
 * <pre>
 * Tween.to(myObject, POSITION_XY, 500, Quad.INOUT)
 *      .target(200, 300).delay(1000).repeat(2).addToManager(myManager);
 * </pre>
 *
 * You need to periodicaly update the tween engine, in order to compute the new
 * values. Add it to a TweenManager, it will take care of the tween life-cycle
 * for you!
 *
 * @see Tweenable
 * @see TweenManager
 * @see TweenGroup
 * 
 * @author Aurelien Ribon (aurelien.ribon@gmail.com)
 */
public class Tween implements Groupable {

	// -------------------------------------------------------------------------
	// Static
	// -------------------------------------------------------------------------

	private static boolean isPoolEnabled = false;

	/** If you need to repeat your tween for infinity, use this. */
	public static final int INFINITY = -1;
	/** The maximum number of attributes that can be tweened in a single tween. */
	public static final int MAX_COMBINED_TWEENS = 10;

	/**
	 * Enables or disables the automatic reuse of ended tweens. Pooling prevents
	 * the allocation of a new tween object when using the static constructors,
	 * thus removing the need for garbage collection. Can be quite helpful on
	 * slow or embedded devices.
	 * <br/><br/>
	 * Defaults to false.
	 */
	public static void setPoolEnabled(boolean value) {
		isPoolEnabled = value;
	}

	/**
	 * Returns true if object pooling is enabled.
	 */
	public static boolean isPoolEnabled() {
		return isPoolEnabled;
	}

	/**
	 * Used for debug purpose. Gets the current number of objects that are
	 * waiting in the pool.
	 * @return The current size of the pool.
	 */
	public static int getPoolSize() {
		return pool.size();
	}

	/**
	 * Increases the pool capacity directly. Capacity defaults to 20.
	 * @param minCapacity The minimum capacity of the pool.
	 */
	public static void ensurePoolCapacity(int minCapacity) {
		pool.ensureCapacity(minCapacity);
	}

	/**
	 * Clears every static resources and resets the static instance.
	 */
	public static void dispose() {
		isPoolEnabled = false;
		pool.clear();
	}

	// -------------------------------------------------------------------------

	private static final Pool.Callback<Tween> poolCallback = new Pool.Callback<Tween>() {
		@Override public void onPool(Tween obj) {obj.reset();}
		@Override public void onUnpool(Tween obj) {obj.isPooled = Tween.isPoolEnabled();}
	};

	private static final Pool<Tween> pool = new Pool<Tween>(20, poolCallback) {
		@Override protected Tween create() {
			Tween tween = new Tween(null, -1, 0, null);
			tween.reset();
			return tween;
		}
	};

	// -------------------------------------------------------------------------
	// Factories
	// -------------------------------------------------------------------------

	/**
	 * Convenience method to create a new interpolation.
	 *
	 * <br/><br/>
	 * You need to set the target values of the interpolation by using one
	 * of the ".target()" methods. The interpolation will run from the current
	 * values (retrieved after the delay, if any) to these target values.
	 *
	 * <br/><br/>
	 * The following lines are equivalent (if pooling has been disabled):
	 *
	 * <br/><br/>
	 * <pre>
	 * Tween.to(myObject, Types.POSITION, 1000, Quad.INOUT).target(50, 70);
	 * new Tween(myObject, Types.POSITION, 1000, Quad.INOUT).target(50, 70);
	 * </pre>
	 *
	 * Several options such as delays and callbacks can be added to the tween.
	 * This method hides some of the internal optimizations such as object
	 * reuse for convenience. However, you can control the creation of the
	 * tween by using the classic constructor.
	 * 
	 * @param target The target of the interpolation.
	 * @param tweenType The desired type of interpolation.
	 * @param durationMillis The duration of the interpolation, in milliseconds.
	 * @param equation The easing equation used during the interpolation.
	 * @return The generated Tween.
	 * @see Tweenable
	 * @see TweenEquation
	 */
	public static Tween to(Tweenable target, int tweenType, int durationMillis, TweenEquation equation) {
		Tween tween = pool.get();
		tween.build(target, tweenType, durationMillis, equation);
		return tween;
	}

	/**
	 * Convenience method to create a new interpolation.
	 *
	 * <br/><br/>
	 * You need to set the target values of the interpolation by using one
	 * of the ".target()" methods. The interpolation will run from the current
	 * values (retrieved after the delay, if any) to these target values.
	 *
	 * <br/><br/>
	 * The following lines are equivalent (if pooling has been disabled):
	 *
	 * <br/><br/>
	 * <pre>
	 * Tween.to(myObject, Types.POSITION, 1000, Quad.INOUT).target(50, 70);
	 * new Tween(myObject, Types.POSITION, 1000, Quad.INOUT).target(50, 70);
	 * </pre>
	 *
	 * Several options such as delays and callbacks can be added to the tween.
	 * This method hides some of the internal optimizations such as object
	 * reuse for convenience. However, you can control the creation of the
	 * tween by using the classic constructor.
	 *
	 * @param target The target of the interpolation.
	 * @param durationMillis The duration of the interpolation, in milliseconds.
	 * @param equation The easing equation used during the interpolation.
	 * @return The generated Tween.
	 * @see SimpleTweenable
	 * @see TweenEquation
	 */
	public static Tween to(SimpleTweenable target, int durationMillis, TweenEquation equation) {
		Tween tween = pool.get();
		tween.build(target, 0, durationMillis, equation);
		return tween;
	}

	/**
	 * Convenience method to create a new reversed interpolation.
	 *
	 * <br/><br/>
	 * You need to set the target values of the interpolation by using one
	 * of the ".target()" methods. The interpolation will run from these target
	 * values to the current values (retrieved after the delay, if any).
	 *
	 * <br/><br/>
	 * The following lines are equivalent (if pooling has been disabled):
	 *
	 * <br/><br/>
	 * <pre>
	 * Tween.from(myObject, Types.POSITION, 1000, Quad.INOUT).target(50, 70);
	 * new Tween(myObject, Types.POSITION, 1000, Quad.INOUT).target(50, 70).reverse();
	 * </pre>
	 *
	 * Several options such as delays and callbacks can be added to the tween.
	 * This method hides some of the internal optimizations such as object
	 * reuse for convenience. However, you can control the creation of the
	 * tween by using the classic constructor.
	 *
	 * @param target The target of the interpolation.
	 * @param tweenType The desired type of interpolation.
	 * @param durationMillis The duration of the interpolation, in milliseconds.
	 * @param equation The easing equation used during the interpolation.
	 * @return The generated Tween.
	 * @see Tweenable
	 * @see TweenEquation
	 */
	public static Tween from(Tweenable target, int tweenType, int durationMillis, TweenEquation equation) {
		Tween tween = pool.get();
		tween.build(target, tweenType, durationMillis, equation);
		tween.reverse();
		return tween;
	}

	/**
	 * Convenience method to create a new reversed interpolation.
	 *
	 * <br/><br/>
	 * You need to set the target values of the interpolation by using one
	 * of the ".target()" methods. The interpolation will run from these target
	 * values to the current values (retrieved after the delay, if any).
	 *
	 * <br/><br/>
	 * The following lines are equivalent (if pooling has been disabled):
	 *
	 * <br/><br/>
	 * <pre>
	 * Tween.from(myObject, Types.POSITION, 1000, Quad.INOUT).target(50, 70);
	 * new Tween(myObject, Types.POSITION, 1000, Quad.INOUT).target(50, 70).reverse();
	 * </pre>
	 *
	 * Several options such as delays and callbacks can be added to the tween.
	 * This method hides some of the internal optimizations such as object
	 * reuse for convenience. However, you can control the creation of the
	 * tween by using the classic constructor.
	 *
	 * @param target The target of the interpolation.
	 * @param durationMillis The duration of the interpolation, in milliseconds.
	 * @param equation The easing equation used during the interpolation.
	 * @return The generated Tween.
	 * @see SimpleTweenable
	 * @see TweenEquation
	 */
	public static Tween from(SimpleTweenable target, int durationMillis, TweenEquation equation) {
		Tween tween = pool.get();
		tween.build(target, 0, durationMillis, equation);
		tween.reverse();
		return tween;
	}

	/**
	 * Convenience method to create a new instantaneous interpolation (as a
	 * result, this is not really an "interpolation").
	 *
	 * <br/><br/>
	 * You need to set the target values of the interpolation by using one
	 * of the ".target()" methods. The interpolation will directly apply these
	 * target values. Of course, a delay can be specified, like in every tween.
	 *
	 * <br/><br/>
	 * The following lines are equivalent (if pooling has been disabled):
	 *
	 * <br/><br/>
	 * <pre>
	 * Tween.set(myObject, Types.POSITION).target(50, 70);
	 * new Tween(myObject, Types.POSITION, 0, null).target(50, 70);
	 * </pre>
	 *
	 * Several options such as delays and callbacks can be added to the tween.
	 * This method hides some of the internal optimizations such as object
	 * reuse for convenience. However, you can control the creation of the
	 * tween by using the classic constructor.
	 *
	 * @param target The target of the interpolation.
	 * @param tweenType The desired type of interpolation.
	 * @return The generated Tween.
	 * @see Tweenable
	 */
	public static Tween set(Tweenable target, int tweenType) {
		Tween tween = pool.get();
		tween.build(target, tweenType, 0, null);
		return tween;
	}

	/**
	 * Convenience method to create a new instantaneous interpolation (as a
	 * result, this is not really an "interpolation").
	 *
	 * <br/><br/>
	 * You need to set the target values of the interpolation by using one
	 * of the ".target()" methods. The interpolation will directly apply these
	 * target values. Of course, a delay can be specified, like in every tween.
	 *
	 * <br/><br/>
	 * The following lines are equivalent (if pooling has been disabled):
	 *
	 * <br/><br/>
	 * <pre>
	 * Tween.set(myObject, Types.POSITION).target(50, 70);
	 * new Tween(myObject, Types.POSITION, 0, null).target(50, 70);
	 * </pre>
	 *
	 * Several options such as delays and callbacks can be added to the tween.
	 * This method hides some of the internal optimizations such as object
	 * reuse for convenience. However, you can control the creation of the
	 * tween by using the classic constructor.
	 *
	 * @param target The target of the interpolation.
	 * @return The generated Tween.
	 * @see SimpleTweenable
	 */
	public static Tween set(SimpleTweenable target) {
		Tween tween = pool.get();
		tween.build(target, 0, 0, null);
		return tween;
	}

	/**
	 * Convenience method to create a new simple timer.
	 *
	 * <br/><br/>
	 * You need to set the target values of the interpolation by using one
	 * of the ".target()" methods. The interpolation will run from the current
	 * values (retrieved after the delay, if any) to these target values.
	 *
	 * <br/><br/>
	 * The following lines are equivalent (if pooling has been disabled):
	 *
	 * <br/><br/>
	 * <pre>
	 * Tween.call(myCallback).delay(1000);
	 * new Tween(null, -1, 0, null).addCallback(mycallback).delay(1000);
	 * </pre>
	 *
	 * Several options such as delays and callbacks can be added to the tween.
	 * This method hides some of the internal optimizations such as object
	 * reuse for convenience. However, you can control the creation of the
	 * tween by using the classic constructor.
	 *
	 * @param callback The callback that will be triggered at the end of the
	 * delay (if specified). A repeat behavior can be set to the tween to
	 * trigger it more than once.
	 * @return The generated Tween.
	 * @see TweenCallback
	 */
	public static Tween call(TweenCallback callback) {
		Tween tween = pool.get();
		tween.build(null, -1, 0, null);
		tween.addIterationCompleteCallback(callback);
		return tween;
	}

	// -------------------------------------------------------------------------
	// Attributes
	// -------------------------------------------------------------------------

	// Main
	private Tweenable target;
	private int tweenType;
	private TweenEquation equation;

	// General
	private boolean isReversed;
	private boolean isPooled;
	private boolean isRelative;
	private float speedFactor;

	// Values
	private int combinedTweenCount;
	private final float[] startValues = new float[MAX_COMBINED_TWEENS];
	private final float[] targetValues = new float[MAX_COMBINED_TWEENS];
	private final float[] targetMinusStartValues = new float[MAX_COMBINED_TWEENS];

	// Timings
	private int durationMillis;
	private int delayMillis;
	private int endDelayMillis;
	private int endMillis;
	private int currentMillis;
	private boolean isStarted;
	private boolean isDelayEnded;
	private boolean isEnded;
	private boolean isFinished;

	// Callbacks
	private final List<TweenCallback> startCallbacks = new ArrayList<TweenCallback>(3);;
	private final List<TweenCallback> endOfDelayCallbacks = new ArrayList<TweenCallback>(3);;
	private final List<TweenCallback> iterationCompleteCallbacks = new ArrayList<TweenCallback>(3);;
	private final List<TweenCallback> completeCallbacks = new ArrayList<TweenCallback>(3);;
	private final List<TweenCallback> killCallbacks = new ArrayList<TweenCallback>(3);;
	private final List<TweenCallback> poolCallbacks = new ArrayList<TweenCallback>(3);;

	// Repeat
	private int repeatCnt;
	private int iteration;
	private int repeatDelayMillis;

	// UserData
	private Object userData;

	// Misc
	private final float[] localTmp = new float[MAX_COMBINED_TWEENS];

	// -------------------------------------------------------------------------
	// Ctor
	// -------------------------------------------------------------------------

	/**
	 * Instantiates a new Tween from scratch.
	 * @param target The target of the interpolation.
	 * @param tweenType The desired type of interpolation.
	 * @param durationMillis The duration of the interpolation, in milliseconds.
	 * @param equation The easing equation used during the interpolation.
	 */
	public Tween(Tweenable target, int tweenType, int durationMillis, TweenEquation equation) {
		reset();
		build(target, tweenType, durationMillis, equation);
	}

	private void reset() {
		target = null;
		tweenType = -1;
		equation = null;

		isReversed = false;
		isRelative = false;
		speedFactor = 1;

		combinedTweenCount = 0;

		delayMillis = 0;
		isStarted = false;
		isDelayEnded = false;
		isEnded = false;
		isFinished = true;

		completeCallbacks.clear();
		iterationCompleteCallbacks.clear();
		killCallbacks.clear();
		poolCallbacks.clear();
		startCallbacks.clear();
		endOfDelayCallbacks.clear();

		repeatCnt = 0;
		iteration = 0;
		repeatDelayMillis = 0;

		userData = null;
	}

	private void build(Tweenable target, int tweenType, int durationMillis, TweenEquation equation) {
		reset();

		this.target = target;
		this.tweenType = tweenType;
		this.durationMillis = durationMillis;
		this.equation = equation;

		if (target != null) {
			combinedTweenCount = target.getTweenValues(tweenType, localTmp);
			if (combinedTweenCount < 1 || combinedTweenCount > MAX_COMBINED_TWEENS)
				throw new RuntimeException("Min combined tweens = 1, max = " + MAX_COMBINED_TWEENS);
		}
	}

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	/**
	 * Starts or restart the interpolation. Using this method can lead to some
	 * side-effects if you call it multiple times. <b>The recommanded behavior
	 * is to add the tween to a Tween Manager instead.</b>
	 */
	public Tween start() {
		currentMillis = 0;

		endDelayMillis = iteration == 0
			? delayMillis
			: Math.max(delayMillis + repeatDelayMillis, 0);

		endMillis = endDelayMillis + durationMillis;

		isStarted = true;
		isDelayEnded = false;
		isEnded = false;
		isFinished = false;

		callStartCallbacks();

		return this;
	}

	/**
	 * Sets the target value of the interpolation. The interpolation will run
	 * from the <b>value at start time (after the delay, if any)</b> to this
	 * target value.
	 * <br/><br/>
	 * To sum-up:<br/>
	 * - start value: value at start time, after delay<br/>
	 * - end value: param
	 * @param targetValue The target value of the interpolation.
	 * @return The current tween for chaining instructions.
	 */
	public Tween target(float targetValue) {
		targetValues[0] = targetValue;
		return this;
	}

	/**
	 * Sets the target values of the interpolation. The interpolation will run
	 * from the <b>values at start time (after the delay, if any)</b> to these
	 * target values.
	 * <br/><br/>
	 * To sum-up:<br/>
	 * - start values: values at start time, after delay<br/>
	 * - end values: params
	 * @param targetValue1 The 1st target value of the interpolation.
	 * @param targetValue2 The 2nd target value of the interpolation.
	 * @return The current tween for chaining instructions.
	 */
	public Tween target(float targetValue1, float targetValue2) {
		targetValues[0] = targetValue1;
		targetValues[1] = targetValue2;
		return this;
	}

	/**
	 * Sets the target values of the interpolation. The interpolation will run
	 * from the <b>values at start time (after the delay, if any)</b> to these
	 * target values.
	 * <br/><br/>
	 * To sum-up:<br/>
	 * - start values: values at start time, after delay<br/>
	 * - end values: params
	 * @param targetValue1 The 1st target value of the interpolation.
	 * @param targetValue2 The 2nd target value of the interpolation.
	 * @param targetValue3 The 3rd target value of the interpolation.
	 * @return The current tween for chaining instructions.
	 */
	public Tween target(float targetValue1, float targetValue2, float targetValue3) {
		targetValues[0] = targetValue1;
		targetValues[1] = targetValue2;
		targetValues[2] = targetValue3;
		return this;
	}

	/**
	 * Sets the target values of the interpolation. The interpolation will run
	 * from the <b>values at start time (after the delay, if any)</b> to these
	 * target values.
	 * <br/><br/>
	 * To sum-up:<br/>
	 * - start values: values at start time, after delay<br/>
	 * - end values: params
	 * @param targetValues The target values of the interpolation.
	 * @return The current tween for chaining instructions.
	 */
	public Tween target(float... targetValues) {
		if (targetValues.length > MAX_COMBINED_TWEENS)
			throw new RuntimeException("You cannot set more than " + MAX_COMBINED_TWEENS + " targets.");
		System.arraycopy(targetValues, 0, this.targetValues, 0, targetValues.length);
		return this;
	}

	/**
	 * Sets the target value of the interpolation, relatively to the <b>value
	 * at start time (after the delay, if any)</b>.
	 * <br/><br/>
	 * To sum-up:<br/>
	 * - start value: value at start time, after delay<br/>
	 * - end value: param + value at start time, after delay
	 * @param targetValue The relative target value of the interpolation.
	 * @return The current tween for chaining instructions.
	 */
	public Tween targetRelative(float targetValue) {
		isRelative = true;
		targetValues[0] = targetValue;
		return this;
	}

	/**
	 * Sets the target values of the interpolation, relatively to the <b>values
	 * at start time (after the delay, if any)</b>.
	 * <br/><br/>
	 * To sum-up:<br/>
	 * - start values: values at start time, after delay<br/>
	 * - end values: params + values at start time, after delay
	 * @param targetValue1 The 1st relative target value of the interpolation.
	 * @param targetValue2 The 2nd relative target value of the interpolation.
	 * @return The current tween for chaining instructions.
	 */
	public Tween targetRelative(float targetValue1, float targetValue2) {
		isRelative = true;
		targetValues[0] = targetValue1;
		targetValues[1] = targetValue2;
		return this;
	}

	/**
	 * Sets the target values of the interpolation, relatively to the <b>values
	 * at start time (after the delay, if any)</b>.
	 * <br/><br/>
	 * To sum-up:<br/>
	 * - start values: values at start time, after delay<br/>
	 * - end values: params + values at start time, after delay
	 * @param targetValue1 The 1st relative target value of the interpolation.
	 * @param targetValue2 The 2nd relative target value of the interpolation.
	 * @param targetValue3 The 3rd relative target value of the interpolation.
	 * @return The current tween for chaining instructions.
	 */
	public Tween targetRelative(float targetValue1, float targetValue2, float targetValue3) {
		isRelative = true;
		targetValues[0] = targetValue1;
		targetValues[1] = targetValue2;
		targetValues[2] = targetValue3;
		return this;
	}

	/**
	 * Sets the target values of the interpolation, relatively to the <b>values
	 * at start time (after the delay, if any)</b>.
	 * <br/><br/>
	 * To sum-up:<br/>
	 * - start values: values at start time, after delay<br/>
	 * - end values: params + values at start time, after delay
	 * @param targetValues The relative target values of the interpolation.
	 * @return The current tween for chaining instructions.
	 */
	public Tween targetRelative(float... targetValues) {
		if (targetValues.length > MAX_COMBINED_TWEENS)
			throw new RuntimeException("You cannot set more than " + MAX_COMBINED_TWEENS + " targets.");
		System.arraycopy(targetValues, 0, this.targetValues, 0, targetValues.length);
		isRelative = true;
		return this;
	}

	/**
	 * Sets the target value(s) of the interpolation as <b>the current value(s),
	 * the one(s) present when this call is made</b>.
	 * <br/><br/>
	 * To sum-up:<br/>
	 * - start value: value at start time, after delay<br/>
	 * - end value: value at current time
	 * @return The current tween for chaining instructions.
	 */
	public Tween targetCurrent() {
		target.getTweenValues(tweenType, targetValues);
		return this;
	}

	/**
	 * Sets the target value of the interpolation, relatively to the <b>the
	 * current value, the one present when this call is made</b>.
	 * <br/><br/>
	 * To sum-up:<br/>
	 * - start value: value at start time, after delay<br/>
	 * - end value: param + value at current time
	 * @param targetValue The relative target value of the interpolation.
	 * @return The current tween for chaining instructions.
	 */
	public Tween targetCurrentRelative(float targetValue) {
		target.getTweenValues(tweenType, targetValues);
		targetValues[0] += targetValue;
		return this;
	}

	/**
	 * Sets the target values of the interpolation, relatively to the <b>the
	 * current values, the ones present when this call is made</b>.
	 * <br/><br/>
	 * To sum-up:<br/>
	 * - start values: values at start time, after delay<br/>
	 * - end values: params + values at current time
	 * @param targetValue1 The 1st relative target value of the interpolation.
	 * @param targetValue2 The 2nd relative target value of the interpolation.
	 * @return The current tween for chaining instructions.
	 */
	public Tween targetCurrentRelative(float targetValue1, float targetValue2) {
		target.getTweenValues(tweenType, targetValues);
		targetValues[0] += targetValue1;
		targetValues[1] += targetValue2;
		return this;
	}

	/**
	 * Sets the target values of the interpolation, relatively to the <b>the
	 * current values, the ones present when this call is made</b>.
	 * <br/><br/>
	 * To sum-up:<br/>
	 * - start values: values at start time, after delay<br/>
	 * - end values: params + values at current time
	 * @param targetValue1 The 1st relative target value of the interpolation.
	 * @param targetValue2 The 2nd relative target value of the interpolation.
	 * @param targetValue3 The 3rd relative target value of the interpolation.
	 * @return The current tween for chaining instructions.
	 */
	public Tween targetCurrentRelative(float targetValue1, float targetValue2, float targetValue3) {
		target.getTweenValues(tweenType, targetValues);
		targetValues[0] += targetValue1;
		targetValues[1] += targetValue2;
		targetValues[2] += targetValue3;
		return this;
	}

	/**
	 * Sets the target values of the interpolation, relatively to the <b>the
	 * current values, the ones present when this call is made</b>.
	 * <br/><br/>
	 * To sum-up:<br/>
	 * - start values: values at start time, after delay<br/>
	 * - end values: params + values at current time
	 * @param targetValues The relative target values of the interpolation.
	 * @return The current tween for chaining instructions.
	 */
	public Tween targetCurrentRelative(float... targetValues) {
		if (targetValues.length > MAX_COMBINED_TWEENS)
			throw new RuntimeException("You cannot set more than " + MAX_COMBINED_TWEENS + " targets.");
		target.getTweenValues(tweenType, targetValues);
		for (int i=0, n=targetValues.length; i<n; i++)
			this.targetValues[i] += targetValues[i];
		return this;
	}

	/**
	 * Kills the interpolation. If pooling was enabled when this tween was
	 * created, the tween will be freed, cleared, and returned to the pool. As
	 * a result, you shouldn't use it anymore.
	 */
	public void kill() {
		isFinished = true;
		callKillCallbacks();
	}

	/**
	 * Adds a delay to the tween.
	 * @param millis The delay, in milliseconds.
	 * @return The current tween for chaining instructions.
	 */
	@Override
	public Tween delay(int millis) {
		delayMillis += millis;
		return this;
	}

	/**
	 * Adds a callback to the tween.
	 * The callback is triggered when start() is called on the tween.
	 * @param callback A tween callback.
	 * @return The current tween for chaining instructions.
	 */
	public Tween addStartCallback(TweenCallback callback) {
		startCallbacks.add(callback);
		return this;
	}

	/**
	 * Adds a callback to the tween.
	 * The callback is triggered at the end of the delay.
	 * @param callback A tween callback.
	 * @return The current tween for chaining instructions.
	 */
	public Tween addEndOfDelayCallback(TweenCallback callback) {
		endOfDelayCallbacks.add(callback);
		return this;
	}

	/**
	 * Adds a callback to the tween.
	 * The callback is triggered on each iteration ending. If no repeat
	 * behavior was specified, this callback is similar to a Types.COMPLETE
	 * callback.
	 * @param callback A tween callback.
	 * @return The current tween for chaining instructions.
	 */
	public Tween addIterationCompleteCallback(TweenCallback callback) {
		iterationCompleteCallbacks.add(callback);
		return this;
	}

	/**
	 * Adds a callback to the tween.
	 * The callback is triggered at the end of the tween.
	 * @param callback A tween callback.
	 * @return The current tween for chaining instructions.
	 */
	public Tween addCompleteCallback(TweenCallback callback) {
		completeCallbacks.add(callback);
		return this;
	}

	/**
	 * Adds a callback to the tween.
	 * The callback is triggered if the tween is manually killed.
	 * @param callback A tween callback.
	 * @return The current tween for chaining instructions.
	 */
	public Tween addKillCallback(TweenCallback callback) {
		killCallbacks.add(callback);
		return this;
	}

	/**
	 * Adds a callback to the tween.
	 * The callback is triggered right before a tween is sent back to the pool.
	 * @param callback A tween callback.
	 * @return The current tween for chaining instructions.
	 */
	public Tween addPoolCallback(TweenCallback callback) {
		poolCallbacks.add(callback);
		return this;
	}

	/**
	 * Repeats the tween for a given number of times. 
	 * @param count The number of desired repetition. For infinite repetition,
	 * use Tween.INFINITY, or a negative number.
	 * @param millis A delay before each repetition.
	 * @return The current tween for chaining instructions.
	 */
	@Override
	public Tween repeat(int count, int delayMillis) {
		repeatCnt = count;
		repeatDelayMillis = delayMillis;
		return this;
	}

	/**
	 * Reverse the tween. Will interpolate from target values to the
	 * current values if not already reversed.
	 * @return The current tween for chaining instructions.
	 */
	public Tween reverse() {
		isReversed = !isReversed;
		return this;
	}

	/**
	 * Sets an object attached to this tween. It can be useful in order to
	 * retrieve some data from a TweenCallback.
	 * @param data Any kind of object.
	 * @return The current tween for chaining instructions.
	 */
	public Tween setUserData(Object data) {
		userData = data;
		return this;
	}

	/**
	 * Convenience method to add a single tween to a manager. Both usages are
	 * equivalent:<br/>
	 * <pre>
	 * myManager.add(Tween.to(...));
	 * Tween.to(...).addToManager(myManager);
	 * </pre>
	 * @param manager A TweenManager.
	 * @return The current tween for chaining instructions.
	 */
	public Tween addToManager(TweenManager manager) {
		manager.add(this);
		return this;
	}

	/**
	 * Gets the tween target.
	 * @return The tween target.
	 */
	public Tweenable getTarget() {
		return target;
	}

	/**
	 * Gets the tween type.
	 * @return The tween type.
	 */
	public int getTweenType() {
		return tweenType;
	}

	/**
	 * Gets the tween easing equation.
	 * @return The tween easing equation.
	 */
	public TweenEquation getEquation() {
		return equation;
	}

	/**
	 * Gets the tween target values.
	 * @return The tween target values.
	 */
	public float[] getTargetValues() {
		return targetValues;
	}

	/**
	 * Gets the tween duration.
	 * @return The tween duration.
	 */
	@Override
	public int getDuration() {
		return durationMillis;
	}

	/**
	 * Gets the tween delay.
	 * @return The tween delay.
	 */
	@Override
	public int getDelay() {
		return delayMillis;
	}

	/**
	 * Gets the number of combined tweens.
	 * @return The number of combined tweens.
	 */
	public int getCombinedTweenCount() {
		return combinedTweenCount;
	}

	/**
	 * Gets the total number of repetitions.
	 * @return The total number of repetitions.
	 */
	public int getRepeatCount() {
		return repeatCnt;
	}

	/**
	 * Gets the delay before each repetition.
	 * @return The delay before each repetition.
	 */
	public int getRepeatDelay() {
		return repeatDelayMillis;
	}

	/**
	 * Gets the number of remaining iterations.
	 * @return The number of remaining iterations.
	 */
	public int getRemainingIterationsCount() {
		return repeatCnt - iteration;
	}

	/**
	 * Returns true if the tween is finished (i.e. if the tween has reached
	 * its end or has been killed). If this is the case and tween pooling is
	 * enabled, the tween should no longer been used, since it will be reset
	 * and returned to the pool.
	 * @return True if the tween is finished.
	 */
	public boolean isFinished() {
		return isFinished;
	}

	/**
	 * Gets the attached user data, or null if none.
	 * @return The attached user data.
	 */
	public Object getUserData() {
		return userData;
	}

	// -------------------------------------------------------------------------
	// Update engine
	// -------------------------------------------------------------------------

	/**
	 * <b>Advanced use.</b><br/>
	 * Updates the tween state. Using this method can be unsafe if tween
	 * pooling was first enabled. <b>The recommanded behavior is to use a
	 * TweenManager instead.</b> Of course, you can give any delta time you
	 * want. Therefore, slow or fast motion can be easily achieved.
	 * @param deltaMillis A delta time, in milliseconds, between now and the
	 * last call.
	 */
	public final void update(int deltaMillis) {
		currentMillis += deltaMillis * speedFactor;

		// Is the tween valid ?
		if (checkForValidity()) return;

		// Wait for the end of the delay then either grab the start or end
		// values if it is the first iteration, or restart from those values
		// if the animation is replaying.
		if (checkForEndOfDelay()) return;

		// Test for the end of the tween. If true, set the target values to
		// their final values (to avoid precision loss when moving fast), and
		// call the callbacks.
		if (checkForEndOfTween()) return;

		// New values computation
		updateTarget();
	}

	private boolean checkForValidity() {
		if (isFinished && isPooled) {
			callPoolCallbacks();
			pool.free(this);
		}
		return isFinished || !isStarted || isEnded;
	}
	
	private boolean checkForEndOfDelay() {
		if (!isDelayEnded && currentMillis >= endDelayMillis) {
			isDelayEnded = true;

			if (iteration > 0 && target != null) {
				target.onTweenUpdated(tweenType, startValues);
			} else if (target != null) {
				target.getTweenValues(tweenType, startValues);
				for (int i=0; i<combinedTweenCount; i++) {
					targetValues[i] += isRelative ? startValues[i] : 0;
					targetMinusStartValues[i] = targetValues[i] - startValues[i];
				}
			}

			callDelayEndedCallbacks();

		} else if (!isDelayEnded) {
			return true;
		}
		return false;
	}

	private boolean checkForEndOfTween() {
		if (!isEnded && currentMillis >= endMillis) {
			isEnded = true;

			if (target != null) {
				for (int i=0; i<combinedTweenCount; i++) {
					localTmp[i] = isReversed
						? targetValues[i] - targetMinusStartValues[i]
						: startValues[i] + targetMinusStartValues[i];
				}
				target.onTweenUpdated(tweenType, localTmp);
			}

			if (shouldRepeat()) {
				callIterationCompleteCallbacks();
				iteration += 1;
				start();
			} else {
				isFinished = true;
				callIterationCompleteCallbacks();
				callCompleteCallbacks();
			}

			return true;
		}
		return false;
	}

	private void updateTarget() {
		if (target != null) {
			for (int i=0; i<combinedTweenCount; i++) {
				localTmp[i] = equation.compute(
					currentMillis - endDelayMillis,
					isReversed ? targetValues[i] : startValues[i],
					isReversed ? -targetMinusStartValues[i] : +targetMinusStartValues[i],
					durationMillis);
			}
			target.onTweenUpdated(tweenType, localTmp);
		}
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private boolean shouldRepeat() {
		return (repeatCnt < 0) || (iteration < repeatCnt);
	}

	private void callStartCallbacks() {
		for (int i=startCallbacks.size()-1; i>=0; i--)
			startCallbacks.get(i).tweenEventOccured(TweenCallback.Types.START, this);
	}

	private void callDelayEndedCallbacks() {
		for (int i=endOfDelayCallbacks.size()-1; i>=0; i--)
			endOfDelayCallbacks.get(i).tweenEventOccured(TweenCallback.Types.END_OF_DELAY, this);
	}

	private void callIterationCompleteCallbacks() {
		for (int i=iterationCompleteCallbacks.size()-1; i>=0; i--)
			iterationCompleteCallbacks.get(i).tweenEventOccured(TweenCallback.Types.ITERATION_COMPLETE, this);
	}

	private void callCompleteCallbacks() {
		for (int i=completeCallbacks.size()-1; i>=0; i--)
			completeCallbacks.get(i).tweenEventOccured(TweenCallback.Types.COMPLETE, this);
	}

	private void callKillCallbacks() {
		for (int i=killCallbacks.size()-1; i>=0; i--)
			killCallbacks.get(i).tweenEventOccured(TweenCallback.Types.KILL, this);
	}

	private void callPoolCallbacks() {
		for (int i=poolCallbacks.size()-1; i>=0; i--)
			poolCallbacks.get(i).tweenEventOccured(TweenCallback.Types.POOL, this);
	}
}
