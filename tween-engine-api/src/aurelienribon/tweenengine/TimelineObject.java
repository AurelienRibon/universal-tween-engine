package aurelienribon.tweenengine;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public abstract class TimelineObject {
	public abstract void kill();
	public abstract void update(int deltaMillis);
	public abstract boolean isFinished();
	public abstract void free();
	public abstract int getFullDuration();
	protected abstract void setCurrentMillis(int millis);
	protected abstract int getChildrenCount();
	protected abstract void killTarget(Object target);
	protected abstract void killTarget(Object target, int tweenType);
	protected abstract boolean containsTarget(Object target);
	protected abstract boolean containsTarget(Object target, int tweenType);
}
