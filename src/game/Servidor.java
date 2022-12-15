
package game;

import environment.Direction;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor extends Thread{

    private ServerSocket serverSocket;
    protected Game game;
    public static final int PORT = 2022;

    /**
     * Metodo construtor utilizado para Server sem GUI
     */
    public Servidor() {
        System.out.println("Server created!");
        this.game = new Game();
        try{
            serverSocket = new ServerSocket(PORT); // Throws IOException
        }catch (IOException e){
            System.out.println("Error connecting server... aborting!");
            System.exit(1);
        }
    }

    /**
     * Metodo construtor para Server com GUI, o Game cria o Server
     * @param game Game
     */
    public Servidor(Game game) {
        System.out.println("Server created!");
        this.game = game;
        try{
            serverSocket = new ServerSocket(PORT); // Throws IOException
        }catch (IOException e){
            System.out.println("Error connecting server... aborting!");
            System.exit(1);
        }
    }

    /**
     * Server tem de ser thread porque e criado pelo Game se queremos GUI
     */
    @Override
    public void run(){
        try{
            while (true){
                try {
                    waitForConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }finally {
            closeServer();
        }

    }

    /**
     * Cria um handler por conexao
     * @throws IOException
     */
    public void waitForConnection() throws IOException{
        System.out.println("Waiting for connection...");
        Socket connection = serverSocket.accept();
        ConnectionHandler connectionHandler = new ConnectionHandler(connection);
        connectionHandler.start();
    }

    private void closeServer(){
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Closed server!");
    }


    //Classe que trata das conexões dos vários clientes, é a thread que trata
    public class ConnectionHandler extends Thread{

        // O que é necessário para ligar um cliente
        private final Socket connection;
        private BufferedReader input;
        private ObjectOutputStream output;

        public ConnectionHandler(Socket connection){
            this.connection = connection;
        }

        @Override
        public void run(){
            try {
                getStreams();
                proccessStreams();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                closeConnection();
            }

        }

        private void getStreams() throws IOException{
            output = new ObjectOutputStream(connection.getOutputStream());
            input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        }

        /**
         * Metodo que processa a ligacao
         * @throws IOException
         * @throws InterruptedException
         */
        private void proccessStreams() throws IOException, InterruptedException {
            // Cria o jogador e inicializa a usa posicao
            PhoneyHumanPlayer player = new PhoneyHumanPlayer(15 , game, (byte) 5, game.CDL);

            System.out.println("Successful connection, starting proccessing...");
            while(true){
                sleep(Game.REFRESH_INTERVAL);
                GameStatus sendGameStatus = new GameStatus(game);
                output.writeObject(sendGameStatus);
                output.reset();
                //Jogo terminou, parar de enviar
                if(game.isGameOver()){
                    break;
                }
                if(input.ready() && player.getCurrentStrength() < 10){
                    String directionReceived = input.readLine();
                    System.out.println("DIRECTION RECEIVED !!! " + directionReceived);
                    player.move(Direction.valueOf(directionReceived));
                }
            }
        }

        private void closeConnection() {

            try{
                input.close();
            }catch (IOException e){
                e.printStackTrace();
            }
            try{
                output.close();
            }catch (IOException e) {
                throw new RuntimeException(e);
            }
            try{
                connection.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Closed connection with client!");
        }

    }


}

