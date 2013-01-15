package cz.nkp.urnnbn.xml;

import cz.nkp.urnnbn.xml.commons.XOMUtils;
import cz.nkp.urnnbn.xml.commons.XsltXmlTransformer;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import nu.xom.Document;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import nu.xom.XPathContext;
import nu.xom.xslt.XSLException;

/**
 * Hello world!
 *
 */
public class Main {

    public static void main(String[] args) {
        //xpathTest();
        //xsltTest();
        validateXmlExamples();
        //transformationTest();
    }

    private static void validateXmlExamples() {
        //TODO: move to the test class, rootDirs should be relative
        validateDigDocRegistrationApiV3Examples();
        validateDigitalInstanceImportApiV3Examples();
        validateResponseExamples();
        transformDigDocRegistrationV2ToV3WithValidation();
        transformDigInstImportV2ToV3WithValidation();
        validateOaiResponse();
    }

    private static void validateDigDocRegistrationApiV3Examples() {
        File xsd = new File("/home/martin/NetBeansProjects/xml/src/main/resources/xsd/digDocRegistration.xsd.xml");
        String rootDir = "/home/martin/NetBeansProjects/xml/src/main/resources/xml/request/registerDigitalDocument/";
        validate(rootDir + "analytical.xml", xsd);
        validate(rootDir + "monograph-withUrnNbn.xml", xsd);
        validate(rootDir + "monograph.xml", xsd);
        validate(rootDir + "monographVolume.xml", xsd);
        validate(rootDir + "otherEntity-map.xml", xsd);
        validate(rootDir + "otherEntity-musicSheet.xml", xsd);
        validate(rootDir + "periodical.xml", xsd);
        validate(rootDir + "periodicalIssue.xml", xsd);
        validate(rootDir + "periodicalVolume.xml", xsd);
        validate(rootDir + "thesis.xml", xsd);
    }

    private static void validateDigitalInstanceImportApiV3Examples() {
        File xsd = new File("/home/martin/NetBeansProjects/xml/src/main/resources/xsd/digInstImport.xsd.xml");
        String rootDir = "/home/martin/NetBeansProjects/xml/src/main/resources/xml/request/importDigitalInstance/";
        validate(rootDir + "complete.xml", xsd);
        validate(rootDir + "minimal.xml", xsd);
        validate(rootDir + "https.xml", xsd);
        validate(rootDir + "emptyOptionalElements.xml", xsd);
    }

    private static void validateResponseExamples() {
        File xsd = new File("/home/martin/NetBeansProjects/xml/src/main/resources/xsd/response.xsd.xml");
        String rootDir = "/home/martin/NetBeansProjects/xml/src/main/resources/xml/response/";
        //some error
        validate(rootDir + "error.xml", xsd);
        //import digital instance
        validate(rootDir + "addDigitalInstance.xml", xsd);
        //reserve 
        validate(rootDir + "reserveUrnNbns.xml", xsd);
        //get reservations
        validate(rootDir + "getUrnNbnReservations.xml", xsd);
        //get registrars
        validate(rootDir + "getRegistrars.xml", xsd);
        validate(rootDir + "getRegistrarsWithLibrariesAndCatalogs.xml", xsd);
        //get registrar
        validate(rootDir + "getRegistrar.xml", xsd);
        validate(rootDir + "getRegistrarsWithoutLibrariesAndCatalogs.xml", xsd);
        //get digital documents
        validate(rootDir + "getDigitalDocuments.xml", xsd);
        //get digital document
        validate(rootDir + "getDigDoc-MONOGRAPH.xml", xsd);
        validate(rootDir + "getDigDoc-MONOGRAPH_VOLUME.xml", xsd);
        validate(rootDir + "getDigDoc-PERIODICAL.xml", xsd);
        validate(rootDir + "getDigDoc-PERIODICAL_VOLUME.xml", xsd);
        validate(rootDir + "getDigDoc-PERIODICAL_ISSUE.xml", xsd);
        validate(rootDir + "getDigDoc-ANALYTICAL.xml", xsd);
        validate(rootDir + "getDigDoc-THESIS.xml", xsd);
        validate(rootDir + "getDigDoc-OTHER.xml", xsd);
        //get digital instances of digital document
        validate(rootDir + "getDigitalInstancesOfDigDoc.xml", xsd);
        //get registrar scope identifiers of digital document
        validate(rootDir + "getRegistrarScopeIdentfiersOfDigDoc.xml", xsd);
        //delete all registrar-scope identifiers of digital document
        validate(rootDir + "deleteRegistrarScopeIdentifiersOfDigDoc.xml", xsd);
        //get registrar-scope identifier value
        validate(rootDir + "getRegistrarScopeIdentifierValueOfDigDoc.xml", xsd);
        //set registrar-scope identifier value
        validate(rootDir + "postRegistrarScopeIdentifier-insert.xml", xsd);
        validate(rootDir + "postRegistrarScopeIdentifier-update.xml", xsd);
        //delete registrar-scope identifier
        validate(rootDir + "deleteRegistrarScopeIdenfier.xml", xsd);

        //get urn:nbn
        validate(rootDir + "getUrnNbn/free.xml", xsd);
        validate(rootDir + "getUrnNbn/reserved.xml", xsd);
        validate(rootDir + "getUrnNbn/active.xml", xsd);
        validate(rootDir + "getUrnNbn/active-formalyReserved.xml", xsd);
        validate(rootDir + "getUrnNbn/deactivated.xml", xsd);
        validate(rootDir + "getUrnNbn/deactivated-formalyReserved.xml", xsd);

        //get all digital instances
        validate(rootDir + "getAllDigitalInstances.xml", xsd);
        //get digital instance
        validate(rootDir + "getDigitalInstanceById.xml", xsd);
        validate(rootDir + "getDigitalInstanceById-deactivated.xml", xsd);
        //deactivate digital instance
        validate(rootDir + "deactivateDigitalInstance.xml", xsd);

        //response to registerDD
        validate(rootDir + "registerDigitalDocument/byResolver.xml", xsd);
        validate(rootDir + "registerDigitalDocument/byRegistrar.xml", xsd);
        validate(rootDir + "registerDigitalDocument/byReservation.xml", xsd);
    }

    private static void validate(String docFileName, File xsdFile) {
        validate(new File(docFileName), xsdFile);
    }

    private static void validate(File docFile, File xsdFile) {
        try {
            XOMUtils.loadDocumentValidByExternalXsd(docFile, xsdFile);
            System.out.println(docFile.getAbsolutePath() + " is valid");
        } catch (ValidityException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParsingException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void xpathTest() {
        try {
            File in = new File("/home/martin/NetBeansProjects/xml/src/main/java/cz/nkp/urnnbn/xml/examples/request/importDigitalInstance.xml");
            nu.xom.Document doc = XOMUtils.loadDocumentWithoutValidation(in);
            XPathContext context = new XPathContext("r", "http://resolver.nkp.cz/v3/");
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

    private static void transformDigDocRegistrationV2ToV3WithValidation() {
        try {
            String rootDir = "/home/martin/NetBeansProjects/xml/src/main/resources/xml/request/registerDigitalDocument/apiV2/";
            File xsltFile = new File("/home/martin/NetBeansProjects/xml/src/main/resources/xslt/digDocRegistrationV2ToV3.xsl");
            Document xsltDoc = XOMUtils.loadDocumentWithoutValidation(xsltFile);

            File digDocRegistrationV3Xsd = new File("/home/martin/NetBeansProjects/xml/src/main/resources/xsd/digDocRegistration.xsd.xml");
            File digDocRegistrationV2Xsd = new File("/home/martin/NetBeansProjects/xml/src/main/resources/xsd/apiV2/registerDigitalDocument.xsd");

            validateTransformValidate(rootDir + "monograph.xml", xsltDoc, digDocRegistrationV2Xsd, digDocRegistrationV3Xsd);
            validateTransformValidate(rootDir + "monograph-withUrnNbn.xml", xsltDoc, digDocRegistrationV2Xsd, digDocRegistrationV3Xsd);
            validateTransformValidate(rootDir + "monographVolume.xml", xsltDoc, digDocRegistrationV2Xsd, digDocRegistrationV3Xsd);
            validateTransformValidate(rootDir + "analytical.xml", xsltDoc, digDocRegistrationV2Xsd, digDocRegistrationV3Xsd);
            validateTransformValidate(rootDir + "periodical.xml", xsltDoc, digDocRegistrationV2Xsd, digDocRegistrationV3Xsd);
            validateTransformValidate(rootDir + "periodicalVolume.xml", xsltDoc, digDocRegistrationV2Xsd, digDocRegistrationV3Xsd);
            validateTransformValidate(rootDir + "periodicalIssue.xml", xsltDoc, digDocRegistrationV2Xsd, digDocRegistrationV3Xsd);
            validateTransformValidate(rootDir + "thesis.xml", xsltDoc, digDocRegistrationV2Xsd, digDocRegistrationV3Xsd);
            validateTransformValidate(rootDir + "otherEntity-map.xml", xsltDoc, digDocRegistrationV2Xsd, digDocRegistrationV3Xsd);
            validateTransformValidate(rootDir + "otherEntity-musicSheet.xml", xsltDoc, digDocRegistrationV2Xsd, digDocRegistrationV3Xsd);
        } catch (ParsingException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void transformDigInstImportV2ToV3WithValidation() {
        try {
            String rootDir = "/home/martin/NetBeansProjects/xml/src/main/resources/xml/request/importDigitalInstance/apiV2/";
            File xsltFile = new File("/home/martin/NetBeansProjects/xml/src/main/resources/xslt/digInstImportV2ToV3.xsl");
            Document xsltDoc = XOMUtils.loadDocumentWithoutValidation(xsltFile);

            File digInstImportV3Xsd = new File("/home/martin/NetBeansProjects/xml/src/main/resources/xsd/digInstImport.xsd.xml");
            File digInstImportV2Xsd = new File("/home/martin/NetBeansProjects/xml/src/main/resources/xsd/apiV2/importDigitalInstance.xsd");

            validateTransformValidate(rootDir + "complete.xml", xsltDoc, digInstImportV2Xsd, digInstImportV3Xsd);
            validateTransformValidate(rootDir + "https.xml", xsltDoc, digInstImportV2Xsd, digInstImportV3Xsd);
            validateTransformValidate(rootDir + "minimal.xml", xsltDoc, digInstImportV2Xsd, digInstImportV3Xsd);
        } catch (ParsingException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void validateOaiResponse() {
        String rootDir = "/home/martin/NetBeansProjects/xml/src/main/resources/xml/oai/";
        File resolverFormatXsd = new File("/home/martin/NetBeansProjects/xml/src/main/resources/xsd/response.xsd.xml");
        validate(rootDir + "response-resolver.xml", resolverFormatXsd);
    }

    private static void validateTransformValidate(String inDocFilename, Document xslt, File xsdBefore, File xsdAfter) {
        validate(inDocFilename, xsdBefore);
        File transformed = transform(inDocFilename, xslt);
        if (transformed != null) {
            validate(transformed.getAbsolutePath(), xsdAfter);
        }
    }

    private static File transform(String inDocFilename, Document xslt) {
        try {
            File inFile = new File(inDocFilename);
            Document inDoc = XOMUtils.loadDocumentWithoutValidation(inFile);
            Document transformedDoc = XOMUtils.transformDocument(inDoc, xslt);
            File transformedFile = new File(inDocFilename + "-transformed.xml");
            XOMUtils.saveDocumentToFile(transformedDoc, transformedFile);
            return transformedFile;
        } catch (XSLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (ParsingException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    static void transformationTest() {
        try {
            File inFile = new File("/home/martin/NetBeansProjects/xml/src/main/resources/xml/oai/response-resolver.xml");
            //File xsltFile = new File("/home/martin/NetBeansProjects/OaiPmhProvider/src/main/resources/resolverToOaiDc.xsd");
            File xsltFile = new File("/home/martin/NetBeansProjects/xml/src/main/resources/xslt/oai/resolverToOaiDc.xsl");
            Document srcDoc = XOMUtils.loadDocumentWithoutValidation(inFile);
            Document xslt = XOMUtils.loadDocumentWithoutValidation(xsltFile);
            XsltXmlTransformer transformer = new XsltXmlTransformer(xslt);
            //String transformed = transformer.transformToString(srcDoc);
            String transformed = transformer.transform(srcDoc).toXML();
            System.err.println("transformed: " + transformed);
        } catch (XSLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParsingException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
