/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider;

import cz.nkp.urnnbn.oaipmhprovider.response.GetRecord;
import cz.nkp.urnnbn.oaipmhprovider.response.Identify;
import cz.nkp.urnnbn.oaipmhprovider.response.ListIdentifiers;
import cz.nkp.urnnbn.oaipmhprovider.response.ListMetadataFormats;
import cz.nkp.urnnbn.oaipmhprovider.response.ListRecords;
import cz.nkp.urnnbn.oaipmhprovider.response.ListSets;
import cz.nkp.urnnbn.oaipmhprovider.response.OaiVerbResponse;
import java.io.IOException;
import java.util.Map;

/**
 *
 * @author Martin Řehánek (rehan at mzk.cz)
 */
public class VerbFactory {

    public static OaiVerbResponse getVerbResponse(String verbStr, Map<String, String[]> parameters) throws OaiException, IOException {
        if ("Identify".equals(verbStr)) {
            return new Identify(parameters);
        }
        if ("ListSets".equals(verbStr)) {
            return new ListSets(parameters);
        }
        if ("GetRecord".equals(verbStr)) {
            return new GetRecord(parameters);
        }
        if ("ListMetadataFormats".equals(verbStr)) {
            return new ListMetadataFormats(parameters);
        }
        if ("ListIdentifiers".equals(verbStr)) {
            return new ListIdentifiers(parameters);
        }
        if ("ListRecords".equals(verbStr)) {
            return new ListRecords(parameters);
        }
        throw new OaiException(ErrorCode.badVerb, "Illegal verb '" + verbStr + "'");
    }
}
