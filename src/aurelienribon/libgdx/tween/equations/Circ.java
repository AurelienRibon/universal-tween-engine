package aurelienribon.libgdx.tween.equations;

import aurelienribon.libgdx.tween.TweenEquation;

public class Circ {
	public static final TweenEquation IN = new TweenEquation() {
		@Override
		public float compute(float t, float b, float c, float d) {
			return -c * ((float)Math.sqrt(1 - (t/=d)*t) - 1) + b;
		}
	};

	public static final TweenEquation OUT = new TweenEquation() {
		@Override
		public float compute(float t, float b, float c, float d) {
			return c * (float)Math.sqrt(1 - (t=t/d-1)*t) + b;
		}
	};

	public static final TweenEquation INOUT = new TweenEquation() {
		@Override
		public float compute(float t, float b, float c, float d) {
			if ((t/=d/2) < 1) return -c/2 * ((float)Math.sqrt(1 - t*t) - 1) + b;
			return c/2 * ((float)Math.sqrt(1 - (t-=2)*t) + 1) + b;
		}
	};
}