import java.awt.Image;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.Iterator;
import javax.swing.JFrame;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
    The TileMap class contains the data for a tile-based
    map, including Sprites. Each tile is a reference to an
    Image. Images are used multiple times in the tile map.
*/

public class TileMap {

    private static final int TILE_SIZE = 64;
    private static final int TILE_SIZE_BITS = 6;

    private Image[][] tiles;
    private int screenWidth, screenHeight;
    private int mapWidth, mapHeight;
    private int offsetY;

    private LinkedList<Sprite> sprites;
    private Car player;

    // Camera position
    private double cameraX = 0;
    private double cameraY = 0;
    private double movementFactor = 2;
    
    // Array to store the different tile images
    private Image[] tileImages;

    /**
        Creates a new TileMap with the specified width and
        height (in number of tiles) of the map.
    */
    public TileMap(int width, int height, int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        mapWidth = width;
        mapHeight = height;

        offsetY = screenHeight - tilesToPixels(mapHeight);
        System.out.println("offsetY: " + offsetY);

        tiles = new Image[mapWidth][mapHeight];
        sprites = new LinkedList<Sprite>();

        loadTileImages();
        generateMap();
    }
    
    /**
     * Set the player reference
     */
    public void setPlayer(Car player) {
        this.player = player;
    }
    
    /**
     * Loads and splits the tile images from the tileset file
     */
    private void loadTileImages() {
        try {
            BufferedImage tilesetImage = ImageIO.read(new File("tilemap/basic_tileset_and_assets_standard/terrain_tiles_v2.png"));
            
           //i saw this is how we can split it like a split animation so it do be workin (i think)
            int tilesetWidth = tilesetImage.getWidth() / TILE_SIZE;
            int tilesetHeight = tilesetImage.getHeight() / TILE_SIZE;
            int totalTiles = tilesetWidth * tilesetHeight;
            
            tileImages = new Image[totalTiles];
            
            int tileIndex = 0;
            for (int y = 0; y < tilesetHeight; y++) {
                for (int x = 0; x < tilesetWidth; x++) {
                    if (tileIndex < totalTiles) {
                        tileImages[tileIndex] = tilesetImage.getSubimage(
                            x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE
                        );
                        tileIndex++;
                    }
                }
            }
            
            System.out.println("Loaded " + tileIndex + " tiles from tileset");
        } catch (IOException e) {
            System.err.println("Error loading tileset: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Generates a simple map layout using the loaded tiles
     */
    private void generateMap() {
        // This is a simple example - customize based on your game needs
        
        // Create sky
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight - 5; y++) {
                // Use a sky tile (e.g., tile index 6)
                tiles[x][y] = tileImages[6]; 
            }
        }
        
        // Create ground/road at the bottom
        for (int x = 0; x < mapWidth; x++) {
            // Use a ground tile (e.g., tile index 0)
            tiles[x][mapHeight - 1] = tileImages[0]; 
            tiles[x][mapHeight - 2] = tileImages[1];
        }
        
        // Add some random obstacles or terrain features
        for (int x = 0; x < mapWidth; x += 4) {
            if (Math.random() < 0.4) {
                int tileIndex = 2 + (int)(Math.random() * 4);
                int y = mapHeight - 3 - (int)(Math.random() * 2);
                tiles[x][y] = tileImages[tileIndex];
            }
        }
    }

    /**
        Gets the width of this TileMap (number of pixels across).
    */
    public int getWidthPixels() {
        return tilesToPixels(mapWidth);
    }

    /**
        Gets the width of this TileMap (number of tiles across).
    */
    public int getWidth() {
        return mapWidth;
    }

    /**
        Gets the height of this TileMap (number of tiles down).
    */
    public int getHeight() {
        return mapHeight;
    }

    /**
        Gets the tile at the specified location. Returns null if
        no tile is at the location or if the location is out of
        bounds.
    */
    public Image getTile(int x, int y) {
        if (x < 0 || x >= mapWidth ||
            y < 0 || y >= mapHeight)
        {
            return null;
        }
        else {
            return tiles[x][y];
        }
    }

    /**
        Sets the tile at the specified location.
    */
    public void setTile(int x, int y, Image tile) {
        tiles[x][y] = tile;
    }

    /**
        Class method to convert a pixel position to a tile position.
    */
    public static int pixelsToTiles(float pixels) {
        return pixelsToTiles(Math.round(pixels));
    }

    /**
        Class method to convert a pixel position to a tile position.
    */
    public static int pixelsToTiles(int pixels) {
        return (int)Math.floor((float)pixels / TILE_SIZE);
    }

    /**
        Class method to convert a tile position to a pixel position.
    */
    public static int tilesToPixels(int numTiles) {
        return numTiles * TILE_SIZE;
    }

    /**
     * Move the map based on player velocity
     */
    public void moveMap(double velX, double velY) {
        cameraX -= velX * movementFactor;
        cameraY -= velY * movementFactor;
        boundCamera();
    }
    
    /**
     * Ensure camera stays within map bounds
     */
    private void boundCamera() {
        if (cameraX < 0) cameraX = 0;
        if (cameraY < 0) cameraY = 0;
        if (cameraX > mapWidth * TILE_SIZE - screenWidth) cameraX = mapWidth * TILE_SIZE - screenWidth;
        if (cameraY > mapHeight * TILE_SIZE - screenHeight) cameraY = mapHeight * TILE_SIZE - screenHeight;
    }
    
    /**
     * Reset the camera position
     */
    public void reset() {
        cameraX = 0;
        cameraY = 0;
    }

    public void draw(Graphics2D g2) {
        int startX = pixelsToTiles((int)cameraX);
        int startY = pixelsToTiles((int)cameraY);

        int endX = startX + pixelsToTiles(screenWidth) + 1;
        int endY = startY + pixelsToTiles(screenHeight) + 1;

        startX = Math.max(0, startX);
        startY = Math.max(0, startY);

        endX = Math.min(endX, mapWidth);
        endY = Math.min(endY, mapHeight);

        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                Image image = getTile(x, y);
                if (image != null) {
                    g2.drawImage(image,
                        tilesToPixels(x) - (int)cameraX,
                        tilesToPixels(y) - (int)cameraY,
                        TILE_SIZE, TILE_SIZE,
                        null);
                }
            }
        }
    }
}
