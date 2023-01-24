package game;
import java.io.Serializable;

import environment.Cell;

/**
 * Represents a player.
 * @author luismota
 *
 */
public abstract class Player implements Serializable {
	protected Game game;
	private int id;
	private byte currentStrength;
	protected byte originalStrength;
	public Cell cell;

	// It is already getting the player position from data in game!
	public Cell getCurrentCell() {
		return game.findMe(this);
	}

	public Player(int id, Game game, byte strength) {
		super();
		this.id = id;
		this.game = game;
		currentStrength = strength;
		originalStrength = strength;
	}

	public abstract boolean isHumanPlayer();

	/* Faz a alteração de energia e ainda dá update ao CDL */
	public void setCurrentStrength(byte strength) {
		currentStrength = (byte)(Math.min(strength, 10)); //Não é necessário ser o mínimo, mas contamos com 10 sendo a energia máxima que o player pode ter
		if(currentStrength == 10) {
			game.cdl.countDown();
			System.out.println("COUNTDOWNLATCH ----------" + game.cdl.getCount());
		}
	}

	@Override
	public String toString() {
		return "player [id=" + id + ", currentStrength=" + currentStrength + ", getCurrentCell()=" + getCurrentCell() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Player other = (Player) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public byte getCurrentStrength() {
		return currentStrength;
	}

	public int getIdentification() {
		return id;
	}
}
