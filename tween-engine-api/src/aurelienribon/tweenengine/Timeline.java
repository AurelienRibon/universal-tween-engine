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
 * 1. First, Opacity and Scale are reset to 0. <br/>
 * 2. Then, Opacity and Scale are tweened to 1. <br/>
 * 3. Then, the animation is paused for 1s. <br/>
 * 4. Then, Position is tweened to x=100. <br/>
 * 5. Then, Rotation is tweened to 360°.
 * <br/><br/>
 *
 * This animation will be repeated 5 times, with a 500ms setDelay between each
 * iteration.
 * 
 * <pre>
 * Timeline.createSequence()
 *     .beginParallel()
 *         .push(Tween.set(myObject, OPACITY).target(0))
 *         .push(Tween.set(myObject, SCALE).target(0, 0))
 *     .end()
 *     .beginParallel()
 *          .push(Tween.to(myObject, OPACITY, 500).target(1).ease(Quad.INOUT))
 *          .push(Tween.to(myObject, SCALE, 500).target(1, 1).ease(Quad.INOUT))
 *     .end()
 *     .pushPause(1000)
 *     .push(Tween.to(myObject, POSITION_X, 500).target(100).ease(Quad.INOUT))
 *     .push(Tween.to(myObject, ROTATION, 500).target(360).ease(Quad.INOUT))
 *     .repeat(5, 500)
 *     .start(myManager);
 * </pre>
 *
 * @see Tween
 * @see TweenManager
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public final class Timeline extends TimelineObject {
	// -------------------------------------------------------------------------
	// Static -- pool
	// -------------------------------------------------------------------------

	private static final Pool.Callback<Timeline> poolCallback = new Pool.Callback<Timeline>() {
		@Override public void onPool(Timeline obj) {obj.reset();}
		@Override public void onUnpool(Timeline obj) {obj.isPooled = Tween.isPoolingEnabled();}
	};

	static final Pool<Timeline> pool = new Pool<Timeline>(15, poolCallback) {
		@Override protected Timeline create() {Timeline tl = new Timeline(); tl.reset(); return tl;}
	};

	/**
	 * Used for debug purpose. Gets the current number of objects that are
	 * waiting in the pool.
	 * @return The current size of the pool.
	 */
	public static int getPoolSize() {
		return pool.size();
	}

	/**
	 * Increases the pool capacity directly. Capacity defaults to 20.
	 * @param minCapacity The minimum capacity of the pool.
	 */
	public static void ensurePoolCapacity(int minCapacity) {
		pool.ensureCapacity(minCapacity);
	}

	// -------------------------------------------------------------------------
	// Static -- factories
	// -------------------------------------------------------------------------

	public static Timeline createSequence() {
		Timeline root = pool.get();
		root.mode = Modes.SEQUENCE;
		return root;
	}

	public static Timeline createParallel() {
		Timeline root = pool.get();
		root.mode = Modes.PARALLEL;
		return root;
	}

	// -------------------------------------------------------------------------
	// Attributes
	// -------------------------------------------------------------------------

	private enum Modes {SEQUENCE, PARALLEL}

	// Main
	private final List<TimelineObject> children = new ArrayList<TimelineObject>(10);
	private Timeline parent;
	private Modes mode;

	// -------------------------------------------------------------------------
	// Ctor
	// -------------------------------------------------------------------------

	@Override
	protected void reset() {
		super.reset();
		
		children.clear();
		parent = null;
	}

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	public Timeline beginSequence() {
		Timeline child = pool.get();
		child.parent = this;
		child.mode = Modes.SEQUENCE;
		children.add(child);
		return child;
	}

	public Timeline beginParallel() {
		Timeline child = pool.get();
		child.parent = this;
		child.mode = Modes.PARALLEL;
		children.add(child);
		return child;
	}

	public Timeline end() {
		if (parent == null) throw new RuntimeException("Nothing to end...");
		return parent;
	}

	public Timeline push(Tween tween) {
		children.add(tween);
		return this;
	}

	public Timeline push(Timeline timeline) {
		timeline.parent = this;
		children.add(timeline);
		return this;
	}

	public Timeline pushPause(int millis) {
		children.add(Tween.mark().delay(millis));
		return this;
	}

	public Timeline start() {
		if (parent != null) throw new RuntimeException("You forgot to call a few 'end()' statements...");
		sequence(this);
		return this;
	}

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
			TimelineObject obj = children.get(i);
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
			TimelineObject obj = children.get(i);
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
			TimelineObject obj = tl.children.get(i);

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
	// TimelineObject impl.
	// -------------------------------------------------------------------------

	@Override
	protected void forceStartValues(int iteration) {
		for (int i=0, n=children.size(); i<n; i++) {
			TimelineObject obj = children.get(i);
			if (isIterationYoyo(iteration)) obj.forceToEnd(durationMillis); else obj.forceToStart();
		}
	}

	@Override
	protected void forceEndValues(int iteration) {
		for (int i=0, n=children.size(); i<n; i++) {
			TimelineObject obj = children.get(i);
			if (isIterationYoyo(iteration)) obj.forceToStart(); else obj.forceToEnd(durationMillis);
		}
	}

	@Override
	protected int getChildrenCount() {
		int cnt = 0;
		for (int i=0, n=children.size(); i<n; i++) {
			TimelineObject obj = children.get(i);
			cnt += 1 + obj.getChildrenCount();
		}
		return cnt;
	}

	@Override
	protected void killTarget(Object target) {
		for (int i=0, n=children.size(); i<n; i++) {
			TimelineObject obj = children.get(i);
			obj.killTarget(target);
		}
	}

	@Override
	protected void killTarget(Object target, int tweenType) {
		for (int i=0, n=children.size(); i<n; i++) {
			TimelineObject obj = children.get(i);
			obj.killTarget(target, tweenType);
		}
	}

	@Override
	protected boolean containsTarget(Object target) {
		for (int i=0, n=children.size(); i<n; i++) {
			TimelineObject obj = children.get(i);
			if (obj.containsTarget(target)) return true;
		}
		return false;
	}

	@Override
	protected boolean containsTarget(Object target, int tweenType) {
		for (int i=0, n=children.size(); i<n; i++) {
			TimelineObject obj = children.get(i);
			if (obj.containsTarget(target, tweenType)) return true;
		}
		return false;
	}
}
