package aurelienribon.tweenengine.simpletweenables;

import aurelienribon.tweenengine.SimpleTweenable;

/**
 * Defines a double value on which tweens can be applied.
 *
 * @see SimpleTweenable
 * @author Aurelien Ribon (aurelien.ribon@gmail.com)
 */
public class TweenableDouble implements SimpleTweenable {
	public double value;

	@Override
	public int getTweenValues(int tweenType, float[] returnValues) {
		returnValues[0] = (float) value;
		return 1;
	}

	@Override
	public void onTweenUpdated(int tweenType, float[] newValues) {
		value = newValues[0];
	}
}
