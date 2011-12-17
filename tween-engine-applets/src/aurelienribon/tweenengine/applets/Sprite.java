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
	private float x;
	private float y;

	public Sprite(String gfxName) {
		try {
			image = ImageIO.read(Sprite.class.getResource("/aurelienribon/tweenengine/applets/gfx/" + gfxName));
		} catch (IOException ex) {
		}
	}

	public void draw(Graphics2D gg) {
		gg.translate(x, y);
		gg.drawImage(image, null, -image.getWidth()/2, -image.getHeight()/2);
		gg.translate(-x, -y);
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}
}
