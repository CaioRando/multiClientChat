import java.net.Socket;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {
    private static final Logger log = Logger.getLogger(Server.class.getName());
    private Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        log.info("Client connected: " + clientSocket.getRemoteSocketAddress());
    }

}
