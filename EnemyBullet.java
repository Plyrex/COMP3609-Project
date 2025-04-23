import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.Image;

public class EnemyBullet {

   private GamePanel panel;

   private int x;
   private int y;

   private int width;
   private int height;

   // Ellipse2D.Double bullet;	// ellipse drawn for bullet
   // Double hitBox;	// ellipse drawn for bullet

   private int dx;		// increment to move along x-axis
   private int dy;		// increment to move along y-axis

   private Color backgroundColour;
   private SoundManager soundManager;
   private Image bulletImage;
   private Car bat;
   private int enemy_num;

   public EnemyBullet (GamePanel p, int width, int xPos, int yPos, Car bat, int enemy) {
      panel = p;
      backgroundColour = panel.getBackground ();
      soundManager= SoundManager.getInstance();

      this.width = 15;
      this.height = 15;
      enemy_num= enemy;

      this.bat= bat;

      x = xPos;
      y = yPos;

      setLocation(width, xPos, yPos);

      dx = 0;			// no movement along x-axis
      dy = 5;			// would like the alien to drop down

      bulletImage= ImageManager.loadImage("images/enemyBullet.png");
 
   }

   
   public void setLocation(int width, int x, int y) {
      this.x= x+ (width/2) - 7;
      this.y= y;
   }

   public void goAway(int x, int y) {
      this.x= x;
      this.y= y;
   }

   public void draw (Graphics2D imageContext) {
      Graphics g = panel.getGraphics ();
      Graphics2D g2 = (Graphics2D) g;

      imageContext.drawImage(bulletImage, x, y, width, height, null);

         // bullet = new Ellipse2D.Double(x, y, width, height);

         // g2.setColor(Color.BLACK); 
         // g2.fill(bullet);

         // g2.setColor(Color.BLACK);	 
         // g2.draw(bullet);

         // g2.setColor(Color.BLACK);

         

      g.dispose();
   }

   public void erase() {
        Graphics g = panel.getGraphics();
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor (backgroundColour);
        g2.fill (new Ellipse2D.Double (x, y, width, height));

        g2.setColor (backgroundColour);
        g2.draw (new Ellipse2D.Double (x, y, width, height));

        g.dispose();
    }


   public void shoot() {
      boolean CarCollision= collidesWithCar();

      if (!panel.isVisible ()) 
         return;
      
      y = y + dy;

      if (y >= panel.getHeight()) {
         // this.erase();
         panel.enemyDeleteBullet(enemy_num);
      }

      if(CarCollision){
         soundManager.playClip("playerHit", false);
         panel.loseLife(1);
         if(panel.getLifeTotal()<= 0){
            soundManager.playClip("death", false);
            panel.destroyed(bat.getX(), bat.getY(), bat.getHeight(), bat.getWidth());
            panel.endGame();
            return;
         }
         panel.enemyDeleteBullet(enemy_num);
         panel.destroyed(bat.getX(), bat.getY(), bat.getHeight(), bat.getWidth());
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

}
