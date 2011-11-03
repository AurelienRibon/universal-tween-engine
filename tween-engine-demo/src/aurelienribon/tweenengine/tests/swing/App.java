package aurelienribon.tweenengine.tests.swing;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Back;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class App {
	private final JFrame window;
	private final JButton button;
	private final TweenManager tweenManager;
	private final TweenComponent tweenableButton;

	public App() {
		button = new JButton("Push me!");
		button.setLocation(10, 10);
		button.setSize(button.getPreferredSize());
		button.addMouseListener(buttonMouseListener);
		button.addActionListener(buttonActionListener);

		window = new JFrame("TweenEngine Swing test");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(800, 600);
		window.setLayout(null);
		window.getContentPane().add(button);
		window.setVisible(true);

		tweenManager = new TweenManager();
		tweenableButton = new TweenComponent(button);

		SwingTweenThread.start(window.getContentPane(), tweenManager);
	}

	private final MouseAdapter buttonMouseListener = new MouseAdapter() {
		@Override
		public void mouseEntered(MouseEvent e) {
			Random rand = new Random();
			int tx = rand.nextInt(window.getContentPane().getWidth() - button.getWidth());
			int ty = rand.nextInt(window.getContentPane().getHeight() - button.getHeight());

			tweenManager.kill(tweenableButton);
			Tween.to(tweenableButton, TweenComponent.POSITION, 500, Back.OUT)
				.target(tx, ty)
				.addToManager(tweenManager);
		}
	};

	private final ActionListener buttonActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(window, "Congratulations! I guess you had some luck, or resized the window :)");
		}
	};
}
