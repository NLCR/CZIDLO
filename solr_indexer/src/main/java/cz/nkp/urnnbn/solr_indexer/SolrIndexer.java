package cz.nkp.urnnbn.solr_indexer;


import cz.nkp.urnnbn.api_client.v5.CzidloApiConnector;
import cz.nkp.urnnbn.api_client.v5.CzidloApiErrorException;
import cz.nkp.urnnbn.api_client.v5.utils.XmlTools;
import cz.nkp.urnnbn.core.CountryCode;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.xslt.XSLException;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrException;
import org.joda.time.DateTime;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
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
    private final boolean czidloApiUseHttps;
    // SOLR API
    private final String solrApiBaseUrl;
    private final String solrApiCollection;
    private final boolean solrApiUseHttps;
    private final String solrApiLogin;
    private final String solrApiPassword;
    //services
    private final DataProvider dataProvider;
    // XSLT
    private final String czidloToSolrXslt;
    private final File czidloToSolrXsltFile;

    //run info
    private boolean stopped = false;
    private ProgressListener progressListener;
    private long initTime;

    //helpers
    private ReportLogger reportLogger;
    private CzidloApiConnector czidloApiConnector = null;
    private SolrConnector solrConnector = null;
    private Document digDocRegistrationXslt = null;


    public SolrIndexer(String czidloApiBaseUrl,
                       boolean czidloApiUseHttps,
                       String solrApiBaseUrl,
                       String solrApiCollection,
                       boolean solrApiUseHttps,
                       String solrApiLogin,
                       String solrApiPassword,
                       DataProvider dataProvider,
                       String czidloToSolrXslt,
                       File czidloToSolrXsltFile,
                       OutputStream reportLoggerStream) {
        long start = System.currentTimeMillis();
        this.czidloApiBaseUrl = czidloApiBaseUrl;
        this.czidloApiUseHttps = czidloApiUseHttps;
        this.solrApiBaseUrl = solrApiBaseUrl;
        this.solrApiCollection = solrApiCollection;
        this.solrApiUseHttps = solrApiUseHttps;
        this.solrApiLogin = solrApiLogin;
        this.solrApiPassword = solrApiPassword;
        this.dataProvider = dataProvider;
        this.czidloToSolrXslt = czidloToSolrXslt;
        this.czidloToSolrXsltFile = czidloToSolrXsltFile;
        init(reportLoggerStream);
        initTime = System.currentTimeMillis() - start;
    }

    private void init(OutputStream reportLoggerStream) {
        reportLogger = new ReportLogger(reportLoggerStream);
        report("Parameters");
        report("==============================");
        reportParams();

        report("Initialization");
        report("==============================");
        CountryCode.initialize("CZ");

        try {
            czidloApiConnector = new CzidloApiConnector(czidloApiBaseUrl, null, czidloApiUseHttps, false);
            report("- CZIDLO API connector initialized");
            digDocRegistrationXslt = buildDigDocRegistrationXsltDoc();
            report("- XSLT built");
            solrConnector = new SolrConnector(solrApiBaseUrl, solrApiCollection, solrApiUseHttps, solrApiLogin, solrApiPassword);
            report("- SOLR API connector initialized");
        } catch (TemplateException e) {
            report("Initialization error: TemplateException: " + e.getMessage());
            logger.log(Level.SEVERE, "Initialization error", e);
        }
        report(" ");
    }


    private void report(String message) {
        reportLogger.report(message);
    }

    private void report(String message, Throwable e) {
        reportLogger.report(message, e);
    }

    public void indexDocument(long ddInternalId) {
        indexDocument(ddInternalId, new Counters(1), true);
    }

    public void indexDocuments(DateTime from, DateTime to) {
        long start = System.currentTimeMillis();
        List<DigitalDocument> digitalDocuments = dataProvider.digDocsByModificationDate(from, to);
        Counters counters = new Counters(digitalDocuments.size());
        report("Processing " + counters.getFound() + " records");
        //int limit = 3;
        report("==============================");
        for (DigitalDocument doc : digitalDocuments) {
            if (stopped) {
                report(" stopped ");
                break;
            }
            indexDocument(doc.getId(), counters, false);
        }
        report(" ");

        report("Summary");
        report("=====================================================");
        report(" records found    : " + counters.getFound());
        report(" records processed: " + counters.getProcessed());
        report(" records indexed  : " + counters.getIndexed());
        report(" records erroneous: " + counters.getErrors());
        report(" initialization duration: " + formatTime(initTime));
        report(" records processing duration: " + formatTime(System.currentTimeMillis() - start));
        if (progressListener != null) {
            progressListener.onFinished(counters.getProcessed(), counters.getFound());
        }
    }

    private void indexDocument(long ddInternalId, Counters counters, boolean explicitCommit) {
        UrnNbn urnNbn = dataProvider.urnByDigDocId(ddInternalId, false);
        if (urnNbn == null) {
            report(" digital document with id " + ddInternalId + " is missing URN:NBN");
        } else {
            report(" processing " + urnNbn.toString());
            try {
                Document ddCzidloDoc = czidloApiConnector.getDigitalDocumentByInternalId(ddInternalId, true);
                if (ddCzidloDoc == null) {
                    report(" digital document's xml record not found, ignoring");
                    counters.incrementErrors();
                } else {
                    report(" converting");
                    Document solrDoc = XmlTools.getTransformedDocument(ddCzidloDoc, digDocRegistrationXslt);
                    report(" indexing");
                    solrConnector.indexFromXmlString(solrDoc.toXML(), explicitCommit);
                    report(" indexed");
                    counters.incrementIndexed();
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
            } catch (XSLException e) {
                counters.incrementErrors();
                report(" XSLT error", e);
            } catch (ParserConfigurationException e) {
                counters.incrementErrors();
                report(" Parser configuration error", e);
            } catch (SAXException e) {
                counters.incrementErrors();
                report(" SAX error", e);
            } catch (SolrServerException e) {
                counters.incrementErrors();
                report(" Solr server error", e);
            } catch (SolrException e) {
                counters.incrementErrors();
                report(" Solr error", e);
            }
        }
        if (progressListener != null) {
            progressListener.onProgress(counters.getProcessed(), counters.getFound());
        }
    }

    private String formatTime(long millis) {
        long hours = millis / (60 * 60 * 1000);
        long minutes = millis / (60 * 1000) - hours * 60;
        long seconds = (millis / 1000) % 60;
        return String.format("%d:%02d:%02d", hours, minutes, seconds);
    }


    private void reportParams() {
        report(" CZIDLO API");
        report(" -----------------");
        report("  Base url: " + czidloApiBaseUrl);
        report("  Https: " + czidloApiUseHttps);
        report(" ");

        report(" SOLR API");
        report(" -----------------");
        report("  Base url: " + solrApiBaseUrl);
        report("  Collection: " + solrApiCollection);
        report("  Https: " + solrApiUseHttps);
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

    public void stop() {
        stopped = true;
    }

    public void close() {
        reportLogger.close();
    }

    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }
}
