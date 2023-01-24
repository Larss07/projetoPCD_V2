package game;

import java.util.Observable;

public class ClienteGame extends Observable {

	private GameStatus message;

	public ClienteGame(GameStatus message) {
		this.message = message;
	}

	public void setMessage(GameStatus message) {
		this.message = message;
	}

	public GameStatus getMessage() {
		return message;
	}

	public void notifyChange() {
		setChanged();
		notifyObservers();
	}

}