package aurelienribon.tweenengine.applets;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenCallback.EventType;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Quart;
import aurelienribon.utils.swing.DrawingCanvas;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Hashtable;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class TimelineApplet extends javax.swing.JApplet {
	/*public static void main(String[] args) {
		TimelineApplet applet = new TimelineApplet();
		applet.init();
		applet.start();

		javax.swing.JFrame wnd = new javax.swing.JFrame();
		wnd.add(applet);
		wnd.setSize(600, 700);
		wnd.setVisible(true);
	}*/

	// -------------------------------------------------------------------------
	// Applet
	// -------------------------------------------------------------------------

	private MyCanvas canvas;
	private boolean isPaused = false;

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
		rptSpinner.addChangeListener(listener);
		rptDelaySpinner.addChangeListener(listener);
		yoyoChk.addActionListener(listener);

		generateCode();

		Hashtable<Integer, JLabel> labels = new Hashtable<Integer, JLabel>();
		labels.put(-300, new JLabel("-3"));
		labels.put(-200, new JLabel("-2"));
		labels.put(-100, new JLabel("-1"));
		labels.put(0, new JLabel("0"));
		labels.put(100, new JLabel("1"));
		labels.put(200, new JLabel("2"));
		labels.put(300, new JLabel("3"));
		for (JLabel lbl : labels.values()) lbl.setForeground(Theme.MAIN_FOREGROUND);
		speedSlider.setLabelTable(labels);

		canvas = (MyCanvas) new MyCanvas().start();
		canvasWrapper.add(canvas, BorderLayout.CENTER);
		canvas.setCallback(new DrawingCanvas.Callback() {
			@Override public void onUpdate(int elapsedMillis) {
				if (canvas.getTimeline() == null || isPaused) return;
				int delta = (int) (elapsedMillis * (speedSlider.getValue() / 100f));

				if (canvas.getTimeline().getState()%4 == 2 && canvas.getTimeline().isYoyo())
					iterationTimeSlider.setValue(iterationTimeSlider.getValue() - delta);
				else if (canvas.getTimeline().getState()%2 == 0)
					iterationTimeSlider.setValue(iterationTimeSlider.getValue() + delta);
				totalTimeSlider.setValue(totalTimeSlider.getValue() + delta);
			}
		});

		canvas.createTimeline();
		initTimeline();
	}

	private void initTimeline() {
		iterationTimeSlider.setMaximum(canvas.getTimeline().getDuration());
		totalTimeSlider.setMaximum(canvas.getTimeline().getFullDuration());

		canvas.getTimeline().addCallback(EventType.BEGIN, new TweenCallback() {
			@Override public void onEvent(EventType eventType, BaseTween source) {
				totalTimeSlider.setValue(0);
			}
		});

		canvas.getTimeline().addCallback(EventType.START, new TweenCallback() {
			@Override public void onEvent(EventType eventType, BaseTween source) {
				if (canvas.getTimeline().getState()%4 == 2 && canvas.getTimeline().isYoyo())
					iterationTimeSlider.setValue(canvas.getTimeline().getFullDuration());
				else if (canvas.getTimeline().getState()%2 == 0)
					iterationTimeSlider.setValue(0);
			}
		});
	}

	private void generateCode() {
		int rptCnt = (Integer) rptSpinner.getValue();
		int rptDelay = (Integer) rptDelaySpinner.getValue();
		boolean isYoyo = yoyoChk.isSelected();

		String code = "Timeline.createSequence()" +
				"\n    .push(Tween.to(imgTweenSprite, POSITION_XY, 500).target(60, 90).ease(Quart.OUT))" +
				"\n    .push(Tween.to(imgEngineSprite, POSITION_XY, 500).target(200, 90).ease(Quart.OUT))" +
				"\n    .push(Tween.to(imgUniversalSprite, POSITION_XY, 1000).target(60, 55).ease(Bounce.OUT))" +
				"\n    .pushPause(500)" +
				"\n    .beginParallel()" +
				"\n        .push(Tween.set(imgLogoSprite, VISIBILITY).target(1))" +
				"\n        .push(Tween.to(imgLogoSprite, SCALE_XY, 800).target(1, 1).ease(Back.OUT))" +
				"\n        .push(Tween.to(blankStripSprite, SCALE_XY, 500).target(1, 1).ease(Back.OUT))" +
				"\n    .end()";

		if (rptCnt > 0) code += "\n    .repeat" + (isYoyo ? "Yoyo" : "") + "(" + rptCnt + ", " + rptDelay + ")";

		code += "\n    .start(myManager);";
		
		resultArea.setText(code);
	}

	private void restart() {
		speedSlider.setValue(100);
		canvas.createTimeline();
		initTimeline();
	}

	private class OptionsListener implements ChangeListener, ActionListener {
		@Override public void stateChanged(ChangeEvent e) {onEvent();}
		@Override public void actionPerformed(ActionEvent e) {onEvent();}
		private void onEvent() {
			generateCode();
			restart();
		}
	}

	// -------------------------------------------------------------------------
	// Canvas
	// -------------------------------------------------------------------------

	private class MyCanvas extends DrawingCanvas {
		private final TweenManager tweenManager = new TweenManager();
		private final Sprite imgUniversalSprite;
		private final Sprite imgTweenSprite;
		private final Sprite imgEngineSprite;
		private final Sprite imgLogoSprite;
		private final Sprite blankStripSprite;
		private TexturePaint bgPaint;
		private Timeline timeline;

		public MyCanvas() {
			Tween.enablePooling(false);
			Tween.registerAccessor(Sprite.class, new SpriteAccessor());
			
			imgUniversalSprite = new Sprite("img-universal.png").setCentered(false);
			imgTweenSprite = new Sprite("img-tween.png").setCentered(false);
			imgEngineSprite = new Sprite("img-engine.png").setCentered(false);
			imgLogoSprite = new Sprite("img-logo.png");
			blankStripSprite = new Sprite("blankStrip.png");

			try {
				BufferedImage bgImage = ImageIO.read(TimelineApplet.class.getResource("/aurelienribon/tweenengine/applets/gfx/transparent-dark.png"));
				bgPaint = new TexturePaint(bgImage, new Rectangle(0, 0, bgImage.getWidth(), bgImage.getHeight()));
			} catch (IOException ex) {
			}
		}

		@Override
		protected void update(int elapsedMillis) {
			if (isPaused) return;
			int delta = (int) (elapsedMillis * (speedSlider.getValue() / 100f));
			tweenManager.update(delta);
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

			blankStripSprite.draw(gg);
			imgUniversalSprite.draw(gg);
			imgTweenSprite.draw(gg);
			imgEngineSprite.draw(gg);
			imgLogoSprite.draw(gg);
		}

		public void createTimeline() {
			tweenManager.killAll();
			
			imgUniversalSprite.setPosition(60, 105 - 200);
			imgTweenSprite.setPosition(60 - 300, 140);
			imgEngineSprite.setPosition(200 + 300, 140);

			imgLogoSprite.setPosition(310, 120);
			imgLogoSprite.setScale(7, 7);
			imgLogoSprite.setVisible(false);

			blankStripSprite.setPosition(250, 140);
			blankStripSprite.setScale(1, 0);

			timeline = Timeline.createSequence()
				.push(Tween.to(imgTweenSprite, SpriteAccessor.POSITION_XY, 500).target(60, 140).ease(Quart.OUT))
				.push(Tween.to(imgEngineSprite, SpriteAccessor.POSITION_XY, 500).target(200, 140).ease(Quart.OUT))
				.push(Tween.to(imgUniversalSprite, SpriteAccessor.POSITION_XY, 1000).target(60, 105).ease(Bounce.OUT))
				.pushPause(500)
				.beginParallel()
					.push(Tween.set(imgLogoSprite, SpriteAccessor.VISIBILITY).target(1))
					.push(Tween.to(imgLogoSprite, SpriteAccessor.SCALE_XY, 800).target(1, 1).ease(Back.OUT))
					.push(Tween.to(blankStripSprite, SpriteAccessor.SCALE_XY, 500).target(1, 1).ease(Back.OUT))
				.end();

			int rptCnt = (Integer) rptSpinner.getValue();
			int rpDelay = (Integer) rptDelaySpinner.getValue();
			boolean yoyo = yoyoChk.isSelected();

			if (rptCnt > 0 && yoyo) timeline.repeatYoyo(rptCnt, rpDelay);
			else if (rptCnt > 0) timeline.repeat(rptCnt, rpDelay);

			timeline.addCallback(EventType.COMPLETE, new TweenCallback() {
				@Override public void onEvent(EventType eventType, BaseTween source) {
					timeline = null;
				}
			});

			timeline.addCallback(EventType.BACK_COMPLETE, new TweenCallback() {
				@Override public void onEvent(EventType eventType, BaseTween source) {
					timeline = null;
				}
			});

			timeline.start(tweenManager);
		}

		public Timeline getTimeline() {
			return timeline;
		}
	}

	// -------------------------------------------------------------------------
	// Generated stuff
	// -------------------------------------------------------------------------

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
        jLabel4 = new javax.swing.JLabel();
        rptSpinner = new javax.swing.JSpinner();
        yoyoChk = new javax.swing.JCheckBox();
        jLabel6 = new javax.swing.JLabel();
        rptDelaySpinner = new javax.swing.JSpinner();
        jPanel5 = new javax.swing.JPanel();
        speedSlider = new javax.swing.JSlider();
        jPanel4 = new javax.swing.JPanel();
        restartBtn = new javax.swing.JButton();
        pauseBtn = new javax.swing.JButton();
        resumeBtn = new javax.swing.JButton();
        reverseBtn = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        iterationTimeSlider = new javax.swing.JSlider();
        totalTimeSlider = new javax.swing.JSlider();

        jPanel1.setBorder(new aurelienribon.utils.swing.GroupBorder());

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        resultArea.setColumns(20);
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
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 598, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 273, Short.MAX_VALUE)
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
                .addContainerGap())
        );

        canvasWrapper.setLayout(new java.awt.BorderLayout());

        jPanel3.setOpaque(false);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/aurelienribon/tweenengine/applets/gfx/logo-timeline.png"))); // NOI18N

        aurelienribon.utils.swing.GroupBorder groupBorder1 = new aurelienribon.utils.swing.GroupBorder();
        groupBorder1.setTitle("Timeline options");
        jPanel2.setBorder(groupBorder1);

        jLabel4.setText("Repetitions:");

        rptSpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(2), Integer.valueOf(0), null, Integer.valueOf(1)));

        yoyoChk.setSelected(true);
        yoyoChk.setText("Yoyo repetitions");

        jLabel6.setText("Repeat delay:");

        rptDelaySpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(500), Integer.valueOf(0), null, Integer.valueOf(100)));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(yoyoChk, javax.swing.GroupLayout.Alignment.TRAILING)
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

        aurelienribon.utils.swing.GroupBorder groupBorder2 = new aurelienribon.utils.swing.GroupBorder();
        groupBorder2.setTitle("Animation speed");
        jPanel5.setBorder(groupBorder2);

        speedSlider.setMajorTickSpacing(100);
        speedSlider.setMaximum(300);
        speedSlider.setMinimum(-300);
        speedSlider.setPaintLabels(true);
        speedSlider.setPaintTicks(true);
        speedSlider.setValue(100);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(speedSlider, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(speedSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel4.setBorder(new aurelienribon.utils.swing.GroupBorder());

        restartBtn.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        restartBtn.setText("Restart");
        restartBtn.setMargin(new java.awt.Insets(2, 3, 2, 3));
        restartBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restartBtnActionPerformed(evt);
            }
        });

        pauseBtn.setText("Pause");
        pauseBtn.setMargin(new java.awt.Insets(2, 3, 2, 3));
        pauseBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseBtnActionPerformed(evt);
            }
        });

        resumeBtn.setText("Resume");
        resumeBtn.setMargin(new java.awt.Insets(2, 3, 2, 3));
        resumeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resumeBtnActionPerformed(evt);
            }
        });

        reverseBtn.setText("Reverse");
        reverseBtn.setMargin(new java.awt.Insets(2, 3, 2, 3));
        reverseBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reverseBtnActionPerformed(evt);
            }
        });

        jLabel3.setText("Total time:");

        jLabel5.setText("Iteration time:");

        iterationTimeSlider.setEnabled(false);

        totalTimeSlider.setEnabled(false);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(restartBtn)
                    .addComponent(reverseBtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(resumeBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(pauseBtn)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(totalTimeSlider, javax.swing.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
                    .addComponent(iterationTimeSlider, javax.swing.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {pauseBtn, restartBtn, resumeBtn, reverseBtn});

        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(restartBtn)
                        .addComponent(pauseBtn)
                        .addComponent(jLabel5))
                    .addComponent(iterationTimeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(resumeBtn)
                        .addComponent(reverseBtn)
                        .addComponent(jLabel3))
                    .addComponent(totalTimeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(canvasWrapper, javax.swing.GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(canvasWrapper, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

	private void restartBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restartBtnActionPerformed
		restart();
	}//GEN-LAST:event_restartBtnActionPerformed

	private void pauseBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pauseBtnActionPerformed
		isPaused = true;
	}//GEN-LAST:event_pauseBtnActionPerformed

	private void resumeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resumeBtnActionPerformed
		isPaused = false;
	}//GEN-LAST:event_resumeBtnActionPerformed

	private void reverseBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reverseBtnActionPerformed
		speedSlider.setValue(-speedSlider.getValue());
	}//GEN-LAST:event_reverseBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel canvasWrapper;
    private javax.swing.JSlider iterationTimeSlider;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton pauseBtn;
    private javax.swing.JButton restartBtn;
    private javax.swing.JTextArea resultArea;
    private javax.swing.JButton resumeBtn;
    private javax.swing.JButton reverseBtn;
    private javax.swing.JSpinner rptDelaySpinner;
    private javax.swing.JSpinner rptSpinner;
    private javax.swing.JSlider speedSlider;
    private javax.swing.JSlider totalTimeSlider;
    private javax.swing.JCheckBox yoyoChk;
    // End of variables declaration//GEN-END:variables

}
