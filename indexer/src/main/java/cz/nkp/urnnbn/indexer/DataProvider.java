package cz.nkp.urnnbn.indexer;

import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.ResolvationLog;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by Martin Řehánek on 29.1.18.
 */
public interface DataProvider {

    public List<DigitalDocument> digDocsByModificationDate(DateTime from, DateTime until);

    public List<ResolvationLog> resolvationLogsByDate(DateTime from, DateTime until);

    public UrnNbn urnByDigDocId(long id, boolean withPredecessorsAndSuccessors);

}
