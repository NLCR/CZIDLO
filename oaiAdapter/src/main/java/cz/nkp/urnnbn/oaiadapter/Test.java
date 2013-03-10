/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter;

import cz.nkp.urnnbn.oaiadapter.resolver.ResolverConnectionException;
import cz.nkp.urnnbn.oaiadapter.resolver.ResolverConnector;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;

/**
 *
 * @author hanis
 */
public class Test {
    
    private ResolverConnector resolverConnector = new ResolverConnector("resolver-test2.nkp.cz/api");

    public void makeReservation() {
        try {
            List<String> reserveUrnnbnBundle = resolverConnector.reserveUrnnbnBundle("rych01", 5, Credentials.LOGIN, Credentials.PASSWORD);
            for (String string : reserveUrnnbnBundle) {
                System.out.println(string);
            }
        } catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ResolverConnectionException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParsingException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void importDI() {
        try {

            String urn = "urn:nbn:cz:tsh01-0000qv";
            String di = "/home/hanis/prace/resolver/oai/parser-test/docs/digitalInstance3.xml";

            String registrator = "tsh03";
            Builder builder = new Builder();
            Document digitalInstance = builder.build(new File(di));
            resolverConnector.importDigitalInstance(digitalInstance, urn, Credentials.LOGIN, Credentials.PASSWORD);
        } catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParsingException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ResolverConnectionException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void importDDwithDI() {
        try {
            String dd = "/home/hanis/prace/resolver/oai/parser-test/docs/digitalDocument.xml";
            String di = "/home/hanis/prace/resolver/oai/parser-test/docs/digitalInstance4.xml";
            String registrator = "rych01";
            String oaiIdentifier = "myCollectionx:myIdi23";

            Builder builder = new Builder();
            Document digitalDocument = builder.build(new File(dd));
            Document digitalInstance = builder.build(new File(di));

            OaiAdapter oaiAdapter = new OaiAdapter();
            oaiAdapter.setLogin(Credentials.LOGIN);
            oaiAdapter.setPassword(Credentials.PASSWORD);
            oaiAdapter.setRegistrarCode(registrator);
            //oaiAdapter.setMode(OaiAdapter.Mode.BY_REGISTRAR);
            oaiAdapter.setMode(OaiAdapter.Mode.BY_RESERVATION);
            //oaiAdapter.setMode(OaiAdapter.Mode.BY_RESERVATION);

            oaiAdapter.processSingleDocument(oaiIdentifier, digitalDocument, digitalInstance);
        } catch (ResolverConnectionException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (OaiAdapterException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParsingException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //public void 
    public static void main(String[] args) {



        Test test = new Test();

        //  test.makeReservation();


//urn:nbn:cz:rych01-000000
//urn:nbn:cz:rych01-000001
//urn:nbn:cz:rych01-000002
//urn:nbn:cz:rych01-000003
//urn:nbn:cz:rych01-000004            
        // test.importDDwithDI();

//            String rc = "roe301";
//            System.out.println("BY_REGISTRAR: " + ResolverConnector.checkRegistrarMode(rc, OaiAdapter.Mode.BY_REGISTRAR));
//            System.out.println("BY_RESOLVER: " + ResolverConnector.checkRegistrarMode(rc, OaiAdapter.Mode.BY_RESOLVER));
//            System.out.println("RESERVATION: " + ResolverConnector.checkRegistrarMode(rc, OaiAdapter.Mode.BY_RESERVATION));

        /// System.out.println(ResolverConnector.getDigitailInstanceById("35747").toXML());

        //  test.importDI();
//        try {    
//            ResolverConnector.removeDigitalInstance("35747", Credentials.LOGIN, Credentials.PASSWORD);
//        } catch (IOException ex) {
//            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (ResolverConnectionException ex) {
//            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
//        }

//            RESERVED
//                urn:nbn:cz:tsh01-0000qz
//                urn:nbn:cz:tsh01-0000r0
//                urn:nbn:cz:tsh01-0000r1
//                urn:nbn:cz:tsh01-0000r2
//                urn:nbn:cz:tsh01-0000r3

    }
}
