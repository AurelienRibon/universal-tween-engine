package aurelienribon.libgdx.tween.equations;

import aurelienribon.libgdx.tween.TweenEquation;
import com.badlogic.gdx.utils.MathUtils;

public class Sine {
	public static final TweenEquation IN = new TweenEquation() {
		@Override
		public float compute(float t, float b, float c, float d) {
			return -c * MathUtils.cos(t/d * (MathUtils.PI/2)) + c + b;
		}

		@Override
		public String toString() {
			return "Sine.IN";
		}
	};

	public static final TweenEquation OUT = new TweenEquation() {
		@Override
		public float compute(float t, float b, float c, float d) {
			return c * MathUtils.sin(t/d * (MathUtils.PI/2)) + b;
		}

		@Override
		public String toString() {
			return "Sine.OUT";
		}
	};

	public static final TweenEquation INOUT = new TweenEquation() {
		@Override
		public float compute(float t, float b, float c, float d) {
			return -c/2 * (MathUtils.cos(MathUtils.PI*t/d) - 1) + b;
		}

		@Override
		public String toString() {
			return "Sine.INOUT";
		}
	};
}