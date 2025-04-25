import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.Color;
import java.util.Random;
import java.awt.Image;

public abstract class Enemy implements EntityInterface{
    protected GamePanel panel;

    protected int x;
    protected int y;

    protected int width;
    protected int height;

    protected int dx;		// increment to move along x-axis
    protected int dy;		// increment to move along y-axis

    protected Color backgroundColour;
    protected Random random;

    protected Car bat;
    protected Bullet bullet;
    protected EnemyBullet oppBullet;
    protected SoundManager soundManager;
    protected Image alienImage, imageUp, imageDown, imageLeft, imageRight;
    protected ImageFX imageFX1;
    protected int enemy_num; //keeps track of which enemy this is
    protected int rand, rand1;
    protected boolean side;
    protected long lastShotTime, shotDelay;

    public void setLocation(){
        int panelWidth = panel.getWidth();
        int panelHeight= panel.getHeight();
        x = random.nextInt (panelWidth - width);
        y = random.nextInt(panelHeight- height);
        dx+= 2;
    };

    public void draw(Graphics2D imageConxtext,  double cameraX, double cameraY){
        int drawX = (int)(x - cameraX);
        int drawY = (int)(y - cameraY);
        Graphics g = panel.getGraphics ();
        Graphics2D g2 = (Graphics2D) g;

        imageConxtext.drawImage(alienImage, drawX, drawY, width, height, null);
        g.dispose();
    };

    public void erase(){
        Graphics g = panel.getGraphics();
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor (backgroundColour);
        g2.fill (new Ellipse2D.Double (x, y, width, height));

        g2.setColor(backgroundColour);	 
        g2.draw(new Ellipse2D.Double (x, y, width, height));

        g.dispose();
    };

    public void shoot(){

    };

    public void move(){

    };

    public Rectangle2D.Double getBoundingRectangle(){
        return new Rectangle2D.Double (x, y, width, height);
    };

    public boolean collideWithPlayer(){
        Rectangle2D.Double myRect = getBoundingRectangle();
        Rectangle2D.Double batRect = bat.getBoundingRectangle();
      
        return myRect.intersects(batRect);
    };

    public boolean collideWithBullet(){
        Rectangle2D.Double myRect = getBoundingRectangle();
        Rectangle2D.Double bulletRect = null;
        if(bullet!= null){
            bulletRect = bullet.getBoundingRectangle();
            return myRect.intersects(bulletRect); 
        }

        return false;
    };

    public int getX(){
        return this.x;
    };

    public int getY(){
        return this.y;
    };
}
