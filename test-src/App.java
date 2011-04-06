import aurelienribon.libgdx.tween.Tween;
import aurelienribon.libgdx.tween.TweenSequence;
import aurelienribon.libgdx.tween.callbacks.TweenCompleteCallback;
import aurelienribon.libgdx.tween.equations.Back;
import aurelienribon.libgdx.tween.equations.Bounce;
import aurelienribon.libgdx.tween.equations.Circ;
import aurelienribon.libgdx.tween.equations.Cubic;
import aurelienribon.libgdx.tween.equations.Elastic;
import aurelienribon.libgdx.tween.equations.Expo;
import aurelienribon.libgdx.tween.equations.Linear;
import aurelienribon.libgdx.tween.equations.Quad;
import aurelienribon.libgdx.tween.equations.Quart;
import aurelienribon.libgdx.tween.equations.Quint;
import aurelienribon.libgdx.tween.equations.Sine;
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
	private static final float POS_LEFT = -200;
	private static final float POS_CENTER = 0;
	private static final float POS_RIGHT = +200;
	private static final int DURATION = 500;

	private OrthographicCamera camera;
	private SpriteBatch sb;
	private Texture texture;
	private TweenableSprite sprite;
	private BitmapFont font;
	private String text;

	@Override
	public void create() {
		camera = new OrthographicCamera(
			Gdx.graphics.getWidth(),
			Gdx.graphics.getHeight());
		camera.position.set(0, 0, 0);
		camera.update();

		sb = new SpriteBatch();
		font = new BitmapFont();
		font.setColor(Color.BLACK);

		text = "Idle (click to continue)";

		texture = new Texture(Gdx.files.internal("data/logo.png"));
		sprite = new TweenableSprite(texture);
		sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
		moveSprite(sprite, POS_LEFT, 0);

		Gdx.input.setInputProcessor(inputProcessor);
	}

	@Override
	public void render() {
		GL10 gl = Gdx.gl10;
		gl.glClearColor(1, 1, 1, 1);
		gl.glClear(gl.GL_COLOR_BUFFER_BIT);

		sb.begin();
		camera.apply(gl);
		sprite.draw(sb);
		sb.end();

		sb.begin();
		font.draw(sb, text, 60, 50);
		sb.end();

		Tween.update();
	}

	private void moveSprite(Sprite sp, float x, float y) {
		sp.setPosition(x - sp.getOriginX(), y - sp.getOriginY());
	}

	private int state = 0;
	private void startNextTween() {
		switch (state) {
			case 0:
				TweenSequence seq = TweenSequence.set(
					Tween.to(sprite, TweenableSprite.ROTATION_AND_OPACITY, Linear.INOUT, DURATION, 360, 0.2f),
					Tween.to(sprite, TweenableSprite.X, Linear.INOUT, DURATION, POS_RIGHT),
					Tween.to(sprite, TweenableSprite.ROTATION_AND_OPACITY, Linear.INOUT, DURATION, 720, 1),
					Tween.to(sprite, TweenableSprite.X, Linear.INOUT, DURATION, POS_LEFT).onComplete(onComplete)
				);
				seq.start();
				text = "LINEAR : INOUT (using a TweenSequence of 4 tweens)";
				break;

			case 1: Tween.to(sprite, TweenableSprite.X, Quad.OUT, DURATION, POS_CENTER).onComplete(onComplete).start(); text = "Quad : OUT"; break;
			case 2: Tween.to(sprite, TweenableSprite.X, Quad.IN, DURATION, POS_RIGHT).onComplete(onComplete).start(); text = "Quad : IN"; break;
			case 3: Tween.to(sprite, TweenableSprite.X, Quad.INOUT, DURATION * 2, POS_LEFT).onComplete(onComplete).start(); text = "Quad : INOUT"; break;

			case 4: Tween.to(sprite, TweenableSprite.X, Cubic.OUT, DURATION, POS_CENTER).onComplete(onComplete).start(); text = "Cubic : OUT"; break;
			case 5: Tween.to(sprite, TweenableSprite.X, Cubic.IN, DURATION, POS_RIGHT).onComplete(onComplete).start(); text = "Cubic : IN"; break;
			case 6: Tween.to(sprite, TweenableSprite.X, Cubic.INOUT, DURATION * 2, POS_LEFT).onComplete(onComplete).start(); text = "Cubic : INOUT"; break;

			case 7: Tween.to(sprite, TweenableSprite.X, Quart.OUT, DURATION, POS_CENTER).onComplete(onComplete).start(); text = "Quart : OUT"; break;
			case 8: Tween.to(sprite, TweenableSprite.X, Quart.IN, DURATION, POS_RIGHT).onComplete(onComplete).start(); text = "Quart : IN"; break;
			case 9: Tween.to(sprite, TweenableSprite.X, Quart.INOUT, DURATION * 2, POS_LEFT).onComplete(onComplete).start(); text = "Quart : INOUT"; break;

			case 10: Tween.to(sprite, TweenableSprite.X, Quint.OUT, DURATION, POS_CENTER).onComplete(onComplete).start(); text = "Quint : OUT"; break;
			case 11: Tween.to(sprite, TweenableSprite.X, Quint.IN, DURATION, POS_RIGHT).onComplete(onComplete).start(); text = "Quint : IN"; break;
			case 12: Tween.to(sprite, TweenableSprite.X, Quint.INOUT, DURATION * 2, POS_LEFT).onComplete(onComplete).start(); text = "Quint : INOUT"; break;

			case 13: Tween.to(sprite, TweenableSprite.X, Expo.OUT, DURATION, POS_CENTER).onComplete(onComplete).start(); text = "Expo : OUT"; break;
			case 14: Tween.to(sprite, TweenableSprite.X, Expo.IN, DURATION, POS_RIGHT).onComplete(onComplete).start(); text = "Expo : IN"; break;
			case 15: Tween.to(sprite, TweenableSprite.X, Expo.INOUT, DURATION * 2, POS_LEFT).onComplete(onComplete).start(); text = "Expo : INOUT"; break;

			case 16: Tween.to(sprite, TweenableSprite.X, Circ.OUT, DURATION, POS_CENTER).onComplete(onComplete).start(); text = "Circ : OUT"; break;
			case 17: Tween.to(sprite, TweenableSprite.X, Circ.IN, DURATION, POS_RIGHT).onComplete(onComplete).start(); text = "Circ : IN"; break;
			case 18: Tween.to(sprite, TweenableSprite.X, Circ.INOUT, DURATION * 2, POS_LEFT).onComplete(onComplete).start(); text = "Circ : INOUT"; break;

			case 19: Tween.to(sprite, TweenableSprite.X, Sine.OUT, DURATION, POS_CENTER).onComplete(onComplete).start(); text = "Sine : OUT"; break;
			case 20: Tween.to(sprite, TweenableSprite.X, Sine.IN, DURATION, POS_RIGHT).onComplete(onComplete).start(); text = "Sine : IN"; break;
			case 21: Tween.to(sprite, TweenableSprite.X, Sine.INOUT, DURATION * 2, POS_LEFT).onComplete(onComplete).start(); text = "Sine : INOUT"; break;

			case 22: Tween.to(sprite, TweenableSprite.X, Back.OUT, DURATION, POS_CENTER).onComplete(onComplete).start(); text = "Back : OUT"; break;
			case 23: Tween.to(sprite, TweenableSprite.X, Back.IN, DURATION, POS_RIGHT).onComplete(onComplete).start(); text = "Back : IN"; break;
			case 24: Tween.to(sprite, TweenableSprite.X, Back.INOUT, DURATION * 2, POS_LEFT).onComplete(onComplete).start(); text = "Back : INOUT"; break;

			case 25: Tween.to(sprite, TweenableSprite.X, Bounce.OUT, DURATION * 2, POS_CENTER).onComplete(onComplete).start(); text = "Bounce : OUT"; break;
			case 26: Tween.to(sprite, TweenableSprite.X, Bounce.IN, DURATION * 2, POS_RIGHT).onComplete(onComplete).start(); text = "Bounce : IN"; break;
			case 27: Tween.to(sprite, TweenableSprite.X, Bounce.INOUT, DURATION * 4, POS_LEFT).onComplete(onComplete).start(); text = "Bounce : INOUT"; break;

			case 28: Tween.to(sprite, TweenableSprite.X, Elastic.OUT, DURATION * 2, POS_CENTER).onComplete(onComplete).start(); text = "Elastic : OUT"; break;
			case 29: Tween.to(sprite, TweenableSprite.X, Elastic.IN, DURATION * 2, POS_RIGHT).onComplete(onComplete).start(); text = "Elastic : IN"; break;
			case 30: Tween.to(sprite, TweenableSprite.X, Elastic.INOUT, DURATION * 4, POS_LEFT).onComplete(onComplete).start(); text = "Elastic : INOUT"; break;

			default: state = 0; startNextTween();
		}

		state += 1;
	}

	private TweenCompleteCallback onComplete = new TweenCompleteCallback() {
		@Override
		public void tweenComplete(Tween tween) {
			text = "Idle (click to continue)";
		}
	};

	private InputProcessor inputProcessor = new InputAdapter() {
		@Override
		public boolean touchDown(int x, int y, int pointer, int button) {
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
