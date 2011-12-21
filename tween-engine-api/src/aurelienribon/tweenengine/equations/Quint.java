package aurelienribon.tweenengine.equations;

import aurelienribon.tweenengine.TweenEquation;

/**
 * Easing equation based on Robert Penner's work:
 * http://robertpenner.com/easing/
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public abstract class Quint extends TweenEquation {
	public static final Quint IN = new Quint() {
		@Override
		public final float compute(float t, float b, float c, float d) {
			return c*(t/=d)*t*t*t*t + b;
		}

		@Override
		public String toString() {
			return "Quint.IN";
		}
	};

	public static final Quint OUT = new Quint() {
		@Override
		public final float compute(float t, float b, float c, float d) {
			return c*((t=t/d-1)*t*t*t*t + 1) + b;
		}

		@Override
		public String toString() {
			return "Quint.OUT";
		}
	};

	public static final Quint INOUT = new Quint() {
		@Override
		public final float compute(float t, float b, float c, float d) {
			if ((t/=d/2) < 1) return c/2*t*t*t*t*t + b;
			return c/2*((t-=2)*t*t*t*t + 2) + b;
		}

		@Override
		public String toString() {
			return "Quint.INOUT";
		}
	};
}