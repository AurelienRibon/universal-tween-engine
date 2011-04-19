import aurelienribon.tweenengine.Tweenable;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class TweenableSprite implements Tweenable {
	public static final int ORIGIN_XY = 1;
	public static final int POSITION_XY = 2;
	public static final int SCALE_XY = 3;
	public static final int ROTATION = 4;
	public static final int OPACITY = 5;

	private Sprite sprite;

	public TweenableSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	@Override
	public int getTweenValues(int tweenType, float[] returnValues) {
		switch (tweenType) {
			case OPACITY:
				returnValues[0] = sprite.getColor().a;
				return 1;

			case ORIGIN_XY:
				returnValues[0] = sprite.getOriginX();
				returnValues[1] = sprite.getOriginY();
				return 2;

			case POSITION_XY:
				returnValues[0] = sprite.getX();
				returnValues[1] = sprite.getY();
				return 2;

			case ROTATION:
				returnValues[0] = sprite.getRotation();
				return 1;

			case SCALE_XY:
				returnValues[0] = sprite.getScaleX();
				returnValues[1] = sprite.getScaleY();
				return 2;

			default: assert false; return -1;
		}
	}

	@Override
	public void onTweenUpdated(int tweenType, float[] newValues) {
		switch (tweenType) {
			case OPACITY:
				Color c = sprite.getColor();
				c.set(c.r, c.g, c.b, newValues[0]);
				sprite.setColor(c);
				break;

			case ORIGIN_XY:
				sprite.setOrigin(newValues[0], newValues[1]);
				break;

			case POSITION_XY:
				sprite.setPosition(newValues[0], newValues[1]);
				break;

			case ROTATION:
				sprite.setRotation(newValues[0]);
				break;

			case SCALE_XY:
				sprite.setScale(newValues[0], newValues[1]);
				break;

			default: assert false;
		}
	}
}
