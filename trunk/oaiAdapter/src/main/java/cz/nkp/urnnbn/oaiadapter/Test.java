/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter;

import cz.nkp.urnnbn.oaiadapter.utils.XmlTools;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import nu.xom.xslt.XSLException;
import org.xml.sax.SAXException;

/**
 *
 * @author hanis
 */
public class Test {

    public static void main(String[] args) {
        try {
            Builder builder = new Builder();
            //Logger.getLogger(Test.class.getName()).log(Level.INFO, "Nejaka zprava");
            //Document doc = XmlTools.getDocument("http://oai.mzk.cz/?verb=GetRecord&metadataPrefix=marc21&identifier=oai:aleph.mzk.cz:MZK03-001056568");
            //Document doc = XmlTools.getDocument("http://kramerius.mzk.cz/oaiprovider/?verb=GetRecord&identifier=uuid:40e2f4dd-91ad-11e0-bf0c-0050569d679d&metadataPrefix=oai_dc");
            Document doc = XmlTools.getDocument("http://duha.mzk.cz/oai?verb=GetRecord&identifier=oai:duha.mzk.cz:158&metadataPrefix=oai_dc");            
            
            
//            Document importStylesheet = builder.build("/home/hanis/prace/resolver/urnnbn-resolver-v2/oaiAdapter/src/main/java/cz/nkp/urnnbn/oaiadapter/stylesheets/dc_kramerius_periodical_import.xsl");
//            Document digitalInstanceStylesheet = builder.build("/home/hanis/prace/resolver/urnnbn-resolver-v2/oaiAdapter/src/main/java/cz/nkp/urnnbn/oaiadapter/stylesheets/dc_kramerius_periodical_digital_instance.xsl");            
            Document importStylesheet = builder.build("/home/hanis/prace/resolver/urnnbn-resolver-v2/oaiAdapter/src/main/java/cz/nkp/urnnbn/oaiadapter/stylesheets/dc_duha_import.xsl");
            Document digitalInstanceStylesheet = builder.build("/home/hanis/prace/resolver/urnnbn-resolver-v2/oaiAdapter/src/main/java/cz/nkp/urnnbn/oaiadapter/stylesheets/dc_duha_digital_instance.xsl");            
            
            //Document importStylesheet = builder.build("/home/hanis/prace/resolver/urnnbn-resolver-v2/oaiAdapter/src/main/java/cz/nkp/urnnbn/oaiadapter/stylesheets/marc21_stmpa_import.xsl");
            //Document digitalInstanceStylesheet = builder.build("/home/hanis/prace/resolver/urnnbn-resolver-v2/oaiAdapter/src/main/java/cz/nkp/urnnbn/oaiadapter/stylesheets/marc21_stmpa_digital_instance.xsl");            

            Document output = XmlTools.getTransformedDocument(doc, importStylesheet);
            Document output2 = XmlTools.getTransformedDocument(doc, digitalInstanceStylesheet);
            //System.out.println(output.toXML());
            XmlTools.saveDocumentToFile(output, "/home/hanis/prace/resolver/output/out.xml");
            XmlTools.saveDocumentToFile(output2, "/home/hanis/prace/resolver/output/outd.xml");
            XmlTools.validateImport(output);
            XmlTools.validateDigitalIntance(output2);
        } catch (SAXException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);        
//        } catch (SAXException ex) {
//            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (ParserConfigurationException ex) {
//            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ValidityException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XSLException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParsingException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
