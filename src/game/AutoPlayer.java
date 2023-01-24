package game;
import environment.Cell;
import environment.Coordinate;
import environment.Direction;

public class AutoPlayer extends Player implements Runnable {

    //Construtor
    public AutoPlayer(int id, Game game, byte strength) {
        super(id, game, strength);
        System.out.println("Autoplayer nº " + id + ": " + strength + " power");
    }

    //Função que verifica se é human player - sempre false [vem do Player]
    @Override
    public boolean isHumanPlayer() {
        return false;
    }

    //Função de tentativa do player de se mover para uma nova célula
    public void move() {
        Cell currcell = super.getCurrentCell(); // vai buscar a célula atual
        Coordinate position = currcell.getPosition(); // vai buscar a posição atual da célula
        System.out.println("Posição atual " + position + " do " + this.toString());
        
        Direction vector = Direction.randomDirection(); // recebe uma direção aleatória por ser autoplayer
        Coordinate newPosition = position.translate(vector.getVector()); // posição para a qual se quer mover
        
        if (newPosition.x >= 0 && newPosition.x < Game.DIMX-1 &&
            newPosition.y >= 0 && newPosition.y < Game.DIMY-1) { // verificar limites do board
            
            currcell.moveTo(game.getCell(newPosition));
            game.notifyChange();
            System.out.println("Posição nova " + position + " do " + this.toString());
        }
    }

    //Método que corre o player [Runnable]
    @Override
    public void run() {
        try {
            game.addPlayerToGame(this); // é o player que se auto-adiciona ao jogo
            while (super.getCurrentStrength() < 10 && super.getCurrentStrength() > 0) { // enquanto está vivo
                move();
                Thread.sleep(Game.REFRESH_INTERVAL * originalStrength); // implementação dos ciclos diferentes para strengths iniciais diferentes
            }
        } catch (InterruptedException e) {}
    }
}
