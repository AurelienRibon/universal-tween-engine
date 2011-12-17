package aurelienribon.tweenengine.applets;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.utils.swing.DrawingCanvas;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class TweenApplet extends javax.swing.JApplet {
	/*public static void main(String[] args) {
		TweenApplet applet = new TweenApplet();
		applet.init();
		applet.start();

		javax.swing.JFrame wnd = new javax.swing.JFrame();
		wnd.add(applet);
		wnd.setSize(600, 550);
		wnd.setVisible(true);
	}*/

	@Override
	public void init() {
		try {
			java.awt.EventQueue.invokeAndWait(new Runnable() {
				@Override public void run() {load();}
			});
		} catch (Exception ex) {
		}
	}

	@Override
	public void destroy() {
		DrawingCanvas canvas = (DrawingCanvas) canvasWrapper.getComponent(0);
		canvas.stop();
	}

	private void load() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException ex) {
		} catch (InstantiationException ex) {
		} catch (IllegalAccessException ex) {
		} catch (UnsupportedLookAndFeelException ex) {
		}

		initComponents();

		getContentPane().setBackground(Theme.MAIN_BACKGROUND);
		Theme.apply(getContentPane());

		OptionsListener listener = new OptionsListener();
		easingCbox.addActionListener(listener);
		delaySpinner.addChangeListener(listener);
		durationSpinner.addChangeListener(listener);
		rptSpinner.addChangeListener(listener);
		rptDelaySpinner.addChangeListener(listener);
		yoyoChk.addActionListener(listener);

		generateCode();

		canvasWrapper.add(new MyCanvas().start(), BorderLayout.CENTER);
	}

	private void generateCode() {
		String easing = (String) easingCbox.getSelectedItem();
		int delay = (Integer) delaySpinner.getValue();
		int duration = (Integer) durationSpinner.getValue();
		int rptCnt = (Integer) rptSpinner.getValue();
		int rptDelay = (Integer) rptDelaySpinner.getValue();
		boolean isYoyo = yoyoChk.isSelected();

		String code = "Tween.to(mySprite, POSITION_XY, " + duration + ")";
		code += "\n     .target()";

		if (!easing.equals("Linear") && !easing.equals("----------")) code += "\n     .ease(" + easing + ")";
		if (delay > 0) code += "\n     .delay(" + delay + ")";
		if (rptCnt > 0) code += "\n     .repeat" + (isYoyo ? "Yoyo" : "") + "(" + rptCnt + ", " + rptDelay + ")";

		code += "\n     .start(myManager);";
		
		resultArea.setText(code);
	}

	private class OptionsListener implements ChangeListener, ActionListener {
		@Override public void stateChanged(ChangeEvent e) {onEvent();}
		@Override public void actionPerformed(ActionEvent e) {onEvent();}
		private void onEvent() {
			generateCode();
		}
	}

	private class MyCanvas extends DrawingCanvas {
		private final TweenManager tweenManager = new TweenManager();
		private final Sprite vialSprite;
		private TexturePaint bgPaint;

		public MyCanvas() {
			Tween.enablePooling(false);
			Tween.registerAccessor(Sprite.class, new SpriteAccessor());
			addMouseListener(mouseAdapter);
			vialSprite = new Sprite("vial.png");
			vialSprite.setX(100);
			vialSprite.setY(100);

			try {
				BufferedImage bgImage = ImageIO.read(TweenApplet.class.getResource("/aurelienribon/tweenengine/applets/gfx/transparent-dark.png"));
				bgPaint = new TexturePaint(bgImage, new Rectangle(0, 0, bgImage.getWidth(), bgImage.getHeight()));
			} catch (IOException ex) {
			}
		}

		@Override
		protected void update(int elapsedMillis) {
			tweenManager.update(elapsedMillis);
			repaint();
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D gg = (Graphics2D) g;

			if (bgPaint != null) {
				gg.setPaint(bgPaint);
				gg.fillRect(0, 0, getWidth(), getHeight());
				gg.setPaint(null);
			}

			vialSprite.draw(gg);
		}

		private final MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override public void mousePressed(MouseEvent e) {
				TweenEquation easing = TweenEquation.parse((String) easingCbox.getSelectedItem());
				int delay = (Integer) delaySpinner.getValue();
				int duration = (Integer) durationSpinner.getValue();
				int rptCnt = (Integer) rptSpinner.getValue();
				int rptDelay = (Integer) rptDelaySpinner.getValue();
				boolean isYoyo = yoyoChk.isSelected();

				tweenManager.killAll();

				Tween tween = Tween.to(vialSprite, SpriteAccessor.POSITION_XY, duration)
					.target(e.getX(), e.getY())
					.delay(delay);

				if (easing != null) tween.ease(easing);
				if (isYoyo) tween.repeatYoyo(rptCnt, rptDelay);
				else tween.repeat(rptCnt, rptDelay);

				tween.start(tweenManager);
			}
		};
	}

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        resultArea = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        canvasWrapper = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        delaySpinner = new javax.swing.JSpinner();
        jLabel4 = new javax.swing.JLabel();
        rptSpinner = new javax.swing.JSpinner();
        yoyoChk = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        durationSpinner = new javax.swing.JSpinner();
        jLabel7 = new javax.swing.JLabel();
        easingCbox = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        rptDelaySpinner = new javax.swing.JSpinner();
        jPanel4 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();

        jPanel1.setBorder(new aurelienribon.utils.swing.GroupBorder());

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        resultArea.setColumns(20);
        resultArea.setRows(5);
        jScrollPane1.setViewportView(resultArea);

        jLabel1.setText("Java code:");

        jLabel9.setText("<html>\nUniversal Tween Engine v6.0.0 - <font color=\"#77C8FF\">www.aurelienribon.com</font>");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 562, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 237, Short.MAX_VALUE)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE)
                .addContainerGap())
        );

        canvasWrapper.setLayout(new java.awt.BorderLayout());

        jPanel3.setOpaque(false);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/aurelienribon/tweenengine/applets/gfx/logo.png"))); // NOI18N

        aurelienribon.utils.swing.GroupBorder groupBorder1 = new aurelienribon.utils.swing.GroupBorder();
        groupBorder1.setTitle("Options");
        jPanel2.setBorder(groupBorder1);

        jLabel3.setText("Delay:");

        delaySpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(100)));

        jLabel4.setText("Repetitions:");

        rptSpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));

        yoyoChk.setText("Yoyo repetitions");

        jLabel5.setText("Duration:");

        durationSpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(500), Integer.valueOf(0), null, Integer.valueOf(100)));

        jLabel7.setText("Easing:");

        easingCbox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Linear.INOUT", "----------", "Quad.IN", "Quad.OUT", "Quad.INOUT", "----------", "Cubic.IN", "Cubic.OUT", "Cubic.INOUT", "----------", "Quart.IN", "Quart.OUT", "Quart.INOUT", "----------", "Quint.IN", "Quint.OUT", "Quint.INOUT", "----------", "Circ.IN", "Circ.OUT", "Circ.INOUT", "----------", "Sine.IN", "Sine.OUT", "Sine.INOUT", "----------", "Expo.IN", "Expo.OUT", "Expo.INOUT", "----------", "Back.IN", "Back.OUT", "Back.INOUT", "----------", "Bounce.IN", "Bounce.OUT", "Bounce.INOUT", "----------", "Elastic.IN", "Elastic.OUT", "Elastic.INOUT" }));
        easingCbox.setSelectedIndex(31);

        jLabel6.setText("Repeat delay:");

        rptDelaySpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(100)));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(yoyoChk, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(easingCbox, 0, 105, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(durationSpinner, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(delaySpinner, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(rptSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(rptDelaySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(easingCbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(delaySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(durationSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rptSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rptDelaySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(18, 18, 18)
                .addComponent(yoyoChk)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(new aurelienribon.utils.swing.GroupBorder());

        jLabel8.setText("<html>\nClick anywhere on the canvas to fire your custom tween.");
        jLabel8.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(canvasWrapper, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(canvasWrapper, javax.swing.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel canvasWrapper;
    private javax.swing.JSpinner delaySpinner;
    private javax.swing.JSpinner durationSpinner;
    private javax.swing.JComboBox easingCbox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea resultArea;
    private javax.swing.JSpinner rptDelaySpinner;
    private javax.swing.JSpinner rptSpinner;
    private javax.swing.JCheckBox yoyoChk;
    // End of variables declaration//GEN-END:variables

}
