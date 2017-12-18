package cz.nkp.urnnbn.solr_indexer;


import cz.nkp.urnnbn.api_client.v5.CzidloApiConnector;
import cz.nkp.urnnbn.api_client.v5.utils.XmlTools;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.xslt.XSLException;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Martin Řehánek on 17.12.17.
 */
public class SolrIndexer {

    private static final Logger logger = Logger.getLogger(SolrIndexer.class.getName());

    // CZIDLO API
    private final String czidloApiBaseUrl;
    // SOLR API
    private final String solrApiBaseUrl;
    private final String solrApiLogin;
    private final String solrApiPassword;
    // XSLT
    private final String czidloToSolrXslt;
    private final File czidloToSolrXsltFile;
    //report
    private final ReportLogger reportLogger;
    //params
    private final Date from;
    private final Date to;

    public SolrIndexer(String czidloApiBaseUrl,
                       String solrApiBaseUrl,
                       String solrApiLogin,
                       String solrApiPassword,
                       String czidloToSolrXslt,
                       File czidloToSolrXsltFile,
                       ReportLogger reportLogger,
                       Date from, Date to) {
        this.czidloApiBaseUrl = czidloApiBaseUrl;
        this.solrApiBaseUrl = solrApiBaseUrl;
        this.solrApiLogin = solrApiLogin;
        this.solrApiPassword = solrApiPassword;
        this.czidloToSolrXslt = czidloToSolrXslt;
        this.czidloToSolrXsltFile = czidloToSolrXsltFile;
        this.reportLogger = reportLogger;
        this.from = from;
        this.to = to;
    }

    private void report(String message) {
        reportLogger.report(message);
    }

    private void report(String message, Throwable e) {
        reportLogger.report(message, e);
    }

    public void run() {
        report("Parameters");
        report("==============================");
        reportParams();

        report("Initialization");
        report("==============================");
        CzidloApiConnector czidloApiConnector = null;
        Document digDocRegistrationXslt = null;
        try {
            czidloApiConnector = new CzidloApiConnector(czidloApiBaseUrl, null, false);
            report("- CZIDLO API connector initialized");
            digDocRegistrationXslt = buildDigDocRegistrationXsltDoc();
            report("- XSLT built");
        } catch (TemplateException e) {
            report("Initialization error: TemplateException: " + e.getMessage());
            logger.log(Level.SEVERE, "Initialization error", e);
        }
        report(" ");

        // TODO: 18.12.17 get list of urn:nbn of digital documents matching from-to dates
        report("Records");
        report("==============================");
        // TODO: 18.12.17 process digital documents
        report(" ");

        report("Summary");
        report("=====================================================");
        // TODO: 18.12.17 summary (how many errors etc)

        if (reportLogger != null) {
            reportLogger.close();
        }
    }

    private void reportParams() {
        report(" CZIDLO API");
        report(" -----------------");
        report("  CZIDLO API base url: " + czidloApiBaseUrl);
        report(" ");

        report(" SOLR API");
        report(" -----------------");
        report("  SOLR API base url: " + solrApiBaseUrl);
        report("  Login: " + solrApiLogin);
        report(" ");

        report(" Transformations");
        report(" -----------------");
        if (czidloToSolrXsltFile != null) {
            report("  CZIDLO record to SOLR transformation: " + czidloToSolrXsltFile.getAbsolutePath());
        }
        report(" ");
    }

    private Document buildDigDocRegistrationXsltDoc() throws TemplateException {
        try {
            return XmlTools.parseDocumentFromString(czidloToSolrXslt);
        } catch (XSLException ex) {
            throw new TemplateException("XSLException occurred during building xsl transformation: " + ex.getMessage());
        } catch (ParsingException ex) {
            throw new TemplateException("ParsingException occurred during building xsl transformation: " + ex.getMessage());
        } catch (IOException ex) {
            throw new TemplateException("IOException occurred during building xsl transformation: " + ex.getMessage());
        }
    }


}
