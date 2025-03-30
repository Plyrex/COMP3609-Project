import java.util.Random;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;

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


	public DisintegrateFX (GamePanel p, int xPos, int yPos, int height, int width, String name) {
		panel = p;

		HEIGHT= height;
		WIDTH= width;

		Random random = new Random();
		x = xPos;
		y = yPos;

		time = 0;				// range is 0 to 70
		timeChange = 1;				// how to increment time in game loop

		spriteImage = ImageManager.loadBufferedImage(name);
		copy = ImageManager.copyImage(spriteImage);		
							//  make a copy of the original image

	}


  	public void eraseImageParts(BufferedImage im, int interval) {

    		int imWidth = im.getWidth();
    		int imHeight = im.getHeight();

    		int [] pixels = new int[imWidth * imHeight];
    		im.getRGB(0, 0, imWidth, imHeight, pixels, 0, imWidth);

		for (int i = 0; i < pixels.length; i = i + interval) {
      			pixels[i] = 0;    // make transparent (or black if no alpha)
		}
  
    		im.setRGB(0, 0, imWidth, imHeight, pixels, 0, imWidth);
  	}


	public void draw (Graphics2D g2) {

		if (time == 5)
			eraseImageParts(copy, 11);
		else
		if (time == 6)
			eraseImageParts(copy, 7);
		else
		if (time == 7)
			eraseImageParts(copy, 5);
		else
		if (time == 10)
			eraseImageParts(copy, 3);
		else
		if (time == 12)
			eraseImageParts(copy, 2);
		else
		if (time == 14)
			eraseImageParts(copy, 1);
		else
		if (time >= 15)
			copy = ImageManager.copyImage(spriteImage);

		g2.drawImage(copy, x, y, WIDTH, HEIGHT, null);

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