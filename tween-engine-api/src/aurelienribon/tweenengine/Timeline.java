package aurelienribon.tweenengine;

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
 * This animation will be repeated 5 times, with a 500ms delay between each
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
public class Timeline extends TimelineObject {
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
		root.type = Types.SEQUENCE;
		return root;
	}

	public static Timeline createParallel() {
		Timeline root = pool.get();
		root.type = Types.PARALLEL;
		return root;
	}

	// -------------------------------------------------------------------------
	// Attributes
	// -------------------------------------------------------------------------

	private enum Types {SEQUENCE, PARALLEL}

	// Main
	private final List<TimelineObject> children = new ArrayList<TimelineObject>(10);
	private Timeline parent;
	private Types type;

	// General
	private boolean isPooled;
	private boolean isYoyo;
	private int repeatCnt;

	// Timings
	private int delayMillis;
	private int durationMillis;
	private int repeatDelayMillis;
	private int currentMillis;
	private boolean isFinished;

	// -------------------------------------------------------------------------
	// Ctor
	// -------------------------------------------------------------------------

	private void reset() {
		children.clear();
		parent = null;

		repeatCnt = 0;
		isYoyo = false;
		delayMillis = durationMillis = repeatDelayMillis = currentMillis = 0;
	}

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	public Timeline beginSequence() {
		Timeline child = pool.get();
		child.parent = this;
		child.type = Types.SEQUENCE;
		return child;
	}

	public Timeline beginParallel() {
		Timeline child = pool.get();
		child.parent = this;
		child.type = Types.PARALLEL;
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

	public Timeline repeat(int count, int delayMillis) {
		this.repeatCnt = count;
		this.repeatDelayMillis = delayMillis;
		this.isYoyo = false;
		return this;
	}

	public Timeline repeatYoyo(int count, int delayMillis) {
		this.repeatCnt = count;
		this.repeatDelayMillis = delayMillis;
		this.isYoyo = true;
		return this;
	}

	public Timeline start() {
		if (parent != null) throw new RuntimeException("You forgot to call a few 'end()' statements...");
		initialize(this);
		sequence(this);
		return this;
	}

	public Timeline start(TweenManager manager) {
		manager.add(this);
		return this;
	}

	@Override
	public void kill() {
		isFinished = true;
	}

	/**
	 * If you want to manually manage your timelines (without using a
	 * TweenManager), and you enabled object pooling, then you need to call
	 * this method on your timelines once they are finished (see <i>isFinished()
	 * </i> method).
	 */
	@Override
	public void free() {
		for (int i=0, n=children.size(); i<n; i++) {
			TimelineObject obj = children.get(i);
			obj.free();
		}
		if (isPooled) pool.free(this);
	}

	// -------------------------------------------------------------------------
	// Getters
	// -------------------------------------------------------------------------

	public int getDuration() {
		return durationMillis;
	}

	public int getRepeatCount() {
		return repeatCnt;
	}

	public int getRepeatDelay() {
		return repeatDelayMillis;
	}

	/**
	 * Returns true if the timeline is finished (i.e. if it has reached
	 * its end or has been killed). If you don't use a TweenManager, and enabled
	 * object pooling, then don't forget to call <i>Timeline.free()</i> on your
	 * timelines once <i>isFinished()</i> returns true.
	 */
	@Override
	public boolean isFinished() {
		return isFinished;
	}

	// -------------------------------------------------------------------------
	// Update engine
	// -------------------------------------------------------------------------

	/**
	 * Updates the timeline state. <b>You may want to use a TweenManager to
	 * update timelines for you.</b> Slow motion, fast motion and backwards play
	 * can be easily achieved by tweaking the deltaMillis given as parameter.
	 * @param deltaMillis A delta time, in milliseconds, between now and the
	 * last call.
	 */
	@Override
	public void update(int deltaMillis) {
		currentMillis += deltaMillis;

		for (int i=0; i<children.size(); i++) {
			TimelineObject obj = children.get(i);

			if (obj instanceof Tween) {
				Tween child = (Tween) obj;

			} else if (obj instanceof Timeline) {
				Timeline child = (Timeline) obj;
				
			}
		}
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private void initialize(Timeline tl) {
		tl.durationMillis = 0;
		// TODO : ...
	}

	private void sequence(Timeline tl) {
		for (int i=0; i<tl.children.size(); i++) {
			TimelineObject obj = tl.children.get(i);

			if (obj instanceof Tween) {
				Tween child = (Tween) obj;
				if (tl.type == Types.SEQUENCE) child.delay(tl.durationMillis);
				tl.durationMillis = Math.max(tl.durationMillis, getTweenLength(child));

			} else if (obj instanceof Timeline) {
				Timeline child = (Timeline) obj;
				if (tl.type == Types.SEQUENCE) child.delayMillis = tl.durationMillis;
				sequence(child);
				tl.durationMillis = Math.max(tl.durationMillis, getTimelineLength(child));
			}
		}
	}

	private int getTweenLength(Tween t) {
		return t.getDelay() + t.getDuration() + (t.getRepeatDelay() + t.getDuration()) * t.getRepeatCount();
	}

	private int getTimelineLength(Timeline tl) {
		return tl.delayMillis + tl.durationMillis + (tl.repeatDelayMillis + tl.durationMillis) * tl.repeatCnt;
	}

	// -------------------------------------------------------------------------
	// TimelineObject impl.
	// -------------------------------------------------------------------------

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
