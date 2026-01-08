package multithreadedserver;

import java.io.*;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    private Socket socket;
    BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            this.bufferedWriter= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader= new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = bufferedReader.readLine();
            clientHandlers.add(this);
            broadcastMessage("Server: "+ username + " has entered the chat!");
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
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

    public void broadcastMessage(String messageToSend){
        for(ClientHandler clientHandler: clientHandlers){
            try{
                if(!clientHandler.username.equals(username)){
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void removeClientHandler(){
        clientHandlers.remove(this);
        broadcastMessage("Server: " + username + " has left the chat");
    }
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        removeClientHandler();
        try{
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter == null) {
                bufferedWriter.close();
            }
            if (socket == null) {
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
























