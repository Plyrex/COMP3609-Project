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

public class TileMap {

    private static final int TILE_SIZE = 64;
    private static final int TILE_SIZE_BITS = 6;

    private Image[][] tiles;
    private int screenWidth, screenHeight;
    private int mapWidth, mapHeight;
    private int offsetY;

    private LinkedList<Sprite> sprites;
    private Car player;

    private double cameraX = 0;
    private double cameraY = 0;
    private double movementFactor = 2;
    
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
    }

    public TileMap(JFrame window, int mapWidth, int mapHeight) {
        this(mapWidth, mapHeight, window.getWidth(), window.getHeight());
    }

    /**
     * Set the player reference
     */
    public void setPlayer(Car player) {
        this.player = player;
    }
    
    public void loadTileImages(String tilesetPath) {
        try {
            BufferedImage tilesetImage = ImageIO.read(new File(tilesetPath));
            
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
    
    public Image getTileImage(int index) {
        if (index >= 0 && index < tileImages.length) {
            return tileImages[index];
        }
        return null;
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
    
    private void boundCamera() {
        int mapPixelWidth = getWidthPixels();
        int mapPixelHeight = tilesToPixels(mapHeight);
        double maxX = Math.max(0, mapPixelWidth - screenWidth);
        double maxY = Math.max(0, mapPixelHeight - screenHeight);
        if (cameraX < 0) {
            cameraX = 0;
        } else if (cameraX > maxX) {
            cameraX = maxX;
        }
        if (cameraY < 0) {
            cameraY = 0;
        } else if (cameraY > maxY) {
            cameraY = maxY;
        }
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

        int tilesWide = (int)Math.ceil((double)screenWidth / TILE_SIZE);
        int tilesHigh = (int)Math.ceil((double)screenHeight / TILE_SIZE);

        int endX = Math.min(startX + tilesWide + 2, mapWidth);
        int endY = Math.min(startY + tilesHigh + 2, mapHeight);

        startX = Math.max(0, startX);
        startY = Math.max(0, startY);

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

    public void addSprite(Sprite sprite) {
        sprites.add(sprite);
    }

    public double getCameraY() {
        return cameraY;
    }

    public double getCameraX() {
        return cameraX;
    }

    public void setCameraPosition(double x, double y) {
        this.cameraX = x;
        this.cameraY = y;
        boundCamera();
    }
    public void centerOn(int worldX, int worldY) {
        double targetX = worldX - (screenWidth / 2.0);
        double targetY = worldY - (screenHeight / 2.0);
        setCameraPosition(targetX, targetY);
    }

    public int[] findRunwayCenter() {
        for (int x = 0; x < mapWidth - 1; x++) {
            for (int y = 0; y < mapHeight; y++) {
                Image left = getTile(x, y);
                Image right = getTile(x + 1, y);
                if (left == getTileImage(5) && right == getTileImage(6)) {
                    int startY = y;
                    int endY = y;
                    while (endY + 1 < mapHeight &&
                           getTile(x, endY + 1) == getTileImage(5) &&
                           getTile(x + 1, endY + 1) == getTileImage(6)) {
                        endY++;
                    }
                    int centerX = x + 0; 
                    return new int[]{centerX, startY, endY};
                }
            }
        }
        return null;
    }

    public int getScreenWidth() {
        return screenWidth;
    }
    public int getScreenHeight() {
        return screenHeight;
    }
}

