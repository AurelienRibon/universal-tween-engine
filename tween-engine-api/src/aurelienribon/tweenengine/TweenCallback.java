package aurelienribon.tweenengine;

/**
 * 
 * @author Aurelien Ribon (aurelien.ribon@gmail.com)
 */
public interface TweenCallback {
	public enum Types {ITERATION_COMPLETE, COMPLETE}
	public void tweenEventOccured(Types eventType, Tween tween);
}
