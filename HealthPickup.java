import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.Image;

public class HealthPickup extends PowerUp{
   public HealthPickup (GamePanel p, Car bat, int x, int y) {
      panel = p;
      backgroundColour = panel.getBackground ();

      width = 25;
      height = 25;

      this.x = x;
      this.y = y;

      this.bat = bat;
      
      soundManager = SoundManager.getInstance();
      alienImage = ImageManager.loadImage ("images/health.png");
   }

   // public void draw (Graphics2D imageContext) {
   //    Graphics g = panel.getGraphics ();
   //    Graphics2D g2 = (Graphics2D) g;

   //    imageContext.drawImage(alienImage, x, y, width, height, null);

   //    g.dispose();
   // }

//    public void erase() {
//       Graphics g = panel.getGraphics();
//       Graphics2D g2 = (Graphics2D) g;

//       g2.setColor (backgroundColour);
//       g2.fill (new Ellipse2D.Double (x, y, width, height));

//       g2.setColor(backgroundColour);	 
//       g2.draw(new Ellipse2D.Double (x, y, width, height));

//       g.dispose();
//   }

   public void move() {
      boolean CarCollision = collidesWithCar();

      if (!panel.isVisible ()) return;
          
      if (CarCollision) {
         soundManager.playClip("pickup", false);
         System.out.println("Health Picked Up");
         if(panel.getLifeTotal()>= 5){
            panel.addPoints(5);
         }else{
            panel.addLife(2);
         }

      }

   }

   // public Rectangle2D.Double getBoundingRectangle() {
   //    return new Rectangle2D.Double (x, y, width, height);
   // }

   
   // public boolean collidesWithCar() {
   //    Rectangle2D.Double myRect = getBoundingRectangle();
   //    Rectangle2D.Double batRect = bat.getBoundingRectangle();
      
   //    return myRect.intersects(batRect); 
   // }

   // public int getX(){
   //    return this.x;
   // }

   // public int getY(){
   //    return this.y;
   // }

}