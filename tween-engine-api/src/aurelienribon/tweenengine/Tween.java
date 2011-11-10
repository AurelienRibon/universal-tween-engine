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

	/**
	 * Convenience method to create an empty tween. Such object is only usefull
	 * when placed inside animation sequences (see TweenGroup), in which it
	 * may act as a beacon, so you can set callbacks on it in order to trigger
	 * then at the moment you need.
	 *
	 * <br/><br/>
	 * The following lines are equivalent (if pooling has been disabled):
	 *
	 * <br/><br/>
	 * <pre>
	 * Tween.mark();
	 * new Tween(null, -1, 0, null);
	 * </pre>
	 *
	 * Several options such as delays and callbacks can be added to the tween.
	 * This method hides some of the internal optimizations such as object
	 * reuse for convenience. However, you can control the creation of the
	 * tween by using the classic constructor.
	 * @see TweenGroup
	 */
	public static Tween mark() {
		Tween tween = pool.get();
		tween.build(null, -1, 0, null);
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
	private int endMillis;
	private int completeMillis;
	private int currentMillis;
	private int lastCurrentMillis;
	private boolean isStarted; // true when the tween is started
	private boolean isInitialized; // true when starting values have been retrieved (after first delay)
	private boolean isFinished; // true when all repetitions are done or the tween has been killed

	// Callbacks
	private final List<CallbackTuple> callbacks = new ArrayList<CallbackTuple>();

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
		isInitialized = false;
		isFinished = false;

		callbacks.clear();

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
		endMillis = delayMillis + durationMillis;
		completeMillis = endMillis + repeatDelayMillis;
		isStarted = true;
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
	 * The callback is triggered on each iteration ending.
	 * @param callback A tween callback.
	 * @return The current tween for chaining instructions.
	 */
	public Tween addIterationCompleteCallback(TweenCallback callback) {
		callbacks.add(new CallbackTuple(callback, TweenCallback.Types.ITERATION_COMPLETE));
		return this;
	}

	/**
	 * Adds a callback to the tween.
	 * The callback is triggered at the end of the tween.
	 * @param callback A tween callback.
	 * @return The current tween for chaining instructions.
	 */
	public Tween addCompleteCallback(TweenCallback callback) {
		callbacks.add(new CallbackTuple(callback, TweenCallback.Types.COMPLETE));
		return this;
	}

	/**
	 * Adds a callback to the tween.
	 * The callback is triggered on each iteration ending, if the animation is
	 * running backwards (ie. with a negative speed factor).
	 * @param callback A tween callback.
	 * @return The current tween for chaining instructions.
	 */
	public Tween addBackwardsIterationCompleteCallback(TweenCallback callback) {
		callbacks.add(new CallbackTuple(callback, TweenCallback.Types.BACK_ITERATION_COMPLETE));
		return this;
	}

	/**
	 * Adds a callback to the tween.
	 * The callback is triggered at the end of the tween, if the animation is
	 * running backwards (ie. with a negative speed factor).
	 * @param callback A tween callback.
	 * @return The current tween for chaining instructions.
	 */
	public Tween addBackwardsCompleteCallback(TweenCallback callback) {
		callbacks.add(new CallbackTuple(callback, TweenCallback.Types.BACK_COMPLETE));
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
		repeatDelayMillis = delayMillis >= 0 ? delayMillis : 0;
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
	 * Convenience method to add a single tween to a manager. The tween is 
	 * automatically started. Both following usages are equivalent:<br/>
	 * <pre>
	 * myManager.add(Tween.to(...));
	 * Tween.to(...).addToManager(myManager);
	 * </pre>
	 * @param manager A TweenManager.
	 * @return The current tween for chaining instructions.
	 */
	public Tween addToManager(TweenManager manager) {
		manager.tweens.add(this);
		start();
		return this;
	}

	/**
	 * Gets the tween target.
	 */
	public Tweenable getTarget() {
		return target;
	}

	/**
	 * Gets the tween type.
	 */
	public int getTweenType() {
		return tweenType;
	}

	/**
	 * Gets the tween easing equation.
	 */
	public TweenEquation getEquation() {
		return equation;
	}

	/**
	 * Gets the tween target values.
	 */
	public float[] getTargetValues() {
		return targetValues;
	}

	/**
	 * Gets the tween duration.
	 */
	@Override
	public int getDuration() {
		return durationMillis;
	}

	/**
	 * Gets the tween delay.
	 */
	@Override
	public int getDelay() {
		return delayMillis;
	}

	/**
	 * Gets the number of combined tweens.
	 */
	public int getCombinedTweenCount() {
		return combinedTweenCount;
	}

	/**
	 * Gets the total number of repetitions.
	 */
	@Override
	public int getRepeatCount() {
		return repeatCnt;
	}

	/**
	 * Gets the delay before each repetition.
	 */
	@Override
	public int getRepeatDelay() {
		return repeatDelayMillis;
	}

	/**
	 * Gets the number of remaining iterations.
	 */
	public int getRemainingIterationsCount() {
		return repeatCnt - iteration;
	}

	/**
	 * Returns true if the tween is finished (i.e. if the tween has reached
	 * its end or has been killed). If this is the case and tween pooling is
	 * enabled, the tween should no longer been used, since it will be reset
	 * and returned to the pool.
	 */
	public boolean isFinished() {
		return isFinished;
	}

	/**
	 * Gets the attached user data, or null if none.
	 */
	public Object getUserData() {
		return userData;
	}

	/**
	 * Changes the speed of the animation. '1' is the default. '2' would make
	 * the animation run at twice its speed, and so on. Negative values are
	 * possible, they will make the animation go backwards.
	 * @param speedFactor A speed coefficient.
	 */
	public void setSpeed(float speedFactor) {
		this.speedFactor = speedFactor;
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
		if (checkValidity()) return;
		lastCurrentMillis = currentMillis;
		currentMillis += deltaMillis * speedFactor;
		currentMillis = Math.max(currentMillis, -1);

		// Wait for the end of the delay then either grab the start or end
		// values if it is the first iteration, or restart from those values
		// if the animation is replaying.
		if (checkEndOfDelay()) return;

		// Test for the end of the iteration. If true, set the target values to
		// their final values (to avoid precision loss when moving fast), and
		// call the callbacks.
		if (checkIteration()) return;

		updateTarget();
	}

	private boolean checkValidity() {
		if (isFinished && isPooled) pool.free(this);
		return isFinished || !isStarted;
	}
	
	private boolean checkEndOfDelay() {
		if (target != null) {
			if (justTriggeredForwards(delayMillis) || justTriggeredBackwards(delayMillis)) {
				if (!isInitialized) {
					isInitialized = true;
					target.getTweenValues(tweenType, startValues);
					for (int i=0; i<combinedTweenCount; i++) {
						targetValues[i] += isRelative ? startValues[i] : 0;
						targetMinusStartValues[i] = targetValues[i] - startValues[i];
					}
				} else {
					target.onTweenUpdated(tweenType, startValues);
				}
			}
		}
		return 0 <= currentMillis && currentMillis <= delayMillis;
	}

	private boolean checkIteration() {
		boolean fwdEnd = justTriggeredForwards(endMillis);
		boolean fwdComplete = justTriggeredForwards(completeMillis);

		// -----Forwards

		if (fwdEnd) {
			if (target != null) {
				for (int i=0; i<combinedTweenCount; i++) {
					localTmp[i] = isReversed
						? targetValues[i] - targetMinusStartValues[i]
						: startValues[i] + targetMinusStartValues[i];
				}
				target.onTweenUpdated(tweenType, localTmp);
			}

			callCallbacks(TweenCallback.Types.ITERATION_COMPLETE);
			if (repeatCnt >= 0 && iteration == repeatCnt)
				callCallbacks(TweenCallback.Types.COMPLETE);
		}

		if (fwdComplete) {
			if (iteration < repeatCnt || repeatCnt < 0) {
				iteration += 1;
				start();
			} else {
				isFinished = true;
			}
		}

		if (fwdEnd || fwdComplete) {
			return true;
		}

		// -----Backwards

		boolean bckStart = justTriggeredBackwards(0);

		if (bckStart) {
			callCallbacks(TweenCallback.Types.BACK_ITERATION_COMPLETE);
			if (iteration > 0) {
				iteration -= 1;
				start();
				currentMillis = completeMillis;
			} else {
				callCallbacks(TweenCallback.Types.BACK_COMPLETE);
			}
			return true;
		}

		return currentMillis < 0 || currentMillis > endMillis;
	}

	private void updateTarget() {
		if (target != null && equation != null) {
			for (int i=0; i<combinedTweenCount; i++) {
				localTmp[i] = equation.compute(
					currentMillis - delayMillis,
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

	private boolean justTriggeredForwards(int time) {
		return lastCurrentMillis <= time && currentMillis > time;
	}

	private boolean justTriggeredBackwards(int time) {
		return lastCurrentMillis >= time && currentMillis < time;
	}

	private void callCallbacks(TweenCallback.Types type) {
		for (int i=callbacks.size()-1; i>=0; i--)
			if (callbacks.get(i).type == type)
				callbacks.get(i).callback.tweenEventOccured(type, this);
	}

	private class CallbackTuple {
		public final TweenCallback callback;
		public final TweenCallback.Types type;
		public CallbackTuple(TweenCallback callback, TweenCallback.Types type) {
			this.callback = callback;
			this.type = type;
		}
	}
}
