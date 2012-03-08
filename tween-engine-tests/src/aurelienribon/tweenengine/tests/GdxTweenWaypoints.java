package aurelienribon.tweenengine.tests;

import aurelienribon.tweenaccessors.gdx.SpriteAccessor;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.TweenPath;
import aurelienribon.tweenengine.TweenPaths;
import aurelienribon.tweenengine.equations.Quad;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class GdxTweenWaypoints implements ApplicationListener {
	public static void start() {
		new LwjglApplication(new GdxTweenWaypoints(), "", 500, 500, false);
	}

	private OrthographicCamera camera;
	private SpriteBatch sb;
	private BitmapFont font;
	private Sprite sprite;

	private TweenManager tweenManager;

	@Override
	public void create() {
		// GDX stuff...

		camera = new OrthographicCamera(10, 10);
		camera.position.set(1, 1, 0);
		camera.update();

		sb = new SpriteBatch();
		font = new BitmapFont();
		font.setColor(Color.BLACK);

		sprite = new Sprite(new Texture(Gdx.files.internal("data/logo1.png")));
		sprite.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		sprite.setSize(2, 2);
		sprite.setOrigin(1, 1);
		sprite.setPosition(0, 0);

		// Tween engine setup

		Tween.setWaypointsLimit(2);
		Tween.registerAccessor(Sprite.class, new SpriteAccessor());

		// Tween manager creation

		tweenManager = new TweenManager();

		// Let's make our sprite move !

		Tween.to(sprite, SpriteAccessor.POSITION_XY, 2.0f)
			.waypoint(2, 2)
			.waypoint(2, -2)
			.target(0, 0)
			.ease(Quad.INOUT)
			.path(TweenPaths.catmullRom)
			.repeatYoyo(-1, 0.2f)
			.delay(0.5f)
			.start(tweenManager);
	}

	@Override
	public void render() {
		// Tween manager update

		tweenManager.update(Gdx.graphics.getDeltaTime());

		// Gdx stuff...

		GL10 gl = Gdx.gl10;
		gl.glClearColor(1, 1, 1, 1);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		sb.setProjectionMatrix(camera.combined);
		sb.begin();
		sprite.draw(sb);
		sb.end();
	}

	@Override public void resize(int width, int height) {}
	@Override public void pause() {}
	@Override public void resume() {}
	@Override public void dispose() {}
}
