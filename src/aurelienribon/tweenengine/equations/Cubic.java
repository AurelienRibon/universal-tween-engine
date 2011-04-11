package aurelienribon.tweenengine.equations;

import aurelienribon.tweenengine.TweenEquation;

public class Cubic {
	public static final TweenEquation IN = new TweenEquation() {
		@Override
		public float compute(float t, float b, float c, float d) {
			return c*(t/=d)*t*t + b;
		}

		@Override
		public String toString() {
			return "Cubic.IN";
		}
	};

	public static final TweenEquation OUT = new TweenEquation() {
		@Override
		public float compute(float t, float b, float c, float d) {
			return c*((t=t/d-1)*t*t + 1) + b;
		}

		@Override
		public String toString() {
			return "Cubic.OUT";
		}
	};

	public static final TweenEquation INOUT = new TweenEquation() {
		@Override
		public float compute(float t, float b, float c, float d) {
			if ((t/=d/2) < 1) return c/2*t*t*t + b;
			return c/2*((t-=2)*t*t + 2) + b;
		}

		@Override
		public String toString() {
			return "Cubic.INOUT";
		}
	};
}