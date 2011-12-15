package aurelienribon.tweenengine;

import aurelienribon.tweenengine.TweenCallback.Types;
import aurelienribon.tweenengine.equations.Linear;
import java.util.HashMap;
import java.util.Map;

/**
 * Core class of the Tween Engine. It contains many static factory methods to
 * create and instantiate new interpolations easily.
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
 * Tween.to(myObject, POSITION_XY, 500)
 *      .target(200, 300)
 *      .ease(Quad.INOUT)
 *      .delay(1000)
 *      .repeat(2, 0)
 *      .start(myManager);
 * </pre>
 *
 * You need to periodicaly update the tween engine, in order to compute the new
 * values. Add it to a TweenManager, it will take care of the tween life-cycle
 * for you!
 *
 * @see TweenAccessor
 * @see TweenManager
 * @see TweenEquation
 * @see Timeline
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public final class Tween extends BaseTween {
	// -------------------------------------------------------------------------
	// Static -- misc
	// -------------------------------------------------------------------------

	/**
	 * Used as parameter in <i>repeat()</i> and <i>repeatYoyo()</i> methods.
	 */
	public static final int INFINITY = -1;

	/**
	 * The maximum number of attributes that can be animated in a single tween.
	 */
	public static final int MAX_COMBINED_TWEENS = 10;

	/**
	 * Gets the version number of the library.
	 */
	public static String getVersion() {
		return "6.0.0";
	}

	// -------------------------------------------------------------------------
	// Static -- pool
	// -------------------------------------------------------------------------

	private static boolean isPoolEnabled = false;

	private static final Pool.Callback<Tween> poolCallback = new Pool.Callback<Tween>() {
		@Override public void onPool(Tween obj) {obj.reset();}
		@Override public void onUnpool(Tween obj) {obj.isPooled = Tween.isPoolingEnabled();}
	};

	private static final Pool<Tween> pool = new Pool<Tween>(20, poolCallback) {
		@Override protected Tween create() {return new Tween();}
	};

	/**
	 * Enables or disables the automatic reuse of finished tweens. Pooling
	 * prevents the allocation of a new tween object when using the static
	 * constructors, thus removing the need for garbage collection. Can be quite
	 * helpful on slow or embedded devices. <b>Defaults to true</b>.
	 */
	public static void enablePooling(boolean value) {
		isPoolEnabled = value;
	}

	/**
	 * Returns true if object pooling is enabled.
	 */
	public static boolean isPoolingEnabled() {
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
		Timeline.pool.clear();
	}

	// -------------------------------------------------------------------------
	// Static -- tween accessors
	// -------------------------------------------------------------------------

	private static final Map<Class, TweenAccessor> registeredAccessors = new HashMap<Class, TweenAccessor>();
	private static final float[] buffer = new float[MAX_COMBINED_TWEENS];

	/**
	 * Registers an engine with the class of an object. This engine will be used
	 * with interpolations applied to every objects of the registered class.
	 * @param someClass An object class.
	 * @param defaultAccessor The accessor that will be used to tween any object
	 * of class "someClass".
	 */
	public static void registerAccessor(Class someClass, TweenAccessor defaultAccessor) {
		registeredAccessors.put(someClass, defaultAccessor);
	}

	/**
	 * Gets the registered TweenAccessor associated with the given object class.
	 * @param someClass An object class.
	 */
	public static TweenAccessor getRegisteredAccessor(Class someClass) {
		return registeredAccessors.get(someClass);
	}

	// -------------------------------------------------------------------------
	// Static -- factories
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
	 * The common use of Tweens is "fire-and-forget": you do not need to care
	 * for tweens once you added them to a TweenManager, they will be updated
	 * automatically, and cleaned once finished. Common call:
	 * <br/>
	 * <pre>
	 * Tween.to(myObject, POSITION, 1000)
	 *      .target(50, 70)
	 *      .ease(Quad.INOUT)
	 *      .addToManager(myManager);
	 * </pre>
	 * 
	 * Several options such as delays and callbacks can be added to the tween.
	 * This method hides some of the internal optimizations such as object
	 * reuse for convenience.
	 *
	 * @param target The target of the interpolation.
	 * @param tweenType The desired type of interpolation.
	 * @param durationMillis The duration of the interpolation, in milliseconds.
	 * @return The generated Tween.
	 */
	public static Tween to(Object target, int tweenType, int durationMillis) {
		Tween tween = pool.get();
		tween.setup(target, tweenType, durationMillis);
		tween.ease(Linear.INOUT);
		return tween;
	}

	/**
	 * Convenience method to create a new reversed interpolation.
	 *
	 * <br/><br/>
	 * You need to set the target values of the interpolation by using one
	 * of the ".target()" methods. The interpolation will run from these
	 * values (retrieved after the delay, if any) to the current values.
	 *
	 * <br/><br/>
	 * The common use of Tweens is "fire-and-forget": you do not need to care
	 * for tweens once you added them to a TweenManager, they will be updated
	 * automatically, and cleaned once finished. Common call:
	 * <br/>
	 * <pre>
	 * Tween.from(myObject, POSITION, 1000)
	 *      .target(50, 70)
	 *      .ease(Quad.INOUT)
	 *      .addToManager(myManager);
	 * </pre>
	 *
	 * Several options such as delays and callbacks can be added to the tween.
	 * This method hides some of the internal optimizations such as object
	 * reuse for convenience.
	 *
	 * @param target The target of the interpolation.
	 * @param tweenType The desired type of interpolation.
	 * @param durationMillis The duration of the interpolation, in milliseconds.
	 * @return The generated Tween.
	 */
	public static Tween from(Object target, int tweenType, int durationMillis) {
		Tween tween = pool.get();
		tween.setup(target, tweenType, durationMillis);
		tween.ease(Linear.INOUT);
		tween.isFrom = true;
		return tween;
	}

	/**
	 * Convenience method to create a new instantaneous interpolation (as a
	 * result, this is not really an interpolation).
	 *
	 * <br/><br/>
	 * You need to set the target values of the interpolation by using one
	 * of the ".target()" methods. The interpolation will directly set the target
	 * to these values.
	 *
	 * <br/><br/>
	 * The common use of Tweens is "fire-and-forget": you do not need to care
	 * for tweens once you added them to a TweenManager, they will be updated
	 * automatically, and cleaned once finished. Common call:
	 * <br/>
	 * <pre>
	 * Tween.set(myObject, POSITION)
	 *      .target(50, 70)
	 *      .addToManager(myManager);
	 * </pre>
	 *
	 * Several options such as delays and callbacks can be added to the tween.
	 * This method hides some of the internal optimizations such as object
	 * reuse for convenience.
	 *
	 * @param target The target of the interpolation.
	 * @param tweenType The desired type of interpolation.
	 * @param durationMillis The duration of the interpolation, in milliseconds.
	 * @return The generated Tween.
	 */
	public static Tween set(Object target, int tweenType) {
		Tween tween = pool.get();
		tween.setup(target, tweenType, 0);
		return tween;
	}

	/**
	 * Convenience method to create a new simple timer.
	 *
	 * <br/><br/>
	 * The common use of Tweens is "fire-and-forget": you do not need to care
	 * for tweens once you added them to a TweenManager, they will be updated
	 * automatically, and cleaned once finished. Common call:
	 * <br/>
	 * <pre>
	 * Tween.call(myCallback)
	 *      .addToManager(myManager);
	 * </pre>
	 *
	 * Several options such as delays and callbacks can be added to the tween.
	 * This method hides some of the internal optimizations such as object
	 * reuse for convenience.
	 *
	 * @param callback The callback that will be triggered at the end of the
	 * delay (if specified). A repeat behavior can be set to the tween to
	 * trigger it more than once.
	 * @return The generated Tween.
	 * @see TweenCallback
	 */
	public static Tween call(TweenCallback callback) {
		Tween tween = pool.get();
		tween.setup(null, -1, 0);
		tween.addCallback(Types.START, callback);
		return tween;
	}

	/**
	 * Convenience method to create an empty tween. Such object is only useful
	 * when placed inside animation sequences (see TweenGroup), in which it
	 * may act as a beacon, so you can set callbacks on it in order to trigger
	 * then at the moment you need.
	 * @see TweenGroup
	 */
	public static Tween mark() {
		Tween tween = pool.get();
		tween.setup(null, -1, 0);
		return tween;
	}

	// -------------------------------------------------------------------------
	// Attributes
	// -------------------------------------------------------------------------

	// Main
	private Object target;
	private TweenAccessor accessor;
	private int type;
	private TweenEquation equation;

	// General
	private boolean isFrom;
	private boolean isRelative;
	private int combinedTweenCnt;

	// Values
	private final float[] startValues = new float[MAX_COMBINED_TWEENS];
	private final float[] targetValues = new float[MAX_COMBINED_TWEENS];

	// -------------------------------------------------------------------------
	// Setup
	// -------------------------------------------------------------------------

	private Tween() {
		reset();
	}

	@Override
	protected void reset() {
		super.reset();

		target = null;
		accessor = null;
		type = -1;
		equation = null;

		isFrom = isRelative = false;
		combinedTweenCnt = 0;
	}

	private void setup(Object target, int tweenType, int durationMillis) {
		this.target = target;
		this.type = tweenType;
		this.durationMillis = durationMillis;

		if (target != null) {
			if (!registeredAccessors.containsKey(target.getClass()) && !(target instanceof TweenAccessor)) {
				TweenAccessor parentAccessor = getParentAccessor(target.getClass());
				if (parentAccessor != null) {
					registerAccessor(target.getClass(), parentAccessor);
				} else {
					throw new RuntimeException("No TweenAccessor was found for the target class");
				}
			}

			accessor = registeredAccessors.get(target.getClass());
			if (accessor == null) accessor = (TweenAccessor) target;

			combinedTweenCnt = accessor.getValues(target, tweenType, buffer);
			if (combinedTweenCnt < 1 || combinedTweenCnt > MAX_COMBINED_TWEENS)
				throw new RuntimeException("Min combined tweens = 1, max = " + MAX_COMBINED_TWEENS);
		}
	}

	private TweenAccessor getParentAccessor(Class clazz) {
		Class parentClass = clazz.getSuperclass();
		while (parentClass != null && !registeredAccessors.containsKey(parentClass))
			parentClass = parentClass.getSuperclass();
		return parentClass != null ? registeredAccessors.get(parentClass) : null;
	}

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	/**
	 * Starts or restarts the interpolation. You will need to update the tween
	 * yourself. <b>Recommanded behavior is to use <i>start(TweenManager)</i>
	 * </b>.
	 * @return The current tween for chaining instructions.
	 */
	public Tween start() {
		currentMillis = 0;
		isStarted = true;
		return this;
	}

	/**
	 * Starts or restarts the interpolation using a manager.
	 * @param manager A TweenManager.
	 * @return The current tween for chaining instructions.
	 */
	public Tween start(TweenManager manager) {
		manager.add(this);
		return this;
	}

	/**
	 * Sets the easing equation of the tween. Existing equations are located in
	 * aurelienribon.tweenengine.equations, but you can of course implement
	 * your own, see TweenEquation.
	 * @return The current tween for chaining instructions.
	 * @see TweenEquation
	 */
	public Tween ease(TweenEquation easeEquation) {
		this.equation = easeEquation;
		return this;
	}

	/**
	 * Adds a delay to the tween.
	 * @param millis The delay, in milliseconds.
	 * @return The current tween for chaining instructions.
	 */
	public Tween delay(int millis) {
		if (isStarted) throw new RuntimeException("You can't delay a tween once it is started");
		delayMillis += millis;
		return this;
	}

	@Override
	public Tween repeat(int count, int delayMillis) {
		super.repeat(count, delayMillis);
		return this;
	}

	@Override
	public Tween repeatYoyo(int count, int delayMillis) {
		super.repeatYoyo(count, delayMillis);
		return this;
	}
	
	@Override
	public Tween addCallback(Types callbackType, TweenCallback callback) {
		super.addCallback(callbackType, callback);
		return this;
	}
	
	@Override
	public Tween setUserData(Object data) {
		super.setUserData(data);
		return this;
	}

	@Override
	public void free() {
		if (isPooled) pool.free(this);
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
		if (isStarted) throw new RuntimeException("You can't change the target of a tween once it is started");
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
		if (isStarted) throw new RuntimeException("You can't change the target of a tween once it is started");
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
		if (isStarted) throw new RuntimeException("You can't change the target of a tween once it is started");
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
		if (isStarted) throw new RuntimeException("You can't change the target of a tween once it is started");
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
		if (isStarted) throw new RuntimeException("You can't change the target of a tween once it is started");
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
		if (isStarted) throw new RuntimeException("You can't change the target of a tween once it is started");
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
		if (isStarted) throw new RuntimeException("You can't change the target of a tween once it is started");
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
		if (isStarted) throw new RuntimeException("You can't change the target of a tween once it is started");
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
		if (isStarted) throw new RuntimeException("You can't change the target of a tween once it is started");
		accessor.getValues(target, type, targetValues);
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
		if (isStarted) throw new RuntimeException("You can't change the target of a tween once it is started");
		accessor.getValues(target, type, targetValues);
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
		if (isStarted) throw new RuntimeException("You can't change the target of a tween once it is started");
		accessor.getValues(target, type, targetValues);
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
		if (isStarted) throw new RuntimeException("You can't change the target of a tween once it is started");
		accessor.getValues(target, type, targetValues);
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
		if (isStarted) throw new RuntimeException("You can't change the target of a tween once it is started");
		if (targetValues.length > MAX_COMBINED_TWEENS)
			throw new RuntimeException("You cannot set more than " + MAX_COMBINED_TWEENS + " targets.");
		accessor.getValues(target, type, targetValues);
		for (int i=0, n=targetValues.length; i<n; i++)
			this.targetValues[i] += targetValues[i];
		return this;
	}

	// -------------------------------------------------------------------------
	// Getters
	// -------------------------------------------------------------------------
	
	/**
	 * Gets the tween target.
	 */
	public Object getTarget() {
		return target;
	}

	/**
	 * Gets the tween type.
	 */
	public int getType() {
		return type;
	}

	/**
	 * Gets the tween easing equation.
	 */
	public TweenEquation getEasing() {
		return equation;
	}

	/**
	 * Gets the tween target values.
	 */
	public float[] getTargetValues() {
		return targetValues;
	}

	/**
	 * Gets the number of combined tweens.
	 */
	public int getCombinedTweenCount() {
		return combinedTweenCnt;
	}

	// -------------------------------------------------------------------------
	// Update engine
	// -------------------------------------------------------------------------

	@Override
	protected void initializeOverride() {
		if (target != null) {
			accessor.getValues(target, type, startValues);
			for (int i=0; i<combinedTweenCnt; i++)
				targetValues[i] += isRelative ? startValues[i] : 0;
		}
	}

	@Override
	protected void computeOverride(int iteration, int lastIteration, int deltaMillis) {
		if (target == null || equation == null) return;

		for (int i=0; i<combinedTweenCnt; i++) {
			float startValue = !isFrom ? startValues[i] : targetValues[i];
			float deltaValue = (targetValues[i] - startValues[i]) * (!isFrom ? +1 : -1);
			int millis = isIterationYoyo(iteration) ? durationMillis - currentMillis : currentMillis;
			buffer[i] = equation.compute(millis, startValue, deltaValue, durationMillis);
		}

		accessor.setValues(target, type, buffer);
	}

	// -------------------------------------------------------------------------
	// BaseTween impl.
	// -------------------------------------------------------------------------

	@Override
	protected void forceStartValues(int iteration) {
		if (target == null) return;
		boolean swapStartAndTarget = isIterationYoyo(iteration) ? !isFrom : isFrom;
		accessor.setValues(target, type, swapStartAndTarget ? targetValues : startValues);
	}

	@Override
	protected void forceEndValues(int iteration) {
		if (target == null) return;
		boolean swapStartAndTarget = isIterationYoyo(iteration) ? !isFrom : isFrom;
		accessor.setValues(target, type, swapStartAndTarget ? startValues : targetValues);
	}

	@Override
	protected int getChildrenCount() {
		return 0;
	}

	@Override
	protected void killTarget(Object target) {
		if (this.target == target) kill();
	}

	@Override
	protected void killTarget(Object target, int tweenType) {
		if (this.target == target && this.type == tweenType) kill();
	}

	@Override
	protected boolean containsTarget(Object target) {
		return this.target == target;
	}

	@Override
	protected boolean containsTarget(Object target, int tweenType) {
		return this.target == target && this.type == tweenType;
	}
}
