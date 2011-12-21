package aurelienribon.tweenengine.equations;

import aurelienribon.tweenengine.TweenEquation;

/**
 * Easing equation based on Robert Penner's work:
 * http://robertpenner.com/easing/
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public abstract class Sine extends TweenEquation {
	private static final float PI = 3.14159265f;
	
	public static final Sine IN = new Sine() {
		@Override
		public final float compute(float t, float b, float c, float d) {
			return -c * (float)Math.cos(t/d * (PI/2)) + c + b;
		}

		@Override
		public String toString() {
			return "Sine.IN";
		}
	};

	public static final Sine OUT = new Sine() {
		@Override
		public final float compute(float t, float b, float c, float d) {
			return c * (float)Math.sin(t/d * (PI/2)) + b;
		}

		@Override
		public String toString() {
			return "Sine.OUT";
		}
	};

	public static final Sine INOUT = new Sine() {
		@Override
		public final float compute(float t, float b, float c, float d) {
			return -c/2 * ((float)Math.cos(PI*t/d) - 1) + b;
		}

		@Override
		public String toString() {
			return "Sine.INOUT";
		}
	};
}