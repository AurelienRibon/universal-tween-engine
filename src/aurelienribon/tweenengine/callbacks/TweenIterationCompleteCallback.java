package aurelienribon.tweenengine.callbacks;

import aurelienribon.tweenengine.Tween;

/**
 * A callback triggered after the completion of each repetition iteration
 * of a tween. If the tween won't repeat, it is equivalent as a
 * TweenCompleteCallback. Won't be triggered if the tween is killed by hand.
 * 
 * @author Aurelien Ribon (aurelien.ribon@gmail.com)
 */
public interface TweenIterationCompleteCallback {
    public void iterationComplete(Tween tween);
}
