package multithreadedserver.readersandwriters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class SocketReader {

    private final BufferedReader reader;

    public SocketReader(Socket socket) throws IOException {
        this.reader =
                new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
    }

    public String readLine() throws IOException {
        return reader.readLine(); // blocks
    }

    public void close() {
        try {
            reader.close();
        } catch (IOException ignored) {}
    }
}
