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
            Server.broadcastMessage("----    " + clientName + " conectado ao chat    ----");
            while(streamScanner.hasNextLine()) {
                String message = streamScanner.nextLine();
                if (message.startsWith("/name ")) {
                    String newName = message.substring(6).trim();
                    if (!newName.isEmpty()) {
                        Server.broadcastMessage("---- " + clientName + " mudou o nickname para " + newName + " ----");
                        clientName = newName;
                    }
                    continue;
                }

                String formattedMessage = "[" + clientName + "]: " + message;
                Server.broadcastMessage(formattedMessage);
            }
        } finally {
            log.info("Usuário " + clientName + " desconectado.");
            Server.broadcastMessage("----    " + clientName + " desconectado do chat.    ----");
            Server.clientHandlers.remove(this);
        }
    }

}
