package aurelienribon.tweenengine.callbacks;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;

/**
 * Callback triggered after the end of a tween delay.
 * @author Aurelien Ribon (aurelien.ribon@gmail.com)
 */
public interface DelayEndedCallback extends TweenCallback {
    public void onDelayEnded(Tween tween);
}
