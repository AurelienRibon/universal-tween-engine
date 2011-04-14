package aurelienribon.tweenengine.callbacks;

import aurelienribon.tweenengine.Tween;

/**
 * A callback triggered after the full completion of a tween (including every
 * repetitions and delays). Won't be triggered if the tween is killed by hand.
 * 
 * @author Aurelien Ribon (aurelien.ribon@gmail.com)
 */
public interface TweenCompleteCallback {
    public void tweenComplete(Tween tween);
}
