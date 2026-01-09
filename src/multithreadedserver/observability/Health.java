package multithreadedserver.observability;

public final class Health {

    private Health() {}

    public static boolean isHealthy() {
        return Metrics.errors.get() < 50 &&
                Metrics.activeConnections.get() < 45;
    }
}
