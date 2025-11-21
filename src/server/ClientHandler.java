package server;

import common.Colors;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;
    private String username;
    private static final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm:ss");

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Ask for username
        out.println(Colors.CYAN + "Welcome to the chat! Please enter your name:" + Colors.RESET);
        try {
            username = in.readLine();
            if (username == null || username.trim().isEmpty()) {
                username = "Guest" + socket.getPort();
            }
            username = username.trim();
        } catch (IOException e) {
            username = "Unknown";
        }

        String joinMsg = Colors.GREEN + "➜ " + username + " joined the chat!" + Colors.RESET;
        ChatServer.broadcast(joinMsg, this);
        log("User " + username + " connected from " + socket.getInetAddress());
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                if (message.trim().equalsIgnoreCase("/quit")) {
                    break;
                }

                String timestamp = SDF.format(new Date());
                String formattedMsg = Colors.BLUE + "[" + timestamp + "] " +
                        Colors.BOLD + Colors.PURPLE + username + ": " +
                        Colors.RESET + message;

                ChatServer.broadcast(formattedMsg, this);
                log("[" + timestamp + "] " + username + ": " + message);
            }
        } catch (IOException e) {
            // Client disconnected unexpectedly
        } finally {
            cleanup();
        }
    }

    private void cleanup() {
        String leaveMsg = Colors.YELLOW + "➜ " + username + " left the chat." + Colors.RESET;
        ChatServer.broadcast(leaveMsg, this);
        log(username + " disconnected.");
        ChatServer.removeClient(this);
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    private void log(String msg) {
        String timestamp = SDF.format(new Date());
        System.out.println(Colors.YELLOW + "[SERVER LOG " + timestamp + "] " + Colors.RESET + msg);
    }

    public Socket getSocket() {
        return socket;
    }

    public String getUsername() {
        return username;
    }
}