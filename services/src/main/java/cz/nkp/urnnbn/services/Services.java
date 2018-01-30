/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services;

import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.services.impl.*;
import cz.nkp.urnnbn.solr_indexer.DataProvider;
import cz.nkp.urnnbn.solr_indexer.IndexerConfig;
import cz.nkp.urnnbn.solr_indexer.SolrIndexer;
import org.joda.time.DateTime;

import java.util.List;
import java.util.logging.Logger;

/**
 * @author Martin Řehánek
 */
public class Services {

    private static final Logger logger = Logger.getLogger(Services.class.getName());
    private static Services instance;
    private static final Integer DEFAULT_MAX_URN_RESERVATION_SIZE = 100;
    private final DatabaseConnector connector;
    private final Integer urnNbnReservationMaxSize;
    private final SolrIndexer solrIndexer;
    private DataAccessService dataAccess;
    private DataImportService dataImport;
    private DataRemoveService dataRemove;
    private DataUpdateService dataUpdate;
    private UrnNbnReservationService urnReservation;
    private AuthenticationService authenticationService;
    private StatisticService statisticService;

    private Services(DatabaseConnector connector, Integer urnNbnReservationMaxSize, IndexerConfig indexerConfig) {
        this.connector = connector;
        if (urnNbnReservationMaxSize == null) {
            this.urnNbnReservationMaxSize = DEFAULT_MAX_URN_RESERVATION_SIZE;
        } else {
            this.urnNbnReservationMaxSize = urnNbnReservationMaxSize;
        }
        solrIndexer = new SolrIndexer(
                indexerConfig,
                null,//new FileOutputStream(reportFile)
                new DataProvider() {
                    @Override
                    public List<DigitalDocument> digDocsByModificationDate(DateTime from, DateTime until) {
                        return dataAccessService().digDocsByModificationDate(from, until);
                    }

                    @Override
                    public UrnNbn urnByDigDocId(long id, boolean withPredecessorsAndSuccessors) {
                        return dataAccessService().urnByDigDocId(id, withPredecessorsAndSuccessors);
                    }
                }
        );
    }

    public static void init(DatabaseConnector con, Integer urnNbnReservationMaxSize, IndexerConfig indexerConfig) {
        logger.info("Initializing");
        instance = new Services(con, urnNbnReservationMaxSize, indexerConfig);
    }

    public static void init(DatabaseConnector con, IndexerConfig indexerConfig) {
        logger.info("Initializing");
        instance = new Services(con, null, indexerConfig);
    }

    public static Services instanceOf() {
        if (instance == null) {
            throw new IllegalStateException("Services not initialized");
        }
        return instance;
    }

    public DataAccessService dataAccessService() {
        if (dataAccess == null) {
            dataAccess = new DataAccessServiceImpl(connector);
        }
        return dataAccess;
    }

    public DataImportService dataImportService() {
        if (dataImport == null) {
            dataImport = new DataImportServiceImpl(connector, solrIndexer);
        }
        return dataImport;
    }

    public UrnNbnReservationService urnReservationService() {
        if (urnReservation == null) {
            urnReservation = new UrnNbnReservationServiceImpl(connector, urnNbnReservationMaxSize);
        }
        return urnReservation;
    }

    public DataRemoveService dataRemoveService() {
        if (dataRemove == null) {
            dataRemove = new DataRemoveServiceImpl(connector);
        }
        return dataRemove;
    }

    public DataUpdateService dataUpdateService() {
        if (dataUpdate == null) {
            dataUpdate = new DataUpdateServiceImpl(connector);
        }
        return dataUpdate;
    }

    public AuthenticationService authenticationService() {
        if (authenticationService == null) {
            authenticationService = new AuthenticationServiceImpl(connector);
        }
        return authenticationService;
    }

    public StatisticService statisticService() {
        if (statisticService == null) {
            statisticService = new StatisticServiceImpl(connector);
        }
        return statisticService;
    }
}
