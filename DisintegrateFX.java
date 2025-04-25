import java.util.Random;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import java.awt.Color;

public class DisintegrateFX implements ImageFX {

	private int WIDTH;		// width of the image
	private int HEIGHT;		// height of the image
	private int YPOS;		// vertical position of the image

	private GamePanel panel;

	private int x;
	private int y;

	private BufferedImage spriteImage;		// image for sprite effect
	private BufferedImage copy;			// copy of image

	Graphics2D g2;

	int time, timeChange;				// to control when the image is grayed


	public DisintegrateFX (GamePanel p, int xPos, int yPos, double height2, double width2, String name) {
		panel = p;

		HEIGHT = (int) height2;
		WIDTH = (int) width2;

		Random random = new Random();
		x = xPos;
		y = yPos;

		time = 0;
		timeChange = 1;
		spriteImage = ImageManager.loadBufferedImage(name);
		
		if (spriteImage == null) {
			spriteImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = spriteImage.createGraphics();
			g.setColor(Color.RED);
			g.fillRect(0, 0, WIDTH, HEIGHT);
			g.dispose();
		}
		copy = ImageManager.copyImage(spriteImage);		
		

	}


  	public void eraseImageParts(BufferedImage im, int interval) {
  		if (im == null) {
  			return;
  		}

    		int imWidth = im.getWidth();
    		int imHeight = im.getHeight();

    		int[] pixels = new int[imWidth * imHeight];
    		im.getRGB(0, 0, imWidth, imHeight, pixels, 0, imWidth);

		for (int i = 0; i < pixels.length; i = i + interval) {
      			pixels[i] = 0;
		}
  
    		im.setRGB(0, 0, imWidth, imHeight, pixels, 0, imWidth);
  	}


	public void draw(Graphics2D g2) {
		if (copy == null) {
			if (spriteImage == null) {
				g2.setColor(Color.RED);
				g2.fillRect(x, y, WIDTH, HEIGHT);
				return;
			} else {
				copy = ImageManager.copyImage(spriteImage);
				if (copy == null) return;
			}
		}
		TileMap tileMap = panel.getTileMap();
		int drawX = x;
		int drawY = y;
		
		if (tileMap != null) {
			drawX = (int)(x - tileMap.getCameraX());
			drawY = (int)(y - tileMap.getCameraY());
		}

		if (time == 5)
			eraseImageParts(copy, 11);
		else if (time == 6)
			eraseImageParts(copy, 7);
		else if (time == 7)
			eraseImageParts(copy, 5);
		else if (time == 10)
			eraseImageParts(copy, 3);
		else if (time == 12)
			eraseImageParts(copy, 2);
		else if (time == 14)
			eraseImageParts(copy, 1);
		else if (time >= 15)
			copy = ImageManager.copyImage(spriteImage);

		g2.drawImage(copy, drawX, drawY, WIDTH, HEIGHT, null);

	}


	public Rectangle2D.Double getBoundingRectangle() {
		return new Rectangle2D.Double (x, y, WIDTH, HEIGHT);
	}


	public void update() {				// modify time
	
		time = time + timeChange;

		if (time >= 15)			
			panel.endDisintegrate();
	}

}