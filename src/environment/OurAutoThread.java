package environment;
import game.Game;

//Thread autónoma que permite que um player na célula cell que esteja bloqueado desbloqueie ao fim de 
public class OurAutoThread extends Thread {
    private Cell cell;

    public OurAutoThread (Cell c) {
        cell = c;
    }

    @Override
    public void run() {
        cell.lock.lock();
        try {
            sleep(Game.MAX_WAITING_TIME_FOR_MOVE);
            cell.canMove.signal();
        }
        catch(InterruptedException e) {
            interrupt();
        }
        finally {
            cell.lock.unlock();
        }
    }

}
