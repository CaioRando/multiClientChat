import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Logger;

public class ServerListener implements Runnable {  //classe aux do cliente     ;;permite rodar em Thread
    private static final Logger log = Logger.getLogger(ServerListener.class.getName());
    private Socket socket;
    private Scanner streamScanner;  //leitor das linhas que vção vir  do servidor


    public ServerListener(Socket socket) {  //recebe o socket conectado e guarda
        this.socket = socket;

        try {
            this.streamScanner = new Scanner(socket.getInputStream()); //pega canal de entrada, tudo que o servirdor manda
        } catch(IOException e) {
            log.severe("Erro de comunicação.");
        }
    }

    @Override
    public void run() {
        while(streamScanner.hasNextLine()) {  //fica bloqueado até chegar os dados
            String message = streamScanner.nextLine();
            System.out.println(message);
        }
        log.info("Stream does not have a next line.");
    }
}
