package cz.nkp.urnnbn.solr_indexer;

/**
 * Created by Martin Řehánek on 21.12.17.
 */
public class Counters {
    private final int found;
    private int indexed = 0;
    private int errors = 0;

    public Counters(int found) {
        this.found = found;
    }

    public void incrementIndexed() {
        indexed += 1;
    }

    public void incrementErrors() {
        errors += 1;
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
        return indexed + errors;
    }
}
