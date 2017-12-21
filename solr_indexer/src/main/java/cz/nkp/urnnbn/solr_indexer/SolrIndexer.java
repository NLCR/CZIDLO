package cz.nkp.urnnbn.solr_indexer;


import cz.nkp.urnnbn.api_client.v5.CzidloApiConnector;
import cz.nkp.urnnbn.api_client.v5.CzidloApiErrorException;
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
    private final boolean czidloApiAlwaysUseHttps;
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
                       boolean czidloApiAlwaysUseHttps,
                       String solrApiBaseUrl,
                       String solrApiLogin,
                       String solrApiPassword,
                       DataAccessService dataAccessService,
                       String czidloToSolrXslt,
                       File czidloToSolrXsltFile,
                       ReportLogger reportLogger,
                       DateTime from, DateTime to) {
        this.czidloApiBaseUrl = czidloApiBaseUrl;
        this.czidloApiAlwaysUseHttps = czidloApiAlwaysUseHttps;
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
            czidloApiConnector = new CzidloApiConnector(czidloApiBaseUrl, null, czidloApiAlwaysUseHttps, false);
            report("- CZIDLO API connector initialized");
            digDocRegistrationXslt = buildDigDocRegistrationXsltDoc();
            report("- XSLT built");
        } catch (TemplateException e) {
            report("Initialization error: TemplateException: " + e.getMessage());
            logger.log(Level.SEVERE, "Initialization error", e);
        }
        report(" ");

        List<DigitalDocument> digitalDocuments = dataAccessService.digDocsByModificationDate(from, to);
        Counters counters = new Counters(digitalDocuments.size());
        report("Processing " + counters.getFound() + " records");
        report("==============================");
        for (DigitalDocument doc : digitalDocuments) {
            UrnNbn urnNbn = dataAccessService.urnByDigDocId(doc.getId(), false);
            report(" processing " + urnNbn.toString());
            try {
                Document ddCzidloDoc = czidloApiConnector.getDigitalDocumentByInternalId(doc.getId(), true);
                if (ddCzidloDoc == null) {
                    report(" digital document's xml record not found, ignoring");
                    counters.incrementErrors();
                } else {
                    try {
                        Document solrDoc = XmlTools.getTransformedDocument(ddCzidloDoc, digDocRegistrationXslt);
                        index(urnNbn, solrDoc);
                        counters.incrementIndexed();
                    } catch (XSLException e) {
                        report(" XSLT error", e);
                    }
                }
            } catch (CzidloApiErrorException e) {
                counters.incrementErrors();
                report(" CZIDLO API error", e);
            } catch (ParsingException e) {
                counters.incrementErrors();
                report(" Parsing error", e);
            } catch (IOException e) {
                counters.incrementErrors();
                report(" I/O error", e);
            }
        }
        report(" ");

        report("Summary");
        report("=====================================================");
        report(" records found    : " + counters.getFound());
        report(" records processed: " + counters.getProcessed());
        report(" records indexed  : " + counters.getIndexed());
        report(" records erroneous: " + counters.getErrors());

        if (reportLogger != null) {
            reportLogger.close();
        }
    }

    private void index(UrnNbn urnNbn, Document solrDoc) {
        report("indexing " + urnNbn);
        // TODO: 21.12.17 implement actual solr indexing
        //System.err.println(solrDoc.toXML());
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
