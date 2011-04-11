package aurelienribon.tweenengine.equations;

import aurelienribon.tweenengine.TweenEquation;

public class Linear {
	public static final TweenEquation INOUT = new TweenEquation() {
		@Override
		public float compute(float t, float b, float c, float d) {
			return c * t/d + b;
		}

		@Override
		public String toString() {
			return "Linear.INOUT";
		}
	};
}
