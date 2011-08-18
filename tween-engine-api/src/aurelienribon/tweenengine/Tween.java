package aurelienribon.tweenengine;

import aurelienribon.tweenengine.utils.Pool;
import java.util.ArrayList;

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
 *      .target(200, 300).delay(1000).repeat(2).start();
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
public class Tween {

	// -------------------------------------------------------------------------
	// Static
	// -------------------------------------------------------------------------

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
	 * Used for debug purpose. Gets the current number of objects that are
	 * waiting in the pool.
	 * @return The current size of the pool.
	 */
	public static int getPoolSize() {
		return pool.size();
	}

	/**
	 * Clears every static resources and resets the static instance.
	 */
	public static void dispose() {
		isPoolEnabled = false;
		pool.clear();
	}

	// -------------------------------------------------------------------------

	private static boolean isPoolEnabled = false;
	private static final Pool<Tween> pool;

	static {
		pool = new Pool<Tween>(20) {
			@Override protected Tween getNew() {
				return new Tween(null, -1, 0, null);
			}
		};
	}

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
		tween.reset();
		tween.__build(target, tweenType, durationMillis, equation);
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
		tween.reset();
		tween.__build(target, 0, durationMillis, equation);
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
		tween.reset();
		tween.__build(target, tweenType, durationMillis, equation);
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
		tween.reset();
		tween.__build(target, 0, durationMillis, equation);
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
		tween.reset();
		tween.__build(target, tweenType, 0, null);
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
		tween.reset();
		tween.__build(target, 0, 0, null);
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
		tween.reset();
		tween.__build(null, -1, 0, null);
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

	// Values
	private int combinedTweenCount;
	private final float[] startValues;
	private final float[] targetValues;
	private final float[] targetMinusStartValues;

	// Timings
	private long startMillis;
	private int durationMillis;
	private int delayMillis;
	private long endDelayMillis;
	private long endMillis;
	private boolean isInitialized;
	private boolean isStarted;
	private boolean isDelayEnded;
	private boolean isEnded;
	private boolean isFinished;

	// Callbacks
	private final ArrayList<TweenCallback> startCallbacks;
	private final ArrayList<TweenCallback> endOfDelayCallbacks;
	private final ArrayList<TweenCallback> iterationCompleteCallbacks;
	private final ArrayList<TweenCallback> completeCallbacks;
	private final ArrayList<TweenCallback> killCallbacks;
	private final ArrayList<TweenCallback> poolCallbacks;

	// Repeat
	private int repeatCnt;
	private int iteration;
	private int repeatDelayMillis;
	private long endRepeatDelayMillis;

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
		startValues = new float[MAX_COMBINED_TWEENS];
		targetValues = new float[MAX_COMBINED_TWEENS];
		targetMinusStartValues = new float[MAX_COMBINED_TWEENS];

		startCallbacks = new ArrayList<TweenCallback>(3);
		endOfDelayCallbacks = new ArrayList<TweenCallback>(3);
		iterationCompleteCallbacks = new ArrayList<TweenCallback>(3);
		completeCallbacks = new ArrayList<TweenCallback>(3);
		killCallbacks = new ArrayList<TweenCallback>(3);
		poolCallbacks = new ArrayList<TweenCallback>(3);

		reset();
		__build(target, tweenType, durationMillis, equation);
	}

	/**
	 * Starts or restart the interpolation.
	 * @return The current tween for chaining instructions.
	 */
	public Tween start() {
		startMillis = System.currentTimeMillis();
		endDelayMillis = startMillis + delayMillis;

		if (iteration > 0 && repeatDelayMillis < 0)
			endDelayMillis = Math.max(endDelayMillis + repeatDelayMillis, startMillis);

		endMillis = endDelayMillis + durationMillis;
		endRepeatDelayMillis = Math.max(endMillis, endMillis + repeatDelayMillis);

		isInitialized = true;
		isStarted = true;
		isDelayEnded = false;
		isEnded = false;
		isFinished = false;

		callStartCallbacks();

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
	public Tween delay(int millis) {
		this.delayMillis += millis;
		return this;
	}

	/**
	 * Sets the target value of the interpolation. If not reversed, the
	 * interpolation will run from the current value to this target value.
	 * @param targetValue The target value of the interpolation.
	 * @return The current tween for chaining instructions.
	 */
	public Tween target(float targetValue) {
		targetValues[0] = targetValue;
		return this;
	}

	/**
	 * Sets the target values of the interpolation. If not reversed, the
	 * interpolation will run from the current values to these target values.
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
	 * Sets the target values of the interpolation. If not reversed, the
	 * interpolation will run from the current values to these target values.
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
	 * Sets the target values of the interpolation. If not reversed, the
	 * interpolation will run from the current values to these target values.
	 * <br/><br/>
	 * The other methods are convenience to avoid the allocation of an array.
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
	 * Sets the target value of the interpolation, relatively to the current
	 * value. If not reversed, the interpolation will run from the current value
	 * to this target value.
	 * @param targetValue The relative target value of the interpolation.
	 * @return The current tween for chaining instructions.
	 */
	public Tween targetRelative(float targetValue) {
		isRelative = true;
		return target(targetValue);
	}

	/**
	 * Sets the target values of the interpolation, relatively to the current
	 * values. If not reversed, the interpolation will run from the current
	 * values to these target values.
	 * @param targetValue1 The 1st relative target value of the interpolation.
	 * @param targetValue2 The 2nd relative target value of the interpolation.
	 * @return The current tween for chaining instructions.
	 */
	public Tween targetRelative(float targetValue1, float targetValue2) {
		isRelative = true;
		return target(targetValue1, targetValue2);
	}

	/**
	 * Sets the target values of the interpolation, relatively to the current
	 * values. If not reversed, the interpolation will run from the current
	 * values to these target values.
	 * @param targetValue1 The 1st relative target value of the interpolation.
	 * @param targetValue2 The 2nd relative target value of the interpolation.
	 * @param targetValue3 The 3rd relative target value of the interpolation.
	 * @return The current tween for chaining instructions.
	 */
	public Tween targetRelative(float targetValue1, float targetValue2, float targetValue3) {
		isRelative = true;
		return target(targetValue1, targetValue2, targetValue3);
	}

	/**
	 * Sets the target values of the interpolation, relatively to the current
	 * values. If not reversed, the interpolation will run from the current
	 * values to these target values.
	 * <br/><br/>
	 * The other methods are convenience to avoid the allocation of an array.
	 * @param targetValues The relative target values of the interpolation.
	 * @return The current tween for chaining instructions.
	 */
	public Tween targetRelative(float... targetValues) {
		isRelative = true;
		return target(targetValues);
	}

	/**
	 * Sets the target value(s) of the interpolation as the current value(s) of
	 * the Tweenable object. If not reversed, the interpolation will run from
	 * the current value(s) to the(se) target value(s).
	 * @return
	 */
	public Tween targetCurrent() {
		target.getTweenValues(tweenType, targetValues);
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
	 * Convenience method to add a single tween to a manager and avoid the
	 * verbose <i>myManager.add(Tween.to(....).delay(...).start());</i>.
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
	public int getDuration() {
		return durationMillis;
	}

	/**
	 * Gets the tween delay.
	 * @return The tween delay.
	 */
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
	 * Getsthe total number of repetitions.
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
	public int getRemainingIterationCount() {
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

	/**
	 * Updates the tween state. Using this method can be unsafe if tween
	 * pooling was first enabled. <b>The recommanded behavior is to use a
	 * TweenManager instead.</b>
	 * @param currentMillis The current milliseconds. You would generally
	 * want to use <i>System.currentMillis()</i> and pass the result to
	 * every unsafeUpdate call.
	 */
	public final void unsafeUpdate(long currentMillis) {
		update(currentMillis);
	}

	// -------------------------------------------------------------------------
	// Update engine
	// -------------------------------------------------------------------------

	final void update(long currentMillis) {
		// Is the tween valid ?
		checkForValidity();

		// Are we started ?
		if (!isStarted)
			return;

		// Shall we repeat ?
		if (checkForRepetition(currentMillis))
			return;

		// Is the tween ended ?
		if (isEnded)
			return;

		// Wait for the end of the delay then either grab the start or end
		// values if it is the first iteration, or restart from those values
		// if the animation is replaying.
		if (checkForEndOfDelay(currentMillis))
			return;

		// Test for the end of the tween. If true, set the target values to
		// their final values (to avoid precision loss when moving fast), and
		// call the callbacks.
		if (checkForEndOfTween(currentMillis))
			return;

		// New values computation
		updateTarget(currentMillis);
	}

	private boolean checkForValidity() {
		if (isFinished && isPooled && isInitialized) {
			callPoolCallbacks();
			reset();
			pool.free(this);
			return true;
		} else if (isFinished) {
			return true;
		}
		return false;
	}

	private boolean checkForRepetition(long currentMillis) {
		if (shouldRepeat() && currentMillis >= endRepeatDelayMillis) {
			iteration += 1;
			start();
			return true;
		}
		return false;
	}
	
	private boolean checkForEndOfDelay(long currentMillis) {
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

	private boolean checkForEndOfTween(long currentMillis) {
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
			} else {
				isFinished = true;
				callIterationCompleteCallbacks();
				callCompleteCallbacks();
			}

			return true;
		}
		return false;
	}

	private void updateTarget(long currentMillis) {
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
	// Expert features
	// -------------------------------------------------------------------------

	/**
	 * <b>Advanced use.</b>
	 * <br/>Rebuilds a tween from the current one. May be used if you want to
	 * build your own pool system. You should call __reset() before.
	 */
	public final void __build(Tweenable target, int tweenType, int durationMillis, TweenEquation equation) {
		reset();

		this.isInitialized = true;

		this.target = target;
		this.tweenType = tweenType;
		this.durationMillis = durationMillis;
		this.equation = equation;

		if (target != null) {
			this.combinedTweenCount = target.getTweenValues(tweenType, localTmp);
			if (this.combinedTweenCount < 1 || this.combinedTweenCount > MAX_COMBINED_TWEENS)
				throw new RuntimeException("Min combined tweens = 1, max = " + MAX_COMBINED_TWEENS);
		}
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private void reset() {
		this.target = null;
		this.tweenType = -1;
		this.equation = null;

		this.isReversed = false;
		this.isInitialized = false;
		this.isPooled = isPoolEnabled;
		this.isRelative = false;

		this.combinedTweenCount = 0;

		this.delayMillis = 0;
		this.isStarted = false;
		this.isDelayEnded = false;
		this.isEnded = false;
		this.isFinished = true;

		this.completeCallbacks.clear();
		this.iterationCompleteCallbacks.clear();
		this.killCallbacks.clear();
		this.poolCallbacks.clear();
		this.startCallbacks.clear();
		this.endOfDelayCallbacks.clear();

		this.repeatCnt = 0;
		this.iteration = 0;
		this.repeatDelayMillis = 0;

		this.userData = null;
	}

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
