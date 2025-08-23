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

    private static String getUserData(Scanner userInput, Socket socket) {
        String username = null;
        while(true) {
            System.out.printf("Nome de usuário: ");
            username = userInput.nextLine().trim();

            if(username.isEmpty()) {
                username = socket.getLocalAddress().getHostAddress() + ":" + socket.getLocalPort();
                break;
            }

            if(isValidUsername(username)) {
                break;
            } else {
                System.out.println("O nome de usuário deve ter entre 3 e 16 caracteres válidos.");
            }
        }
        return username;
    }
    public static void main(String[] args) {
        final String SERVER_ADDRESS = "127.0.0.1";
        final int PORT = 12345;
        Scanner userInput = new Scanner(System.in);

        try(Socket socket = new Socket(SERVER_ADDRESS, PORT)) {
            log.info("Conectou ao servidor " + SERVER_ADDRESS + " PORTA " + PORT);

            final String username = getUserData(userInput, socket);

            ServerListener serverListener = new ServerListener(socket);            
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            new Thread(serverListener).start();

            writer.println(username);
            while(userInput.hasNextLine()){
                String message = userInput.nextLine();
                if (message.startsWith(":")) {
                    if (message.equals(":quit")) {
                        writer.println(username + " saiu do chat.");
                        break;
                    } else if (message.startsWith(":name ")) {
                        String newName = message.substring(6).trim();
                        writer.println("/name " + newName);
                    } else {
                        System.out.println("Comando inexistente.");
                        continue;
                    }
                    continue;
                }
                writer.println(message);
            }
            userInput.close();
        } catch(IOException e) {
            log.severe("Não foi possível conectar ao servidor.");
        } finally {
            log.info("Desconectado.");
        }
    }
}