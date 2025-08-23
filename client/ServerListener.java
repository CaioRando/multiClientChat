import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Logger;

public class ServerListener implements Runnable {
    private static final Logger log = Logger.getLogger(ServerListener.class.getName());
    private Socket socket;
    private Scanner streamScanner;


    public ServerListener(Socket socket) {
        this.socket = socket;

        try {
            this.streamScanner = new Scanner(socket.getInputStream());
        } catch(IOException e) {
            log.severe("Erro de comunicação.");
        }
    }

    @Override
    public void run() {
        while(streamScanner.hasNextLine()) {
            String message = streamScanner.nextLine();
            System.out.println(message);
        }
        log.info("Stream does not have a next line.");
    }
}
