import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.io.IOException;
import javax.swing.SwingUtilities;
import javax.swing.JFrame;

/**
   A component that displays all the game entities
*/

public class GamePanel extends JPanel implements Runnable {
   
    private Car car;
    private Enemy[] enemies;
    private EnemyBullet[] oppBullets;
    private List<Bullet> bullets;
    private List<EnemyBullet> enemyBullets;
    private boolean isRunning;
    private boolean isPaused;
    private TileMap tileMap;
    private TileMapManager tileMapManager;
    private BufferedImage image;
    private Thread gameThread;
    public ScoringPanel scoringPanel;
    public LifePanel lifePanel;
    private ImageFX imageFX1, imageFX2;
    private ImageFX[] spawns;
    private SoundManager soundManager;
    private CutsceneManager cutsceneManager;
    Random random= new Random();
    int rand, type, move;
    private int NUM_ENEMIES= 2;
    private int kills= 0, powerupKills= 0;
    private StripAnimation animation, animation2;
    private int speed= 2;
    private int time, timeChange= 1;
    private HealthPickup health;
    private int currentLevel = 1;
    private static final int MAX_LEVEL = 3;
    private treeveg Vageeta;

    private static final int VIEW_WIDTH= 800;
    private static final int VIEW_HEIGHT= 600;
    private static final double ZOOM= 2.0;

    public GamePanel() {
        // setPreferredSize(new Dimension(VIEW_WIDTH, VIEW_HEIGHT));
        soundManager= SoundManager.getInstance();
        scoringPanel= new ScoringPanel();
        lifePanel= new LifePanel();
        lifePanel.setDoubleBuffered(true);
        image = new BufferedImage(getWidth() > 0 ? getWidth() : 400, 
                            getHeight() > 0 ? getHeight() : 400, 
                            BufferedImage.TYPE_INT_ARGB);
        car = null;
        enemies= null;
        spawns= null;
        oppBullets= null;
        gameThread = null;
        isRunning = false;
        health= null;


        bullets = new ArrayList<>();
          enemyBullets = new ArrayList<>();

        cutsceneManager = new CutsceneManager(this);

          tileMap = null;
        tileMapManager = new TileMapManager(
            (JFrame)SwingUtilities.getWindowAncestor(this), 
            "tilemap/basic_tileset_and_assets_standard/terrain_tiles_v2.png"
        );
    }

    // public void createGameEntities() {
    //     car = new Car (this, 200, 350);
    //     animation= new StripAnimation("images/kaboom.gif", 6, false);
    //     animation2= new StripAnimation("images/select.png", 4, true);
    //     // rotate= new RotateFX(this, "images/health.png");

    //     bullets.clear();
    //       enemyBullets.clear();
    // }


    public void checkOpponents(){
        if(enemies== null){
            createOpponentsForLevel(currentLevel);
        }
    }

    public void run() {
        try {
            isRunning = true;
            while (isRunning) {
                if(!isPaused) {
                    gameUpdate();
                }
                    
                gameRender();
                if (!cutsceneManager.isPlaying() && car != null) {
                    car.tick();
                }
                
                Thread.sleep(33); 
            }
        }
        catch(InterruptedException e) {}
    }

    public void gameUpdate() {
        if (cutsceneManager.isPlaying()) {
            cutsceneManager.update();
            return;
        }
        // car.tick();
        if(!isPaused){
            if(car != null && tileMap != null) {
                tileMap.centerOn(car.getX(), car.getY());
            }

            // WACK AHH bullet implememntation but we work
            for (int i = bullets.size() - 1; i >= 0; i--) {
                Bullet b = bullets.get(i);
                b.shoot();
                if (b.isOffScreen()) {
                    bullets.remove(i);
                    continue;
                }
                Rectangle2D.Double bulletRect = b.getBoundingRectangle();
                boolean hitSomething = false;
    
                if (enemies != null) {
                    for (int j = 0; j < NUM_ENEMIES; j++) {
                        if (enemies[j] != null && bulletRect.intersects(enemies[j].getBoundingRectangle())) {
                            soundManager.playClip("hit", false);
                            bullets.remove(i);
                            addPoints(1);
                            health= new HealthPickup(this, car, enemies[j].getX(), enemies[j].getY());
                            animation2.start(health.getX()-20, health.getY()-10);
                            // System.out.println("Health Created at"+ );
                            killEnemy(enemies[j].getX(), enemies[j].getY(), 
                                    enemies[j].getBoundingRectangle().height, 
                                    enemies[j].getBoundingRectangle().width, 
                                    0, j, 0);
                            hitSomething = true;
                            break;
                        }
                    }
                }
            }

            for (int i = enemyBullets.size() - 1; i >= 0; i--) {
                EnemyBullet eb = enemyBullets.get(i);
                eb.shoot();

                if (eb.isOffScreen()) {
                    enemyBullets.remove(i);
                    continue;
                }
                if (car != null && eb.getBoundingRectangle().intersects(car.getBoundingRectangle())) {
                    soundManager.playClip("playerHit", false);
                    enemyBullets.remove(i);
                    loseLife(1);
                    if (getLifeTotal() <= 0) {
                        soundManager.playClip("death", false);
                        destroyed(car.getX(), car.getY(), car.getHeight(), car.getWidth());
                        endGame();
                    }
                }
            }

            for (int i=0; i<NUM_ENEMIES; i++) {
                checkOpponents();
                if(spawns[i]!= null){
                    spawns[i].update();
                }
                if (enemies[i] != null){
                    enemies[i].move();
                    
                if (random.nextInt(100) < 0.5) { 
                        enemyShoot(i, enemies[i].getX(), enemies[i].getY(), 
                                (int)enemies[i].getBoundingRectangle().width);
                }
                }
            }

            while (bullets.size() > 50) {
                bullets.remove(0);
            }
            while (enemyBullets.size() > 50) {
                enemyBullets.remove(0);
            }
            
            if(imageFX1!= null)
                imageFX1.update();
            
            if(imageFX2!= null)
                imageFX2.update();
            
            if(animation!= null)
                animation.update();

            if(animation2!= null)
                animation2.update();
            
            if(health!= null)
                health.move();
        }
        //we use here for fuel tanfk shenangicans
        boolean allEnemiesDefeated = true;
        for (Enemy enemy : enemies) {
            if (enemy != null) {
                allEnemiesDefeated = false;
                break;
            }
        }

        if (allEnemiesDefeated) {
            advanceToNextLevel();
        }
    }

    public void updateBat(int direction) {
        if (car != null && !isPaused) {
            car.move(direction);
        }
    }

    public void batStop(int num){
        if(num== 0){
            car.setVelX(0);
        }
        if(num== 1){
            car.setVelY(0);
        }
    }

    public void removeHealth(){
        health= null;
        animation2.stop();
    }

    public void destroyed(int x, int y, int height, int width){ //SNAHYBUIDFBAYUSFGYAUSGVBTUYASVTFYAUVSGTFAYTSVFTYGAVYDVAUT

        int screenCenterX = getWidth() / 2 - width / 2;
        int screenCenterY = getHeight() / 2 - height / 2;
    
        animation.start(screenCenterX, screenCenterY);
        
        // imageFX2 = new DisintegrateFX(this, screenCenterX, screenCenterY, height, width, "images/car.png");
        // car.goAway(200, 350);
    }

    public void killEnemy(int x, int y, double height, double width, int type, int enemy, int method){
        if(type==0){
            enemies[enemy]= null;
            animation.start(x, y);
            imageFX1= new DisintegrateFX(this, x, y, height, width, "images/banditUp.png");
            repaint();
            kills+= 1;
            if(method== 0)
                powerupKills+= 1;
        }else if(type==1){
            animation.start(x, y);
            imageFX1= new DisintegrateFX(this, x, y, height, width, "images/tank.png");
            soundManager.stopClip ("kamikaze");
            if(method== 1)
                powerupKills= -5;
        }
    }

    public void endDisintegrate(){
        imageFX1= null;
        imageFX2= null;
    }

    public void endDisappear(int enemy){
        spawns[enemy]= null;
    }

    public void shootBullet() { 
        if (car != null && !isPaused) {
            soundManager.playClip("shoot", false);
            int screenCenterX = getWidth() / 2;
            int screenCenterY = getHeight() / 2;
            
            int bulletX = (int)tileMap.getCameraX() + screenCenterX;
            int bulletY = (int)tileMap.getCameraY() + screenCenterY;
            
            Bullet newBullet = new Bullet(this, car.getHeight(), car.getWidth(), 
                                         bulletX - car.getWidth()/2,
                                         bulletY - car.getHeight()/2,
                                         car.getDirection());
            bullets.add(newBullet);
        }
    }

    public void enemyShoot(int enemy, int x, int y, int width) {
        if (car != null && enemies[enemy] != null) {
            EnemyBullet newEnemyBullet = new EnemyBullet(this, width, x, y, car, enemy);
            enemyBullets.add(newEnemyBullet);
        }
    }

    public boolean bulletExist(){
        return !bullets.isEmpty();
    }

    public boolean enemyBulletExist(int enemy) {
        for (EnemyBullet eb : enemyBullets) {
            if (eb.getEnemyIndex() == enemy) {
                return true;
            }
        }
        return false;
    }

    public boolean running(){
        return isRunning;
    }

    public void deleteBullet(){
        if (!bullets.isEmpty()) {
            bullets.remove(bullets.size() - 1);
        }
    }
    public void enemyDeleteBullet(int enemy){
   
    }

    public void addPoints(int points){
        scoringPanel.addPoints(points);
    }

    public void loseLife(int oof){
        lifePanel.loseLife(oof);
    }

    public void addLife(int yay){
        lifePanel.addLife(yay);
    }

    public int getLifeTotal(){
        return lifePanel.getLifeTotal();
    }

    public void gameRender() { //I FIXED MFING GAME RENDER YEAAAAAAAAAAAA  https://jvm-gaming.org/t/best-way-to-render-with-java2d/45029
                                //https://stackoverflow.com/questions/14922999/2d-graphics-rendering-in-java
        if (image == null) return;
        
        Graphics2D imageContext = null;
        Graphics2D g2 = null;
        
        try {
            imageContext = (Graphics2D) image.getGraphics();
            imageContext.setColor(Color.BLACK);
            imageContext.fillRect(0, 0, image.getWidth(), image.getHeight());
            
            if (cutsceneManager.isPlaying()) {
                cutsceneManager.draw(imageContext);
            } else {
                if (tileMap != null) {
                    tileMap.draw(imageContext);
                }
                double cameraX = tileMap != null ? tileMap.getCameraX() : 0;
                double cameraY = tileMap != null ? tileMap.getCameraY() : 0;
                
                Vageeta.draw(imageContext, cameraX, cameraY);

                if (car != null) car.draw(imageContext, cameraX, cameraY);
                for (Enemy e : enemies) if (e != null) e.draw(imageContext, cameraX, cameraY);
                for (Bullet b : bullets) b.draw(imageContext, cameraX, cameraY);
                for (EnemyBullet eb : enemyBullets) eb.draw(imageContext, cameraX, cameraY);

                if(imageFX1 != null) imageFX1.draw(imageContext); //I SAW THIS SICK AAH WAY TO WRITE THIS INSTEAD OF HOW LONG IT USUALLY IS
                if(imageFX2 != null) imageFX2.draw(imageContext);
                if(animation != null) animation.draw(imageContext);
                if(animation2 != null) animation2.draw(imageContext);
                if(health != null) health.draw(imageContext);

                Font levelFont = new Font("Arial", Font.BOLD, 16);
                imageContext.setFont(levelFont);
                imageContext.setColor(Color.WHITE);
                imageContext.drawString("LEVEL: " + currentLevel, 10, 20);
            }
            
            g2 = (Graphics2D) getGraphics();
            if (g2 != null) {
                g2.drawImage(image, 0, 0, getWidth(), getHeight(), null);
            }
        } finally {
            if (imageContext != null) imageContext.dispose();
            if (g2 != null) g2.dispose();
        }

    }

    public void startGame() {				
        if (isRunning) return;
        
        //soundManager.setVolume("background", 0.7f);
        //soundManager.playClip("background", true);
        isPaused = false;
        currentLevel = 1; // Reset to level 1
        
        // Create car
        car = new Car(this, 200, 350);
        // create animations
        animation= new StripAnimation("images/kaboom.gif", 6, false);
        animation2= new StripAnimation("images/select.png", 4, true);
        
        JFrame window = (JFrame)SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            tileMapManager.setWindow(window);
        }
        
        // Load level 1
        loadLevel(currentLevel);
        
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void pauseGame() {				
        if (isRunning) {
            if (isPaused)
                isPaused = false;
            else
                isPaused = true;
        }
    }

    public void endGame() {					
        isRunning = false;
        speed = 5;
    }

    public boolean isOnCar(int x, int y) {
        return car.isOnCar(x, y);
    }

    public void playCutscene(String name) {
        cutsceneManager.startCutscene(name);
    }

    public boolean isOffScreen(int x, int y) {
        return x < 0 || x > getWidth() || y < 0 || y > getHeight();
    }

    public TileMap getTileMap() {
        return tileMap;
    }

    public void advanceToNextLevel() {
        if (currentLevel < MAX_LEVEL) {
            currentLevel++;
            loadLevel(currentLevel);
        } else {
            endGame();
        }
    }

    private void loadLevel(int level) {
        String tilesetPath;
        switch (level) { //unfortunately i did not continue the funny if else statmenets here bc switch is sm easier here
            case 1:
                tilesetPath = "tilemap/level1.png";
                soundManager.playClip("backgroundfull", false);
                soundManager.setVolume("backgroundfull", 0.7f);
                break;
            case 2:
                tilesetPath = "tilemap/level2.png"; //idk something
                soundManager.stopClip("backgroundfull");
                soundManager.playClip("background2", false);
                soundManager.setVolume("background2", 0.7f);
                break;
            case 3:
                tilesetPath = "tilemap/level3.png";
                soundManager.stopClip("background2");
                soundManager.playClip("background3", false);
                soundManager.setVolume("background3", 0.7f);
                break;
            default:
                tilesetPath = "tilemap/basic_tileset_and_assets_standard/terrain_tiles_v2.png";
        }
        
        tileMapManager = new TileMapManager(
            (JFrame)SwingUtilities.getWindowAncestor(this), 
            tilesetPath
        );
        try {
            String mapFile = "maps/level" + level + ".map";
            tileMap = tileMapManager.loadMap(mapFile);
            tileMap.setPlayer(car);
            createOpponentsForLevel(level);
            Vageeta = new treeveg(tileMap);

            if (level == 1 && cutsceneManager != null) {
                cutsceneManager.setTileMap(tileMap);
                // cutsceneManager.startCutscene("takeoff");
            }

        } catch (IOException e) {
            System.err.println("Error loading map: " + e.getMessage());
        }
    }

    private void createOpponentsForLevel(int level) {
        enemies = new Enemy[NUM_ENEMIES];
        oppBullets = new EnemyBullet[NUM_ENEMIES];
        spawns = new ImageFX[NUM_ENEMIES];

        switch (level) { //no funny here bc it was easier for switch (maybe will replace with if else later toiday)
                        //@saeed you can use here to fix the enemy spawning and add what you need to
            case 1:
                for (int i = 0; i < NUM_ENEMIES; i++) {
                    spawnEnemyInWorldBounds(i, 5, 5, 10, 10, speed);
                }
                break;
                
            case 2:
                speed += 2; 
                for (int i = 0; i < NUM_ENEMIES; i++) {
                    int enemyType = random.nextInt(10);
                    if (enemyType < 7) { // 70% chance for Bandit
                        spawnEnemyInWorldBounds(i, 3, 3, 15, 15, speed);
                    } else { // 30% chance for Tank
                        spawnEnemyInWorldBounds(i, 3, 3, 15, 15, speed);
                    }
                }
                //replace kamikaze with whgatever we decide
                // kamikaze = new Kamikaze(this, random.nextInt(400), 50, car, 0);
                break;
                
            case 3:

                speed += 3;
                for (int i = 0; i < NUM_ENEMIES; i++) {
                    int enemyType = random.nextInt(10);
                    if (enemyType < 3) { // 30% chance for Tank
                        spawnEnemyInWorldBounds(i, 2, 2, 18, 18, speed);
                    } else if (enemyType < 8) { // 50% chance for Bandit
                        spawnEnemyInWorldBounds(i, 2, 2, 18, 18, speed);
                    } else { 
                        // enemies[i] = new Blimp(this, randX, randY, car, i, speed + 2);
                        spawnEnemyInWorldBounds(i, 2, 2, 18, 18, speed);
                    }
                }
                // replace with wahthever
                // kamikaze = new Kamikaze(this, random.nextInt(400), 50, car, 0);
                break;
        }
    }

    private void spawnEnemyInWorldBounds(int index, int minTileX, int minTileY, int maxTileX, int maxTileY, int speed) {
    int mapWidthTiles = tileMap.getWidth();//my god i want to put down my computer I SFDNJAIFNIUAJBNSU
    int mapHeightTiles = tileMap.getHeight();//the code was basically what i was doing in making things work with the world bounds and just having it all here but omgdfnsjk

    int minX = 0;
    int minY = 0;
    int maxX = TileMap.tilesToPixels(mapWidthTiles) - 1;
    int maxY = TileMap.tilesToPixels(mapHeightTiles) - 1;

    int randX = random.nextInt(maxX - minX) + minX;
    int randY = random.nextInt(maxY - minY) + minY;

    if (currentLevel == 1) {
        if (random.nextInt(2) == 0)
            enemies[index] = new Tank(this, randX, randY, car, index, speed);
        else
            enemies[index] = new Bandit(this, randX, randY, car, index, speed);
    } 
    else if (currentLevel == 2) {
        if (random.nextInt(10) < 7)
            enemies[index] = new Bandit(this, randX, randY, car, index, speed);
        else
            enemies[index] = new Tank(this, randX, randY, car, index, speed);
    } 
    else {
        int type = random.nextInt(3);
        if (type == 0)
            enemies[index] = new Tank(this, randX, randY, car, index, speed + 1);
        else if (type == 1)
            enemies[index] = new Bandit(this, randX, randY, car, index, speed + 2);
        else {
            // enemies[index] = new Blimp(this, randX, randY, car, index, speed);
            enemies[index] = new Tank(this, randX, randY, car, index, speed + 3);
        }
    }
}
}