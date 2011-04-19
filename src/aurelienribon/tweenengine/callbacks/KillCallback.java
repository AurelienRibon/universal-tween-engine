package aurelienribon.tweenengine.callbacks;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;

/**
 * Callback triggered when a tween is killed.
 * @author Aurelien Ribon (aurelien.ribon@gmail.com)
 */
public interface KillCallback extends TweenCallback {
    public void onKill(Tween tween);
}
