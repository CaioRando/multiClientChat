import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class Server {
    private static final Logger log = Logger.getLogger(Server.class.getName());
    public static List<ClientHandler> clientHandlers = Collections.synchronizedList(new ArrayList<>());

    public static void broadcastMessage(String message) {
        log.info("Broadcasting: " + message);
        for(ClientHandler handler : clientHandlers) {
            handler.sendMessage(message);
        }
    }

    public static void main(String[] args) {
        final int PORT = 12345; // Server PORT

        try(ServerSocket server = new ServerSocket(PORT)) {
            log.info("Server listening in port " + PORT);
            while(true) {
                ClientHandler client =  new ClientHandler(server.accept());
                clientHandlers.add(client);
                new Thread(client).start();
            }
        } catch(IOException e) {
            log.severe("Error starting server in port " + PORT);
        }
    }
}
