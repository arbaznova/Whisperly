package multithreadedserver.observability;

public class MetricsReporter implements Runnable {

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Logger.info("Metrics",
                    "active=" + Metrics.activeConnections.get() +
                            ", total=" + Metrics.totalConnections.get() +
                            ", messages=" + Metrics.messagesProcessed.get() +
                            ", private=" + Metrics.privateMessages.get() +
                            ", rejected=" + Metrics.rejectedConnections.get() +
                            ", errors=" + Metrics.errors.get()
            );
            if (!Health.isHealthy()) {
                Logger.warn("Server", "Server entering degraded mode");
            }
        }
    }
}
