// import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;
import java.awt.Image;

public class HealthPickup {

   private GamePanel panel;

   private int x;
   private int y;

   private int width;
   private int height;

   Ellipse2D.Double head;	// ellipse drawn for face

   private int dx;		// increment to move along x-axis
   private int dy;		// increment to move along y-axis

   private Color backgroundColour;
   // private Dimension dimension;

   private Random random= new Random();

   private Car bat;
   private Bullet bullet;
   private EnemyBullet oppBullet;
   private SoundManager soundManager;
   private Image alienImage;
   private ImageFX imageFX1;
   private int enemy_num; //keeps track of which enemy this is
   private int rand;
   private boolean side;
   private long lastShotTime, shotDelay;

   public HealthPickup (GamePanel p, Car bat, int x, int y) {
      panel = p;
      // dimension = panel.getSize();
      backgroundColour = panel.getBackground ();

      width = 25;
      height = 25;

      random = new Random();

      this.x = x;
      this.y = y;

      // setLocation();

      dx = 0;			// move side to side
      dy = 0;			// would like to drop down after hitting wall

      this.bat = bat;
      
      soundManager = SoundManager.getInstance();
      alienImage = ImageManager.loadImage ("images/health.png");
      imageFX1= null;

   }

   
   public void setLocation() {
      int panelWidth = panel.getWidth();
      int panelHeight = panel.getHeight();
      x = random.nextInt (panelWidth/2, panelWidth);
      y = 10;
      dx+= 2;
   }

   // public void spawn(){
   //    while(this.y< this.height){
   //       this.y+= 2;
   //    }
   // }

   public void draw (Graphics2D imageContext) {
      Graphics g = panel.getGraphics ();
      Graphics2D g2 = (Graphics2D) g;

      imageContext.drawImage(alienImage, x, y, width, height, null);

      g.dispose();
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
      int height = panel.getHeight();
      int width= panel.getWidth();
      boolean CarCollision = collidesWithCar();

      if (!panel.isVisible ()) return;
          
      if (CarCollision) {
         soundManager.playClip("pickup", false);
         if(panel.getLifeTotal()>= 5){
            panel.addPoints(5);
         }else{
            panel.addLife(2);
         }

         panel.removeHealth();
      }

   }

   public boolean isOnOpponent(int x, int y) {
      if (head == null)
      	  return false;

      Rectangle2D.Double myRect = getBoundingRectangle();
      
      return myRect.contains(x, y);
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