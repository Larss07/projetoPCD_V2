package game;
import java.io.Serializable;
import java.util.concurrent.CountDownLatch;

import environment.Cell;
import environment.Coordinate;
import environment.Direction;

public class AutoPlayer extends Player implements Runnable {

    public AutoPlayer(int id, Game game, byte strength, CountDownLatch cdl) {
        super(id, game, strength, cdl);
        System.out.println("Autoplayer nº " + id + ": " + strength + " power");
    }

    @Override
    public boolean isHumanPlayer() {
        return false;
    }

    public void move() {
        Cell currcell = super.getCurrentCell(); /* vai buscar a celula atual */
        Coordinate position = currcell.getPosition(); /* vai buscar a posição atual */
        Direction vector = Direction.randomDirection(); /* recebe uma direção aleatoria */
        Coordinate newPosition = position.translate(vector.getVector()); /* posição para a qual se quer mover */

        if (newPosition.x >= 0 && newPosition.x < Game.DIMX-1 && newPosition.y >= 0 && newPosition.y < Game.DIMY-1) { /* se esta estiver dentro dos limites do board */
            currcell.moveTo(game.getCell(newPosition));
            game.notifyChange();
        }
    }

    @Override
    public void run() {
        while (super.getCurrentStrength() < 10 && super.getCurrentStrength() > 0) {
            try {
                move();
                Thread.sleep(Game.REFRESH_INTERVAL * originalStrength); /* implementação dos ciclos diferentes para strengths iniciais diferentes */
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
