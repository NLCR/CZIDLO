/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider.response;

import java.io.IOException;
import java.util.Map;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;

/**
 *
 * @author Martin Řehánek (rehan at mzk.cz)
 */
public class Identify extends OaiVerbResponse {

    public Identify(Map<String, String[]> parameters) throws IOException {
        super("Identify", parameters);
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
        return null;
    }

    @Override
    void createResponse() throws IOException {
        System.err.println("creating identify response");
        addOaiElemenToIdentifyEl("repositoryName", config.getRepositoryName());
        addOaiElemenToIdentifyEl("baseURL", config.getBaseUrl());
        addOaiElemenToIdentifyEl("protocolVersion", "2.0");
        addOaiElemenToIdentifyEl("adminEmail", config.getAdminEmail());
        addOaiElemenToIdentifyEl("earliestDatestamp", config.getEarliestDatestamp());
        addOaiElemenToIdentifyEl("deletedRecord", "transient");
        addOaiElemenToIdentifyEl("granularity", "YYYY-MM-DDThh:mm:ssZ");
        addDescription();
    }

    Element addOaiElemenToIdentifyEl(String elementName, String text) {
        Element el = rootEl.addElement(elementName);
        el.addText(text);
        return el;
    }

    private void addDescription() {
        Element description = rootEl.addElement("description");
        Namespace descriptionNs = DocumentHelper.createNamespace("dsc", "http://oai.mzk.cz/MoZaKi/v1/description/");
        Element repository = description.addElement(new QName("oaiPmhRepository", descriptionNs));
        repository.addAttribute(new QName("schemaLocation", xsi), "http://oai.mzk.cz/MoZaKi/v1/description/ http://oai.mzk.cz/xsd/v1/description.xsd");
        Element providerImpl = repository.addElement(new QName("providerImplementation", descriptionNs));
        providerImpl.addAttribute("licence", "GNU GPL v2");
        Element projectUrl = providerImpl.addElement(new QName("projectUrl", descriptionNs));
        projectUrl.addText("https://code.google.com/p/joai-mzk/");
        Element author = providerImpl.addElement(new QName("author", descriptionNs));
        Element name = author.addElement(new QName("name", descriptionNs));
        name.addText("Martin Řehánek");
        Element email = author.addElement(new QName("email", descriptionNs));
        email.addText("rehan@mzk.cz");
        Element organization = author.addElement(new QName("organization", descriptionNs));
        organization.addText("Moravská zemská knihovna");
        Element webUI = providerImpl.addElement(new QName("webUI", descriptionNs));
        webUI.addText((config.getWebUrl()));
    }
}
