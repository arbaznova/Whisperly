package multithreadedserver.Main;

import multithreadedserver.readersandwriters.SocketWriter;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ClientRegistry {

    private static final ConcurrentHashMap<String, SocketWriter> clients =
            new ConcurrentHashMap<>();

    private ClientRegistry() {}

    // ---------- USERNAME VALIDATION ----------
    public static boolean isValidUsername(String username) {
        return username != null
                && username.matches("[a-zA-Z0-9_]{1,20}")
                && !clients.containsKey(username);
    }

    // ---------- ADD / REMOVE ----------
    public static void add(String username, SocketWriter writer) {
        clients.put(username, writer);
        broadcast("Server: " + username + " joined", username);
    }

    public static void remove(String username) {
        clients.remove(username);
        broadcast("Server: " + username + " left", username);
    }

    // ---------- BROADCAST ----------
    public static void broadcast(String message, String exclude) {
        clients.forEach((user, writer) -> {
            if (!user.equals(exclude)) {
                try {
                    writer.write(message);
                } catch (Exception ignored) {}
            }
        });
    }

    // ---------- PRIVATE MESSAGE ----------
    public static boolean sendPrivate(String from, String to, String msg) {
        SocketWriter target = clients.get(to);
        if (target == null) return false;

        try {
            target.write("[PM from " + from + "]: " + msg);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ---------- USER LIST ----------
    public static String listUsers() {
        Set<String> users = clients.keySet();
        return "Online users: " +
                users.stream().collect(Collectors.joining(", "));
    }
}
