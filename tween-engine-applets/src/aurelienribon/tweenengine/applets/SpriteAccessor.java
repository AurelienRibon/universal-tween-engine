package aurelienribon.tweenengine.applets;

import aurelienribon.tweenengine.TweenAccessor;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class SpriteAccessor implements TweenAccessor<Sprite> {
	public static final int POSITION_XY = 1;

	@Override
	public int getValues(Sprite target, int tweenType, float[] returnValues) {
		switch (tweenType) {
			case POSITION_XY:
				returnValues[0] = target.getX();
				returnValues[1] = target.getY();
				return 2;

			default: assert false; return -1;
		}
	}

	@Override
	public void setValues(Sprite target, int tweenType, float[] newValues) {
		switch (tweenType) {
			case POSITION_XY:
				target.setX(newValues[0]);
				target.setY(newValues[1]);
				break;

			default: assert false;
		}
	}
}
