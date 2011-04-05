package aurelienribon.libgdx.tween.equations;

import aurelienribon.libgdx.tween.TweenEquation;

public class Expo {
	public static final TweenEquation IN = new TweenEquation() {
		@Override
		public float compute(float t, float b, float c, float d) {
			return (t==0) ? b : c * (float)Math.pow(2, 10 * (t/d - 1)) + b;
		}

		@Override
		public String toString() {
			return "Expo.IN";
		}
	};

	public static final TweenEquation OUT = new TweenEquation() {
		@Override
		public float compute(float t, float b, float c, float d) {
			return (t==d) ? b+c : c * (-(float)Math.pow(2, -10 * t/d) + 1) + b;
		}

		@Override
		public String toString() {
			return "Expo.OUT";
		}
	};

	public static final TweenEquation INOUT = new TweenEquation() {
		@Override
		public float compute(float t, float b, float c, float d) {
			if (t==0) return b;
			if (t==d) return b+c;
			if ((t/=d/2) < 1) return c/2 * (float)Math.pow(2, 10 * (t - 1)) + b;
			return c/2 * (-(float)Math.pow(2, -10 * --t) + 2) + b;
		}

		@Override
		public String toString() {
			return "Expo.INOUT";
		}
	};
}