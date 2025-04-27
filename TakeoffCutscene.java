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

    private Image carImage;
    private double carX, carY;
    private double carScale;
    private double cameraZoom;
    private double cameraYTarget;

    public TakeoffCutscene(GamePanel panel, TileMap tileMap) {
        super(panel, tileMap);
        carImage = ImageManager.loadImage("images/playerUp.png");
        reset();
    }

    @Override
    protected void reset() {
        currentState = RUNWAY_SCENE;
        frameCount = 0;
        carScale = 1.0;
        cameraZoom = 1.0;

        int[] runway = tileMap.findRunwayCenter();
        if (runway != null) {
            int centerX = runway[0];
            int startY = runway[1];
            int endY = runway[2];
            carX = TileMap.tilesToPixels(centerX) + 64 / 2.0;
            carY = TileMap.tilesToPixels(endY) - 64 / 2.0;
            tileMap.setCameraPosition(
                carX - tileMap.getScreenWidth() / 2,
                carY - tileMap.getScreenHeight() / 2
            );
        } else {
            carX = tileMap.getWidth() * TileMap.tilesToPixels(1) / 2.0;
            carY = tileMap.getHeight() * TileMap.tilesToPixels(1) - TileMap.tilesToPixels((int) 1.5);
            tileMap.setCameraPosition(0, tileMap.getHeight() * TileMap.tilesToPixels(1) - tileMap.getHeight());
        }
        cameraYTarget = 0; 
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
            carY -= 4;
            tileMap.setCameraPosition(
                carX - tileMap.getScreenWidth() / 2,
                carY - tileMap.getScreenHeight() / 2
            );
            if (carY < tileMap.getScreenHeight() / 4) {
                currentState = ENDING;
                frameCount = 0;
            }
        } else if (currentState == ENDING) {
            if (frameCount > 30) {
                soundManager.stopClip("engine");
                isPlaying = false;
                isDone = true;
            }
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        if (!isPlaying) return;
        tileMap.draw(g2);
        int carWidth = (int)(50 * carScale);
        int carHeight = (int)(50 * carScale);
        int drawX = (int)(carX - tileMap.getCameraX() - carWidth / 2);
        int drawY = (int)(carY - tileMap.getCameraY() - carHeight / 2);
        if (carImage != null) {
            g2.drawImage(carImage, drawX, drawY, carWidth, carHeight, null);
        }

        if (currentState == ENDING) {
            float alpha = 1.0f - (frameCount / 30.0f);
            if (alpha < 0) alpha = 0;
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, panel.getWidth(), panel.getHeight());
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
    }

    @Override
    public void start() {
        super.start();
        soundManager.playClip("takeoff", false);
    }
}