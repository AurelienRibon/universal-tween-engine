package aurelienribon.tweenengine.equations;

import aurelienribon.tweenengine.TweenEquation;

/**
 * Easing equations based on Robert Penner's work:
 * http://robertpenner.com/easing/
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class Sine {
	private static final float PI = 3.14159265f;
	
	public static final TweenEquation IN = new TweenEquation() {
		@Override
		public final float compute(float t, float b, float c, float d) {
			return -c * (float)Math.cos(t/d * (PI/2)) + c + b;
		}

		@Override
		public String toString() {
			return "Sine.IN";
		}
	};

	public static final TweenEquation OUT = new TweenEquation() {
		@Override
		public final float compute(float t, float b, float c, float d) {
			return c * (float)Math.sin(t/d * (PI/2)) + b;
		}

		@Override
		public String toString() {
			return "Sine.OUT";
		}
	};

	public static final TweenEquation INOUT = new TweenEquation() {
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