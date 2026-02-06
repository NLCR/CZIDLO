package cz.nkp.urnnbn.indexer.es;

import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.ResolvationLog;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.indexer.*;
import org.joda.time.DateTime;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;
import java.util.ArrayList;

public class EsIndexerBatching extends EsIndexerAbstract implements EsIndexer, AutoCloseable {

    private static final Logger logger = Logger.getLogger(EsIndexerBatching.class.getName());
    private static final boolean REPORT_MEASUREMENTS = true;
    private static final int INDEXING_BATCH_SIZE = 50; // start: 200–1000 typicky OK

    public EsIndexerBatching(IndexerConfig config, OutputStream reportLoggerStream, DataProvider dataProvider) {
        super(config, reportLoggerStream, dataProvider);
    }

    @Override
    public void indexResolvationLog(ResolvationLog resolvationLog) {
        try {
            esConnector.indexResolvation(resolvationLog.getId(), reportLogger);
        } catch (SQLException e) {
            report(" SQL error", e);
        } catch (IOException e) {
            report(" I/O error", e);
        }
    }

    @Override
    public void indexDigitalDocument(long ddId) {
        indexDigitalDocument(ddId, new Counters(1));
    }

    private void indexDigitalDocument(long ddInternalId, Counters counters) {
        UrnNbn urnNbn = dataProvider.urnByDigDocId(ddInternalId, false);
        if (urnNbn == null) {
            report(" digital document with id " + ddInternalId + " is missing URN:NBN");
        } else {
            //report(" processing " + urnNbn);
            try {
                esConnector.indexDigitalDocument(ddInternalId, reportLogger);
                counters.incrementIndexed();
            } catch (IOException e) {
                counters.incrementErrors();
                report(" I/O error", e);
            } catch (SQLException e) {
                counters.incrementErrors();
                report(" SQL error", e);
            } catch (Throwable e) {
                counters.incrementErrors();
                report(" Unexpected error", e);
            }
        }
        if (progressListener != null) {
            progressListener.onProgress(counters.getProcessed(), counters.getFound());
        }
    }

    @Override
    public void indexResolvationLogs(DateTime from, DateTime to) {
        long start = System.currentTimeMillis();
        report("Indexing resolvation logs");
        report("==============================");

        //range: dd.mm.yyyy - dd.mm.yyyy
        String fromStr = from == null ? null : from.toString("dd.MM.yyyy");
        String toStr = to == null ? null : to.toString("dd.MM.yyyy");
        report(String.format("Range: %s - %s", fromStr, toStr));
        List<ResolvationLog> resolvationLogs = dataProvider.resolvationLogsByDate(from, to);
        report("Matching records: " + resolvationLogs.size());

        Counters counters = new Counters(resolvationLogs.size());
        Integer limit = null; // for testing, set to null for production
        int iterationCount = 0;
        for (ResolvationLog log : resolvationLogs) {
            if (stopped) {
                report(" stopped ");
                break;
            }
            if (limit != null && iterationCount++ >= limit) {
                report(" limit of " + limit + " reached, stopping (for testing purposes) ");
                break;
            }
            indexResolvationLog(log, counters, false);
        }

        long now = System.currentTimeMillis();
        long totalDuration = now - start;
        float durationPerRecord = counters.getProcessed() == 0 ? 0 : (float) totalDuration / counters.getProcessed();

        report("Results");
        report("------------------------------");
        report(" records found    : " + counters.getFound());
        report(" records processed: " + counters.getProcessed());
        report(" records indexed  : " + counters.getIndexed());
        report(" records erroneous: " + counters.getErrors());
        report(" initialization time: " + formatTime(initTime));
        report(" records processing time: " + formatTime(totalDuration));
        report(" avg. record processing time: " + String.format("%.2f ms", durationPerRecord));
        if (progressListener != null) {
            progressListener.onFinished(counters.getProcessed(), counters.getFound());
        }
        report("");
    }

    private void indexResolvationLog(ResolvationLog resolvationLog, Counters counters, boolean explicitCommit) {
        //report(" processing " + resolvationLog);
        try {
            esConnector.indexResolvation(resolvationLog.getId(), reportLogger);
            counters.incrementIndexed();
        } catch (IOException e) {
            counters.incrementErrors();
            report(" I/O error", e);
        } catch (SQLException e) {
            counters.incrementErrors();
            report(" SQL error", e);
        } catch (Throwable e) {
            counters.incrementErrors();
            report(" Unexpected error", e);
        }
        if (progressListener != null) {
            progressListener.onProgress(counters.getProcessed(), counters.getFound());
        }
    }

    @Override
    public void indexDigitalDocuments(DateTime from, DateTime to) {
        long start = System.currentTimeMillis();
        report("Indexing digital documents");
        report("==============================");

        String fromStr = from == null ? null : from.toString("dd.MM.yyyy");
        String toStr = to == null ? null : to.toString("dd.MM.yyyy");
        report(String.format("Range: %s - %s", fromStr, toStr));

        List<DigitalDocument> digitalDocuments = dataProvider.digDocsByModificationDate(from, to);
        report("Matching records: " + digitalDocuments.size());

        Counters counters = new Counters(digitalDocuments.size());

        final int batchSize = INDEXING_BATCH_SIZE;
        List<Long> batch = new ArrayList<>(batchSize);

        long bulkTotalMs = 0;
        long convertTotalMs = 0;

        for (DigitalDocument doc : digitalDocuments) {
            if (stopped) {
                report(" stopped ");
                break;
            }

            batch.add(doc.getId());
            if (batch.size() >= batchSize) {
                BulkIndexResult r = flushDigDocBulk(batch, counters);
                bulkTotalMs += r.bulkMs();
                convertTotalMs += r.convertMs();
                batch.clear();
            }
        }

        if (!stopped && !batch.isEmpty()) {
            BulkIndexResult r = flushDigDocBulk(batch, counters);
            bulkTotalMs += r.bulkMs();
            convertTotalMs += r.convertMs();
            batch.clear();
        }

        long totalDuration = System.currentTimeMillis() - start;
        float durationPerRecord = counters.getProcessed() == 0 ? 0 : (float) totalDuration / counters.getProcessed();

        report("Results");
        report("------------------------------");
        report(" records found    : " + counters.getFound());
        report(" records processed: " + counters.getProcessed());
        report(" records indexed  : " + counters.getIndexed());
        report(" records erroneous: " + counters.getErrors());
        report(" initialization time: " + formatTime(initTime));
        report(" records processing time: " + formatTime(totalDuration));
        report(" avg. record processing time: " + String.format("%.2f ms", durationPerRecord));
        report(" bulk total time: " + bulkTotalMs + " ms");
        report(" convert total time: " + convertTotalMs + " ms");
        report("");

        if (progressListener != null) {
            progressListener.onFinished(counters.getProcessed(), counters.getFound());
        }
    }

    private BulkIndexResult flushDigDocBulk(List<Long> ddIds, Counters counters) {
        long t0 = System.currentTimeMillis();
        try {
            BulkIndexResult r = esConnector.bulkIndexDigitalDocuments(ddIds, reportLogger);

            counters.addProcessed(r.requestedDocs());
            counters.addIndexed(r.convertedDocs());
            counters.addErrors(r.bulkErrors());

            if (progressListener != null) {
                progressListener.onProgress(counters.getProcessed(), counters.getFound());
            }

            if (REPORT_MEASUREMENTS) {
                report(String.format(
                        "bulk docs=%d converted=%d ops=%d bulkMs=%d convertMs=%d totalMs=%d errors=%d",
                        r.requestedDocs(), r.convertedDocs(), r.operations(),
                        r.bulkMs(), r.convertMs(), r.totalMs(), r.bulkErrors()
                ));
            }

            return r;
        } catch (Exception e) {
            // celá dávka fail -> započti jako errors všechny requestedDocs
            counters.addProcessed(ddIds.size());
            counters.addErrors(ddIds.size());
            report(" Bulk indexing failed for batch size=" + ddIds.size(), e);

            long dt = System.currentTimeMillis() - t0;
            return new BulkIndexResult(ddIds.size(), 0, 0, 0, ddIds.size(), 0, 0, dt, dt);
        }
    }
}
