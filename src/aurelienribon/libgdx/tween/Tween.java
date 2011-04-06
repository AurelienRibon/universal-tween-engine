package aurelienribon.libgdx.tween;

import aurelienribon.libgdx.tween.callbacks.TweenCompleteCallback;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class Tween {
	// -------------------------------------------------------------------------
	// Static
	// -------------------------------------------------------------------------

	private static final int MAX_COMBINED_TWEENS = 3;

	private static final float[] tmpVals = new float[MAX_COMBINED_TWEENS];
    private static final Array<Tween> runningTweens = new Array<Tween>(false, 30);
	private static final Pool<Tween> tweenPool = new Pool<Tween>(30) {
		@Override protected Tween newObject() {	return new Tween();	}
	};

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

		Tween newTween = tweenPool.obtain();
		tmpVals[0] = targetValue;

		newTween.reset(target, tweenType, equation, durationMillis, false, tmpVals);
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

		Tween newTween = tweenPool.obtain();
		tmpVals[0] = targetValue1;
		tmpVals[1] = targetValue2;

		newTween.reset(target, tweenType, equation, durationMillis, false, tmpVals);
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

		Tween newTween = tweenPool.obtain();
		tmpVals[0] = targetValue1;
		tmpVals[1] = targetValue2;
		tmpVals[3] = targetValue3;

		newTween.reset(target, tweenType, equation, durationMillis, false, tmpVals);
		runningTweens.add(newTween);

		return newTween;
	}

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

		Tween newTween = tweenPool.obtain();
		tmpVals[0] = startValue;

		newTween.reset(target, tweenType, equation, durationMillis, true, tmpVals);
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

		Tween newTween = tweenPool.obtain();
		tmpVals[0] = startValue1;
		tmpVals[1] = startValue2;

		newTween.reset(target, tweenType, equation, durationMillis, true, tmpVals);
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

		Tween newTween = tweenPool.obtain();
		tmpVals[0] = startValue1;
		tmpVals[1] = startValue2;
		tmpVals[2] = startValue3;

		newTween.reset(target, tweenType, equation, durationMillis, true, tmpVals);
		runningTweens.add(newTween);
		return newTween;
	}

	/**
	 * Updates all the running interpolations.
	 */
	public static void update() {
		int currentTimeMillis = (int) System.currentTimeMillis();
		for (Tween tween : runningTweens) {
			if (tween != null && tween.update(currentTimeMillis)) {
				for (int i=0; i<tween.completeCallbacks.size; i++)
					tween.completeCallbacks.get(i).tweenComplete(tween);
				runningTweens.removeValue(tween, true);
				tweenPool.free(tween);
			}
		}
	}

	/**
	 * Resets every static resource.
	 */
	public static void dispose() {
		for (Tween tween : runningTweens)
			tweenPool.free(tween);
		runningTweens.clear();
	}

	// -------------------------------------------------------------------------
	// Tween
	// -------------------------------------------------------------------------

	protected Tweenable target;
	protected int tweenType;
	protected TweenEquation equation;

	protected int tweenCount;
	protected final float[] startValues;
	protected final float[] addedValues;
	protected final float[] targetValues;
	protected boolean isFromModeEnabled;

	protected int startTimeMillis;
	protected int durationMillis;
	protected int delayMillis;
	protected int endDelayTimeMillis;
	protected int endTimeMillis;
	protected boolean isDelayEnded = false;
	protected boolean isStarted = false;

	protected Array<TweenCompleteCallback> completeCallbacks;

	private Tween() {
		startValues = new float[MAX_COMBINED_TWEENS];
		addedValues = new float[MAX_COMBINED_TWEENS];
		targetValues = new float[MAX_COMBINED_TWEENS];
		completeCallbacks = new Array<TweenCompleteCallback>(true, 3);
	}

	/**
	 * Starts the interpolation.
	 */
	public void start() {
		startTimeMillis = (int) System.currentTimeMillis();
		endDelayTimeMillis = startTimeMillis + delayMillis;
		endTimeMillis = endDelayTimeMillis + durationMillis;
		isStarted = true;
	}

	/**
	 * Kills this interpolation.
	 * Stops it and removes it from the running tween list.
	 */
	public void kill() {
		runningTweens.removeValue(this, true);
		tweenPool.free(this);
	}

	/**
	 * Delays the tween. Time has to be specified as milliseconds.
	 * Returns the current tween for chaining instructions.
	 */
	public Tween delay(int delayMillis) {
		this.delayMillis += delayMillis;
		this.endDelayTimeMillis += delayMillis;
		this.endTimeMillis += delayMillis;
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
	 * Reset the tween with new parameters.
	 */
	private void reset(Tweenable target, int tweenType, TweenEquation equation,
		int durationMillis, boolean isFromModeEnabled, float[] targetValues) {

		this.tweenCount = target.getTweenedAttributeCount(tweenType);
		if (this.tweenCount < 1 || this.tweenCount > MAX_COMBINED_TWEENS)
			throw new RuntimeException("You cannot combine more than "
				+ MAX_COMBINED_TWEENS + " tweens together");
		
		this.target = target;
		this.tweenType = tweenType;
		this.equation = equation;
		this.isFromModeEnabled = isFromModeEnabled;
		System.arraycopy(targetValues, 0, this.targetValues, 0, MAX_COMBINED_TWEENS);

		this.durationMillis = durationMillis;
		this.delayMillis = 0;
		this.isDelayEnded = false;
		this.isStarted = false;

		this.completeCallbacks.clear();
	}

	/**
	 * Updates the tween state.
	 * Returns true if the tween is ended and has to be killed.
	 */
	private boolean update(int currentTimeMillis) {
		// Are we okay ?
		if (!isStarted)
			return false;

		// Test for the end of the tween. If true, set the target values to
		// their final values (to avoid precision loss when moving fast).
		if (currentTimeMillis > endTimeMillis) {
			for (int i=0; i<MAX_COMBINED_TWEENS; i++)
				tmpVals[i] = startValues[i] + addedValues[i];
			target.tweenUpdated(tweenType, tmpVals);
			return true;
		}

		// Wait for the end of the delay and grab the start and end values
		if (!isDelayEnded) {
			if (currentTimeMillis < endDelayTimeMillis) {
				return false;
			} else {
				if (!isFromModeEnabled) {
					target.getTweenValues(tweenType, startValues);
					for (int i=0; i<MAX_COMBINED_TWEENS; i++)
						addedValues[i] = targetValues[i] - startValues[i];
				} else {
					System.arraycopy(targetValues, 0, startValues, 0, MAX_COMBINED_TWEENS);
					target.getTweenValues(tweenType, addedValues);
					for (int i=0; i<MAX_COMBINED_TWEENS; i++)
						addedValues[i] -= targetValues[i];
				}
				isDelayEnded = true;
			}
		}

		// New values computation
		for (int i=0; i<tweenCount; i++)
			tmpVals[i] = equation.compute(
				currentTimeMillis - delayMillis - startTimeMillis,
				startValues[i],
				addedValues[i],
				durationMillis);
		target.tweenUpdated(tweenType, tmpVals);
		return false;
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
}
