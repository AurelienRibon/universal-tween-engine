package aurelienribon.utils.swing;

import java.lang.reflect.InvocationTargetException;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public abstract class DrawingCanvas extends JPanel {
	private Thread thread = null;
	private boolean isRunning = false;

	public DrawingCanvas start() {
		thread = new Thread(loop);
		thread.start();
		isRunning = true;
		return this;
	}

	public void stop() {
		isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException ex) {
		}
	}

	protected abstract void update(int elapsedMillis);

	private Runnable loop = new Runnable() {
		private long lastMillis = 0;

		@Override public void run() {
			while (isRunning) {
				try {
					final long millis = System.currentTimeMillis();
					final long delta = millis - lastMillis;
					lastMillis = millis;

					SwingUtilities.invokeAndWait(new Runnable() {@Override public void run() {update((int) delta);}});
					Thread.sleep(16);

				} catch (InterruptedException ex) {
				} catch (InvocationTargetException ex) {
				}
			}
		}
	};
}
