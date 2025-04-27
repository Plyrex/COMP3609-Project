import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.Image;

public abstract class PowerUp{

   protected GamePanel panel;

   protected int x;
   protected int y;

   protected int width;
   protected int height;

   protected Ellipse2D.Double head;	// ellipse drawn for face

   protected Color backgroundColour;

   protected Car bat;
   protected SoundManager soundManager;
   protected Image alienImage;

   public void draw(Graphics2D g2, double cameraX, double cameraY) {
      int drawX = (int)(x - cameraX);
      int drawY = (int)(y - cameraY);
      g2.drawImage(alienImage, drawX, drawY, width, height, null);
  }

   public void erase() {
      Graphics g = panel.getGraphics();
      Graphics2D g2 = (Graphics2D) g;

      g2.setColor (backgroundColour);
      g2.fill (new Ellipse2D.Double (x, y, width, height));

      g2.setColor(backgroundColour);	 
      g2.draw(new Ellipse2D.Double (x, y, width, height));

      g.dispose();
  }

   public void move() {
      boolean CarCollision = collidesWithCar();

      if (!panel.isVisible ()) return;
          
      if (CarCollision) {
         soundManager.playClip("pickup", false);
         if(panel.getLifeTotal()>= 5){
            panel.addPoints(5);
         }else{
            panel.addLife(2);
         }

      }

   }

   public Rectangle2D.Double getBoundingRectangle() {
      return new Rectangle2D.Double (x, y, width, height);
   }

   
   public boolean collidesWithCar() {
      Rectangle2D.Double myRect = getBoundingRectangle();
      Rectangle2D.Double batRect = bat.getBoundingRectangle();
      
      return myRect.intersects(batRect); 
   }

   public int getX(){
      return this.x;
   }

   public int getY(){
      return this.y;
   }

}