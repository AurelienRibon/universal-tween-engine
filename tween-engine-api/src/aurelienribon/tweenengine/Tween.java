package aurelienribon.tweenengine;

import aurelienribon.tweenengine.TweenCallback.EventType;
import aurelienribon.tweenengine.equations.Linear;
import java.util.HashMap;
import java.util.Map;

/**
 * Core class of the Tween Engine. A Tween is basically an interpolation
 * between two values of an object attribute. However, the main interest of a 
 * Tween is that you can apply an easing formula on this interpolation, in
 * order to smooth the transitions or to achieve cool effects like springs or
 * bounces.
 * <br/><br/>
 * 
 * The Universal Tween Engine is called "universal" because it is able to apply
 * interpolations on every attribute from every possible object. Therefore,
 * every object in your application can be animated with cool effects: it does
 * not matter if your application is a game, a desktop interface or even a
 * console program! If it makes sense to animate something, then it can be
 * animated through this engine.
 * <br/><br/>
 * 
 * This class contains many static factory methods to create and instantiate
 * new interpolations easily. The common way to create a Tween is by using one 
 * of these factories:
 * <br/><br/>
 *
 * - Tween.to(...)<br/>
 * - Tween.from(...)<br/>
 * - Tween.set(...)<br/>
 * - Tween.call(...)
 * <br/><br/>
 *
 * <h2>Example - firing a Tween</h2>
 *
 * The following example will move the target horizontal position from its
 * current value to x=200 and y=300, during 500ms, but only after a delay of
 * 1000ms. The animation will also be repeated 2 times (the starting position
 * is registered at the end of the delay, so the animation will automatically
 * restart from this registered position).
 * <br/><br/>
 * 
 * <pre> {@code
 * Tween.to(myObject, POSITION_XY, 500)
 *      .target(200, 300)
 *      .ease(Quad.INOUT)
 *      .delay(1000)
 *      .repeat(2, 200)
 *      .start(myManager);
 * }</pre>
 *
 * Tween life-cycles can be automatically managed for you, thanks to the
 * {@link TweenManager} class. If you choose to manage your tween when you start
 * it (with <i>.start(yourManager)</i>), then you don't need to care about it
 * anymore. <b>Tweens are <i>fire-and-forget</i>: don't think about them
 * anymore once you started them (if they are managed of course).</b>
 * <br/><br/>
 *
 * You need to periodicaly update the tween engine, in order to compute the new
 * values. If your tweens are managed, only update the manager; else you need
 * to call <i>update()</i> on your tweens periodically.
 * <br/><br/>
 *
 * <h2>Example - setting up the engine</h2>
 *
 * The engine cannot directly change your objects attributes, since it doesn't
 * know them. Therefore, you need to tell him how to get and set the different
 * attributes of your objects: <b>you need to implement the {@link 
 * TweenAccessor} interface for each object class you will animate</b>. Once
 * done, don't forget to register these implementations, using the static method
 * {@link #registerAccessor}, when you start your application.
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
		return "6.1.0";
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
	 * prevents the allocation of a new tween instance when using the static
	 * factories, thus removing the need for garbage collection. Can be quite
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
	 * waiting in the Tween pool.
	 */
	public static int getPoolSize() {
		return pool.size();
	}

	/**
	 * Increases the minimum capacity of the pool. Capacity defaults to 20.
	 */
	public static void ensurePoolCapacity(int minCapacity) {
		pool.ensureCapacity(minCapacity);
	}

	/**
	 * Clears every static resources used by the whole engine.
	 */
	public static void dispose() {
		isPoolEnabled = true;
		pool.clear();
		Timeline.pool.clear();
	}

	// -------------------------------------------------------------------------
	// Static -- tween accessors
	// -------------------------------------------------------------------------

	private static final Map<Class, TweenAccessor> registeredAccessors = new HashMap<Class, TweenAccessor>();
	private static final float[] buffer = new float[MAX_COMBINED_TWEENS];

	/**
	 * Registers an accessor with the class of an object. This accessor will be
	 * used by tweens applied to every objects implementing the registered
	 * class, or inheriting from it.
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
	 * Factory creating a new standard interpolation. This is the most common
	 * type of interpolation. The starting values are retrieved automatically
	 * after the delay (if any).
	 * <br/><br/>
	 *
	 * <b>You need to set the target values of the interpolation by using one
	 * of the {@link #target} methods</b>. The interpolation will run from the
	 * starting values to these target values.
	 * <br/><br/>
	 *
	 * The common use of Tweens is "fire-and-forget": you do not need to care
	 * for tweens once you added them to a TweenManager, they will be updated
	 * automatically, and cleaned once finished. Common call:
	 * <br/><br/>
	 *
	 * <pre> {@code
	 * Tween.to(myObject, POSITION, 1000)
	 *      .target(50, 70)
	 *      .ease(Quad.INOUT)
	 *      .start(myManager);
	 * }</pre>
	 * 
	 * Several options such as delay, repetitions and callbacks can be added to
	 * the tween.
	 *
	 * @param target The target object of the interpolation.
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
	 * Factory creating a new reversed interpolation. The ending values are
	 * retrieved automatically after the delay (if any).
	 * <br/><br/>
	 *
	 * <b>You need to set the starting values of the interpolation by using one
	 * of the {@link #target} methods</b>. The interpolation will run from the
	 * starting values to these target values.
	 * <br/><br/>
	 *
	 * The common use of Tweens is "fire-and-forget": you do not need to care
	 * for tweens once you added them to a TweenManager, they will be updated
	 * automatically, and cleaned once finished. Common call:
	 * <br/><br/>
	 *
	 * <pre> {@code
	 * Tween.from(myObject, POSITION, 1000)
	 *      .target(0, 0)
	 *      .ease(Quad.INOUT)
	 *      .start(myManager);
	 * }</pre>
	 *
	 * Several options such as delay, repetitions and callbacks can be added to
	 * the tween.
	 *
	 * @param target The target object of the interpolation.
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
	 * Factory creating a new instantaneous interpolation (thus this is not
	 * really an interpolation).
	 * <br/><br/>
	 *
	 * <b>You need to set the target values of the interpolation by using one
	 * of the {@link #target} methods</b>. The interpolation will set the target
	 * attribute to these values after the delay (if any).
	 * <br/><br/>
	 *
	 * The common use of Tweens is "fire-and-forget": you do not need to care
	 * for tweens once you added them to a TweenManager, they will be updated
	 * automatically, and cleaned once finished. Common call:
	 * <br/><br/>
	 *
	 * <pre> {@code
	 * Tween.set(myObject, POSITION)
	 *      .target(50, 70)
	 *      .delay(1000)
	 *      .start(myManager);
	 * }</pre>
	 *
	 * Several options such as delay, repetitions and callbacks can be added to
	 * the tween.
	 *
	 * @param target The target object of the interpolation.
	 * @param tweenType The desired type of interpolation.
	 * @return The generated Tween.
	 */
	public static Tween set(Object target, int tweenType) {
		Tween tween = pool.get();
		tween.setup(target, tweenType, 0);
		return tween;
	}

	/**
	 * Factory creating a new timer. The given callback will be triggered on
	 * each iteration start, after the delay.
	 * <br/><br/>
	 *
	 * The common use of Tweens is "fire-and-forget": you do not need to care
	 * for tweens once you added them to a TweenManager, they will be updated
	 * automatically, and cleaned once finished. Common call:
	 * <br/><br/>
	 *
	 * <pre> {@code
	 * Tween.call(myCallback)
	 *      .delay(1000)
	 *      .repeat(10, 1000)
	 *      .start(myManager);
	 * }</pre>
	 *
	 * @param callback The callback that will be triggered on each iteration
	 * start.
	 * @return The generated Tween.
	 * @see TweenCallback
	 */
	public static Tween call(TweenCallback callback) {
		Tween tween = pool.get();
		tween.setup(null, -1, 0);
		tween.addCallback(EventType.START, callback);
		return tween;
	}

	/**
	 * Convenience method to create an empty tween. Such object is only useful
	 * when placed inside animation sequences (see {@link Timeline}), in which
	 * it may act as a beacon, so you can set callbacks on it in order to
	 * trigger then at the right moment.
	 * @return The generated Tween.
	 * @see Timeline
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
	private Class targetClass;
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
		targetClass = null;
		accessor = null;
		type = -1;
		equation = null;

		isFrom = isRelative = false;
		combinedTweenCnt = 0;
	}

	private void setup(Object target, int tweenType, int durationMillis) {
		if (durationMillis < 0) throw new RuntimeException("Duration can't be negative");

		this.target = target;
		this.targetClass = target != null ? findTargetClass() : null;
		this.type = tweenType;
		this.durationMillis = durationMillis;
	}

	private Class findTargetClass() {
		if (registeredAccessors.containsKey(target.getClass())) return target.getClass();
		if (target instanceof TweenAccessor) return target.getClass();

		Class parentClass = target.getClass().getSuperclass();
		while (parentClass != null && !registeredAccessors.containsKey(parentClass))
			parentClass = parentClass.getSuperclass();

		return parentClass;
	}

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	/**
	 * Sets the easing equation of the tween. Existing equations are located in
	 * <i>aurelienribon.tweenengine.equations</i> package, but you can of course
	 * implement your own, see {@link TweenEquation}.
	 * @return The current tween, for chaining instructions.
	 * @see TweenEquation
	 */
	public Tween ease(TweenEquation easeEquation) {
		this.equation = easeEquation;
		return this;
	}

	/**
	 * Adds a delay to the tween.
	 * @param millis The delay, in milliseconds.
	 * @return The current tween, for chaining instructions.
	 */
	public Tween delay(int millis) {
		if (isStarted) throw new RuntimeException("You can't delay a tween once it is started");
		delayMillis += millis;
		return this;
	}

	/**
	 * Forces the tween to use the TweenAccessor registered with the given
	 * target class. Useful if you want to use a specific accessor associated
	 * to an interface, for instance.
	 * @param targetClass A class registered with an accessor.
	 * @return The current tween, for chaining instructions.
	 */
	public Tween cast(Class targetClass) {
		if (isStarted) throw new RuntimeException("You can't cast the target of a tween once it is started");
		this.targetClass = targetClass;
		isBuilt = false;
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
	 * @return The current tween, for chaining instructions.
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
	 * @return The current tween, for chaining instructions.
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
	 * @return The current tween, for chaining instructions.
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
	 * @return The current tween, for chaining instructions.
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
	 * @return The current tween, for chaining instructions.
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
	 * @return The current tween, for chaining instructions.
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
	 * @return The current tween, for chaining instructions.
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
	 * @return The current tween, for chaining instructions.
	 */
	public Tween targetRelative(float... targetValues) {
		if (isStarted) throw new RuntimeException("You can't change the target of a tween once it is started");
		if (targetValues.length > MAX_COMBINED_TWEENS)
			throw new RuntimeException("You cannot set more than " + MAX_COMBINED_TWEENS + " targets.");
		System.arraycopy(targetValues, 0, this.targetValues, 0, targetValues.length);
		isRelative = true;
		return this;
	}

	
	@Override
	public Tween build() {
		if (!isBuilt) {
			if (target != null) accessor = registeredAccessors.get(targetClass);
			if (accessor != null) combinedTweenCnt = accessor.getValues(target, type, buffer);

			if (target != null && accessor == null)
				throw new RuntimeException("No TweenAccessor was found for the target");
			if (combinedTweenCnt < 0 || combinedTweenCnt > MAX_COMBINED_TWEENS)
				throw new RuntimeException("Min combined tweens = 0, max = " + MAX_COMBINED_TWEENS);

			isBuilt = true;
		}
		return this;
	}

	@Override
	public Tween start() {
		build();
		currentMillis = 0;
		isStarted = true;
		return this;
	}

	@Override
	public Tween start(TweenManager manager) {
		manager.add(this);
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
	public Tween addCallback(EventType callbackType, TweenCallback callback) {
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

	// -------------------------------------------------------------------------
	// Getters
	// -------------------------------------------------------------------------
	
	/**
	 * Gets the target object.
	 */
	public Object getTarget() {
		return target;
	}

	/**
	 * Gets the type of the tween.
	 */
	public int getType() {
		return type;
	}

	/**
	 * Gets the easing equation.
	 */
	public TweenEquation getEasing() {
		return equation;
	}

	/**
	 * Gets the target values. The returned buffer is as long as the maximum
	 * allowed combined values. Therefore, you're surely not interested in all
	 * its content. Use {@link #getCombinedTweenCount()} to get the number of
	 * interesting slots.
	 */
	public float[] getTargetValues() {
		return targetValues;
	}

	/**
	 * Gets the number of combined animations.
	 */
	public int getCombinedTweenCount() {
		return combinedTweenCnt;
	}

	/**
	 * Gets the TweenAccessor used with the target.
	 */
	public TweenAccessor getAccessor() {
		return accessor;
	}

	/**
	 * Gets the class that was used to find the associated TweenAccessor.
	 */
	public Class getTargetClass() {
		return targetClass;
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

		if (durationMillis == 0) {
			accessor.setValues(target, type, isFrom ? startValues : targetValues);
			return;
		}

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
