package aurelienribon.tweenengine;

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

	// -------------------------------------------------------------------------
	// -------------------------------------------------------------------------

	private Tween[] tweens;
	private int groupId;

    private TweenGroup() {
	}

	private void reset(Tween[] tweens) {
		this.tweens = tweens;
		this.groupId = ++staticId;
		for (int i=0; i<tweens.length; i++)
			tweens[i].setId(groupId);
	}

	/**
	 * Starts the group.
	 */
	public void start() {
		for (int i=0; i<tweens.length; i++)
			if (tweens[i].getId() == groupId)
				tweens[i].start();
	}

	/**
	 * Delays every Tween in the sequence.
	 */
	public TweenGroup delay(int delayMillis) {
		for (int i=0; i<tweens.length; i++)
			if (tweens[i].getId() == groupId)
				tweens[i].delay(delayMillis);
		return this;
	}

	/**
	 * Kills every Tween in the sequence.
	 */
	public void kill() {
		for (int i=0; i<tweens.length; i++)
			if (tweens[i].getId() == groupId)
				tweens[i].kill();
	}

	/**
	 * Return the tweens included in this sequence.
	 * Warning, you should only call this method before the "start()" method,
	 * because tweens are pooled. That way, if a tween has ended, it can be
	 * reused for another tween, maybe in another sequence. Be sure that the
	 * tweens returned have the same id as the group id (see "getId()" on Tween
	 * and TweenGroup).
	 */
	public Tween[] getTweens() {
		return tweens;
	}

	public int getId() {
		return groupId;
	}
}
