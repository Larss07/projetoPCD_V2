package game;

import environment.Direction;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Servidor extends Thread {

    protected Game game;
    public static final int PORTO = 8080;
    public static final int DELAY = 30;
    private int playerid;

    public Servidor(Game game) {
        this.game = game;
        playerid = 1;
    }

    /**
     * Server tem de ser thread porque e criado pelo Game se queremos GUI
     */
    @Override
    public void run() {
        ServerSocket ss;
        try {
            ss = new ServerSocket(PORTO);
            try {
                while (true) {
                    Socket socket = ss.accept();
                    System.out.println("Created server socket " + socket.toString());
                    new ConnectionHandler(socket).start();
                }
            } finally {
                ss.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Classe que trata das conexões dos vários clientes, é a thread que trata
    public class ConnectionHandler extends Thread {

        // O que é necessário para ligar um cliente
        private BufferedReader input;
        private ObjectOutputStream output;
        private PhoneyHumanPlayer phoneyHumanPlayer;

        public ConnectionHandler(Socket connection) throws IOException {
            doConnections(connection);
        }

        private void doConnections(Socket socket) throws IOException {
            System.out.println("Doing connections on socket " + socket.toString());
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Connections done");
        }

        @Override
        public void run() {
            try {
                serve();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void serve() throws IOException, EOFException {
            try {
                phoneyHumanPlayer = new PhoneyHumanPlayer(Game.NUM_PLAYERS + playerid++, game, (byte) 5);

                SendGameStateToPlayer s = new SendGameStateToPlayer();
                s.start();

                while (!game.isGameOver()) {
                    String direction;
                    direction = input.readLine();
                    if (direction != null) {
                        phoneyHumanPlayer.move(Direction.valueOf(direction));
                    }
                }
            } finally {
                input.close();
                output.close();
                System.out.println("Server/client connection stopped");
            }
        }

        public class SendGameStateToPlayer extends Thread {

            @Override
            public void run() {
                GameStatus message;
                while (!game.isGameOver()) {
                    try {
                        message = new GameStatus(game);
                        output.writeObject(message);

                        output.flush();
                        output.reset();
                        sleep(DELAY);
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }
                }
                // para enviar o estado quando o jogo acabou
                message = new GameStatus(game);
                try {
                    output.writeObject(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

}
