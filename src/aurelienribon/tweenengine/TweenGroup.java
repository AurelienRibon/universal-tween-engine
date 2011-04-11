package aurelienribon.tweenengine;

import java.util.ArrayList;

public class TweenGroup {
	private final static Pool<TweenGroup> pool;
	private static int staticId = 0;

	static {
		pool = new Pool<TweenGroup>(5) {
			@Override
			protected TweenGroup getNew() {
				return new TweenGroup();
			}
		};
	}

	/**
	 * Defines a group of parallel tweens.
	 * @param tweens A list of tweens to group.
	 * @return The group, for chaining a delay or anything else.
	 */
	public static TweenGroup parallel(Tween... tweens) {		
		TweenGroup group = pool.get();
		group.reset(tweens);
		return group;
	}

	/**
	 * Defines a group of sequenced tweens.
	 * @param tweens A list of tweens to group.
	 * @return The group, for chaining a delay or anything else.
	 */
	public static TweenGroup sequence(Tween... tweens) {
		for (int i=1; i<tweens.length; i++) {
			Tween tween = tweens[i];
			Tween previousTween = tweens[i-1];
			tween.delay(previousTween.getDurationMillis() + previousTween.getDelayMillis());
		}

		TweenGroup group = pool.get();
		group.reset(tweens);
		return group;
	}

	/**
	 * Disposes of every static resources.
	 */
	public static void dispose() {
		pool.clear();
	}

	// -------------------------------------------------------------------------

	private final ArrayList<Tween> tweens;
	private int groupId;
	private int durationMillis;

    private TweenGroup() {
		tweens = new ArrayList<Tween>();
	}

	private void reset(Tween[] tweens) {
		this.tweens.clear();
		for (int i=0; i<tweens.length; i++)
			this.tweens.add(tweens[i]);

		this.groupId = ++staticId;
		this.durationMillis = computeDuration();
		
		for (int i=0; i<tweens.length; i++)
			tweens[i].setId(groupId);
	}

	private int computeDuration() {
		int dur = 0;
		for (int i=0; i<tweens.size(); i++) {
			Tween tween = tweens.get(i);
			dur = Math.max(dur, tween.getDelayMillis() + tween.getDurationMillis());
		}
		return dur;
	}

	/**
	 * Starts the group.
	 */
	public void start() {
		for (int i=0; i<tweens.size(); i++) {
			Tween tween = tweens.get(i);
			if (tween.getId() == groupId)
				tween.start();
		}
	}

	/**
	 * Delays every Tween in the sequence.
	 */
	public TweenGroup delay(int delayMillis) {
		for (int i=0; i<tweens.size(); i++) {
			Tween tween = tweens.get(i);
			if (tween.getId() == groupId)
				tween.delay(delayMillis);
		}
		durationMillis = computeDuration();
		return this;
	}

	/**
	 * Kills every Tween in the sequence.
	 */
	public void kill() {
		for (int i=0; i<tweens.size(); i++) {
			Tween tween = tweens.get(i);
			if (tween.getId() == groupId)
				tween.kill();
		}
	}

	/**
	 * Repeats the tween group for a given number of times. For infinity
	 * repeats,use Tween.INFINITY.
	 */
	public TweenGroup repeat(int count) {
		return repeat(count, 0);
	}

	/**
	 * Repeats the tween group for a given number of times. For infinity
	 * repeats,use Tween.INFINITY. A delay before the repeat occurs can be
	 * specified.
	 */
	public TweenGroup repeat(int count, int delayMillis) {
		for (int i=0; i<tweens.size(); i++) {
			Tween tween = tweens.get(i);
			tween.repeat(count, durationMillis - tween.getDurationMillis() - tween.getDelayMillis() + delayMillis);
		}
		return this;
	}

	/**
	 * Gets the tweens included in this sequence.
	 * <p>Warning: this instantiate an array.</p>
	 * <p>Warning: you should only call this method before the "start()" method,
	 * because tweens are pooled. That way, if a tween has ended, it can be
	 * reused for another tween, maybe in another sequence. Be sure that the
	 * tweens returned have the same id as the group id (see "getId()" on Tween
	 * and TweenGroup).</p>
	 */
	public Tween[] getTweens() {
		return tweens.toArray(new Tween[0]);
	}

	/**
	 * Gets the id assigned to this group.
	 */
	public int getId() {
		return groupId;
	}
}
