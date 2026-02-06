package cz.nkp.urnnbn.indexer.es;

import cz.nkp.urnnbn.core.dto.ResolvationLog;
import cz.nkp.urnnbn.indexer.ProgressListener;
import org.joda.time.DateTime;

public interface EsIndexer extends AutoCloseable {

    void setProgressListener(ProgressListener progressListener);

    void indexDigitalDocument(long ddId);

    void indexDigitalDocuments(DateTime from, DateTime to);

    void indexResolvationLog(ResolvationLog resolvationLog);

    void indexResolvationLogs(DateTime from, DateTime to);

    void close();

    void stop();

}
