/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider.response;

import cz.nkp.urnnbn.oaipmhprovider.ErrorCode;
import cz.nkp.urnnbn.oaipmhprovider.OaiException;
import cz.nkp.urnnbn.oaipmhprovider.repository.DateStamp;

/**
 *
 * @author Martin Řehánek (rehan at mzk.cz)
 */
public class ListConditions {

    public static ListConditions instanceOf(String set, String fromStr, String untilStr) throws OaiException {
        DateStamp from = fromStr == null ? null : parseDatestamp("from", fromStr);
        DateStamp until = untilStr == null ? null : parseDatestamp("until", untilStr);
        return new ListConditions(set, from, until);
    }

    private static DateStamp parseDatestamp(String name, String value) throws OaiException {
        try {
            return DateStamp.parse(value);
        } catch (Throwable e) {
            throw new OaiException(ErrorCode.badArgument, "cannot parse argument " + name + ": '" + value + "'");
        }
    }

    private final String setSpec;
    private final DateStamp from;
    private final DateStamp until;

    private ListConditions(String setSpec, DateStamp from, DateStamp until) {
        this.setSpec = setSpec;
        this.from = from;
        this.until = until;
    }

    /**
     * @return the from
     */
    public DateStamp getFrom() {
        return from;
    }

    /**
     * @return the until
     */
    public DateStamp getUntil() {
        return until;
    }

    /**
     * @return the set
     */
    public String getSetSpec() {
        return setSpec;
    }
}
