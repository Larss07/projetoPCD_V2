import game.Cliente;
import game.Servidor;
import java.net.UnknownHostException;

public class ClientArrows {
    public static void main(String[] args) throws UnknownHostException {
        Cliente client = new Cliente("127.0.0.1", Servidor.PORTO, false);
        client.runClient();
    }
}
