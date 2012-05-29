package cz.nkp.urnnbn.oaiadapter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;

/**
 * Hello world!
 *
 */
public class App {

    //public static void 
    public static void main(String[] args) {
                
        
        OaiAdapter adapter = new OaiAdapter();
        adapter.setLogin(Credentials.LOGIN);
        adapter.setPassword(Credentials.PASSWORD);
        adapter.setOaiBaseUrl("http://oai.mzk.cz/");
        adapter.setMetadataPrefix("marc21");
        adapter.setSetSpec("collection:oldMaps");
        adapter.setRegistrarCode("tsh03");
        //adapter.setLimit(25);

        Document importStylesheet = null;
        Document digitalInstanceStylesheet = null;
        try {
            Builder builder = new Builder();
            importStylesheet = builder.build("/home/hanis/prace/resolver/urnnbn-resolver-v2/oaiAdapter/src/main/java/cz/nkp/urnnbn/oaiadapter/stylesheets/marc21_stmpa_import.xsl");
            digitalInstanceStylesheet = builder.build("/home/hanis/prace/resolver/urnnbn-resolver-v2/oaiAdapter/src/main/java/cz/nkp/urnnbn/oaiadapter/stylesheets/marc21_stmpa_digital_instance.xsl");
        } catch (ParsingException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        adapter.setMetadataToImportTemplate(importStylesheet.toXML());
      //  System.out.println(adapter.getMetadataToImportTemplate());
        adapter.setMetadataToDigitalInstanceTemplate(digitalInstanceStylesheet.toXML());  
        try {
            adapter.run();
        } catch (TemplateException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        
        
        
        
        
        
        
//        OaiAdapter adapter = new OaiAdapter();
//        adapter.setLogin(Credentials.LOGIN);
//        adapter.setPassword(Credentials.PASSWORD);
//        adapter.setOaiBaseUrl("http://kramerius.mzk.cz/oaiprovider/");
//        adapter.setMetadataPrefix("oai_dc");
//        adapter.setSetSpec("monograph");
//        adapter.setRegistrarCode("tsh01");
//        adapter.setLimit(25);
//
//        Document importStylesheet = null;
//        Document digitalInstanceStylesheet = null;
//        try {
//            Builder builder = new Builder();
//            importStylesheet = builder.build("/home/hanis/prace/resolver/urnnbn-resolver-v2/oaiAdapter/src/main/java/cz/nkp/urnnbn/oaiadapter/stylesheets/dc_import.xsl");
//            digitalInstanceStylesheet = builder.build("/home/hanis/prace/resolver/urnnbn-resolver-v2/oaiAdapter/src/main/java/cz/nkp/urnnbn/oaiadapter/stylesheets/dc_digital_instance.xsl");
//        } catch (ParsingException ex) {
//            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//        adapter.setMetadataToImportTemplate(importStylesheet.toXML());
//      //  System.out.println(adapter.getMetadataToImportTemplate());
//        adapter.setMetadataToDigitalInstanceTemplate(digitalInstanceStylesheet.toXML());  
//        try {
//            adapter.run();
//        } catch (TemplateException ex) {
//            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
}
