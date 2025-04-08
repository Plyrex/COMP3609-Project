import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class BackgroundManager {
    
    private Image backgroundImage;
    private int imageWidth;
    private int imageHeight;
    private double cameraX;
    private double cameraY;
    private double movementFactor = 2;
    private int panelWidth;
    private int panelHeight;
    
    public BackgroundManager(String imagePath, int panelWidth, int panelHeight) {
        this.backgroundImage = ImageManager.loadImage(imagePath);
        this.imageWidth = backgroundImage.getWidth(null);
        this.imageHeight = backgroundImage.getHeight(null);
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
        this.cameraX = 0;
        this.cameraY = 0;
    }
    
    //dawg and dawgettes who are reading this, i have learned from the great gods of stackoverflow on how to make this thing work. I understand it, however omg 
    //I would not have thought about it like this. 
    public void moveBackground(double velX, double velY) {
        cameraX -= velX * movementFactor;
        cameraY -= velY * movementFactor;
        boundCamera();
    }
    
    private void boundCamera() {
        if (cameraX < 0) cameraX = 0;
        if (cameraY < 0) cameraY = 0;
        if (cameraX > imageWidth - panelWidth) cameraX = imageWidth - panelWidth;
        if (cameraY > imageHeight - panelHeight) cameraY = imageHeight - panelHeight;
    }
    
    public void draw(Graphics2D g2) {
        int x = (int)cameraX % imageWidth;
        int y = (int)cameraY % imageHeight;
        if (x > 0) x -= imageWidth;
        if (y > 0) y -= imageHeight;
        for (int i = x; i < panelWidth; i += imageWidth) {
            for (int j = y; j < panelHeight; j += imageHeight) {
                g2.drawImage(backgroundImage, i, j, null);
            }
        }
    }
    

    public void reset() {
        cameraX = 0;
        cameraY = 0;
    }
}