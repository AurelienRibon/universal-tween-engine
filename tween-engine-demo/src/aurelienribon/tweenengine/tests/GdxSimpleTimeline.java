package aurelienribon.tweenengine.tests;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenaccessors.gdx.SpriteAccessor;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.equations.Quad;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GdxSimpleTimeline implements ApplicationListener {
	public static void start() {
		new LwjglApplication(new GdxSimpleTimeline(), "", 500, 200, false);
	}

	private OrthographicCamera camera;
	private SpriteBatch sb;
	private BitmapFont font;
	private Sprite sprite1;

	private TweenManager tweenManager;

	@Override
	public void create() {
		// GDX stuff...

		float ratio = (float)Gdx.graphics.getWidth() / (float)Gdx.graphics.getHeight();
		camera = new OrthographicCamera(10, 10/ratio);
		sb = new SpriteBatch();
		font = new BitmapFont();
		font.setColor(Color.BLACK);

		sprite1 = new Sprite(new Texture(Gdx.files.internal("data/logo1.png")));
		sprite1.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		sprite1.setSize(2, 2);
		sprite1.setOrigin(1, 1);
		sprite1.setPosition(-2, 0);

		// Tween engine setup

		Tween.enablePooling(true);
		Tween.registerAccessor(Sprite.class, new SpriteAccessor());

		// Tween manager creation

		tweenManager = new TweenManager();

		// Let's make our sprite move !

		Timeline.createSequence()
			.push(Tween.to(sprite1, SpriteAccessor.POSITION_XY, 700).target(2, 0).ease(Quad.OUT))
			.start(tweenManager);
	}

	@Override
	public void render() {
		// TweenManager update

		int delta = (int) (Gdx.graphics.getDeltaTime() * 1000);
		tweenManager.update(delta);

		// Gdx stuff...
		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();

		GL10 gl = Gdx.gl10;
		gl.glClearColor(1, 1, 1, 1);
		gl.glClear(gl.GL_COLOR_BUFFER_BIT);

		sb.setProjectionMatrix(camera.combined);
		sb.begin();
		sprite1.draw(sb);
		sb.end();

		sb.getProjectionMatrix().setToOrtho2D(0, 0, w, h);
		sb.begin();
		font.setColor(0.5f, 0.5f, 0.5f, 1);
		sb.end();
	}

	@Override public void resize(int width, int height) {}
	@Override public void pause() {}
	@Override public void resume() {}
	@Override public void dispose() {}
}
