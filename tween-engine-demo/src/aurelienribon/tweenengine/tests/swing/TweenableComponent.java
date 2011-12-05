package aurelienribon.tweenengine.tests.swing;

import aurelienribon.tweenengine.Tweenable;
import java.awt.Component;

public class TweenableComponent implements Tweenable<Component> {
	public static final int POSITION = 0;

	@Override
	public int getTweenValues(Component target, int tweenType, float[] returnValues) {
		switch (tweenType) {
			case POSITION:
				returnValues[0] = target.getX();
				returnValues[1] = target.getY();
				return 2;
		}
		return 0;
	}

	@Override
	public void onTweenUpdated(Component target, int tweenType, float[] newValues) {
		switch (tweenType) {
			case POSITION:
				target.setLocation((int) newValues[0], (int) newValues[1]);
				break;
		}
	}
}
