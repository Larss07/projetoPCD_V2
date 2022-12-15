package game;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.CountDownLatch;

import environment.Cell;
import environment.Coordinate;

public class Game extends Observable implements Serializable {

	public static final int DIMY = 10;
	public static final int DIMX = 10;
	public static final int NUM_PLAYERS = 15;
	public static final int NUM_FINISHED_PLAYERS_TO_END_GAME=3;
	public static final long REFRESH_INTERVAL = 400;
	public static final double MAX_INITIAL_STRENGTH = 3;
	public static final long MAX_WAITING_TIME_FOR_MOVE = 2000;
	public static final long INITIAL_WAITING_TIME = 10000;
	public CountDownLatch CDL = new CountDownLatch(Game.NUM_FINISHED_PLAYERS_TO_END_GAME);
	private List<Player> players = Collections.synchronizedList(new ArrayList<Player>());
	private List<Thread> threads = Collections.synchronizedList(new ArrayList<Thread>());
	public Cell[][] board;
	private boolean gameIsOver = false;

	public Game() {
		board = new Cell[Game.DIMX][Game.DIMY];
	
		for (int x = 0; x < Game.DIMX; x++) 
			for (int y = 0; y < Game.DIMY; y++) 
				board[x][y] = new Cell(new Coordinate(x, y),this);

		//Adicionar autoplayers ao jogo
		addAutoPlayers();

		Thread gameOver = new Thread() {

			@Override 

			public void run() {
				try {
					CDL.await();
				}catch(InterruptedException e){
					e.printStackTrace();
				}
				// Matar as threads dos jogadores
				for(Thread p : threads){
					p.stop();
				}
				gameIsOver = true;
				//TODO O que fazer quando acabar
				System.err.println("GAME FINISHED!");
			};
		};
		
		gameOver.start();
		Servidor servidor = new Servidor(this);
		servidor.start();

	}

	public boolean isGameOver() {
		return gameIsOver;
	}

	public List<Player> getPlayers() {
		return players;
	}
	
	private void addAutoPlayers() {
		for (int i = 1; i <= Game.NUM_PLAYERS; i++) {
			AutoPlayer player = new AutoPlayer(i*10, this, (byte)((Math.random() * (Game.MAX_INITIAL_STRENGTH) + 1)), CDL);
			this.addPlayerToGame(player);
			Thread autoPlayerThread = new Thread(player);
			threads.add(autoPlayerThread);
			autoPlayerThread.start();
		} // quando uma das threads não arranja lugar, as outras são obrigadas a esperar por ela...
		
		// try {
		// 	CDL.await(); //aguarda até que o CDL chegue a zero para avançar
		// } catch (InterruptedException e) {
		// 	e.printStackTrace();
		// }
		// /* término do jogo */
		// for (Thread thread : threads) {
		// 	thread.stop();
		// }
	}

	/** 
	 * @param player 
	 */
	public void addPlayerToGame(Player player) {		
		Cell initialPos = getRandomCell();
		while(initialPos.isOcupied()) {
			if(initialPos.isBlocked()) {
				System.out.println("Estou a tentar entrar numa célula bloqueada");
				addPlayerToGame(player); /* recursividade! tenta escolher uma nova célula */
				players.add(player);
				return;
			}
			System.out.println(player.getIdentification() + ": À espera da célula (" + initialPos.getPosition().x + ", " + initialPos.getPosition().y + ")");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		initialPos.setPlayer(player);
		// To update GUI
		notifyChange();
	}

	public Cell getCell(Coordinate at) {
		return board[at.x][at.y];
	}

	/* função que encontra o player no game e devolve-lhe a sua célula atual */
	public Cell findMe(Player p) {
		for (int x = 0; x < Game.DIMX; x++) {
			for (int y = 0; y < Game.DIMY; y++) {
				if (board[x][y].getPlayer() == p)
					return board[x][y];
			}
		}
		return null; /* não deve acontecer */
	}

	/**	
	 * Updates GUI. Should be called anytime the game state changes
	 */
	public void notifyChange() {
		setChanged();
		notifyObservers();
	}

	public Cell getRandomCell() {
		Cell newCell = getCell(new Coordinate((int)(Math.random()*Game.DIMX),(int)(Math.random()*Game.DIMY)));
		return newCell; 
	}
}
