package aurelienribon.libgdx.tween.equations;

import aurelienribon.libgdx.tween.TweenEquation;

public class Bounce {
	public static final TweenEquation IN = new TweenEquation() {
		@Override
		public float compute(float t, float b, float c, float d) {
			return c - OUT.compute(d-t, 0, c, d) + b;
		}
	};

	public static final TweenEquation OUT = new TweenEquation() {
		@Override
		public float compute(float t, float b, float c, float d) {
			if ((t/=d) < (1/2.75)) {
				return c*(7.5625f*t*t) + b;
			} else if (t < (2/2.75)) {
				return c*(7.5625f*(t-=(1.5f/2.75f))*t + .75f) + b;
			} else if (t < (2.5/2.75)) {
				return c*(7.5625f*(t-=(2.25f/2.75f))*t + .9375f) + b;
			} else {
				return c*(7.5625f*(t-=(2.625f/2.75f))*t + .984375f) + b;
			}
		}
	};

	public static final TweenEquation INOUT = new TweenEquation() {
		@Override
		public float compute(float t, float b, float c, float d) {
			if (t < d/2) return IN.compute(t*2, 0, c, d) * .5f + b;
			else return OUT.compute(t*2-d, 0, c, d) * .5f + c*.5f + b;
		}
	};
}