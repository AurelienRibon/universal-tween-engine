package aurelienribon.tweenengine;

import aurelienribon.tweenengine.TweenCallback.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * BaseTween is the base class of Tween and Timeline. It defines the
 * iteration engine used to play animations for any number of times, and in
 * any direction, at any speed.
 * <br/><br/>
 *
 * It is responsible for calling the different callbacks at the right moments,
 * and for making sure that every callbacks are triggered, even if the update
 * engine gets a big delta time at once.
 *
 * @see Tween
 * @see Timeline
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public abstract class BaseTween {

	// -------------------------------------------------------------------------
	// Abstract stuff
	// -------------------------------------------------------------------------

	/**
	 * If you want to manually manage your tweens and timelines (without using a
	 * TweenManager), and you enabled object pooling, then you need to call
	 * this method on your tweens and timelines once they are finished (see
	 * <i>isFinished()</i> method).
	 */
	public abstract void free();

	protected abstract void initializeOverride();
	protected abstract void computeOverride(int iteration, int lastIteration, int deltaMillis);
	protected abstract void forceStartValues(int iteration);
	protected abstract void forceEndValues(int iteration);

	protected abstract int getChildrenCount();
	protected abstract void killTarget(Object target);
	protected abstract void killTarget(Object target, int tweenType);
	protected abstract boolean containsTarget(Object target);
	protected abstract boolean containsTarget(Object target, int tweenType);

	// -------------------------------------------------------------------------
	// Attributes
	// -------------------------------------------------------------------------
	
	// General
	protected boolean isPooled;
	private boolean isYoyo;
	private boolean isComputeIteration;
	private int iteration;
	private int repeatCnt;

	// Timings
	protected int delayMillis;
	protected int durationMillis;
	protected int repeatDelayMillis;
	protected int currentMillis;
	protected boolean isStarted; // true when the object is started
	protected boolean isInitialized; // true after the delay
	protected boolean isFinished; // true when all repetitions are done or the object has been killed

	// Callbacks
	private List<TweenCallback> beginCallbacks;
	private List<TweenCallback> startCallbacks;
	private List<TweenCallback> endCallbacks;
	private List<TweenCallback> completeCallbacks;
	private List<TweenCallback> backStartCallbacks;
	private List<TweenCallback> backEndCallbacks;
	private List<TweenCallback> backCompleteCallbacks;

	// Misc
	private Object userData;

	// -------------------------------------------------------------------------

	protected void reset() {
		isPooled = Tween.isPoolingEnabled();

		isYoyo = isComputeIteration = false;
		iteration = repeatCnt = 0;
		
		delayMillis = durationMillis = repeatDelayMillis = currentMillis = 0;
		isStarted = isInitialized = isFinished = false;

		if (beginCallbacks != null) beginCallbacks.clear();
		if (startCallbacks != null) startCallbacks.clear();
		if (endCallbacks != null) endCallbacks.clear();
		if (completeCallbacks != null) completeCallbacks.clear();
		if (backStartCallbacks != null) backStartCallbacks.clear();
		if (backEndCallbacks != null) backEndCallbacks.clear();
		if (backCompleteCallbacks != null) backCompleteCallbacks.clear();

		userData = null;
	}

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	/**
	 * Kills the tween or timeline. If you're using a TweenManager, this object
	 * will be removed automatically.
	 */
	public void kill() {
		isFinished = true;
	}

	/**
	 * Repeats the tween or timeline for a given number of times.
	 * @param count The number of repetitions. For infinite repetition,
	 * use Tween.INFINITY, or a negative number.
	 * @param millis A delay between each iteration.
	 * @return The current tween or timeline, for chaining instructions.
	 */
	public BaseTween repeat(int count, int delayMillis) {
		if (isStarted) throw new RuntimeException("You can't change the repetitions of a tween or timeline once it is started");
		repeatCnt = count;
		repeatDelayMillis = delayMillis >= 0 ? delayMillis : 0;
		isYoyo = false;
		return this;
	}

	/**
	 * Repeats the tween or timeline for a given number of times. 
	 * Every two iterations, it will be played backwards.
	 * @param count The number of repetitions. For infinite repetition,
	 * use Tween.INFINITY, or a negative number.
	 * @param millis A delay before each repetition.
	 * @return The current tween or timeline, for chaining instructions.
	 */
	public BaseTween repeatYoyo(int count, int delayMillis) {
		if (isStarted) throw new RuntimeException("You can't change the repetitions of a tween or timeline once it is started");
		repeatCnt = count;
		repeatDelayMillis = delayMillis >= 0 ? delayMillis : 0;
		isYoyo = true;
		return this;
	}

	/**
	 * Adds a callback to the tween or timeline. The moment when the callback is
	 * triggered depends on its type:
	 * <br/><br/>
	 *
	 * <b>BEGIN</b>: at first START, right after the delay (if any)<br/>
	 * <b>START</b>: at each iteration beginning<br/>
	 * <b>END</b>: at each iteration ending, before the repeat delay<br/>
	 * <b>COMPLETE</b>: at last END<br/>
	 * <b>BACK_START</b>: at each backwards iteration beginning, after the repeat delay<br/>
	 * <b>BACK_END</b>: at each backwards iteration ending<br/>
	 * <b>BACK_COMPLETE</b>: at last BACK_END
	 * <br/><br/>
	 *
	 * <pre> {@code
	 * forwards :         BEGIN                                   COMPLETE
	 * forwards :         START    END      START    END      START    END
	 * |------------------[XXXXXXXXXX]------[XXXXXXXXXX]------[XXXXXXXXXX]
	 * backwards:         bEND  bSTART      bEND  bSTART      bEND  bSTART
	 * backwards:         bCOMPLETE
	 * }</pre>
	 *
	 *
	 * @param callbackType The callback type.
	 * @param callback A callback.
	 * @return The current tween or timeline, for chaining instructions.
	 */
	public BaseTween addCallback(Types callbackType, TweenCallback callback) {
		List<TweenCallback> callbacks = null;

		switch (callbackType) {
			case BEGIN: callbacks = beginCallbacks; break;
			case START: callbacks = startCallbacks; break;
			case END: callbacks = endCallbacks; break;
			case COMPLETE: callbacks = completeCallbacks; break;
			case BACK_START: callbacks = backStartCallbacks; break;
			case BACK_END: callbacks = backEndCallbacks; break;
			case BACK_COMPLETE: callbacks = backCompleteCallbacks; break;
		}

		if (callbacks == null) callbacks = new ArrayList<TweenCallback>(1);
		callbacks.add(callback);

		switch (callbackType) {
			case BEGIN: beginCallbacks = callbacks; break;
			case START: startCallbacks = callbacks; break;
			case END: endCallbacks = callbacks; break;
			case COMPLETE: completeCallbacks = callbacks; break;
			case BACK_START: backStartCallbacks = callbacks; break;
			case BACK_END: backEndCallbacks = callbacks; break;
			case BACK_COMPLETE: backCompleteCallbacks = callbacks; break;
		}

		return this;
	}

	/**
	 * Attaches an object to this tween or timeline. It can be useful in order
	 * to retrieve some data from a TweenCallback.
	 * @param data Any kind of object.
	 * @return The current tween or timeline, for chaining instructions.
	 */
	public BaseTween setUserData(Object data) {
		userData = data;
		return this;
	}

	// -------------------------------------------------------------------------
	// Getters
	// -------------------------------------------------------------------------

	/**
	 * Gets the delay of the tween or timeline. Nothing won't happen before this
	 * delay.
	 */
	public int getDelay() {
		return delayMillis;
	}

	/**
	 * Gets the duration of a single iteration.
	 */
	public int getDuration() {
		return durationMillis;
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
	public int getRepeatDelay() {
		return repeatDelayMillis;
	}

	/**
	 * Returns the complete duration of a tween or timeline, including its
	 * delay and its repetitions. The formula is as follows:
	 * <br/><br/>
	 *
	 * fullDuration = delay + duration + (repeatDelay + duration) * repeatCnt
	 */
	public int getFullDuration() {
		return delayMillis + durationMillis + (repeatDelayMillis + durationMillis) * repeatCnt;
	}

	/**
	 * Gets the attached data, or null if none.
	 */
	public Object getUserData() {
		return userData;
	}

	/**
	 * Returns true if the tween or timeline has been started.
	 */
	public boolean isStarted() {
		return isStarted;
	}

	/**
	 * Returns true if the tween is finished (i.e. if the tween has reached
	 * its end or has been killed). If you don't use a TweenManager, and enabled
	 * object pooling, then don't forget to call <i>Tween.free()</i> on your
	 * tweens once <i>isFinished()</i> returns true.
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
	 * Returns true if the tween or timeline is pooled. If true, and if you
	 * don't use a TweenManager, you need to call <i>free()</i> on your objects
	 * once they are finished.
	 */
	public boolean isPooled() {
		return isPooled;
	}

	// -------------------------------------------------------------------------
	// Protected API
	// -------------------------------------------------------------------------

	protected boolean isIterationYoyo(int iteration) {
		return isYoyo && Math.abs(iteration%4) == 2;
	}

	protected void forceToStart() {
		currentMillis = -delayMillis;
		iteration = -1;
		isComputeIteration = false;
		forceStartValues(0);
	}

	protected void forceToEnd(int millis) {
		currentMillis = millis - getFullDuration();
		iteration = repeatCnt*2 + 1;
		isComputeIteration = false;
		forceEndValues(repeatCnt*2);
	}

	protected void callCallbacks(Types type) {
		List<TweenCallback> callbacks = null;

		switch (type) {
			case BEGIN: callbacks = beginCallbacks; break;
			case START: callbacks = startCallbacks; break;
			case END: callbacks = endCallbacks; break;
			case COMPLETE: callbacks = completeCallbacks; break;
			case BACK_START: callbacks = backStartCallbacks; break;
			case BACK_END: callbacks = backEndCallbacks; break;
			case BACK_COMPLETE: callbacks = backCompleteCallbacks; break;
		}

		if (callbacks != null && !callbacks.isEmpty())
			for (int i=0, n=callbacks.size(); i<n; i++)
				callbacks.get(i).tweenEventOccured(type, null);
	}

	// -------------------------------------------------------------------------
	// Update engine
	// -------------------------------------------------------------------------

	/**
	 * Updates the tween or timeline state. <b>You may want to use a 
	 * TweenManager to update objects for you.</b> Slow motion, fast motion and
	 * backwards play can be easily achieved by tweaking the deltaMillis given
	 * as parameter.
	 * @param deltaMillis A delta time, in milliseconds, between now and the
	 * last call.
	 */
	public void update(int deltaMillis) {
		if (!isStarted) return;

		int lastIteration = iteration;
		currentMillis += deltaMillis;

		initialize();

		if (isInitialized) {
			testRelaunch();
			updateIteration();
			testInnerTransition(lastIteration);
			testLimitTransition(lastIteration);
			testCompletion();
			if (isComputeIteration) compute(lastIteration, deltaMillis);
		}
	}

	private void initialize() {
		if (!isInitialized && currentMillis >= delayMillis) {
			initializeOverride();
			isInitialized = true;
			isComputeIteration = true;
			currentMillis -= delayMillis;
			callCallbacks(Types.BEGIN);
			callCallbacks(Types.START);
		}
	}

	private void testRelaunch() {
		if (repeatCnt >= 0 && iteration > repeatCnt*2 && currentMillis <= 0) {
			assert iteration == repeatCnt*2 + 1;
			isComputeIteration = true;
			currentMillis += durationMillis;
			iteration = repeatCnt*2;

		} else if (repeatCnt >= 0 && iteration < 0 && currentMillis >= 0) {
			assert iteration == -1;
			isComputeIteration = true;
			iteration = 0;
		}
	}

	private void updateIteration() {
		while (isValid(iteration)) {
			if (!isComputeIteration && currentMillis <= 0) {
				isComputeIteration = true;
				currentMillis += durationMillis;
				iteration -= 1;
				callCallbacks(Types.BACK_START);

			} else if (!isComputeIteration && currentMillis >= repeatDelayMillis) {
				isComputeIteration = true;
				currentMillis -= repeatDelayMillis;
				iteration += 1;
				callCallbacks(Types.START);

			} else if (isComputeIteration && currentMillis < 0) {
				isComputeIteration = false;
				currentMillis += isValid(iteration-1) ? repeatDelayMillis : 0;
				iteration -= 1;
				callCallbacks(Types.BACK_END);

			} else if (isComputeIteration && currentMillis > durationMillis) {
				isComputeIteration = false;
				currentMillis -= durationMillis;
				iteration += 1;
				callCallbacks(Types.END);

			} else break;
		}
	}

	private void testInnerTransition(int lastIteration) {
		if (isComputeIteration) return;
		if (iteration > lastIteration) forceEndValues(iteration-1);
		else if (iteration < lastIteration) forceStartValues(iteration+1);
	}

	private void testLimitTransition(int lastIteration) {
		if (repeatCnt < 0 || iteration == lastIteration) return;
		if (iteration > repeatCnt*2) callCallbacks(Types.COMPLETE);
		else if (iteration < 0) callCallbacks(Types.BACK_COMPLETE);
	}

	private void testCompletion() {
		isFinished = (repeatCnt >= 0 && iteration > repeatCnt*2) || (repeatCnt >= 0 && iteration < 0);
	}

	private void compute(int lastIteration, int deltaMillis) {
		assert currentMillis >= 0;
		assert currentMillis <= durationMillis;
		assert isInitialized;
		assert !isFinished;
		assert isComputeIteration;
		assert isValid(iteration);
		computeOverride(iteration, lastIteration, deltaMillis);
	}

	private boolean isValid(int iteration) {
		return (iteration >= 0 && iteration <= repeatCnt*2) || repeatCnt < 0;
	}
}
