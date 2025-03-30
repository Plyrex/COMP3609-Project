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
	private Image backgroundImage;
	private BufferedImage image;
	private Thread gameThread;
	public ScoringPanel scoringPanel;
	public LifePanel lifePanel;
	private ImageFX imageFX1, imageFX2;
	private ImageFX[] spawns;
	private SoundManager soundManager;
	Random random= new Random();
	int rand;
	private int NUM_ENEMIES= random.nextInt(3, 5);
	private int kills= 0, powerupKills= 0;
	private StripAnimation animation, animation2;
	private int speed= 5;
	private int time, timeChange= 1;
	private HealthPickup health;

	public GamePanel () {
		soundManager= SoundManager.getInstance();
		scoringPanel= new ScoringPanel();
		lifePanel= new LifePanel();
		lifePanel.setDoubleBuffered(true);
		backgroundImage = ImageManager.loadImage ("images/Background1.jpg");
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


	}

	public void createGameEntities() {
		car = new Car (this, 200, 350);
		createOpponents();
		animation= new StripAnimation("images/kaboom.gif", 6, false);
		animation2= new StripAnimation("images/select.png", 4, true);
		// opponents[1] = new Opponent (this, 150, 10, car);
		// opponents[2] = new Opponent (this, 330, 10, car); 

		// imageFX1= new DisintegrateFX(this);
	}

	public void createOpponents(){
		opponents = new Opponent [NUM_ENEMIES];
		oppBullets= new EnemyBullet[NUM_ENEMIES];
		spawns= new ImageFX[NUM_ENEMIES];
		for(int i=0; i<NUM_ENEMIES; i++){
			rand= random.nextInt(400);
			// spawns[i]= new DisappearFX(this, rand, 10, 58, 52, "images/opp.png", i);
			opponents[i] = new Opponent (this, rand, 10, car, i, speed);
		}
		rand= random.nextInt(3);
		if(rand==1 || rand== 2){
			kamikaze= new Kamikaze(this, random.nextInt(400), 10, car, 0);
			soundManager.playClip ("kamikaze", true);
		}
		speed+= 1;
	}

	public void checkOpponents(){
		if(opponents== null){
			createOpponents();
		}
	}

	public void run () {
		try {
			isRunning = true;
			while (isRunning) {
				if(!isPaused)
					gameUpdate();
				gameRender();
				car.tick();
				Thread.sleep (50);	
			}
		}
		catch(InterruptedException e) {}
	}

	public void gameUpdate() {

		if(!isPaused){
			for (int i=0; i<NUM_ENEMIES; i++) {
				checkOpponents();
				// opponents[i].erase();
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
					// bullet.erase();
					bullet.shoot();
				}
			}

			if (kamikaze != null)
				kamikaze.move();
			
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

	}

	public void updateBat (int direction) {

		if (car != null && !isPaused) {
			// car.erase();
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

	// public void spawnEnemy(int x, int y, int height, int width){
	// 	imageFX3= new DisappearFX(this, x, y, height, width, "images/opp.png");
	// }

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

		if(kills== NUM_ENEMIES){
			if(powerupKills== NUM_ENEMIES){
				health= new HealthPickup(this, car);
				animation2.start(health.getX()-20, health.getY()-10);
			}
			powerupKills= 0;
			opponents= null;
			kills= 0;
			NUM_ENEMIES= random.nextInt(3,5);
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
		// repaint();
		// erase();
		Graphics g = getGraphics ();
      		// Graphics2D g2 = (Graphics2D) g;
			Graphics2D imageContext= (Graphics2D) image.getGraphics();
			// g2.setBackground(Color.LIGHT_GRAY);
			// g2.drawImage(backgroundImage, 0, 0, null);
			imageContext.drawImage(backgroundImage, 0, 0, null);

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

		if(animation!= null){
			animation.draw(imageContext);
		}

		if(animation2!= null){
			animation2.draw(imageContext);
		}

		if(health!= null){
			health.draw(imageContext);
		}

		Graphics2D g2= (Graphics2D) getGraphics();
		g2.drawImage(image, 0, 0, 400, 400, null);

		imageContext.dispose();
		g2.dispose();

	}

	public void startGame() {				// initialise and start the game thread 
		if (isRunning)
			return;

		// repaint(); //clears the screen
		// soundManager.setVolume("background", 0.7f);
		soundManager.setVolume("background", 0.7f);
		soundManager.playClip ("background", true);
		isPaused = false;
		createGameEntities();
		gameThread = new Thread (this);			
		gameThread.start();

		// if(animation!= null){
		// 	animation.start();
		// }
	}

	public void pauseGame() {				// pause the game (don't update game entities)
		if (isRunning) {
			if (isPaused)
				isPaused = false;
			else
				isPaused = true;
		}
	}

	public void endGame() {					// end the game thread
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
}