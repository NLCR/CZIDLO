package cz.nkp.urnnbn.oaiadapter.utils;

import java.util.logging.Logger;

import nu.xom.Document;
import nu.xom.Nodes;
import cz.nkp.urnnbn.oaiadapter.DigitalInstance;
import cz.nkp.urnnbn.oaiadapter.czidlo.CzidloApiConnector;

public class DiApiResponseDocHelper {

    private static final Logger logger = Logger.getLogger(DiApiResponseDocHelper.class.getName());

    private final Document diDocFromApiRespons;

    public DiApiResponseDocHelper(Document diDocFromApiRespons) {
        this.diDocFromApiRespons = diDocFromApiRespons;
    }

    public DigitalInstance buildDi() {
        DigitalInstance di = new DigitalInstance();
        Nodes libraryIdNodes = diDocFromApiRespons.query("//r:digitalInstance/r:digitalLibrary/@id", CzidloApiConnector.CONTEXT);
        if (libraryIdNodes.size() == 1) {
            di.setDigitalLibraryId(Long.valueOf(libraryIdNodes.get(0).getValue()));
        }
        Nodes urlNodes = diDocFromApiRespons.query("//r:digitalInstance/r:url", CzidloApiConnector.CONTEXT);
        if (urlNodes.size() == 1) {
            di.setUrl(urlNodes.get(0).getValue());
        }
        Nodes formatNodes = diDocFromApiRespons.query("//r:digitalInstance/r:format", CzidloApiConnector.CONTEXT);
        if (formatNodes.size() == 1) {
            di.setFormat(formatNodes.get(0).getValue());
        }
        Nodes accessibilityNodes = diDocFromApiRespons.query("//r:digitalInstance/r:accessibility", CzidloApiConnector.CONTEXT);
        if (accessibilityNodes.size() == 1) {
            di.setAccessibility(accessibilityNodes.get(0).getValue());
        }
        Nodes idNodes = diDocFromApiRespons.query("//r:digitalInstance/@id", CzidloApiConnector.CONTEXT);
        if (idNodes.size() == 1) {
            di.setId(idNodes.get(0).getValue());
        }
        return di;
    }

}
