/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider.response;

import cz.nkp.urnnbn.oaipmhprovider.ErrorCode;
import cz.nkp.urnnbn.oaipmhprovider.OaiException;
import cz.nkp.urnnbn.oaipmhprovider.repository.DateStamp;
import cz.nkp.urnnbn.oaipmhprovider.repository.MetadataFormat;
import cz.nkp.urnnbn.oaipmhprovider.repository.Record;
import cz.nkp.urnnbn.oaipmhprovider.repository.Repository;
import cz.nkp.urnnbn.oaipmhprovider.response.listRequests.CompleteList;
import cz.nkp.urnnbn.oaipmhprovider.response.listRequests.ListPart;
import cz.nkp.urnnbn.oaipmhprovider.response.listRequests.ResumptionTokenManager;
import cz.nkp.urnnbn.oaipmhprovider.tools.ElementAppender;
import cz.nkp.urnnbn.oaipmhprovider.tools.Parser;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;

/**
 *
 * @author Martin Řehánek (rehan at mzk.cz)
 */
public abstract class AbstractListResponse extends OaiVerbResponse {

    private static final Logger logger = Logger.getLogger(AbstractListResponse.class.getName());
    private final String FROM = "from";
    private final String UNTIL = "until";
    private final String METADATA_PREFIX = "metadataPrefix";
    private final String SET = "set";
    private final String RESUMPTION_TOKEN = "resumptionToken";

    public AbstractListResponse(String verbStr, Map<String, String[]> parameters) throws IOException {
        super(verbStr, parameters);
    }

    @Override
    String[] getRequiredArguments() {
        String[] result = {METADATA_PREFIX};
        return result;
    }

    @Override
    String[] getOptionalArguments() {
        String[] result = {FROM, UNTIL, SET};
        return result;
    }

    @Override
    String getExclusiveArgument() {
        return RESUMPTION_TOKEN;
    }

    @Override
    void createResponse() throws OaiException, IOException {
        String resumptionToken = getArgumentValueIfPresent(RESUMPTION_TOKEN);
        if (resumptionToken == null) {
            logger.log(Level.FINE, "first request sequence");
            createResponseForFirstRecords();
        } else {
            logger.log(Level.FINE, "response from resumptionToken {0}", resumptionToken);
            createResponseFromToken(resumptionToken);
        }
    }

    private void createResponseForFirstRecords() throws OaiException, IOException {
        String metadataPrefix = getArgumentValueIfPresent(METADATA_PREFIX);
        MetadataFormat format = Parser.parseMetadataPrefix(metadataPrefix);
        ListConditions conditions = getCriteria();
        logger.log(Level.FINE, "getting records from repository");
        //Set<Record> records = getRecords(format, conditions);
        Set<Record> identifiers = getRecordsWithCriteria(format, conditions);
        logger.log(Level.FINE, "building ListRequest");
        //CompleteList request = new CompleteList(records);
        CompleteList request = new CompleteList(identifiers);
        logger.log(Level.FINE, "creating RequestSequence");
        ListPart part = new ListPart(request, 0, getResultPartsManager().returnedRecords());
        //logger.log(Level.INFO, "creating new resumptionToken");
        String resumptionToken = getResultPartsManager().registerNextResultPart(part);
        //logger.log(Level.INFO, "resumptionToken: {0}", resumptionToken);
        createResponse(part, resumptionToken);
    }

    private ListConditions getCriteria() throws OaiException {
        String set = getArgumentValueIfPresent(SET);
        String from = getArgumentValueIfPresent(FROM);
        String until = getArgumentValueIfPresent(UNTIL);
        return ListConditions.instanceOf(set, from, until);
    }

    private Set<Record> getRecordsWithCriteria(MetadataFormat format, ListConditions conditions) throws OaiException {
        String setSpec = conditions.getSetSpec();
        DateStamp from = conditions.getFrom();
        DateStamp until = conditions.getUntil();
        Set<Record> records = getRecordsWithCriteria(format, setSpec, from, until);
        if (records.isEmpty()) {
            throw new OaiException(ErrorCode.noRecordsMatch,
                    " no records for format: " + format.toString()
                    + ", setSpec: " + setSpec
                    + ", from: " + from
                    + ", until: " + until);
        }
        return records;
    }

    private Set<Record> getRecordsWithCriteria(MetadataFormat format, String setSpec, DateStamp from, DateStamp until) {
        Repository repository = config.getRepository();
        if (setSpec == null) {
            return repository.getRecords(format, from, until);
        } else {
            return repository.getRecords(format, setSpec, from, until);
        }
    }

    private void createResponseFromToken(String resumptionToken) throws OaiException, IOException {
        ListPart part = getResultPartsManager().requestSeqFromResToken(resumptionToken);
        if (part == null) {
            throw new OaiException(ErrorCode.badResumptionToken, "invalid or expired resumption token");
        }
        String nextResumptionToken = getResultPartsManager().registerNextResultPart(part);
        createResponse(part, nextResumptionToken);
    }

    private void createResponse(ListPart part, String resumptionToken) throws IOException {
        for (Record record : part.getRecords()) {
            appendRecordDataToRoot(record);
        }
//        for (Identifier item : part.getRecords()) {
//            appendDataToRoot(item);
//        }
        int completeSize = part.getCompleteListSize();
        int cursor = part.cursor();
        DateTime validUntil = part.getValidUntil();
        ElementAppender.appendResumptionToken(rootEl, completeSize, cursor, validUntil, resumptionToken);
    }

    abstract ResumptionTokenManager getResultPartsManager();

    //abstract void appendDataToRoot(Identifier itemId) throws IOException;
    
    abstract void appendRecordDataToRoot(Record record) throws IOException;
}
