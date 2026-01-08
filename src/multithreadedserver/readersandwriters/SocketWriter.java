package multithreadedserver.readersandwriters;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class SocketWriter {

    private final BufferedWriter writer;

    public SocketWriter(Socket socket) throws IOException {
        this.writer =
                new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream()));
    }

    public synchronized void write(String message) throws IOException {
        writer.write(message);
        writer.newLine();
        writer.flush();
    }
