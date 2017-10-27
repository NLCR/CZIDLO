package cz.nkp.urnnbn.oaiadapter.utils;

import java.util.logging.Logger;

import nu.xom.Document;
import nu.xom.Nodes;
import cz.nkp.urnnbn.oaiadapter.DigitalInstance;
import cz.nkp.urnnbn.oaiadapter.czidlo.CzidloApiConnector;

public class DiImportDataHelper {

    private static final Logger logger = Logger.getLogger(DiImportDataHelper.class.getName());

    private final Document diImportData;

    public DiImportDataHelper(Document diImportData) {
        this.diImportData = diImportData;
    }

    public DigitalInstance buildDi() {
        DigitalInstance di = new DigitalInstance();
        Nodes libraryIdNodes = diImportData.query("/r:digitalInstance/r:digitalLibraryId", CzidloApiConnector.CONTEXT);
        if (libraryIdNodes.size() == 1) {
            di.setDigitalLibraryId(Long.valueOf(libraryIdNodes.get(0).getValue()));
        }
        Nodes urlNodes = diImportData.query("/r:digitalInstance/r:url", CzidloApiConnector.CONTEXT);
        if (urlNodes.size() == 1) {
            di.setUrl(urlNodes.get(0).getValue());
        }
        Nodes formatNodes = diImportData.query("/r:digitalInstance/r:format", CzidloApiConnector.CONTEXT);
        if (formatNodes.size() == 1) {
            di.setFormat(formatNodes.get(0).getValue());
        }
        Nodes accessibilityNodes = diImportData.query("/r:digitalInstance/r:accessibility", CzidloApiConnector.CONTEXT);
        if (accessibilityNodes.size() == 1) {
            di.setAccessibility(accessibilityNodes.get(0).getValue());
        }
        return di;
    }

}
