package cz.nkp.urnnbn.solr_indexer;


import cz.nkp.urnnbn.api_client.v5.CzidloApiConnector;
import cz.nkp.urnnbn.api_client.v5.utils.XmlTools;
import cz.nkp.urnnbn.core.CountryCode;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.services.DataAccessService;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.xslt.XSLException;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.util.List;
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
    //services
    private final DataAccessService dataAccessService;
    // XSLT
    private final String czidloToSolrXslt;
    private final File czidloToSolrXsltFile;
    //report
    private final ReportLogger reportLogger;
    //params
    private final DateTime from;
    private final DateTime to;

    public SolrIndexer(String czidloApiBaseUrl,
                       String solrApiBaseUrl,
                       String solrApiLogin,
                       String solrApiPassword,
                       DataAccessService dataAccessService,
                       String czidloToSolrXslt,
                       File czidloToSolrXsltFile,
                       ReportLogger reportLogger,
                       DateTime from, DateTime to) {
        this.czidloApiBaseUrl = czidloApiBaseUrl;
        this.solrApiBaseUrl = solrApiBaseUrl;
        this.solrApiLogin = solrApiLogin;
        this.solrApiPassword = solrApiPassword;
        this.dataAccessService = dataAccessService;
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
        CountryCode.initialize("CZ");
        DataAccessService readService;
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

        report("Records");
        report("==============================");
        List<DigitalDocument> digitalDocuments = dataAccessService.digDocsByModificationDate(from, to);
        report(" matching documents: " + digitalDocuments.size());
        for (DigitalDocument doc : digitalDocuments) {
            UrnNbn urnNbn = dataAccessService.urnByDigDocId(doc.getId(), false);
            report("processing " + urnNbn.toString());
            // TODO: 19.12.17 fetch xml from api, index
        }
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
