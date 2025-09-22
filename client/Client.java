import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
//import java.util.logging.Logger; //


//Programa do cliente
//pede um username e valida com regex
//abre um Socket para o servidor
//cria uma thread (ServerListener) para ouvir o servidor
//na thread principal, lê o teclado e envia cada linha ao servidor

public class Client {  //classe cleinte 
    
    private static String getUserData(Scanner userInput, Socket socket) { //Scanner classe para ler dados
        String username = null;
        while(true) { //solicita o nome e repete até passar na vilidaçãp
            System.out.printf("Nome de usuário: "); 
            username = userInput.nextLine().trim();

            if(username.isEmpty()) {
                username = socket.getLocalAddress().getHostAddress() + ":" + socket.getLocalPort();
                break;
            } else {
                break;
            }
        }
        return username;
    }
    public static void main(String[] args) {
        final String SERVER_ADDRESS = "127.0.0.1"; // localhost or change IP  ---Define a porta
        final int PORT = 12345;
        Scanner userInput = new Scanner(System.in);

        try(Socket socket = new Socket(SERVER_ADDRESS, PORT)) {
            System.out.println("Conectado ao servidor " + SERVER_ADDRESS + " PORTA " + PORT);
           //log.info("Conectou ao servidor " + SERVER_ADDRESS + " PORTA " + PORT);

            final String username = getUserData(userInput, socket);

            ServerListener serverListener = new ServerListener(socket);            
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            new Thread(serverListener).start();

            writer.println(username);
            while(userInput.hasNextLine()){  //loop que vai ler o teclado enquanto houver entrada
                String message = userInput.nextLine();
                if (message.startsWith(":")) {  //VERIFICA se a linha começa com :
                    if (message.equals(":quit")) { //se o comando for quit
                        writer.println(":quit"); //avisa o servidor que quer sair
                        break;
                    } else if (message.startsWith(":nome ")) {
                        String newName = message.substring(6).trim();
                        writer.println(":nome " + newName);
                    } else {
                        System.out.println("Comando inexistente.");
                        continue;

                //        writer.println(username + " saiu do chat.");
                //        break;
                //   } else if (message.startsWith(":name ")) {
                 //       String newName = message.substring(6).trim();
                 //       writer.println("/name " + newName);
                 //   } else {
                 //       System.out.println("Comando inexistente.");
                 //       continue;
                }
                    continue;
                }
                writer.println(message);
            }
            userInput.close();
        } catch(IOException e) {
            System.out.println("Não foi possível conectar ao servidor.");

        } finally {
            System.out.println("Desconectado.");
        }
    }
}