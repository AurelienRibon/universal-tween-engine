

import aurelienribon.libgdx.tween.Tweenable;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class TweenableSprite extends Sprite implements Tweenable {
	public static final int OPACITY = 1;
	public static final int X = 2;
	public static final int Y = 3;
	public static final int ORIG_X = 4;
	public static final int ORIG_Y = 5;
	public static final int ROTATION = 6;

	public TweenableSprite(Texture texture) {
		super(texture);
	}

	@Override
	public float getTweenValue(int tweenType) {
		float ret = 0;
		switch (tweenType) {
			case OPACITY: ret = getColor().a; break;
			case X: ret = getX(); break;
			case Y: ret = getY(); break;
			case ORIG_X: ret = getX() + getOriginX(); break;
			case ORIG_Y: ret = getY() + getOriginY(); break;
			case ROTATION: ret = getRotation(); break;
		}
		return ret;
	}

	@Override
	public void tweenUpdated(int tweenType, float newValue) {
		switch (tweenType) {
			case OPACITY: Color c = getColor(); c.set(c.r, c.g, c.b, newValue); break;
			case X: setPosition(newValue, getY()); break;
			case Y: setPosition(getX(), newValue);
			case ORIG_X: setPosition(newValue - getOriginX(), getY()); break;
			case ORIG_Y: setPosition(getX(), newValue - getOriginY()); break;
			case ROTATION: setRotation(newValue); break;
		}
	}
}
