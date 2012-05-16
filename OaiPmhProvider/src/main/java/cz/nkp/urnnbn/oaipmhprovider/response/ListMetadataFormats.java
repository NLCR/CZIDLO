/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider.response;

import cz.nkp.urnnbn.oaipmhprovider.ErrorCode;
import cz.nkp.urnnbn.oaipmhprovider.OaiException;
import cz.nkp.urnnbn.oaipmhprovider.repository.Identifier;
import cz.nkp.urnnbn.oaipmhprovider.repository.MetadataFormat;
import cz.nkp.urnnbn.oaipmhprovider.repository.Record;
import cz.nkp.urnnbn.oaipmhprovider.repository.Repository;
import cz.nkp.urnnbn.oaipmhprovider.tools.Parser;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.dom4j.Element;

/**
 *
 * @author Martin Řehánek (rehan at mzk.cz)
 */
public class ListMetadataFormats extends OaiVerbResponse {

    private final String IDENTIFIER = "identifier";

    public ListMetadataFormats(Map<String, String[]> parameters) throws IOException {
        super("ListMetadataFormats", parameters);
    }

    @Override
    String[] getRequiredArguments() {
        String[] result = {};
        return result;
    }

    @Override
    String[] getOptionalArguments() {
        String[] result = {IDENTIFIER};
        return result;
    }

    @Override
    String getExclusiveArgument() {
        return null;
    }

    @Override
    void createResponse() throws OaiException, IOException {
        String identifier = getArgumentValueIfPresent(IDENTIFIER);
        if (identifier == null) {
            createResponseForAll();
        } else {
            createResponseFor(identifier);
        }
    }

    private void createResponseForAll() {
        for (MetadataFormat format : MetadataFormat.values()) {
            addMetadataFormatEl(format);
        }
    }

    private void addMetadataFormatEl(MetadataFormat format) {
        Element metadataFormatEl = rootEl.addElement("metadataFormat");
        Element prefixEl = metadataFormatEl.addElement("metadataPrefix");
        prefixEl.addText(format.toString());
        Element schemaEl = metadataFormatEl.addElement("schema");
        schemaEl.addText(format.getSchemaUrl().toString());
        Element namespaceEl = metadataFormatEl.addElement("metadataNamespace");
        namespaceEl.addText(format.getNamespaceUri());
    }

    private void createResponseFor(String idString) throws OaiException, IOException {
        Identifier id = Parser.parseIdentifier(idString);
        Set<Record> recordsFound = findRecords(id);
        if (recordsFound.isEmpty()) {
            throw new OaiException(ErrorCode.idDoesNotExist, "Item with id '" + idString + "' not found");
        }
        Set<Record> recordsPresent = getNotDeletedRecords(recordsFound);
        if (recordsPresent.isEmpty()) {
            throw new OaiException(ErrorCode.noMetadataFormats, "No metadata formats for item with id '" + idString + "' found");
        }
        addFormatsOfRecords(recordsPresent);
    }

    private Set<Record> findRecords(Identifier id) throws IOException {
        Repository repository = config.getRepository();
        Set<Record> result = new HashSet<Record>();
        for (MetadataFormat format : MetadataFormat.values()) {
            Record record = repository.getRecord(id, format, true);
            if (record != null) {
                result.add(record);
            }
        }
        return result;
    }

    private Set<Record> getNotDeletedRecords(Set<Record> recordsFound) {
        Set<Record> result = new HashSet<Record>();
        for (Record record : recordsFound) {
            if (!record.isDeleted()) {
                result.add(record);
            }
        }
        return result;
    }

    private void addFormatsOfRecords(Set<Record> recordsPresent) {
        for (Record record : recordsPresent) {
            addMetadataFormatEl(record.getMetadataFormat());
        }
    }
}
