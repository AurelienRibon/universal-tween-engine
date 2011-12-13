package aurelienribon.tweenengine.tests.swing;

import aurelienribon.tweenengine.TweenManager;
import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;

/**
 * A SwingTweenThread creates and manages an internal rendering loop. Indeed,
 * since Swing GUIs are event-based, there is no concept of an infinite
 * rendering loop lik ein every game. Therefore, the loop is created using a
 * dedicated thread, updated at ~60fps.
 *
 * <br/><br/>
 * Call "SwingTweenThread.start(yourObjectContainer, yourTweenManager)" to start
 * the engine.
 *
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class SwingTweenThread extends Thread {
	public static SwingTweenThread start(Component container, TweenManager manager) {
		SwingTweenThread th = new SwingTweenThread(container, manager);
		th.start();
		return th;
	}

	// -------------------------------------------------------------------------

	private final Component container;
	private final TweenManager manager;
	private boolean isRunning;

	/**
	 * Creates and starts a rendering loop.
	 * @param container The container that needs to be repaint every frame.
	 * @param manager Your tween manager.
	 */
	private SwingTweenThread(Component container, TweenManager manager) {
		this.container = container;
		this.manager = manager;
		this.isRunning = true;
	}

	/**
	 * Kills the thread. Once killed, the thread cannot be restarted, you need
	 * to instantiate a new one.
	 */
	public void kill() {
		isRunning = false;
	}

	@Override
	public void run() {
		while (isRunning) {
			try {
				SwingUtilities.invokeAndWait(updateRunnable);
				Thread.sleep(16);
			} catch (InterruptedException ex) {
			} catch (InvocationTargetException ex) {
			}
		}
	}

	private Runnable updateRunnable = new Runnable() {
		private long lastMillis = 0;

		@Override
		public void run() {
			long millis = System.currentTimeMillis();
			int delta = (int) (millis - lastMillis);
			lastMillis = millis;
			manager.update(delta);
			container.repaint();
		}
	};
}
