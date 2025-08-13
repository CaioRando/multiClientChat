import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Logger;

public class Client {
    private static final Logger log = Logger.getLogger(Client.class.getName());

    public static void main(String[] args) {
        final String SERVER_ADDRESS = "127.0.0.1"; // localhost or change IP
        final int PORT = 12345;

        try(Socket socket = new Socket(SERVER_ADDRESS, PORT)) {
            log.info("Connected to server " + SERVER_ADDRESS + " PORT " + PORT);

            ServerListener serverListener = new ServerListener(socket);
            new Thread(serverListener).start();
            Scanner userInput = new Scanner(System.in);
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            System.out.printf("Username: ");
            final String username = userInput.nextLine();
            writer.println(username);
            while(userInput.hasNextLine()){
                String message = userInput.nextLine();
                writer.println(message);
            }
            userInput.close();
        } catch(IOException e) {
            log.severe("Not able to connect to the server.");
        } finally {
            log.info(" disconnected.");
        }
    }
}