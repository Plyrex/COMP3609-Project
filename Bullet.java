import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;

public class Bullet {
    private int x, y;
    private int dx, dy;
    private int size = 12; // ADJUST FOR SIZE
    private int speed = 10;
    private GamePanel panel;
    private Image bulletImage, bulletUpImage, bulletDownImage, bulletLeftImage, bulletRightImage;
    private int carDirection;

    public Bullet(GamePanel panel, int height, int width, int startX, int startY, int direction) {
        this.panel = panel;

        if(direction== 0){ //up
            this.x = startX + width/2;
            this.y = startY;
        }else if(direction== 1){ //down
            this.x = startX + width/2;
            this.y = startY+ height;
        }else if(direction== 2){ //left
            this.x = startX;
            this.y = startY+ height/2;
        }else if(direction== 3){ //right
            this.x = startX + width;
            this.y = startY+ height/2;
        }
        
        this.dx = 0;
        this.dy = -speed;
        this.carDirection= direction;

        this.bulletUpImage= ImageManager.loadImage("images/bulletUp.png");
        this.bulletDownImage= ImageManager.loadImage("images/bulletDown.png");
        this.bulletLeftImage= ImageManager.loadImage("images/bulletLeft.png");
        this.bulletRightImage= ImageManager.loadImage("images/bulletRight.png");
        this.bulletImage = bulletUpImage;
    }

    public void shoot() {
        if(carDirection== 0){
            dx=0;
            dy= -speed;
            bulletImage= bulletUpImage;
        }else if(carDirection== 1){
            dx=0;
            dy= speed;
            bulletImage= bulletDownImage;
        }else if(carDirection== 2){
            dx= -speed;
            dy=0;
            bulletImage= bulletLeftImage;
        }else if(carDirection== 3){
            dx= speed;
            dy=0;
            bulletImage= bulletRightImage;
        }
        x += dx;
        y += dy;
    }

    public void draw(Graphics2D g2) {
        if (bulletImage != null) {
            g2.drawImage(bulletImage, x - size/2, y - size/2, size, size, null);
        } else {
            g2.setColor(Color.YELLOW);
            g2.fillOval(x - size/2, y - size/2, size, size);
        }
    }

    public boolean isOffScreen() {
        return x < 0 || x > panel.getWidth() || y < 0 || y > panel.getHeight();
    }

    public Rectangle2D.Double getBoundingRectangle() {
        return new Rectangle2D.Double(x - size/2, y - size/2, size, size);
    }
}
