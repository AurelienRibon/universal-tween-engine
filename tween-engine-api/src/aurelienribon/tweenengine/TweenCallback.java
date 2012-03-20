package aurelienribon.tweenengine;

/**
 * TweenCallbacks are used to trigger actions at some specific times. They are
 * used in both Tweens and Timelines. The moment when the callback is
 * triggered depends on its registered triggers:
 * <p/>
 *
 * <b>BEGIN</b>: right after the delay (if any)<br/>
 * <b>START</b>: at each iteration beginning<br/>
 * <b>END</b>: at each iteration ending, before the repeat delay<br/>
 * <b>COMPLETE</b>: at last END event<br/>
 * <b>BACK_START</b>: at each backwards iteration beginning, after the repeat delay<br/>
 * <b>BACK_END</b>: at each backwards iteration ending<br/>
 * <b>BACK_COMPLETE</b>: at last BACK_END event
 * <p/>
 *
 * <pre> {@code
 * forwards :         BEGIN                                   COMPLETE
 * forwards :         START    END      START    END      START    END
 * |------------------[XXXXXXXXXX]------[XXXXXXXXXX]------[XXXXXXXXXX]
 * backwards:         bEND  bSTART      bEND  bSTART      bEND  bSTART
 * backwards:         bCOMPLETE
 * }</pre>
 *
 * @see Tween
 * @see Timeline
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public interface TweenCallback {
	public static final int BEGIN = 0x01;
	public static final int START = 0x02;
	public static final int END = 0x04;
	public static final int COMPLETE = 0x08;
	public static final int BACK_START = 0x10;
	public static final int BACK_END = 0x20;
	public static final int BACK_COMPLETE = 0x40;

	public void onEvent(int type, BaseTween<?> source);
}
