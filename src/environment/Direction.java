package environment;
import java.util.Random;

public enum Direction {
	UP(0,-1), DOWN(0,1), LEFT(-1,0), RIGHT(1,0);
	private Coordinate vector;
	public static final Random direction = new Random();

	Direction(int x, int y) {
		vector = new Coordinate(x, y);
	}
	
	public Coordinate getVector() {
		return vector;
	}
	
	// Gerar uma direção random para os jogadores!
	public static Direction randomDirection() {
		Direction [] directions = values();
		return directions[direction.nextInt(directions.length)];
	}

}
