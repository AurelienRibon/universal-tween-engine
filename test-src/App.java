import aurelienribon.libgdx.tween.Tween;
import aurelienribon.libgdx.tween.TweenTypes;
import aurelienribon.libgdx.tween.Tweenable;
import aurelienribon.libgdx.tween.equations.Bounce;
import aurelienribon.libgdx.tween.equations.Elastic;
import aurelienribon.libgdx.tween.equations.Linear;
import aurelienribon.libgdx.tween.equations.Quad;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.g2d.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer;

public class App implements ApplicationListener {
	private ImmediateModeRenderer imr;
	private OrthographicCamera camera;
	private MyObject obj;

	private static final int TWEEN_TINT = 1;

	@Override
	public void create() {
		imr = new ImmediateModeRenderer();
		camera = new OrthographicCamera();
		camera.setViewport(15, 15);

		obj = new MyObject();

		Tween.to(obj, TweenTypes.X, Quad.INOUT, 3, 1000);
		Tween.to(obj, TweenTypes.Y, Bounce.OUT, 3, 1000).delay(1000);

		Tween.to(obj, TweenTypes.X, Elastic.OUT, 0, 1500).delay(2000);
		Tween.to(obj, TweenTypes.Y, Elastic.OUT, 0, 1500).delay(2000);

		Tween.to(obj, TweenTypes.ROTATION, Quad.INOUT, 360, 1000).delay(3500);

		Tween.to(obj, TweenTypes.OPACITY, Quad.INOUT, 0, 1000).delay(4500);
		Tween.to(obj, TweenTypes.OPACITY, Quad.INOUT, 1, 1000).delay(5500);

		Tween.to(obj, TWEEN_TINT, Linear.INOUT, 1, 1000).delay(6500);
	}

	@Override
	public void resume() {
	}

	@Override
	public void render() {
		Tween.update();

		GL11 gl = Gdx.gl11;
		gl.glClearColor(1, 1, 1, 1);
		gl.glClear(gl.GL_COLOR_BUFFER_BIT);
		gl.glEnable(gl.GL_BLEND);
		gl.glBlendFunc(gl.GL_SRC_ALPHA, gl.GL_ONE_MINUS_SRC_ALPHA);

		camera.setMatrices();
		obj.render(imr);
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void dispose() {
	}

	private class MyObject implements Tweenable {
		private float x = 0;
		private float y = 0;
		private float rotation = 0;
		private float alpha = 1;
		private float tint = 0;

		public void render(ImmediateModeRenderer imr) {
			Gdx.gl11.glPushMatrix();
			Gdx.gl11.glTranslatef(x, y, 0);
			Gdx.gl11.glRotatef(rotation, 0, 0, 1);
			imr.begin(GL11.GL_TRIANGLE_FAN);
			imr.color(0.2f, tint, 0.2f, alpha); imr.vertex(-2, -2, 0);
			imr.color(1 - tint, 0.8f, 0.2f, alpha); imr.vertex(+2, -2, 0);
			imr.color(0.2f, 0.8f, tint, alpha); imr.vertex(+2 ,+2, 0);
			imr.color(0.2f, 1 - tint, 0.2f, alpha); imr.vertex(-2, +2, 0);
			imr.end();
			Gdx.gl11.glPopMatrix();
		}

		@Override
		public float getTweenValue(int tweenType) {
			switch (tweenType) {
				case TweenTypes.X: return x;
				case TweenTypes.Y: return y;
				case TweenTypes.ROTATION: return rotation;
				case TweenTypes.OPACITY: return alpha;
				case TWEEN_TINT: return tint;
				default: assert false;
			}
			return 0;
		}

		@Override
		public void tweenUpdated(int tweenType, float newValue) {
			switch (tweenType) {
				case TweenTypes.X: x = newValue; break;
				case TweenTypes.Y: y = newValue; break;
				case TweenTypes.ROTATION: rotation = newValue; break;
				case TweenTypes.OPACITY: alpha = newValue; break;
				case TWEEN_TINT: tint = newValue; break;
				default: assert false;
			}
		}
	}
}
