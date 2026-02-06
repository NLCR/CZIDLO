package cz.nkp.urnnbn.indexer.es;

public record BulkIndexResult(
        int requestedDocs,
        int convertedDocs,
        int operations,
        int bulkItems,
        int bulkErrors,
        long connMs,
        long convertMs,
        long bulkMs,
        long totalMs
) {
}
