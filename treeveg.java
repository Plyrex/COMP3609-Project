import java.awt.Graphics2D;
import java.awt.Image;
import java.util.Random;
import java.util.ArrayList;

public class treeveg { //i made this class so that it spawns trees and bushes around the map instead of in the tilemap. I think this is a cooler approach so we ball and hope it works

    private static final int NUM_TREES = 60;
    private static final int NUM_BUSHES = 40;
    private static final int[] BLOCKED_TILES = {5, 6, 7, 8}; 

    private class Veg {
        int x, y;
        Image img;
        Veg(int x, int y, Image img) {
            this.x = x;
            this.y = y;
            this.img = img;
        }
    }

    private ArrayList<Veg> vegList = new ArrayList<>();
    private TileMap tileMap;
    private Random random = new Random();
    private Image treeImg, bushImg;

    public treeveg(TileMap tileMap) {
        this.tileMap = tileMap;
        treeImg = ImageManager.loadImage("images/tree.png");
        bushImg = ImageManager.loadImage("images/bush.png");
        spawnVegetation();
    }

    private void spawnVegetation() {
        int mapWidth = tileMap.getWidth();
        int mapHeight = tileMap.getHeight();
        if (mapWidth <= 0 || mapHeight <= 0) {
            return; }
        for (int i = 0; i < NUM_TREES; i++) {
            int x, y;
            do {
                x = random.nextInt(mapWidth);
                y = random.nextInt(mapHeight);
            } while (isBlockedTile(x, y));
            vegList.add(new Veg(x, y, treeImg));
        }
        for (int i = 0; i < NUM_BUSHES; i++) {
            int x, y;
            do {
                x = random.nextInt(mapWidth);
                y = random.nextInt(mapHeight);
            } while (isBlockedTile(x, y));
            vegList.add(new Veg(x, y, bushImg));
        }
    }

    private boolean isBlockedTile(int x, int y) {
        Image tile = tileMap.getTile(x, y);
        if (tile == null) return true;
        for (int blocked : BLOCKED_TILES) {
            if (tile == tileMap.getTileImage(blocked)) return true;
        }
        return false;
    }

    public void draw(Graphics2D g2, double cameraX, double cameraY) {
        for (Veg v : vegList) {
            int px = TileMap.tilesToPixels(v.x) - (int)cameraX;
            int py = TileMap.tilesToPixels(v.y) - (int)cameraY;
            g2.drawImage(v.img, px, py, TileMap.tilesToPixels(1), TileMap.tilesToPixels(1), null);
        }
    }
}