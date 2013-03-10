package cz.nkp.urnnbn.oaiadapter;

import cz.nkp.urnnbn.oaiadapter.resolver.RegistrationMode;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
        adapter.setPassword(Credentials.PASSWORD);//http://duha.mzk.cz/
//        adapter.setOaiBaseUrl("http://oai.mzk.cz/MoZaKi/");
//        adapter.setMetadataPrefix("marc21");
//        adapter.setSetSpec("collection:oldMaps");
        adapter.setRegistrarCode("duha");    
        adapter.setMode(RegistrationMode.BY_REGISTRAR);

                //adapter.setLimit(1);
//        adapter.setOaiBaseUrl("http://kramerius.mzk.cz/oaiprovider/");
//        adapter.setMetadataPrefix("oai_dc");
//        adapter.setSetSpec("periodical");
//        adapter.setRegistrarCode("tsh02");

        //adapter.setRegistrarCode("duha");
        adapter.setOaiBaseUrl("http://duha-devel.mzk.cz/oai");
        adapter.setMetadataPrefix("oai_dc");

//        adapter.setOaiBaseUrl("http://kramerius.mzk.cz/oaiprovider/");
//        adapter.setMetadataPrefix("oai_dc");
//        adapter.setSetSpec("monograph");        
//        adapter.setRegistrarCode("tsh05");
        //adapter.setLimit(10);


//            adapter.setOutputStream(System.out);
        try {
            adapter.setOutputStream(new FileOutputStream("/home/hanis/prace/resolver/oai/parser-test/log-duha-full.txt"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }

        Document importStylesheet = null;
        Document digitalInstanceStylesheet = null;
        try {
            Builder builder = new Builder();
            //importStylesheet = builder.build("/home/hanis/prace/resolver/urnnbn-resolver-v2/oaiAdapter/src/main/resources/cz/nkp/urnnbn/oaiadapter/stylesheets/marc21_stmpa_import.xsl");
            //digitalInstanceStylesheet = builder.build("/home/hanis/prace/resolver/urnnbn-resolver-v2/oaiAdapter/src/main/resources/cz/nkp/urnnbn/oaiadapter/stylesheets/marc21_stmpa_digital_instance.xsl");
            //importStylesheet = builder.build("/home/hanis/prace/resolver/urnnbn-resolver-v2/oaiAdapter/src/main/java/cz/nkp/urnnbn/oaiadapter/stylesheets/dc_kramerius_monograph_import.xsl");
            //digitalInstanceStylesheet = builder.build("/home/hanis/prace/resolver/urnnbn-resolver-v2/oaiAdapter/src/main/java/cz/nkp/urnnbn/oaiadapter/stylesheets/dc_kramerius_monograph_digital_instance.xsl");
            //importStylesheet = builder.build("/home/hanis/prace/resolver/urnnbn-resolver-v2/oaiAdapter/src/main/java/cz/nkp/urnnbn/oaiadapter/stylesheets/dc_kramerius_periodical_import.xsl");
            //digitalInstanceStylesheet = builder.build("/home/hanis/prace/resolver/urnnbn-resolver-v2/oaiAdapter/src/main/java/cz/nkp/urnnbn/oaiadapter/stylesheets/dc_kramerius_periodical_digital_instance.xsl");
            importStylesheet = builder.build("/home/hanis/prace/resolver/urnnbn-resolver-v2/oaiAdapter/src/main/resources/cz/nkp/urnnbn/oaiadapter/stylesheets/dc_duha_import.xsl");
            digitalInstanceStylesheet = builder.build("/home/hanis/prace/resolver/urnnbn-resolver-v2/oaiAdapter/src/main/resources/cz/nkp/urnnbn/oaiadapter/stylesheets/dc_duha_digital_instance.xsl");
            //importStylesheet = builder.build("/home/hanis/prace/resolver/urnnbn-resolver-v2/oaiAdapter/src/main/resources/cz/nkp/urnnbn/oaiadapter/stylesheets/dc_duha_reserved_import.xsl");
            //digitalInstanceStylesheet = builder.build("/home/hanis/prace/resolver/urnnbn-resolver-v2/oaiAdapter/src/main/resources/cz/nkp/urnnbn/oaiadapter/stylesheets/dc_duha_digital_instance.xsl");
        } catch (ParsingException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        adapter.setMetadataToImportTemplate(importStylesheet.toXML());
        adapter.setMetadataToDigitalInstanceTemplate(digitalInstanceStylesheet.toXML());

        adapter.run();


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
