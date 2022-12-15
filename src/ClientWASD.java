import game.Cliente;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ClientWASD {
    public static void main(String[] args) throws UnknownHostException {
        Cliente client = new Cliente(InetAddress.getByName("localHost"), 2022,
                65, 68, 87, 83);
        client.runClient();
    }

}
