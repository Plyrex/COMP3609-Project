import javax.swing.*;			// need this for GUI objects
import java.awt.*;			// need this for Layout Managers
import java.awt.event.*;		// need this to respond to GUI events
	
public class GameWindow extends JFrame 
				implements ActionListener,
					   KeyListener,
					   MouseListener
{
	// declare instance variables for user interface objects

	// declare labels 

	private JLabel statusBarL;
	private JLabel keyL;

	// declare text fields

	private JTextField statusBarTF;
	private JTextField keyTF;

	// declare buttons

	private JButton startB;
	private JButton pauseB;
	private JButton endB;
	private JButton exitB;

	private Container c;

	private JPanel mainPanel;
	private GamePanel gamePanel;

	@SuppressWarnings({"unchecked"})
	public GameWindow() {
 
		setTitle ("Bravo One: Skies of Retribution");
		setSize (525, 525);

		// create user interface objects

		// create labels

		statusBarL = new JLabel ("Application Status: ");
		keyL = new JLabel("Key Pressed: ");

		// create text fields and set their colour, etc.

		statusBarTF = new JTextField (25);
		keyTF = new JTextField (25);

		statusBarTF.setEditable(false);
		keyTF.setEditable(false);

		statusBarTF.setBackground(Color.CYAN);
		keyTF.setBackground(Color.YELLOW);

		// create buttons

		startB = new JButton ("Start");
		pauseB = new JButton ("Pause");
		endB = new JButton ("End Game");
		exitB = new JButton ("Exit");

		// add listener to each button (same as the current object)

		startB.addActionListener(this);
		pauseB.addActionListener(this);
		endB.addActionListener(this);
		exitB.addActionListener(this);
		
		// create mainPanel

		mainPanel = new JPanel();
		FlowLayout flowLayout = new FlowLayout();
		mainPanel.setLayout(flowLayout);

		GridLayout gridLayout;

		// create the gamePanel for game entities

		gamePanel = new GamePanel();
		gamePanel.setPreferredSize(new Dimension(450, 450));

		// create infoPanel

		JPanel infoPanel = new JPanel();
		gridLayout = new GridLayout(2, 2);
		infoPanel.setLayout(gridLayout);
		infoPanel.setBackground(Color.ORANGE);

		// add user interface objects to infoPanel
	
		infoPanel.add (statusBarL);
		infoPanel.add (statusBarTF);

		infoPanel.add (keyL);
		infoPanel.add (keyTF);		
		
		// create buttonPanel

		JPanel buttonPanel = new JPanel();
		gridLayout = new GridLayout(1, 4);
		buttonPanel.setLayout(gridLayout);

		// add buttons to buttonPanel

		buttonPanel.add (startB);
		// buttonPanel.add (pauseB);
		// buttonPanel.add (endB);
		buttonPanel.add (exitB);

		// add sub-panels with GUI objects to mainPanel and set its colour

		// mainPanel.add(infoPanel);
		mainPanel.add(gamePanel);
		mainPanel.add(buttonPanel);
		mainPanel.setBackground(Color.BLACK);

		// set up mainPanel to respond to keyboard and mouse

		gamePanel.addMouseListener(this);
		mainPanel.addKeyListener(this);

		// add mainPanel to window surface

		c = getContentPane();
		c.add(mainPanel);

		// set properties of window

		setResizable(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setVisible(true);

		// set status bar message

		statusBarTF.setText("Application started.");
	}


	// implement single method in ActionListener interface

	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();
		
		statusBarTF.setText(command + " button clicked.");

		if (command.equals(startB.getText())) {
			gamePanel.startGame();
			gamePanel.scoringPanel.initialize(0);
			gamePanel.lifePanel.initialize(5);
			pauseB.setText ("Pause");
		}

		if (command.equals("Pause")) {
			if(gamePanel.running()){
				gamePanel.pauseGame();
				pauseB.setText ("Resume");
			}
		}

		if (command.equals("Resume")) {
			if(gamePanel.running()){
				gamePanel.pauseGame();
				pauseB.setText ("Pause");
			}
		}

		
		if (command.equals(endB.getText())) {
			gamePanel.endGame();
			pauseB.setText ("Pause");
		}

		if (command.equals(exitB.getText()))
			System.exit(0);

		mainPanel.requestFocus();
	}


	// implement methods in KeyListener interface

	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		String keyText = e.getKeyText(keyCode);
		keyTF.setText(keyText + " pressed.");

		if(gamePanel.running()){

			if (keyCode == KeyEvent.VK_LEFT || keyCode== KeyEvent.VK_A) {
				gamePanel.updateBat (1);
			}
	
			if (keyCode == KeyEvent.VK_RIGHT || keyCode== KeyEvent.VK_D) {
				gamePanel.updateBat (2);
			}
	
			if (keyCode == KeyEvent.VK_UP || keyCode== KeyEvent.VK_W) {
				gamePanel.updateBat (3);
			}
	
			if (keyCode == KeyEvent.VK_DOWN || keyCode== KeyEvent.VK_S) {
				gamePanel.updateBat (4);
			}
	
			if(keyCode == KeyEvent.VK_SPACE){
				if(!gamePanel.bulletExist())
					gamePanel.shootBullet();
			}
		}
	}

	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();

		if(gamePanel.running()){

			if (keyCode == KeyEvent.VK_LEFT || keyCode== KeyEvent.VK_A) {
				gamePanel.batStop(0);
			}
	
			if (keyCode == KeyEvent.VK_RIGHT || keyCode== KeyEvent.VK_D) {
				gamePanel.batStop(0);
			}
	
			if (keyCode == KeyEvent.VK_UP || keyCode== KeyEvent.VK_W) {
				gamePanel.batStop(1);
			}
	
			if (keyCode == KeyEvent.VK_DOWN || keyCode== KeyEvent.VK_S) {
				gamePanel.batStop(1);
			}
		}
	}

	public void keyTyped(KeyEvent e) {

	}


	// implement methods in MouseListener interface

	public void mouseClicked(MouseEvent e) {

	}


	public void mouseEntered(MouseEvent e) {
	
	}

	public void mouseExited(MouseEvent e) {
	
	}

	public void mousePressed(MouseEvent e) {
	
	}

	public void mouseReleased(MouseEvent e) {
	
	}

}