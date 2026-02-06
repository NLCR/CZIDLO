package cz.nkp.urnnbn.indexer.es;

public class ThrottleController {

    // Cílové limity
    private final long targetBulkMs;      // např. 200ms pro batch
    private final double targetMsPerOp;   // např. 2.5 ms/op (100 ops => 250ms)

    // EMA pro vyhlazení
    private final double alpha;           // 0.2 je rozumné
    private Double emaBulkMs = null;
    private Double emaMsPerOp = null;

    // Backoff stav
    private long backoffMs = 0;
    private final long maxBackoffMs;
    private final long minBackoffMs;

    ThrottleController(long targetBulkMs, double targetMsPerOp, double alpha, long minBackoffMs, long maxBackoffMs) {
        this.targetBulkMs = targetBulkMs;
        this.targetMsPerOp = targetMsPerOp;
        this.alpha = alpha;
        this.minBackoffMs = minBackoffMs;
        this.maxBackoffMs = maxBackoffMs;
    }

    long computeSleepMs(BulkIndexResult r) {
        if (r == null || r.operations() <= 0) return 0;

        double bulkMs = r.bulkMs();
        double msPerOp = bulkMs / (double) r.operations();

        emaBulkMs = emaBulkMs == null ? bulkMs : (alpha * bulkMs + (1 - alpha) * emaBulkMs);
        emaMsPerOp = emaMsPerOp == null ? msPerOp : (alpha * msPerOp + (1 - alpha) * emaMsPerOp);

        boolean overloaded =
                emaBulkMs > targetBulkMs
                        || emaMsPerOp > targetMsPerOp;

        if (overloaded) {
            // když je přetížení, backoff navyšuj rychle
            backoffMs = backoffMs == 0 ? minBackoffMs : Math.min(maxBackoffMs, backoffMs * 2);

            // bonus: pokud je to hodně nad limit, přidej ještě trochu
            double severity = Math.max(emaBulkMs / targetBulkMs, emaMsPerOp / targetMsPerOp);
            long extra = (long) Math.min(5000, (severity - 1.0) * 200); // max +5s

            return Math.min(maxBackoffMs, backoffMs + extra);
        } else {
            // když je OK, povoluj pomalu (aby to „nepumpovalo“)
            if (backoffMs > 0) {
                backoffMs = Math.max(0, (long) (backoffMs * 0.7)); // decay
            }
            return 0;
        }
    }

    String stateString() {
        return String.format("throttle emaBulk=%.1fms emaMsPerOp=%.3f backoff=%dms",
                emaBulkMs == null ? 0.0 : emaBulkMs,
                emaMsPerOp == null ? 0.0 : emaMsPerOp,
                backoffMs
        );
    }
}
