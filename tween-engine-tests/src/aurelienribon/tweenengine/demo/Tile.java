package aurelienribon.tweenengine.demo;

import aurelienribon.accessors.SpriteAccessor;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class Tile {
	private final float x, y;
	private final Test test;
	private final Sprite sprite;
	private final Sprite interactiveIcon;
	private final Sprite veil;
	private final OrthographicCamera camera;
	private final BitmapFont font;
	private final TweenManager tweenManager;
	private final MutableFloat textOpacity = new MutableFloat(1);

	public Tile(float x, float y, float w, float h, Test test, TextureAtlas atlas, OrthographicCamera camera, BitmapFont font, TweenManager tweenManager) {
		this.x = x;
		this.y = y;
		this.test = test;
		this.camera = camera;
		this.font = font;
		this.tweenManager = tweenManager;

		this.sprite = test.getImageName() != null ? atlas.createSprite(test.getImageName()) : atlas.createSprite("tile");
		this.interactiveIcon = atlas.createSprite("interactive");
		this.veil = atlas.createSprite("white");

		sprite.setSize(w, h);
		sprite.setOrigin(w/2, h/2);
		sprite.setPosition(x + camera.viewportWidth, y);

		interactiveIcon.setSize(w/10, w/10 * interactiveIcon.getHeight() / interactiveIcon.getWidth());
		interactiveIcon.setPosition(x+w - interactiveIcon.getWidth() - w/50, y+h - interactiveIcon.getHeight() - w/50);
		interactiveIcon.setColor(1, 1, 1, 0);

		veil.setSize(w, h);
		veil.setOrigin(w/2, h/2);
		veil.setPosition(x, y);
		veil.setColor(1, 1, 1, 0);
	}

	public void draw(SpriteBatch batch) {
		sprite.draw(batch);
		if (test.getInput() != null) interactiveIcon.draw(batch);

		float wrapW = (sprite.getWidth() - sprite.getWidth()/10) * Gdx.graphics.getWidth() / camera.viewportWidth;

		font.setColor(1, 1, 1, textOpacity.floatValue());
		font.drawWrapped(batch, test.getTitle(),
			sprite.getX() + sprite.getWidth()/20,
			sprite.getY() + sprite.getHeight()*19/20,
			wrapW);

		if (veil.getColor().a > 0.1f) veil.draw(batch);
	}

	public void enter(float delay) {
		Timeline.createSequence()
			.push(Tween.to(sprite, SpriteAccessor.POS_XY, 0.7f).target(x, y).ease(Cubic.INOUT))
			.pushPause(0.1f)
			.push(Tween.to(interactiveIcon, SpriteAccessor.OPACITY, 0.4f).target(1))
			.delay(delay)
			.start(tweenManager);
	}

	public void maximize(TweenCallback callback) {
		tweenManager.killTarget(interactiveIcon);
		tweenManager.killTarget(textOpacity);
		tweenManager.killTarget(sprite);

		float tx = camera.position.x - sprite.getWidth()/2;
		float ty = camera.position.y - sprite.getHeight()/2;
		float sx = camera.viewportWidth / sprite.getWidth();
		float sy = camera.viewportHeight / sprite.getHeight();

		Timeline.createSequence()
			.push(Tween.set(veil, SpriteAccessor.POS_XY).target(tx, ty))
			.push(Tween.set(veil, SpriteAccessor.SCALE_XY).target(sx, sy))
			.beginParallel()
				.push(Tween.to(textOpacity, 0, 0.2f).target(0))
				.push(Tween.to(interactiveIcon, SpriteAccessor.OPACITY, 0.2f).target(0))
			.end()
			.push(Tween.to(sprite, SpriteAccessor.SCALE_XY, 0.3f).target(0.9f, 0.9f).ease(Quad.OUT))
			.beginParallel()
				.push(Tween.to(sprite, SpriteAccessor.SCALE_XY, 0.5f).target(sx, sy).ease(Cubic.IN))
				.push(Tween.to(sprite, SpriteAccessor.POS_XY, 0.5f).target(tx, ty).ease(Quad.IN))
			.end()
			.pushPause(-0.3f)
			.push(Tween.to(veil, SpriteAccessor.OPACITY, 0.7f).target(1))
			.setUserData(this)
			.setCallback(callback)
			.start(tweenManager);
	}

	public void minimize(TweenCallback callback) {
		tweenManager.killTarget(sprite);
		tweenManager.killTarget(textOpacity);

		Timeline.createSequence()
			.push(Tween.set(veil, SpriteAccessor.OPACITY).target(0))
			.beginParallel()
				.push(Tween.to(sprite, SpriteAccessor.SCALE_XY, 0.3f).target(1, 1).ease(Quad.OUT))
				.push(Tween.to(sprite, SpriteAccessor.POS_XY, 0.5f).target(x, y).ease(Quad.OUT))
			.end()
			.beginParallel()
				.push(Tween.to(textOpacity, 0, 0.3f).target(1))
				.push(Tween.to(interactiveIcon, SpriteAccessor.OPACITY, 0.3f).target(1))
			.end()
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
