package aurelienribon.tweenengine;

/**
 * BaseTween is the base class of Tween and Timeline. It defines the
 * iteration engine used to play animations for any number of times, and in
 * any direction, at any speed.
 * <p/>
 *
 * It is responsible for calling the different callbacks at the right moments,
 * and for making sure that every callbacks are triggered, even if the update
 * engine gets a big delta time at once.
 *
 * @see Tween
 * @see Timeline
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public abstract class BaseTween<T> {

	// General
	private int step;
	private int repeatCnt;
	private boolean isIterationStep;
	private boolean isYoyo;

	// Timings
	protected float delay;
	protected float duration;
	protected float repeatDelay;
	protected float currentTime;
	protected boolean isStarted; // true when the object is started
	protected boolean isInitialized; // true after the delay
	protected boolean isFinished; // true when all repetitions are done or kill() was called
	protected boolean isPaused; // true if pause() was called

	// Misc
	private TweenCallback callback;
	private int callbackTriggers;
	private Object userData;

	// Package access
	boolean isAutoRemoveEnabled;
	boolean isAutoStartEnabled;

	// -------------------------------------------------------------------------

	protected void reset() {
		step = repeatCnt = 0;
		isIterationStep = isYoyo = false;

		delay = duration = repeatDelay = currentTime = 0;
		isStarted = isInitialized = isFinished = isPaused = false;

		callback = null;
		callbackTriggers = 0;
		userData = null;

		isAutoRemoveEnabled = isAutoStartEnabled = true;
	}

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	/**
	 * Builds and validates the object. Only needed if you want to finalize a
	 * tween or timeline without starting it, since a call to ".start()" also
	 * calls this method.
	 *
	 * @return The current object, for chaining instructions.
	 */
	public T build() {
		return (T) this;
	}

	/**
	 * Starts or restarts the object unmanaged. You will need to take care of
	 * its life-cycle. If you want the tween to be managed for you, use a
	 * {@link TweenManager}.
	 *
	 * @return The current object, for chaining instructions.
	 */
	public T start() {
		build();
		currentTime = 0;
		isStarted = true;
		return (T) this;
	}

	/**
	 * Convenience method to add an object to a manager. Its life-cycle will be
	 * handled for you. Relax and enjoy the animation.
	 *
	 * @return The current object, for chaining instructions.
	 */
	public T start(TweenManager manager) {
		manager.add(this);
		return (T) this;
	}

	/**
	 * Adds a delay to the tween or timeline.
	 *
	 * @param delay A duration.
	 * @return The current tween, for chaining instructions.
	 */
	public T delay(float delay) {
		this.delay += delay;
		return (T) this;
	}

	/**
	 * Kills the tween or timeline. If you are using a TweenManager, this object
	 * will be removed automatically.
	 */
	public void kill() {
		isFinished = true;
	}

	/**
	 * If you want to manually manage your tweens and timelines (without using a
	 * TweenManager), and you enabled object pooling, then you need to call
	 * this method on your tweens and timelines once they are finished (see
	 * <i>isFinished()</i> method).
	 */
	public void free() {
	}

	/**
	 * Pauses the tween or timeline. Further update calls won't have any effect.
	 */
	public void pause() {
		isPaused = true;
	}

	/**
	 * Resumes the tween or timeline. Has no effect is it was no already paused.
	 */
	public void resume() {
		isPaused = false;
	}

	/**
	 * Repeats the tween or timeline for a given number of times.
	 * @param count The number of repetitions. For infinite repetition,
	 * use Tween.INFINITY, or a negative number.
	 *
	 * @param delay A delay between each iteration.
	 * @return The current tween or timeline, for chaining instructions.
	 */
	public T repeat(int count, float delay) {
		if (isStarted) throw new RuntimeException("You can't change the repetitions of a tween or timeline once it is started");
		repeatCnt = count;
		repeatDelay = delay >= 0 ? delay : 0;
		isYoyo = false;
		return (T) this;
	}

	/**
	 * Repeats the tween or timeline for a given number of times.
	 * Every two iterations, it will be played backwards.
	 *
	 * @param count The number of repetitions. For infinite repetition,
	 * use Tween.INFINITY, or '-1'.
	 * @param delay A delay before each repetition.
	 * @return The current tween or timeline, for chaining instructions.
	 */
	public T repeatYoyo(int count, float delay) {
		if (isStarted) throw new RuntimeException("You can't change the repetitions of a tween or timeline once it is started");
		repeatCnt = count;
		repeatDelay = delay >= 0 ? delay : 0;
		isYoyo = true;
		return (T) this;
	}

	/**
	 * Sets the callback. By default, it will be fired at the completion of the
	 * tween or timeline (event COMPLETE). If you want to change this behavior
	 * and add more triggers, use the {@link setCallbackTriggers()} method.
	 *
	 * @see TweenCallback
	 */
	public T setCallback(TweenCallback callback) {
		this.callback = callback;
		this.callbackTriggers = TweenCallback.COMPLETE;
		return (T) this;
	}

	/**
	 * Changes the triggers of the callback. The available triggers, listed as
	 * members of the {@link TweenCallback} interface, are:
	 * <p/>
	 *
	 * <b>BEGIN</b>: right after the delay (if any)<br/>
	 * <b>START</b>: at each iteration beginning<br/>
	 * <b>END</b>: at each iteration ending, before the repeat delay<br/>
	 * <b>COMPLETE</b>: at last END event<br/>
	 * <b>BACK_START</b>: at each backwards iteration beginning, after the repeat delay<br/>
	 * <b>BACK_END</b>: at each backwards iteration ending<br/>
	 * <b>BACK_COMPLETE</b>: at last BACK_END event
	 * <p/>
	 *
	 * <pre> {@code
	 * forwards :         BEGIN                                   COMPLETE
	 * forwards :         START    END      START    END      START    END
	 * |------------------[XXXXXXXXXX]------[XXXXXXXXXX]------[XXXXXXXXXX]
	 * backwards:         bEND  bSTART      bEND  bSTART      bEND  bSTART
	 * backwards:         bCOMPLETE
	 * }</pre>
	 *
	 * @param flags one or more triggers, separated by the '|' operator.
	 * @see TweenCallback
	 */
	public T setCallbackTriggers(int flags) {
		this.callbackTriggers = flags;
		return (T) this;
	}

	/**
	 * Attaches an object to this tween or timeline. It can be useful in order
	 * to retrieve some data from a TweenCallback.
	 *
	 * @param data Any kind of object.
	 * @return The current tween or timeline, for chaining instructions.
	 */
	public T setUserData(Object data) {
		userData = data;
		return (T) this;
	}

	// -------------------------------------------------------------------------
	// Getters
	// -------------------------------------------------------------------------

	/**
	 * Gets the delay of the tween or timeline. Nothing will happen before
	 * this delay.
	 */
	public float getDelay() {
		return delay;
	}

	/**
	 * Gets the duration of a single iteration.
	 */
	public float getDuration() {
		return duration;
	}

	/**
	 * Gets the number of iterations that will be played.
	 */
	public int getRepeatCount() {
		return repeatCnt;
	}

	/**
	 * Gets the delay occuring between two iterations.
	 */
	public float getRepeatDelay() {
		return repeatDelay;
	}

	/**
	 * Returns the complete duration, including initial delay and repetitions.
	 * The formula is as follows:
	 * <pre>
	 * fullDuration = delay + duration + (repeatDelay + duration) * repeatCnt
	 * </pre>
	 */
	public float getFullDuration() {
		if (repeatCnt < 0) return -1;
		return delay + duration + (repeatDelay + duration) * repeatCnt;
	}

	/**
	 * Gets the attached data, or null if none.
	 */
	public Object getUserData() {
		return userData;
	}

	/**
	 * Returns the id of the current step. Values are as follows:<br/>
	 * <ul>
	 * <li>even numbers mean that an iteration is playing,<br/>
	 * <li>odd numbers mean that we are between two iterations,<br/>
	 * <li>-2 means that the initial delay has not ended,<br/>
	 * <li>-1 means that we are before the first iteration,<br/>
	 * <li>repeatCount*2 + 1 means that we are after the last iteration
	 */
	public int getStep() {
		if (!isInitialized) return -2;
		return step;
	}

	/**
	 * Returns true if the tween or timeline has been started.
	 */
	public boolean isStarted() {
		return isStarted;
	}

	/**
	 * Returns true if the tween is finished (i.e. if the tween has reached
	 * its end or has been killed). If you don't use a TweenManager, you may
	 * want to call {@link free()} to reuse the object later.
	 */
	public boolean isFinished() {
		return isFinished;
	}

	/**
	 * Returns true if the iterations are played as yoyo. Yoyo means that
	 * every two iterations, the animation will be played backwards.
	 */
	public boolean isYoyo() {
		return isYoyo;
	}

	/**
	 * Returns true if the tween or timeline is currently paused.
	 */
	public boolean isPaused() {
		return isPaused;
	}

	// -------------------------------------------------------------------------
	// Abstract API
	// -------------------------------------------------------------------------

	protected abstract void forceStartValues();
	protected abstract void forceEndValues();

	protected abstract void initializeOverride();
	protected abstract void computeOverride(int step, int lastStep, float delta);

	protected abstract void killTarget(Object target);
	protected abstract void killTarget(Object target, int tweenType);
	protected abstract boolean containsTarget(Object target);
	protected abstract boolean containsTarget(Object target, int tweenType);

	// -------------------------------------------------------------------------
	// Protected API
	// -------------------------------------------------------------------------

	protected void forceToStart() {
		currentTime = -delay;
		step = -1;
		isIterationStep = false;
		forceStartValues(0);
	}

	protected void forceToEnd(float time) {
		currentTime = time - getFullDuration();
		step = repeatCnt*2 + 1;
		isIterationStep = false;
		forceEndValues(repeatCnt*2);
	}

	protected void callCallback(int type) {
		if (callback != null && (callbackTriggers & type) > 0) callback.onEvent(type, this);
	}

	protected boolean isYoyo(int step) {
		return isYoyo && Math.abs(step%4) == 2;
	}

	protected void forceStartValues(int step) {
		if (isYoyo(step)) forceEndValues();
		else forceStartValues();
	}

	protected void forceEndValues(int step) {
		if (isYoyo(step)) forceStartValues();
		else forceEndValues();
	}

	// -------------------------------------------------------------------------
	// Update engine
	// -------------------------------------------------------------------------

	/**
	 * Updates the tween or timeline state. <b>You may want to use a
	 * TweenManager to update objects for you.</b> Slow motion, fast motion and
	 * backwards play can be easily achieved by tweaking the delta time given
	 * as parameter.
	 *
	 * @param delta A delta time between now and the last call.
	 */
	public void update(float delta) {
		if (!isStarted || isPaused) return;

		int lastStep = step;
		currentTime += delta;

		initialize();

		if (isInitialized) {
			testRelaunch();
			updateStep();
			testInnerTransition(lastStep);
			testLimitTransition(lastStep);
			testCompletion();
			if (isIterationStep) compute(lastStep, delta);
		}
	}

	private void initialize() {
		if (!isInitialized && currentTime >= delay) {
			initializeOverride();
			isInitialized = true;
			isIterationStep = true;
			currentTime -= delay;
			callCallback(TweenCallback.BEGIN);
			callCallback(TweenCallback.START);
		}
	}

	private void testRelaunch() {
		if (repeatCnt >= 0 && step > repeatCnt*2 && currentTime <= 0) {
			assert step == repeatCnt*2 + 1;
			isIterationStep = true;
			currentTime += duration;
			step = repeatCnt*2;

		} else if (repeatCnt >= 0 && step < 0 && currentTime >= 0) {
			assert step == -1;
			isIterationStep = true;
			step = 0;
		}
	}

	private void updateStep() {
		while (isValid(step)) {
			if (!isIterationStep && currentTime <= 0) {
				isIterationStep = true;
				currentTime += duration;
				step -= 1;
				callCallback(TweenCallback.BACK_START);

			} else if (!isIterationStep && currentTime >= repeatDelay) {
				isIterationStep = true;
				currentTime -= repeatDelay;
				step += 1;
				callCallback(TweenCallback.START);

			} else if (isIterationStep && currentTime < 0) {
				isIterationStep = false;
				currentTime += isValid(step-1) ? repeatDelay : 0;
				step -= 1;
				callCallback(TweenCallback.BACK_END);

			} else if (isIterationStep && currentTime > duration) {
				isIterationStep = false;
				currentTime -= duration;
				step += 1;
				callCallback(TweenCallback.END);

			} else break;
		}
	}

	private void testInnerTransition(int lastStep) {
		if (isIterationStep) return;
		if (step > lastStep) forceEndValues(step-1);
		else if (step < lastStep) forceStartValues(step+1);
	}

	private void testLimitTransition(int lastStep) {
		if (repeatCnt < 0 || step == lastStep) return;
		if (step > repeatCnt*2) callCallback(TweenCallback.COMPLETE);
		else if (step < 0) callCallback(TweenCallback.BACK_COMPLETE);
	}

	private void testCompletion() {
		isFinished = (repeatCnt >= 0 && step > repeatCnt*2) || (repeatCnt >= 0 && step < 0);
	}

	private void compute(int lastStep, float delta) {
		assert currentTime >= 0;
		assert currentTime <= duration;
		assert isInitialized;
		assert !isFinished;
		assert isIterationStep;
		assert isValid(step);
		computeOverride(step, lastStep, delta);
	}

	private boolean isValid(int step) {
		return (step >= 0 && step <= repeatCnt*2) || repeatCnt < 0;
	}
}
