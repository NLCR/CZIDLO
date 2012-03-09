/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services;

import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.impl.DatabaseConnectorFactory;
import cz.nkp.urnnbn.services.impl.AuthenticationServiceImpl;
import cz.nkp.urnnbn.services.impl.DataAccessServiceImpl;
import cz.nkp.urnnbn.services.impl.DataImportServiceImpl;
import cz.nkp.urnnbn.services.impl.DataRemoveServiceImpl;
import cz.nkp.urnnbn.services.impl.DataUpdateServiceImpl;
import cz.nkp.urnnbn.services.impl.UrnNbnReservationServiceImpl;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Řehánek
 */
public class Services {

    private static final Logger logger = Logger.getLogger(Services.class.getName());
    private static Services instance;
    private final DatabaseConnector connector;
    private final int urnNbnReservationMaxSize;
    private DataAccessService dataAccess;
    private DataImportService dataImport;
    private DataRemoveService dataRemove;
    private DataUpdateService dataUpdate;
    private UrnNbnReservationService urnReservation;
    private AuthenticationService authenticationService;

    private Services(DatabaseConnector connector, int urnNbnReservationMaxSize) {
        this.connector = connector;
        this.urnNbnReservationMaxSize = urnNbnReservationMaxSize;
    }

    public static void init(boolean develMode, int urnNbnReservationMaxSize) {
        DatabaseConnector connector = null;
        if (develMode) {
            logger.log(Level.INFO, "initializing {0} in development mode", Services.class.getName());
            connector = DatabaseConnectorFactory.getConnector(DevelDatabaseConfig.DRIVER, DevelDatabaseConfig.HOST, DevelDatabaseConfig.DATABASE, DevelDatabaseConfig.LOGIN, DevelDatabaseConfig.PASSWORD);
        } else {
            logger.log(Level.INFO, "initializing {0} in production mode", Services.class.getName());
            connector = DatabaseConnectorFactory.getConnector();
        }
        instance = new Services(connector, urnNbnReservationMaxSize);
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
            dataImport = new DataImportServiceImpl(connector);
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
}
