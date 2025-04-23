import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public interface PowerUpInterface {
    void setLocation();
    void draw(Graphics2D imageContext);
    void erase();
    void move();
    Rectangle2D.Double getBoundingRectangle();
    boolean collideWithPlayer();
    int getX();
    int getY();
}
