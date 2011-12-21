package aurelienribon.tweenengine.equations;

import aurelienribon.tweenengine.TweenEquation;

/**
 * Easing equation based on Robert Penner's work:
 * http://robertpenner.com/easing/
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public abstract class Quad extends TweenEquation {
	public static final Quad IN = new Quad() {
		@Override
		public final float compute(float t, float b, float c, float d) {
			return c*(t/=d)*t + b;
		}

		@Override
		public String toString() {
			return "Quad.IN";
		}
	};

	public static final Quad OUT = new Quad() {
		@Override
		public final float compute(float t, float b, float c, float d) {
			return -c*(t/=d)*(t-2) + b;
		}

		@Override
		public String toString() {
			return "Quad.OUT";
		}
	};

	public static final Quad INOUT = new Quad() {
		@Override
		public final float compute(float t, float b, float c, float d) {
			if ((t/=d/2) < 1) return c/2*t*t + b;
			return -c/2 * ((--t)*(t-2) - 1) + b;
		}

		@Override
		public String toString() {
			return "Quad.INOUT";
		}
	};
}