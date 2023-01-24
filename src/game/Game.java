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

	public static final int DIMY = 9;
	public static final int DIMX = 9;
	public static final int NUM_PLAYERS = 3;
	public static final int NUM_FINISHED_PLAYERS_TO_END_GAME = 3;
	public static final long REFRESH_INTERVAL = 1000;
	public static final double MAX_INITIAL_STRENGTH = 9;
	public static final long MAX_WAITING_TIME_FOR_MOVE = 2000;
	public static final long INITIAL_WAITING_TIME = 10000;
	public CountDownLatch cdl = new CountDownLatch(Game.NUM_FINISHED_PLAYERS_TO_END_GAME);
	private List<Player> players = Collections.synchronizedList(new ArrayList<Player>());
	private List<Thread> threads = Collections.synchronizedList(new ArrayList<Thread>());
	public Cell[][] board;
	private boolean isGameOver = false;

	// Construtor
	public Game() {
		board = new Cell[Game.DIMX][Game.DIMY];
		for (int x = 0; x < Game.DIMX; x++) 
			for (int y = 0; y < Game.DIMY; y++) 
				board[x][y] = new Cell(new Coordinate(x, y), this);

		// adicionar autoplayers ao jogo
		addAutoPlayers();

		//Thread Autónoma que termina o jogo quando o NUM_FINISHED_PLAYERS_TO_END_GAME é atingido
		Thread gameOver = new Thread() {
			@Override 
			public void run() {
				try {
					cdl.await(); // aguarda que o cdl atinja o limite 
				} catch(InterruptedException e){
					e.printStackTrace();
				}
				// interrompe as threads que correm os jogadores
				for(Thread p : threads){
					p.interrupt();
				}
				isGameOver = true;
				System.err.println("GAME FINISHED!");
			};
		};
		gameOver.start();

		Servidor servidor = new Servidor(this);
		servidor.start();
	}

	//Função que verifica se o jogo terminou
	public boolean isGameOver() {
		return isGameOver;
	}

	//Retorna a lista de players em jogo
	public List<Player> getPlayers() {
		return players;
	}
	
	//Cria os autoPlayers do jogo
	private void addAutoPlayers() {
		for (int i = 1; i <= Game.NUM_PLAYERS; i++) {
			AutoPlayer player = new AutoPlayer(i*10, this, (byte)((Math.random() * (Game.MAX_INITIAL_STRENGTH) + 1)));
			Thread autoPlayerThread = new Thread(player);
			threads.add(autoPlayerThread);
			autoPlayerThread.start();
		}
	}

	//Adiciona um player ao jogo - é invocado pelo próprio player!
	public void addPlayerToGame(Player player) {		
		try {
			System.out.println("Eu sou o player " + player.getIdentification() + " bbbbbbbbbbbbbbbbbbbbb");
			getRandomCell().setPlayer(player);
			players.add(player);
			notifyChange();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	//Retorna a célula correspondente à coordenada AT
	public Cell getCell(Coordinate at) {
		return board[at.x][at.y];
	}

	//Função que encontra o player no game e devolve-lhe a sua célula atual
	public Cell findMe(Player p) {
		for (int x = 0; x < Game.DIMX; x++) {
			for (int y = 0; y < Game.DIMY; y++) {
				if (p.equals(board[x][y].getPlayer()))
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

	//Retorna uma célula aleatória dentro do jogo
	public Cell getRandomCell() {
		Cell newCell = getCell(new Coordinate((int)(Math.random()*Game.DIMX),(int)(Math.random()*Game.DIMY)));
		return newCell; 
	}
}
