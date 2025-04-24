import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Bullet {
    private int x, y;
    private int dx, dy;
    private int size = 5;
    private int speed = 10;
    private GamePanel panel;
    private Animation bulletAnimation;
    private int bulletWidth;
    private int bulletHeight;

    public Bullet(GamePanel panel, int width, int startX, int startY) {
        this.panel = panel;
        this.x = startX + width/2;
        this.y = startY;
        this.dx = 0;
        this.dy = -speed;
        
        bulletWidth = size;
        bulletHeight = size;
        
        try {
            Image stripImage = ImageManager.loadImage("images/regular.png");
            if (stripImage != null) {
                int frameCount = 10;
                int imgWidth = stripImage.getWidth(null);
                int imgHeight = stripImage.getHeight(null);
                
                if (imgWidth > 0 && imgHeight > 0 && imgWidth >= frameCount) {
                    bulletWidth = imgWidth / frameCount;
                    bulletHeight = imgHeight;
                    
                    bulletAnimation = new Animation(true);
                    for (int i = 0; i < frameCount; i++) {
                        BufferedImage frame = new BufferedImage(bulletWidth, bulletHeight, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g2d = frame.createGraphics();
                        g2d.drawImage(stripImage, 
                                    0, 0, bulletWidth, bulletHeight, 
                                    i * bulletWidth, 0, (i + 1) * bulletWidth, bulletHeight, 
                                    null);
                        g2d.dispose();
                        bulletAnimation.addFrame(frame, 100);
                    }
                    bulletAnimation.start();
                }
            } else {
                System.out.println("Error: Bullet image not found."); //this was a copilot suggestion that i'll probably remove when i do enemies
            }
        } catch (Exception e) {
            System.out.println("Error loading bullet image: " + e.getMessage()); //same here for this bc i used try/catch for the image loading
        }
    }
    
    public void shoot() {
        x += dx;
        y += dy;
        
        if(bulletAnimation != null) {
            bulletAnimation.update();
        }
    }
    
    public void draw(Graphics2D g2) {
        if (bulletAnimation != null && bulletAnimation.isStillActive()) {
            g2.drawImage(bulletAnimation.getImage(), x - bulletWidth/2, y - bulletHeight/2, bulletWidth, bulletHeight, null);
        } else {
            g2.setColor(Color.YELLOW);
            g2.fillOval(x - size/2, y - size/2, size, size);
        }
    }
    
    public boolean isOffScreen() {
        return x < 0 || x > panel.getWidth() || y < 0 || y > panel.getHeight();
    }
    
    public Rectangle2D.Double getBoundingRectangle() {
        return new Rectangle2D.Double(x - bulletWidth/2, y - bulletHeight/2, bulletWidth, bulletHeight);
    }
}
