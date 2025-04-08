// import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import java.awt.Image;


public class Car {

   private JPanel panel;
   private int x;
   private int y;
   private int width;
   private int height;

   private int dx;
   private int dy;
   private double velX= 0, velY= 0; 

   private Rectangle2D.Double car;

   private Color backgroundColour;
   // private Dimension dimension;

   private Image batImage;
   private Image batLeftImage;
   private Image batRightImage;
   private Image carImage, carUpImage, carLeftImage, carRightImage, carDownImage;
   private SoundManager soundManager;

   public Car (JPanel p, int xPos, int yPos) {
      panel = p;
      // dimension = panel.getSize();
      backgroundColour = panel.getBackground();
      x = xPos;
      y = yPos;

      dx = 5;
      dy = 5;

      width = 50;
      height = 50;

      carUpImage = ImageManager.loadImage ("images/car.png");
      carLeftImage= ImageManager.loadImage ("images/carLeft.png");
      carRightImage= ImageManager.loadImage ("images/carRight.png");
      carDownImage= ImageManager.loadImage ("images/carDown.png");
      carImage= carUpImage;
   }

   public void goAway(int x, int y){
      this.x= x;
      this.y= y;
   }
   public void draw (Graphics2D imageContext) {
      Graphics g = panel.getGraphics ();
      Graphics2D g2 = (Graphics2D) g;

      imageContext.drawImage(carImage, x, y, width, height, null);

      // car = new Rectangle2D.Double(x, y, width, height);
      // g2.setColor(Color.BLUE);
      // g2.fill(car);

      // g2.setColor(Color.BLACK);
      // g2.draw(car);

      g.dispose();
   }


   public void erase () {
      Graphics g = panel.getGraphics ();
      Graphics2D g2 = (Graphics2D) g;

      g2.setColor (backgroundColour);
      g2.fill (new Rectangle2D.Double (x, y, width, height));

      g2.setColor(backgroundColour);	 
      g2.draw(new Rectangle2D.Double (x, y, width, height));

      g.dispose();
   }

   public void tick(){
      x+= velX;
      y+= velY;
   }

	public void move (int direction) {

		if (!panel.isVisible ()) return;

      int panelWidth = panel.getWidth();
      int panelHeight = panel.getHeight();
      
		if (direction == 1) {			// going left
         carImage= carLeftImage;
         setVelX(-5);
         if (x < -width)
	         x = panelWidth;
		}
		else if (direction == 2) {			// going right
         carImage= carRightImage;
         setVelX(5);
         if (x > panelWidth)
            x = -width;
		}
      else if(direction == 3){ //going up
         carImage= carUpImage;
         setVelY(-5);
         if (y <= 0){
	         y = 0;
            setVelY(0);
         }
      }
      else if(direction == 4){ //going down
         carImage= carDownImage;
         setVelY(5);
         if (this.y >= panelHeight - this.height){
            this.y = panelHeight - this.height;
            setVelY(0);
         }
      }
      // else if(direction == 1 && direction== 3){ //going diagonally up and left
      //    x-= dx;
      //    y+= dy;
      // }
      // else if(direction == 1 && direction== 3){ //going diagonally up and right
      //    x+= dx;
      //    y+= dy;
      // }
      // else if(direction == 1 && direction== 3){ //going diagonally down and left
      //    x-= dx;
      //    y-= dy;
      // }
      // else if(direction == 1 && direction== 3){ //going diagonally down and right
      //    x+= dx;
      //    y-= dy;
      // }
	}

   public boolean isOnCar(int x, int y) {
      if (car == null)
      	  return false;

      return car.contains(x, y);
   }

   public Rectangle2D.Double getBoundingRectangle() {
      return new Rectangle2D.Double (x, y, width, height);
   }

   public int getHeight(){
      return height;
   }

   public int getWidth(){
      return width;
   }

   public int getX(){
      return x;
   }

   public int getY(){
      return y;
   }

   public void setVelX(double velX){
      this.velX= velX;
   }

   public void setVelY(double velY){
      this.velY= velY;
   }
//VELOCITY GETTERS
   public double getVelX() {
      return velX;
   }

   public double getVelY() {
      return velY;
   }

}