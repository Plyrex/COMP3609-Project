import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
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
    private Opponent[] opponents;
    private Enemy[] enemies;
    private Kamikaze kamikaze;
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
    private int NUM_ENEMIES= 5;
    private int kills= 0, powerupKills= 0;
    private StripAnimation animation, animation2;
    private int speed= 2;
    private int time, timeChange= 1;
    private HealthPickup health;
    private RotateFX rotate;

    private static final int VIEW_WIDTH= 800;
    private static final int VIEW_HEIGHT= 600;
    private static final double ZOOM= 2.0;

    public GamePanel() {
        // setPreferredSize(new Dimension(VIEW_WIDTH, VIEW_HEIGHT));
        soundManager= SoundManager.getInstance();
        scoringPanel= new ScoringPanel();
        lifePanel= new LifePanel();
        lifePanel.setDoubleBuffered(true);
        image= new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
        car = null;
        opponents = null;
        enemies= null;
        spawns= null;
        oppBullets= null;
        kamikaze= null;
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

    public void createGameEntities() {
        car = new Car (this, 200, 350);
        createOpponents();
        animation= new StripAnimation("images/kaboom.gif", 6, false);
        animation2= new StripAnimation("images/select.png", 4, true);
        // rotate= new RotateFX(this, "images/health.png");

        bullets.clear();
          enemyBullets.clear();
    }

    public void createOpponents(){
        // opponents = new Opponent [NUM_ENEMIES];
        enemies= new Enemy[NUM_ENEMIES];
        oppBullets= new EnemyBullet[NUM_ENEMIES];
        spawns= new ImageFX[NUM_ENEMIES]; 
        for(int i=0; i<NUM_ENEMIES; i++){
            rand= random.nextInt(400);
            if(random.nextInt(2)== 0)
                enemies[i] = new Tank(this, rand, 10, car, i, speed);
            else
                enemies[i] = new Bandit(this, rand, 10, car, i, speed);
        }
        rand= random.nextInt(3);
        speed+= 1;
    }

    public void checkOpponents(){
        if(enemies== null){
            createOpponents();
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
                
                Thread.sleep(50);	
            }
        }
        catch(InterruptedException e) {}
    }

    public void gameUpdate() {
        if (cutsceneManager.isPlaying()) {
            cutsceneManager.update();
            return;
        }

        if(!isPaused){
            if(car != null && tileMap != null) {
                tileMap.moveMap(car.getVelX(), car.getVelY());
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
                            killEnemy(enemies[j].getX(), enemies[j].getY(), 
                                    enemies[j].getBoundingRectangle().height, 
                                    enemies[j].getBoundingRectangle().width, 
                                    0, j, 0);
                            hitSomething = true;
                            break;
                        }
                    }
                }
                if (!hitSomething && kamikaze != null && bulletRect.intersects(kamikaze.getBoundingRectangle())) {
                    soundManager.playClip("hit", false);
                    bullets.remove(i);
                    addPoints(1);
                    killEnemy(kamikaze.getX(), kamikaze.getY(), 
                            (int)kamikaze.getBoundingRectangle().height, 
                            (int)kamikaze.getBoundingRectangle().width, 
                            1, 0, 0);
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
                    
                    if (random.nextInt(100) < 2) { // 2% chance each update
                        enemyShoot(i, enemies[i].getX(), enemies[i].getY(), 
                                (int)enemies[i].getBoundingRectangle().width);
                    }
                }
            }

            if (kamikaze != null)
                kamikaze.move();
            
            if(imageFX1!= null)
                imageFX1.update();
            
            if(imageFX2!= null)
                imageFX2.update();

            if(rotate!= null)
                rotate.update();
            
            if(animation!= null)
                animation.update();

            if(animation2!= null)
                animation2.update();
            
            if(health!= null)
                health.move();
        }
        car.tick();
    }

    public void updateRotate(int num){
        rotate.setRotation(num);
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

    public void destroyed(int x, int y, int height, int width){
        animation.start(x, y);
        imageFX2= new DisintegrateFX(this, x, y, height, width, "images/car.png");
        car.goAway(200, 350);
    }

    public void killEnemy(int x, int y, double height, double width, int type, int enemy, int method){
        if(type==0){
            enemies[enemy]= null;
            animation.start(x, y);
            imageFX1= new DisintegrateFX(this, x, y, height, width, "images/opp.png");
            kills+= 1;
            if(method== 0)
                powerupKills+= 1;
        }else if(type==1){
            kamikaze= null;
            animation.start(x, y);
            imageFX1= new DisintegrateFX(this, x, y, height, width, "images/kamikaze1.png");
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
            Bullet newBullet = new Bullet(this,car.getHeight(), car.getWidth(), car.getX(), car.getY(), car.getDirection());
            bullets.add(newBullet);
        }
    }

    public void enemyShoot(int enemy, int x, int y, int width) {
        if (car != null) {
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

    public void gameRender() {
        Graphics g = getGraphics();
        Graphics2D imageContext = (Graphics2D) image.getGraphics();

        if (cutsceneManager.isPlaying()) {
            cutsceneManager.draw(imageContext);
        } else {
            if (tileMap != null) {
                tileMap.draw(imageContext);
            }
            if (car != null) {
                car.draw(imageContext);
            }

            if (enemies != null) {
                for (int i=0; i<NUM_ENEMIES; i++){
                    if(spawns[i]!= null){
                        spawns[i].draw(imageContext);
                    }
                    if (enemies[i] != null){
                        enemies[i].draw(imageContext);
                    }
                }
            }

            if(kamikaze!= null){
                kamikaze.draw(imageContext);
            }
            
            for (Bullet b : bullets) {
                b.draw(imageContext);
            }
            
            for (EnemyBullet eb : enemyBullets) {
                eb.draw(imageContext);
            }

            if(imageFX1!= null){
                imageFX1.draw(imageContext);
            }

            if(imageFX2!= null){
                imageFX2.draw(imageContext);
            }

            if(rotate!= null){
                rotate.draw(imageContext);
            }

            if(animation!= null){
                animation.draw(imageContext);
            }

            if(animation2!= null){
                animation2.draw(imageContext);
            }

            if(health!= null){
                health.draw(imageContext);
            }
        }

        Graphics2D g2= (Graphics2D) getGraphics();
        g2.drawImage(image, 0, 0, 400, 400, null);

        imageContext.dispose();
        g2.dispose();
    }

    public void startGame() {				
        if (isRunning)
            return;

        soundManager.setVolume("background", 0.7f);
        soundManager.playClip("background", true);
        isPaused = false;
        createGameEntities();
        JFrame window = (JFrame)SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            tileMapManager.setWindow(window);
        }
        
        try {
            tileMap = tileMapManager.loadMap("maps/level1.map");
            tileMap.setPlayer(car);
            cutsceneManager.setTileMap(tileMap);
        } catch (IOException e) {
            System.err.println("Error loading map: " + e.getMessage());
            tileMap = new TileMap(20, 15, getWidth(), getHeight());
            tileMap.loadTileImages("tilemap/basic_tileset_and_assets_standard/terrain_tiles_v2.png");
            tileMap.setPlayer(car);
            for (int x = 0; x < tileMap.getWidth(); x++) {
                for (int y = 0; y < tileMap.getHeight() - 2; y++) {
                    tileMap.setTile(x, y, tileMap.getTileImage(6)); // Sky
                }
                tileMap.setTile(x, tileMap.getHeight() - 1, tileMap.getTileImage(0)); // Road
                tileMap.setTile(x, tileMap.getHeight() - 2, tileMap.getTileImage(1)); // Grass
            }
        }
        
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

    public boolean isOnOpponent(int x, int y) {
        for (int i=0; i<NUM_ENEMIES; i++) {
            if (opponents[i].isOnOpponent(x, y))
                return true;
        }
        return false;
    }

    public void playCutscene(String name) {
        cutsceneManager.startCutscene(name);
    }

    public boolean isOffScreen(int x, int y) {
        return x < 0 || x > getWidth() || y < 0 || y > getHeight();
    }
}