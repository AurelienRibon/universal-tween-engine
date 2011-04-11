package aurelienribon.tweenengine.equations;

import aurelienribon.tweenengine.TweenEquation;

public class Elastic {
	private static final float PI = 3.14159265f;

	public static final TweenEquation IN = new TweenEquation() {
		@Override
		public float compute(float t, float b, float c, float d) {
			if (t==0) return b;  if ((t/=d)==1) return b+c;
			float p = d*.3f;
			float a = c;
			float s = p/4;
			return -(a*(float)Math.pow(2,10*(t-=1)) * (float)Math.sin( (t*d-s)*(2*PI)/p )) + b;
		}

		@Override
		public String toString() {
			return "Elastic.IN";
		}
	};

	public static final TweenEquation OUT = new TweenEquation() {
		@Override
		public float compute(float t, float b, float c, float d) {
			if (t==0) return b;  if ((t/=d)==1) return b+c;
			float p = d*.3f;
			float a = c;
			float s = p/4;
			return (a*(float)Math.pow(2,-10*t) * (float)Math.sin( (t*d-s)*(2*PI)/p ) + c + b);
		}

		@Override
		public String toString() {
			return "Elastic.OUT";
		}
	};

	public static final TweenEquation INOUT = new TweenEquation() {
		@Override
		public float compute(float t, float b, float c, float d) {
			if (t==0) return b;  if ((t/=d/2)==2) return b+c;
			float p = d*(.3f*1.5f);
			float a = c;
			float s = p/4;
			if (t < 1) return -.5f*(a*(float)Math.pow(2,10*(t-=1)) * (float)Math.sin( (t*d-s)*(2*(float)Math.PI)/p )) + b;
			return a*(float)Math.pow(2,-10*(t-=1)) * (float)Math.sin( (t*d-s)*(2*(float)Math.PI)/p )*.5f + c + b;
		}

		@Override
		public String toString() {
			return "Elastic.INOUT";
		}
	};
}