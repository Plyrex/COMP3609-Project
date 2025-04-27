import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Graphics2D;


/**
    The StripAnimation class creates an animation from a strip file.
*/
public class StripAnimation {
	
	Animation animation;

	private int x;		// x position of animation
	private int y;		// y position of animation

	private int width;
	private int height;

	private int dx;		// increment to move along x-axis
	private int dy;		// increment to move along y-axis

	public StripAnimation(String name, int pieces, boolean loop) {

		animation = new Animation(loop);	// run animation once

        	dx = 0;		// increment to move along x-axis
        	dy = 0;	// increment to move along y-axis

		// load images from strip file

		Image stripImage = ImageManager.loadImage(name);

		int imageWidth = (int) stripImage.getWidth(null) / pieces;
		int imageHeight = stripImage.getHeight(null);

		for (int i=0; i<pieces; i++) {

			BufferedImage frameImage = new BufferedImage (imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) frameImage.getGraphics();
     
			g.drawImage(stripImage, 
					0, 0, imageWidth, imageHeight,
					i*imageWidth, 0, (i*imageWidth)+imageWidth, imageHeight,
					null);

			animation.addFrame(frameImage, 100);
		}
	
	}


	public void start(int xPos, int yPos) {
		x = xPos;
        	y = yPos;
		animation.start();
	}

	public void stop(){
		animation.stop();
	}

	
	public void update() {
		if (!animation.isStillActive())
			return;

		animation.update();
		x = x + dx;
		y = y + dy;
	}


	public void draw(Graphics2D g2, double cameraX, double cameraY) {
			int drawX = (int)(x - cameraX);
			int drawY = (int)(y - cameraY);
		if (!animation.isStillActive())
			return;

		g2.drawImage(animation.getImage(), drawX, drawY, 70, 50, null);
	}

}
