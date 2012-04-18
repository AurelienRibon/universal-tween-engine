package aurelienribon.tweenengine.equations;

import aurelienribon.tweenengine.TweenEquation;

/**
 * Easing equation based on Robert Penner's work:
 * http://robertpenner.com/easing/
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public abstract class Bounce extends TweenEquation {
	public static final Bounce IN = new Bounce() {
		@Override
		public final float compute(float t, float d) {
			return 1 - OUT.compute(d-t, d);
		}

		@Override
		public String toString() {
			return "Bounce.IN";
		}
	};

	public static final Bounce OUT = new Bounce() {
		@Override
		public final float compute(float t, float d) {
			if ((t/=d) < (1/2.75)) {
				return 7.5625f*t*t;
			} else if (t < (2/2.75)) {
				return 7.5625f*(t-=(1.5f/2.75f))*t + .75f;
			} else if (t < (2.5/2.75)) {
				return 7.5625f*(t-=(2.25f/2.75f))*t + .9375f;
			} else {
				return 7.5625f*(t-=(2.625f/2.75f))*t + .984375f;
			}
		}

		@Override
		public String toString() {
			return "Bounce.OUT";
		}
	};

	public static final Bounce INOUT = new Bounce() {
		@Override
		public final float compute(float t, float d) {
			if (t < d/2) return IN.compute(t*2, d) * .5f;
			else return OUT.compute(t*2-d, d) * .5f + 0.5f;
		}

		@Override
		public String toString() {
			return "Bounce.INOUT";
		}
	};
}