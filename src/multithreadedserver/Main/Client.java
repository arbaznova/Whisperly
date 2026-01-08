package multithreadedserver.Main;
import multithreadedserver.readersandwriters.ConsoleReader;
import multithreadedserver.readersandwriters.SocketReader;
import multithreadedserver.readersandwriters.SocketWriter;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

    private final Socket socket;
    private final SocketReader reader;
    private final SocketWriter writer;

    public Client(String username, Socket socket) throws IOException {
        this.socket = socket;
        this.reader = new SocketReader(socket);
        this.writer = new SocketWriter(socket);

        // ---------- HANDSHAKE ----------
        writer.write(username);

        String response = reader.readLine();
        if (!"OK".equals(response)) {
            System.out.println(response);
            close();
            throw new IOException("Username rejected");
        }

        System.out.println("Connected to chat server!");
        System.out.println("Type /help for commands");
    }

    public void start() {
        listen();
        send();
    }

    private void send() {
        new Thread(() -> {
            try {
                String msg;
                while ((msg = ConsoleReader.readLine()) != null) {
                    writer.write(msg);
                    if (msg.equalsIgnoreCase("/quit")) {
                        close();
                        break;
                    }
                }
            } catch (IOException ignored) {}
        }).start();
    }

    private void listen() {
        new Thread(() -> {
            try {
                String msg;
                while ((msg = reader.readLine()) != null) {
                    System.out.println(msg);
                }
            } catch (IOException ignored) {}
        }).start();
    }

    private void close() {
        try {
            socket.close();
            reader.close();
            writer.close();
        } catch (IOException ignored) {}
    }

    public static void main(String[] args) throws IOException {
        String username = ConsoleReader.readLine("Enter username: ");
        Socket socket = new Socket("localhost", 7777);
        new Client(username, socket).start();
    }
}
















