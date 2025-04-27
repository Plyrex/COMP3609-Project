import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class EnemyBullet {
    private int x, y;
    private int dx, dy;
    private int size = 5;
    private int speed = 8; 
    private GamePanel panel;
    private Animation bulletAnimation;
    private Image bulletImage;
    private int enemyIndex;
    
    public EnemyBullet(GamePanel panel, int width, int startX, int startY, Car target, int enemyIndex) {
        this.panel = panel;
        this.enemyIndex = enemyIndex;
        this.x = startX + width/2; 
        this.y = startY;

        if (target != null) {
            int targetX = target.getX() + target.getWidth()/2;
            int targetY = target.getY() + target.getHeight()/2;
            int deltaX = targetX - x;
            int deltaY = targetY - y;
            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
            if(distance == 0)
                distance = 1;
            this.dx = (int)(speed * deltaX / distance);
            this.dy = (int)(speed * deltaY / distance);
        } else {
            this.dx = 0;
            this.dy = speed;
        }
        
        bulletImage= ImageManager.loadImage("images/enemyBullet.png");
        // bulletWidth = size;
        // bulletHeight = size;
        
        // try {
        //     Image stripImage = ImageManager.loadImage("images/special.png");
        //     if (stripImage != null) {
        //         int frameCount = 4;
        //         int imgWidth = stripImage.getWidth(null);
        //         int imgHeight = stripImage.getHeight(null);
                
        //         if (imgWidth > 0 && imgHeight > 0 && imgWidth >= frameCount) { //THIS IS TO FIX AN ILLEGAL ARGUMENT EXCEPTION ARGHH
        //             bulletWidth = imgWidth / frameCount;
        //             bulletHeight = imgHeight;
                    
        //             bulletAnimation = new Animation(true);
        //             for (int i = 0; i < frameCount; i++) {
        //                 BufferedImage frame = new BufferedImage(bulletWidth, bulletHeight, BufferedImage.TYPE_INT_ARGB);
        //                 Graphics2D g2d = frame.createGraphics();
        //                 g2d.drawImage(stripImage, 
        //                             0, 0, bulletWidth, bulletHeight, 
        //                             i * bulletWidth, 0, (i + 1) * bulletWidth, bulletHeight, 
        //                             null);
        //                 g2d.dispose();
        //                 bulletAnimation.addFrame(frame, 100);
        //             }
        //             bulletAnimation.start();
        //         }
        //     }
        // } catch (Exception e) {
        //     e.printStackTrace(); //incase of error bc cries
        // }
    }

    public void shoot() {
        x += dx;
        y += dy;
        if(bulletAnimation != null) {
            bulletAnimation.update();
        }
    }
    
    public void draw(Graphics2D g2, double cameraX, double cameraY) {
        int drawX = (int)(x - cameraX - size/2);
        int drawY = (int)(y - cameraY - size/2);
        if (bulletAnimation != null && bulletAnimation.isStillActive()) {
            g2.drawImage(bulletAnimation.getImage(), drawX, drawY, size, size, null);
        } else {
            g2.setColor(Color.RED);
            g2.fillOval(drawX, drawY, size, size);
        }
    }
    
    public boolean isOffScreen() {
        TileMap tileMap = panel.getTileMap();
        if (tileMap == null) return false;
        int worldWidth = tileMap.getWidthPixels();
        int worldHeight = tileMap.tilesToPixels(tileMap.getHeight());
        return x < 0 || x > worldWidth || y < 0 || y > worldHeight;
    }
    
    public Rectangle2D.Double getBoundingRectangle() {
        return new Rectangle2D.Double(x - size/2, y - size/2, size, size);
    }
    
    public int getEnemyIndex() {
        return enemyIndex;
    }
}
