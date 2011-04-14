import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenGroup;
import aurelienribon.tweenengine.callbacks.TweenIterationCompleteCallback;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Circ;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Elastic;
import aurelienribon.tweenengine.equations.Expo;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quart;
import aurelienribon.tweenengine.equations.Quint;
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
	private static final float POS_LEFT = -220;
	private static final float POS_CENTER = -20;
	private static final float POS_RIGHT = +180;
	private static final int DURATION = 500;

	private OrthographicCamera camera;
	private SpriteBatch sb;
	
	private Texture texture;
	private Sprite sprite;
	private TweenableSprite tweenSprite;

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
		texture = new Texture(Gdx.files.internal("test-data/logo.png"));
		sprite = new Sprite(texture);
		sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
		sprite.setPosition(-sprite.getWidth()/2, -sprite.getHeight()/2);

		// Creation of the Tweenable associated with the previous sprite.
		// I use a composition pattern instead of deriving from Sprite. This
		// allows an easier management of Tweenables and I can still use every
		// constructor of the Sprite class to define the sprites. Moreover,
		// some methods in libgdx (as TextureAtlas) are sprite factories, so
		// they create the sprites for you. Thus, you can only tween them with
		// such composition pattern.
		tweenSprite = new TweenableSprite(sprite);

		// Demo of the Tween.call possibility. It's just a timer :)
		Tween.call(callback).delay(2000).start();
		text = "Idle (auto-start in 2 second)";
	}

	private TweenIterationCompleteCallback callback = new TweenIterationCompleteCallback() {
		@Override
		public void iterationComplete(Tween tween) {
			startNextTween();
		}
	};

	@Override
	public void render() {
		Tween.update();

		GL10 gl = Gdx.gl10;
		gl.glClearColor(1, 1, 1, 1);
		gl.glClear(gl.GL_COLOR_BUFFER_BIT);

		sb.begin();
		camera.apply(gl);
		sprite.draw(sb);
		sb.end();

		sb.begin();
		if (state >= 0)
			font.draw(sb, "Click for next tween...", 5, 65);
		font.draw(sb, text, 5, 45);
		font.draw(sb, "Running tweens: " + Tween.getRunningTweenCount(), 5, 25);
		sb.end();
	}

	// -------------------------------------------------------------------------
	// STATE MACHINE
	// -------------------------------------------------------------------------

	private int state = -1;
	private void startNextTween() {
		state += 1;

		switch (state) {
			case 0:
				text = "Demo (with one auto-repeat)";
				TweenGroup.sequence(
					Tween.set(tweenSprite, TweenableSprite.ROTATION, 0),
					Tween.set(tweenSprite, TweenableSprite.OPACITY, 0),
					Tween.set(tweenSprite, TweenableSprite.POSITION_XY, -sprite.getWidth()/2, -sprite.getHeight()/2),
					Tween.set(tweenSprite, TweenableSprite.SCALE_XY, 10, 10),
					Tween.to(tweenSprite, TweenableSprite.OPACITY, Sine.INOUT, 1000, 1),
					Tween.to(tweenSprite, TweenableSprite.SCALE_XY, Quart.INOUT, 1000, 1, 1).delay(-1000),
					Tween.to(tweenSprite, TweenableSprite.POSITION_XY, Elastic.OUT, 1000, POS_LEFT, sprite.getY()).delay(200),
					Tween.to(tweenSprite, TweenableSprite.ROTATION, Cubic.INOUT, 800, 360).delay(-400)
					).repeat(1, 500).start();
				break;

			case 1: Tween.to(tweenSprite, TweenableSprite.POSITION_XY, Quad.OUT, DURATION, POS_CENTER, sprite.getY()).start(); text = "Quad : OUT"; break;
			case 2: Tween.to(tweenSprite, TweenableSprite.POSITION_XY, Quad.IN, DURATION, POS_RIGHT, sprite.getY()).start(); text = "Quad : IN"; break;
			case 3: Tween.to(tweenSprite, TweenableSprite.POSITION_XY, Quad.INOUT, DURATION * 2, POS_LEFT, sprite.getY()).start(); text = "Quad : INOUT"; break;

			case 4: Tween.to(tweenSprite, TweenableSprite.POSITION_XY, Cubic.OUT, DURATION, POS_CENTER, sprite.getY()).start(); text = "Cubic : OUT"; break;
			case 5: Tween.to(tweenSprite, TweenableSprite.POSITION_XY, Cubic.IN, DURATION, POS_RIGHT, sprite.getY()).start(); text = "Cubic : IN"; break;
			case 6: Tween.to(tweenSprite, TweenableSprite.POSITION_XY, Cubic.INOUT, DURATION * 2, POS_LEFT, sprite.getY()).start(); text = "Cubic : INOUT"; break;

			case 7: Tween.to(tweenSprite, TweenableSprite.POSITION_XY, Quart.OUT, DURATION, POS_CENTER, sprite.getY()).start(); text = "Quart : OUT"; break;
			case 8: Tween.to(tweenSprite, TweenableSprite.POSITION_XY, Quart.IN, DURATION, POS_RIGHT, sprite.getY()).start(); text = "Quart : IN"; break;
			case 9: Tween.to(tweenSprite, TweenableSprite.POSITION_XY, Quart.INOUT, DURATION * 2, POS_LEFT, sprite.getY()).start(); text = "Quart : INOUT"; break;

			case 10: Tween.to(tweenSprite, TweenableSprite.POSITION_XY, Quint.OUT, DURATION, POS_CENTER, sprite.getY()).start(); text = "Quint : OUT"; break;
			case 11: Tween.to(tweenSprite, TweenableSprite.POSITION_XY, Quint.IN, DURATION, POS_RIGHT, sprite.getY()).start(); text = "Quint : IN"; break;
			case 12: Tween.to(tweenSprite, TweenableSprite.POSITION_XY, Quint.INOUT, DURATION * 2, POS_LEFT, sprite.getY()).start(); text = "Quint : INOUT"; break;

			case 13: Tween.to(tweenSprite, TweenableSprite.POSITION_XY, Expo.OUT, DURATION, POS_CENTER, sprite.getY()).start(); text = "Expo : OUT"; break;
			case 14: Tween.to(tweenSprite, TweenableSprite.POSITION_XY, Expo.IN, DURATION, POS_RIGHT, sprite.getY()).start(); text = "Expo : IN"; break;
			case 15: Tween.to(tweenSprite, TweenableSprite.POSITION_XY, Expo.INOUT, DURATION * 2, POS_LEFT, sprite.getY()).start(); text = "Expo : INOUT"; break;

			case 16: Tween.to(tweenSprite, TweenableSprite.POSITION_XY, Circ.OUT, DURATION, POS_CENTER, sprite.getY()).start(); text = "Circ : OUT"; break;
			case 17: Tween.to(tweenSprite, TweenableSprite.POSITION_XY, Circ.IN, DURATION, POS_RIGHT, sprite.getY()).start(); text = "Circ : IN"; break;
			case 18: Tween.to(tweenSprite, TweenableSprite.POSITION_XY, Circ.INOUT, DURATION * 2, POS_LEFT, sprite.getY()).start(); text = "Circ : INOUT"; break;

			case 19: Tween.to(tweenSprite, TweenableSprite.POSITION_XY, Sine.OUT, DURATION, POS_CENTER, sprite.getY()).start(); text = "Sine : OUT"; break;
			case 20: Tween.to(tweenSprite, TweenableSprite.POSITION_XY, Sine.IN, DURATION, POS_RIGHT, sprite.getY()).start(); text = "Sine : IN"; break;
			case 21: Tween.to(tweenSprite, TweenableSprite.POSITION_XY, Sine.INOUT, DURATION * 2, POS_LEFT, sprite.getY()).start(); text = "Sine : INOUT"; break;

			case 22: Tween.to(tweenSprite, TweenableSprite.POSITION_XY, Back.OUT, DURATION, POS_CENTER, sprite.getY()).start(); text = "Back : OUT"; break;
			case 23: Tween.to(tweenSprite, TweenableSprite.POSITION_XY, Back.IN, DURATION, POS_RIGHT, sprite.getY()).start(); text = "Back : IN"; break;
			case 24: Tween.to(tweenSprite, TweenableSprite.POSITION_XY, Back.INOUT, DURATION * 2, POS_LEFT, sprite.getY()).start(); text = "Back : INOUT"; break;

			case 25: Tween.to(tweenSprite, TweenableSprite.POSITION_XY, Bounce.OUT, DURATION * 2, POS_CENTER, sprite.getY()).start(); text = "Bounce : OUT"; break;
			case 26: Tween.to(tweenSprite, TweenableSprite.POSITION_XY, Bounce.IN, DURATION * 2, POS_RIGHT, sprite.getY()).start(); text = "Bounce : IN"; break;
			case 27: Tween.to(tweenSprite, TweenableSprite.POSITION_XY, Bounce.INOUT, DURATION * 4, POS_LEFT, sprite.getY()).start(); text = "Bounce : INOUT"; break;

			case 28: Tween.to(tweenSprite, TweenableSprite.POSITION_XY, Elastic.OUT, DURATION * 2, POS_CENTER, sprite.getY()).start(); text = "Elastic : OUT"; break;
			case 29: Tween.to(tweenSprite, TweenableSprite.POSITION_XY, Elastic.IN, DURATION * 2, POS_RIGHT, sprite.getY()).start(); text = "Elastic : IN"; break;
			case 30: Tween.to(tweenSprite, TweenableSprite.POSITION_XY, Elastic.INOUT, DURATION * 4, POS_LEFT, sprite.getY()).start(); text = "Elastic : INOUT"; break;

			default: state = -1; startNextTween();
		}
	}

	// -------------------------------------------------------------------------
	// INPUT
	// -------------------------------------------------------------------------

	private InputProcessor inputProcessor = new InputAdapter() {
		@Override
		public boolean touchDown(int x, int y, int pointer, int button) {
			if (state >= 0)
				startNextTween();
			return true;
		}
	};

	// -------------------------------------------------------------------------

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}
}
