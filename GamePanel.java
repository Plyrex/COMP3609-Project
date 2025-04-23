import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.List;

/**
   A component that displays all the game entities
*/

public class GamePanel extends JPanel implements Runnable {
   
	private Car car;
	private Opponent[] opponents;
	private Kamikaze kamikaze;
	private Bullet bullet;
	private EnemyBullet[] oppBullets;
	private boolean isRunning;
	private boolean isPaused;
	private BackgroundManager backgroundManager;
	private BufferedImage image;
	private Thread gameThread;
	public ScoringPanel scoringPanel;
	public LifePanel lifePanel;
	private ImageFX imageFX1, imageFX2;
	private ImageFX[] spawns;
	private SoundManager soundManager;
	private CutsceneManager cutsceneManager;
	Random random= new Random();
	int rand;
	private int NUM_ENEMIES= 0;
	private int kills= 0, powerupKills= 0;
	private StripAnimation animation, animation2;
	private int speed= 5;
	private int time, timeChange= 1;
	private HealthPickup health;
	private RotateFX rotate;

	public GamePanel () {
		soundManager= SoundManager.getInstance();
		scoringPanel= new ScoringPanel();
		lifePanel= new LifePanel();
		lifePanel.setDoubleBuffered(true);
		image= new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
		car = null;
		opponents = null;
		spawns= null;
		oppBullets= null;
		kamikaze= null;
		gameThread = null;
		isRunning = false;
		bullet= null;
		health= null;
		backgroundManager = new BackgroundManager("images/Background2.png", 400, 400);
		cutsceneManager = new CutsceneManager(this);
	}

	public void createGameEntities() {
		car = new Car (this, 200, 350);
		createOpponents();
		animation= new StripAnimation("images/kaboom.gif", 6, false);
		animation2= new StripAnimation("images/select.png", 4, true);
		rotate= new RotateFX(this, "images/health.png");
	}

	public void createOpponents(){
		opponents = new Opponent [NUM_ENEMIES];
		oppBullets= new EnemyBullet[NUM_ENEMIES];
		spawns= new ImageFX[NUM_ENEMIES];
		for(int i=0; i<NUM_ENEMIES; i++){
			rand= random.nextInt(400);
			opponents[i] = new Opponent (this, rand, 10, car, i, speed);
		}
		rand= random.nextInt(3);
		speed+= 1;
	}

	public void checkOpponents(){
		if(opponents== null){
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
			if(car != null) {
				backgroundManager.moveBackground(car.getVelX(), car.getVelY());
			}

			for (int i=0; i<NUM_ENEMIES; i++) {
				checkOpponents();
				if(spawns[i]!= null){
					spawns[i].update();
				}
				if (opponents[i] != null){
					opponents[i].move();
				}
				if(oppBullets[i]!= null){
					oppBullets[i].shoot();
				}
				if(bullet!= null && !isPaused){
					bullet.shoot();
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

	}

	public void updateRotate(int num){
		rotate.setRotation(num);
	}

	public void updateBat (int direction) {

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

	public void killEnemy(int x, int y, int height, int width, int type, int enemy, int method){
		if(type==0){
			opponents[enemy]= null;
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

	public void shootBullet(){
		if(bullet== null){
			soundManager.playClip("shoot", false);
			bullet= new Bullet(this, car.getWidth(), car.getX(), car.getY(), car);
		}

		if(car!= null && bullet!= null && !isPaused){
			for (int i=0; i<NUM_ENEMIES; i++) {
				if (opponents[i] != null){
				opponents[i].addBullet(bullet);
				}
			}

			if(kamikaze!= null)
				kamikaze.addBullet(bullet);
			
			bullet.shoot();
		}
	}

	public void enemyShoot(int enemy, int x, int y, int width){
		oppBullets[enemy]= new EnemyBullet(this, width, x, y, car, enemy);
		oppBullets[enemy].shoot();
	}

	public void enemyDeleteBullet(int enemy){
		oppBullets[enemy].goAway(1000, 1000);
		oppBullets[enemy]= null;
	}

	public boolean enemyBulletExist(int enemy){
		if(oppBullets[enemy]!= null){
			return true;
		}
		return false;
	}

	public boolean running(){
		return isRunning;
	}
	
	public void deleteBullet(){
		bullet.goAway(1000, 1000);
		bullet= null;
	}

	public boolean bulletExist(){
		if(bullet!= null){
			return true;
		}
		return false;
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
		Graphics g = getGraphics ();
		Graphics2D imageContext= (Graphics2D) image.getGraphics();

		if (cutsceneManager.isPlaying()) {
			cutsceneManager.draw(imageContext);
		} else {
			backgroundManager.draw(imageContext);
			if (car != null) {
				car.draw(imageContext);
			}

			if (opponents != null) {
				for (int i=0; i<NUM_ENEMIES; i++){
					if(spawns[i]!= null){
						spawns[i].draw(imageContext);
					}
					if (opponents[i] != null){
						opponents[i].draw(imageContext);
					}
					if(oppBullets[i]!= null){
						oppBullets[i].draw(imageContext);
					}
				}
			}

			if(kamikaze!= null){
				kamikaze.draw(imageContext);
			}
			
			if(bullet!= null){
				bullet.draw(imageContext);
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
		soundManager.playClip ("background", true);
		isPaused = false;
		createGameEntities();
		backgroundManager.reset();
		
		gameThread = new Thread (this);			
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
		speed= 5;
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

	public void playCutscene() {
		cutsceneManager.startCutscene();
	}
}