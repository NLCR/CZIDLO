/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider.response;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.nkp.urnnbn.core.dto.UrnNbn;
import org.joda.time.DateTime;

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
        MetadataFormat format = getFormat();
        ListConditions conditions = getCriteria();
        logger.log(Level.FINE, "getting records from repository");
        // Set<Record> records = getRecords(format, conditions);
        Set<UrnNbn> urns = getRecordsWithCriteria(format, conditions);
        logger.log(Level.FINE, "building ListRequest");
        // CompleteList request = new CompleteList(records);
        CompleteList request = new CompleteList(urns, format);
        logger.log(Level.FINE, "creating RequestSequence");
        ListPart part = new ListPart(request, 0, getResultPartsManager().returnedRecords());
        // logger.log(Level.INFO, "creating new resumptionToken");
        String resumptionToken = getResultPartsManager().registerNextResultPart(part);
        // logger.log(Level.INFO, "resumptionToken: {0}", resumptionToken);
        createResponse(part, resumptionToken);
    }

    //pozor, ohybame architekturu
    protected MetadataFormat format = null;

    private MetadataFormat getFormat() throws OaiException {
        if (format == null) {
            String metadataPrefix = getArgumentValueIfPresent(METADATA_PREFIX);
            format = Parser.parseMetadataPrefix(metadataPrefix);
        }
        return format;
    }

    private ListConditions getCriteria() throws OaiException {
        String set = getArgumentValueIfPresent(SET);
        String from = getArgumentValueIfPresent(FROM);
        String until = getArgumentValueIfPresent(UNTIL);
        return ListConditions.instanceOf(set, from, until);
    }

    private Set<UrnNbn> getRecordsWithCriteria(MetadataFormat format, ListConditions conditions) throws OaiException {
        String setSpec = conditions.getSetSpec();
        DateStamp from = conditions.getFrom();
        DateStamp until = conditions.getUntil();
        Set<UrnNbn> records = getRecordsWithCriteria(format, setSpec, from, until);
        if (records.isEmpty()) {
            throw new OaiException(ErrorCode.noRecordsMatch, " no records for format: " + format.toString() + ", setSpec: " + setSpec + ", from: "
                    + from + ", until: " + until);
        }
        return records;
    }

    private Set<UrnNbn> getRecordsWithCriteria(MetadataFormat format, String setSpec, DateStamp from, DateStamp until) {
        Repository repository = config.getRepository();
        if (setSpec == null) {
            return repository.getUrns(format, from, until);
        } else {
            return repository.getUrns(format, setSpec, from, until);
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
        //System.out.println("Creating response for part with cursor " + part.cursor() + " and size " + part.getRecords().size());
        //int counter = 0;
        for (UrnNbn record : part.getRecords()) {
            appendRecordDataToRoot(record, part.getMetadataFormat());
            //counter++;
        }
        //System.out.println("Appended " + counter + " records");
        // for (Identifier item : part.getRecords()) {
        // appendDataToRoot(item);
        // }
        int completeSize = part.getCompleteListSize();
        int cursor = part.cursor();
        DateTime validUntil = part.getValidUntil();
        ElementAppender.appendResumptionToken(rootEl, completeSize, cursor, validUntil, resumptionToken);
    }

    abstract ResumptionTokenManager getResultPartsManager();

    abstract void appendRecordDataToRoot(UrnNbn urnNbn, MetadataFormat format) throws IOException;

    protected Record convertToRepositoryRecord(UrnNbn urnNbn, MetadataFormat format) {
/*        MetadataFormat format = null;
        try {
            format = getFormat();
        } catch (OaiException e) {
            //tohle uz by se ne melo dit, parsovani formatu probehlo driv
            e.printStackTrace();
            throw new RuntimeException(e);
        }*/
        return config.getRepository().getRecord(urnNbn, format, false);
    }
}
