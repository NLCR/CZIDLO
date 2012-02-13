package cz.nkp.urnnbn.xml;

//import dom4jutilszbytky.Dom4jUtils;
//import dom4jutilszbytky.ValidationException;
import cz.nkp.urnnbn.xml.commons.XOMUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import nu.xom.XPathContext;

/**
 * Hello world!
 *
 */
public class Main {

    public static void main(String[] args) {
        //xpathTest();
        validateXml();
    }

    private static void validateXml() {
//        try {
////            File toValidate = new File("/home/martin/NetBeansProjects/xml/src/main/java/cz/nkp/urnnbn/xml/examples/response/registrar.xml");
////            File schema = new File("/home/martin/NetBeansProjects/xml/src/main/java/cz/nkp/urnnbn/xml/xsd/response.xsd.xml");
//          File toValidate = new File("/home/martin/NetBeansProjects/xml/src/main/java/cz/nkp/urnnbn/xml/examples/request/import.xml");
//            File schema = new File("/home/martin/NetBeansProjects/xml/src/main/java/cz/nkp/urnnbn/xml/xsd/request.xsd.xml");
//           
//            Dom4jUtils.validateBySchemaFromFile(toValidate, schema);
//            System.err.println("Document is valid");
//        } catch (ValidationException ex) {
//            //Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//            System.err.println("Document is not valid: " + ex.getMessage());
//        }
        try {
//            File toValidate = new File("/home/martin/NetBeansProjects/xml/src/main/java/cz/nkp/urnnbn/xml/examples/response/error.xml");
//            File schema = new File("/home/martin/NetBeansProjects/xml/src/main/java/cz/nkp/urnnbn/xml/xsd/response.xsd.xml");
            File toValidate = new File("/home/martin/NetBeansProjects/xml/src/main/java/cz/nkp/urnnbn/xml/examples/request/import-monograph.xml");
            String toValidateStr = XOMUtils.loadDocumentWithoutValidation(toValidate).toXML();

            File schema = new File("/home/martin/NetBeansProjects/xml/src/main/java/cz/nkp/urnnbn/xml/xsd/importRecord.xsd.xml");
           // FileInputStream schemaIn = new FileInputStream(schema);
            //String schemaStr = XOMUtils.loadDocumentValidByInternalXsd(schemaIn).toXML();
            String schemaStr = XOMUtils.loadDocumentWithoutValidation(schema).toXML();
            
            
            //System.err.println("schema: " + schemaStr);
            //schemaIn.close();
            //URL schemaUrl = new URL("http://iris.mzk.cz/cache/importRecord.xsd");

            //XOMUtils.loadDocumentValidByExternalXsd(toValidate, schema);
            XOMUtils.loadDocumentValidByExternalXsd(toValidateStr, schemaStr);
            //          XOMUtils.loadDocumentValidByExternalXsd(toValidate, schemaUrl);
            //XOMUtils.loadDocumentValidByInternalXsd(new File("/home/martin/NetBeansProjects/xml/src/main/java/cz/nkp/urnnbn/xml/examples/request/import_with_xsd.xml"));
            System.err.println("Document is valid");
        } catch (ValidityException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParsingException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

//    private static void xpathTest() {
//        try {
//            File in = new File("/home/martin/NetBeansProjects/xml/src/main/java/cz/nkp/urnnbn/xml/examples/request/importDigitalInstance.xml");
//            Document doc = Dom4jUtils.loadDocument(in, false);
//            XPath urlPath = Dom4jUtils.createXPath("resolver:digitalInstance/resolver:url");
//            String url = urlPath.selectSingleNode(doc).getText();
//            System.out.println("url:" + url);
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (DocumentException ex) {
//            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    private static void xpathTest() {
        try {
            File in = new File("/home/martin/NetBeansProjects/xml/src/main/java/cz/nkp/urnnbn/xml/examples/request/importDigitalInstance.xml");
            nu.xom.Document doc = XOMUtils.loadDocumentWithoutValidation(in);
            XPathContext context = new XPathContext("r", "http://resolver.nkp.cz/v2/");
            Nodes nodes = doc.query("r:digitalInstance/r:url", context);
            System.out.println("nodes:" + nodes.size());
            for (int i = 0; i < nodes.size(); i++) {
                nu.xom.Node node = nodes.get(i);
                System.out.println(node.toXML());
                System.out.println("value: " + node.getValue());
            }
        } catch (ParsingException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
