package aurelienribon.tweenengine;

/**
 * 
 * @author Aurelien Ribon (aurelien.ribon@gmail.com)
 */
public interface TweenCallback {
	public enum Types {ITERATION_COMPLETE, BACK_ITERATION_COMPLETE, COMPLETE, BACK_COMPLETE}
	public void tweenEventOccured(Types eventType, Tween tween);
}
