package cz.nkp.urnnbn.legacyrecordsimport;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) {
        try {
            Configuration conf = new Configuration(new FileInputStream("/home/martin/secret/resolver-legacy.properties"));
            OracleDatabaseConnector connector = new OracleDatabaseConnector(conf);
            Connection connection = connector.getConnection();
            RecordImportBuilder builder = new RecordImportBuilder(connection, conf.getResultXmlDir(), conf.getUpdateDatastampsDir());
            builder.buildFiles();
        } catch (DatabaseException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
