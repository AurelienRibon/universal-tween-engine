package aurelienribon.tweenengine;

import aurelienribon.tweenengine.TweenCallback.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * A Timeline can be used to create complex animations made of sequences and
 * parallel sets of Tweens.
 * <br/><br/>
 *
 * The following example will create an animation sequence composed of 5 parts:
 * <br/><br/>
 *
 * 1. First, opacity and scale are set to 0 (with Tween.set() calls).<br/>
 * 2. Then, opacity and scale are animated in parallel.<br/>
 * 3. Then, the animation is paused for 1s.<br/>
 * 4. Then, position is animated to x=100.<br/>
 * 5. Then, rotation is animated to 360°.
 * <br/><br/>
 *
 * This animation will be repeated 5 times, with a 500ms delay between each
 * iteration:
 * <br/><br/>
 * 
 * <pre>{@code
 * Timeline.createSequence()
 *     .push(Tween.set(myObject, OPACITY).target(0))
 *     .push(Tween.set(myObject, SCALE).target(0, 0))
 *     .beginParallel()
 *          .push(Tween.to(myObject, OPACITY, 500).target(1).ease(Quad.INOUT))
 *          .push(Tween.to(myObject, SCALE, 500).target(1, 1).ease(Quad.INOUT))
 *     .end()
 *     .pushPause(1000)
 *     .push(Tween.to(myObject, POSITION_X, 500).target(100).ease(Quad.INOUT))
 *     .push(Tween.to(myObject, ROTATION, 500).target(360).ease(Quad.INOUT))
 *     .repeat(5, 500)
 *     .start(myManager);
 * }</pre>
 *
 * @see Tween
 * @see TweenManager
 * @see TweenCallback
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public final class Timeline extends BaseTween {
	// -------------------------------------------------------------------------
	// Static -- pool
	// -------------------------------------------------------------------------

	private static final Pool.Callback<Timeline> poolCallback = new Pool.Callback<Timeline>() {
		@Override public void onPool(Timeline obj) {obj.reset();}
		@Override public void onUnpool(Timeline obj) {obj.isPooled = Tween.isPoolingEnabled();}
	};

	static final Pool<Timeline> pool = new Pool<Timeline>(15, poolCallback) {
		@Override protected Timeline create() {return new Timeline();}
	};

	/**
	 * Used for debug purpose. Gets the current number of empty timelines that
	 * are waiting in the pool.
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
	// Static -- factories
	// -------------------------------------------------------------------------

	/**
	 * Creates a new timeline with a 'sequence' behavior. Its children will
	 * be delayed so that they are triggered one after the other.
	 */
	public static Timeline createSequence() {
		Timeline tl = pool.get();
		tl.setup(Modes.SEQUENCE);
		return tl;
	}

	/**
	 * Creates a new timeline with a 'parallel' behavior. Its children will be
	 * triggered all at once.
	 */
	public static Timeline createParallel() {
		Timeline tl = pool.get();
		tl.setup(Modes.PARALLEL);
		return tl;
	}

	// -------------------------------------------------------------------------
	// Attributes
	// -------------------------------------------------------------------------

	private enum Modes {SEQUENCE, PARALLEL}

	private final List<BaseTween> children = new ArrayList<BaseTween>(10);
	private Timeline current;
	private Timeline parent;
	private Modes mode;

	// -------------------------------------------------------------------------
	// Setup
	// -------------------------------------------------------------------------

	private Timeline() {
		reset();
	}

	@Override
	protected void reset() {
		super.reset();
		
		children.clear();
		current = parent = null;
	}

	private void setup(Modes mode) {
		this.mode = mode;
		this.current = this;
	}

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	/**
	 * Adds a Tween to the current timeline.
	 * @return The current timeline, for chaining instructions.
	 */
	public Timeline push(Tween tween) {
		if (isStarted) throw new RuntimeException("You can't push anything to a timeline once it is started");
		current.children.add(tween);
		return this;
	}

	/**
	 * Nests a Timeline in the current one.
	 * @return The current timeline, for chaining instructions.
	 */
	public Timeline push(Timeline timeline) {
		if (isStarted) throw new RuntimeException("You can't push anything to a timeline once it is started");
		if (timeline.current != timeline) throw new RuntimeException("You forgot to call a few 'end()' statements in your pushed timeline");
		timeline.parent = current;
		current.children.add(timeline);
		return this;
	}

	/**
	 * Adds a pause to the timeline. The millis may be negative if you want to
	 * overlap the preceding and following children.
	 * @return The current timeline, for chaining instructions.
	 */
	public Timeline pushPause(int millis) {
		if (isStarted) throw new RuntimeException("You can't push anything to a timeline once it is started");
		current.children.add(Tween.mark().delay(millis));
		return this;
	}

	/**
	 * Starts a nested timeline with a 'sequence' behavior. Don't forget to call
	 * <i>end()</i> to close this nested timeline.
	 * @return The current timeline, for chaining instructions.
	 */
	public Timeline beginSequence() {
		if (isStarted) throw new RuntimeException("You can't push anything to a timeline once it is started");
		Timeline tl = pool.get();
		tl.parent = current;
		tl.mode = Modes.SEQUENCE;
		current.children.add(tl);
		current = tl;
		return this;
	}

	/**
	 * Starts a nested timeline with a 'parallel' behavior. Don't forget to call
	 * <i>end()</i> to close this nested timeline.
	 * @return The current timeline, for chaining instructions.
	 */
	public Timeline beginParallel() {
		if (isStarted) throw new RuntimeException("You can't push anything to a timeline once it is started");
		Timeline tl = pool.get();
		tl.parent = current;
		tl.mode = Modes.PARALLEL;
		current.children.add(tl);
		current = tl;
		return this;
	}

	/**
	 * Closes the last nested timeline.
	 * @return The current timeline, for chaining instructions.
	 */
	public Timeline end() {
		if (isStarted) throw new RuntimeException("You can't push anything to a timeline once it is started");
		if (current == this) throw new RuntimeException("Nothing to end...");
		current = current.parent;
		return this;
	}

	/**
	 * Starts the timeline unmanaged. You will need to take care of its
	 * life-cycle. If you want the timeline to be managed for you, use a
	 * TweenManager.
	 * @return The current timeline, for chaining instructions.
	 */
	public Timeline start() {
		if (current != this) throw new RuntimeException("You forgot to call a few 'end()' statements before calling start()");
		sequence(this);
		return this;
	}

	/**
	 * Start the timeline managed. Its life-cycle will be handled for you. Relax
	 * and enjoy the animation.
	 * @return The current timeline, for chaining instructions.
	 */
	public Timeline start(TweenManager manager) {
		manager.add(this);
		return this;
	}

	@Override
	public Timeline repeat(int count, int delayMillis) {
		super.repeat(count, delayMillis);
		return this;
	}

	@Override
	public Timeline repeatYoyo(int count, int delayMillis) {
		super.repeatYoyo(count, delayMillis);
		return this;
	}

	@Override
	public Timeline addCallback(Types callbackType, TweenCallback callback) {
		super.addCallback(callbackType, callback);
		return this;
	}

	@Override
	public Timeline setUserData(Object data) {
		super.setUserData(data);
		return this;
	}

	@Override
	public void free() {
		for (int i=0, n=children.size(); i<n; i++) {
			BaseTween obj = children.get(i);
			obj.free();
		}
		if (isPooled) pool.free(this);
	}

	// -------------------------------------------------------------------------
	// Update engine
	// -------------------------------------------------------------------------

	@Override
	protected void initializeOverride() {
	}

	@Override
	protected void computeOverride(int iteration, int lastIteration, int deltaMillis) {
		int millis = 0;

		if (iteration > lastIteration) {
			forceStartValues(iteration);
			millis = isIterationYoyo(iteration) ? -currentMillis : currentMillis;

		} else if (iteration < lastIteration) {
			forceEndValues(iteration);
			millis = isIterationYoyo(iteration) ? durationMillis-currentMillis : currentMillis-durationMillis;

		} else {
			millis = isIterationYoyo(iteration) ? -deltaMillis : deltaMillis;
		}

		for (int i=0; i<children.size(); i++) {
			BaseTween obj = children.get(i);
			obj.update(millis);
		}
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private void sequence(Timeline tl) {
		tl.delayMillis = 0;
		tl.durationMillis = 0;
		tl.currentMillis = 0;
		tl.isStarted = true;

		for (int i=0; i<tl.children.size(); i++) {
			BaseTween obj = tl.children.get(i);

			if (obj instanceof Timeline) sequence((Timeline) obj);

			switch (tl.mode) {
				case SEQUENCE:
					int delay = tl.durationMillis;
					tl.durationMillis += obj.getFullDuration();
					obj.delayMillis += delay;
					break;

				case PARALLEL:
					tl.durationMillis = Math.max(tl.durationMillis, obj.getFullDuration());
					break;
			}

			if (obj instanceof Tween) ((Tween) obj).start();
		}
	}

	// -------------------------------------------------------------------------
	// BaseTween impl.
	// -------------------------------------------------------------------------

	@Override
	protected void forceStartValues(int iteration) {
		for (int i=0, n=children.size(); i<n; i++) {
			BaseTween obj = children.get(i);
			if (isIterationYoyo(iteration)) obj.forceToEnd(durationMillis); else obj.forceToStart();
		}
	}

	@Override
	protected void forceEndValues(int iteration) {
		for (int i=0, n=children.size(); i<n; i++) {
			BaseTween obj = children.get(i);
			if (isIterationYoyo(iteration)) obj.forceToStart(); else obj.forceToEnd(durationMillis);
		}
	}

	@Override
	protected int getChildrenCount() {
		int cnt = 0;
		for (int i=0, n=children.size(); i<n; i++) {
			BaseTween obj = children.get(i);
			cnt += 1 + obj.getChildrenCount();
		}
		return cnt;
	}

	@Override
	protected void killTarget(Object target) {
		for (int i=0, n=children.size(); i<n; i++) {
			BaseTween obj = children.get(i);
			obj.killTarget(target);
		}
	}

	@Override
	protected void killTarget(Object target, int tweenType) {
		for (int i=0, n=children.size(); i<n; i++) {
			BaseTween obj = children.get(i);
			obj.killTarget(target, tweenType);
		}
	}

	@Override
	protected boolean containsTarget(Object target) {
		for (int i=0, n=children.size(); i<n; i++) {
			BaseTween obj = children.get(i);
			if (obj.containsTarget(target)) return true;
		}
		return false;
	}

	@Override
	protected boolean containsTarget(Object target, int tweenType) {
		for (int i=0, n=children.size(); i<n; i++) {
			BaseTween obj = children.get(i);
			if (obj.containsTarget(target, tweenType)) return true;
		}
		return false;
	}
}
