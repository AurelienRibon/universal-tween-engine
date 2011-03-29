package aurelienribon.libgdx.tween;

import aurelienribon.libgdx.tween.callbacks.TweenCompleteCallback;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class Tween {
    private final static Array<Tween> runningTweens = new Array<Tween>(false, 30);
	private final static Pool<Tween> tweenPool = new Pool<Tween>(30) {
		@Override protected Tween newObject() {	return new Tween();	}
	};

	/**
	 * Starts a new tweening interpolation.
	 * @param target The target on which the interpolation wil be done.
	 * @param tweenType The type of interpolation desired. It will be used
	 * to determine the starting value for the interpolation, by calling
	 * Tweenable.getTweenValue(). Can be any custom integer.
	 * @param equation The equation used during the interpolation.
	 * @param targetValue The target value for the interpolation.
	 * @param durationMillis The duration of the interpolation, in milliseconds.
	 */
	public static Tween to(Tweenable target, int tweenType,
		TweenEquation equation, float targetValue, long durationMillis) {

		// Look for a free tween in the pool
		Tween newTween = tweenPool.obtain();
		
		// Set tween to busy state, define its parameters, and run it !
		newTween.reset(target, tweenType, equation, targetValue, durationMillis, 0, null);
		runningTweens.add(newTween);
		return newTween;
	}

	/**
	 * Updates all the running interpolations.
	 */
	public static void update() {
		long currentTimeMillis = System.currentTimeMillis();
		for (Tween tween : runningTweens) {
			if (tween != null && tween.update(currentTimeMillis)) {
				if (tween.completeCallback != null)
					tween.completeCallback.tweenComplete(tween);
				runningTweens.removeValue(tween, true);
			}
		}
	}

	// -------------------------------------------------------------------------
	// -------------------------------------------------------------------------

	private Tweenable target;
	private int tweenType;
	private TweenEquation equation;
	private float startValue;
	private float addedValue;
	private long startTimeMillis;
	private long durationMillis;
	private long delayMillis;
	private long endDelayTimeMillis;
	private long endTimeMillis;
	private TweenCompleteCallback completeCallback;

	private boolean isStarted = false;

	/**
	 * YOU SHALL NOT...PAAAASS !
	 */
	private Tween() {
	}

	/**
	 * Kills this interpolation.
	 * Stops it and removes it from the running tween list.
	 */
	public void kill() {
		endTimeMillis = 0;
	}

	/**
	 * Delays the tween. Time has to be specified as milliseconds.
	 * Returns the current tween for chaining instructions.
	 */
	public Tween delay(long delayMillis) {
		this.delayMillis = delayMillis;
		this.endDelayTimeMillis = startTimeMillis + delayMillis;
		this.endTimeMillis = endDelayTimeMillis + durationMillis;
		return this;
	}

	/**
	 * Sets the callback triggered at the end of the interpolation.
	 * Returns the current tween for chaining instructions.
	 */
	public Tween onComplete(TweenCompleteCallback callback) {
		this.completeCallback = callback;
		return this;
	}

	/**
	 * Reset the tween with new parameters.
	 */
	private void reset(Tweenable target, int tweenType, TweenEquation equation,
		float targetValue, long durationMillis, long delayMillis,
		TweenCompleteCallback completeCallback) {

		this.target = target;
		this.tweenType = tweenType;
		this.equation = equation;
		this.addedValue = targetValue;
		this.startTimeMillis = System.currentTimeMillis();
		this.durationMillis = durationMillis;
		this.delayMillis = delayMillis;
		this.endDelayTimeMillis = startTimeMillis + delayMillis;
		this.endTimeMillis = endDelayTimeMillis + durationMillis;
		this.completeCallback = completeCallback;

		this.isStarted = false;
	}

	/**
	 * Updates the tween state.
	 * Returns true if the tween is ended and has to be killed.
	 */
	private boolean update(long currentTimeMillis) {
		// Test for the end of the tween
		if (currentTimeMillis > endTimeMillis)
			return true;

		// Wait for the end of the delay
		if (currentTimeMillis < endDelayTimeMillis)
			return false;

		// Retrieve the starting value
		if (!isStarted) {
			startValue = target.getTweenValue(tweenType);
			addedValue -= startValue;
			isStarted = true;
		}

		// Current value computation
		float newValue = equation.compute(
			currentTimeMillis - delayMillis - startTimeMillis,
			startValue, 
			addedValue,
			durationMillis);
		target.tweenUpdated(tweenType, newValue);
		return false;
	}
}
