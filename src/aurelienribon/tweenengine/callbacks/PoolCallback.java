package aurelienribon.tweenengine.callbacks;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;

/**
 * Callback triggered when a tween is freed and returned to the pool.
 * @author Aurelien Ribon (aurelien.ribon@gmail.com)
 */
public interface PoolCallback extends TweenCallback {
    public void onPool(Tween tween);
}
