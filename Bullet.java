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
    private Image bulletImage;

    public Bullet(GamePanel panel, int width, int startX, int startY) {
        this.panel = panel;
        this.x = startX + width/2;
        this.y = startY;
        this.dx = 0;
        this.dy = -speed;

        bulletImage = ImageManager.loadImage("images/enemyBullet.png");
    }

    public void shoot() {
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
