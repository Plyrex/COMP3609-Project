import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

public class CutsceneManager {
    private GamePanel panel;
    private Map<String, Cutscene> cutscenes;
    private Cutscene currentCutscene;
    private TileMap tileMap;
    
    public CutsceneManager(GamePanel panel) {
        this.panel = panel;
        this.cutscenes = new HashMap<>();
    }
    

    public void setTileMap(TileMap tileMap) {
        this.tileMap = tileMap;
        initializeCutscenes();
    }

    private void initializeCutscenes() {
        if (tileMap == null) return;

        cutscenes.put("takeoff", new TakeoffCutscene(panel, tileMap));
        
        // ez way for dialogue
        String[] dialogues = {
            "Welcome to deez bolts!",
            "Let's explore this skibidi world.",
            "Watch out for the rizz!"
        };
        cutscenes.put("intro", new DialogCutscene(panel, tileMap, dialogues));
        
        //basic idea for how deez cutscenes gon womrk
    }

    public void startCutscene(String name) {
        if (cutscenes.containsKey(name)) {
            currentCutscene = cutscenes.get(name);
            currentCutscene.start();
        }
    }
    
    public void startCutscene() {
        startCutscene("takeoff");
    }

    public boolean isPlaying() {
        return currentCutscene != null && currentCutscene.isPlaying();
    }
    
    public boolean isDone() {
        return currentCutscene == null || currentCutscene.isDone();
    }
    
    public void skipCutscene() {
        if (currentCutscene != null) {
            currentCutscene.skip();
        }
    }

    public void update() {
        if (currentCutscene != null) {
            currentCutscene.update();
            if (currentCutscene.isDone()) {
                currentCutscene = null;
            }
        }
    }
 
    public void draw(Graphics2D g2) {
        if (currentCutscene != null) {
            currentCutscene.draw(g2);
        }
    }
}
