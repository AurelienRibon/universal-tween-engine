package aurelienribon.launcher;

import aurelienribon.accessors.SpriteAccessor;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import java.util.Random;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public abstract class Test {
	private final TweenManager tweenManager = new TweenManager();
	private final TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("data/test/pack"));
	private final Texture backgroundTex = new Texture(Gdx.files.internal("data/test/background.png"));
	private final Sprite veil = atlas.createSprite("white");

	protected final OrthographicCamera camera = new OrthographicCamera();
	protected final SpriteBatch batch = new SpriteBatch();
	protected final BitmapFont font = new BitmapFont();
	protected final Random rand = new Random();
	protected Sprite[] sprites;

	// -------------------------------------------------------------------------
	// Abstract API
	// -------------------------------------------------------------------------

	public abstract String getTitle();
	public abstract String getInfo();
	public abstract InputProcessor getInput();
	protected abstract void initializeOverride();
	protected abstract void disposeOverride();
	protected abstract void renderOverride();

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	public void initialize() {
		float wpw = 10;
		float wph = wpw * Gdx.graphics.getHeight() / Gdx.graphics.getWidth();

		camera.viewportWidth = wpw;
		camera.viewportHeight = wph;
		camera.update();

		font.setColor(Color.BLACK);

		backgroundTex.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

		initializeOverride();

		veil.setSize(wpw, wph);
		veil.setPosition(-wpw/2, -wph/2);
		Tween.set(veil, SpriteAccessor.OPACITY).target(1).start(tweenManager);
		Tween.to(veil, SpriteAccessor.OPACITY, 0.7f).target(0).start(tweenManager);
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
		batch.draw(backgroundTex, 0, 0, 0, 0, w, h);
		batch.enableBlending();
		batch.end();

		renderOverride();

		if (veil.getColor().a > 0.1f) {
			batch.setProjectionMatrix(camera.combined);
			batch.begin();
			veil.draw(batch);
			batch.end();
		}
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	protected void createSprites(int cnt) {
		sprites = new Sprite[cnt];
		for (int i=0; i<cnt; i++) {
			int idx = rand.nextInt(4) + 1;
			sprites[i] = atlas.createSprite("sprite" + idx);
			sprites[i].getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
			sprites[i].setSize(1f, 1f * sprites[i].getWidth() / sprites[i].getHeight());
			sprites[i].setOrigin(sprites[i].getWidth()/2, sprites[i].getHeight()/2);
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
