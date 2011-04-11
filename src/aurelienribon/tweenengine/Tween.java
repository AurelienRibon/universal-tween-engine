package aurelienribon.tweenengine;

import aurelienribon.tweenengine.callbacks.TweenCompleteCallback;
import java.util.ArrayList;

public class Tween {
	// -------------------------------------------------------------------------
	// Static
	// -------------------------------------------------------------------------

	private static final int MODE_UNKNOWN= -1;
	private static final int MODE_TO = 0;
	private static final int MODE_FROM = 1;
	private static final int MODE_SET = 2;
	private static final int MAX_COMBINED_TWEENS = 3;

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
	public static Tween to(Tweenable target, int tweenType,
		TweenEquation equation, int durationMillis, float targetValue) {

		Tween newTween = pool.get();
		tmp[0] = targetValue;

		newTween.reset(MODE_TO, target, tweenType, equation, durationMillis, tmp);
		runningTweens.add(newTween);

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
	public static Tween to(Tweenable target, int tweenType,
		TweenEquation equation, int durationMillis, float targetValue1, float targetValue2) {

		Tween newTween = pool.get();
		tmp[0] = targetValue1;
		tmp[1] = targetValue2;

		newTween.reset(MODE_TO, target, tweenType, equation, durationMillis, tmp);
		runningTweens.add(newTween);

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
	public static Tween to(Tweenable target, int tweenType,
		TweenEquation equation, int durationMillis, float targetValue1, float targetValue2, float targetValue3) {

		Tween newTween = pool.get();
		tmp[0] = targetValue1;
		tmp[1] = targetValue2;
		tmp[2] = targetValue3;

		newTween.reset(MODE_TO, target, tweenType, equation, durationMillis, tmp);
		runningTweens.add(newTween);

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
	public static Tween from(Tweenable target, int tweenType,
		TweenEquation equation, int durationMillis, float startValue) {

		Tween newTween = pool.get();
		tmp[0] = startValue;

		newTween.reset(MODE_FROM, target, tweenType, equation, durationMillis, tmp);
		runningTweens.add(newTween);
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
	public static Tween from(Tweenable target, int tweenType,
		TweenEquation equation, int durationMillis, float startValue1, float startValue2) {

		Tween newTween = pool.get();
		tmp[0] = startValue1;
		tmp[1] = startValue2;

		newTween.reset(MODE_FROM, target, tweenType, equation, durationMillis, tmp);
		runningTweens.add(newTween);
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
	public static Tween from(Tweenable target, int tweenType,
		TweenEquation equation, int durationMillis, float startValue1, float startValue2, float startValue3) {

		Tween newTween = pool.get();
		tmp[0] = startValue1;
		tmp[1] = startValue2;
		tmp[2] = startValue3;

		newTween.reset(MODE_FROM, target, tweenType, equation, durationMillis, tmp);
		runningTweens.add(newTween);
		return newTween;
	}

	// -------------------------------------------------------------------------
	// TWEEN.IMPULSE
	// -------------------------------------------------------------------------

	/**
	 * Starts a new impulse tween (the target is set to the targetValue without
	 * interpolation, right after the delay).
	 * @param target The target of the impulse.
	 * @param tweenType The type of interpolation desired.
	 * @param targetValue The target value for the impulse.
	 * @return The generated Tween.
	 */
	public static Tween impulse(Tweenable target, int tweenType, float targetValue) {

		Tween newTween = pool.get();
		tmp[0] = targetValue;

		newTween.reset(MODE_SET, target, tweenType, null, 0, tmp);
		runningTweens.add(newTween);
		return newTween;
	}

	/**
	 * Starts a new impulse tween (the target is set to the targetValue without
	 * interpolation, right after the delay).
	 * @param target The target of the impulse.
	 * @param tweenType The type of interpolation desired.
	 * @param targetValue1 The 1st target value for the impulse.
	 * @param targetValue2 The 2nd target value for the impulse.
	 * @return The generated Tween.
	 */
	public static Tween impulse(Tweenable target, int tweenType, float targetValue1, float targetValue2) {

		Tween newTween = pool.get();
		tmp[0] = targetValue1;
		tmp[1] = targetValue2;

		newTween.reset(MODE_SET, target, tweenType, null, 0, tmp);
		runningTweens.add(newTween);
		return newTween;
	}

	/**
	 * Starts a new impulse tween (the target is set to the targetValue without
	 * interpolation, right after the delay).
	 * @param target The target of the impulse.
	 * @param tweenType The type of interpolation desired.
	 * @param targetValue1 The 1st target value for the impulse.
	 * @param targetValue2 The 2nd target value for the impulse.
	 * @param targetValue3 The 3rd target value for the impulse.
	 * @return The generated Tween.
	 */
	public static Tween impulse(Tweenable target, int tweenType, float targetValue1, float targetValue2, float targetValue3) {

		Tween newTween = pool.get();
		tmp[0] = targetValue1;
		tmp[1] = targetValue2;
		tmp[2] = targetValue3;

		newTween.reset(MODE_SET, target, tweenType, null, 0, tmp);
		runningTweens.add(newTween);
		return newTween;
	}

	// -------------------------------------------------------------------------
	// STATIC
	// -------------------------------------------------------------------------

	/**
	 * Updates all the running interpolations.
	 */
	public static void update() {
		int currentMillis = (int) System.currentTimeMillis();
		for (int i=0; i<runningTweens.size(); i++) {
			Tween tween = runningTweens.get(i);

			if (tween.isEnded) {
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
	public static void dispose() {
		for (Tween tween : runningTweens)
			pool.free(tween);
		runningTweens.clear();
	}

	// -------------------------------------------------------------------------
	// Tween
	// -------------------------------------------------------------------------

	private int mode = MODE_UNKNOWN;
	private int id = -1;

	private Tweenable target;
	private int tweenType;
	private TweenEquation equation;

	private int combinedTweenCount;
	private final float[] startValues;
	private final float[] addedValues;
	private final float[] targetValues;

	private int startMillis;
	private int durationMillis;
	private int delayMillis;
	private int endDelayMillis;
	private int endMillis;
	private boolean isDelayEnded = false;
	private boolean isStarted = false;
	private boolean isEnded = false;

	protected ArrayList<TweenCompleteCallback> completeCallbacks;

	private Tween() {
		startValues = new float[MAX_COMBINED_TWEENS];
		addedValues = new float[MAX_COMBINED_TWEENS];
		targetValues = new float[MAX_COMBINED_TWEENS];
		completeCallbacks = new ArrayList<TweenCompleteCallback>();
	}

	/**
	 * Starts the interpolation.
	 */
	public void start() {
		startMillis = (int) System.currentTimeMillis();
		endDelayMillis = startMillis + delayMillis;
		endMillis = endDelayMillis + durationMillis;
		isStarted = true;
	}

	/**
	 * Kills this interpolation.
	 * Stops it and removes it from the running tween list.
	 */
	public void kill() {
		isEnded = true;
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
	 * Adds a callback triggered at the end of the interpolation.
	 * Returns the current tween for chaining instructions.
	 */
	public Tween onComplete(TweenCompleteCallback callback) {
		completeCallbacks.add(callback);
		return this;
	}

	/**
	 * Gets an idle copy of this tween.
	 */
	public Tween copy() {
		Tween tween = pool.get();
		tween.reset(mode, target, tweenType, equation, durationMillis, targetValues);
		tween.delay(delayMillis);
		runningTweens.add(tween);
		return tween;
	}

	/**
	 * Sets an id to the tween. It can be used to test if the tween has not
	 * been reset, since ids are set to -1 on each tween reuse.
	 */
	public Tween setId(int id) {
		this.id = id;
		return this;
	}

	/**
	 * Reset the tween with new parameters.
	 */
	private void reset(int mode, Tweenable target, int tweenType, TweenEquation equation,
		int durationMillis, float[] targetValues) {

		this.id = -1;
		this.mode = mode;
		assert (0 <= mode && mode <= 2);

		this.combinedTweenCount = target.getTweenedAttributeCount(tweenType);
		if (this.combinedTweenCount < 1 || this.combinedTweenCount > MAX_COMBINED_TWEENS)
			throw new RuntimeException("Min combined tweens = 1, max = " + MAX_COMBINED_TWEENS);
		
		this.target = target;
		this.tweenType = tweenType;
		this.equation = equation;
		System.arraycopy(targetValues, 0, this.targetValues, 0, MAX_COMBINED_TWEENS);

		this.durationMillis = durationMillis;
		this.delayMillis = 0;
		this.isDelayEnded = false;
		this.isStarted = false;
		this.isEnded = false;

		this.completeCallbacks.clear();
	}

	/**
	 * Updates the tween state.
	 */
	private void update(int currentMillis) {
		// Are we okay ?
		if (!isStarted)
			return;

		// Test for the end of the tween. If true, set the target values to
		// their final values (to avoid precision loss when moving fast), and
		// call the callbacks.
		if (currentMillis >= endMillis) {
			for (int i=0; i<MAX_COMBINED_TWEENS; i++)
				tmp[i] = startValues[i] + addedValues[i];
			target.tweenUpdated(tweenType, tmp);

			for (int k=0; k<completeCallbacks.size(); k++)
				completeCallbacks.get(k).tweenComplete(this);

			isEnded = true;
			return;
		}

		assert (mode != MODE_SET);

		// Wait for the end of the delay and grab the start or end values
		if (!isDelayEnded && currentMillis >= endDelayMillis) {
			isDelayEnded = true;

			switch (mode) {
				case MODE_TO:
					target.getTweenValues(tweenType, startValues);
					for (int i=0; i<MAX_COMBINED_TWEENS; i++)
						addedValues[i] = targetValues[i] - startValues[i];
					break;

				case MODE_FROM:
					System.arraycopy(targetValues, 0, startValues, 0, MAX_COMBINED_TWEENS);
					target.getTweenValues(tweenType, addedValues);
					for (int i=0; i<MAX_COMBINED_TWEENS; i++)
						addedValues[i] -= targetValues[i];
					break;
			}
		} else if (!isDelayEnded) {
			return;
		}

		// New values computation
		for (int i=0; i<combinedTweenCount; i++)
			tmp[i] = equation.compute(
				currentMillis - delayMillis - startMillis,
				startValues[i],
				addedValues[i],
				durationMillis);
		target.tweenUpdated(tweenType, tmp);
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
