package aurelienribon.tweenengine.callbacks;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;

/**
 * Callback triggered after a call to tween.start().
 * @author Aurelien Ribon (aurelien.ribon@gmail.com)
 */
public interface StartCallback extends TweenCallback {
    public void onStart(Tween tween);
}
