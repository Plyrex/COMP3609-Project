import java.util.Random;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;

public class DisappearFX implements ImageFX {

	private int WIDTH;		// width of the image
	private int HEIGHT;		// height of the image
	private static final int YPOS = 250;		// vertical position of the image

	private GamePanel panel;

	private int x;
	private int y;

	private BufferedImage spriteImage;		// image for sprite effect
	private BufferedImage copy;			// copy of image

	Graphics2D g2;

	int time, timeChange;				// to control when the image is grayed
	int alpha, alphaChange;				// alpha value (for alpha transparency byte)

	int enemy_num;


	public DisappearFX (GamePanel p, int xPos, int yPos, int height, int width, String name, int enemy) {
		panel = p;

		enemy_num= enemy;
		HEIGHT= height;
		WIDTH= width;

		Random random = new Random();
		x = xPos;
		y = yPos;

		time = 0;				// range is 0 to 10
		timeChange = 1;				// set to 1

		alpha = 0;				// set to 255 (fully opaque)
		alphaChange = 10;			// how to update alpha in game loop

		spriteImage = ImageManager.loadBufferedImage(name);
		copy = ImageManager.copyImage(spriteImage);		
							//  make a copy of the original image

	}


	public void draw (Graphics2D g2) {

		int imWidth = copy.getWidth();
		int imHeight = copy.getHeight();

    		int [] pixels = new int[imWidth * imHeight];
    		copy.getRGB(0, 0, imWidth, imHeight, pixels, 0, imWidth);

    		int a, red, green, blue, newValue;

		for (int i=0; i<pixels.length; i++) {

			a = (pixels[i] >> 24);
			red = (pixels[i] >> 16) & 255;
			green = (pixels[i] >> 8) & 255;
			blue = pixels[i] & 255;

/*
			newValue = blue | (green << 8) | (red << 16) | (alpha << 24);
			pixels[i] = newValue;
*/

				
			if (a != 0) {
				newValue = blue | (green << 8) | (red << 16) | (alpha << 24);
				pixels[i] = newValue;
			}


		}
  
    		copy.setRGB(0, 0, imWidth, imHeight, pixels, 0, imWidth);	

		g2.drawImage(copy, x, y, WIDTH, HEIGHT, null);

	}


	public Rectangle2D.Double getBoundingRectangle() {
		return new Rectangle2D.Double (x, y, WIDTH, HEIGHT);
	}


	public void update() {				// modify time and change the effect if necessary
	
		alpha = alpha + alphaChange;

		if (alpha >= 255)			
			panel.endDisappear(enemy_num);
	}

}