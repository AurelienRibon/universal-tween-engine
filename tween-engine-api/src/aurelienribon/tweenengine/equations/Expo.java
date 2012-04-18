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
		public final float compute(float t, float d) {
			return (t==0) ? 0 : (float) Math.pow(2, 10 * (t/d - 1));
		}

		@Override
		public String toString() {
			return "Expo.IN";
		}
	};

	public static final Expo OUT = new Expo() {
		@Override
		public final float compute(float t, float d) {
			return (t==d) ? 1 : -(float) Math.pow(2, -10 * t/d) + 1;
		}

		@Override
		public String toString() {
			return "Expo.OUT";
		}
	};

	public static final Expo INOUT = new Expo() {
		@Override
		public final float compute(float t, float d) {
			if (t==0) return 0;
			if (t==d) return 1;
			if ((t/=d/2) < 1) return 0.5f * (float) Math.pow(2, 10 * (t - 1));
			return 0.5f * (-(float)Math.pow(2, -10 * --t) + 2);
		}

		@Override
		public String toString() {
			return "Expo.INOUT";
		}
	};
}