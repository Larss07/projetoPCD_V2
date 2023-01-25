package gui;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;

import game.ClienteGame;

public class ClientGameGUI implements Observer {

	private JFrame frame = new JFrame("pcd.io");
	public ClientBoardJComponent boardGui;
	private boolean alternativeKeys;
	private ClienteGame clientGame;

	public ClientGameGUI(boolean alternativeKeys) {
		super();
		this.alternativeKeys = alternativeKeys;
		clientGame = new ClienteGame(null);
		clientGame.addObserver(this);

		buildGui();
	}

	private void buildGui() {
		boardGui = new ClientBoardJComponent(clientGame, alternativeKeys);
		frame.add(boardGui);

		frame.setSize(800, 800);
		frame.setLocation(0, 150);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	@Override
	public void update(Observable o, Object arg) {
		boardGui.repaint();
	}

	public void init() {
		frame.setVisible(true);
	}

	public ClienteGame getClientGame() {
		return clientGame;
	}

}
