/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider.response;

import cz.nkp.urnnbn.oaipmhprovider.repository.Record;
import cz.nkp.urnnbn.oaipmhprovider.response.listRequests.ListRequestType;
import cz.nkp.urnnbn.oaipmhprovider.response.listRequests.ResumptionTokenManager;
import cz.nkp.urnnbn.oaipmhprovider.tools.ElementAppender;
import java.io.IOException;
import java.util.Map;

/**
 *
 * @author Martin Řehánek (rehan at mzk.cz)
 */
public class ListRecords extends AbstractListResponse {

    private final ResumptionTokenManager partsManager;

    public ListRecords(Map<String, String[]> parameters) throws IOException {
        super("ListRecords", parameters);
        partsManager = ResumptionTokenManager.instanceOf(ListRequestType.LIST_RECORDS, config.getListRecordsMaxSize());
    }

    @Override
    void appendRecordDataToRoot(Record record) throws IOException {
        ElementAppender.appendRecord(rootEl, record);
    }

    @Override
    ResumptionTokenManager getResultPartsManager() {
        return partsManager;
    }
}
