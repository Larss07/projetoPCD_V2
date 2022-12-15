package environment;
import java.io.Serializable;

import game.Game;
import game.Player;

public class Cell implements Serializable {
	private Coordinate position;
	private Game game;
	private Player player = null;
	private boolean isBlocked;
	
	public Cell(Coordinate position,Game g) {
		super();
		this.position = position;
		this.game=g;
	}

	public Coordinate getPosition() {
		return position;
	}

	public void setBlocked() {
		isBlocked = true;
	}

	public boolean isBlocked() {
		return isBlocked;
	}

	public boolean isOcupied() {
		return player != null;
	}

	public Player getPlayer() {
		return player;
	}

	public synchronized void setPlayer(Player p) {
		this.player = p;
	}

	public synchronized void moveTo(Cell nextCell) { /* synchronized ou locks ou bloquear parte do código */
		if (nextCell.getPlayer() == null) { //se a proxima célula estiver vazia, avança e fica na próxima célula
			nextCell.setPlayer(this.player);
			this.setPlayer(null);
		}
		else if (isBlocked()) /* se a proxima célula tem um obstáculo, dorme e permanece na mesma célula */
			try {
				Thread.sleep(Game.MAX_WAITING_TIME_FOR_MOVE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		else if(nextCell.getPlayer().getCurrentStrength() > 0 && nextCell.getPlayer().getCurrentStrength() < 10) //temos de melhorar isto porque a célula deveria ficar blocked
			fightWith(nextCell.getPlayer());
	}

	private void fightWith(Player p) {
		System.out.println("LUTA ENTRE " + player.getCurrentStrength() + " e " + p.getCurrentStrength());

		if (player.getCurrentStrength() < p.getCurrentStrength() ||
			(player.getCurrentStrength() == p.getCurrentStrength() && Math.random() > 0.5)) {
			player.getCurrentCell().setBlocked();
			p.setCurrentStrength((byte)(player.getCurrentStrength() + p.getCurrentStrength()));
			player.setCurrentStrength((byte)0);
		}
		else { /* o player tem mais strength que o p */
			p.getCurrentCell().setBlocked();
			player.setCurrentStrength((byte)(player.getCurrentStrength() + p.getCurrentStrength()));
			p.setCurrentStrength((byte)0);
		}
		game.notifyChange();
	}
	
}
