package client;

import common.Colors;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 5000;

    public static void main(String[] args) {
        System.out.println(Colors.BOLD + Colors.PURPLE +
            """
             ╔═══════════════════════════════════════╗
             ║     Welcome to Client Server Chat!    ║
             ╚═══════════════════════════════════════╝
            """ + Colors.RESET);

        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            // Thread to read messages from server
            new Thread(() -> {
                String message;
                try {
                    while ((message = in.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    System.out.println(Colors.RED + "Connection lost to server." + Colors.RESET);
                }
            }).start();

            System.out.println(Colors.CYAN + "Connected! Type your messages below (type /quit to exit)\n" + Colors.RESET);

            // Read user input and send to server
            while (true) {
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("/quit")) {
                    out.println("/quit");
                    break;
                }
                if (!input.trim().isEmpty()) {
                    out.println(input);
                }
            }

        } catch (IOException e) {
            System.out.println(Colors.RED + "Could not connect to server. Is it running?" + Colors.RESET);
        }

        System.out.println(Colors.YELLOW + "Goodbye! Thanks for chatting 👋" + Colors.RESET);
    }
}