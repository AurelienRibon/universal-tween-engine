package aurelienribon.tweenengine.tests.swing;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Cubic;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class App {
	private final JFrame window;
	private final JLabel label;
	private final JButton button;
	private final TweenManager tweenManager;

	public App() {
		button = new JButton("Push me!");
		button.setLocation(10, 30);
		button.setSize(button.getPreferredSize());
		button.addMouseListener(buttonMouseListener);
		button.addActionListener(buttonActionListener);

		label = new JLabel("Universal Tween Engine");
		label.setLocation(10, 10);
		label.setSize(label.getPreferredSize());

		window = new JFrame("TweenEngine Swing test");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(800, 600);
		window.setLayout(null);
		window.getContentPane().add(label);
		window.getContentPane().add(button);
		window.setVisible(true);

		Tween.registerAccessor(Component.class, new ComponentTweenAccessor());
		tweenManager = new TweenManager();

		Tween.to(label, ComponentTweenAccessor.POSITION, 1000)
			.target(window.getContentPane().getWidth() - label.getWidth() - 10, 10)
			.ease(Cubic.INOUT)
			.repeatYoyo(-1, 200)
			.start(tweenManager);

		SwingTweenThread.start(window.getContentPane(), tweenManager);
	}

	private final MouseAdapter buttonMouseListener = new MouseAdapter() {
		@Override
		public void mouseEntered(MouseEvent e) {
			Random rand = new Random();
			int tx = rand.nextInt(window.getContentPane().getWidth() - button.getWidth());
			int ty = rand.nextInt(window.getContentPane().getHeight() - 30 - button.getHeight()) + 30;

			tweenManager.kill(button);
			Tween.to(button, ComponentTweenAccessor.POSITION, 500)
				.target(tx, ty)
				.ease(Back.OUT)
				.start(tweenManager);
		}
	};

	private final ActionListener buttonActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(window, "Congratulations! I guess you had some luck, or resized the window :)");
		}
	};
}
