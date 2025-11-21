package server;

import common.Colors;

import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ChatServer {
    private static final int PORT = 5000;
    private static final List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        System.out.println(Colors.BOLD + Colors.CYAN +
            """
                  ╔══════════════════════════════════════════╗
                  ║    ██████╗ ██╗  ██╗ █████╗ ████████╗     ║
                  ║   ██╔════╝ ██║  ██║██╔══██╗╚══██╔══╝     ║
                  ║   ██║      ███████║███████║   ██║        ║
                  ║   ██║      ██╔══██║██╔══██║   ██║        ║
                  ║   ╚██████╔ ██║  ██║██║  ██║   ██║        ║
                  ║    ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝        ║
                  ╚══════════════════════════════════════════╝
                """ + Colors.RESET);
        System.out.println(Colors.BOLD + Colors.GREEN + "Chat Server Started on port " + PORT + Colors.RESET);
        System.out.println(Colors.YELLOW + "Waiting for clients...\n" + Colors.RESET);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();

                System.out.println(Colors.GREEN + "✓ New client connected: " + 
                    clientSocket.getInetAddress() + ":" + clientSocket.getPort() + Colors.RESET);
                System.out.println(Colors.CYAN + "Active clients: " + clients.size() + "\n" + Colors.RESET);
            }
        } catch (Exception e) {
            System.err.println(Colors.RED + "Server error: " + e.getMessage() + Colors.RESET);
        }
    }

    public static void broadcast(String message, ClientHandler exclude) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client != exclude) {
                    client.sendMessage(message);
                }
            }
        }
    }

    public static void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    public static void log(String msg) {
        String timestamp = SDF.format(new Date());
        System.out.println(Colors.YELLOW + "[LOG " + timestamp + "] " + Colors.RESET + msg);
    }
}