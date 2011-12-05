package aurelienribon.tweenengine;

/**
 * 
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public interface Groupable {
	public int getDuration();
	public int getDelay();
	public int getRepeatCount();
	public int getRepeatDelay();
	public Groupable delay(int millis);
	public Groupable repeat(int count, int delayMillis);
}
