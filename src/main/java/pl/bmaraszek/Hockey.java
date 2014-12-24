package pl.bmaraszek;

import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.MediaTracker;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

/**
 * @author bartek
 * 
 */
public class Hockey extends Frame implements ActionListener, MouseListener, Runnable, MouseMotionListener {

	private static final long serialVersionUID = 7680872122208478486L;
	/*
	 * Menu variables:
	 */
	private MenuBar menuBar;
	private Menu fileMenu, helpMenu;
	private MenuItem startGameMenu, endGameMenu, setSpeedMenu, exitGameMenu, howTo;
	/*
	 * Score:
	 */
	private Label humanPlayerScoreLabel, computerPlayerScoreLabel;
	/*
	 * Images:
	 */
	private MediaTracker tracker;
	public Image backgroundImage, instructions, memoryImage, humanPlayer, computerPlayer, goal, puck;
	private Graphics memoryGraphics;
	/*
	 * Thread variables
	 */
	private Thread thread;
	private boolean runOK = true;
	private boolean stop = true;
	/*
	 * Pucks:
	 */
	private Vector<Puck> pucks = new Vector<Puck>();
	private Puck humanPuck, computerPuck;
	private GameObject humanGoal, computerGoal;
	private boolean dragging = false;
	/*
	 * Speed setting dialog:
	 */
	private OkCancelDialog setSpeedDialog;
	private InfoPanel howToPlayPanel;
	/*
	 * Settings variables:
	 */
	private int humanPlayerScore = 0;
	private int computerPlayerScore = 0;
	private int offsetY = 0;
	private int speed = 50;
	/*
	 * Constants:
	 */
	private static final int maxVelocity = 10;
	private static final int numberOfPucks = 5;

	private Rectangle edges;

	public enum puckType {
		NORMAL, PLAYER, COMPUTER
	}

	public enum goalType {
		PLAYER, COMPUTER
	}
	
	public Hockey() {

		createMenuBar();

		addMouseListeners();

		loadImages();

		setScoreLabels();

		setInfoPanels();

		setMainWindow();

		setTextDialog();

		setMemoryImage();

		startThreads();

		addWindowListeners();

	}

	private void createMenuBar() {
		menuBar = new MenuBar();

		fileMenu = new Menu("File");
		helpMenu = new Menu("Help");

		startGameMenu = new MenuItem("Start new game");
		fileMenu.add(startGameMenu);
		startGameMenu.addActionListener(this);

		endGameMenu = new MenuItem("End current game");
		fileMenu.add(endGameMenu);
		endGameMenu.addActionListener(this);

		setSpeedMenu = new MenuItem("Set pucks speed");
		fileMenu.add(setSpeedMenu);
		setSpeedMenu.addActionListener(this);

		exitGameMenu = new MenuItem("Exit Game");
		fileMenu.add(exitGameMenu);
		exitGameMenu.addActionListener(this);

		howTo = new MenuItem("How to play");
		helpMenu.add(howTo);
		howTo.addActionListener(this);

		menuBar.add(fileMenu);
		menuBar.add(helpMenu);
		setMenuBar(menuBar);
	}

	private void addMouseListeners() {
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	private void setScoreLabels() {
		humanPlayerScoreLabel = new Label();
		humanPlayerScoreLabel.setFont(new Font("Arial", Font.PLAIN, 24));
		humanPlayerScoreLabel.setText("0");
		humanPlayerScoreLabel.setBounds(200, 400, 30, 20);
		humanPlayerScoreLabel.setVisible(false);
		add(humanPlayerScoreLabel);

		computerPlayerScoreLabel = new Label();
		computerPlayerScoreLabel.setFont(new Font("Arial", Font.PLAIN, 24));
		computerPlayerScoreLabel.setText("0");
		computerPlayerScoreLabel.setBounds(600, 400, 30, 20);
		computerPlayerScoreLabel.setVisible(false);
		add(computerPlayerScoreLabel);
	}

	private void loadImages() {
		ClassLoader loader = getClass().getClassLoader();
		tracker = new MediaTracker(this);
		backgroundImage = Toolkit.getDefaultToolkit().getImage(loader.getResource("rink.png"));
		tracker.addImage(backgroundImage, 0);

		puck = Toolkit.getDefaultToolkit().getImage(loader.getResource("puck.png"));
		tracker.addImage(puck, 0);

		humanPlayer = Toolkit.getDefaultToolkit().getImage(loader.getResource("player1.png"));
		tracker.addImage(humanPlayer, 0);

		computerPlayer = Toolkit.getDefaultToolkit().getImage(loader.getResource("player2.png"));
		tracker.addImage(computerPlayer, 0);

		goal = Toolkit.getDefaultToolkit().getImage(loader.getResource("goal.png"));
		tracker.addImage(goal, 0);

		try {
			tracker.waitForID(0);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private void setMainWindow() {
		setLayout(null);
		setTitle("Hockey!");
		setResizable(false);

		setVisible(true);

		setSize(backgroundImage.getWidth(this) + getInsets().left + getInsets().right, backgroundImage.getHeight(this)
				+ getInsets().top + getInsets().bottom);
	}

	private void addWindowListeners() {
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				runOK = false;
				System.exit(0);
			}
		});
	}

	private void setTextDialog() {
		setSpeedDialog = new OkCancelDialog(this, "Set speed (1-100)", true);
	}

	private void setInfoPanels() {
		howToPlayPanel = new InfoPanel(humanPlayer, "<p>(1) Click on your goalkeeper and drag him around to deflect incoming pucks</p>"
				+ "<p>(2) Click File->Start new game to begin playing</p>"
				+ "<p>(3) Click File->Set puck speed to alter the game speed. The default speed is 50.</p>"
				+ ""
				+ "<p>Enjoy your play!</p>");
	}

	private void setMemoryImage() {
		memoryImage = createImage(getSize().width, getSize().height);
		memoryGraphics = memoryImage.getGraphics();
	}

	private void startThreads() {
		thread = new Thread(this);
		thread.start();
	}

	private void init() {
		pucks = new Vector<Puck>();

		edges = new Rectangle(0, 0, backgroundImage.getWidth(this) - getInsets().right, backgroundImage.getHeight(this)
				- getInsets().bottom);

		for (int i = 0; i < numberOfPucks; ++i) {
			pucks.add(new Puck(puck, puckType.NORMAL, maxVelocity, this));

			try {
				Thread.sleep(20);
			} catch (Exception e) {
				System.out.println(e);
			}
		}

		Point humanGoalLocation = new Point((int) (edges.getMinX()),
				(int) (backgroundImage.getHeight(this) / 2 - (goal.getHeight(this)) / 2));

		Point computerGoalLocation = new Point((int) (edges.getMaxX() - goal.getWidth(this)),
				(int) (backgroundImage.getHeight(this) / 2 - (goal.getHeight(this)) / 2));

		humanGoal = new Goal(goal, humanGoalLocation, this);
		computerGoal = new Goal(goal, computerGoalLocation, this);

		humanPuck = new Puck(humanPlayer, puckType.PLAYER, maxVelocity, this);
		computerPuck = new Puck(computerPlayer, puckType.COMPUTER, maxVelocity, this);

	}

	@Override
	public void run() {
		while (runOK) {
			if (!stop) {
				// movePlayerPuck();
				computerPlayerBlockPuck();
				movePucks();
				checkCollisions();
				repaint();
				try {
					Thread.sleep(speed);
				} catch (InterruptedException e) {
					System.out.println(e.getMessage());
				}
			}//if
		}//while
	}//eof

	private void movePucks() {
		for (int i = 0; i < pucks.size(); ++i) {
			pucks.elementAt(i).move();
		}
	}

	private void checkCollisions() {
		checkPuckGoalkeeperCollisions(humanPuck);
		checkPuckGoalkeeperCollisions(computerPuck);
		checkPuckPuckCollisions();
		checkPuckWallCollisions();
		checkPuckGoalCollisions();
	}

	private void checkPuckGoalCollisions() {
		Puck p;
		for (int i = 0; i < pucks.size(); ++i) {
			p = pucks.elementAt(i);
			if (p.getRectangle().intersects(computerGoal.getRectangle())) {
				humanPlayerScoreLabel.setText(String.valueOf(++humanPlayerScore));
				pucks.removeElement(p);
				--i;
			} else if (p.getRectangle().intersects(humanGoal.getRectangle())) {
				computerPlayerScoreLabel.setText(String.valueOf(++computerPlayerScore));
				pucks.removeElement(p);
				--i;
			}
		}
	}

	private void checkPuckWallCollisions() {
		Rectangle rect;
		for (Puck p : pucks) {
			rect = p.getRectangle();
			// touches up or down
			if (rect.getMinY() < edges.getMinY()) {
				rect.y = edges.y;
				p.reverseYVelocity();

			} else if (rect.getMaxY() > edges.getMaxY()) {
				rect.y = (int) (edges.getMaxY() - rect.height);
				p.reverseYVelocity();
			}
			// touches left of right
			if (rect.getMinX() < edges.getMinX()) {
				rect.x = edges.x;
				p.reverseXVelocity();
			} else if (rect.getMaxX() > edges.getMaxX()) {
				rect.x = (int) (edges.getMaxX() - rect.width);
				p.reverseXVelocity();
			}
		}
	}

	private void checkPuckPuckCollisions() {
		Puck p1, p2;
		Rectangle intersection;
		/*
		 * We can go for O(n^2) algorithm since the number of pucks is small. For 12 pucks we go 1/2*n^2 + n = 76 times
		 * which is reasonably few.
		 */
		for (int i = 0; i < pucks.size() - 1; ++i) {
			p1 = pucks.elementAt(i);
			for (int j = i + 1; j < pucks.size(); ++j) {
				p2 = pucks.elementAt(j);

				intersection = p1.getRectangle().intersection(p2.getRectangle());

				if (!intersection.isEmpty()) {
					/*
					 * Move the pucks so they don't touch. The right way to move them depends on their mutual position,
					 * thus 4 if conditions. Analyze carefully.
					 */
					if (p1.getRectangle().getMaxY() > p2.getRectangle().getMaxY()) {
						p1.getRectangle().y -= intersection.height / 2;
						p2.getRectangle().y += intersection.height / 2;
					} else if (p1.getRectangle().getMaxY() < p2.getRectangle().getMaxY()) {
						p1.getRectangle().y += intersection.height / 2;
						p2.getRectangle().y -= intersection.height / 2;
					}

					if (p1.getRectangle().getMaxX() > p2.getRectangle().getMaxX()) {
						p1.getRectangle().x += intersection.width / 2;
						p2.getRectangle().x -= intersection.width / 2;
					} else if (p1.getRectangle().getMaxX() < p2.getRectangle().getMaxX()) {
						p1.getRectangle().x -= intersection.width / 2;
						p2.getRectangle().x += intersection.width / 2;
					}
					/*
					 * Inverse the velocity:
					 */
					if (intersection.height > intersection.width) {
						p1.reverseXVelocity();
						p2.reverseXVelocity();
					} else if (intersection.height < intersection.width) {
						p1.reverseYVelocity();
						p2.reverseYVelocity();
					} else {
						p1.reverseVelocity();
						p2.reverseVelocity();
					}// if
				}// if
			}// inner for
		}// outer for
	}// end of function

	private void checkPuckGoalkeeperCollisions(Puck goalkeeper) {
		Rectangle intersection;
		for (Puck p : pucks) {
			intersection = p.getRectangle().intersection(goalkeeper.getRectangle());
			if (!intersection.isEmpty()) {
				/*
				 * Move the puck so it doesn't touch the goalkeeper (similar to puck-puck collision handling):
				 */
				if (p.getRectangle().getMaxY() > goalkeeper.getRectangle().getMaxY()) {
					p.getRectangle().y -= intersection.height + 1;
				} else if (p.getRectangle().getMaxY() < goalkeeper.getRectangle().getMaxY()) {
					p.getRectangle().y += intersection.height + 1;
				}

				if (p.getRectangle().getMaxX() > goalkeeper.getRectangle().getMaxX()) {
					p.getRectangle().x += intersection.width + 1;
				} else if (p.getRectangle().getMaxX() < goalkeeper.getRectangle().getMaxX()) {
					p.getRectangle().x -= intersection.width + 1;
				}
				/*
				 * Inverse the velocity:
				 */
				if (intersection.height > intersection.width) {
					p.reverseXVelocity();
					p.setXVelocity(p.getXVelocity() * 2);
				} else if (intersection.height < intersection.width) {
					p.reverseYVelocity();
					p.setYVelocity(p.getYVelocity() * 2);
				} else {
					p.reverseVelocity();
					p.setXVelocity(p.getXVelocity() * 2);
					p.setYVelocity(p.getYVelocity() * 2);
				}// if
			}// if
		}// for
	}// eof

	private void computerPlayerBlockPuck() {
		int lowestTime = 10000;
		int impactY = -1;

		for (int i = 0; i < pucks.size(); i++) {
			Puck movingPuck = (Puck) pucks.elementAt(i);
			Rectangle r = movingPuck.getRectangle();
			Point mPosition = new Point(r.x, r.y);
			Point mVelocity = movingPuck.getVelocity();

			if (mVelocity.x > 0) {
				int yHit = (mVelocity.y / mVelocity.x) * (backgroundImage.getWidth(this) - mPosition.x) + mPosition.y;

				if (yHit > 121 && yHit < 353) {
					int time = (backgroundImage.getWidth(this) - mPosition.x) / mVelocity.x;
					if (time <= lowestTime) {
						impactY = yHit;
					}
				}
			}
			if (impactY > 0) {
				Puck block = computerPuck;
				int blockPosition = block.getRectangle().y;

				if (blockPosition < impactY) {
					block.move(Math.min(blockPosition + 40, impactY));
				} else {
					block.move(Math.max(blockPosition - 40, impactY));
				}
				repaint();
			}
			// computerPlayerScoreLabel.setText(String.valueOf(computerPlayerScore));
		}
	}

	@Override
	public void update(Graphics g) {
		memoryGraphics.drawImage(backgroundImage, 0, 0, this);

		humanGoal.drawImage(memoryGraphics);
		computerGoal.drawImage(memoryGraphics);

		for (int i = 0; i < pucks.size(); ++i) {
			if (!stop) {
				((Puck) pucks.elementAt(i)).drawImage(memoryGraphics);
			}
		}
		humanPuck.drawImage(memoryGraphics);
		computerPuck.drawImage(memoryGraphics);

		g.drawImage(memoryImage, getInsets().left, getInsets().top, this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == startGameMenu) {
			if (!stop) {
				stop = true;
				repaint();
			}
			init();
			humanPlayerScoreLabel.setVisible(true);
			computerPlayerScoreLabel.setVisible(true);
			stop = false;
			humanPlayerScoreLabel.setText("0");
			computerPlayerScoreLabel.setText("0");
			humanPlayerScore = 0;
			computerPlayerScore = 0;
		}

		if (e.getSource() == endGameMenu) {
			stop = true;
			humanPlayerScoreLabel.setText("0");
			computerPlayerScoreLabel.setText("0");
			humanPlayerScore = 0;
			computerPlayerScore = 0;
			repaint();
		}

		if (e.getSource() == setSpeedMenu) {
			setSpeedDialog.setVisible(true);
			if (!setSpeedDialog.getData().equals("")) {
				int newSpeed = Integer.parseInt(setSpeedDialog.getData());
				newSpeed = 101 - newSpeed;
				if (newSpeed >= 1 && newSpeed <= 100) {
					speed = newSpeed;
				}
			}
		}

		if (e.getSource() == exitGameMenu) {
			runOK = false;
			System.exit(0);
		}

		if (e.getSource() == howTo) {
			howToPlayPanel.setVisible(true);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Hockey();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (dragging) {
			int newY = e.getY() - offsetY;

			humanPuck.move(newY);
			repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		Rectangle r1 = humanPuck.getRectangle();
		if (r1.contains(new Point(e.getX(), e.getY() - getInsets().top))) {
			// offsetX = e.getX() - r1.x;
			offsetY = e.getY() - r1.y;
			dragging = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		dragging = false;
	}

}
