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
	 * Starts a new tween, interpolating between the current value as a starting
	 * value and the targetValue as an ending value.
	 *
	 * @param target The target of the interpolation.
	 * @param tweenType The type of interpolation desired.
	 * @param equation The equation used during the interpolation.
	 * @param targetValue The ending value for the interpolation.
	 * @param durationMillis The duration of the interpolation, in milliseconds.
	 * @return The generated Tween.
	 */
	public static Tween to(Tweenable target, int tweenType,
		TweenEquation equation, float targetValue, int durationMillis) {

		// Look for a free tween in the pool
		Tween newTween = tweenPool.obtain();
		
		// Set tween to busy state, define its parameters, and run it !
		newTween.reset(target, tweenType, equation, targetValue, durationMillis, false);
		runningTweens.add(newTween);
		return newTween;
	}

	/**
	 * Starts a new tween, interpolating between the targetValue as a starting
	 * value and the current value as an ending value.
	 *
	 * @param target The target of the interpolation.
	 * @param tweenType The type of interpolation desired.
	 * @param equation The equation used during the interpolation.
	 * @param targetValue The starting value for the interpolation.
	 * @param durationMillis The duration of the interpolation, in milliseconds.
	 * @return The generated Tween.
	 */
	public static Tween from(Tweenable target, int tweenType,
		TweenEquation equation, float targetValue, int durationMillis) {

		// Look for a free tween in the pool
		Tween newTween = tweenPool.obtain();

		// Set tween to busy state, define its parameters, and run it !
		newTween.reset(target, tweenType, equation, targetValue, durationMillis, true);
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
	// -------------------------------------------------------------------------

	protected Tweenable target;
	protected int tweenType;
	protected TweenEquation equation;

	protected float startValue;
	protected float addedValue;
	protected float targetValue;
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
		endTimeMillis = 0;
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
		float targetValue, int durationMillis, boolean isFromModeEnabled) {

		this.target = target;
		this.tweenType = tweenType;
		this.equation = equation;

		this.targetValue = targetValue;
		this.isFromModeEnabled = isFromModeEnabled;

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

		// Test for the end of the tween
		if (currentTimeMillis > endTimeMillis) {
			target.tweenUpdated(tweenType, startValue + addedValue);
			return true;
		}

		// Wait for the end of the delay and grab the start and end values
		if (!isDelayEnded) {
			if (currentTimeMillis < endDelayTimeMillis) {
				return false;
			} else {
				if (!isFromModeEnabled) {
					startValue = target.getTweenValue(tweenType);
					addedValue = targetValue - startValue;
				} else {
					startValue = targetValue;
					addedValue = target.getTweenValue(tweenType) - targetValue;
				}
				isDelayEnded = true;
			}
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

	public Tweenable getTarget() {
		return target;
	}

	public int getTweenType() {
		return tweenType;
	}

	public TweenEquation getEquation() {
		return equation;
	}

	public float getTargetValue() {
		return targetValue;
	}

	public int getDurationMillis() {
		return durationMillis;
	}

	public int getDelayMillis() {
		return delayMillis;
	}
}
