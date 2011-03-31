package aurelienribon.libgdx.tween;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class TweenSequence {
	private final static Pool<TweenSequence> sequencePool = new Pool<TweenSequence>(10) {
		@Override protected TweenSequence newObject() {	return new TweenSequence();	}
	};

	/**
	 * Starts a new sequence of tweens. Just insert "Tween.to()" and
	 * "Tween.from()" inside the method.
	 * @param tweens A list of interpolations to sequence.
	 * @return The sequence, for chaining a delay or anything else.
	 */
	public static TweenSequence start(Tween... tweens) {
		for (int i=1; i<tweens.length; i++) {
			Tween tween = tweens[i];
			Tween previousTween = tweens[i-1];
			tween.delay(previousTween.durationMillis + previousTween.delayMillis);
		}

		TweenSequence newSequence = sequencePool.obtain();
		newSequence.reset(tweens);
		return newSequence;
	}

	// -------------------------------------------------------------------------
	// -------------------------------------------------------------------------

	protected Array<Tween> tweens = new Array<Tween>(true, 10);

    private TweenSequence() {
	}

	private void reset(Tween[] tweens) {
		this.tweens.clear();
		this.tweens.addAll(tweens);
	}

	/**
	 * Delays every Tween in the sequence.
	 */
	public TweenSequence delay(int delayMillis) {
		for (int i=0; i<tweens.size; i++)
			tweens.get(i).delay(delayMillis);
		return this;
	}

	/**
	 * Kills every Tween in the sequence.
	 */
	public void kill() {
		for (int i=0; i<tweens.size; i++)
			tweens.get(i).kill();
	}
}
