import java.awt.Graphics2D;

public abstract class Cutscene {
    protected GamePanel panel;
    protected SoundManager soundManager;
    protected TileMap tileMap;
    
    protected int currentState;
    protected int frameCount;
    protected boolean isPlaying;
    protected boolean isDone;
    
    public Cutscene(GamePanel panel, TileMap tileMap) {
        this.panel = panel;
        this.tileMap = tileMap;
        this.soundManager = SoundManager.getInstance();
        this.isPlaying = false;
        this.isDone = false;
        this.frameCount = 0;
        this.currentState = 0;
    }
  
    public void start() {
        reset();
        isPlaying = true;
    }
    
    public void skip() {
        isPlaying = false;
        isDone = true;
    }

    public boolean isPlaying() {
        return isPlaying;
    }
    
    public boolean isDone() {
        return isDone;
    }

    protected abstract void reset();

    public abstract void update();

    public abstract void draw(Graphics2D g2);
}