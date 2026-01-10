package multithreadedserver.Main;

import multithreadedserver.observability.Logger;
import multithreadedserver.observability.Metrics;
import multithreadedserver.observability.MetricsReporter;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class Server {

    private final SSLServerSocket serverSocket;
    private final ExecutorService executor;

    private static final int PORT = 7777;
    private static final int MAX_THREADS = 20;
    static final int MAX_CONNECTIONS = 50;

    private final Semaphore connectionLimiter = new Semaphore(MAX_CONNECTIONS);

    // ---- TLS CONFIG ----
    private static final String KEYSTORE_PATH = "src/multithreadedserver/security/server/server-keystore.p12";
    private static final String KEYSTORE_PASSWORD = "Arbaz@123";

    public Server() throws Exception {
        this.serverSocket = createSSLServerSocket();
        this.executor = Executors.newFixedThreadPool(MAX_THREADS);
    }

    private SSLServerSocket createSSLServerSocket() throws Exception {

        //Load server keystore
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

        Logger.info("Server",
                "TLS enabled | protocol=TLSv1.3 | maxThreads=" + MAX_THREADS +
                        " | maxConnections=" + MAX_CONNECTIONS
        );        return sslServerSocket;
    }

    public void start() {
        System.out.println("Server started on port " + serverSocket.getLocalPort());

        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept(); // TLS handshake happens HERE
                Metrics.totalConnections.incrementAndGet();
                Logger.info("Server",
                        "Incoming connection attempt from " + socket.getRemoteSocketAddress()
                );

                // CONNECTION LIMIT CHECK
                if (!connectionLimiter.tryAcquire()) {
                    Metrics.rejectedConnections.incrementAndGet();
                    Logger.warn("Server",
                            "Connection rejected (capacity reached). Active="
                                    + (MAX_CONNECTIONS - connectionLimiter.availablePermits())
                    );
                    socket.close();
                    continue;
                }
                Metrics.activeConnections.incrementAndGet();
                Logger.info("Server",
                        "Connection accepted. Active="
                                + (MAX_CONNECTIONS - connectionLimiter.availablePermits())
                );

                System.out.println("Secure client connected: " +
                        socket.getRemoteSocketAddress());

                executor.execute(
                        new ClientHandler(socket, connectionLimiter)
                );

            } catch (IOException e) {
                if (!serverSocket.isClosed()) {
                    e.printStackTrace();
                }
            }
        }
        shutdown();
    }

    private void shutdown() {
        Logger.info("Server", "Server shutting down gracefully");
        executor.shutdown();
        try {
            serverSocket.close();
        } catch (IOException ignored) {}
    }

    public static void main(String[] args) {
        try {
            new Thread(new MetricsReporter()).start();
            new Server().start();
            new Thread(new MetricsReporter()).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
