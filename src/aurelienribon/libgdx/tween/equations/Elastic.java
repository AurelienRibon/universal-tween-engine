package aurelienribon.libgdx.tween.equations;

import aurelienribon.libgdx.tween.TweenEquation;
import com.badlogic.gdx.utils.MathUtils;

public class Elastic {
	public static final TweenEquation IN = new TweenEquation() {
		@Override
		public float compute(float t, float b, float c, float d) {
			if (t==0) return b;  if ((t/=d)==1) return b+c;
			float p = d*.3f;
			float a = c;
			float s = p/4;
			return -(a*(float)Math.pow(2,10*(t-=1)) * MathUtils.sin( (t*d-s)*(2*MathUtils.PI)/p )) + b;
		}
	};

	public static final TweenEquation OUT = new TweenEquation() {
		@Override
		public float compute(float t, float b, float c, float d) {
			if (t==0) return b;  if ((t/=d)==1) return b+c;
			float p = d*.3f;
			float a = c;
			float s = p/4;
			return (a*(float)Math.pow(2,-10*t) * MathUtils.sin( (t*d-s)*(2*MathUtils.PI)/p ) + c + b);
		}
	};

	public static final TweenEquation INOUT = new TweenEquation() {
		@Override
		public float compute(float t, float b, float c, float d) {
			if (t==0) return b;  if ((t/=d)==1) return b+c;
			float p = d*(.3f*1.5f);
			float a = c;
			float s = p/4;
			if (t < 1) return -.5f*(a*(float)Math.pow(2,10*(t-=1)) * MathUtils.sin( (t*d-s)*(2*MathUtils.PI)/p )) + b;
			return a*(float)Math.pow(2,-10*(t-=1)) * MathUtils.sin( (t*d-s)*(2*MathUtils.PI)/p )*.5f + c + b;
		}
	};
}