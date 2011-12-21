package aurelienribon.tweenengine.equations;

import aurelienribon.tweenengine.TweenEquation;

/**
 * Easing equation based on Robert Penner's work:
 * http://robertpenner.com/easing/
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public abstract class Circ extends TweenEquation {
	public static final Circ IN = new Circ() {
		@Override
		public final float compute(float t, float b, float c, float d) {
			return -c * ((float)Math.sqrt(1 - (t/=d)*t) - 1) + b;
		}

		@Override
		public String toString() {
			return "Circ.IN";
		}
	};

	public static final Circ OUT = new Circ() {
		@Override
		public final float compute(float t, float b, float c, float d) {
			return c * (float)Math.sqrt(1 - (t=t/d-1)*t) + b;
		}

		@Override
		public String toString() {
			return "Circ.OUT";
		}
	};

	public static final Circ INOUT = new Circ() {
		@Override
		public final float compute(float t, float b, float c, float d) {
			if ((t/=d/2) < 1) return -c/2 * ((float)Math.sqrt(1 - t*t) - 1) + b;
			return c/2 * ((float)Math.sqrt(1 - (t-=2)*t) + 1) + b;
		}

		@Override
		public String toString() {
			return "Circ.INOUT";
		}
	};
}