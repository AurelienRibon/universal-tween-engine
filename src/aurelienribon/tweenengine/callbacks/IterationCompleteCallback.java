package aurelienribon.tweenengine.callbacks;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;

/**
 * Callback triggered after the completion of each repetition iteration
 * of a tween.
 * @author Aurelien Ribon (aurelien.ribon@gmail.com)
 */
public interface IterationCompleteCallback extends TweenCallback {
	public void onIterationComplete(Tween tween);
}
