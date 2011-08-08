package aurelienribon.tweenengine.tests.libgdx;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenGroup;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Elastic;
import aurelienribon.tweenengine.equations.Quart;
import aurelienribon.tweenengine.equations.Sine;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class App implements ApplicationListener {
	private OrthographicCamera camera;
	private SpriteBatch sb;
	private TweenManager tweenManager;
	
	private Texture texture;
	private Sprite sprite;
	private TweenSprite tweenSprite;

	private BitmapFont font;
	private String text;

	@Override
	public void create() {
		// Input manager
		Gdx.input.setInputProcessor(inputProcessor);

		// Camera
		camera = new OrthographicCamera(
			Gdx.graphics.getWidth(),
			Gdx.graphics.getHeight());
		camera.position.set(0, 0, 0);
		camera.update();

		// Spritebatch + misc
		sb = new SpriteBatch();
		font = new BitmapFont();
		font.setColor(Color.BLACK);

		// Creation of the Sprite, classic way
		texture = new Texture(Gdx.files.internal("data/logo.png"));
		sprite = new Sprite(texture);
		sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
		sprite.setPosition(-sprite.getWidth()/2, -sprite.getHeight()/2);

		// Creation of a Tween manager
		Tween.setPoolEnabled(true);
		tweenManager = new TweenManager();

		// Creation of the Tweenable associated with the previous sprite.
		// I use a composition pattern instead of deriving from Sprite. This
		// allows an easier management of Tweenables and I can still use every
		// constructor of the Sprite class to define the sprites. Moreover,
		// some methods in libgdx (as TextureAtlas) are sprite factories, so
		// they create the sprites for you. Thus, you can only tween them with
		// such composition pattern.
		tweenSprite = new TweenSprite(sprite);

		// Demo of the Tween.call possibility. It's just a timer :)
		Tween.call(new TweenCallback() {
			@Override public void tweenEventOccured(Types eventType, Tween tween) {
				startNextTween();
			}
		}).delay(1000).addToManager(tweenManager).start();
		text = "Idle (auto-start in 1 second)";
	}

	@Override
	public void render() {
		tweenManager.update();

		GL10 gl = Gdx.gl10;
		gl.glClearColor(1, 1, 1, 1);
		gl.glClear(gl.GL_COLOR_BUFFER_BIT);

		sb.begin();
		camera.apply(gl);
		sprite.draw(sb);
		sb.end();

		sb.begin();
		font.draw(sb, text, 5, 65);
		font.draw(sb, "Running tweens: " + tweenManager.getTweenCount(), 5, 45);
		font.draw(sb, "Tweens in pool: " + Tween.getPoolSize(), 5, 25);
		sb.end();
	}

	@Override public void resize(int width, int height) {}
	@Override public void pause() {}
	@Override public void resume() {}
	@Override public void dispose() {}

	// -------------------------------------------------------------------------
	// STATE MACHINE
	// -------------------------------------------------------------------------

	private int state = -1;
	private void startNextTween() {
		switch (state) {
			case 0:
				text = "Demo (click to override)";
				tweenManager.kill(tweenSprite, TweenSprite.ROTATION);
				tweenManager.kill(tweenSprite, TweenSprite.OPACITY);
				tweenManager.kill(tweenSprite, TweenSprite.POSITION_XY);
				tweenManager.kill(tweenSprite, TweenSprite.SCALE_XY);
				tweenManager.add(new TweenGroup().pack(
					Tween.set(tweenSprite, TweenSprite.ROTATION).target(0),
					Tween.set(tweenSprite, TweenSprite.OPACITY).target(0),
					Tween.set(tweenSprite, TweenSprite.POSITION_XY).target(-sprite.getWidth()/2, -sprite.getHeight()/2),
					Tween.set(tweenSprite, TweenSprite.SCALE_XY).target(10, 10),
					Tween.to(tweenSprite, TweenSprite.OPACITY, 1000, Sine.INOUT).target(1),
					Tween.to(tweenSprite, TweenSprite.SCALE_XY, 1000, Quart.INOUT).target(1, 1).delay(-1000),
					Tween.to(tweenSprite, TweenSprite.POSITION_XY, 1000, Elastic.OUT).target(-200, sprite.getY()).delay(200),
					Tween.to(tweenSprite, TweenSprite.ROTATION, 800, Cubic.INOUT).target(360).delay(-400)
				).sequence().start());
				break;

			default:
				state = 0;
				startNextTween();
				break;
		}
	}

	// -------------------------------------------------------------------------
	// INPUT
	// -------------------------------------------------------------------------

	private InputProcessor inputProcessor = new InputAdapter() {
		@Override
		public boolean touchDown(int x, int y, int pointer, int button) {
			if (state >= 0) {
				state += 1;
				startNextTween();
			}
			return true;
		}
	};
}
