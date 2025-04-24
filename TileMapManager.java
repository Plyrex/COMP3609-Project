import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.JFrame;

/**
    The TileMapManager class loads and manages tile maps and map files.
*/
public class TileMapManager {

    private JFrame window;
    private String tilesetPath;
    private int defaultWidth = 800;
    private int defaultHeight = 600;
    
    /**
     * Creates a new TileMapManager
     */
    public TileMapManager(JFrame window, String tilesetPath) {
        this.window = window;
        this.tilesetPath = tilesetPath;
    }
    
    /**
     * Set the window reference if it wasn't available at construction time
     */
    public void setWindow(JFrame window) {
        this.window = window;
    }

    /**
     * Loads a map from a file
     */
    public TileMap loadMap(String filename) throws IOException {
        ArrayList<String> lines = new ArrayList<String>();
        int mapWidth = 0;
        int mapHeight = 0;

        BufferedReader reader = new BufferedReader(new FileReader(filename));
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                reader.close();
                break;
            }
            if (!line.startsWith("#")) {
                lines.add(line);
                mapWidth = Math.max(mapWidth, line.length());
            }
        }
        mapHeight = lines.size();

        TileMap newMap;
        if (window != null) {
            newMap = new TileMap(window, mapWidth, mapHeight);
        } else {
            newMap = new TileMap(mapWidth, mapHeight, defaultWidth, defaultHeight);
        }

        newMap.loadTileImages(tilesetPath);
        for (int y = 0; y < mapHeight; y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                char ch = line.charAt(x);
                int tileIndex = getTileIndexFromChar(ch);
                if (tileIndex >= 0) {
                    Image tileImage = newMap.getTileImage(tileIndex);
                    if (tileImage != null) {
                        newMap.setTile(x, y, tileImage);
                    }
                }
            }
        }

        return newMap;
    }
    
    private int getTileIndexFromChar(char ch) {
        if (Character.isDigit(ch)) {
            return ch - '0';
        }
        if (ch >= 'A' && ch <= 'Z') {
            return 10 + (ch - 'A');
        }
        
        if (ch >= 'a' && ch <= 'z') {
            return 36 + (ch - 'a');
        }
        
        switch (ch) {
            case '.': return -1; // Empty space
            case '#': return 0;  // Solid block
            case '=': return 1;  // Platform
            case '^': return 6;  // Sky
            default: return -1;
        }
    }
}
