package aurelienribon.tweenengine.tests.swing;

import javax.swing.JLabel;
import aurelienribon.tweenengine.Tweenable;

public class TweenJLabel extends JLabel implements Tweenable {
	public static final int POSITION = 0;

	@Override
	public int getTweenValues(int tweenType, float[] returnValues) {
		switch (tweenType) {
			case POSITION:
				returnValues[0] = getX();
				returnValues[1] = getY();
				return 2;
		}
		return 0;
	}

	@Override
	public void onTweenUpdated(int tweenType, float[] newValues) {
		switch (tweenType) {
			case POSITION:
				setLocation((int) newValues[0], (int) newValues[1]);
				break;
		}
	}
}
