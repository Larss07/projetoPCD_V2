package game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import environment.Coordinate;

public class GameStatus implements Serializable {

	public final static int DIMY = Game.DIMY;
	public final static int DIMX = Game.DIMX;
	public Coordinate[] coordinatesOfPlayers;
	public int[] strength;
	public boolean[] isPhoneyHumanPlayer;
	public int[] id;
	public boolean isGameOver;

	public GameStatus(Game game) {
		this.isGameOver = game.isGameOver();

		List<Player> players = game.getPlayers();


		coordinatesOfPlayers = new Coordinate[players.size()];
		strength = new int[players.size()];
		isPhoneyHumanPlayer = new boolean[players.size()];
		id = new int[players.size()];

		for (int i = 0; i < players.size() && players.get(i).getCurrentCell() != null; i++) {
			coordinatesOfPlayers[i] = players.get(i).getCurrentCell().getPosition();
			strength[i] = players.get(i).getCurrentStrength();
			isPhoneyHumanPlayer[i] = players.get(i).isHumanPlayer();
			id[i] = players.get(i).getIdentification();
		}
	}
}