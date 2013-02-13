package aurelienribon.tweenengine.applets;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class Sprite {
	private BufferedImage image;
	private float x = 0;
	private float y = 0;
	private float scaleX = 1;
	private float scaleY = 1;
	private boolean isCentered = true;
	private boolean isVisible = true;

	public Sprite(String gfxName) {
		try {
			image = ImageIO.read(Sprite.class.getResource("/aurelienribon/tweenengine/applets/gfx/" + gfxName));
		} catch (IOException ex) {
		}
	}

	public void draw(Graphics2D gg) {
		if (!isVisible) return;
		gg = (Graphics2D) gg.create();
		gg.translate(x, y);
		gg.scale(scaleX, scaleY);
		gg.drawImage(image, null, isCentered ? -image.getWidth()/2 : 0, isCentered ? -image.getHeight()/2 : 0);
		gg.dispose();
	}

	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void setScale(float scaleX, float scaleY) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}

	public Sprite setCentered(boolean isCentered) {
		this.isCentered = isCentered;
		return this;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getScaleX() {
		return scaleX;
	}

	public float getScaleY() {
		return scaleY;
	}

	public boolean isVisible() {
		return isVisible;
	}
}
