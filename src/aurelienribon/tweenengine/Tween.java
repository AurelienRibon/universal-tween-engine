package aurelienribon.tweenengine;

import aurelienribon.tweenengine.callbacks.TweenCompleteCallback;
import aurelienribon.tweenengine.callbacks.TweenIterationCompleteCallback;
import java.util.ArrayList;

/**
 * Main class of the Tween Engine. It contains many static factory methods to
 * create and instantiate new interpolations.
 *
 * <p>
 * <b>All factory methods are thread-safe.</b>
 * </p>
 *
 * <p>
 * The following example will move the target horizontal position from its
 * current value to x=200, during 500ms, but only after a delay of 1000ms. The
 * transition will also be repeated 2 times (the starting position is
 * registered at the end of the delay, so the animation will automatically 
 * restart from this registred position).
 * </p>
 *
 * <pre>
 * Tween.to(target, POSITION_X, Quad.INOUT, 500, 200).delay(1000).repeat(2).start();
 * </pre>
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
	public static final int MAX_COMBINED_TWEENS = 15;
	/** Tween was created using Tween.to(). */
	public static final int MODE_TO = 0;
	/** Tween was created using Tween.from(). */
	public static final int MODE_FROM = 1;
	/** Tween was created using Tween.set(). */
	public static final int MODE_SET = 2;
	/** Tween was created using Tween.call(). */
	public static final int MODE_CALL = 3;

	private static final ArrayList<Tween> runningTweens;
	private static final Pool<Tween> pool;
	private static final float[] tmp = new float[MAX_COMBINED_TWEENS];

	static {
		runningTweens = new ArrayList<Tween>(20);
		pool = new Pool<Tween>(20) {
			@Override
			protected Tween getNew() {
				return new Tween();
			}
		};
	}

	// -------------------------------------------------------------------------
	// STATIC ENGINE
	// -------------------------------------------------------------------------

	/**
	 * Updates every running interpolation.
	 */
	public static synchronized void update() {
		long currentMillis = System.currentTimeMillis();
		for (int i=0; i<runningTweens.size(); i++) {
			Tween tween = runningTweens.get(i);
			boolean shoudRepeat = (tween.repeatCnt < 0) || (tween.iteration < tween.repeatCnt);

			if (tween.isKilled) {
				runningTweens.remove(i);
				pool.free(tween);
				i -= 1;
			} else if (tween.isEnded && !shoudRepeat) {
				for (int ii=0; ii<tween.completeCallbacks.size(); ii++)
					tween.completeCallbacks.get(ii).tweenComplete(tween);
				runningTweens.remove(i);
				pool.free(tween);
				i -= 1;
			} else {
				tween.update(currentMillis);
			}
		}
	}

	/**
	 * Resets every static resource.
	 */
	public static synchronized void dispose() {
		pool.clear();
		runningTweens.clear();
	}

	/**
	 * Gets the count of currently running tweens.
	 */
	public static synchronized int getRunningTweenCount() {
		return runningTweens.size();
	}

	/**
	 * Kills every running tween.
	 */
	public static synchronized void killAllTweens() {
		for (int i=0; i<runningTweens.size(); i++)
			runningTweens.get(i).kill();
	}

	/**
	 * Kills every tween associated to a specific target.
	 */
	public static synchronized void killTweens(Tweenable target) {
		for (int i=0; i<runningTweens.size(); i++)
			if (runningTweens.get(i).getTarget() == target)
				runningTweens.get(i).kill();
	}

	// -------------------------------------------------------------------------
	// TWEEN.TO
	// -------------------------------------------------------------------------

	/**
	 * Starts a new tween.
	 * @param target The target of the interpolation.
	 * @param tweenType The type of interpolation desired.
	 * @param equation The equation used during the interpolation.
	 * @param durationMillis The duration of the interpolation, in milliseconds.
	 * @param targetValue The ending value for the interpolation.
	 * @return The generated Tween.
	 */
	public static synchronized Tween to(Tweenable target, int tweenType,
		TweenEquation equation, int durationMillis, float targetValue) {

		Tween newTween = pool.get();
		tmp[0] = targetValue;

		newTween.reset(MODE_TO, target, tweenType, equation, durationMillis, tmp);
		return newTween;
	}

	/**
	 * Starts a new tween.
	 * @param target The target of the interpolation.
	 * @param tweenType The type of interpolation desired.
	 * @param equation The equation used during the interpolation.
	 * @param durationMillis The duration of the interpolation, in milliseconds.
	 * @param targetValue1 The 1st ending value for the interpolation.
	 * @param targetValue2 The 2nd ending value for the interpolation.
	 * @return The generated Tween.
	 */
	public static synchronized Tween to(Tweenable target, int tweenType,
		TweenEquation equation, int durationMillis, float targetValue1, float targetValue2) {

		Tween newTween = pool.get();
		tmp[0] = targetValue1;
		tmp[1] = targetValue2;

		newTween.reset(MODE_TO, target, tweenType, equation, durationMillis, tmp);
		return newTween;
	}

	/**
	 * Starts a new tween.
	 * @param target The target of the interpolation.
	 * @param tweenType The type of interpolation desired.
	 * @param equation The equation used during the interpolation.
	 * @param durationMillis The duration of the interpolation, in milliseconds.
	 * @param targetValue1 The 1st ending value for the interpolation.
	 * @param targetValue2 The 2nd ending value for the interpolation.
	 * @param targetValue3 The 3rd ending value for the interpolation.
	 * @return The generated Tween.
	 */
	public static synchronized Tween to(Tweenable target, int tweenType,
		TweenEquation equation, int durationMillis, float targetValue1, float targetValue2, float targetValue3) {

		Tween newTween = pool.get();
		tmp[0] = targetValue1;
		tmp[1] = targetValue2;
		tmp[2] = targetValue3;

		newTween.reset(MODE_TO, target, tweenType, equation, durationMillis, tmp);
		return newTween;
	}

	/**
	 * Starts a new tween.
	 * @param target The target of the interpolation.
	 * @param tweenType The type of interpolation desired.
	 * @param equation The equation used during the interpolation.
	 * @param durationMillis The duration of the interpolation, in milliseconds.
	 * @param targetValues The ending values for the interpolation.
	 * @return The generated Tween.
	 */
	public static synchronized Tween to(Tweenable target, int tweenType,
		TweenEquation equation, int durationMillis, float... targetValues) {

		Tween newTween = pool.get();
		for (int i=0; i<targetValues.length && i<MAX_COMBINED_TWEENS; i++)
			tmp[i] = targetValues[i];

		newTween.reset(MODE_TO, target, tweenType, equation, durationMillis, tmp);
		return newTween;
	}

	// -------------------------------------------------------------------------
	// TWEEN.FROM
	// -------------------------------------------------------------------------

	/**
	 * Starts a new tween.
	 * @param target The target of the interpolation.
	 * @param tweenType The type of interpolation desired.
	 * @param equation The equation used during the interpolation.
	 * @param durationMillis The duration of the interpolation, in milliseconds.
	 * @param startValue The starting value for the interpolation.
	 * @return The generated Tween.
	 */
	public static synchronized Tween from(Tweenable target, int tweenType,
		TweenEquation equation, int durationMillis, float startValue) {

		Tween newTween = pool.get();
		tmp[0] = startValue;

		newTween.reset(MODE_FROM, target, tweenType, equation, durationMillis, tmp);
		return newTween;
	}

	/**
	 * Starts a new tween.
	 * @param target The target of the interpolation.
	 * @param tweenType The type of interpolation desired.
	 * @param equation The equation used during the interpolation.
	 * @param durationMillis The duration of the interpolation, in milliseconds.
	 * @param startValue1 The 1st starting value for the interpolation.
	 * @param startValue2 The 2nd starting value for the interpolation.
	 * @return The generated Tween.
	 */
	public static synchronized Tween from(Tweenable target, int tweenType,
		TweenEquation equation, int durationMillis, float startValue1, float startValue2) {

		Tween newTween = pool.get();
		tmp[0] = startValue1;
		tmp[1] = startValue2;

		newTween.reset(MODE_FROM, target, tweenType, equation, durationMillis, tmp);
		return newTween;
	}

	/**
	 * Starts a new tween.
	 * @param target The target of the interpolation.
	 * @param tweenType The type of interpolation desired.
	 * @param equation The equation used during the interpolation.
	 * @param durationMillis The duration of the interpolation, in milliseconds.
	 * @param startValue1 The 1st starting value for the interpolation.
	 * @param startValue2 The 2nd starting value for the interpolation.
	 * @param startValue3 The 3rd starting value for the interpolation.
	 * @return The generated Tween.
	 */
	public static synchronized Tween from(Tweenable target, int tweenType,
		TweenEquation equation, int durationMillis, float startValue1, float startValue2, float startValue3) {

		Tween newTween = pool.get();
		tmp[0] = startValue1;
		tmp[1] = startValue2;
		tmp[2] = startValue3;

		newTween.reset(MODE_FROM, target, tweenType, equation, durationMillis, tmp);
		return newTween;
	}

	/**
	 * Starts a new tween.
	 * @param target The target of the interpolation.
	 * @param tweenType The type of interpolation desired.
	 * @param equation The equation used during the interpolation.
	 * @param durationMillis The duration of the interpolation, in milliseconds.
	 * @param startValues The starting values for the interpolation.
	 * @return The generated Tween.
	 */
	public static synchronized Tween from(Tweenable target, int tweenType,
		TweenEquation equation, int durationMillis, float... startValues) {

		Tween newTween = pool.get();
		for (int i=0; i<startValues.length && i<MAX_COMBINED_TWEENS; i++)
			tmp[i] = startValues[i];

		newTween.reset(MODE_FROM, target, tweenType, equation, durationMillis, tmp);
		return newTween;
	}

	// -------------------------------------------------------------------------
	// TWEEN.SET
	// -------------------------------------------------------------------------

	/**
	 * Starts a new instantaneous tween (the target is updated without
	 * interpolation, right after the delay).
	 * @param target The target of the impulse.
	 * @param tweenType The type of interpolation desired.
	 * @param targetValue The target value for the impulse.
	 * @return The generated Tween.
	 */
	public static synchronized Tween set(Tweenable target, int tweenType, float targetValue) {
		Tween newTween = pool.get();
		tmp[0] = targetValue;

		newTween.reset(MODE_SET, target, tweenType, null, 0, tmp);
		return newTween;
	}

	/**
	 * Starts a new instantaneous tween (the target is updated without
	 * interpolation, right after the delay).
	 * @param target The target of the impulse.
	 * @param tweenType The type of interpolation desired.
	 * @param targetValue1 The 1st target value for the impulse.
	 * @param targetValue2 The 2nd target value for the impulse.
	 * @return The generated Tween.
	 */
	public static synchronized Tween set(Tweenable target, int tweenType, float targetValue1, float targetValue2) {
		Tween newTween = pool.get();
		tmp[0] = targetValue1;
		tmp[1] = targetValue2;

		newTween.reset(MODE_SET, target, tweenType, null, 0, tmp);
		return newTween;
	}

	/**
	 * Starts a new instantaneous tween (the target is updated without
	 * interpolation, right after the delay).
	 * @param target The target of the impulse.
	 * @param tweenType The type of interpolation desired.
	 * @param targetValue1 The 1st target value for the impulse.
	 * @param targetValue2 The 2nd target value for the impulse.
	 * @param targetValue3 The 3rd target value for the impulse.
	 * @return The generated Tween.
	 */
	public static synchronized Tween set(Tweenable target, int tweenType, float targetValue1, float targetValue2, float targetValue3) {
		Tween newTween = pool.get();
		tmp[0] = targetValue1;
		tmp[1] = targetValue2;
		tmp[2] = targetValue3;

		newTween.reset(MODE_SET, target, tweenType, null, 0, tmp);
		return newTween;
	}

	/**
	 * Starts a new instantaneous tween (the target is updated without
	 * interpolation, right after the delay).
	 * @param target The target of the impulse.
	 * @param tweenType The type of interpolation desired.
	 * @param targetValues The target values for the impulse.
	 * @return The generated Tween.
	 */
	public static synchronized Tween set(Tweenable target, int tweenType, float... targetValues) {
		Tween newTween = pool.get();
		for (int i=0; i<targetValues.length && i<MAX_COMBINED_TWEENS; i++)
			tmp[i] = targetValues[i];

		newTween.reset(MODE_SET, target, tweenType, null, 0, tmp);
		return newTween;
	}

	// -------------------------------------------------------------------------
	// TWEEN.CALL
	// -------------------------------------------------------------------------

	public static synchronized Tween call(TweenIterationCompleteCallback callback) {
		Tween newTween = pool.get();
		newTween.reset(MODE_CALL, null, -1, null, 0, null);
		newTween.iterationCompleteCallbacks.add(callback);
		return newTween;
	}

	// -------------------------------------------------------------------------
	// Tween implementation
	// -------------------------------------------------------------------------

	// Main
	private Tweenable target;
	private int tweenType;
	private TweenEquation equation;

	// General
	private int mode = -1;
	private int id = -1;

	// Values
	private int combinedTweenCount;
	private final float[] startValues;
	private final float[] addedValues;
	private final float[] targetValues;

	// Timings
	private long startMillis;
	private int durationMillis;
	private int delayMillis;
	private long endDelayMillis;
	private long endMillis;
	private boolean isStarted = false;
	private boolean isDelayEnded = false;
	private boolean isEnded = false;
	private boolean isKilled = false;

	// Callbacks
	private final ArrayList<TweenCompleteCallback> completeCallbacks;
	private final ArrayList<TweenIterationCompleteCallback> iterationCompleteCallbacks;

	// Repeat
	private int repeatCnt;
	private int iteration;
	private int repeatDelayMillis;
	private long endRepeatDelayMillis;

	// Misc
	private final float[] localTmp = new float[MAX_COMBINED_TWEENS];

	private Tween() {
		startValues = new float[MAX_COMBINED_TWEENS];
		addedValues = new float[MAX_COMBINED_TWEENS];
		targetValues = new float[MAX_COMBINED_TWEENS];
		completeCallbacks = new ArrayList<TweenCompleteCallback>(3);
		iterationCompleteCallbacks = new ArrayList<TweenIterationCompleteCallback>(3);
	}

	/**
	 * Starts or restart the interpolation.
	 */
	public void start() {
		startMillis = System.currentTimeMillis();
		endDelayMillis = startMillis + delayMillis;

		if (iteration > 0 && repeatDelayMillis < 0)
			endDelayMillis = Math.max(endDelayMillis + repeatDelayMillis, startMillis);

		endMillis = endDelayMillis + durationMillis;
		endRepeatDelayMillis = Math.max(endMillis, endMillis + repeatDelayMillis);

		isStarted = true;
		isDelayEnded = false;
		isEnded = false;
		isKilled = false;
		
		if (!runningTweens.contains(this))
			runningTweens.add(this);
	}

	/**
	 * Kills this interpolation.
	 * Stops it and removes it from the running tween list.
	 */
	public void kill() {
		isKilled = true;
	}

	/**
	 * Delays the tween. Time has to be specified as milliseconds.
	 * Returns the current tween for chaining instructions.
	 */
	public Tween delay(int delayMillis) {
		this.delayMillis += delayMillis;
		this.endDelayMillis += delayMillis;
		this.endMillis += delayMillis;
		return this;
	}

	/**
	 * Adds a callback triggered at the end of the interpolation (including
	 * repeats). Returns the current tween for chaining instructions.
	 */
	public Tween onComplete(TweenCompleteCallback callback) {
		completeCallbacks.add(callback);
		return this;
	}

	/**
	 * Adds a callback triggered at the end each repeat iteration.
	 * Returns the current tween for chaining instructions.
	 */
	public Tween onComplete(TweenIterationCompleteCallback callback) {
		iterationCompleteCallbacks.add(callback);
		return this;
	}

	/**
	 * Gets an idle copy of this tween.
	 */
	public Tween copy() {
		Tween tween = pool.get();
		tween.reset(mode, target, tweenType, equation, durationMillis, targetValues);
		tween.delay(delayMillis);
		tween.repeat(repeatCnt, repeatDelayMillis);
		return tween;
	}

	/**
	 * Sets an id to the tween. It can be used to test if the tween has not
	 * been reset, since ids are reset to -1 on each tween reuse.
	 */
	public Tween setId(int id) {
		this.id = id;
		return this;
	}

	/**
	 * Repeats the tween for a given number of times. For infinity repeats,
	 * use Tween.INFINITY.
	 */
	public Tween repeat(int count) {
		return repeat(count, 0);
	}

	/**
	 * Repeats the tween for a given number of times. For infinity repeats,
	 * use Tween.INFINITY. A delay before the repeat occurs can be specified.
	 */
	public Tween repeat(int count, int delayMillis) {
		repeatDelayMillis = delayMillis;
		repeatCnt = count;
		return this;
	}

	private void reset(int mode, Tweenable target, int tweenType, TweenEquation equation,
		int durationMillis, float[] targetValues) {

		this.target = target;
		this.tweenType = tweenType;
		this.equation = equation;

		this.id = -1;
		this.mode = mode;

		if (target != null) {
			this.combinedTweenCount = target.getTweenValues(tweenType, localTmp);
			if (this.combinedTweenCount < 1 || this.combinedTweenCount > MAX_COMBINED_TWEENS)
				throw new RuntimeException("Min combined tweens = 1, max = " + MAX_COMBINED_TWEENS);
			System.arraycopy(targetValues, 0, this.targetValues, 0, combinedTweenCount);
		} else {
			this.combinedTweenCount = 0;
		}

		this.durationMillis = durationMillis;
		this.delayMillis = 0;
		this.isStarted = false;
		this.isDelayEnded = false;
		this.isEnded = false;
		this.isKilled = false;

		this.completeCallbacks.clear();
		this.iterationCompleteCallbacks.clear();

		this.repeatCnt = 0;
		this.iteration = 0;
		this.repeatDelayMillis = 0;
	}

	private void update(long currentMillis) {
		// Are we started ?
		if (!isStarted)
			return;

		// Shall we repeat ?
		if ((repeatCnt < 0 || iteration < repeatCnt) && currentMillis >= endRepeatDelayMillis) {
			iteration += 1;
			start();
			return;
		}

		// Wait for the end of the delay then either grab the start or end
		// values if it is the first iteration, or restart from those values
		// if the animation is replaying.
		if (!isDelayEnded && currentMillis >= endDelayMillis) {
			isDelayEnded = true;

			if (iteration > 0 && target != null) {
				target.tweenUpdated(tweenType, startValues);
			} else {
				switch (mode) {
					case MODE_TO:
						target.getTweenValues(tweenType, startValues);
						for (int i=0; i<combinedTweenCount; i++)
							addedValues[i] = targetValues[i] - startValues[i];
						break;

					case MODE_FROM:
						System.arraycopy(targetValues, 0, startValues, 0, MAX_COMBINED_TWEENS);
						target.getTweenValues(tweenType, addedValues);
						for (int i=0; i<combinedTweenCount; i++)
							addedValues[i] -= targetValues[i];
						break;

					case MODE_SET:
						target.getTweenValues(tweenType, startValues);
						for (int i=0; i<combinedTweenCount; i++)
							addedValues[i] = targetValues[i] - startValues[i];
						break;

					case MODE_CALL:
						break;

					default: assert false; break;
				}
			}
		} else if (!isDelayEnded) {
			return;
		}

		// Test for the end of the tween. If true, set the target values to
		// their final values (to avoid precision loss when moving fast), and
		// call the callbacks.
		if (isEnded) {
			return;
		} else if (currentMillis >= endMillis) {
			if (target != null) {
				for (int i=0; i<combinedTweenCount; i++)
					localTmp[i] = startValues[i] + addedValues[i];
				target.tweenUpdated(tweenType, localTmp);
			}

			for (int k=0; k<iterationCompleteCallbacks.size(); k++)
				iterationCompleteCallbacks.get(k).iterationComplete(this);

			isEnded = true;
			return;
		}

		// New values computation
		if (target != null) {
			for (int i=0; i<combinedTweenCount; i++)
				localTmp[i] = equation.compute(
					currentMillis - endDelayMillis,
					startValues[i],
					addedValues[i],
					durationMillis);
			target.tweenUpdated(tweenType, localTmp);
		}
	}

	public int getMode() {
		return mode;
	}

	public Tweenable getTarget() {
		return target;
	}

	public int getTweenType() {
		return tweenType;
	}

	public TweenEquation getEquation() {
		return equation;
	}

	public float[] getTargetValues() {
		return targetValues;
	}

	public int getDurationMillis() {
		return durationMillis;
	}

	public int getDelayMillis() {
		return delayMillis;
	}

	public int getCombinedTweenCount() {
		return combinedTweenCount;
	}

	public int getId() {
		return id;
	}
}
