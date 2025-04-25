import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public interface EntityInterface{
    void setLocation();
    void draw(Graphics2D imageConxtext, double cameraX, double cameraY);
    void erase();
    void shoot();
    void move();
    Rectangle2D.Double getBoundingRectangle();
    boolean collideWithPlayer();
    boolean collideWithBullet();
    int getX();
    int getY();
}
