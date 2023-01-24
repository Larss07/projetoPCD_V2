package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import environment.Coordinate;
import environment.Direction;
import game.ClienteGame;
import game.GameStatus;

public class ClientBoardJComponent extends JComponent implements KeyListener {

	private Image obstacleImage = new ImageIcon("obstacle.png").getImage();
	private Image humanPlayerImage = new ImageIcon("abstract-user-flat.png").getImage();
	private Direction lastPressedDirection = null;
	private final boolean alternativeKeys;
	private ClienteGame game;

	public ClientBoardJComponent(ClienteGame game, boolean alternativeKeys) {
		this.game = game;
		this.alternativeKeys = alternativeKeys;
		setFocusable(true);
		addKeyListener(this);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		double cellHeight = getHeight() / (double) GameStatus.DIMY;
		double cellWidth = getWidth() / (double) GameStatus.DIMX;

		for (int y = 1; y < GameStatus.DIMY; y++) {
			g.drawLine(0, (int) (y * cellHeight), getWidth(), (int) (y * cellHeight));
		}
		for (int x = 1; x < GameStatus.DIMX; x++) {
			g.drawLine((int) (x * cellWidth), 0, (int) (x * cellWidth), getHeight());
		}

		GameStatus message = game.getMessage();

		if (message == null)
			return;

		for (int i = 0; i < message.coordinatesOfPlayers.length; i++) {
			Coordinate p = message.coordinatesOfPlayers[i];

			if (p == null)
				// player that has not yet been placed
				continue;

			if (message.strength[i] == 0) {
				g.setColor(Color.YELLOW);
				g.fillRect((int) (p.x * cellWidth), (int) (p.y * cellHeight), (int) (cellWidth), (int) (cellHeight));
				g.drawImage(obstacleImage, (int) (p.x * cellWidth), (int) (p.y * cellHeight), (int) (cellWidth),
						(int) (cellHeight), null);
				// if player is dead, don'd draw anything else?
				continue;
			}

			if (message.isPhoneyHumanPlayer[i]) {
				g.setColor(Color.GREEN);
				g.fillRect((int) (p.x * cellWidth), (int) (p.y * cellHeight), (int) (cellWidth), (int) (cellHeight));
				// Custom icon?
				g.drawImage(humanPlayerImage, (int) (p.x * cellWidth), (int) (p.y * cellHeight), (int) (cellWidth),
						(int) (cellHeight), null);
			}
			g.setColor(new Color(message.id[i] * 1000));
			((Graphics2D) g).setStroke(new BasicStroke(5));
			Font font = g.getFont().deriveFont((float) cellHeight);
			g.setFont(font);
			String strengthMarking = (message.strength[i] >= 10 ? "X" : "" + message.strength[i]);
			g.drawString(strengthMarking, (int) ((p.x + .2) * cellWidth), (int) ((p.y + .9) * cellHeight));
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (alternativeKeys) {
			switch (e.getKeyCode()) {
				case KeyEvent.VK_A:
					lastPressedDirection = environment.Direction.LEFT;
					break;
				case KeyEvent.VK_D:
					lastPressedDirection = environment.Direction.RIGHT;
					break;
				case KeyEvent.VK_W:
					lastPressedDirection = environment.Direction.UP;
					break;
				case KeyEvent.VK_S:
					lastPressedDirection = environment.Direction.DOWN;
					break;
			}
		} else {
			switch (e.getKeyCode()) {
				case KeyEvent.VK_LEFT:
					lastPressedDirection = environment.Direction.LEFT;
					break;
				case KeyEvent.VK_RIGHT:
					lastPressedDirection = environment.Direction.RIGHT;
					break;
				case KeyEvent.VK_UP:
					lastPressedDirection = environment.Direction.UP;
					break;
				case KeyEvent.VK_DOWN:
					lastPressedDirection = environment.Direction.DOWN;
					break;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// ignore
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// Ignored...
	}

	public Direction getLastPressedDirection() {
		return lastPressedDirection;
	}

	public void clearLastPressedDirection() {
		lastPressedDirection = null;
	}

}