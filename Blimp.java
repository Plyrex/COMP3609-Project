import java.util.Random;

public class Blimp extends Enemy{
    private int lives= 15;
    public Blimp(GamePanel p, int xPos, int yPos, Car bat, int enemy, int speed) {
        panel = p;
        // dimension = panel.getSize();
        backgroundColour = panel.getBackground ();

        width = 150;
        height = 150;

        random = new Random();

        x = xPos;
        y = yPos;

        enemy_num= enemy;
        //setLocation();

        dx = speed;			// move side to side
        dy = speed;			// would like to drop down after hitting wall

        this.bat = bat;
        this.bullet= null;
        this.oppBullet= null;
        
        soundManager = SoundManager.getInstance();
        imageUp= ImageManager.loadImage ("images/blimpUp.png");
        imageDown= ImageManager.loadImage ("images/blimpDown.png");
        imageLeft= ImageManager.loadImage ("images/blimpLeft.png");
        imageRight= ImageManager.loadImage ("images/blimpRight.png");
        alienImage = imageUp;
        imageFX1= null;

        rand= random.nextInt(4);

        type= 2;
    }

    // public void setLocation(){
    //     int panelWidth = panel.getWidth();
    //     int panelHeight= panel.getHeight();
    //     x = random.nextInt (panelWidth - width);
    //     y = random.nextInt(panelHeight- height);
    //     dx+= 2;
    // };

    public void move() {
        int height = panel.getHeight();
        int width= panel.getWidth();
        boolean CarCollision = collideWithPlayer();
        boolean BulletCollision= collideWithBullet();
  
        if (!panel.isVisible ()) return;
  
        long now= System.currentTimeMillis();
        if(now - lastShotTime>= shotDelay){
           shoot();
           lastShotTime= now;
           shotDelay= 2000+ random.nextInt(1000);
        }
        if(rand== 0){
            alienImage= imageUp;
            y-=dy;
        }
        else if(rand== 1){
            alienImage= imageDown;
            y+=dy;
        }
        else if(rand== 2){
            alienImage= imageLeft;
            x-= dx;
        }
        else if(rand==3){
            alienImage= imageRight;
            x+= dx;
        }
        
        rand1= random.nextInt(10);
        if(rand1>7)
            rand= random.nextInt(4);
  
        // if(this.side){
        //    if(x>= width - this.width){
        //       y+= dy;
        //       this.side= false;
        //    }
        //    x+= dx;
        // }
        // else if(!this.side){
        //    x-= dx;
        //    if(x<= 0){
        //       y+= dy;
        //       this.side= true;
        //    }
        // }
            
        if (CarCollision) {
           soundManager.playClip("playerHit", false);
           panel.loseLife(1);
           if(panel.getLifeTotal()<= 0){
              soundManager.playClip("death", false);
              panel.destroyed(bat.getX(), bat.getY(), bat.getHeight(), bat.getWidth());
              panel.endGame();
              return;
           }

           lives-= 1;
           if(lives== 0){
               panel.killEnemy(x, y, this.height, this.width, 0, enemy_num, 1);
               panel.destroyed(bat.getX(), bat.getY(), bat.getHeight(), bat.getWidth());
           }
        }
  
        // if(BulletCollision){
        //    soundManager.playClip("hit", false);
           
        //    // setLocation();
        //    dx= 0;
        //    dy= 0;
        //    panel.addPoints(1);
        //    // bullet.erase();
        //    bullet= null;
        //    panel.deleteBullet();
        //    panel.killEnemy(x, y, this.height, this.width, 0, enemy_num, 0);
        // }
    }
    @Override
public boolean takeDamage() {
    lives--;
    return lives <= 0;
}
}
