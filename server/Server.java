    import java.io.IOException;
    import java.net.ServerSocket;
    import java.time.LocalDateTime;
    import java.time.format.DateTimeFormatter;
    import java.util.ArrayList;
    import java.util.Collections;
    import java.util.List;
    import java.util.concurrent.Executors;
    import java.util.concurrent.ScheduledExecutorService;
    import java.util.concurrent.TimeUnit;
    import java.util.logging.Logger;

    public class Server {
        private static final Logger log = Logger.getLogger(Server.class.getName());
        public static List<ClientHandler> clientHandlers = Collections.synchronizedList(new ArrayList<>()); //lista de todos os clientes logados

        public static void broadcastMessage(String message) { //mandar a mesma mensagem  para todos os clientes conectados
            log.info("Broadcasting: " + message);
            for(ClientHandler handler : clientHandlers) {
                handler.sendMessage(message);
            }
        }

        //quando mandar a mensagem vai para todos os clientes menos para quem mandou, para não repetir a mensagem alem do "voce mandou:".....
        public static void broadcastMessageExcept(String message, ClientHandler exclude) {
        //log.info("Broadcasting (except sender): " + message);
        // percorre todos os clientes conectados e envia para todos, menos o remetente
        for (ClientHandler handler : clientHandlers) { //vai guardar os clientes que esta na lista clienthandles na variavel handler para comparar
            if (handler != exclude) { //se for diferente de quem enviou envia a mensagem normal
                handler.sendMessage(message);
            }
        }
    }

        public static void main(String[] args) {
            final int PORT = 12345; // Server PORT

            try(ServerSocket server = new ServerSocket(PORT)) { //cria um socket do servidor
                log.info("Server listening in port " + PORT);

                ScheduledExecutorService schedule = Executors.newSingleThreadScheduledExecutor(); //roda tarefas de tempos em tempos, cria uma thread dedicada somente para executar tarefas agendadas
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"); //formata data e hora

                schedule.scheduleAtFixedRate(() -> {
                    String localTime = LocalDateTime.now().format(timeFormatter);
                    broadcastMessage("Data:" + localTime);
                }, 0,1, TimeUnit.MINUTES); //em um intervalor de 1 minuto vai mandar





                while(true) {
                    ClientHandler client =  new ClientHandler(server.accept());
                    clientHandlers.add(client);
                    new Thread(client).start();
                }
            } catch(IOException e) {
                log.severe("Error starting server in port " + PORT);
            }
        }
    }
