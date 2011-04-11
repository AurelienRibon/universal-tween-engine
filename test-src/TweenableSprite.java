import aurelienribon.tweenengine.Tweenable;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class TweenableSprite extends Sprite implements Tweenable {
	public static final int OPACITY = 1;
	public static final int X = 2;
	public static final int Y = 3;
	public static final int ROTATION = 4;
	public static final int ROTATION_AND_OPACITY = 5;

	public TweenableSprite(Texture texture) {
		super(texture);
	}

	@Override
	public int getTweenedAttributeCount(int tweenType) {
		return tweenType == ROTATION_AND_OPACITY ? 2 : 1;
	}

	@Override
	public void getTweenValues(int tweenType, float[] returnValues) {
		switch (tweenType) {
			case OPACITY: returnValues[0] = getColor().a; break;
			case X: returnValues[0] = getX() + getOriginX(); break;
			case Y: returnValues[0] = getY() + getOriginY(); break;
			case ROTATION: returnValues[0] = getRotation(); break;
			case ROTATION_AND_OPACITY: returnValues[0] = getRotation(); returnValues[1] = getColor().a; break;
			default: assert false;
		}
	}

	@Override
	public void tweenUpdated(int tweenType, float[] newValues) {
		switch (tweenType) {
			case OPACITY: setOpacity(newValues[0]); break;
			case X: setPosition(newValues[0] - getOriginX(), getY()); break;
			case Y: setPosition(getX(), newValues[0] - getOriginY()); break;
			case ROTATION: setRotation(newValues[0]); break;
			case ROTATION_AND_OPACITY: setRotation(newValues[0]); setOpacity(newValues[1]); break;
			default: assert false;
		}
	}

	private void setOpacity(float a) {
		Color c = getColor();
		c.set(c.r, c.g, c.b, a);
		setColor(c);
	}
}
