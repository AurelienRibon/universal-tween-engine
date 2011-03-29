package aurelienribon.libgdx.tween.equations;

import aurelienribon.libgdx.tween.TweenEquation;

public class Linear {
	public static final TweenEquation INOUT = new TweenEquation() {
		@Override
		public float compute(float t, float b, float c, float d) {
			return (c-b) * t/d + b;
		}
	};
}
