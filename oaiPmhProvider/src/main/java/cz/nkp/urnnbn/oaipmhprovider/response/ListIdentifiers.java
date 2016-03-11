/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider.response;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import cz.nkp.urnnbn.oaipmhprovider.repository.Record;
import cz.nkp.urnnbn.oaipmhprovider.response.listRequests.ListRequestType;
import cz.nkp.urnnbn.oaipmhprovider.response.listRequests.ResumptionTokenManager;
import cz.nkp.urnnbn.oaipmhprovider.tools.ElementAppender;

/**
 *
 * @author Martin Řehánek (rehan at mzk.cz)
 */
public class ListIdentifiers extends AbstractListResponse {

    private static final Logger logger = Logger.getLogger(ListIdentifiers.class.getName());
    private final ResumptionTokenManager partsManager;

    public ListIdentifiers(Map<String, String[]> parameters) throws IOException {
        super("ListIdentifiers", parameters);
        partsManager = ResumptionTokenManager.instanceOf(ListRequestType.LIST_IDENTIFIERS, config.getListIdentifiersMaxSize());
    }

    @Override
    void appendRecordDataToRoot(Record record) {
        ElementAppender.appendHeaderType(rootEl, record);
    }

    @Override
    ResumptionTokenManager getResultPartsManager() {
        return partsManager;
    }
}
