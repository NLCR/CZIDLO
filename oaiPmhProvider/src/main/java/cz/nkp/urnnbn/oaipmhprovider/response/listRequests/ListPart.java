/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider.response.listRequests;

import cz.nkp.urnnbn.oaipmhprovider.conf.OaiPmhConfiguration;
import cz.nkp.urnnbn.oaipmhprovider.repository.Record;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import org.joda.time.DateTime;

/**
 *
 * @author Martin Řehánek <rehan at mzk.cz>
 */
public class ListPart {

    private static final Logger logger = Logger.getLogger(ListPart.class.getName());
    private final CompleteList list;
    private final int cursor;
    private final int maxRecordsReturned;
    private final DateTime validUntil;

    public ListPart(CompleteList list, int cursor, int maxRecordsReturned) throws IOException {
        this.list = list;
        this.cursor = cursor;
        this.maxRecordsReturned = maxRecordsReturned;
        DateTime now = new DateTime();
        this.validUntil = now.plusMinutes(OaiPmhConfiguration.instanceOf().getListRequestsMinutesValid());
    }

    ListPart nextPart() throws IOException {
        int nextCursor = nextCursor();
        if (nextCursor < list.totalSize()) {
            return new ListPart(list, nextCursor, maxRecordsReturned);
        } else {
            logger.fine("last list part");
            return null;
        }
    }

    public int getCompleteListSize() {
        return list.totalSize();
    }

    public List<Record> getRecords() {
        return list.getIdentifiers(cursor, nextCursor(), maxRecordsReturned);
    }

    public int cursor() {
        return cursor;
    }

    private int nextCursor() {
        return cursor + maxRecordsReturned;
    }

    public DateTime getValidUntil() {
        return validUntil;
    }
}
