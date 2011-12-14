
import aurelienribon.tweenengine.tests.GdxComplexDemo;
import aurelienribon.tweenengine.tests.GdxSimpleTimeline;
import aurelienribon.tweenengine.tests.GdxSimpleTween;
import aurelienribon.tweenengine.tests.SwingSimpleTween;
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
					"Simple tween (LibGDX)",
					"Simple tween (Swing)",
					"Simple timeline (LibGDX)",
					"Simple timeline (Swing)",
					"Complex demo (libGDX)"
				};
				
				String testResult = (String) JOptionPane.showInputDialog(null,
					"Choose your test", "Initialization",
					JOptionPane.PLAIN_MESSAGE, null, tests, tests[0]);

				// -------------------------------------------------------------
				// GdxSimpleTween launch
				// -------------------------------------------------------------

				if (testResult != null && testResult.length() > 0) {
					if (testResult.equals(tests[0])) GdxSimpleTween.start();
					else if (testResult.equals(tests[1])) SwingSimpleTween.start();
					else if (testResult.equals(tests[2])) GdxSimpleTimeline.start();
					else if (testResult.equals(tests[3])) ;
					else if (testResult.equals(tests[4])) GdxComplexDemo.start();
				}
			}
		});
	}
}
