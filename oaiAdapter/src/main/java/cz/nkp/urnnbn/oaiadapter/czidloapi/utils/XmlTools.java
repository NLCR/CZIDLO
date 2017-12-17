/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter.czidloapi.utils;

import cz.nkp.urnnbn.core.AccessRestriction;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.oaiadapter.czidloapi.CzidloApiConnector;
import cz.nkp.urnnbn.oaiadapter.czidloapi.CzidloApiError;
import cz.nkp.urnnbn.xml.commons.XOMUtils;
import nu.xom.*;
import nu.xom.xslt.XSLException;
import nu.xom.xslt.XSLTransform;
import org.joda.time.DateTime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jan Rychtář
 * @author Martin Řehánek
 */
public class XmlTools {

    public static Document parseDocumentFromString(String docStr) throws ParsingException, IOException, XSLException {
        Builder builder = new Builder();
        Document document = builder.build(docStr, null);
        return document;
    }

    public static void saveDocumentToFile(Document document, String path) throws IOException {
        File f = new File(path);
        f.getParentFile().mkdirs();
        FileOutputStream out = new FileOutputStream(path);
        Serializer ser = new Serializer(out, "UTF-8");
        ser.setIndent(2);
        ser.write(document);
    }

    public static Document getTransformedDocument(Document input, Document stylesheet) throws XSLException {
        XSLTransform transform = new XSLTransform(stylesheet);
        Nodes output = transform.transform(input);
        Document result = XSLTransform.toDocument(output);
        return result;
    }

    public static void validateByXsdAsString(Document document, String xsd) throws ParsingException, IOException {
        XOMUtils.loadDocumentValidByExternalXsd(document.toXML(), xsd);
        /*try {
            XOMUtils.loadDocumentValidByExternalXsd(document.toXML(), xsd);
        } catch (Exception ex) {
            throw new DocumentOperationException(ex.getMessage());
        }*/
    }

    public static boolean nodeByXpathExists(Document doc, String xpath) {
        Nodes nodes = doc.query(xpath, CzidloApiConnector.CONTEXT);
        return nodes.size() > 0;
    }

    public static String loadXmlFromFile(String xsltFile) throws Exception {
        try {
            Builder builder = new Builder();
            Document importStylesheet = builder.build(xsltFile);
            return importStylesheet.toXML();
        } catch (ParsingException ex) {
            throw new Exception("error parsing " + xsltFile, ex);
        } catch (IOException ex) {
            throw new Exception("error loading " + xsltFile, ex);
        }
    }

    public CzidloApiError parseErrorMessage(Document document) throws IOException, ParsingException {
        Element rootElement = document.getRootElement();
        Nodes codeNodes = rootElement.query("//r:error/r:code", CzidloApiConnector.CONTEXT);
        String code = "";
        if (codeNodes.size() == 1) {
            code = codeNodes.get(0).getValue();
        }
        Nodes messageNodes = rootElement.query("//r:error/r:message", CzidloApiConnector.CONTEXT);
        String message = "";
        if (messageNodes.size() == 1) {
            message = messageNodes.get(0).getValue();
        }
        if (code.isEmpty() && message.isEmpty()) {
            return null;
        }
        return new CzidloApiError(code, message);
    }

    public DigitalInstance buildDiFromImportDigitalInstanceRequest(Document requestDoc) {
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
        Nodes accessRestrictionNodes = requestDoc.query("/r:digitalInstance/r:accessRestriction", CzidloApiConnector.CONTEXT);
        if (accessRestrictionNodes.size() == 1) {
            di.setAccessRestriction(AccessRestriction.valueOf(accessRestrictionNodes.get(0).getValue()));
        } else {
            di.setAccessRestriction(AccessRestriction.UNKNOWN);
        }
        return di;
    }

    public List<DigitalInstance> buildDisFromGetDigitalInstancesByUrnNbn(Document responseDoc) {
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
            Nodes accessRestrictionNodes = diEl.query("r:accessRestriction", CzidloApiConnector.CONTEXT);
            if (accessRestrictionNodes.size() > 0) {
                di.setAccessRestriction(AccessRestriction.valueOf(accessRestrictionNodes.get(0).getValue()));
            } else {
                di.setAccessRestriction(AccessRestriction.UNKNOWN);
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
