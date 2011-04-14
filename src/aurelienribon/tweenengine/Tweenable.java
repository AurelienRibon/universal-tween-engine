package aurelienribon.tweenengine;

/**
 * The Tweenable interface lets you interpolate any attribute from any object.
 * Just implement it as you want and let the engine do the interpolation for
 * you.
 *
 * <p>
 * The following code snippet presents an example of implementation for tweening
 * a Particule class. This Particule class is supposed to only define a position
 * with an "x" and an "y" field.
 * </p>
 *
 * <p>
 * The implementation is done with the Composition Pattern. It allows us to let
 * the Particule class untouched (that way, even third-party classes can be
 * tweened !).
 * </p>
 *
 * <pre>
 * public class TweenableParticule implements Tweenable {
 *     // The following lines define the different possible tween types.
 *     // It's up to you to define what you need :-)
 *     public static final int X = 1;
 *     public static final int Y = 2;
 *     public static final int XY = 3;
 *
 *     // Composition pattern
 *     private Particule target;
 *
 *     // Constructor
 *     public TweenableParticule(Particule particule) {
 *         this.target = particule;
 *     }
 *
 *     public int getTweenValues(int tweenType, float[] returnValues) {
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
 *     public void tweenUpdated(int tweenType, float[] newValues) {
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
 * @author Aurelien Ribon (aurelien.ribon@gmail.com)
 */
public interface Tweenable {
	/**
	 * Gets one or many values from the tweenable object associated to the
	 * given tween type. It is used by the tweening engine to determine starting
	 * values.
	 * @param tweenType An integer representing the tween type.
	 * @param returnValues A table which should be modified by this method.
	 * @return The count of tweened parameters.
	 */
	public int getTweenValues(int tweenType, float[] returnValues);

	/**
	 * This method is called by the tweening engine each time a running tween
	 * associated with the current Tweenable object has been updated.
	 * @param tweenType An integer representing the tween type.
	 * @param newValues The new values determined by the tweening engine.
	 */
	public void tweenUpdated(int tweenType, float[] newValues);
}
