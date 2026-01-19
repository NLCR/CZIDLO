/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider.response;

import java.io.IOException;
import java.util.Map;

import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.oaipmhprovider.repository.MetadataFormat;
import cz.nkp.urnnbn.oaipmhprovider.repository.Record;
import cz.nkp.urnnbn.oaipmhprovider.response.listRequests.ListRequestType;
import cz.nkp.urnnbn.oaipmhprovider.response.listRequests.ResumptionTokenManager;
import cz.nkp.urnnbn.oaipmhprovider.tools.ElementAppender;

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
    void appendRecordDataToRoot(UrnNbn urnNbn, MetadataFormat format) throws IOException {
        Record record = convertToRepositoryRecord(urnNbn, format);
        ElementAppender.appendRecord(rootEl, record);
    }

    @Override
    ResumptionTokenManager getResultPartsManager() {
        return partsManager;
    }
}
