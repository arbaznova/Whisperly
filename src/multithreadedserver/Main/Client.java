package multithreadedserver.Main;
import multithreadedserver.readersandwriters.ConsoleReader;
import multithreadedserver.readersandwriters.SocketReader;
import multithreadedserver.readersandwriters.SocketWriter;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
public class Client {

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
        try {
            //Loaded truststore
            KeyStore trustStore = KeyStore.getInstance("PKCS12");
            trustStore.load(
                    new FileInputStream(
                            "src/multithreadedserver/security/client/client-truststore.p12"
                    ),
                    "FC#123".toCharArray()
            );

            //TrustManagerFactory
            TrustManagerFactory tmf =
                    TrustManagerFactory.getInstance("SunX509");
            tmf.init(trustStore);

            //SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(
                    null,               // client has no private key
                    tmf.getTrustManagers(),
                    null
            );

            //secure socket
            SSLSocketFactory factory =
                    sslContext.getSocketFactory();
            SSLSocket socket =
                    (SSLSocket) factory.createSocket("localhost", 7777);

            // Optional: force handshake now
            socket.startHandshake();
            System.out.println("TLS handshake successful. Secure channel established.");
            String username = ConsoleReader.readLine("Enter username: ");
            new Client(username, socket).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}