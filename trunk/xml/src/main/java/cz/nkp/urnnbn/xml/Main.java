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
        //validateXmlExamples();
        //transformationTest();
        testApiV2Responses();
        testApiV2ErrorResponses();
    }

    private static void validateXmlExamples() {
        //TODO: move to the test class, rootDirs should be relative
        validateDigDocRegistrationApiV3Examples();
        validateDigitalInstanceImportApiV3Examples();
        validateResponseExamples();
        validateErrorResponseExamples();
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
        //import digital instance
        validate(rootDir + "importDigitalInstance.xml", xsd);
        validate(rootDir + "importDigitalInstance-minimal.xml", xsd);
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
        validate(rootDir + "getDigDoc/getDigDoc-MONOGRAPH.xml", xsd);
        validate(rootDir + "getDigDoc/getDigDoc-MONOGRAPH_VOLUME.xml", xsd);
        validate(rootDir + "getDigDoc/getDigDoc-PERIODICAL.xml", xsd);
        validate(rootDir + "getDigDoc/getDigDoc-PERIODICAL_VOLUME.xml", xsd);
        validate(rootDir + "getDigDoc/getDigDoc-PERIODICAL_ISSUE.xml", xsd);
        validate(rootDir + "getDigDoc/getDigDoc-ANALYTICAL.xml", xsd);
        validate(rootDir + "getDigDoc/getDigDoc-THESIS.xml", xsd);
        validate(rootDir + "getDigDoc/getDigDoc-OTHER.xml", xsd);
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
        validate(rootDir + "deleteRegistrarScopeIdentifier.xml", xsd);

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
        validate(rootDir + "deactivateDigitalInstance-minimal.xml", xsd);

        //response to registerDD
        validate(rootDir + "registerDigitalDocument/byResolver.xml", xsd);
        validate(rootDir + "registerDigitalDocument/byRegistrar.xml", xsd);
        validate(rootDir + "registerDigitalDocument/byReservation.xml", xsd);
    }

    private static void validateErrorResponseExamples() {
        File xsd = new File("/home/martin/NetBeansProjects/xml/src/main/resources/xsd/response.xsd.xml");
        String rootDir = "/home/martin/NetBeansProjects/xml/src/main/resources/xml/response/errors/";
        validate(rootDir + "digitalInstanceAlreadyPresent.xml", xsd);
        validate(rootDir + "invalidDigitalDocumentIdType.xml", xsd);
        validate(rootDir + "invalidUrnNbn.xml", xsd);
        validate(rootDir + "unknownDigitalInstance.xml", xsd);
        validate(rootDir + "incorrectPredecessorFree.xml", xsd);
        validate(rootDir + "invalidDigitalInstanceId.xml", xsd);
        validate(rootDir + "notAuthorized.xml", xsd);
        validate(rootDir + "unknownDigitalLibrary.xml", xsd);
        validate(rootDir + "incorrectPredecessorReserved.xml", xsd);
        validate(rootDir + "invalidQueryParamValue.xml", xsd);
        validate(rootDir + "registrarScopeIdValueNotDefined.xml", xsd);
        validate(rootDir + "unknownRegistrar.xml", xsd);
        validate(rootDir + "invalidArchiverId.xml", xsd);
        validate(rootDir + "invalidRegistrarCode.xml", xsd);
        validate(rootDir + "unauthorizedRegistrationMode.xml", xsd);
        validate(rootDir + "unknownUrnNbn.xml", xsd);
        validate(rootDir + "invalidData.xml", xsd);
        validate(rootDir + "invalidRegistrarScopeIdentifier.xml", xsd);
        validate(rootDir + "unknownDigitalDocument.xml", xsd);
        validate(rootDir + "urnNbnDeactivated.xml", xsd);
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
            File xsltFile = new File("/home/martin/NetBeansProjects/xml/src/main/resources/xslt/v2ToV3Request/digDocRegistrationV2ToV3.xsl");
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
            File xsltFile = new File("/home/martin/NetBeansProjects/xml/src/main/resources/xslt/v2ToV3Request/digInstImportV2ToV3.xsl");
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

    private static File transform(String inDocFilename, String xsltFilename, String suffix) throws ParsingException, IOException {
        System.out.println("xslt: " + xsltFilename);
        System.out.println("doc: " + inDocFilename);
        Document xsltDoc = XOMUtils.loadDocumentWithoutValidation(new File(xsltFilename));
        return transform(inDocFilename, xsltDoc, suffix);
    }

    private static File transform(String inDocFilename, Document xslt) {
        return transform(inDocFilename, xslt, null);
    }

    private static File transform(String inDocFilename, Document xslt, String suffix) {
        try {
            if (suffix == null) {
                suffix = "transformed";
            }
            File inFile = new File(inDocFilename);
            Document inDoc = XOMUtils.loadDocumentWithoutValidation(inFile);
            Document transformedDoc = XOMUtils.transformDocument(inDoc, xslt);
            File transformedFile = new File(removeXmlSuffix(inDocFilename) + '-' + suffix + ".xml");
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

    private static void testApiV2Responses() {
        try {
            String xmlRootDir = "/home/martin/NetBeansProjects/xml/src/main/resources/xml/response/";
            String xsltRootDir = "/home/martin/NetBeansProjects/xml/src/main/resources/xslt/v3ToV2Response/";
            transform(xmlRootDir + "getRegistrar.xml", xsltRootDir + "getRegistrar.xsl", "v2");
            transform(xmlRootDir + "getRegistrarWithLibrariesAndCatalogs.xml", xsltRootDir + "getRegistrar.xsl", "v2");
            transform(xmlRootDir + "getRegistrars.xml", xsltRootDir + "getRegistrars.xsl", "v2");
            transform(xmlRootDir + "getRegistrarsWithLibrariesAndCatalogs.xml", xsltRootDir + "getRegistrars.xsl", "v2");
            transform(xmlRootDir + "getUrnNbnReservations.xml", xsltRootDir + "getUrnNbnReservations.xsl", "v2");
            transform(xmlRootDir + "reserveUrnNbns.xml", xsltRootDir + "reserveUrnNbnBlock.xsl", "v2");
            transform(xmlRootDir + "getDigitalDocuments.xml", xsltRootDir + "getDigitalDocuments.xsl", "v2");
            
            transform(xmlRootDir + "getDigDoc/getDigDoc-MONOGRAPH.xml", xsltRootDir + "getDigitalDocument.xsl", "v2");
            transform(xmlRootDir + "getDigDoc/getDigDoc-MONOGRAPH_VOLUME.xml", xsltRootDir + "getDigitalDocument.xsl", "v2");
            transform(xmlRootDir + "getDigDoc/getDigDoc-PERIODICAL.xml", xsltRootDir + "getDigitalDocument.xsl", "v2");
            transform(xmlRootDir + "getDigDoc/getDigDoc-PERIODICAL_VOLUME.xml", xsltRootDir + "getDigitalDocument.xsl", "v2");
            transform(xmlRootDir + "getDigDoc/getDigDoc-PERIODICAL_ISSUE.xml", xsltRootDir + "getDigitalDocument.xsl", "v2");
            transform(xmlRootDir + "getDigDoc/getDigDoc-ANALYTICAL.xml", xsltRootDir + "getDigitalDocument.xsl", "v2");
            transform(xmlRootDir + "getDigDoc/getDigDoc-THESIS.xml", xsltRootDir + "getDigitalDocument.xsl", "v2");
            transform(xmlRootDir + "getDigDoc/getDigDoc-OTHER.xml", xsltRootDir + "getDigitalDocument.xsl", "v2");
            
            transform(xmlRootDir + "getRegistrarScopeIdentfiersOfDigDoc.xml", xsltRootDir + "getRegistrarScopeIdentifiers.xsl", "v2");
            transform(xmlRootDir + "getRegistrarScopeIdentifierValueOfDigDoc.xml", xsltRootDir + "getRegistrarScopeIdentifierValue.xsl", "v2");
            transform(xmlRootDir + "deleteRegistrarScopeIdentifier.xml", xsltRootDir + "deleteRegistrarScopeIdentifier.xsl", "v2");
            transform(xmlRootDir + "deleteRegistrarScopeIdentifiersOfDigDoc.xml", xsltRootDir + "deleteRegistrarScopeIdentifiers.xsl", "v2");
            transform(xmlRootDir + "postRegistrarScopeIdentifier-insert.xml", xsltRootDir + "setOrUpdateRegistrarScopeIdentifier.xsl", "v2");
            transform(xmlRootDir + "postRegistrarScopeIdentifier-update.xml", xsltRootDir + "setOrUpdateRegistrarScopeIdentifier.xsl", "v2");
            transform(xmlRootDir + "getDigitalInstances-global.xml", xsltRootDir + "getDigitalInstances.xsl", "v2");
            transform(xmlRootDir + "getDigitalInstancesOfDigDoc.xml", xsltRootDir + "getDigitalInstances.xsl", "v2");
            transform(xmlRootDir + "getDigitalInstanceById.xml", xsltRootDir + "getDigitalInstance.xsl", "v2");
            transform(xmlRootDir + "getDigitalInstanceById-deactivated.xml", xsltRootDir + "getDigitalInstance.xsl", "v2");
            transform(xmlRootDir + "importDigitalInstance.xml", xsltRootDir + "importDigitalInstance.xsl", "v2");
            transform(xmlRootDir + "importDigitalInstance-minimal.xml", xsltRootDir + "importDigitalInstance.xsl", "v2");
            transform(xmlRootDir + "deactivateDigitalInstance.xml", xsltRootDir + "deactivateDigitalInstance.xsl", "v2");
            transform(xmlRootDir + "deactivateDigitalInstance-minimal.xml", xsltRootDir + "deactivateDigitalInstance.xsl", "v2");
            transform(xmlRootDir + "getUrnNbn/active.xml", xsltRootDir + "getUrnNbn.xsl", "v2");
            transform(xmlRootDir + "getUrnNbn/active-formalyReserved.xml", xsltRootDir + "getUrnNbn.xsl", "v2");
            transform(xmlRootDir + "getUrnNbn/deactivated.xml", xsltRootDir + "getUrnNbn.xsl", "v2");
            transform(xmlRootDir + "getUrnNbn/deactivated-formalyReserved.xml", xsltRootDir + "getUrnNbn.xsl", "v2");
            transform(xmlRootDir + "getUrnNbn/free.xml", xsltRootDir + "getUrnNbn.xsl", "v2");
            transform(xmlRootDir + "getUrnNbn/reserved.xml", xsltRootDir + "getUrnNbn.xsl", "v2");
            transform(xmlRootDir + "registerDigitalDocument/byResolver.xml", xsltRootDir + "registerDigitalDocument.xsl", "v2");
            transform(xmlRootDir + "registerDigitalDocument/byReservation.xml", xsltRootDir + "registerDigitalDocument.xsl", "v2");
            transform(xmlRootDir + "registerDigitalDocument/byRegistrar.xml", xsltRootDir + "registerDigitalDocument.xsl", "v2");
        } catch (ParsingException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void testApiV2ErrorResponses() {
        try {
            String xmlRootDir = "/home/martin/NetBeansProjects/xml/src/main/resources/xml/response/errors/";
            String xsltFilename = "/home/martin/NetBeansProjects/xml/src/main/resources/xslt/v3ToV2Response/error.xsl";
            transform(xmlRootDir + "digitalInstanceAlreadyPresent.xml", xsltFilename, "v2");
            transform(xmlRootDir + "invalidDigitalDocumentIdType.xml", xsltFilename, "v2");
            transform(xmlRootDir + "invalidUrnNbn.xml", xsltFilename, "v2");
            transform(xmlRootDir + "unknownDigitalInstance.xml", xsltFilename, "v2");
            transform(xmlRootDir + "incorrectPredecessorFree.xml", xsltFilename, "v2");
            transform(xmlRootDir + "invalidDigitalInstanceId.xml", xsltFilename, "v2");
            transform(xmlRootDir + "notAuthorized.xml", xsltFilename, "v2");
            transform(xmlRootDir + "unknownDigitalLibrary.xml", xsltFilename, "v2");
            transform(xmlRootDir + "incorrectPredecessorReserved.xml", xsltFilename, "v2");
            transform(xmlRootDir + "invalidQueryParamValue.xml", xsltFilename, "v2");
            transform(xmlRootDir + "registrarScopeIdValueNotDefined.xml", xsltFilename, "v2");
            transform(xmlRootDir + "unknownRegistrar.xml", xsltFilename, "v2");
            transform(xmlRootDir + "invalidArchiverId.xml", xsltFilename, "v2");
            transform(xmlRootDir + "invalidRegistrarCode.xml", xsltFilename, "v2");
            transform(xmlRootDir + "unauthorizedRegistrationMode.xml", xsltFilename, "v2");
            transform(xmlRootDir + "unknownUrnNbn.xml", xsltFilename, "v2");
            transform(xmlRootDir + "invalidData.xml", xsltFilename, "v2");
            transform(xmlRootDir + "invalidRegistrarScopeIdentifier.xml", xsltFilename, "v2");
            transform(xmlRootDir + "unknownDigitalDocument.xml", xsltFilename, "v2");
            transform(xmlRootDir + "urnNbnDeactivated.xml", xsltFilename, "v2");
        } catch (ParsingException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static String removeXmlSuffix(String inDocFilename) {
        if (inDocFilename.toLowerCase().endsWith(".xml")) {
            return inDocFilename.substring(0, inDocFilename.length() - ".xml".length());
        } else {
            return inDocFilename;
        }
    }
}
