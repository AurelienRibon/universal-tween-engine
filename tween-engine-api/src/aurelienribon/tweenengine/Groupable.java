package aurelienribon.tweenengine;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public interface Groupable {
	public int getDelay();
	public int getDuration();
	public int getRepeatDelay();
	public int getRepeatCount();
	public Groupable delay(int millis);
	public Groupable repeat(int count, int delayMillis);
	public Groupable repeatYoyo(int count, int delayMillis);
}
