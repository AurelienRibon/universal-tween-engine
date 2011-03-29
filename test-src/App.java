import aurelienribon.libgdx.tween.Tween;
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
	private static final int DELAY = 500;

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
				Tween.to(sprite, TweenableSprite.ORIG_X, Linear.INOUT, POS_RIGHT, DELAY);
				Tween.to(sprite, TweenableSprite.ORIG_X, Linear.INOUT, POS_LEFT, DELAY).delay(DELAY).onComplete(onComplete);
				text = "LINEAR : INOUT";
				break;

			case 1: Tween.to(sprite, TweenableSprite.ORIG_X, Quad.OUT, POS_CENTER, DELAY).onComplete(onComplete); text = "Quad : OUT"; break;
			case 2: Tween.to(sprite, TweenableSprite.ORIG_X, Quad.IN, POS_RIGHT, DELAY).onComplete(onComplete); text = "Quad : IN"; break;
			case 3: Tween.to(sprite, TweenableSprite.ORIG_X, Quad.INOUT, POS_LEFT, DELAY * 2).onComplete(onComplete); text = "Quad : INOUT"; break;

			case 4: Tween.to(sprite, TweenableSprite.ORIG_X, Cubic.OUT, POS_CENTER, DELAY).onComplete(onComplete); text = "Cubic : OUT"; break;
			case 5: Tween.to(sprite, TweenableSprite.ORIG_X, Cubic.IN, POS_RIGHT, DELAY).onComplete(onComplete); text = "Cubic : IN"; break;
			case 6: Tween.to(sprite, TweenableSprite.ORIG_X, Cubic.INOUT, POS_LEFT, DELAY * 2).onComplete(onComplete); text = "Cubic : INOUT"; break;

			case 7: Tween.to(sprite, TweenableSprite.ORIG_X, Quart.OUT, POS_CENTER, DELAY).onComplete(onComplete); text = "Quart : OUT"; break;
			case 8: Tween.to(sprite, TweenableSprite.ORIG_X, Quart.IN, POS_RIGHT, DELAY).onComplete(onComplete); text = "Quart : IN"; break;
			case 9: Tween.to(sprite, TweenableSprite.ORIG_X, Quart.INOUT, POS_LEFT, DELAY * 2).onComplete(onComplete); text = "Quart : INOUT"; break;

			case 10: Tween.to(sprite, TweenableSprite.ORIG_X, Quint.OUT, POS_CENTER, DELAY).onComplete(onComplete); text = "Quint : OUT"; break;
			case 11: Tween.to(sprite, TweenableSprite.ORIG_X, Quint.IN, POS_RIGHT, DELAY).onComplete(onComplete); text = "Quint : IN"; break;
			case 12: Tween.to(sprite, TweenableSprite.ORIG_X, Quint.INOUT, POS_LEFT, DELAY * 2).onComplete(onComplete); text = "Quint : INOUT"; break;

			case 13: Tween.to(sprite, TweenableSprite.ORIG_X, Expo.OUT, POS_CENTER, DELAY).onComplete(onComplete); text = "Expo : OUT"; break;
			case 14: Tween.to(sprite, TweenableSprite.ORIG_X, Expo.IN, POS_RIGHT, DELAY).onComplete(onComplete); text = "Expo : IN"; break;
			case 15: Tween.to(sprite, TweenableSprite.ORIG_X, Expo.INOUT, POS_LEFT, DELAY * 2).onComplete(onComplete); text = "Expo : INOUT"; break;

			case 16: Tween.to(sprite, TweenableSprite.ORIG_X, Circ.OUT, POS_CENTER, DELAY).onComplete(onComplete); text = "Circ : OUT"; break;
			case 17: Tween.to(sprite, TweenableSprite.ORIG_X, Circ.IN, POS_RIGHT, DELAY).onComplete(onComplete); text = "Circ : IN"; break;
			case 18: Tween.to(sprite, TweenableSprite.ORIG_X, Circ.INOUT, POS_LEFT, DELAY * 2).onComplete(onComplete); text = "Circ : INOUT"; break;

			case 19: Tween.to(sprite, TweenableSprite.ORIG_X, Sine.OUT, POS_CENTER, DELAY).onComplete(onComplete); text = "Sine : OUT"; break;
			case 20: Tween.to(sprite, TweenableSprite.ORIG_X, Sine.IN, POS_RIGHT, DELAY).onComplete(onComplete); text = "Sine : IN"; break;
			case 21: Tween.to(sprite, TweenableSprite.ORIG_X, Sine.INOUT, POS_LEFT, DELAY * 2).onComplete(onComplete); text = "Sine : INOUT"; break;

			case 22: Tween.to(sprite, TweenableSprite.ORIG_X, Back.OUT, POS_CENTER, DELAY).onComplete(onComplete); text = "Back : OUT"; break;
			case 23: Tween.to(sprite, TweenableSprite.ORIG_X, Back.IN, POS_RIGHT, DELAY).onComplete(onComplete); text = "Back : IN"; break;
			case 24: Tween.to(sprite, TweenableSprite.ORIG_X, Back.INOUT, POS_LEFT, DELAY * 2).onComplete(onComplete); text = "Back : INOUT"; break;

			case 25: Tween.to(sprite, TweenableSprite.ORIG_X, Bounce.OUT, POS_CENTER, DELAY * 2).onComplete(onComplete); text = "Bounce : OUT"; break;
			case 26: Tween.to(sprite, TweenableSprite.ORIG_X, Bounce.IN, POS_RIGHT, DELAY * 2).onComplete(onComplete); text = "Bounce : IN"; break;
			case 27: Tween.to(sprite, TweenableSprite.ORIG_X, Bounce.INOUT, POS_LEFT, DELAY * 4).onComplete(onComplete); text = "Bounce : INOUT"; break;

			case 28: Tween.to(sprite, TweenableSprite.ORIG_X, Elastic.OUT, POS_CENTER, DELAY * 2).onComplete(onComplete); text = "Elastic : OUT"; break;
			case 29: Tween.to(sprite, TweenableSprite.ORIG_X, Elastic.IN, POS_RIGHT, DELAY * 2).onComplete(onComplete); text = "Elastic : IN"; break;
			case 30: Tween.to(sprite, TweenableSprite.ORIG_X, Elastic.INOUT, POS_LEFT, DELAY * 4).onComplete(onComplete); text = "Elastic : INOUT"; break;

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
