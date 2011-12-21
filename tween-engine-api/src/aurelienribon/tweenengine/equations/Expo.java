package aurelienribon.tweenengine.equations;

import aurelienribon.tweenengine.TweenEquation;

/**
 * Easing equation based on Robert Penner's work:
 * http://robertpenner.com/easing/
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public abstract class Expo extends TweenEquation {
	public static final Expo IN = new Expo() {
		@Override
		public final float compute(float t, float b, float c, float d) {
			return (t==0) ? b : c * (float)Math.pow(2, 10 * (t/d - 1)) + b;
		}

		@Override
		public String toString() {
			return "Expo.IN";
		}
	};

	public static final Expo OUT = new Expo() {
		@Override
		public final float compute(float t, float b, float c, float d) {
			return (t==d) ? b+c : c * (-(float)Math.pow(2, -10 * t/d) + 1) + b;
		}

		@Override
		public String toString() {
			return "Expo.OUT";
		}
	};

	public static final Expo INOUT = new Expo() {
		@Override
		public final float compute(float t, float b, float c, float d) {
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