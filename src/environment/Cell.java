package environment;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import game.Game;
import game.Player;

public class Cell {
	private Coordinate position;
	private Game game;
	private Player player = null; //células começam vazias
	private Lock lock = new ReentrantLock(); //lock da célula
	private Condition isEmpty = lock.newCondition(); //condição do lock - se a célula está vazia

	//Construtor
	public Cell(Coordinate position,Game g) {
		super();
		this.position = position;
		this.game = g;
	}

	//Retorna a posição associada a esta célula
	public Coordinate getPosition() {
		return position;
	}

	//Verifica que a célula está bloqueada - se tem um obstáculo sendo ele um player morto ou um vencedor
	public boolean isBlocked() { 
		return (player != null && (player.getCurrentStrength() == 0 || player.getCurrentStrength() == 10));
	}

	//Verifica se a célula está ocupada - se tem um player
	public boolean isOccupied() {
		return player != null;
	}

	//Retorna o player que está de momento na célula
	public Player getPlayer() {
		return player;
	}

	//Colocação dos players - seja inicial, seja ao longo do jogo
	public void setPlayer(Player p) throws InterruptedException {
		lock.lock();
		try {
			//só entra neste if e no while no inicio do jogo!! (porque depois só chega a esta função se entrar na 1ª condição do moveTo!)
			if(isBlocked()) {
				System.out.println("Eu, Player " + p.getIdentification() + " estou à espera de uma célula bloqueada xxxxxxxxxxxxxxxxxxxxxxx");
				game.getRandomCell().setPlayer(p);
				return;
			}
			while(isOccupied()) {
				System.out.println("ESTOU PARADO À ESPERA - Player " + p.getIdentification());
				isEmpty.await();
			}
			this.player = p;
			game.notifyChange();
		}
		finally {
			lock.unlock();
		}
	}

	//Função que remove o player da célula onde esteve na anterior jogada - evitando "deixar rasto"!
	public synchronized void removePlayer() throws InterruptedException {
		this.player = null;
		isEmpty.signalAll();
	}

	//Função chave da classe - é aqui que tudo acontece
	public void moveTo(Cell nextCell) {
		lock.lock();
		nextCell.lock.lock();
		try {
			// 1º possibilidade: movimento normal sem confrontos
			if (nextCell.getPlayer() == null) {
				try {
					nextCell.setPlayer(this.player);
					removePlayer();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			// 2ª possibilidade: player tenta mover-se para cima de um obstáculo
			else if (isBlocked())
				try {
					Thread.sleep(Game.MAX_WAITING_TIME_FOR_MOVE);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			// 3ª possibilidade: player confronta outro player vivo
			else if(!nextCell.isBlocked() && nextCell.isOccupied())
				fightWith(nextCell.getPlayer());
		}
		finally {
			lock.unlock();
			nextCell.lock.unlock();
		}
	}

	//Luta entre player caso haja confronto - aqui alteram-se as strenghts
	private void fightWith(Player p) {
		System.out.println("LUTA ENTRE " + player.getCurrentStrength() + " e " + p.getCurrentStrength());

		if (player.getCurrentStrength() < p.getCurrentStrength() ||
		(player.getCurrentStrength() == p.getCurrentStrength() && Math.random() > 0.5)) {
			p.setCurrentStrength((byte)(player.getCurrentStrength() + p.getCurrentStrength()));
			player.setCurrentStrength((byte)0);
		}
		
		else { // se o player atualmente na célula tem mais strength que o que procura mover-se para cá
			player.setCurrentStrength((byte)(player.getCurrentStrength() + p.getCurrentStrength()));
			p.setCurrentStrength((byte)0);
		}
		game.notifyChange();
	}	
}

