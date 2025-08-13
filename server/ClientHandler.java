import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {
    private static final Logger log = Logger.getLogger(ClientHandler.class.getName());
    private Socket clientSocket;
    private PrintWriter writer;
    private Scanner streamScanner;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            this.writer = new PrintWriter(clientSocket.getOutputStream(), true);
            this.streamScanner = new Scanner(clientSocket.getInputStream());
        } catch(IOException e) {
            log.severe("Connection error.");
        }
    }

    public void sendMessage(String message) {
        log.info("Sending: " + message);
        writer.println(message);
    }

    @Override
    public void run() {
        String clientName = "Unknown";
        try {
            log.info("Client connected: " + clientSocket.getRemoteSocketAddress());
            clientName = streamScanner.nextLine();
            Server.broadcastMessage("----    " + clientName + " connected to the chat    ----");
            while(streamScanner.hasNextLine()) {
                String message = streamScanner.nextLine();
                String formattedMessage = "[" + clientName + "]: " + message;
                Server.broadcastMessage(formattedMessage);
            }
        } finally {
            log.info("User " + clientName + " disconnected.");
            Server.broadcastMessage("----    " + clientName + " disconnected from the chat.    ----");
            Server.clientHandlers.remove(this);
        }
    }

}
