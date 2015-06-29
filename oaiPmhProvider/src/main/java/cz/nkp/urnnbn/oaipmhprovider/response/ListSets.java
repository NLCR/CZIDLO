/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider.response;

import cz.nkp.urnnbn.oaipmhprovider.ErrorCode;
import cz.nkp.urnnbn.oaipmhprovider.OaiException;
import cz.nkp.urnnbn.oaipmhprovider.repository.OaiSet;
import cz.nkp.urnnbn.oaipmhprovider.repository.Repository;
import java.io.IOException;
import java.util.Map;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;

/**
 * TODO: implement resumption tokens for sets
 * @author Martin Řehánek (rehan at mzk.cz)
 */
public class ListSets extends OaiVerbResponse {

    private Namespace oai_dc = DocumentHelper.createNamespace("oai_dc", "http://www.openarchives.org/OAI/2.0/oai_dc/");
    private Namespace dc = DocumentHelper.createNamespace("dc", "http://purl.org/dc/elements/1.1/");
    private final String RESUMPTION_TOKEN = "resumptionToken";

    public ListSets(Map<String, String[]> parameters) throws IOException {
        super("ListSets", parameters);
    }

    @Override
    String[] getRequiredArguments() {
        String[] result = {};
        return result;
    }

    @Override
    String[] getOptionalArguments() {
        String[] result = {};
        return result;
    }

    @Override
    String getExclusiveArgument() {
        return RESUMPTION_TOKEN;
    }

    @Override
    void createResponse() throws OaiException, IOException {
        loadResumptionToken();
        Repository repository = config.getRepository();
        for (OaiSet set : repository.getSets()) {
            addSetElement(rootEl, set);
        }
    }

    private void addSetElement(Element rootEl, OaiSet set) {
        Element setEl = rootEl.addElement("set");
        Element setSpec = setEl.addElement("setSpec");
        setSpec.addText(set.getSetSpec());
        Element setName = setEl.addElement("setName");
        setName.addText(set.getSetName());
        //addSetDescription(setEl, set);
    }

    private void addSetDescription(Element setEl, OaiSet set) {
        Element setDescriptionEl = setEl.addElement("setDescription");
        Element dcEl = setDescriptionEl.addElement(new QName("dc", oai_dc));
        dcEl.addAttribute(new QName("schemaLocation", xsi), "http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd");
        Element description = dcEl.addElement(new QName("description", dc));
        description.addText("TODO");
        //description.addText("This set contains " + set.getSize() + " items");
    }

    private void loadResumptionToken() throws OaiException {
        String resumptionToken = getArgumentValueIfPresent(RESUMPTION_TOKEN);
        if (resumptionToken != null) {
            throw new OaiException(ErrorCode.badResumptionToken, "Resumption tokens not currently supported for ListSets");
        }
    }
}
