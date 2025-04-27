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
        int carCenterX = startX + width / 2;
        int carCenterY = startY + height / 2;

        if(direction == 0){
            this.x = carCenterX;
            this.y = startY;
        } else if(direction == 1){ 
            this.x = carCenterX;
            this.y = startY + height;
        } else if(direction == 2){
            this.x = startX;
            this.y = carCenterY;
        } else if(direction == 3){
            this.x = startX + width;
            this.y = carCenterY;
        }

        this.dx = 0;
        this.dy = -speed;
        this.carDirection = direction;

        this.bulletUpImage = ImageManager.loadImage("images/bulletUp.png");
        this.bulletDownImage = ImageManager.loadImage("images/bulletDown.png");
        this.bulletLeftImage = ImageManager.loadImage("images/bulletLeft.png");
        this.bulletRightImage = ImageManager.loadImage("images/bulletRight.png");
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
    public void draw(Graphics2D g2, double cameraX, double cameraY) {
        int drawX = (int)(x - cameraX - size/2);
        int drawY = (int)(y - cameraY - size/2);
        if (bulletImage != null) {
            g2.drawImage(bulletImage, drawX, drawY, size, size, null);
        } else {
            g2.setColor(Color.YELLOW);
            g2.fillOval(drawX, drawY, size, size);
        }
    }
    public Rectangle2D.Double getBoundingRectangle() {
        return new Rectangle2D.Double(x - size/2, y - size/2, size, size);
    }

    public boolean isOffScreen() {
        TileMap tileMap = panel.getTileMap();
        if (tileMap == null) return false;
        double cameraX = tileMap.getCameraX();
        double cameraY = tileMap.getCameraY();
        int screenWidth = panel.getWidth();
        int screenHeight = panel.getHeight();
        return x < cameraX || x > cameraX + screenWidth || y < cameraY || y > cameraY + screenHeight;
    }
    
}
