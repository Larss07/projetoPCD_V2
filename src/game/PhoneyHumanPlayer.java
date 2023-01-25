package game;
import environment.*;

/**
 * Class to demonstrate a player being added to the game.
 * @author luismota
 *
 */
public class PhoneyHumanPlayer extends Player {
	
	public PhoneyHumanPlayer(int id, Game game, byte strength) {
		super(id, game, strength);
        game.addPlayerToGame(this);
	}

	public boolean isHumanPlayer() {
		return true;
	}

	public void move(Direction d) {
        if(getCurrentStrength() > 0 && getCurrentStrength() < 10) {
            Cell currcell = super.getCurrentCell(); /* vai buscar a celula atual */
            Coordinate position = currcell.getPosition(); /* vai buscar a posição atual */
            Coordinate newPosition = position.translate(d.getVector()); /* cria a nova posição com base na direção recebida */

            if (newPosition.x >= 0 && newPosition.x < Game.DIMX && newPosition.y >= 0 && newPosition.y < Game.DIMY) { /* se está dentro do board */
                currcell.moveTo(game.getCell(newPosition));
                System.out.println(getIdentification() + ": " + newPosition + "nova posição");
            }
            game.notifyChange();
        }
    }
}
