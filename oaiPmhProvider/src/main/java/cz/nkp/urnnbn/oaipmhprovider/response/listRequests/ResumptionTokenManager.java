/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider.response.listRequests;

import cz.nkp.urnnbn.oaipmhprovider.repository.MetadataFormat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Řehánek <rehan at mzk.cz>
 */
public class ResumptionTokenManager {

    private static final Logger logger = Logger.getLogger(ResumptionTokenManager.class.getName());
    private static ResumptionTokenManager listIdentifiersInst;
    private static ResumptionTokenManager listRecordsInst;
    // instance data
    private final Map<String, ListPart> resumptionTokens = new HashMap<String, ListPart>();
    private int returnRecords = 5;

    public static ResumptionTokenManager instanceOf(ListRequestType type, int returnRecords) {
        if (type == ListRequestType.LIST_IDENTIFIERS) {
            System.out.printf("ResumptionTokenManager: Creating instance for LIST_IDENTIFIERS with returnRecords=%d%n", returnRecords);
            if (listIdentifiersInst == null) {
                listIdentifiersInst = new ResumptionTokenManager(returnRecords);
            }
            return listIdentifiersInst;
        } else if (type == ListRequestType.LIST_RECORDS) {
            if (listRecordsInst == null) {
                listRecordsInst = new ResumptionTokenManager(returnRecords);
            }
            return listRecordsInst;
        } else {
            return null;
        }
    }

    private ResumptionTokenManager(int returnRecords) {
        this.returnRecords = returnRecords;
    }

    public static void clearOldResumptionTokens() {
        if (listIdentifiersInst != null) {
            listIdentifiersInst.clearElderlyResumptionTokens();
        }
        if (listRecordsInst != null) {
            listRecordsInst.clearElderlyResumptionTokens();
        }
    }

    private void clearElderlyResumptionTokens() {
        for (String resumptionToken : resumptionTokens.keySet()) {
            ListPart requestSequence = resumptionTokens.get(resumptionToken);
            if (timeToDie(requestSequence)) {
                logger.log(Level.INFO, "Removing resumption token {0} which was valid until {1}", new Object[]{resumptionToken, requestSequence.getValidUntil().toString()});
                resumptionTokens.remove(resumptionToken);
            }
        }
    }

    private boolean timeToDie(ListPart resumption) {
        return resumption.getValidUntil().isBeforeNow();
    }

    public synchronized String registerNextResultPart(ListPart part) throws IOException {
        ListPart nextPart = part.nextPart();
        if (nextPart != null) {
            String resumptionToken = generateNotColidingUuid();
            logger.log(Level.INFO, "registering new resumption token {0}", resumptionToken);
            resumptionTokens.put(resumptionToken, nextPart);
            return resumptionToken;
        } else {
            return null;
        }
    }

    public ListPart requestSeqFromResToken(String resumptionToken) {
        return resumptionTokens.get(resumptionToken);
    }

    public int returnedRecords() {
        return returnRecords;
    }

    private static String generateNotColidingUuid() {
        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while (resumptionTokenUsed(uuid.toString()));
        return uuid.toString();
    }

    private static boolean resumptionTokenUsed(String resumptionToken) {
        return (listIdentifiersInst != null && listIdentifiersInst.containsResumptionToken(resumptionToken))
                || (listRecordsInst != null && listRecordsInst.containsResumptionToken(resumptionToken));
    }

    private boolean containsResumptionToken(String resumptionToken) {
        return resumptionTokens.keySet().contains(resumptionToken);
    }
}
