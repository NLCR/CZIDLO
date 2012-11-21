package cz.nkp.urnnbn.xml;

import cz.nkp.urnnbn.xml.commons.XOMUtils;
import java.io.File;
import java.io.IOException;
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
        //registration of digital documents
        File digDocRegistrationXsd = new File("/home/martin/NetBeansProjects/xml/src/main/resources/xsd/digDocRegistration.xsd.xml");
        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/request/importRecord/analytical.xml", digDocRegistrationXsd);
        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/request/importRecord/monograph-withUrnNbn.xml", digDocRegistrationXsd);
        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/request/importRecord/monograph.xml", digDocRegistrationXsd);
        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/request/importRecord/monographVolume.xml", digDocRegistrationXsd);
        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/request/importRecord/otherEntity-map.xml", digDocRegistrationXsd);
        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/request/importRecord/otherEntity-musicSheet.xml", digDocRegistrationXsd);
        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/request/importRecord/periodical.xml", digDocRegistrationXsd);
        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/request/importRecord/periodicalIssue.xml", digDocRegistrationXsd);
        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/request/importRecord/periodicalVolume.xml", digDocRegistrationXsd);
        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/request/importRecord/thesis.xml", digDocRegistrationXsd);

        //import digital instance
        File digInstImportXsd = new File("/home/martin/NetBeansProjects/xml/src/main/resources/xsd/digInstImport.xsd.xml");
        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/request/importDigitalInstance/complete.xml", digInstImportXsd);
        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/request/importDigitalInstance/minimal.xml", digInstImportXsd);
        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/request/importDigitalInstance/https.xml", digInstImportXsd);
        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/request/importDigitalInstance/emptyOptionalElements.xml", digInstImportXsd);

        //api responses
        File responseXsd = new File("/home/martin/NetBeansProjects/xml/src/main/resources/xsd/response.xsd.xml");
        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/response/error.xml", responseXsd);
        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/response/addDigitalInstance.xml", responseXsd);
        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/response/digitalInstance-deactivated.xml", responseXsd);
        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/response/digitalInstance-full.xml", responseXsd);
        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/response/digitalInstances.xml", responseXsd);
        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/response/digitalInstances-all.xml", responseXsd);
        //urn:nbn
        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/response/urnnbn-ACTIVE.xml", responseXsd);
        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/response/urnnbn-FREE.xml", responseXsd);
        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/response/urnnbn-RESERVED.xml", responseXsd);
        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/response/urnnbn-DEACTIVATED.xml", responseXsd);
        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/response/urnnbn-digDocRegResponse.xml", responseXsd);
        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/response/urnNbnReservations.xml", responseXsd);
        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/response/urnNbnReservation.xml", responseXsd);
        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/response/registrar.xml", responseXsd);
        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/response/registrars.xml", responseXsd);
        //digital document
        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/response/digitalDocuments.xml", responseXsd);
        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/response/digitalDocument-MONOGRAPH.xml", responseXsd);
        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/response/digitalDocument-MONOGRAPH_VOLUME.xml", responseXsd);
        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/response/digitalDocument-PERIODICAL.xml", responseXsd);
        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/response/digitalDocument-PERIODICAL_VOLUME.xml", responseXsd);
        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/response/digitalDocument-PERIODICAL_ISSUE.xml", responseXsd);
        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/response/digitalDocument-ANALYTICAL.xml", responseXsd);
        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/response/digitalDocument-THESIS.xml", responseXsd);
        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/response/digitalDocument-OTHER.xml", responseXsd);

        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/response/registrarScopeIdentifiers.xml", responseXsd);
        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/response/registrarScopeIdentifier.xml", responseXsd);
        validate("/home/martin/NetBeansProjects/xml/src/main/resources/xml/response/registrarScopeIdentifier-UPDATED.xml", responseXsd);

    }

    private static void validate(String docFileName, File xsdFile) {
        File docFile = new File(docFileName);
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
}
