package aurelienribon.tweenengine.equations;

import aurelienribon.tweenengine.TweenEquation;

/**
 * Easing equation based on Robert Penner's work:
 * http://robertpenner.com/easing/
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public abstract class Cubic extends TweenEquation {
	public static final Cubic IN = new Cubic() {
		@Override
		public final float compute(float t, float b, float c, float d) {
			return c*(t/=d)*t*t + b;
		}

		@Override
		public String toString() {
			return "Cubic.IN";
		}
	};

	public static final Cubic OUT = new Cubic() {
		@Override
		public final float compute(float t, float b, float c, float d) {
			return c*((t=t/d-1)*t*t + 1) + b;
		}

		@Override
		public String toString() {
			return "Cubic.OUT";
		}
	};

	public static final Cubic INOUT = new Cubic() {
		@Override
		public final float compute(float t, float b, float c, float d) {
			if ((t/=d/2) < 1) return c/2*t*t*t + b;
			return c/2*((t-=2)*t*t + 2) + b;
		}

		@Override
		public String toString() {
			return "Cubic.INOUT";
		}
	};
}