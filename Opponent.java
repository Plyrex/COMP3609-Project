// import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;
import java.awt.Image;

public class Opponent {

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

   private Random random;

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

   public Opponent (GamePanel p, int xPos, int yPos, Car bat, int enemy, int speed) {
      panel = p;
      // dimension = panel.getSize();
      backgroundColour = panel.getBackground ();

      width = 58;
      height = 52;

      random = new Random();

      x = xPos;
      y = yPos;

      enemy_num= enemy;
      setLocation();

      dx = speed;			// move side to side
      dy = height;			// would like to drop down after hitting wall

      this.bat = bat;
      this.bullet= null;
      this.oppBullet= null;
      
      soundManager = SoundManager.getInstance();
      alienImage = ImageManager.loadImage ("images/opp.png");
      imageFX1= null;

      rand= random.nextInt(2);
      if(rand==0){
         side= true;
      }else{
         side= false;
      }

   }

   
   public void setLocation() {
      // panel.spawnEnemy(x, y, this.height, this.width);
      int panelWidth = panel.getWidth();
      x = random.nextInt (panelWidth - width);
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

         // head = new Ellipse2D.Double(x, y, width, height);

         // g2.setColor(Color.RED); 
         // g2.fill(head);

         // g2.setColor(Color.BLACK);	 
         // g2.draw(head);

         // g2.setColor(Color.BLACK);

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

   public void addBullet(Bullet bullet){
      this.bullet= bullet;
   }

   public void shoot(){
      if(!panel.enemyBulletExist(enemy_num))
         panel.enemyShoot(enemy_num, this.x, this.y, this.width);
   }

   public void move() {
      int height = panel.getHeight();
      int width= panel.getWidth();
      boolean CarCollision = collidesWithCar();
      boolean BulletCollision= collidesWithBullet();

      if (!panel.isVisible ()) return;

      long now= System.currentTimeMillis();
      if(now - lastShotTime>= shotDelay){
         shoot();
         lastShotTime= now;
         shotDelay= 2000+ random.nextInt(1000);
      }

      if(this.side){
         if(x>= width - this.width){
            y+= dy;
            this.side= false;
         }
         x+= dx;
      }
      else if(!this.side){
         x-= dx;
         if(x<= 0){
            y+= dy;
            this.side= true;
         }
      }
          
      if (CarCollision) {
         soundManager.playClip("playerHit", false);
         panel.loseLife(1);
         if(panel.getLifeTotal()<= 0){
            soundManager.playClip("death", false);
            panel.destroyed(bat.getX(), bat.getY(), bat.getHeight(), bat.getWidth());
            panel.endGame();
            return;
         }
         // setLocation();
         dx= 0;
         dy= 0;
         panel.killEnemy(x, y, this.height, this.width, 0, enemy_num, 1);
         panel.destroyed(bat.getX(), bat.getY(), bat.getHeight(), bat.getWidth());
      }

      if(BulletCollision){
         soundManager.playClip("hit", false);
         
         // setLocation();
         dx= 0;
         dy= 0;
         panel.addPoints(1);
         // bullet.erase();
         bullet= null;
         panel.deleteBullet();
         panel.killEnemy(x, y, this.height, this.width, 0, enemy_num, 0);
      }

      if (y > height) {
         panel.loseLife(3);
         panel.destroyed(bat.getX(), bat.getY(), bat.getHeight(), bat.getWidth());
         panel.endGame();
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

   public boolean collidesWithBullet() {
      Rectangle2D.Double myRect = getBoundingRectangle();
      Rectangle2D.Double bulletRect = null;
      if(bullet!= null){
         bulletRect = bullet.getBoundingRectangle();
         return myRect.intersects(bulletRect); 
      }

      return false;
   }

   public int getY(){
      return this.y;
   }

}