import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.geom.AffineTransform;

public class TakeoffCutscene extends Cutscene {
    private static final int RUNWAY_SCENE = 0;
    private static final int TAKE_OFF = 1;
    private static final int ZOOM_OUT = 2;
    private static final int HEIGHT_REACHED = 3;
    private static final int ZOOM_IN_OUT = 4;
    private static final int ENDING = 5;
    
    private Image runwayImage;
    private Image carImage;
    private Image skyImage;
    private Image cloudsImage;
    
    private double carX, carY;
    private double carScale;
    private double cameraZoom;
    
    public TakeoffCutscene(GamePanel panel, TileMap tileMap) {
        super(panel, tileMap);
        
        runwayImage = ImageManager.loadImage("images/runway.ppg");
        carImage = ImageManager.loadImage("images/car.png");
        skyImage = ImageManager.loadImage("images/Background2.png");
        cloudsImage = ImageManager.loadImage("images/clouds.png");
        
        reset();
    }
    
    @Override
    protected void reset() {
        currentState = RUNWAY_SCENE;
        frameCount = 0;
        carX = 200;
        carY = 350;
        carScale = 1.0;
        cameraZoom = 1.0;
        isPlaying = false;
        isDone = false;
    }
    
    @Override
    public void update() {
        if (!isPlaying) return;

        frameCount++;

        if (currentState == RUNWAY_SCENE) {
            if (frameCount > 40) {
                currentState = TAKE_OFF;
                frameCount = 0;
                soundManager.playClip("engine", false);
            }
        } else if (currentState == TAKE_OFF) {
            carX += frameCount * 0.1;
            if (frameCount > 60) {
                carY -= (frameCount - 60) * 0.5;
                currentState = ZOOM_OUT;
                frameCount = 0;
                soundManager.playClip("wind", false);
            }
        } else if (currentState == ZOOM_OUT) {
            carY -= 2.0;
            cameraZoom -= 0.01;
            if (cameraZoom < 0.5) cameraZoom = 0.5;
            if (frameCount > 100) {
                currentState = HEIGHT_REACHED;
                frameCount = 0;
            }
        } else if (currentState == HEIGHT_REACHED) {
            carX += 0.5;
            if (frameCount > 50) {
                currentState = ZOOM_IN_OUT;
                frameCount = 0;
            }
        } else if (currentState == ZOOM_IN_OUT) {
            if (frameCount < 30) {
                carScale += 0.03;
            } else if (frameCount < 60) {
                carScale -= 0.03;
            } else {
                currentState = ENDING;
                frameCount = 0;
            }
        } else if (currentState == ENDING) {
            if (frameCount > 30) {
                isPlaying = false;
                isDone = true;
            }
        }
    }
    
    @Override
    public void draw(Graphics2D g2) {
        if (!isPlaying) return;
        
        AffineTransform oldTransform = g2.getTransform();
        g2.scale(cameraZoom, cameraZoom);
        int centerX = (int)(panel.getWidth() / (2 * cameraZoom));
        int centerY = (int)(panel.getHeight() / (2 * cameraZoom));
        
        if (currentState == RUNWAY_SCENE || currentState == TAKE_OFF) {
            g2.drawImage(runwayImage, 0, 0, panel.getWidth() * 2, panel.getHeight() * 2, null);
        } else {
            g2.drawImage(skyImage, 0, 0, panel.getWidth() * 3, panel.getHeight() * 3, null);
            for (int i = 0; i < 5; i++) {
                g2.drawImage(cloudsImage, 
                    (int)(100 * i + frameCount * (0.5 + i * 0.2)), 
                    100 + i * 30, 
                    100, 80, null);
            }
        }
        int carWidth = (int)(50 * carScale);
        int carHeight = (int)(50 * carScale);
        g2.drawImage(carImage, 
                    (int)(carX - carWidth/2), 
                    (int)(carY - carHeight/2), 
                    carWidth, carHeight, null);
        
        if (currentState == ENDING) {
            float alpha = 1.0f - (frameCount / 30.0f);
            if (alpha < 0) alpha = 0;
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, panel.getWidth(), panel.getHeight());
        }
        
        g2.setTransform(oldTransform);
    }
    
    @Override
    public void start() {
        super.start();
        soundManager.playClip("takeoff", false);
    }
}