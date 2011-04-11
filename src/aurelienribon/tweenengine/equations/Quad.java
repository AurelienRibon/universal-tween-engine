package aurelienribon.tweenengine.equations;

import aurelienribon.tweenengine.TweenEquation;

public class Quad {
	public static final TweenEquation IN = new TweenEquation() {
		@Override
		public float compute(float t, float b, float c, float d) {
			return c*(t/=d)*t + b;
		}

		@Override
		public String toString() {
			return "Quad.IN";
		}
	};

	public static final TweenEquation OUT = new TweenEquation() {
		@Override
		public float compute(float t, float b, float c, float d) {
			return -c*(t/=d)*(t-2) + b;
		}

		@Override
		public String toString() {
			return "Quad.OUT";
		}
	};

	public static final TweenEquation INOUT = new TweenEquation() {
		@Override
		public float compute(float t, float b, float c, float d) {
			if ((t/=d/2) < 1) return c/2*t*t + b;
			return -c/2 * ((--t)*(t-2) - 1) + b;
		}

		@Override
		public String toString() {
			return "Quad.INOUT";
		}
	};
}