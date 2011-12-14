package aurelienribon.tweenaccessors.swing;

import aurelienribon.tweenengine.TweenAccessor;
import java.awt.Component;

public class ComponentAccessor implements TweenAccessor<Component> {
	public static final int POSITION = 0;

	@Override
	public int getValues(Component target, int tweenType, float[] returnValues) {
		switch (tweenType) {
			case POSITION:
				returnValues[0] = target.getX();
				returnValues[1] = target.getY();
				return 2;
		}
		return 0;
	}

	@Override
	public void setValues(Component target, int tweenType, float[] newValues) {
		switch (tweenType) {
			case POSITION: target.setLocation((int) newValues[0], (int) newValues[1]); break;
		}
	}
}
