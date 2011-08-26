package aurelienribon.tweenengine.tests.swing;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenGroup;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Elastic;
import javax.swing.JFrame;

public class App {
	public App() {
		TweenJLabel label = new TweenJLabel();
		label.setText("Hello TweenEngine !");
		label.setSize(label.getPreferredSize());
		label.setLocation(100, 100);

		JFrame wnd = new JFrame("TweenEngine Swing test");
		wnd.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		wnd.setSize(800, 600);
		wnd.setLayout(null);
		wnd.getContentPane().add(label);
		wnd.setVisible(true);

		TweenManager manager = new TweenManager();
		TweenGroup.sequence(
			Tween.to(label, TweenJLabel.POSITION, 1500, Elastic.OUT).target(500, 300).delay(500),
			Tween.to(label, TweenJLabel.POSITION, 1000, Bounce.OUT).target(500, 20)
		).addToManager(manager);

		SwingTweenThread.start(wnd.getContentPane(), manager);
	}
}
