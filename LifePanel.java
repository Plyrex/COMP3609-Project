import javax.swing.*;
import java.awt.*;
import java.text.MessageFormat;

public class LifePanel extends JPanel{

    private JLabel lifeL;
	private JLabel lifeL2;
    private int lives;
    private Image lifeImage;

    public LifePanel(){
        GridLayout gridLayout = new GridLayout(1, 2);
		setLayout(gridLayout);
        
        lives = 5;
        lifeL = new JLabel ("Health: ");
        
        lifeImage= ImageManager.loadImage("images/life_5.png");
        ImageIcon icon= new ImageIcon(lifeImage);

        lifeL2= new JLabel(icon);

		// lifeTF = new JTextField (Integer.toString(lives), 5);
		// lifeTF.setEditable(false);
		// lifeTF.setBackground(Color.WHITE);

        add(lifeL);
        add(lifeL2);
    }

    public void loseLife(int oof){
        lives-= oof;
        if(lives<= 0)
            lives= 0;

        lifeImage= ImageManager.loadImage(MessageFormat.format("images/life_{0}.png", lives));
        ImageIcon icon= new ImageIcon(lifeImage);
        lifeL2.setIcon(icon);
        
        // lifeTF.setText(Integer.toString(lives)); 
    }

    public void addLife(int yay){
        lives+= yay;
        if(lives>= 5)
            lives= 5;
        
        lifeImage= ImageManager.loadImage(MessageFormat.format("images/life_{0}.png", lives));
        ImageIcon icon= new ImageIcon(lifeImage);
        lifeL2.setIcon(icon);
        // lifeTF.setText(Integer.toString(lives)); 
    }

    public int getLifeTotal(){
        return lives;
    }

    public void initialize(int init){
        lives= 5;
        lifeImage= ImageManager.loadImage(MessageFormat.format("images/life_{0}.png", lives));
        ImageIcon icon= new ImageIcon(lifeImage);
        lifeL2.setIcon(icon);
        // lifeTF.setText(Integer.toString(init));
    }
}
