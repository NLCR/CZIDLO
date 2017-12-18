package cz.nkp.urnnbn.czidlo_indexer;

import cz.nkp.urnnbn.api_client.v5.utils.XmlTools;
import cz.nkp.urnnbn.solr_indexer.ReportLogger;
import cz.nkp.urnnbn.solr_indexer.SolrIndexer;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.File;
import java.io.FileNotFoundException;
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
        File xsltFile = new File("src/main/resources/czidlo-to-solr.xslt");
        String xslt = XmlTools.loadXmlFromFile(xsltFile.getAbsolutePath());
        File reportFile = new File("/tmp/solr_indexer_report.txt");
        ReportLogger reportLogger = buildReportLogger(reportFile);

        SolrIndexer indexer = new SolrIndexer(
                "localhost:8080/api",
                "todo", "todo", "todo",
                xslt, xsltFile, reportLogger,
                null, null
        );

        indexer.run();
    }

    private static ReportLogger buildReportLogger(File reportFile) throws Exception {
        try {
            return new ReportLogger(new FileOutputStream(reportFile));
        } catch (FileNotFoundException ex) {
            throw new Exception("Cannot open report file for writing", ex);
        }
    }

}
