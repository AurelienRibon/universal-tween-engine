package aurelienribon.tweenengine;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public interface TweenCallback {
	public enum Types {BEGIN, START, END, COMPLETE, BACK_START, BACK_END, BACK_COMPLETE}
	public void tweenEventOccured(Types eventType, BaseTween source);
}
