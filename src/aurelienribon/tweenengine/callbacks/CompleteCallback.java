package aurelienribon.tweenengine.callbacks;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;

/**
 * Callback triggered after the full completion of a tween (including every
 * repetitions and delays).
 * @author Aurelien Ribon (aurelien.ribon@gmail.com)
 */
public interface CompleteCallback extends TweenCallback {
    public void onComplete(Tween tween);
}
