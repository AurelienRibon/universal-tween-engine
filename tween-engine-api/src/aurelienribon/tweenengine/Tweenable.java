package aurelienribon.tweenengine;

/**
 * The Tweenable interface lets you interpolate any attribute from any object.
 * Just implement it as you want and let the engine do the interpolation for
 * you. To setup the Tween Engine, you need to call the Tween.register() with
 * your implementations of this interface.
 *
 * <p>
 * The following code snippet presents an example of implementation for tweening
 * a Particle class. This Particle class is supposed to only define a position
 * with an "x" and an "y" field.
 * </p>
 *
 * <pre>
 * public class TweenableParticle implements Tweenable<Particle> {
 *     // The following lines define the different possible tween types.
 *     // It's up to you to define what you need :-)
 *     public static final int X = 1;
 *     public static final int Y = 2;
 *     public static final int XY = 3;
 *
 *     public int getTweenValues(Particle target, int tweenType, float[] returnValues) {
 *         switch (tweenType) {
 *             case X: returnValues[0] = target.getX(); return 1;
 *             case Y: returnValues[0] = target.getY(); return 1;
 *             case XY:
 *                 returnValues[0] = target.getX();
 *                 returnValues[1] = target.getY();
 *                 return 2;
 *             default: assert false; return 0;
 *         }
 *     }
 *     
 *     public void onTweenUpdated(Particle target, int tweenType, float[] newValues) {
 *         switch (tweenType) {
 *             case X: target.setX(newValues[0]); break;
 *             case Y: target.setY(newValues[1]); break;
 *             case XY:
 *                 target.setX(newValues[0]);
 *                 target.setY(newValues[1]);
 *                 break;
 *             default: assert false; break;
 *         }
 *     }
 * }
 * </pre>
 * 
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public interface Tweenable<T> {
	/**
	 * Gets one or many values from the tweenable object associated to the
	 * given tween type. It is used by the tweening engine to determine starting
	 * values.
	 * @param target The target object of the tween.
	 * @param tweenType An integer representing the tween type.
	 * @param returnValues A table which should be modified by this method.
	 * @return The count of tweened parameters.
	 */
	public int getTweenValues(T target, int tweenType, float[] returnValues);

	/**
	 * This method is called by the tweening engine each time a running tween
	 * associated with the current Tweenable object has been updated.
	 * @param target The target object of the tween.
	 * @param tweenType An integer representing the tween type.
	 * @param newValues The new values determined by the tweening engine.
	 */
	public void onTweenUpdated(T target, int tweenType, float[] newValues);
}
