/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence;

import cz.nkp.urnnbn.core.persistence.impl.postgres.ArchiverDaoPostgres;
import cz.nkp.urnnbn.core.persistence.impl.postgres.CatalogDaoPostgres;
import cz.nkp.urnnbn.core.persistence.impl.postgres.DigDocIdentifierDaoPostgres;
import cz.nkp.urnnbn.core.persistence.impl.postgres.DigitalInstanceDaoPostgres;
import cz.nkp.urnnbn.core.persistence.impl.postgres.DigitalLibraryDaoPostgres;
import cz.nkp.urnnbn.core.persistence.impl.postgres.DigitalDocumentDaoPostgres;
import cz.nkp.urnnbn.core.persistence.impl.postgres.IntEntIdentifierDaoPostgres;
import cz.nkp.urnnbn.core.persistence.impl.postgres.IntelectualEntityDaoPostgres;
import cz.nkp.urnnbn.core.persistence.impl.postgres.OriginatorDaoPostgres;
import cz.nkp.urnnbn.core.persistence.impl.postgres.PostgresConnector;
import cz.nkp.urnnbn.core.persistence.impl.postgres.PublicationDaoPostgres;
import cz.nkp.urnnbn.core.persistence.impl.postgres.RegistrarDaoPostgres;
import cz.nkp.urnnbn.core.persistence.impl.postgres.SourceDocumentDaoPostgres;
import cz.nkp.urnnbn.core.persistence.impl.postgres.UrnNbnReservedDaoPostgres;
import cz.nkp.urnnbn.core.persistence.impl.postgres.UrnNbnGeneratorDaoPostgres;
import cz.nkp.urnnbn.core.persistence.impl.postgres.UrnNbnDaoPostgres;
import cz.nkp.urnnbn.core.persistence.impl.postgres.UserDaoPostgres;
import java.util.logging.Logger;

/**
 *
 * @author Martin Řehánek
 */
public class DAOFactory {

    private static final Logger logger = Logger.getLogger(DAOFactory.class.getName());
    private final DatabaseConnector connector;
    //DAO instances
    private ArchiverDAO archiverDao;
    private CatalogDAO catalogDao;
    private DigDocIdentifierDAO digRepId;
    private DigitalInstanceDAO digInst;
    private DigitalLibraryDAO libraryDao;
    private DigitalDocumentDAO representationDao;
    private IntelectualEntityDAO entityDao;
    private IntEntIdentifierDAO intEntIdDao;
    private OriginatorDAO originatorDao;
    private PublicationDAO publicationDao;
    private RegistrarDAO registrarDao;
    private SourceDocumentDAO srcDocDao;
    private UrnNbnDAO urnDao;
    private UserDAO userDao;
    private UrnNbnGeneratorDAO urnSearchDao;
    private UrnNbnReservedDAO urnReservedDao;
    //cache
    private final boolean postgresImplemantation;
    private final boolean oracleImplementation;

    public DAOFactory(DatabaseConnector connector) {
        this.connector = connector;
        this.postgresImplemantation = connector instanceof PostgresConnector;
        this.oracleImplementation = false;
    }

    public ArchiverDAO archiverDao() {
        if (archiverDao == null) {
            if (postgresImplemantation) {
                archiverDao = new ArchiverDaoPostgres(connector);
            } else if (oracleImplementation) {
                archiverDao = null;//TODO
            }
        }
        return archiverDao;
    }

    public RegistrarDAO registrarDao() {
        if (registrarDao == null) {
            if (postgresImplemantation) {
                registrarDao = new RegistrarDaoPostgres(connector);
            } else if (oracleImplementation) {
                registrarDao = null;//TODO
            }
        }
        return registrarDao;
    }

    public DigitalLibraryDAO diglLibDao() {
        if (libraryDao == null) {
            if (postgresImplemantation) {
                libraryDao = new DigitalLibraryDaoPostgres(connector);
            } else if (oracleImplementation) {
                libraryDao = null; //TODO
            }
        }
        return libraryDao;
    }

    public IntelectualEntityDAO intelectualEntityDao() {
        if (entityDao == null) {
            if (postgresImplemantation) {
                entityDao = new IntelectualEntityDaoPostgres(connector);
            } else if (oracleImplementation) {
                entityDao = null; //TODO
            }
        }
        return entityDao;
    }

    public IntEntIdentifierDAO intEntIdentifierDao() {
        if (intEntIdDao == null) {
            if (postgresImplemantation) {
                intEntIdDao = new IntEntIdentifierDaoPostgres(connector);
            } else if (oracleImplementation) {
                intEntIdDao = null;//TODO   
            }
        }
        return intEntIdDao;
    }

    public DigitalDocumentDAO documentDao() {
        if (representationDao == null) {
            if (postgresImplemantation) {
                representationDao = new DigitalDocumentDaoPostgres(connector);
            } else if (oracleImplementation) {
                representationDao = null;//TODO
            }
        }
        return representationDao;
    }

    public UserDAO userDao() {
        if (userDao == null) {
            if (postgresImplemantation) {
                userDao = new UserDaoPostgres(connector);
            } else if (oracleImplementation) {
                userDao = null;//TODO
            }
        }
        return userDao;
    }

    public PublicationDAO publicationDao() {
        if (publicationDao == null) {
            if (postgresImplemantation) {
                publicationDao = new PublicationDaoPostgres(connector);
            } else if (oracleImplementation) {
                publicationDao = null; //TODO
            }
        }
        return publicationDao;
    }

    public UrnNbnDAO urnDao() {
        if (urnDao == null) {
            if (postgresImplemantation) {
                urnDao = new UrnNbnDaoPostgres(connector);
            } else if (oracleImplementation) {
                urnDao = null;//TODO
            }
        }
        return urnDao;
    }

    public CatalogDAO catalogDao() {
        if (catalogDao == null) {
            if (postgresImplemantation) {
                catalogDao = new CatalogDaoPostgres(connector);
            } else if (oracleImplementation) {
            }
        }
        return catalogDao;
    }

    public DigDocIdentifierDAO digDocIdDao() {
        if (digRepId == null) {
            if (postgresImplemantation) {
                digRepId = new DigDocIdentifierDaoPostgres(connector);
            } else if (oracleImplementation) {
            }
        }
        return digRepId;
    }

    public DigitalInstanceDAO digInstDao() {
        if (digInst == null) {
            if (postgresImplemantation) {
                digInst = new DigitalInstanceDaoPostgres(connector);
            } else if (oracleImplementation) {
            }
        }
        return digInst;
    }

    public OriginatorDAO originatorDao() {
        if (originatorDao == null) {
            if (postgresImplemantation) {
                originatorDao = new OriginatorDaoPostgres(connector);
            } else if (oracleImplementation) {
            }
        }
        return originatorDao;
    }

    public SourceDocumentDAO srcDocDao() {
        if (srcDocDao == null) {
            if (postgresImplemantation) {
                srcDocDao = new SourceDocumentDaoPostgres(connector);
            } else if (oracleImplementation) {
            }
        }
        return srcDocDao;
    }

    public UrnNbnReservedDAO urnReservedDao() {
        if (urnReservedDao == null) {
            if (postgresImplemantation) {
                urnReservedDao = new UrnNbnReservedDaoPostgres(connector);
            } else if (oracleImplementation) {
            }
        }
        return urnReservedDao;
    }

    public UrnNbnGeneratorDAO urnSearchDao() {
        if (urnSearchDao == null) {
            if (postgresImplemantation) {
                urnSearchDao = new UrnNbnGeneratorDaoPostgres(connector);
            } else if (oracleImplementation) {
            }
        }
        return urnSearchDao;
    }
}
