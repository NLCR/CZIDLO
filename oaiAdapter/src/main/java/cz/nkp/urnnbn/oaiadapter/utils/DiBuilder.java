package cz.nkp.urnnbn.oaiadapter.utils;

import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.oaiadapter.czidlo.CzidloApiConnector;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Martin Řehánek on 28.10.17.
 */
public class DiBuilder {

    public static DigitalInstance buildDiFromImportDigitalInstanceRequest(Document requestDoc) {
        DigitalInstance di = new DigitalInstance();
        Nodes libraryIdNodes = requestDoc.query("/r:digitalInstance/r:digitalLibraryId", CzidloApiConnector.CONTEXT);
        if (libraryIdNodes.size() == 1) {
            di.setLibraryId(Long.valueOf(libraryIdNodes.get(0).getValue()));
        }
        Nodes urlNodes = requestDoc.query("/r:digitalInstance/r:url", CzidloApiConnector.CONTEXT);
        if (urlNodes.size() == 1) {
            di.setUrl(urlNodes.get(0).getValue());
        }
        Nodes formatNodes = requestDoc.query("/r:digitalInstance/r:format", CzidloApiConnector.CONTEXT);
        if (formatNodes.size() == 1) {
            di.setFormat(formatNodes.get(0).getValue());
        }
        Nodes accessibilityNodes = requestDoc.query("/r:digitalInstance/r:accessibility", CzidloApiConnector.CONTEXT);
        if (accessibilityNodes.size() == 1) {
            di.setAccessibility(accessibilityNodes.get(0).getValue());
        }
        return di;
    }

    public static List<DigitalInstance> buildDisFromGetDigitalInstancesByUrnNbn(Document responseDoc) {
        Nodes diNodes = responseDoc.query("/r:response/r:digitalInstances/r:digitalInstance", CzidloApiConnector.CONTEXT);
        List<DigitalInstance> result = new ArrayList<>(diNodes.size());
        for (int i = 0; i < diNodes.size(); i++) {
            DigitalInstance di = new DigitalInstance();
            Element diEl = (Element) diNodes.get(i);
            di.setId(Long.valueOf(diEl.getAttributeValue("id")));
            di.setLibraryId(Long.valueOf(diEl.query("r:digitalLibraryId", CzidloApiConnector.CONTEXT).get(0).getValue()));
            di.setUrl(diEl.query("r:url", CzidloApiConnector.CONTEXT).get(0).getValue());
            di.setActive(Boolean.valueOf(diEl.getAttributeValue("active")));
            Nodes formatNodes = diEl.query("r:format", CzidloApiConnector.CONTEXT);
            if (formatNodes.size() > 0) {
                di.setFormat(formatNodes.get(0).getValue().trim());
            }
            Nodes accessibilityNodes = diEl.query("r:accessibility", CzidloApiConnector.CONTEXT);
            if (accessibilityNodes.size() > 0) {
                di.setAccessibility(accessibilityNodes.get(0).getValue().trim());
            }
            Nodes createdNodes = diEl.query("r:created", CzidloApiConnector.CONTEXT);
            if (createdNodes.size() > 0) {
                di.setCreated(DateTime.parse(createdNodes.get(0).getValue()));
            }
            Nodes deactivatedNodes = diEl.query("r:deactivated", CzidloApiConnector.CONTEXT);
            if (deactivatedNodes.size() > 0) {
                di.setDeactivated(DateTime.parse(deactivatedNodes.get(0).getValue()));
            }
            result.add(di);
        }
        return result;
    }

}
