    import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
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
        //lista compartilhada com todos os clientes conectados

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
            int maxClients = 5; //vai vir por padrão caso n venha nenhum parametro
            if(args.length >=1){  //ve c foi passo pelo menos um parametro na linha de comando, se foi, verifica
                try{
                     maxClients = Integer.parseInt(args[0]); //converte para interiro
                if (maxClients < 1) { //caso venha um numero que n aceita
                    System.out.println("Valor inválido para maxClients");
                    maxClients = 1; //caso o valor seja invalido coloca o max como 1
                }
            } catch (NumberFormatException e){ //caso coloou abc, ou qualquer outra coisa ele vem para ca
                System.out.println("Parâmetro inválido: '" + args[0] + "'. Usando 5.");//nesse caso vai usar o 5 como padrão
                maxClients = 5;
            }
        }







            try(ServerSocket server = new ServerSocket(PORT)) { //cria um socket do servidor
                log.info("Server listening in port " + PORT + " | maxClients=" + maxClients);


                ScheduledExecutorService schedule = Executors.newSingleThreadScheduledExecutor(); //roda tarefas de tempos em tempos, cria uma thread dedicada somente para executar tarefas agendadas
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"); //formata data e hora

                schedule.scheduleAtFixedRate(() -> { //a cada um minuto manda horario
                    String localTime = LocalDateTime.now().format(timeFormatter);
                    broadcastMessage("Data:" + localTime);
                }, 0,1, TimeUnit.MINUTES); //em um intervalor de 1 minuto vai mandar




                while(true) {
                    Socket socket = server.accept(); //fica bloqueado no accept até chegar uma nova conexão, quanod chega retorna um socket

                    int current;
                    synchronized (clientHandlers) { //ve o numero atual de clientes conectados
                        current = clientHandlers.size(); //current recebe o numero de clientes conectdos
                    }

                    if (current >= maxClients) {//verifica se ja atingiu o numero max de clientes, se sim, recusa
                    try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) { //pega o canal que esta conectando esse cliente e manda a mesnagem para ele e envia somente para ele
                        String now = LocalDateTime.now().format(timeFormatter);
                        out.println("Limite de clientes atingidos(" + maxClients + "). Conexão recusada em " + now + ".");
                    } catch (IOException ignored) {}
                    try { socket.close(); } catch (IOException ignored) {}
                    continue; // volta a esperar a próxima conexão
                    }


                    ClientHandler client = new ClientHandler(socket); //cria um clienthandler para esse cliente
                    synchronized (clientHandlers) { //adiciona o cliente na lista
                        clientHandlers.add(client);
                    }
                    new Thread(client, "ClientHandler-" + socket.getRemoteSocketAddress()).start();
                }
            } catch(IOException e) {
                log.severe("Error starting server in port " + PORT);
            }
        }
    }
