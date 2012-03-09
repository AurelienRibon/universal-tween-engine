package aurelienribon.gdxtests;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import java.util.Random;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public abstract class Test {
	protected final Random rand = new Random();
	protected final TweenManager tweenManager = new TweenManager();
	protected OrthographicCamera camera;
	protected SpriteBatch batch;
	protected BitmapFont font;
	protected Sprite[] sprites;

	private final MutableFloat veilOpacity = new MutableFloat(1);
	private Texture veilTexture;
	private Texture backgroundTexture;

	public abstract String getTitle();
	public abstract String getInfo();
	public abstract InputProcessor getInput();
	protected abstract void initializeOverride();
	protected abstract void disposeOverride();
	protected abstract void renderOverride();

	public void initialize() {
		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();

		camera = new OrthographicCamera(10, 10 * h / w);
		batch = new SpriteBatch();
		font = new BitmapFont();
		font.setColor(Color.BLACK);

		backgroundTexture = new Texture(Gdx.files.internal("data/background.png"));
		backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		veilTexture = new Texture(Gdx.files.internal("data/white.png"));
		veilTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

		initializeOverride();

		veilOpacity.setValue(0);
		Tween.to(veilOpacity, 0, 0.7f).target(0).start(tweenManager);
	}

	public void dispose() {
		disposeOverride();
	}

	public void render() {
		tweenManager.update(Gdx.graphics.getDeltaTime());

		GL10 gl = Gdx.gl10;
		gl.glClearColor(1, 1, 1, 1);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();

		batch.getProjectionMatrix().setToOrtho2D(0, 0, w, h);
		batch.begin();
		batch.disableBlending();
		batch.draw(backgroundTexture, 0, 0, 0, 0, w, h);
		batch.enableBlending();
		batch.end();

		renderOverride();

		if (veilOpacity.floatValue() > 0.05f) {
			batch.getProjectionMatrix().setToOrtho2D(0, 0, w, h);
			batch.begin();
			batch.setColor(1, 1, 1, veilOpacity.floatValue());
			batch.draw(veilTexture, 0, 0, 0, 0, w, h);
			batch.end();
		}
	}

	protected void createSprites(int cnt) {
		sprites = new Sprite[cnt];
		for (int i=0; i<cnt; i++) {
			int idx = rand.nextInt(4) + 1;
			Texture.setEnforcePotImages(false);
			Texture tex = new Texture(Gdx.files.internal("data/sprite" + idx + ".png"));
			Texture.setEnforcePotImages(true);
			sprites[i] = new Sprite(tex);
			sprites[i].getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
			sprites[i].setSize(1f, 1f * tex.getWidth() / tex.getHeight());
			sprites[i].setOrigin(0.5f, 0.5f);
		}
	}

	protected void center(Sprite sp, float x, float y) {
		sp.setPosition(x - sp.getWidth()/2, y - sp.getHeight()/2);
	}

	private final Vector2 v2 = new Vector2();
	private final Vector3 v3 = new Vector3();
	protected Vector2 touch2world(int x, int y) {
		v3.set(x, y, 0);
		camera.unproject(v3);
		return v2.set(v3.x, v3.y);
	}
}
