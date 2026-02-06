package cz.nkp.urnnbn.services.impl;

import cz.nkp.urnnbn.core.dto.ResolvationLog;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.indexer.es.EsIndexer;
import cz.nkp.urnnbn.services.StatisticService;

import java.util.logging.Logger;

public class StatisticServiceImpl extends BusinessServiceImpl implements StatisticService {

    private static final Logger LOGGER = Logger.getLogger(StatisticServiceImpl.class.getName());

    private final EsIndexer esIndexer;

    public StatisticServiceImpl(DatabaseConnector conn, EsIndexer esIndexer) {
        super(conn);
        this.esIndexer = esIndexer;
    }

    @Override
    public void logResolvationAccess(String registrarCode, String documentCode) {
        try {
            ResolvationLog resolvationLog = factory.urnNbnResolvationLogsDao().insertResolvationAccessLog(registrarCode, documentCode);
            try {
                this.esIndexer.indexResolvationLog(resolvationLog);
            } catch (Throwable e) { //don't break main flow
                LOGGER.warning("Failed to index resolvation log into Elasticsearch: " + e.getMessage());
            }
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

}
