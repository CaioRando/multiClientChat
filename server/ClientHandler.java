import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.logging.Logger;


public class ClientHandler implements Runnable { //classe pode ser rodada dentro de uma thread
    private static final Logger log = Logger.getLogger(ClientHandler.class.getName());
    private Socket clientSocket;
    private PrintWriter writer; //escreve dados pro cliente
    private Scanner streamScanner; //le dados do cliente

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;  //recebe o socket do cliente que acabou de se conectar
        try {
            this.writer = new PrintWriter(clientSocket.getOutputStream(), true); //cria para mandar pro cliente
            this.streamScanner = new Scanner(clientSocket.getInputStream()); //cria leitor de linhas pro cleinte
        } catch(IOException e) {
            log.severe("Connection error.");
        }
    }

    public void sendMessage(String message) {
        //log.info("Sending: " + message);
        writer.println(message);
    }

    @Override
    public void run() {
        String clientName = "Unknown";

        DateTimeFormatter entryTime = DateTimeFormatter.ofPattern("HH:mm:ss");


        try {
            log.info("Client connected: " + clientSocket.getRemoteSocketAddress());
            clientName = streamScanner.nextLine();

            String serverTime = LocalTime.now().format(entryTime);
            this.sendMessage(serverTime + ": CONECTADO!!");

            Server.broadcastMessage("----    " + clientName + " conectado ao chat    ----");


            while(streamScanner.hasNextLine()) {
                String message = streamScanner.nextLine();
               // System.out.println(".(asddfasda)");
                if (message.equalsIgnoreCase(":quit")) { //não é sensitive case
                 //   System.out.println(".(aaaaaaaaaaaa)");
                    sendMessage("OK: desconectando...");
                    break;
                }


                if (message.startsWith(":nome ")) {
                    String newName = message.substring(6).trim();
                    if (!newName.isEmpty()) {
                        Server.broadcastMessage("---- " + clientName + " mudou o nickname para " + newName + " ----");
                        clientName = newName;
                    }
                    continue;
                }

                log.info( clientName + ": " + message);

                this.sendMessage("Você digitou: " + message);

                String formattedMessage = clientName + " (" + LocalTime.now().format(entryTime) + "): " + message;
                Server.broadcastMessageExcept(formattedMessage, this); //this vai indicar o cliente que mandou a mensagem
            }
        } finally {
            //ordem de encerrar a conexão TCP associada a esse Socket
            try { clientSocket.close(); } catch (IOException ignored) {} //libera a porta e os recursos de rede daquela conexão
            log.info("Usuário " + clientName + " desconectado.");
            Server.broadcastMessage("----    " + clientName + " desconectado do chat.    ----");
            Server.clientHandlers.remove(this); //remove da lista
        }
    }
}
