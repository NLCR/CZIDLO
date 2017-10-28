package cz.nkp.urnnbn.oaiadapter.utils;

import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.oaiadapter.czidlo.CzidloApiConnector;
import nu.xom.Document;
import nu.xom.Nodes;

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

    public static DigitalInstance buildDiFromGetDigitalInstanceByLibraryIdResponse(Document responseDoc) {
        DigitalInstance di = new DigitalInstance();
        Nodes libraryIdNodes = responseDoc.query("//r:digitalInstance/r:digitalLibrary/@id", CzidloApiConnector.CONTEXT);
        if (libraryIdNodes.size() == 1) {
            di.setLibraryId(Long.valueOf(libraryIdNodes.get(0).getValue()));
        }
        Nodes urlNodes = responseDoc.query("//r:digitalInstance/r:url", CzidloApiConnector.CONTEXT);
        if (urlNodes.size() == 1) {
            di.setUrl(urlNodes.get(0).getValue());
        }
        Nodes formatNodes = responseDoc.query("//r:digitalInstance/r:format", CzidloApiConnector.CONTEXT);
        if (formatNodes.size() == 1) {
            di.setFormat(formatNodes.get(0).getValue());
        }
        Nodes accessibilityNodes = responseDoc.query("//r:digitalInstance/r:accessibility", CzidloApiConnector.CONTEXT);
        if (accessibilityNodes.size() == 1) {
            di.setAccessibility(accessibilityNodes.get(0).getValue());
        }
        Nodes idNodes = responseDoc.query("//r:digitalInstance/@id", CzidloApiConnector.CONTEXT);
        if (idNodes.size() == 1) {
            di.setId(Long.valueOf(idNodes.get(0).getValue()));
        }
        return di;
    }


}
