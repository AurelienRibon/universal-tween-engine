package aurelienribon.utils.swing;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import javax.swing.border.Border;

public class GroupBorder implements Border {
	private final int titleHeight = 20;
	private final int borderPadding = 0;
	private String title = "";

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		Graphics2D gg = (Graphics2D) g.create();
		
		int titleW = gg.getFontMetrics().stringWidth(title) + 20;
		int titleDescent = gg.getFontMetrics().getDescent();
		
		gg.setColor(c.getBackground());

		if (!title.equals("")) {
			int[] xs = {0, titleW, titleW + titleHeight, 0};
			int[] ys = {0, 0, titleHeight, titleHeight};
			gg.fillPolygon(xs, ys, 4);
			gg.fillRect(0, titleHeight, width, height);
			gg.setColor(c.getForeground());
			gg.drawString(title, 10, titleHeight - titleDescent);
		} else {
			gg.fillRect(0, 0, width, height);
		}
		
		gg.dispose();
	}

	@Override
	public Insets getBorderInsets(Component c) {
		return new Insets(!title.equals("") ? borderPadding + titleHeight : borderPadding, borderPadding, borderPadding, borderPadding);
	}

	@Override
	public boolean isBorderOpaque() {
		return false;
	}
}
