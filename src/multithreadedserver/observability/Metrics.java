package multithreadedserver.observability;

import java.util.concurrent.atomic.AtomicLong;

public final class Metrics{

    private Metrics() {}

    public static final AtomicLong activeConnections = new AtomicLong();
    public static final AtomicLong totalConnections = new AtomicLong();
    public static final AtomicLong messagesProcessed = new AtomicLong();
    public static final AtomicLong privateMessages = new AtomicLong();
    public static final AtomicLong rejectedConnections = new AtomicLong();
    public static final AtomicLong errors = new AtomicLong();
}
