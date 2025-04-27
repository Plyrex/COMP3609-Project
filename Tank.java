import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Random;

public class Tank extends Enemy{
    public Tank(GamePanel p, int xPos, int yPos, Car bat, int enemy, int speed) {
        panel = p;
        // dimension = panel.getSize();
        backgroundColour = panel.getBackground ();

        width = 30;
        height = 52;

        random = new Random();

        x = xPos;
        y = yPos;

        enemy_num= enemy;
        //setLocation();

        dx = speed;			// move side to side
        dy = height;			// would like to drop down after hitting wall

        this.bat = bat;
        this.bullet= null;
        this.oppBullet= null;
        
        soundManager = SoundManager.getInstance();
        alienImage = ImageManager.loadImage ("images/tank.png");
        imageFX1= null;

        rand= random.nextInt(2);
        if(rand==0){
            side= true;
        }else{
            side= false;
        }

    }

    public void setLocation(){
        int panelWidth = panel.getWidth();
        int panelHeight= panel.getHeight();
        x = random.nextInt (panelWidth - width);
        y = random.nextInt(panelHeight- height);
        dx+= 2;
    }
    

    // public void move() {
    //     int height = panel.getHeight();
    //     int width= panel.getWidth();
    //     boolean CarCollision = collideWithPlayer();
    //     boolean BulletCollision= collideWithBullet();
  
    //     if (!panel.isVisible ()) return;
  
    //     long now= System.currentTimeMillis();
    //     if(now - lastShotTime>= shotDelay){
    //        shoot();
    //        lastShotTime= now;
    //        shotDelay= 2000+ random.nextInt(1000);
    //     }
  
    //     if(this.side){
    //        if(x>= width - this.width){
    //           y+= dy;
    //           this.side= false;
    //        }
    //        x+= dx;
    //     }
    //     else if(!this.side){
    //        x-= dx;
    //        if(x<= 0){
    //           y+= dy;
    //           this.side= true;
    //        }
    //     }
            
    //     if (CarCollision) {
    //        soundManager.playClip("playerHit", false);
    //        panel.loseLife(1);
    //        if(panel.getLifeTotal()<= 0){
    //           soundManager.playClip("death", false);
    //           panel.destroyed(bat.getX(), bat.getY(), bat.getHeight(), bat.getWidth());
    //           panel.endGame();
    //           return;
    //        }
    //        // setLocation();
    //        dx= 0;
    //        dy= 0;
    //        panel.killEnemy(x, y, this.height, this.width, 0, enemy_num, 1);
    //        panel.destroyed(bat.getX(), bat.getY(), bat.getHeight(), bat.getWidth());
    //     }
  
    //     if(BulletCollision){
    //        soundManager.playClip("hit", false);
           
    //        // setLocation();
    //        dx= 0;
    //        dy= 0;
    //        panel.addPoints(1);
    //        // bullet.erase();
    //        bullet= null;
    //        panel.deleteBullet();
    //        panel.killEnemy(x, y, this.height, this.width, 0, enemy_num, 0);
    //     }
    // }
}
