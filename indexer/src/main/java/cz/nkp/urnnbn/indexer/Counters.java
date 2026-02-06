package cz.nkp.urnnbn.indexer;

/**
 * Created by Martin Řehánek on 21.12.17.
 */
public class Counters {
    private final int found;
    private int indexed = 0;
    private int errors = 0;
    private int processed = 0;

    public Counters(int found) {
        this.found = found;
    }

    public void incrementIndexed() {
        indexed += 1;
        processed += 1;
    }

    public void incrementErrors() {
        errors += 1;
        processed += 1;
    }

    public int getFound() {
        return found;
    }

    public int getIndexed() {
        return indexed;
    }

    public int getErrors() {
        return errors;
    }

    public int getProcessed() {
        return processed;
    }

    public void addProcessed(int i) {
        this.processed += i;
    }

    public void addIndexed(int i) {
        this.indexed += i;
    }

    public void addErrors(int i) {
        this.errors += i;
    }
}
