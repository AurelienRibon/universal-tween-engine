package aurelienribon.tweenengine;

public abstract class TweenEquation {
    public abstract float compute(float t, float b, float c, float d);

	public boolean isValueOf(String str) {
		return str.equals(toString());
	}
}
