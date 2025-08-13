import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

public class Client {
    private static final Logger log = Logger.getLogger(Client.class.getName());

    public static void main(String[] args) {
        final String SERVER_ADDRESS = "127.0.0.1";
        final int PORT = 12345;

        try(Socket socket = new Socket(SERVER_ADDRESS, PORT)) {
            log.info("Connected to server " + SERVER_ADDRESS + " PORT " + PORT);
            while(true){
                log.info("Still connected...");
                Thread.sleep(5000);
            }
        } catch(IOException e) {
            log.severe("Not able to connect to the server");
        } catch (InterruptedException e) {
            log.severe("Interrupted thread");
        }
    }
}