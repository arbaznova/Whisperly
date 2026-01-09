package multithreadedserver.Main;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final SSLServerSocket serverSocket;
    private final ExecutorService executor;

    private static final int PORT = 7777;
    private static final int MAX_THREADS = 20;

    // ---- TLS CONFIG ----
    private static final String KEYSTORE_PATH = "security/server-keystore.p12";
    private static final String KEYSTORE_PASSWORD = "Arbaz@123";

    public Server() throws Exception {
        this.serverSocket = createSSLServerSocket();
        this.executor = Executors.newFixedThreadPool(MAX_THREADS);
    }

    private SSLServerSocket createSSLServerSocket() throws Exception {

        // 1️⃣ Load server keystore
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(
                new FileInputStream(KEYSTORE_PATH),
                KEYSTORE_PASSWORD.toCharArray()
        );

        // 2️⃣ KeyManager (server identity)
        KeyManagerFactory kmf =
                KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, KEYSTORE_PASSWORD.toCharArray());

        // 3️⃣ TLS context
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), null, null);

        // 4️⃣ Create SSL server socket
        SSLServerSocketFactory factory = sslContext.getServerSocketFactory();
        SSLServerSocket sslServerSocket =
                (SSLServerSocket) factory.createServerSocket(PORT);

        sslServerSocket.setNeedClientAuth(false); // mutual TLS later if needed
        sslServerSocket.setEnabledProtocols(new String[]{"TLSv1.3"});

        System.out.println("TLS server started on port " + PORT);
        return sslServerSocket;
    }

    public void start() {
        System.out.println("Server started on port " + serverSocket.getLocalPort());

        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept(); // TLS handshake happens HERE
                System.out.println("Secure client connected: " +
                        socket.getRemoteSocketAddress());

                executor.execute(new ClientHandler(socket));

            } catch (IOException e) {
                if (!serverSocket.isClosed()) {
                    e.printStackTrace();
                }
            }
        }
        shutdown();
    }

    private void shutdown() {
        executor.shutdown();
        try {
            serverSocket.close();
        } catch (IOException ignored) {}
    }

    public static void main(String[] args) {
        try {
            new Server().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
