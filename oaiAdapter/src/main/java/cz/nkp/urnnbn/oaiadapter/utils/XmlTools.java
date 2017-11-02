/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter.utils;

import cz.nkp.urnnbn.oaiadapter.DocumentOperationException;
import cz.nkp.urnnbn.oaiadapter.czidlo.CzidloApiConnector;
import cz.nkp.urnnbn.oaiadapter.czidlo.CzidloApiError;
import cz.nkp.urnnbn.xml.commons.XOMUtils;
import nu.xom.*;
import nu.xom.xslt.XSLException;
import nu.xom.xslt.XSLTransform;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author hanis
 */
public class XmlTools {


    public static Document getTemplateDocumentFromString(String template) throws ParsingException, IOException, XSLException {
        Builder builder = new Builder();
        Document document = builder.build(template, null);
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

    public static void validateByXsdAsString(Document document, String xsd) throws DocumentOperationException {
        try {
            XOMUtils.loadDocumentValidByExternalXsd(document.toXML(), xsd);
        } catch (Exception ex) {
            throw new DocumentOperationException(ex.getMessage());
        }
    }

    public static boolean nodeByXpathExists(Document doc, String xpath) throws DocumentOperationException {
        try {
            Nodes nodes = doc.query(xpath, CzidloApiConnector.CONTEXT);
            return nodes.size() > 0;
        } catch (Throwable ex) {
            throw new DocumentOperationException(ex.getMessage());
        }
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

}
