package aurelienribon.utils.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class ImagePanel extends JPanel {
	private BufferedImage background;
	private BufferedImage image;
	private boolean useRegion;
	private int x, y, width, height;

	public void setBackground(URL bgURL) {
		try {
			background = ImageIO.read(bgURL);
		} catch (IOException ex) {
		}
	}

	public void clearImage() {
		image = null;
		repaint();
	}

	public void setImage(BufferedImage img) {
		image = img;
		repaint();
	}

	public void setImage(File img) {
		setImage(img, 0, 0, 0, 0);
		useRegion = false;
	}

	public void setImage(URL imgUrl) {
		setImage(imgUrl, 0, 0, 0, 0);
		useRegion = false;
	}

	public void setImage(File img, int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		useRegion = true;

		try {
			image = img != null ? ImageIO.read(img) : null;
			repaint();
		} catch (IOException ex) {
			Logger.getLogger(ImagePanel.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void setImage(URL imgUrl, int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		useRegion = true;

		try {
			image = imgUrl != null ? ImageIO.read(imgUrl) : null;
			repaint();
		} catch (IOException ex) {
			Logger.getLogger(ImagePanel.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D gg = (Graphics2D)g;
		gg.setColor(Color.LIGHT_GRAY);
		gg.fillRect(0, 0, getWidth(), getHeight());

		if (background != null) {
			TexturePaint paint = new TexturePaint(background, new Rectangle(0, 0, background.getWidth(), background.getHeight()));
			gg.setPaint(paint);
			gg.fillRect(0, 0, getWidth(), getHeight());
			gg.setPaint(null);
		}

		if (image != null && !useRegion) {
			float panelRatio = (float)getWidth() / (float)getHeight();
			float imgRatio = (float)image.getWidth() / (float)image.getHeight();

			if (imgRatio > panelRatio) {
				float tw = (float)getWidth();
				float th = (float)getWidth() / imgRatio;
				gg.drawImage(image, 0, (int)(getHeight()/2 - th/2), (int) tw, (int) th, null);
			} else {
				float tw = (float)getHeight() * imgRatio;
				float th = (float)getHeight();
				gg.drawImage(image, (int)((float)getWidth()/2 - tw/2), 0, (int) tw, (int) th, null);
			}

		} else if (image != null && useRegion) {
			float panelRatio = (float)getWidth() / (float)getHeight();
			float imgRatio = (float)width / (float)height;

			if (imgRatio > panelRatio) {
				int tw = getWidth();
				int th = (int) (getWidth() / imgRatio);
				int tx = 0;
				int ty = getHeight()/2 - th/2;
				gg.drawImage(image, tx, ty, tx + tw, ty + th, x, y, x + width, y + width, null);
			} else {
				int tw = (int) (getHeight() * imgRatio);
				int th = getHeight();
				int tx = getWidth()/2 - tw/2;
				int ty = 0;
				gg.drawImage(image, tx, ty, tx + tw, ty + th, x, y, x + width, y + width, null);
			}
		}
	}
}
