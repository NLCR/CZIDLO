package cz.nkp.urnnbn.czidlo_indexer;

import cz.nkp.urnnbn.api_client.v5.utils.XmlTools;
import cz.nkp.urnnbn.core.persistence.impl.postgres.PostgresSimpleConnector;
import cz.nkp.urnnbn.services.Services;
import cz.nkp.urnnbn.solr_indexer.ReportLogger;
import cz.nkp.urnnbn.solr_indexer.SolrIndexer;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.joda.time.DateTime;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Unit test for simple App.
 */
public class SolrIndexerTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public SolrIndexerTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(SolrIndexerTest.class);
    }

    public void testSolrIndexer() throws Exception {
        ReportLogger reportLogger = null;
        try {
            File xsltFile = new File("src/main/resources/czidlo-to-solr.xslt");
            String xslt = XmlTools.loadXmlFromFile(xsltFile.getAbsolutePath());
            File reportFile = new File("/tmp/solr_indexer_report.txt");
            reportLogger = new ReportLogger(new FileOutputStream(reportFile));
            //init services
            Services.init(new PostgresSimpleConnector(
                    "localhost", "czidlo_core", 5432,
                    "czidlo", "czidlo"));

            //DateTime from = new DateTime(2017, 8, 16, 0, 0);
            DateTime from = new DateTime(2000, 8, 16, 0, 0);
            DateTime to = new DateTime();

            String solrBaseUrl = "localhost:8983/solr";
            String solrCollection = "czidlo";
            boolean solrUseHttps = false;
            String solrLogin = "solr";
            String solrPass = "SolrRocks";


            SolrIndexer indexer = new SolrIndexer(
                    "localhost:8080/api", false,
                    solrBaseUrl, solrCollection, solrUseHttps, solrLogin, solrPass,
                    Services.instanceOf().dataAccessService(),
                    xslt, xsltFile, reportLogger,
                    from, to
            );

            indexer.run();
        } finally {
            if (reportLogger != null) {
                reportLogger.close();
            }
        }
    }


}
