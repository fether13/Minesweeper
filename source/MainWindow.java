import java.util.Random;
import java.util.Vector;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class MainWindow {

	public static int WIDTH = 4, HEIGHT = 4, MINES = 2, TILE_SIZE = 30;
	public boolean playing = false;
	final int TILE_GAP = 5;
	final int MAX_WIDTH = 50, MAX_HEIGHT = 20;

	///// COLORS
	Color gray1 = Color.decode("#BFBFBF");
	Color gray2 = Color.decode("#808080");
	Color gray3 = Color.decode("#666666");
	Color gray4 = Color.decode("#4A4A4A");
	Color gray5 = Color.decode("#333333");

	public JFrame window;
	/////
	JPanel interfacePanel;

	private JPanel customizePanel; // contains text fields and buttons
	/////

	// Components that need to be global for functional reasons
	private JButton[][] buttons; // list of every button in the field
	private final ButtonGroup difficultyGroup = new ButtonGroup();
	private JButton confirmButton;
	private JRadioButton easy, medium, hard, custom;
	private JTextField heightTF, widthTF, minesTF;
	private Font customFont;
	private JLabel scoreLabel;
	private JLabel winLabel;
	public Grid grid;
	private int score;

	///////////////////////////////////////
	// CONSTRUCTOR
	public MainWindow() {
		handleFont();
		window = initWindow();
	}

	///////////////////////////////////////
	// WINDOW SETTINGS
	private JFrame initWindow() {

		JFrame window = new JFrame();

		window.setTitle("Minesweeper");
		window.setResizable(false);
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// the JFrame only contains the general interface panel
		interfacePanel = initInterface();
		window.add(interfacePanel);

		window.setVisible(true);
		window.pack();
		window.setLocationRelativeTo(null);

		return window;
	}

	///////////////////////////////////////
	private void handleFont() {
		InputStream is = getClass().getResourceAsStream("/assets/BakbakOne-Regular.ttf");
		try {
			customFont = Font.createFont(Font.TRUETYPE_FONT, is);
		} catch (FontFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	///////////////////////////////////////
	// MAIN INTERFACE PANEL
	private JPanel initInterface() {

		JPanel interfacePanel = new JPanel();
		interfacePanel.setLayout(new BoxLayout(interfacePanel, BoxLayout.Y_AXIS));

		interfacePanel.setBackground(gray4);

		grid = new Grid(); //reset the grid everytime the interface is rebuilt
		score = -1;

		buttons = new JButton[HEIGHT][WIDTH]; // renew the button array
		if (WIDTH >= 32 || HEIGHT >= 16)
			TILE_SIZE = 25;
		else
			TILE_SIZE = 30;

		// contains the menu and the board
		/////
		// main menu panel
		JPanel menuPanel = initMenu();
		/////
		// main board panel
		JPanel boardPanel = initBoard();

		interfacePanel.add(menuPanel);
		interfacePanel.add(boardPanel);

		return interfacePanel;
	}

	///////////////////////////////////////
	// MENU INIT
	private JPanel initMenu() {
		JPanel menuPanel = new JPanel(new BorderLayout());
		menuPanel.setBackground(gray3);
		menuPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		// the menu panel contains the quit panel and the settings panel
		JPanel quitPanel = initQuitPanel();
		menuPanel.add(quitPanel, BorderLayout.EAST);

		// contains all the useful things
		JPanel settingsPanel = initSettings();
		menuPanel.add(settingsPanel, BorderLayout.CENTER);

		return menuPanel;
	}

	// QUIT BUTTON PANEL
	private JPanel initQuitPanel() {
		JPanel quitPanel = new JPanel();
		quitPanel.setLayout(new BoxLayout(quitPanel, BoxLayout.Y_AXIS));
		quitPanel.setBackground(gray3);

		// this panel only contains the quit button
		JButton quitButton = generateMenuButton("QUIT");
		quitButton.setFont(customFont.deriveFont(15f));
		quitButton.setFocusable(false);

		quitButton.addActionListener(e -> window.dispose());
		quitPanel.add(quitButton);

		// I'll also add the fullscreen toggle here
		JButton fullscreenButton = generateMenuButton("");
		ImageIcon fsIcon = new ImageIcon("src/assets/fs.png");
		ImageIcon sfIcon = new ImageIcon("src/assets/sf.png");

		fullscreenButton.setFont(customFont.deriveFont(13f));
		fullscreenButton.setIcon(fsIcon);
		fullscreenButton.setBorder(new EmptyBorder(5, 5, 5, 5));
		fullscreenButton.setAlignmentX(JButton.CENTER);

		fullscreenButton.addActionListener(new ActionListener() {
			boolean fs = false;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (fs) {
					window.setExtendedState(JFrame.NORMAL);
					window.pack();
					fs = false;
					fullscreenButton.setIcon(fsIcon);
				} else {
					window.setExtendedState(JFrame.MAXIMIZED_BOTH);
					fs = true;
					fullscreenButton.setIcon(sfIcon);
				}
				// TODO Auto-generated method stub
			}
		});

		quitPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		quitPanel.add(fullscreenButton);

		//display the score
		scoreLabel = new JLabel();
		scoreLabel.setFont(customFont.deriveFont(13f));
		scoreLabel.setForeground(Color.white);

		winLabel = new JLabel();
		winLabel.setFont(customFont.deriveFont(13f));

		quitPanel.add(scoreLabel);
		quitPanel.add(winLabel);

		return quitPanel;
	}

	// SETTINGS PANEL
	private JPanel initSettings() {
		JPanel settingsPanel = new JPanel(new FlowLayout());

		// this one contains the difficulty and the customization panels
		// contains difficulty levels
		JPanel difficultyPanel = initDifficulty();
		customizePanel = initCustomize();

		settingsPanel.setBackground(gray3);
		settingsPanel.add(difficultyPanel);
		settingsPanel.add(customizePanel);

		return settingsPanel;
	}

	// DIFFICULTY PANEL
	private JPanel initDifficulty() {
		JPanel difficultyPanel = new JPanel();
		difficultyPanel.setLayout(new BoxLayout(difficultyPanel, BoxLayout.Y_AXIS));
		difficultyPanel.setBackground(gray2);
		difficultyPanel.setBorder(new LineBorder(gray1, 1));
		// this panel contains the "difficulty" label and the radio buttons

		JLabel difficultyLabel = new JLabel(" DIFFICULTY ");
		difficultyLabel.setForeground(Color.WHITE);
		difficultyLabel.setFont(customFont.deriveFont(16f));

		easy = generateDifficultyLevel("easy");
		medium = generateDifficultyLevel("medium");
		hard = generateDifficultyLevel("hard");
		custom = generateDifficultyLevel("custom");

		// selecting custom enables the text fields
		custom.addActionListener(e -> {
			// TODO Auto-generated method stub
			heightTF.setFocusable(true);
			widthTF.setFocusable(true);
			minesTF.setFocusable(true);

			heightTF.setEditable(true);
			widthTF.setEditable(true);
			minesTF.setEditable(true);

			heightTF.setForeground(Color.WHITE);
			widthTF.setForeground(Color.WHITE);
			minesTF.setForeground(Color.WHITE);
		});

		// selecting the other ones disables them
		ActionListener disableTFs = e -> {

			heightTF.setFocusable(false);
			widthTF.setFocusable(false);
			minesTF.setFocusable(false);

			heightTF.setEditable(false);
			widthTF.setEditable(false);
			minesTF.setEditable(false);

			heightTF.setForeground(gray1);
			widthTF.setForeground(gray1);
			minesTF.setForeground(gray1);

			heightTF.setText("height");
			widthTF.setText("width");
			minesTF.setText("mines");
		};

		easy.addActionListener(disableTFs);
		medium.addActionListener(disableTFs);
		hard.addActionListener(disableTFs);

		difficultyPanel.add(difficultyLabel);
		difficultyPanel.add(easy);
		difficultyPanel.add(medium);
		difficultyPanel.add(hard);
		difficultyPanel.add(custom);

		return difficultyPanel;
	}

	// CUSTOMIZE PANEL
	private JPanel initCustomize() {
		customizePanel = new JPanel();
		customizePanel.setLayout(new BoxLayout(customizePanel, BoxLayout.Y_AXIS));

		// in this panel we have the three text fields
		heightTF = generateTextField("height");
		widthTF = generateTextField("width");
		minesTF = generateTextField("mines");

		customizePanel.add(widthTF);
		customizePanel.add(Box.createRigidArea(new Dimension(0, 5)));
		customizePanel.add(heightTF);
		customizePanel.add(Box.createRigidArea(new Dimension(0, 5)));
		customizePanel.add(minesTF);
		customizePanel.add(Box.createRigidArea(new Dimension(0, 5)));

		// it should also contain the space to display error messages
		JLabel errorLabel = new JLabel();
		errorLabel.setForeground(Color.RED);
		errorLabel.setFont(customFont.deriveFont(10f));
		errorLabel.setText("");

		customizePanel.add(errorLabel);
		errorLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

		// we also need to add the confirm and the reset button
		// CONFIRM BUTTON
		confirmButton = generateMenuButton("CONFIRM");
		confirmButton.setEnabled(false);
		confirmButton.setAlignmentX(JButton.CENTER_ALIGNMENT);

		confirmButton.addActionListener(e -> {
			if (!checkTFinput() && custom.isSelected()) {
				errorLabel.setText("Invalid input.");
				window.pack();
			}
			confirmAction();
		});

		// RESET BUTTON
		JButton resetButton = generateMenuButton("RESET");
		resetButton.setAlignmentX(JButton.CENTER_ALIGNMENT);

		resetButton.addActionListener(e -> {
			grid = new Grid();
			rebuildFrame();
		});

		customizePanel.add(resetButton);

		//////////
		customizePanel.add(Box.createRigidArea(new Dimension(0, 5)));
		customizePanel.add(confirmButton);
		customizePanel.add(Box.createRigidArea(new Dimension(0, 5)));

		customizePanel.setBackground(gray3);

		return customizePanel;
	}

	///////////////////////////////////////
	// BOARD PANEL
	private JPanel initBoard() {
		JPanel boardPanel = new JPanel(new FlowLayout());
		boardPanel.setBackground(gray4);
		boardPanel.setBorder(new EmptyBorder(new Insets(15, 15, 15, 15)));

		// Board only contains the Field panel
		// the grid containing the squares
		JPanel fieldPanel = initField();
		boardPanel.add(fieldPanel);

		return boardPanel;
	}

	// FIELD PANEL
	private JPanel initField() {
		JPanel fieldPanel = new JPanel(new GridLayout(HEIGHT, WIDTH, TILE_GAP, TILE_GAP));
		fieldPanel.setBackground(gray4);

		// this panel only contains the square buttons
		Square square;

		for (int i = 0; i < HEIGHT; i++) {
			for (int j = 0; j < WIDTH; j++) {
				square = generateSquare(j, i);
				buttons[i][j] = square;
				fieldPanel.add(square);
			}

		}

		return fieldPanel;
	}

	/////////////////////////////////////// USEFUL STUFF
	// GENERATE STANDARD MENU BUTTON
	private JButton generateMenuButton(String label) {
		JButton button = new JButton(label);

		button.setBackground(gray4);
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
		button.setRolloverEnabled(false);
		button.setFocusPainted(false);
		button.setFont(customFont.deriveFont(14f));

		//EmptyBorder empty = new EmptyBorder(1, 1, 1, 1);
		//LineBorder line = new LineBorder(gray1, 1);
		//CompoundBorder compound = new CompoundBorder(line, empty);

		// button.setBorder(compound);

		// button.setMargin(new Insets(3,15,3,15));
		// button.setBorder(new LineBorder(gray1, 1));

		return button;
	}

	// GENERATE DIFFICULTY RADIO BUTTONS
	private JRadioButton generateDifficultyLevel(String label) {
		JRadioButton difficultyRadio = new JRadioButton(label);

		difficultyRadio.setBackground(gray2);
		difficultyRadio.setForeground(Color.WHITE);
		difficultyRadio.setFocusable(true);
		difficultyRadio.setFont(customFont.deriveFont(14f));
		difficultyRadio.setMargin(new Insets(0, 0, 0, 0));
		difficultyRadio.setFocusPainted(false);

		difficultyRadio.addActionListener(e -> confirmButton.setEnabled(true));

		difficultyGroup.add(difficultyRadio);
		return difficultyRadio;
	}

	// GENERATE TEXT FIELD
	private JTextField generateTextField(String label) {
		JTextField textfield;
		textfield = new JTextField();

		textfield.setBackground(gray3);
		textfield.setBorder(new LineBorder(gray1, 1));
		textfield.setForeground(gray1);
		textfield.setFont(customFont.deriveFont(13f));
		textfield.setText(label);
		textfield.setColumns(6);
		textfield.setEditable(false);
		textfield.setFocusable(false);

		EmptyBorder empty = new EmptyBorder(1, 1, 1, 1);
		LineBorder line = new LineBorder(gray1, 1);
		CompoundBorder compound = new CompoundBorder(line, empty);
		textfield.setBorder(compound); // set a more precise border

		textfield.addFocusListener(new FocusListener() { // make a placeholder appear when not focused and blank
			public void focusGained(FocusEvent e) {
				if (textfield.getText().equals(label)) {
					textfield.setText("");
				}
			}

			public void focusLost(FocusEvent e) {
				if (textfield.getText().isBlank()) {
					textfield.setText(label);
				}
			}
		});

		return textfield;
	}

	// GENERATE SQUARE
	private Square generateSquare(int x, int y) {
		Square square = new Square(x, y);
		square.setPreferredSize(new Dimension(TILE_SIZE, TILE_SIZE));
		square.setBackground(gray5);
		square.setRolloverEnabled(false);
		square.setFocusPainted(false);

		EmptyBorder empty = new EmptyBorder(1, 1, 1, 1);
		LineBorder line = new LineBorder(gray2, 2);
		CompoundBorder compound = new CompoundBorder(line, empty); //more precise border
		square.setBorder(compound); // set a more precise border

		square.addActionListener(e -> {

			//generate the grid when the first is clicked
			if (!playing) {
				playing = true;
				grid.populate(square.x, square.y);
				square.doClick();
			}
			square.setEnabled(false);
			//if the game already started
			if(playing) {
				square.setText(grid.display(square.x, square.y));
				grid.click(square.x, square.y);
			}

			//very ugly code to click the adjacent non-bomb squares
			if (grid.mat[square.y][square.x]==0) { //if it's a 0 keep uncovering
				for (int j = -1; j <= 1; j++) {
					for (int k = -1; k <= 1; k++)
						if (square.y + j >= 0 && square.y + j < HEIGHT && square.x + k >= 0 && square.x + k < WIDTH
								&& grid.mat[square.y + j][square.x + k] != -1) {
							if(j==0 || k==0) //always click horizontally and vertically
								buttons[square.y + j][square.x + k].doClick();
							else {
								if(grid.mat[square.y + j][square.x + k]>0) //only click diagonally if it's not a 0
									buttons[square.y + j][square.x + k].doClick();
							}
						}
				}
			}

			scoreLabel.setText("SCORE: " + score);
			if(score == WIDTH * HEIGHT - MINES) {
				endGame(true);
			}
			if(grid.mat[square.y][square.x]==-1)
				endGame(false);
		});
		return square;
	}

	//END THE GAME
	private void endGame(boolean won) {
		playing = false;

		if (won) {
			winLabel.setText("YOU WON");
			winLabel.setForeground(Color.GREEN);
		} else {
			winLabel.setText("GAME OVER");
			winLabel.setForeground(Color.RED);
			for(int i=0; i<HEIGHT; i++) {
				for(int j=0; j<WIDTH; j++) {
					if(grid.mat[i][j]==-1) {
						buttons[i][j].setText("-1");
					}
				}
			}
		}

		for(int i=0; i<HEIGHT; i++) {
			for(int j=0; j<WIDTH; j++) {
				buttons[i][j].setEnabled(false);
			}
		}
	}

	// PARSE AND CHECK TF VALUES AND SAVE THEM INTO GLOBALS
	private boolean checkTFinput() {

		try {
			int height = Integer.parseInt(heightTF.getText());
			int width = Integer.parseInt(widthTF.getText());
			int mines = Integer.parseInt(minesTF.getText());

			if (height > 0 && width > 0 && mines > 0 && mines < height * width && height <= MAX_HEIGHT && width <= MAX_WIDTH) {

				WIDTH = width;
				HEIGHT = height;
				MINES = mines;
				return true;

			} else
				return false;

		} catch (NumberFormatException e) {
			return false;
		}
	}

/////////////////////////////////////// 

	// RESET THE BOARD WHEN THE CONFIRM BUTTON IS PRESSED
	private void confirmAction() {

		if (easy.isSelected()) { // easy difficulty
			WIDTH = 9;
			HEIGHT = 9;
			MINES = 10;
			rebuildFrame();
			window.pack();

		} else if (medium.isSelected()) { // medium difficulty
			WIDTH = 16;
			HEIGHT = 16;
			MINES = 40;
			rebuildFrame();
			window.pack();

		} else if (hard.isSelected()) { // hard difficulty
			WIDTH = 30;
			HEIGHT = 16;
			MINES = 99;
			rebuildFrame();
			window.pack();

		} else if (custom.isSelected() && checkTFinput()) { // custom is handled by checkTFinput()
			rebuildFrame();
			window.pack();
		}

	}

	private void rebuildFrame() {
		playing = false; //stop the game

		JFrame newFrame;
		JFrame oldFrame;

		newFrame = initWindow();
		newFrame.setVisible(true);
		oldFrame = window;

		window.setVisible(false);
		window = newFrame;
		oldFrame.dispose();

	}

	class Square extends JButton {
		int x, y; 
		public Square(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	///////////////////////////////////////
	class Grid {
		public int[][] mat;

		public Grid() {
			mat = new int[HEIGHT][WIDTH];
			reset();
		}

		public void reset() {

			for (int i = 0; i < HEIGHT; i++) {
				for (int j = 0; j < WIDTH; j++) {
					mat[i][j] = 0;
				}
			}
		}

		public void populate(int x, int y) {
			Random random = new Random();
			Vector<Integer> selection = new Vector<Integer>();

			int index;
			int[] mines = new int[MINES];
			int tempx, tempy;

			//selection contains the possible coords
			for (int i = 0; i < HEIGHT * WIDTH; i++) {
				selection.add(i);
			}

			//remove squares adjacent to the click from the possible bomb coords
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					if (y + i >= 0 && y + i < HEIGHT && x + j >= 0 && x + j < WIDTH) {
						selection.removeElement((y + i) * WIDTH + (x + j));
					}
				}
			}

			//
			for (int i = 0; i < MINES; i++) {
				//determine the coords of the bomb

				index = random.nextInt(selection.size());
				mines[i] = selection.elementAt(index);
				//since there already is a bomb remove it from the possibilities
				selection.removeElementAt(index);
				tempy = mines[i] / WIDTH;
				tempx = mines[i] % WIDTH;
				mat[tempy][tempx] = -1;
			}
			for (int i = 0; i < MINES; i++) {
				tempy = mines[i] / WIDTH;
				tempx = mines[i] % WIDTH;
				//increase the number of the 8 adjacent squares if they're not bombs
				for (int j = -1; j <= 1; j++) {
					for (int k = -1; k <= 1; k++) {
						if (tempy + j >= 0 && tempy + j < HEIGHT && tempx + k >= 0 && tempx + k < WIDTH
								&& mat[tempy + j][tempx + k] != -1) {
							mat[tempy+j][tempx+k]++;
						}
					}
				}
			}
		}

		public void click(int x, int y){
			buttons[y][x].setEnabled(false);
			if(mat[y][x] != -1)
				score++;
		}

		public String display(int x, int y) {
			String value = "" + mat[y][x];
			return value;
		}

	}
}