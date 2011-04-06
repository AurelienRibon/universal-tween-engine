package aurelienribon.libgdx.tween;

public interface Tweenable {
	/**
	 * Returns the number of combined interpolations per tween type.
	 * Has to be at least 1.
	 */
	public int getTweenedAttributeCount(int tweenType);

	/**
	 * Returns one or many values from the tweenable object associated to the
	 * given tween type. It is used by the tweening engine to determine starting
	 * values.
	 * @param tweenType An integer representing the tween type.
	 * @param returnValues A table which should be modified by this method.
	 */
	public void getTweenValues(int tweenType, float[] returnValues);

	/**
	 * This method is called by the tweening engine each time a running tween
	 * associated with the current Tweenable object has been updated.
	 * @param tweenType An integer representing the tween type.
	 * @param newValues The new values determined by the tweening engine.
	 */
	public void tweenUpdated(int tweenType, float[] newValues);
}
