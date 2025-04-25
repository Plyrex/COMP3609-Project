// import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import java.awt.Image;


public class Car {

   private JPanel panel;
   private int x;
   private int y;
   private int width;
   private int height;
   private int direction= 0;

    private int dx;
    private int dy;
    private double velX= 0, velY= 0; 

    private Rectangle2D.Double car;

    private Color backgroundColour;
    private Image batImage;
    private Image batLeftImage;
    private Image batRightImage;
    private Image carImage, carUpImage, carLeftImage, carRightImage, carDownImage;
    private SoundManager soundManager;

    public Car (JPanel p, int xPos, int yPos) {
        panel = p;
        backgroundColour = panel.getBackground();
        
        x = xPos;
        y = yPos;
        
        dx = 5;
        dy = 5;
        width = 50;
        height = 50;

        carUpImage = ImageManager.loadImage ("images/playerUp.png");
        carLeftImage= ImageManager.loadImage ("images/playerLeft.png");
        carRightImage= ImageManager.loadImage ("images/playerRight.png");
        carDownImage= ImageManager.loadImage ("images/playerDown.png");
        carImage= carUpImage;
    }

    public void goAway(int x, int y){
        this.x= x;
        this.y= y;
    }
    public void draw(Graphics2D g2, double cameraX, double cameraY) {
        int screenX = (int)(panel.getWidth() / 2 - width / 2);
        int screenY = (int)(panel.getHeight() / 2 - height / 2);
        g2.drawImage(carImage, screenX, screenY, width, height, null);
    }


    public void erase () {
        Graphics g = panel.getGraphics ();
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor (backgroundColour);
        g2.fill (new Rectangle2D.Double (x, y, width, height));

        g2.setColor(backgroundColour);	 
        g2.draw(new Rectangle2D.Double (x, y, width, height));

        g.dispose();
    }
    public void tick(){
        x+= velX;
        y+= velY;
    }

	public void move(int direction) {
    if (!panel.isVisible()) return;
    
    TileMap tileMap = ((GamePanel)panel).getTileMap();
    int worldWidth = tileMap != null ? tileMap.getWidthPixels() : panel.getWidth();
    int worldHeight = tileMap != null ? tileMap.tilesToPixels(tileMap.getHeight()) : panel.getHeight();
    
    if (direction == 1) {
        carImage = carLeftImage;
        setDirection(2);
        setVelX(-5);
        if (x <= 0) {
            x = 0;
            setVelX(0);
        }
    }
    else if (direction == 2) {
        carImage = carRightImage;
        setDirection(3);
        setVelX(5);
        if (x >= worldWidth - width) {
            x = worldWidth - width;
            setVelX(0);
        }
    }
    else if(direction == 3) {
        carImage = carUpImage;
        setDirection(0);
        setVelY(-5);
        if (y <= 0) {
            y = 0;
            setVelY(0);
        }
    }
    else if(direction == 4) {
        carImage = carDownImage;
        setDirection(1);
        setVelY(5);
        if (y >= worldHeight - height) {
            y = worldHeight - height;
            setVelY(0);
        }
    }
}

   public boolean isOnCar(int x, int y) {
      if (car == null)
      	  return false;

      return car.contains(x, y);
   }

   public Rectangle2D.Double getBoundingRectangle() {
      return new Rectangle2D.Double (x, y, width, height);
   }

   public int getHeight(){
      return height;
   }

   public int getWidth(){
      return width;
   }

   public int getX(){
      return this.x;
   }

   public int getY(){
      return this.y;
   }

   public void setVelX(double velX){
      this.velX= velX;
   }

   public void setVelY(double velY){
      this.velY= velY;
   }

   public double getVelX() {
      return velX;
   }

   public double getVelY() {
      return velY;
   }

   public void setDirection(int direction){
      this.direction= direction;
   }
   public int getDirection(){
      return direction;
   }
}