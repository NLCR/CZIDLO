/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.xml.unmarshallers;

import cz.nkp.urnnbn.core.dto.DigitalInstance;
import nu.xom.Document;

/**
 *
 * @author Martin Řehánek
 */
public class DigInstUnmrashaller extends Unmarshaller {

    public DigInstUnmrashaller(Document doc) {
        super(doc);
    }

    public DigitalInstance getDigitalInstance() {
        String url = selectSingleNodeFromDoc("/resolver:digitalInstance/resolver:url").getValue();
        String digLibId = selectSingleNodeFromDoc("/resolver:digitalInstance/resolver:digitalLibraryId").getValue();
        DigitalInstance digitalInstance = new DigitalInstance();
        digitalInstance.setUrl(url);
        digitalInstance.setLibraryId(Long.valueOf(digLibId));
        return digitalInstance;
    }
}
