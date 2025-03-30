import javax.swing.*;
import java.awt.*;

public class ScoringPanel extends JPanel{

    private JLabel scoreL;
	private JTextField scoreTF;
    private int score;

    public ScoringPanel(){
        GridLayout gridLayout = new GridLayout(1, 2);
		setLayout(gridLayout);

        score = 0;
        scoreL = new JLabel ("Score: ");

		// create text fields and set their colour, etc.

		scoreTF = new JTextField (Integer.toString(score), 5);
		scoreTF.setEditable(false);
		scoreTF.setBackground(Color.WHITE);

        add(scoreL);
        add(scoreTF);
    }

    public void addPoints(int points){
        score+= points;
        scoreTF.setText(Integer.toString(score)); 
    }

    public void initialize(int init){
        score= 0;
        scoreTF.setText(Integer.toString(init));
    }
}
