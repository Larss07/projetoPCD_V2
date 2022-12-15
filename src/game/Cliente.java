package game;

import environment.Direction;
import gui.BoardJComponent;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

import javax.swing.JFrame;


public class Cliente{
    private final InetAddress address;
    private Socket socket;
    private ObjectInputStream input;
    private PrintWriter output;
    private final int PORT;
    private ClientGUI clientGUI;
    private final int LEFT;
    private final int RIGHT;
    private final int UP;
    private final int DOWN;

    //Construtor para GUI Clientes
    public Cliente(InetAddress address, int PORT, int LEFT, int RIGHT, int UP, int DOWN) {
        super();
        //TODO nao preciso do game
        //this.game = new Game();
        this.address = address;
        this.PORT = PORT;
        this.LEFT = LEFT;
        this.RIGHT = RIGHT;
        this.UP = UP;
        this.DOWN = DOWN;
        //TODO receber estado jogo do server
        //TODO iniciar GUI

    }

    public void runClient() {
        try {
            connectToServer();
            getStreams();
            firstConnection();
            proccessConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            closeConnection();
        }
    }

    public void connectToServer(){
        try {
            socket = new Socket(address, PORT);
            System.err.println("Client " + address + " CONNECTED TO SERVER!");
        } catch (IOException e) {
            System.err.println("Client " + address + " error connecting... exiting");
            System.exit(1);
        }
    }

    public void getStreams() throws IOException{
        //TODO Autoflush, quando escrevo algo, manda logo
        output = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream())),
                true);
        //output = new PrintWriter(socket.getOutputStream(), true);
        input = new ObjectInputStream(socket.getInputStream());

    }

    public void firstConnection() throws IOException{
        System.out.println("Client processing first connection...");
        try {
            //TODO receção
            GameStatus receivedGameStatus = (GameStatus) input.readObject();
            System.out.println("Recebi status");
            StringBuilder debug = new StringBuilder(receivedGameStatus.toString());
            System.out.println("Aqui aqui aqui!");
            for(Player p : receivedGameStatus.getPlayerList()){
                System.out.println("Entrei no ciclo for!s");
                debug.append(" ").append(p.getCurrentCell());
                System.out.println("Status player" + p);
            }
            System.out.println(debug);

            List<Player> playerList = receivedGameStatus.getPlayerList();
            clientGUI = new ClientGUI(playerList, LEFT, RIGHT, UP, DOWN);
            clientGUI.init();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public void proccessConnection() throws IOException {
        System.out.println("Client processing continuous connection...");
        Direction directionPressed;
        while(true){
            try {
                //TODO receção
                //Game receivedGame = (Game) input.readObject();
                GameStatus receivedGameStatus = (GameStatus) input.readObject();
                //System.out.println("UPDATING STATUS...");
                StringBuilder debug = new StringBuilder(receivedGameStatus.toString());
                for(Player p : receivedGameStatus.getPlayerList()){
                    debug.append(" ").append(p.getCurrentCell());
                }
                //System.out.println(debug);
                clientGUI.updateGameStatus(receivedGameStatus.getPlayerList());
                //TODO envio de direcao

                if(clientGUI.getBoardJComponent().getLastPressedDirection() != null) {
                    directionPressed = clientGUI.getBoardJComponent().getLastPressedDirection();
                    clientGUI.getBoardJComponent().clearLastPressedDirection();
                    //System.out.println("SENDING " + directionPressed.toString());
                    output.println(directionPressed);
                }

            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void closeConnection(){
        try{
            System.out.println("Closing connection...");
            input.close();
            output.close();
            socket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public class ClientGUI {
        private final JFrame frame;
        private final BoardJComponent boardGui;
    
        public ClientGUI(List<Player> playerList, int LEFT, int RIGHT, int UP, int DOWN) {
            super();
            frame = new JFrame("Cliente");
            boardGui = new BoardJComponent(playerList, LEFT, RIGHT, UP, DOWN);
            buildGui();
        }
    
        private void buildGui() {
            frame.add(boardGui);
            frame.setSize(800,800);
            frame.setLocation(0, 150);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
    
        public void updateGameStatus(List<Player> playerList){
            boardGui.setPlayerList(playerList);
            boardGui.repaint();
        }
    
        public BoardJComponent getBoardJComponent() {
            return boardGui;
        }
    
        public void init()  {
            frame.setVisible(true);
    
            // Demo players, should be deleted
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    
    
    }

}


