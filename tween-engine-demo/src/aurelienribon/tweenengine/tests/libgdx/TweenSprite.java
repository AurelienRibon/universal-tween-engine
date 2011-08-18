package aurelienribon.tweenengine.tests.libgdx;

import aurelienribon.tweenengine.Tweenable;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class TweenSprite implements Tweenable {
	public static final int POSITION_XY = 1;
	public static final int SCALE_XY = 2;
	public static final int ROTATION = 3;
	public static final int OPACITY = 4;

	private Sprite sprite;

	public TweenSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	@Override
	public int getTweenValues(int tweenType, float[] returnValues) {
		switch (tweenType) {
			case POSITION_XY:
				returnValues[0] = sprite.getX() + sprite.getOriginX();
				returnValues[1] = sprite.getY() + sprite.getOriginY();
				return 2;

			case SCALE_XY:
				returnValues[0] = sprite.getScaleX();
				returnValues[1] = sprite.getScaleY();
				return 2;

			case ROTATION:
				returnValues[0] = sprite.getRotation();
				return 1;

			case OPACITY:
				returnValues[0] = sprite.getColor().a;
				return 1;

			default: assert false; return -1;
		}
	}

	@Override
	public void onTweenUpdated(int tweenType, float[] newValues) {
		switch (tweenType) {
			case POSITION_XY:
				sprite.setPosition(
					newValues[0] - sprite.getOriginX(),
					newValues[1] - sprite.getOriginY());
				break;

			case SCALE_XY:
				sprite.setScale(newValues[0], newValues[1]);
				break;

			case ROTATION:
				sprite.setRotation(newValues[0]);
				break;

			case OPACITY:
				Color c = sprite.getColor();
				c.set(c.r, c.g, c.b, newValues[0]);
				sprite.setColor(c);
				break;

			default: assert false;
		}
	}
}
