/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider.response;

import java.io.IOException;
import java.util.Map;

import cz.nkp.urnnbn.oaipmhprovider.ErrorCode;
import cz.nkp.urnnbn.oaipmhprovider.OaiException;
import cz.nkp.urnnbn.oaipmhprovider.repository.Identifier;
import cz.nkp.urnnbn.oaipmhprovider.repository.MetadataFormat;
import cz.nkp.urnnbn.oaipmhprovider.repository.Record;
import cz.nkp.urnnbn.oaipmhprovider.repository.Repository;
import cz.nkp.urnnbn.oaipmhprovider.tools.ElementAppender;
import cz.nkp.urnnbn.oaipmhprovider.tools.Parser;

/**
 *
 * @author Martin Řehánek (rehan at mzk.cz)
 */
public class GetRecord extends OaiVerbResponse {

    private final String IDENTIFIER = "identifier";
    private final String METADATA_PREFIX = "metadataPrefix";

    public GetRecord(Map<String, String[]> parameters) throws IOException {
        super("GetRecord", parameters);
    }

    @Override
    String[] getRequiredArguments() {
        String[] result = { IDENTIFIER, METADATA_PREFIX };
        return result;
    }

    @Override
    String[] getOptionalArguments() {
        String[] result = {};
        return result;

    }

    @Override
    String getExclusiveArgument() {
        return null;
    }

    @Override
    void createResponse() throws OaiException, IOException {
        String idString = getArgumentValueIfPresent(IDENTIFIER);
        Identifier id = Parser.parseIdentifier(idString);
        String metadataPrefix = getArgumentValueIfPresent(METADATA_PREFIX);
        MetadataFormat format = Parser.parseMetadataPrefix(metadataPrefix);
        Record record = getRecord(id, format);
        ElementAppender.appendRecord(rootEl, record);
    }

    private Record getRecord(Identifier id, MetadataFormat format) throws OaiException, IOException {
        Repository repository = config.getRepository();
        Record record = repository.getRecord(id, format, true);
        if (record == null) {
            throw new OaiException(ErrorCode.cannotDisseminateFormat, "no such record");
        } else {
            return record;
        }
    }
}
