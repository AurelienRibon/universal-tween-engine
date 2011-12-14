package aurelienribon.tweenengine;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public interface TimelineCallback {
	public enum Types {BEGIN, START, END, COMPLETE, BACK_START, BACK_END, BACK_COMPLETE}
	public void timelineEventOccured(Types eventType, Timeline timeline);
}
