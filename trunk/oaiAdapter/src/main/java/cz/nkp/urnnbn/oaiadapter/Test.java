/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter;

import cz.nkp.urnnbn.oaiadapter.utils.XmlTools;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

/**
 *
 * @author hanis
 */
public class Test {

    public void makeReservation() {
        try {
            List<String> reserveUrnnbnBundle = ResolverConnector.reserveUrnnbnBundle("tsh01", 5, Credentials.LOGIN, Credentials.PASSWORD);
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
            ResolverConnector.importDigitalInstance(digitalInstance, urn, Credentials.LOGIN, Credentials.PASSWORD);
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
            String di = "/home/hanis/prace/resolver/oai/parser-test/docs/digitalInstance2.xml";
            String registrator = "tsh02";
            String oaiIdentifier = "myCollection:myId";

            Builder builder = new Builder();
            Document digitalDocument = builder.build(new File(dd));
            Document digitalInstance = builder.build(new File(di));

            OaiAdapter oaiAdapter = new OaiAdapter();
            oaiAdapter.setLogin(Credentials.LOGIN);
            oaiAdapter.setPassword(Credentials.PASSWORD);
            oaiAdapter.setRegistrarCode(registrator);
            oaiAdapter.setMode(OaiAdapter.Mode.BY_RESOLVER);

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
            //test.makeReservation();
            test.importDDwithDI();
          //  test.importDI();
//        try {    
//            ResolverConnector.removeDigitalInstance("35747", Credentials.LOGIN, Credentials.PASSWORD);
//        } catch (IOException ex) {
//            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (ResolverConnectionException ex) {
//            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
//        }

    }
}
