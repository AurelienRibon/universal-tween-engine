package aurelienribon.tweenengine.equations;

import aurelienribon.tweenengine.TweenEquation;

/**
 * Easing equations based on Robert Penner's work:
 * http://robertpenner.com/easing/
 * @author Aurelien Ribon (aurelien.ribon@gmail.com)
 */
public class Circ {
	public static final TweenEquation IN = new TweenEquation() {
		@Override
		public float compute(float t, float b, float c, float d) {
			return -c * ((float)Math.sqrt(1 - (t/=d)*t) - 1) + b;
		}

		@Override
		public String toString() {
			return "Circ.IN";
		}
	};

	public static final TweenEquation OUT = new TweenEquation() {
		@Override
		public float compute(float t, float b, float c, float d) {
			return c * (float)Math.sqrt(1 - (t=t/d-1)*t) + b;
		}

		@Override
		public String toString() {
			return "Circ.OUT";
		}
	};

	public static final TweenEquation INOUT = new TweenEquation() {
		@Override
		public float compute(float t, float b, float c, float d) {
			if ((t/=d/2) < 1) return -c/2 * ((float)Math.sqrt(1 - t*t) - 1) + b;
			return c/2 * ((float)Math.sqrt(1 - (t-=2)*t) + 1) + b;
		}

		@Override
		public String toString() {
			return "Circ.INOUT";
		}
	};
}