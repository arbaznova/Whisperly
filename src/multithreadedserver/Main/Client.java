package multithreadedserver;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

    public Client(String clientUsername, Socket socket) {
        try {
            this.clientUsername = clientUsername;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.socket = socket;
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
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
    public void listenForMessage(){
        new Thread(()->{
            try {
                String msg;
                while ((msg = reader.readLine()) != null) {
                    System.out.println(msg);
                }
            } catch (IOException ignored) {}
        }).start();
    }
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        try{
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        String username = ConsoleWriter.readLine("Enter your username: ");
        Socket socket1 = new Socket("localhost", 1234);
        Client client = new Client( username, socket1);
        client.listenForMessage();
        client.sendMessage();
    }
}
















