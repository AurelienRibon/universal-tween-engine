package aurelienribon.tweenengine;

import aurelienribon.tweenengine.equations.Linear;
import java.util.HashMap;
import java.util.Map;

/**
 * Core class of the Tween Engine. A Tween is basically an interpolation
 * between two values of an object attribute. However, the main interest of a
 * Tween is that you can apply an easing formula on this interpolation, in
 * order to smooth the transitions or to achieve cool effects like springs or
 * bounces.
 * <p/>
 *
 * The Universal Tween Engine is called "universal" because it is able to apply
 * interpolations on every attribute from every possible object. Therefore,
 * every object in your application can be animated with cool effects: it does
 * not matter if your application is a game, a desktop interface or even a
 * console program! If it makes sense to animate something, then it can be
 * animated through this engine.
 * <p/>
 *
 * This class contains many static factory methods to create and instantiate
 * new interpolations easily. The common way to create a Tween is by using one
 * of these factories:
 * <p/>
 *
 * - Tween.to(...)<br/>
 * - Tween.from(...)<br/>
 * - Tween.set(...)<br/>
 * - Tween.call(...)
 * <p/>
 *
 * <h2>Example - firing a Tween</h2>
 *
 * The following example will move the target horizontal position from its
 * current value to x=200 and y=300, during 500ms, but only after a delay of
 * 1000ms. The animation will also be repeated 2 times (the starting position
 * is registered at the end of the delay, so the animation will automatically
 * restart from this registered position).
 * <p/>
 *
 * <pre> {@code
 * Tween.to(myObject, POSITION_XY, 0.5f)
 *      .target(200, 300)
 *      .ease(Quad.INOUT)
 *      .delay(1.0f)
 *      .repeat(2, 0.2f)
 *      .start(myManager);
 * }</pre>
 *
 * Tween life-cycles can be automatically managed for you, thanks to the
 * {@link TweenManager} class. If you choose to manage your tween when you start
 * it, then you don't need to care about it anymore. <b>Tweens are
 * <i>fire-and-forget</i>: don't think about them anymore once you started
 * them (if they are managed of course).</b>
 * <p/>
 *
 * You need to periodicaly update the tween engine, in order to compute the new
 * values. If your tweens are managed, only update the manager; else you need
 * to call {@link #update()} on your tweens periodically.
 * <p/>
 *
 * <h2>Example - setting up the engine</h2>
 *
 * The engine cannot directly change your objects attributes, since it doesn't
 * know them. Therefore, you need to tell him how to get and set the different
 * attributes of your objects: <b>you need to implement the {@link
 * TweenAccessor} interface for each object class you will animate</b>. Once
 * done, don't forget to register these implementations, using the static method
 * {@link registerAccessor()}, when you start your application.
 *
 * @see TweenAccessor
 * @see TweenManager
 * @see TweenEquation
 * @see Timeline
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public final class Tween extends BaseTween<Tween> {
	// -------------------------------------------------------------------------
	// Static -- misc
	// -------------------------------------------------------------------------

	/**
	 * Used as parameter in {@link #repeat(int, float)} and
	 * {@link #repeatYoyo(int, float)} methods.
	 */
	public static final int INFINITY = -1;

	private static int combinedAttrsLimit = 3;
	private static int waypointsLimit = 0;
	private static float[] accessorBuffer = new float[3];
	private static float[] pathBuffer = new float[(2+0)*3];

	/**
	 * Changes the limit for combined attributes. Defaults to 3 to reduce
	 * memory footprint.
	 */
	public static void setCombinedAttributesLimit(int limit) {
		Tween.combinedAttrsLimit = limit;
		accessorBuffer = new float[combinedAttrsLimit];
		pathBuffer = new float[(2+waypointsLimit)*combinedAttrsLimit];
	}

	/**
	 * Changes the limit of allowed waypoints for each tween. Defaults to 0 to
	 * reduce memory footprint.
	 */
	public static void setWaypointsLimit(int limit) {
		Tween.waypointsLimit = limit;
		pathBuffer = new float[(2+waypointsLimit)*combinedAttrsLimit];
	}

	/**
	 * Gets the version number of the library.
	 */
	public static String getVersion() {
		return "6.2.0";
	}

	// -------------------------------------------------------------------------
	// Static -- pool
	// -------------------------------------------------------------------------

	private static final Pool.Callback<Tween> poolCallback = new Pool.Callback<Tween>() {
		@Override public void onPool(Tween obj) {obj.reset();}
	};

	private static final Pool<Tween> pool = new Pool<Tween>(20, poolCallback) {
		@Override protected Tween create() {return new Tween();}
	};

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

	// -------------------------------------------------------------------------
	// Static -- tween accessors
	// -------------------------------------------------------------------------

	private static final Map<Class, TweenAccessor> registeredAccessors = new HashMap<Class, TweenAccessor>();

	/**
	 * Registers an accessor with the class of an object. This accessor will be
	 * used by tweens applied to every objects implementing the registered
	 * class, or inheriting from it.
	 *
	 * @param someClass An object class.
	 * @param defaultAccessor The accessor that will be used to tween any
	 * object of class "someClass".
	 */
	public static void registerAccessor(Class someClass, TweenAccessor defaultAccessor) {
		registeredAccessors.put(someClass, defaultAccessor);
	}

	/**
	 * Gets the registered TweenAccessor associated with the given object class.
	 *
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
	 * of the target() methods</b>. The interpolation will run from the
	 * starting values to these target values.
	 * <br/><br/>
	 *
	 * The common use of Tweens is "fire-and-forget": you do not need to care
	 * for tweens once you added them to a TweenManager, they will be updated
	 * automatically, and cleaned once finished. Common call:
	 * <br/><br/>
	 *
	 * <pre> {@code
	 * Tween.to(myObject, POSITION, 1.0f)
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
	 * @param duration The duration of the interpolation, in milliseconds.
	 * @return The generated Tween.
	 */
	public static Tween to(Object target, int tweenType, float duration) {
		Tween tween = pool.get();
		tween.setup(target, tweenType, duration);
		tween.ease(Linear.INOUT);
		tween.path(TweenPaths.linear);
		return tween;
	}

	/**
	 * Factory creating a new reversed interpolation. The ending values are
	 * retrieved automatically after the delay (if any).
	 * <br/><br/>
	 *
	 * <b>You need to set the starting values of the interpolation by using one
	 * of the target() methods</b>. The interpolation will run from the
	 * starting values to these target values.
	 * <br/><br/>
	 *
	 * The common use of Tweens is "fire-and-forget": you do not need to care
	 * for tweens once you added them to a TweenManager, they will be updated
	 * automatically, and cleaned once finished. Common call:
	 * <br/><br/>
	 *
	 * <pre> {@code
	 * Tween.from(myObject, POSITION, 1.0f)
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
	 * @param duration The duration of the interpolation, in milliseconds.
	 * @return The generated Tween.
	 */
	public static Tween from(Object target, int tweenType, float duration) {
		Tween tween = pool.get();
		tween.setup(target, tweenType, duration);
		tween.ease(Linear.INOUT);
		tween.path(TweenPaths.linear);
		tween.isFrom = true;
		return tween;
	}

	/**
	 * Factory creating a new instantaneous interpolation (thus this is not
	 * really an interpolation).
	 * <br/><br/>
	 *
	 * <b>You need to set the target values of the interpolation by using one
	 * of the target() methods</b>. The interpolation will set the target
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
	 *      .delay(1.0f)
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
	 *      .delay(1.0f)
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
		tween.setCallback(callback);
		tween.setCallbackTriggers(TweenCallback.START);
		return tween;
	}

	/**
	 * Convenience method to create an empty tween. Such object is only useful
	 * when placed inside animation sequences (see {@link Timeline}), in which
	 * it may act as a beacon, so you can set a callback on it in order to
	 * trigger some action at the right moment.
	 *
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
	private TweenPath path;

	// General
	private boolean isFrom;
	private boolean isRelative;
	private int combinedAttrsCnt;
	private int waypointsCnt;

	// Values
	private final float[] startValues = new float[combinedAttrsLimit];
	private final float[] targetValues = new float[combinedAttrsLimit];
	private final float[] waypoints = new float[waypointsLimit * combinedAttrsLimit];

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
		path = null;

		isFrom = isRelative = false;
		combinedAttrsCnt = waypointsCnt = 0;
	}

	private void setup(Object target, int tweenType, float duration) {
		if (duration < 0) throw new RuntimeException("Duration can't be negative");

		this.target = target;
		this.targetClass = target != null ? findTargetClass() : null;
		this.type = tweenType;
		this.duration = duration;
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
	 * implement your owns, see {@link TweenEquation}.
	 * <p/>
	 *
	 * <b>Proposed equations are:</b><br/>
	 * - Linear.INOUT,<br/>
	 * - Quad.IN | OUT | INOUT,<br/>
	 * - Cubic.IN | OUT | INOUT,<br/>
	 * - Quart.IN | OUT | INOUT,<br/>
	 * - Quint.IN | OUT | INOUT,<br/>
	 * - Circ.IN | OUT | INOUT,<br/>
	 * - Sine.IN | OUT | INOUT,<br/>
	 * - Expo.IN | OUT | INOUT,<br/>
	 * - Back.IN | OUT | INOUT,<br/>
	 * - Bounce.IN | OUT | INOUT,<br/>
	 * - Elastic.IN | OUT | INOUT
	 *
	 * @return The current tween, for chaining instructions.
	 * @see TweenEquation
	 */
	public Tween ease(TweenEquation easeEquation) {
		this.equation = easeEquation;
		return this;
	}

	/**
	 * Forces the tween to use the TweenAccessor registered with the given
	 * target class. Useful if you want to use a specific accessor associated
	 * to an interface, for instance.
	 *
	 * @param targetClass A class registered with an accessor.
	 * @return The current tween, for chaining instructions.
	 */
	public Tween cast(Class targetClass) {
		if (isStarted) throw new RuntimeException("You can't cast the target of a tween once it is started");
		this.targetClass = targetClass;
		return this;
	}

	/**
	 * Sets the target value of the interpolation. The interpolation will run
	 * from the <b>value at start time (after the delay, if any)</b> to this
	 * target value.
	 * <p/>
	 *
	 * To sum-up:<br/>
	 * - start value: value at start time, after delay<br/>
	 * - end value: param
	 *
	 * @param targetValue The target value of the interpolation.
	 * @return The current tween, for chaining instructions.
	 */
	public Tween target(float targetValue) {
		targetValues[0] = targetValue;
		return this;
	}

	/**
	 * Sets the target values of the interpolation. The interpolation will run
	 * from the <b>values at start time (after the delay, if any)</b> to these
	 * target values.
	 * <p/>
	 *
	 * To sum-up:<br/>
	 * - start values: values at start time, after delay<br/>
	 * - end values: params
	 *
	 * @param targetValue1 The 1st target value of the interpolation.
	 * @param targetValue2 The 2nd target value of the interpolation.
	 * @return The current tween, for chaining instructions.
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
	 * <p/>
	 *
	 * To sum-up:<br/>
	 * - start values: values at start time, after delay<br/>
	 * - end values: params
	 *
	 * @param targetValue1 The 1st target value of the interpolation.
	 * @param targetValue2 The 2nd target value of the interpolation.
	 * @param targetValue3 The 3rd target value of the interpolation.
	 * @return The current tween, for chaining instructions.
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
	 * <p/>
	 *
	 * To sum-up:<br/>
	 * - start values: values at start time, after delay<br/>
	 * - end values: params
	 *
	 * @param targetValues The target values of the interpolation.
	 * @return The current tween, for chaining instructions.
	 */
	public Tween target(float... targetValues) {
		if (targetValues.length > combinedAttrsLimit) throwCombinedAttrsLimitReached();
		System.arraycopy(targetValues, 0, this.targetValues, 0, targetValues.length);
		return this;
	}

	/**
	 * Sets the target value of the interpolation, relatively to the <b>value
	 * at start time (after the delay, if any)</b>.
	 * <p/>
	 *
	 * To sum-up:<br/>
	 * - start value: value at start time, after delay<br/>
	 * - end value: param + value at start time, after delay
	 *
	 * @param targetValue The relative target value of the interpolation.
	 * @return The current tween, for chaining instructions.
	 */
	public Tween targetRelative(float targetValue) {
		isRelative = true;
		targetValues[0] = isInitialized ? targetValue + startValues[0] : targetValue;
		return this;
	}

	/**
	 * Sets the target values of the interpolation, relatively to the <b>values
	 * at start time (after the delay, if any)</b>.
	 * <p/>
	 *
	 * To sum-up:<br/>
	 * - start values: values at start time, after delay<br/>
	 * - end values: params + values at start time, after delay
	 *
	 * @param targetValue1 The 1st relative target value of the interpolation.
	 * @param targetValue2 The 2nd relative target value of the interpolation.
	 * @return The current tween, for chaining instructions.
	 */
	public Tween targetRelative(float targetValue1, float targetValue2) {
		isRelative = true;
		targetValues[0] = isInitialized ? targetValue1 + startValues[0] : targetValue1;
		targetValues[1] = isInitialized ? targetValue2 + startValues[1] : targetValue2;
		return this;
	}

	/**
	 * Sets the target values of the interpolation, relatively to the <b>values
	 * at start time (after the delay, if any)</b>.
	 * <p/>
	 *
	 * To sum-up:<br/>
	 * - start values: values at start time, after delay<br/>
	 * - end values: params + values at start time, after delay
	 *
	 * @param targetValue1 The 1st relative target value of the interpolation.
	 * @param targetValue2 The 2nd relative target value of the interpolation.
	 * @param targetValue3 The 3rd relative target value of the interpolation.
	 * @return The current tween, for chaining instructions.
	 */
	public Tween targetRelative(float targetValue1, float targetValue2, float targetValue3) {
		isRelative = true;
		targetValues[0] = isInitialized ? targetValue1 + startValues[0] : targetValue1;
		targetValues[1] = isInitialized ? targetValue2 + startValues[1] : targetValue2;
		targetValues[2] = isInitialized ? targetValue3 + startValues[2] : targetValue3;
		return this;
	}

	/**
	 * Sets the target values of the interpolation, relatively to the <b>values
	 * at start time (after the delay, if any)</b>.
	 * <p/>
	 *
	 * To sum-up:<br/>
	 * - start values: values at start time, after delay<br/>
	 * - end values: params + values at start time, after delay
	 *
	 * @param targetValues The relative target values of the interpolation.
	 * @return The current tween, for chaining instructions.
	 */
	public Tween targetRelative(float... targetValues) {
		if (targetValues.length > combinedAttrsLimit) throwCombinedAttrsLimitReached();
		for (int i=0; i<targetValues.length; i++) {
			this.targetValues[i] = isInitialized ? targetValues[i] + startValues[i] : targetValues[i];
		}

		isRelative = true;
		return this;
	}

	public Tween waypoint(float targetValue) {
		if (waypointsCnt == waypointsLimit) throwWaypointsLimitReached();
		waypoints[waypointsCnt] = targetValue;
		waypointsCnt += 1;
		return this;
	}

	public Tween waypoint(float targetValue1, float targetValue2) {
		if (waypointsCnt == waypointsLimit) throwWaypointsLimitReached();
		waypoints[waypointsCnt*2] = targetValue1;
		waypoints[waypointsCnt*2+1] = targetValue2;
		waypointsCnt += 1;
		return this;
	}

	public Tween waypoint(float targetValue1, float targetValue2, float targetValue3) {
		if (waypointsCnt == waypointsLimit) throwWaypointsLimitReached();
		waypoints[waypointsCnt*3] = targetValue1;
		waypoints[waypointsCnt*3+1] = targetValue2;
		waypoints[waypointsCnt*3+2] = targetValue3;
		waypointsCnt += 1;
		return this;
	}

	public Tween waypoint(float... targetValues) {
		if (waypointsCnt == waypointsLimit) throwWaypointsLimitReached();
		System.arraycopy(targetValues, 0, waypoints, waypointsCnt*targetValues.length, targetValues.length);
		waypointsCnt += 1;
		return this;
	}

	public Tween path(TweenPath path) {
		this.path = path;
		return this;
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
	public int getCombinedAttributesCount() {
		return combinedAttrsCnt;
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
	// Overrides
	// -------------------------------------------------------------------------

	@Override
	public Tween build() {
		if (target == null) return this;

		accessor = registeredAccessors.get(targetClass);
		if (accessor == null && target instanceof TweenAccessor) accessor = (TweenAccessor) target;
		if (accessor != null) combinedAttrsCnt = accessor.getValues(target, type, accessorBuffer);
		else throw new RuntimeException("No TweenAccessor was found for the target");

		if (combinedAttrsCnt > combinedAttrsLimit) throwCombinedAttrsLimitReached();
		return this;
	}

	@Override
	public void free() {
		pool.free(this);
	}

	@Override
	protected void initializeOverride() {
		if (target == null) return;

		accessor.getValues(target, type, startValues);
		for (int i=0; i<combinedAttrsCnt; i++) {
			targetValues[i] += isRelative ? startValues[i] : 0;

			if (isFrom) {
				float tmp = startValues[i];
				startValues[i] = targetValues[i];
				targetValues[i] = tmp;
			}
		}
	}

	@Override
	protected void computeOverride(int step, int lastStep, float delta) {
		if (target == null || equation == null) return;

		if (duration == 0) {
			accessor.setValues(target, type, targetValues);
			return;
		}

		float time = isYoyo(step) ? duration - currentTime : currentTime;
		float t = equation.compute(time, 0, 1, duration);

		if (waypointsCnt == 0 || path == null) {
			for (int i=0; i<combinedAttrsCnt; i++) {
				accessorBuffer[i] = startValues[i] + t * (targetValues[i] - startValues[i]);
			}

		} else {
			for (int i=0; i<combinedAttrsCnt; i++) {
				pathBuffer[0] = startValues[i];
				pathBuffer[1+waypointsCnt] = targetValues[i];
				for (int ii=0; ii<waypointsCnt; ii++) {
					pathBuffer[ii+1] = waypoints[ii*combinedAttrsCnt+i];
				}

				accessorBuffer[i] = path.compute(t, pathBuffer, waypointsCnt+2);
			}
		}

		accessor.setValues(target, type, accessorBuffer);
	}

	@Override
	protected void forceStartValues() {
		if (target == null) return;
		accessor.setValues(target, type, startValues);
	}

	@Override
	protected void forceEndValues() {
		if (target == null) return;
		accessor.setValues(target, type, targetValues);
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

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private void throwCombinedAttrsLimitReached() {
		String msg = "You cannot combine more than " + combinedAttrsLimit + " "
			+ "attributes in a tween. You can raise this limit with "
			+ "Tween.setCombinedAttributesLimit(), which should be called once "
			+ "in application initialization code.";
		throw new RuntimeException(msg);
	}

	private void throwWaypointsLimitReached() {
		String msg = "You cannot add more than " + waypointsLimit + " "
			+ "waypoints to a tween. You can raise this limit with "
			+ "Tween.setWaypointsLimit(), which should be called once in "
			+ "application initialization code.";
		throw new RuntimeException(msg);
	}
}
