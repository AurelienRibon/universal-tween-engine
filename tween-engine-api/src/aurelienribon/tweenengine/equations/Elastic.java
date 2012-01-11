package aurelienribon.tweenengine.equations;

import aurelienribon.tweenengine.TweenEquation;

/**
 * Easing equation based on Robert Penner's work:
 * http://robertpenner.com/easing/
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public abstract class Elastic extends TweenEquation {
	private static final float PI = 3.14159265f;

	public static final Elastic IN = new Elastic() {
		@Override
		public final float compute(float t, float b, float c, float d) {
			float a = param_a;
			float p = param_p;
			if (t==0) return b;  if ((t/=d)==1) return b+c; if (!setP) p=d*.3f;
			float s;
			if (!setA || a < Math.abs(c)) { a=c; s=p/4; }
			else s = p/(2*PI) * (float)Math.asin(c/a);
			return -(a*(float)Math.pow(2,10*(t-=1)) * (float)Math.sin( (t*d-s)*(2*PI)/p )) + b;
		}

		@Override
		public String toString() {
			return "Elastic.IN";
		}
	};

	public static final Elastic OUT = new Elastic() {
		@Override
		public final float compute(float t, float b, float c, float d) {
			float a = param_a;
			float p = param_p;
			if (t==0) return b;  if ((t/=d)==1) return b+c; if (!setP) p=d*.3f;
			float s;
			if (!setA || a < Math.abs(c)) { a=c; s=p/4; }
			else s = p/(2*PI) * (float)Math.asin(c/a);
			return (a*(float)Math.pow(2,-10*t) * (float)Math.sin( (t*d-s)*(2*PI)/p ) + c + b);
		}

		@Override
		public String toString() {
			return "Elastic.OUT";
		}
	};

	public static final Elastic INOUT = new Elastic() {
		@Override
		public final float compute(float t, float b, float c, float d) {
			float a = param_a;
			float p = param_p;
			if (t==0) return b;  if ((t/=d/2)==2) return b+c; if (!setP) p=d*(.3f*1.5f);
			float s;
			if (!setA || a < Math.abs(c)) { a=c; s=p/4; }
			else s = p/(2*PI) * (float)Math.asin(c/a);
			if (t < 1) return -.5f*(a*(float)Math.pow(2,10*(t-=1)) * (float)Math.sin( (t*d-s)*(2*PI)/p )) + b;
			return a*(float)Math.pow(2,-10*(t-=1)) * (float)Math.sin( (t*d-s)*(2*PI)/p )*.5f + c + b;
		}

		@Override
		public String toString() {
			return "Elastic.INOUT";
		}
	};

	// -------------------------------------------------------------------------

	protected float param_a;
	protected float param_p;
	protected boolean setA = false;
	protected boolean setP = false;

	public Elastic a(float a) {
		param_a = a;
		this.setA = true;
		return this;
	}

	public Elastic p(float p) {
		param_p = p;
		this.setP = true;
		return this;
	}
}