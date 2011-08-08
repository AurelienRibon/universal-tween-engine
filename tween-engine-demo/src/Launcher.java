
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public abstract class Launcher {
	public static void launch() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// -------------------------------------------------------------
				// Test selection
				// -------------------------------------------------------------

				String[] tests = null;
				tests = new String[] {
				"Game demo (libGDX)",
				"GUI demo (Swing)"};
				
				String testResult = (String) JOptionPane.showInputDialog(
					null,
					"Choose your test",
					"Initialization",
					JOptionPane.PLAIN_MESSAGE,
					null,
					tests,
					tests[0]);

				// -------------------------------------------------------------
				// App launch
				// -------------------------------------------------------------

				if (testResult != null && testResult.length() > 0) {
					if (testResult.equals(tests[0]))
						new LwjglApplication(new aurelienribon.tweenengine.tests.libgdx.App(), "", 500, 200, false);
					else if (testResult.equals(tests[1]))
						new aurelienribon.tweenengine.tests.swing.App();
				}
			}
		});
	}
}
