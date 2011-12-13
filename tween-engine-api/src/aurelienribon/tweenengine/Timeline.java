package aurelienribon.tweenengine;

import java.util.ArrayList;
import java.util.List;

/**
 * A TweenGroup can be used to create complex animations made of sequences and
 * parallel sets of Tweens.
 *
 * <br/><br/>
 * The following example will create an animation sequence composed of 5 parts:
 * <br/>
 * 1. First, Opacity and Scale are reset to 0. <br/>
 * 2. Then, Opacity and Scale are tweened to 1. <br/>
 * 3. Then, the animation is paused for 1s. <br/>
 * 4. Then, Position is tweened to x=100. <br/>
 * 5. Then, Rotation is tweened to 360°. <br/>
 *
 * <br/>
 * <pre>
 * TweenGroup.sequence(
 *     TweenGroup.parallel(
 *         Tween.set(myObject, OPACITY).target(0),
 *         Tween.set(myObject, SCALE).target(0, 0),
 *     ),
 *     TweenGroup.parallel(
 *          Tween.to(myObject, OPACITY, 500).target(1).ease(Quad.INOUT),
 *          Tween.to(myObject, SCALE, 500).target(1, 1).ease(Quad.INOUT),
 *     ),
 *     TweenGroup.tempo(1000),
 *     Tween.to(myObject, POSITION_X, 500).target(100).ease(Quad.INOUT),
 *     Tween.to(myObject, ROTATION, 500).target(360).ease(Quad.INOUT)
 * ).addToManager(myManager);
 * </pre>
 *
 * Note that you can call sequence(), parallel() and tempo() inside
 * any other sequence() or parallel() call.
 *
 * <br/><br/>
 * <b>Alike individual tweens, add the group to a TweenManager and update the
 * latter periodically. Also, note that groups are pooled if you set
 * "Tween.setPoolEnabled(true)".
 * </b>
 *
 * @see Tween
 * @see TweenManager
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class Timeline implements TimelineObject {
	private static final Pool.Callback<Timeline> poolCallback = new Pool.Callback<Timeline>() {
		@Override public void onPool(Timeline obj) {
			obj.reset();
		}

		@Override public void onUnpool(Timeline obj) {
			obj.isPooled = Tween.isPoolEnabled();
		}
	};

	static final Pool<Timeline> pool = new Pool<Timeline>(15, poolCallback) {
		@Override protected Timeline create() {Timeline tl = new Timeline(); tl.reset(); return tl;}
	};
	
	// -------------------------------------------------------------------------
	// Factory
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
	// Impl.
	// -------------------------------------------------------------------------

	private enum Types {SEQUENCE, PARALLEL}

	private final List<TimelineObject> children = new ArrayList<TimelineObject>(10);
	private Timeline parent;
	private Types type;

	private int repeatCnt;
	private boolean isPooled;
	private boolean isYoyo;

	private int delayMillis;
	private int durationMillis;
	private int repeatDelayMillis;

	public void reset() {
		children.clear();
		parent = null;

		repeatCnt = 0;
		isYoyo = false;
		delayMillis = durationMillis = repeatDelayMillis = 0;
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

	public Timeline start(TweenManager manager) {
		if (parent != null) throw new RuntimeException("You forgot to call a few 'end()' statements...");
		validate(this);
		sequence(this);
		
		return this;
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private void validate(Timeline tl) {
		for (int i=0; i<tl.children.size(); i++) {
			TimelineObject obj = tl.children.get(i);
			if (obj instanceof Timeline) {
				Timeline child = (Timeline) obj;
				if (child.repeatCnt != 0) throw new RuntimeException("The engine does not yet support repetition of nested timelines, sorry.");
				validate(child);
			}
		}
	}

	private void sequence(Timeline tl) {
		tl.durationMillis = 0;

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
}
