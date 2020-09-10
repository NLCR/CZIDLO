package cz.nkp.urnnbn.xml;

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
import cz.nkp.urnnbn.xml.commons.Namespaces;
import cz.nkp.urnnbn.xml.commons.XOMUtils;
import cz.nkp.urnnbn.xml.commons.XsltXmlTransformer;

/**
 * Hello world!
 */
public class Main {

    public static void main(String[] args) {
        // xpathTest();
        // xsltTest();
        // transformationTest();
        validateXmlExamples();
    }

    private static void validateXmlExamples() {
        // TODO: move to API test
        // TODO: move to the test class
        validateDigDocRegistrationApiV3Examples();
        validateDigitalInstanceImportApiV3Examples();
        validateResponseExamples();
        validateErrorResponseExamples();
        validateOaiResponse();
    }

    private static void validateDigDocRegistrationApiV3Examples() {
        File xsd = new File("xml/src/main/resources/xsd/digDocRegistration.xsd.xml");
        String rootDir = "xml/src/main/resources/xml/request/registerDigitalDocument/";
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
        File xsd = new File("xml/src/main/resources/xsd/digInstImport.xsd.xml");
        String rootDir = "xml/src/main/resources/xml/request/importDigitalInstance/";
        validate(rootDir + "complete.xml", xsd);
        validate(rootDir + "minimal.xml", xsd);
        validate(rootDir + "https.xml", xsd);
        validate(rootDir + "emptyOptionalElements.xml", xsd);
    }

    private static void validateResponseExamples() {
        File xsd = new File("xml/src/main/resources/xsd/response.xsd.xml");
        String rootDir = "xml/src/main/resources/xml/response/";
        // import digital instance
        validate(rootDir + "importDigitalInstance.xml", xsd);
        validate(rootDir + "importDigitalInstance-minimal.xml", xsd);
        // reserve
        validate(rootDir + "reserveUrnNbns.xml", xsd);
        // get reservations
        validate(rootDir + "getUrnNbnReservations.xml", xsd);
        // get registrars
        validate(rootDir + "getRegistrars.xml", xsd);
        validate(rootDir + "getRegistrarsWithLibrariesAndCatalogs.xml", xsd);
        // get registrar
        validate(rootDir + "getRegistrar.xml", xsd);
        validate(rootDir + "getRegistrarsWithoutLibrariesAndCatalogs.xml", xsd);
        // get digital documents
        validate(rootDir + "getDigitalDocuments.xml", xsd);
        // get digital document
        validate(rootDir + "getDigDoc/getDigDoc-MONOGRAPH.xml", xsd);
        validate(rootDir + "getDigDoc/getDigDoc-MONOGRAPH_VOLUME.xml", xsd);
        validate(rootDir + "getDigDoc/getDigDoc-PERIODICAL.xml", xsd);
        validate(rootDir + "getDigDoc/getDigDoc-PERIODICAL_VOLUME.xml", xsd);
        validate(rootDir + "getDigDoc/getDigDoc-PERIODICAL_ISSUE.xml", xsd);
        validate(rootDir + "getDigDoc/getDigDoc-ANALYTICAL.xml", xsd);
        validate(rootDir + "getDigDoc/getDigDoc-THESIS.xml", xsd);
        validate(rootDir + "getDigDoc/getDigDoc-OTHER.xml", xsd);
        // get digital instances of digital document
        validate(rootDir + "getDigitalInstancesOfDigDoc.xml", xsd);
        // get registrar scope identifiers of digital document
        validate(rootDir + "getRegistrarScopeIdentfiersOfDigDoc.xml", xsd);
        // delete all registrar-scope identifiers of digital document
        validate(rootDir + "deleteRegistrarScopeIdentifiersOfDigDoc.xml", xsd);
        // get registrar-scope identifier value
        validate(rootDir + "getRegistrarScopeIdentifierValueOfDigDoc.xml", xsd);
        // set registrar-scope identifier value
        validate(rootDir + "postRegistrarScopeIdentifier-insert.xml", xsd);
        validate(rootDir + "postRegistrarScopeIdentifier-update.xml", xsd);
        // delete registrar-scope identifier
        validate(rootDir + "deleteRegistrarScopeIdentifier.xml", xsd);

        // get urn:nbn
        validate(rootDir + "getUrnNbn/free.xml", xsd);
        validate(rootDir + "getUrnNbn/reserved.xml", xsd);
        validate(rootDir + "getUrnNbn/active.xml", xsd);
        validate(rootDir + "getUrnNbn/active-formalyReserved.xml", xsd);
        validate(rootDir + "getUrnNbn/deactivated.xml", xsd);
        validate(rootDir + "getUrnNbn/deactivated-formalyReserved.xml", xsd);

        // get all digital instances
        validate(rootDir + "getAllDigitalInstances.xml", xsd);
        // get digital instance
        validate(rootDir + "getDigitalInstanceById.xml", xsd);
        validate(rootDir + "getDigitalInstanceById-deactivated.xml", xsd);
        // deactivate digital instance
        validate(rootDir + "deactivateDigitalInstance.xml", xsd);
        validate(rootDir + "deactivateDigitalInstance-minimal.xml", xsd);

        // response to registerDD
        validate(rootDir + "registerDigitalDocument/byResolver.xml", xsd);
        validate(rootDir + "registerDigitalDocument/byRegistrar.xml", xsd);
        validate(rootDir + "registerDigitalDocument/byReservation.xml", xsd);
    }

    private static void validateErrorResponseExamples() {
        File xsd = new File("xml/src/main/resources/xsd/response.xsd.xml");
        String rootDir = "xml/src/main/resources/xml/response/errors/";
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
            File in = new File("xml/src/main/java/cz/nkp/urnnbn/xml/examples/request/importDigitalInstance.xml");
            nu.xom.Document doc = XOMUtils.loadDocumentWithoutValidation(in);
            XPathContext context = new XPathContext("r", Namespaces.CZIDLO_V3_NS);
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

    private static void validateOaiResponse() {
        String rootDir = "xml/src/main/resources/xml/oai/";
        File resolverFormatXsd = new File("xml/src/main/resources/xsd/response.xsd.xml");
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
            File inFile = new File("xml/src/main/resources/xml/oai/response-resolver.xml");
            File xsltFile = new File("xml/src/main/resources/xslt/oai/czidloToOaiDc.xsl");
            Document srcDoc = XOMUtils.loadDocumentWithoutValidation(inFile);
            Document xslt = XOMUtils.loadDocumentWithoutValidation(xsltFile);
            XsltXmlTransformer transformer = new XsltXmlTransformer(xslt);
            // String transformed = transformer.transformToString(srcDoc);
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

    private static String removeXmlSuffix(String inDocFilename) {
        if (inDocFilename.toLowerCase().endsWith(".xml")) {
            return inDocFilename.substring(0, inDocFilename.length() - ".xml".length());
        } else {
            return inDocFilename;
        }
    }
}
