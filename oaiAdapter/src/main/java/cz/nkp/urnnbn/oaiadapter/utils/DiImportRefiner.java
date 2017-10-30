package cz.nkp.urnnbn.oaiadapter.utils;

import cz.nkp.urnnbn.oaiadapter.DocumentOperationException;
import nu.xom.Document;
import nu.xom.Element;

/**
 * Created by Martin Řehánek on 30.10.17.
 */
public class DiImportRefiner extends XmlRefiner {

    public void refineDocument(Document document) throws DocumentOperationException {
        Element root = document.getRootElement();
        if ("digitalInstance".equals(root.getLocalName())) {
            refineDigitalInstanceElement(root);
        }
    }

    private void refineDigitalInstanceElement(Element digitalInstanceEl) {
        //remove elements if empty
        removeElementIfContentNoMatch(digitalInstanceEl, "format", ".+");
        removeElementIfContentNoMatch(digitalInstanceEl, "accessibility", ".+");
    }
}
