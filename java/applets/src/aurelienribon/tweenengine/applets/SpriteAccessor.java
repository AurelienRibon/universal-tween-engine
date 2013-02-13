package aurelienribon.tweenengine.applets;

import aurelienribon.tweenengine.TweenAccessor;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class SpriteAccessor implements TweenAccessor<Sprite> {
	public static final int POSITION_XY = 1;
	public static final int SCALE_XY = 2;
	public static final int VISIBILITY = 3;

	@Override
	public int getValues(Sprite target, int tweenType, float[] returnValues) {
		switch (tweenType) {
			case POSITION_XY:
				returnValues[0] = target.getX();
				returnValues[1] = target.getY();
				return 2;

			case SCALE_XY:
				returnValues[0] = target.getScaleX();
				returnValues[1] = target.getScaleY();
				return 2;

			case VISIBILITY:
				returnValues[0] = target.isVisible() ? 1 : 0;
				return 1;

			default: assert false; return -1;
		}
	}

	@Override
	public void setValues(Sprite target, int tweenType, float[] newValues) {
		switch (tweenType) {
			case POSITION_XY: target.setPosition(newValues[0], newValues[1]); break;
			case SCALE_XY: target.setScale(newValues[0], newValues[1]); break;
			case VISIBILITY: target.setVisible(newValues[0] > 0); break;
			default: assert false;
		}
	}
}
