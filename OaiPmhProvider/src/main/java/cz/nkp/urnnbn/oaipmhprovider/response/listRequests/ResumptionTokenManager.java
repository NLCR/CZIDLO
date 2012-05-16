/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider.response.listRequests;

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
    //instance data
    private final Map<String, ListPart> resumptionTokens = new HashMap<String, ListPart>();
    private int returnRecords = 5;

    public static ResumptionTokenManager instanceOf(ListRequestType type, int returnRecords) {
        if (type == ListRequestType.LIST_IDENTIFIERS) {
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
        for (ListPart requestSequence : resumptionTokens.values()) {
            if (timeToDie(requestSequence)) {
                String uuid = uuidFromResultPart(requestSequence);
                logger.log(Level.INFO, "Removing resumption token {0} which was valid until {1}", new Object[]{uuid, requestSequence.getValidUntil().toString()});
                resumptionTokens.values().remove(requestSequence);
            }
        }
    }

    private boolean timeToDie(ListPart resumption) {
        return resumption.getValidUntil().isBeforeNow();
    }

    private String uuidFromResultPart(ListPart resumption) {
        for (String uuid : resumptionTokens.keySet()) {
            if (resumption.equals(resumptionTokens.get(uuid))) {
                return uuid;
            }
        }
        return null;
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
        ListPart resumption = resumptionTokens.get(resumptionToken);
        return resumption;
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
