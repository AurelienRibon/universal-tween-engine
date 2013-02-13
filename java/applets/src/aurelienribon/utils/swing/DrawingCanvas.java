package aurelienribon.utils.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public abstract class DrawingCanvas extends JPanel {
	private final Timer timer;
	private long lastMillis;
	private Callback callback;

	public DrawingCanvas() {
		timer = new Timer(1000/60, loop);
		timer.setRepeats(true);
	}

	protected abstract void update(int elapsedMillis);

	public DrawingCanvas start() {
		lastMillis = System.currentTimeMillis();
		timer.start();
		return this;
	}

	public void stop() {
		timer.stop();
	}

	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	private final ActionListener loop = new ActionListener() {
		@Override public void actionPerformed(ActionEvent e) {
			final long millis = System.currentTimeMillis();
			final long delta = millis - lastMillis;
			lastMillis = millis;

			update((int) delta);
			if (callback != null) callback.onUpdate((int) delta);
		}
	};

	public interface Callback {
		public void onUpdate(int elapsedMillis);
	}
}
