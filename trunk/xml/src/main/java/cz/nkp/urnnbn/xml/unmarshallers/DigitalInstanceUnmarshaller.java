/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.xml.unmarshallers;

import cz.nkp.urnnbn.core.dto.DigitalInstance;
import nu.xom.Document;
import nu.xom.Element;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalInstanceUnmarshaller extends Unmarshaller {

    private final Document doc;

    public DigitalInstanceUnmarshaller(Document doc) {
        this.doc = doc;
    }

    public DigitalInstance getDigitalInstance() {
        DigitalInstance result = new DigitalInstance();
        Element root = doc.getRootElement();
        result.setUrl(elementContentOrNull("url", root));
        result.setLibraryId(Long.valueOf(elementContentOrNull("digitalLibraryId", root)));
        result.setFormat(elementContentOrNull("format", root));
        result.setAccessibility(elementContentOrNull("accessibility", root));
        result.setActive(Boolean.TRUE);
        return result;
    }
}
