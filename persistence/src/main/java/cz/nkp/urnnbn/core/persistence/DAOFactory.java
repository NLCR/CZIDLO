/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence;

import cz.nkp.urnnbn.core.persistence.impl.postgres.*;

import java.util.logging.Logger;

/**
 * @author Martin Řehánek
 */
public class DAOFactory {

    private static final Logger logger = Logger.getLogger(DAOFactory.class.getName());
    private final DatabaseConnector connector;
    // DAO instances
    private ArchiverDAO archiverDao;
    private CatalogDAO catalogDao;
    private RegistrarScopeIdentifierDAO registrarScopeId;
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
    private ContentDAO contentDao;
    private UrnNbnStatisticDAO statisticDao;
    // cache
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
                throw new UnsupportedOperationException("Oracle implementation not available");
            }
        }
        return archiverDao;
    }

    public RegistrarDAO registrarDao() {
        if (registrarDao == null) {
            if (postgresImplemantation) {
                registrarDao = new RegistrarDaoPostgres(connector);
            } else if (oracleImplementation) {
                throw new UnsupportedOperationException("Oracle implementation not available");
            }
        }
        return registrarDao;
    }

    public DigitalLibraryDAO diglLibDao() {
        if (libraryDao == null) {
            if (postgresImplemantation) {
                libraryDao = new DigitalLibraryDaoPostgres(connector);
            } else if (oracleImplementation) {
                throw new UnsupportedOperationException("Oracle implementation not available");
            }
        }
        return libraryDao;
    }

    public IntelectualEntityDAO intelectualEntityDao() {
        if (entityDao == null) {
            if (postgresImplemantation) {
                entityDao = new IntelectualEntityDaoPostgres(connector);
            } else if (oracleImplementation) {
                throw new UnsupportedOperationException("Oracle implementation not available");
            }
        }
        return entityDao;
    }

    public IntEntIdentifierDAO intEntIdentifierDao() {
        if (intEntIdDao == null) {
            if (postgresImplemantation) {
                intEntIdDao = new IntEntIdentifierDaoPostgres(connector);
            } else if (oracleImplementation) {
                throw new UnsupportedOperationException("Oracle implementation not available");
            }
        }
        return intEntIdDao;
    }

    public DigitalDocumentDAO documentDao() {
        if (representationDao == null) {
            if (postgresImplemantation) {
                representationDao = new DigitalDocumentDaoPostgres(connector);
            } else if (oracleImplementation) {
                throw new UnsupportedOperationException("Oracle implementation not available");
            }
        }
        return representationDao;
    }

    public UserDAO userDao() {
        if (userDao == null) {
            if (postgresImplemantation) {
                userDao = new UserDaoPostgres(connector);
            } else if (oracleImplementation) {
                throw new UnsupportedOperationException("Oracle implementation not available");
            }
        }
        return userDao;
    }

    public PublicationDAO publicationDao() {
        if (publicationDao == null) {
            if (postgresImplemantation) {
                publicationDao = new PublicationDaoPostgres(connector);
            } else if (oracleImplementation) {
                throw new UnsupportedOperationException("Oracle implementation not available");
            }
        }
        return publicationDao;
    }

    public UrnNbnDAO urnDao() {
        if (urnDao == null) {
            if (postgresImplemantation) {
                urnDao = new UrnNbnDaoPostgres(connector);
            } else if (oracleImplementation) {
                throw new UnsupportedOperationException("Oracle implementation not available");
            }
        }
        return urnDao;
    }

    public CatalogDAO catalogDao() {
        if (catalogDao == null) {
            if (postgresImplemantation) {
                catalogDao = new CatalogDaoPostgres(connector);
            } else if (oracleImplementation) {
                throw new UnsupportedOperationException("Oracle implementation not available");
            }
        }
        return catalogDao;
    }

    public RegistrarScopeIdentifierDAO digDocIdDao() {
        if (registrarScopeId == null) {
            if (postgresImplemantation) {
                registrarScopeId = new RegistrarScopeIdentifierDaoPostgres(connector);
            } else if (oracleImplementation) {
                throw new UnsupportedOperationException("Oracle implementation not available");
            }
        }
        return registrarScopeId;
    }

    public DigitalInstanceDAO digInstDao() {
        if (digInst == null) {
            if (postgresImplemantation) {
                digInst = new DigitalInstanceDaoPostgres(connector);
            } else if (oracleImplementation) {
                throw new UnsupportedOperationException("Oracle implementation not available");
            }
        }
        return digInst;
    }

    public OriginatorDAO originatorDao() {
        if (originatorDao == null) {
            if (postgresImplemantation) {
                originatorDao = new OriginatorDaoPostgres(connector);
            } else if (oracleImplementation) {
                throw new UnsupportedOperationException("Oracle implementation not available");
            }
        }
        return originatorDao;
    }

    public SourceDocumentDAO srcDocDao() {
        if (srcDocDao == null) {
            if (postgresImplemantation) {
                srcDocDao = new SourceDocumentDaoPostgres(connector);
            } else if (oracleImplementation) {
                throw new UnsupportedOperationException("Oracle implementation not available");
            }
        }
        return srcDocDao;
    }

    public UrnNbnReservedDAO urnReservedDao() {
        if (urnReservedDao == null) {
            if (postgresImplemantation) {
                urnReservedDao = new UrnNbnReservedDaoPostgres(connector);
            } else if (oracleImplementation) {
                throw new UnsupportedOperationException("Oracle implementation not available");
            }
        }
        return urnReservedDao;
    }

    public UrnNbnGeneratorDAO urnSearchDao() {
        if (urnSearchDao == null) {
            if (postgresImplemantation) {
                urnSearchDao = new UrnNbnGeneratorDaoPostgres(connector);
            } else if (oracleImplementation) {
                throw new UnsupportedOperationException("Oracle implementation not available");
            }
        }
        return urnSearchDao;
    }

    public ContentDAO contentDao() {
        if (contentDao == null) {
            if (postgresImplemantation) {
                contentDao = new ContentDaoPostgres(connector);
            } else if (oracleImplementation) {
                throw new UnsupportedOperationException("Oracle implementation not available");
            }
        }
        return contentDao;
    }

    public UrnNbnStatisticDAO urnNbnStatisticDao() {
        if (statisticDao == null) {
            if (postgresImplemantation) {
                statisticDao = new UrnNbntatisticDaoPostgres(connector);
            } else if (oracleImplementation) {
                throw new UnsupportedOperationException("Oracle implementation not available");
            }
        }
        return statisticDao;
    }

}
