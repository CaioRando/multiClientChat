import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client {
    private static final Logger log = Logger.getLogger(Client.class.getName());

    private static boolean isValidUsername(String username) {

        String regex = "^[A-Za-z0-9_][A-Za-z0-9_\s]{2,15}$";
        Pattern p = Pattern.compile(regex);

        if(username == null) {
            return false;
        }

        Matcher m = p.matcher(username);
        return m.matches();
    }

    private static String getUserData(Scanner userInput) {
        
        String username = null;
        while(true) {
            System.out.printf("Username: ");
            username = userInput.nextLine().trim();

            if(isValidUsername(username)) {
                break;
            } else {
                System.out.println("Username must have 3-16 valid characters.");
            }
        }

        return username;
    }
    public static void main(String[] args) {
        final String SERVER_ADDRESS = "127.0.0.1"; // localhost or change IP
        final int PORT = 12345;
        Scanner userInput = new Scanner(System.in);
        final String username = getUserData(userInput);

        try(Socket socket = new Socket(SERVER_ADDRESS, PORT)) {
            log.info("Connected to server " + SERVER_ADDRESS + " PORT " + PORT);

            ServerListener serverListener = new ServerListener(socket);            
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            new Thread(serverListener).start();
            
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