package gui;

import java.util.Observable;
import java.util.Observer;

import game.Game;

import javax.swing.JFrame;

public class GameGuiMain implements Observer {
	private JFrame frame;
	private BoardJComponent boardGui;
	private Game game;

	public GameGuiMain() {
		super();
		frame = new JFrame("pcd.io");
		game = new Game();
		game.addObserver(this);
		buildGui();
	}

	private void buildGui() {
		boardGui = new BoardJComponent(game);
		frame.add(boardGui);
		frame.setSize(800, 800);
		frame.setLocation(0, 150);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void init() {
		frame.setVisible(true);

		// Demo players, should be deleted
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		boardGui.repaint();
	}

}
