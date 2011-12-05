package aurelienribon.tweenengine.tests.libgdx;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class SpriteTweenAccessor implements TweenAccessor<Sprite> {
	public static final int POSITION_XY = 1;
	public static final int SCALE_XY = 2;
	public static final int ROTATION = 3;
	public static final int OPACITY = 4;

	@Override
	public int getValues(Sprite target, int tweenType, float[] returnValues) {
		switch (tweenType) {
			case POSITION_XY:
				returnValues[0] = target.getX() + target.getOriginX();
				returnValues[1] = target.getY() + target.getOriginY();
				return 2;

			case SCALE_XY:
				returnValues[0] = target.getScaleX();
				returnValues[1] = target.getScaleY();
				return 2;

			case ROTATION:
				returnValues[0] = target.getRotation();
				return 1;

			case OPACITY:
				returnValues[0] = target.getColor().a;
				return 1;

			default: assert false; return -1;
		}
	}

	@Override
	public void setValues(Sprite target, int tweenType, float[] newValues) {
		switch (tweenType) {
			case POSITION_XY:
				target.setPosition(
					newValues[0] - target.getOriginX(),
					newValues[1] - target.getOriginY());
				break;

			case SCALE_XY:
				target.setScale(newValues[0], newValues[1]);
				break;

			case ROTATION:
				target.setRotation(newValues[0]);
				break;

			case OPACITY:
				Color c = target.getColor();
				c.set(c.r, c.g, c.b, newValues[0]);
				target.setColor(c);
				break;

			default: assert false;
		}
	}
}
