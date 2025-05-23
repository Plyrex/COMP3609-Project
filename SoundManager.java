// for playing sound clips
import javax.sound.sampled.*;
import java.io.*;
import java.util.HashMap;				// for storing sound clips
import java.util.Objects;

public class SoundManager {				// a Singleton class
	HashMap<String, Clip> clips;

	private static SoundManager instance = null;	// keeps track of Singleton instance

	private float volume;

	private SoundManager () {
		clips = new HashMap<String, Clip>();

		Clip clip = loadClip("sounds/background.wav");	// played from start of the game
		clips.put("background", clip);

		clip = loadClip("sounds/enemy_hit.wav");
		clips.put("hit", clip);

		clip = loadClip("sounds/player_hit.wav");
		clips.put("playerHit", clip);

		clip = loadClip("sounds/shoot.wav");	
		clips.put("shoot", clip);

		clip = loadClip("sounds/death.wav");	
		clips.put("death", clip);

		clip = loadClip("sounds/kamikaze.wav");	
		clips.put("kamikaze", clip);

		clip = loadClip("sounds/powerup.wav");	
		clips.put("powerup", clip);

		clip = loadClip("sounds/pickup.wav");	
		clips.put("pickup", clip);

		clip = loadClip("sounds/takeoff.wav");
		clips.put("takeoff", clip);

		clip = loadClip("sounds/engine.wav");
		clips.put("engine", clip);

		clip = loadClip("sounds/wind.wav");
		clips.put("wind", clip);

		clip = loadClip("sounds/background2.wav");
		clips.put("country2", clip);

		clip = loadClip("sounds/background3.wav");
		clips.put("country3", clip);

		clip = loadClip("sounds/backgroundfull.wav");
		clips.put("backgroundfull", clip);

		volume = 1.0f;
	}


	public static SoundManager getInstance() {	// class method to retrieve instance of Singleton
		if (instance == null)
			instance = new SoundManager();
		
		return instance;
	}		


    	public Clip loadClip (String fileName) {	// gets clip from the specified file
 		AudioInputStream audioIn;
		Clip clip = null;

		try {
    			File file = new File(fileName);
    			audioIn = AudioSystem.getAudioInputStream(file.toURI().toURL()); 
    			clip = AudioSystem.getClip();
    			clip.open(audioIn);
		}
		catch (Exception e) {
 			System.out.println ("Error opening sound files: " + e);
		}
    		return clip;
    	}


	public Clip getClip (String title) {

		return clips.get(title);
	}


    	public void playClip(String title, boolean looping) {
		Clip clip = getClip(title);
		if (clip != null) {
			clip.setFramePosition(0);
			if (looping)
				clip.loop(Clip.LOOP_CONTINUOUSLY);
			else
				clip.start();
		}
    	}


    	public void stopClip(String title) {
		Clip clip = getClip(title);
		if (clip != null) {
			clip.stop();
		}
    	}

		public void setVolume (String title, float volume) {
    Clip clip = getClip(title);
    if (clip == null) return;

    if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float range = gainControl.getMaximum() - gainControl.getMinimum();
        float gain = (range * volume) + gainControl.getMinimum();
        gainControl.setValue(gain);
    } else {
        System.out.println("Volume control not supported for: " + title);
    }
}

}