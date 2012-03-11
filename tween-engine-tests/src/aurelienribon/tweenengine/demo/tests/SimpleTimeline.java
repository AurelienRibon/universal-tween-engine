package aurelienribon.tweenengine.demo.tests;

import aurelienribon.accessors.SpriteAccessor;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.demo.Test;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Quint;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class SimpleTimeline extends Test {
	private final TweenManager tweenManager = new TweenManager();

	@Override
	public String getTitle() {
		return "Simple Timeline";
	}

	@Override
	public String getInfo() {
		return "A timeline sequences multiple tweens (or other timelines) either one after the other, or all at once!";
	}

	@Override
	public String getImageName() {
		return "tile-timeline";
	}

	@Override
	public InputProcessor getInput() {
		return null;
	}

	@Override
	protected void initializeOverride() {
		createSprites(1);
		center(sprites[0], -3, 2);

		Timeline.createSequence()
			.push(Tween.set(sprites[0], SpriteAccessor.OPACITY).target(0))
			.push(Tween.to(sprites[0], SpriteAccessor.OPACITY, 0.7f).target(1))
			.push(Tween.to(sprites[0], SpriteAccessor.CPOS_XY, 0.7f).target(3, 0).ease(Cubic.INOUT))
			.beginParallel()
				.push(Tween.to(sprites[0], SpriteAccessor.ROTATION, 1.0f).target(360))
				.push(Tween.to(sprites[0], SpriteAccessor.OPACITY, 0.5f).target(0).repeatYoyo(1, 0))
			.end()
			.push(Tween.to(sprites[0], SpriteAccessor.CPOS_XY, 0.7f).target(0, 0).ease(Quint.INOUT))
			.push(Tween.to(sprites[0], SpriteAccessor.SCALE_XY, 0.8f).target(2.5f, 2.5f).ease(Back.INOUT))
			.push(Tween.to(sprites[0], SpriteAccessor.TINT, 0.4f).target(1, 0, 0))
			.push(Tween.to(sprites[0], SpriteAccessor.TINT, 0.4f).target(0, 1, 0))
			.push(Tween.to(sprites[0], SpriteAccessor.TINT, 0.4f).target(0, 0, 1))
			.push(Tween.to(sprites[0], SpriteAccessor.TINT, 0.4f).target(0, 0, 0))
			.push(Tween.to(sprites[0], SpriteAccessor.OPACITY, 0.7f).target(0))
			.repeat(-1, 0.5f)
			.start(tweenManager);
	}

	@Override
	protected void disposeOverride() {
		tweenManager.killAll();
	}

	@Override
	protected void renderOverride() {
		tweenManager.update(Gdx.graphics.getDeltaTime());
	}
}
