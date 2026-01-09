package multithreadedserver.Main;

import multithreadedserver.readersandwriters.SocketReader;
import multithreadedserver.readersandwriters.SocketWriter;

import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private SocketReader reader;
    private SocketWriter writer;
    private String username;

    private final Semaphore connectionLimiter;

    public ClientHandler(Socket socket, Semaphore limiter) {
        this.socket = socket;
        this.connectionLimiter = limiter;
    }

    @Override
    public void run() {
        try {
            reader = new SocketReader(socket);
            writer = new SocketWriter(socket);

            // ---------- HANDSHAKE ----------
            username = reader.readLine();

            if (!ClientRegistry.isValidUsername(username)) {
                writer.write("Server: Username invalid or already taken");
                cleanup();
                return;
            }

            ClientRegistry.add(username, writer);
            writer.write("OK");

            // ---------- MESSAGE LOOP ----------
            String input;
            while ((input = reader.readLine()) != null) {

                if (input.equalsIgnoreCase("/help")) {
                    writer.write("""
                            Commands:
                            /users       - list online users
                            /quit        - quit chat
                            @user msg    - private message
                            message      - public message
                            """);
                    continue;
                }

                if (input.equalsIgnoreCase("/users")) {
                    writer.write(ClientRegistry.listUsers());
                    continue;
                }

                if (input.equalsIgnoreCase("/quit")) {
                    writer.write("Server: Disconnected");
                    break;
                }

                if (input.startsWith("@")) {
                    handlePrivate(input);
                    continue;
                }

                // public message
                ClientRegistry.broadcast(username + ": " + input, username);
            }

        } catch (IOException ignored) {
        } finally {
            cleanup();
        }
    }

    private void handlePrivate(String input) throws IOException {
        int space = input.indexOf(" ");
        if (space == -1) {
            writer.write("Server: Invalid private message");
            return;
        }

        String target = input.substring(1, space);
        String msg = input.substring(space + 1);

        boolean sent = ClientRegistry.sendPrivate(username, target, msg);
        if (!sent) {
            writer.write("Server: User not found");
        }
    }

    private void cleanup() {
        if (username != null) {
            ClientRegistry.remove(username);
        }
        try {
            socket.close();
            reader.close();
            writer.close();
        } catch (IOException ignored) {}
        finally {
            //RELEASING CONNECTION SLOT
            connectionLimiter.release();
            System.out.println("Connection released. Active connections: "
                    + (Server.MAX_CONNECTIONS - connectionLimiter.availablePermits()));
        }
    }
}

