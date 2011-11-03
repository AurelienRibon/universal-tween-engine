package aurelienribon.tweenengine.tests.swing;

import aurelienribon.tweenengine.Tweenable;
import java.awt.Component;

public class TweenComponent implements Tweenable {
	public static final int POSITION = 0;

	// -------------------------------------------------------------------------

	private final Component model;

	public TweenComponent(Component model) {
		this.model = model;
	}

	@Override
	public int getTweenValues(int tweenType, float[] returnValues) {
		switch (tweenType) {
			case POSITION:
				returnValues[0] = model.getX();
				returnValues[1] = model.getY();
				return 2;
		}
		return 0;
	}

	@Override
	public void onTweenUpdated(int tweenType, float[] newValues) {
		switch (tweenType) {
			case POSITION:
				model.setLocation((int) newValues[0], (int) newValues[1]);
				break;
		}
	}
}
