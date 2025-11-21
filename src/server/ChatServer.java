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
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static void main(String[] args) {
        printBanner();
        log("Chat Server initialized and listening on port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);

                // Create and start dedicated thread with meaningful name
                Thread clientThread = new Thread(clientHandler);
                String threadName = "ClientThread-" + clientSocket.getPort() + "-" + clientHandler.getUsername();
                clientThread.setName(threadName);
                clientThread.start();

                clients.add(clientHandler);

                log("New client connected → " +
                    "IP: " + clientSocket.getInetAddress().getHostAddress() +
                    " | Port: " + clientSocket.getPort() +
                    " | Thread: " + Colors.BOLD + threadName + Colors.RESET +
                    " | Active: " + clients.size());
            }
        } catch (Exception e) {
            log("FATAL SERVER ERROR: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
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
        String threadName = Thread.currentThread().getName();
        if (client != null && client.getSocket() != null) {
            threadName = "ClientThread-" + client.getSocket().getPort() + "-" + client.getUsername();
        }

        clients.remove(client);
        log("Client disconnected → " +
            "Username: " + (client != null ? client.getUsername() : "Unknown") +
            " | Thread: " + Colors.BOLD + threadName + Colors.RESET +
            " | Active clients: " + clients.size());
    }

    // Enhanced log method with timestamp and nice formatting
    public static void log(String msg) {
        String timestamp = SDF.format(new Date());
        String threadInfo = Thread.currentThread().getName();

        System.out.println(Colors.YELLOW + "╭─[SERVER]────────────────────────────────────╮" + Colors.RESET);
        System.out.println(Colors.YELLOW + "│ " + Colors.CYAN + timestamp + Colors.YELLOW + " │ " + 
                         Colors.BOLD + "Thread:" + threadInfo + Colors.RESET);
        System.out.println(Colors.YELLOW + "├─ " + Colors.RESET + msg);
        System.out.println(Colors.YELLOW + "╰─────────────────────────────────────────────╯\n" + Colors.RESET);
    }

    private static void printBanner() {
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
    }
}