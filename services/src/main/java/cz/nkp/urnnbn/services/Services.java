/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services;

import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.ResolvationLog;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.indexer.es.EsIndexer;
import cz.nkp.urnnbn.services.impl.*;
import cz.nkp.urnnbn.indexer.DataProvider;
import cz.nkp.urnnbn.indexer.IndexerConfig;
import org.joda.time.DateTime;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Martin Řehánek
 */
public class Services implements AutoCloseable {

    private static final Integer DEFAULT_MAX_URN_RESERVATION_SIZE = 100;
    private static final Logger logger = Logger.getLogger(Services.class.getName());
    private static final ConcurrentHashMap<ClassLoader, Services> INSTANCES = new ConcurrentHashMap<>();
    private final Object lifecycleLock = new Object();
    private volatile boolean closed = false;

    private final DatabaseConnector dbConnector;
    private final Integer urnNbnReservationMaxSize;
    private EsIndexer esIndexer;

    private DataAccessService dataAccess;
    private DataImportService dataImport;
    private DataRemoveService dataRemove;
    private DataUpdateService dataUpdate;
    private UrnNbnReservationService urnReservation;
    private AuthenticationService authenticationService;
    private StatisticService statisticService;

    private Services(DatabaseConnector dbConnector, Integer urnNbnReservationMaxSize, IndexerConfig indexerConfig) {
        this.dbConnector = dbConnector;
        if (urnNbnReservationMaxSize == null) {
            this.urnNbnReservationMaxSize = DEFAULT_MAX_URN_RESERVATION_SIZE;
        } else {
            this.urnNbnReservationMaxSize = urnNbnReservationMaxSize;
        }
        if (indexerConfig == null) {
            esIndexer = null;
            logger.warning("Indexer config is null, ignoring ES indexer initialization");
        } else {
            logger.info("initializing ES indexer");
            esIndexer = new EsIndexer(indexerConfig, null,
                    new DataProvider() {
                        @Override
                        public List<DigitalDocument> digDocsByModificationDate(DateTime from, DateTime until) {
                            return dataAccessService().digDocsByModificationDate(from, until);
                        }

                        @Override
                        public List<ResolvationLog> resolvationLogsByDate(DateTime from, DateTime until) {
                            return dataAccessService().resolvationLogsByDate(from, until);
                        }

                        @Override
                        public UrnNbn urnByDigDocId(long id, boolean withPredecessorsAndSuccessors) {
                            return dataAccessService().urnByDigDocId(id, withPredecessorsAndSuccessors);
                        }
                    }
            );
        }
    }

    @Override
    public void close() {
        synchronized (lifecycleLock) {
            if (closed) return;
            closed = true;

            if (esIndexer != null) {
                try {
                    esIndexer.close();
                } catch (Exception e) {
                    logger.log(Level.WARNING, "EFailed to close EsIndexer", e);
                } finally {
                    esIndexer = null;
                }
            }
            // TODO: close other resources if needed
        }
    }

    public static void shutdownForCurrentClassLoader() {
        ClassLoader cl = Services.class.getClassLoader();
        Services s = INSTANCES.remove(cl);
        if (s != null) {
            logger.info("Shutting down Services for classloader " + clInfoWithContext());
            s.close();
        }
    }

    public static void init(DatabaseConnector con, Integer urnNbnReservationMaxSize, IndexerConfig indexerConfig) {
        if (getInstanceForClassLoader() == null) {
            logger.info("Initializing Services for classloader " + clInfoWithContext());
            setInstanceForClassLoader(new Services(con, urnNbnReservationMaxSize, indexerConfig));
        } else {
            logger.info("Services already initialized for classloader " + clInfoShort());
        }
    }

    public static void init(DatabaseConnector con, IndexerConfig indexerConfig) {
        if (getInstanceForClassLoader() == null) {
            logger.info("Initializing Services for classloader " + clInfoWithContext());
            setInstanceForClassLoader(new Services(con, null, indexerConfig));
        } else {
            logger.info("Services already initialized for classloader " + clInfoShort());
        }
    }

    private static String clInfoWithContext() {
        ClassLoader cl = Services.class.getClassLoader();
        if (cl == null) return "cl=null";

        String ctx = null;
        String s = String.valueOf(cl); // může být multiline
        for (String line : s.split("\\R")) {          // \\R = libovolný newline
            line = line.trim();
            if (line.startsWith("context:")) {
                ctx = line.substring("context:".length()).trim();
                break;
            }
        }

        ClassLoader p = cl.getParent();

        return String.format(
                "%s@%08x%s parent=%s@%08x",
                cl.getClass().getSimpleName(),
                System.identityHashCode(cl),
                (ctx != null ? " ctx=" + ctx : ""),
                (p == null ? "null" : p.getClass().getSimpleName()),
                (p == null ? 0 : System.identityHashCode(p))
        );
    }

    private static String clInfoShort() {
        ClassLoader cl = Services.class.getClassLoader();
        if (cl == null) return "cl=null";

        ClassLoader p = cl.getParent();

        return String.format(
                "%s@%08x parent=%s@%08x",
                cl.getClass().getSimpleName(),
                System.identityHashCode(cl),
                (p == null ? "null" : p.getClass().getSimpleName()),
                (p == null ? 0 : System.identityHashCode(p))
        );
    }

    private static Services getInstanceForClassLoader() {
        ClassLoader cl = Services.class.getClassLoader();
        return INSTANCES.get(cl);
    }

    private static void setInstanceForClassLoader(Services instance) {
        ClassLoader cl = Services.class.getClassLoader();
        INSTANCES.put(cl, instance);
    }

    public static Services instanceOf() {
        Services instance = getInstanceForClassLoader();
        if (instance == null) {
            throw new IllegalStateException("Services not initialized for classloader " + clInfoWithContext());
        }
        return instance;
    }

    public DataAccessService dataAccessService() {
        if (dataAccess == null) {
            dataAccess = new DataAccessServiceImpl(dbConnector);
        }
        return dataAccess;
    }

    public DataImportService dataImportService() {
        if (dataImport == null) {
            dataImport = new DataImportServiceImpl(dbConnector, esIndexer);
        }
        return dataImport;
    }

    public UrnNbnReservationService urnReservationService() {
        if (urnReservation == null) {
            urnReservation = new UrnNbnReservationServiceImpl(dbConnector, urnNbnReservationMaxSize);
        }
        return urnReservation;
    }

    public DataRemoveService dataRemoveService() {
        if (dataRemove == null) {
            dataRemove = new DataRemoveServiceImpl(dbConnector, esIndexer);
        }
        return dataRemove;
    }

    public DataUpdateService dataUpdateService() {
        if (dataUpdate == null) {
            dataUpdate = new DataUpdateServiceImpl(dbConnector, esIndexer);
        }
        return dataUpdate;
    }

    public AuthenticationService authenticationService() {
        if (authenticationService == null) {
            authenticationService = new AuthenticationServiceImpl(dbConnector);
        }
        return authenticationService;
    }

    public StatisticService statisticService() {
        if (statisticService == null) {
            statisticService = new StatisticServiceImpl(dbConnector, esIndexer);
        }
        return statisticService;
    }
}
