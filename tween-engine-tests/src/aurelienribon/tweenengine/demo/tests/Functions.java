package aurelienribon.tweenengine.demo.tests;

import aurelienribon.accessors.SpriteAccessor;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.demo.Assets;
import aurelienribon.tweenengine.demo.Test;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Circ;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Elastic;
import aurelienribon.tweenengine.equations.Expo;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quart;
import aurelienribon.tweenengine.equations.Quint;
import aurelienribon.tweenengine.equations.Sine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class Functions extends Test {
	private final TweenManager tweenManager = new TweenManager();
	private Sprite functions1;
	private Sprite functions2;
	private Sprite functions3;
	private int state;

	@Override
	public String getTitle() {
		return "Easing functions";
	}

	@Override
	public String getInfo() {
		return "The most common easing functions - used in JQuery and Flash - are available, plus "
			+ "your owns (touch to switch functions).";
	}

	@Override
	public String getImageName() {
		return "tile-functions";
	}

	@Override
	public InputProcessor getInput() {
		return inputProcessor;
	}

	@Override
	protected void initializeOverride() {
		TextureAtlas atlas = Assets.inst().get("data/test/pack", TextureAtlas.class);
		functions1 = atlas.createSprite("functions1");
		functions2 = atlas.createSprite("functions2");
		functions3 = atlas.createSprite("functions3");
		functions1.setSize(3.5f * functions1.getWidth() / functions1.getHeight(), 3.5f);
		functions2.setSize(3.5f * functions2.getWidth() / functions2.getHeight(), 3.5f);
		functions3.setSize(2.5f * functions3.getWidth() / functions3.getHeight(), 2.5f);
		center(functions1, -3.5f, 0.5f);
		center(functions2, -3.5f, 0.5f);
		center(functions3, -3.5f, 1.0f);
		functions1.setColor(1, 1, 1, 0);
		functions2.setColor(1, 1, 1, 0);
		functions3.setColor(1, 1, 1, 0);

		createSprites(4);
		enableDots(0);
		enableDots(1);
		enableDots(2);
		enableDots(3);
		center(sprites[0], -2, +2);
		center(sprites[1], -2, +1);
		center(sprites[2], -2, +0);
		center(sprites[3], -2, -1);

		startFunctions1(0.5f);
	}

	@Override
	protected void disposeOverride() {
		tweenManager.killAll();
	}

	@Override
	protected void renderOverride() {
		tweenManager.update(Gdx.graphics.getDeltaTime());

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		if (functions1.getColor().a > 0.1f) functions1.draw(batch);
		if (functions2.getColor().a > 0.1f) functions2.draw(batch);
		if (functions3.getColor().a > 0.1f) functions3.draw(batch);
		batch.end();
	}

	private void reset(float duration) {
		tweenManager.killAll();

		Timeline.createParallel()
			.push(Tween.set(sprites[0], SpriteAccessor.CPOS_XY).target(-2, +2))
			.push(Tween.set(sprites[1], SpriteAccessor.CPOS_XY).target(-2, +1))
			.push(Tween.set(sprites[2], SpriteAccessor.CPOS_XY).target(-2, +0))
			.push(Tween.set(sprites[3], SpriteAccessor.CPOS_XY).target(-2, -1))
			.start(tweenManager);
	}

	private void startFunctions1(float delay) {
		state = 0;
		enableDots(3);

		Timeline.createParallel()
			.push(Tween.to(functions1, SpriteAccessor.OPACITY, 0.4f).target(1))
			.push(Tween.to(functions2, SpriteAccessor.OPACITY, 0.4f).target(0))
			.push(Tween.to(functions3, SpriteAccessor.OPACITY, 0.4f).target(0))
			.push(Tween.to(sprites[3], SpriteAccessor.OPACITY, 0.4f).target(1))
			.start(tweenManager);

		Timeline.createParallel()
			.push(Tween.to(sprites[0], SpriteAccessor.CPOS_XY, 1.0f).targetRelative(6, 0).ease(Quad.INOUT))
			.push(Tween.to(sprites[1], SpriteAccessor.CPOS_XY, 1.0f).targetRelative(6, 0).ease(Cubic.INOUT))
			.push(Tween.to(sprites[2], SpriteAccessor.CPOS_XY, 1.0f).targetRelative(6, 0).ease(Quart.INOUT))
			.push(Tween.to(sprites[3], SpriteAccessor.CPOS_XY, 1.0f).targetRelative(6, 0).ease(Quint.INOUT))
			.repeat(-1, 1.0f)
			.delay(delay)
			.start(tweenManager);
	}

	private void startFunctions2(float delay) {
		state = 1;
		Timeline.createParallel()
			.push(Tween.to(functions1, SpriteAccessor.OPACITY, 0.4f).target(0))
			.push(Tween.to(functions2, SpriteAccessor.OPACITY, 0.4f).target(1))
			.push(Tween.to(functions3, SpriteAccessor.OPACITY, 0.4f).target(0))
			.start(tweenManager);

		Timeline.createParallel()
			.push(Tween.to(sprites[0], SpriteAccessor.CPOS_XY, 1.0f).targetRelative(6, 0).ease(Circ.INOUT))
			.push(Tween.to(sprites[1], SpriteAccessor.CPOS_XY, 1.0f).targetRelative(6, 0).ease(Sine.INOUT))
			.push(Tween.to(sprites[2], SpriteAccessor.CPOS_XY, 1.0f).targetRelative(6, 0).ease(Expo.INOUT))
			.push(Tween.to(sprites[3], SpriteAccessor.CPOS_XY, 1.0f).targetRelative(6, 0).ease(Linear.INOUT))
			.repeat(-1, 1.0f)
			.delay(delay)
			.start(tweenManager);
	}

	private void startFunctions3(float delay) {
		state = 2;
		disableDots(3);

		Timeline.createParallel()
			.push(Tween.to(functions1, SpriteAccessor.OPACITY, 0.4f).target(0))
			.push(Tween.to(functions2, SpriteAccessor.OPACITY, 0.4f).target(0))
			.push(Tween.to(functions3, SpriteAccessor.OPACITY, 0.4f).target(1))
			.push(Tween.to(sprites[3], SpriteAccessor.OPACITY, 0.4f).target(0))
			.start(tweenManager);

		Timeline.createParallel()
			.push(Tween.to(sprites[0], SpriteAccessor.CPOS_XY, 1.0f).targetRelative(6, 0).ease(Back.OUT))
			.push(Tween.to(sprites[1], SpriteAccessor.CPOS_XY, 1.0f).targetRelative(6, 0).ease(Elastic.OUT))
			.push(Tween.to(sprites[2], SpriteAccessor.CPOS_XY, 1.0f).targetRelative(6, 0).ease(Bounce.OUT))
			.repeat(-1, 1.0f)
			.delay(delay)
			.start(tweenManager);
	}

	private final InputProcessor inputProcessor = new InputAdapter() {
		@Override
		public boolean touchDown(int x, int y, int pointer, int button) {
			switch (state) {
				case 0: reset(0.5f); startFunctions2(1.0f); break;
				case 1: reset(0.5f); startFunctions3(1.0f); break;
				case 2: reset(0.5f); startFunctions1(1.0f); break;
			}

			return true;
		}
	};
}
