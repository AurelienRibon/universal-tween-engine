package aurelienribon.gdxtests;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class Tile {
	private final float x, y;
	private final Test test;
	private final Sprite sprite;
	private final OrthographicCamera camera;
	private final TweenManager tweenManager;
	private final MutableFloat textOpacity = new MutableFloat(1);

	public Tile(float x, float y, float w, float h, Test test, Texture tex, OrthographicCamera camera, TweenManager tweenManager) {
		this.x = x;
		this.y = y;
		this.test = test;
		this.camera = camera;
		this.tweenManager = tweenManager;
		this.sprite = new Sprite(tex);
		sprite.setSize(w, h);
		sprite.setOrigin(w/2, h/2);
		sprite.setPosition(x, y);
		sprite.setColor(68/255f, 99/255f, 112/255f, 1);
	}

	public void draw(SpriteBatch batch, BitmapFont font) {
		sprite.draw(batch);

		font.setColor(1, 1, 1, textOpacity.floatValue());
		font.drawWrapped(batch, test.getTitle(),
			sprite.getX() + sprite.getWidth()/20,
			sprite.getY() + sprite.getHeight()*19/20,
			sprite.getWidth() - sprite.getWidth()/10);
	}

	public void enter(float delay) {
		sprite.setPosition(x + camera.viewportWidth, y);

		Tween.to(sprite, SpriteAccessor.POS_XY, 1.0f)
			.target(x, y)
			.ease(Cubic.INOUT)
			.delay(delay)
			.start(tweenManager);
	}

	public void maximize(TweenCallback callback) {
		float tx = camera.position.x - sprite.getWidth()/2;
		float ty = camera.position.y - sprite.getHeight()/2;
		float sx = camera.viewportWidth / sprite.getWidth();
		float sy = camera.viewportHeight / sprite.getHeight();

		Timeline.createSequence()
			.beginParallel()
				.push(Tween.to(textOpacity, 0, 0.3f).target(0))
				.push(Tween.to(sprite, SpriteAccessor.SCALE_XY, 0.3f).target(0.9f, 0.9f).ease(Quad.OUT))
			.end()
			.beginParallel()
				.push(Tween.to(sprite, SpriteAccessor.SCALE_XY, 0.5f).target(sx, sy).ease(Cubic.IN))
				.push(Tween.to(sprite, SpriteAccessor.POS_XY, 0.5f).target(tx, ty).ease(Quad.IN))
			.end()
			.pushPause(-0.3f)
			.push(Tween.to(sprite, SpriteAccessor.TINT, 0.5f).target(1, 1, 1))
			.setUserData(this)
			.setCallback(callback)
			.start(tweenManager);
	}

	public void minimize(TweenCallback callback) {
		tweenManager.killTarget(sprite);
		tweenManager.killTarget(textOpacity);

		Timeline.createSequence()
			.beginParallel()
				.push(Tween.to(sprite, SpriteAccessor.TINT, 0.5f).target(68/255f, 99/255f, 112/255f))
				.push(Tween.to(sprite, SpriteAccessor.SCALE_XY, 0.3f).target(1, 1).ease(Quad.OUT))
				.push(Tween.to(sprite, SpriteAccessor.POS_XY, 0.5f).target(x, y).ease(Quad.OUT))
			.end()
			.push(Tween.to(textOpacity, 0, 0.3f).target(1))
			.setUserData(this)
			.setCallback(callback)
			.start(tweenManager);
	}

	public boolean isOver(float x, float y) {
		return sprite.getX() <= x && x <= sprite.getX() + sprite.getWidth()
			&& sprite.getY() <= y && y <= sprite.getY() + sprite.getHeight();
	}

	public Test getTest() {
		return test;
	}
}
