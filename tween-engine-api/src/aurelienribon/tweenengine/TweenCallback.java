package aurelienribon.tweenengine;

/**
 * 
 * @author Aurelien Ribon (aurelien.ribon@gmail.com)
 */
public interface TweenCallback {
	public enum Types {
		START, END_OF_DELAY, ITERATION_COMPLETE, COMPLETE, KILL, POOL
	}

	public void tweenEventOccured(Types eventType, Tween tween);
}
