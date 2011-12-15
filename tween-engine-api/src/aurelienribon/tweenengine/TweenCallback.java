package aurelienribon.tweenengine;

/**
 * TweenCallbacks are used to trigger actions at some specific times. They are
 * used both in Tweens and in Timelines. The moment when the callback is
 * triggered depends on its type:
 * <br/><br/>
 *
 * <b>BEGIN</b>: at first START, right after the delay (if any)<br/>
 * <b>START</b>: at each iteration beginning<br/>
 * <b>END</b>: at each iteration ending, before the repeat delay<br/>
 * <b>COMPLETE</b>: at last END<br/>
 * <b>BACK_START</b>: at each backwards iteration beginning, after the repeat delay<br/>
 * <b>BACK_END</b>: at each backwards iteration ending<br/>
 * <b>BACK_COMPLETE</b>: at last BACK_END
 * <br/><br/>
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
	public enum EventType {BEGIN, START, END, COMPLETE, BACK_START, BACK_END, BACK_COMPLETE}
	public void onEvent(EventType eventType, BaseTween source);
}
