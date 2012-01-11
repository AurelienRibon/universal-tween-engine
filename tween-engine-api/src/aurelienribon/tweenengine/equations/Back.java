package aurelienribon.tweenengine.equations;

import aurelienribon.tweenengine.TweenEquation;

/**
 * Easing equation based on Robert Penner's work:
 * http://robertpenner.com/easing/
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public abstract class Back extends TweenEquation {
	public static final Back IN = new Back() {
		@Override
		public final float compute(float t, float b, float c, float d) {
			float s = param_s;
			return c*(t/=d)*t*((s+1)*t - s) + b;
		}

		@Override
		public String toString() {
			return "Back.IN";
		}
	};

	public static final Back OUT = new Back() {
		@Override
		public final float compute(float t, float b, float c, float d) {
			float s = param_s;
			return c*((t=t/d-1)*t*((s+1)*t + s) + 1) + b;
		}

		@Override
		public String toString() {
			return "Back.OUT";
		}
	};

	public static final Back INOUT = new Back() {
		@Override
		public final float compute(float t, float b, float c, float d) {
			float s = param_s;
			if ((t/=d/2) < 1) return c/2*(t*t*(((s*=(1.525f))+1)*t - s)) + b;
			return c/2*((t-=2)*t*(((s*=(1.525f))+1)*t + s) + 2) + b;
		}

		@Override
		public String toString() {
			return "Back.INOUT";
		}
	};

	// -------------------------------------------------------------------------

	protected float param_s = 1.70158f;

	public Back s(float s) {
		param_s = s;
		return this;
	}
}