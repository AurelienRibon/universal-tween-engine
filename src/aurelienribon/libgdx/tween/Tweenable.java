package aurelienribon.libgdx.tween;

public interface Tweenable {
	/**
	 * Returns a value from the tweenable object associated to the given tween
	 * type. It is used by the tweening engine to determine starting values.
	 * @param tweenType An integer representing the tween type.
	 * @return A value from the tweenable object corresponding to the tween type
	 */
	public float getTweenValue(int tweenType);

	/**
	 * This method is called by the tweening engine each time a running tween
	 * associated with the current Tweenable object has been updated.
	 * @param tweenType An integer representing the tween type.
	 * @param newValue The new value determined by the tweening engine.
	 */
	public void tweenUpdated(int tweenType, float newValue);
}
