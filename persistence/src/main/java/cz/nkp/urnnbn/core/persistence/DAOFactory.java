/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence;

import cz.nkp.urnnbn.core.persistence.impl.DatabaseDriver;
import cz.nkp.urnnbn.core.persistence.impl.postgres.ArchiverDaoPostgres;
import cz.nkp.urnnbn.core.persistence.impl.postgres.CatalogDaoPostgres;
import cz.nkp.urnnbn.core.persistence.impl.postgres.DigDocIdentifierDaoPostgres;
import cz.nkp.urnnbn.core.persistence.impl.postgres.DigitalInstanceDaoPostgres;
import cz.nkp.urnnbn.core.persistence.impl.postgres.DigitalLibraryDaoPostgres;
import cz.nkp.urnnbn.core.persistence.impl.postgres.DigitalDocumentDaoPostgres;
import cz.nkp.urnnbn.core.persistence.impl.postgres.IntEntIdentifierDaoPostgres;
import cz.nkp.urnnbn.core.persistence.impl.postgres.IntelectualEntityDaoPostgres;
import cz.nkp.urnnbn.core.persistence.impl.postgres.OriginatorDaoPostgres;
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

    public DAOFactory(DatabaseConnector connector) {
        this.connector = connector;
    }

    private boolean postgres() {
        return DatabaseDriver.POSTGRES.equals(connector.getDriver());
    }

    private boolean oracle() {
        return DatabaseDriver.ORACLE.equals(connector.getDriver());
    }

    public ArchiverDAO archiverDao() {
        if (archiverDao == null) {
            if (postgres()) {
                archiverDao = new ArchiverDaoPostgres(connector);
            } else if (oracle()) {
                archiverDao = null;//TODO
            }
        }
        return archiverDao;
    }

    public RegistrarDAO registrarDao() {
        if (registrarDao == null) {
            if (postgres()) {
                registrarDao = new RegistrarDaoPostgres(connector);
            } else if (oracle()) {
                registrarDao = null;//TODO
            }
        }
        return registrarDao;
    }

    public DigitalLibraryDAO digitalLibraryDao() {
        if (libraryDao == null) {
            if (postgres()) {
                libraryDao = new DigitalLibraryDaoPostgres(connector);
            } else if (oracle()) {
                libraryDao = null; //TODO
            }
        }
        return libraryDao;
    }

    public IntelectualEntityDAO intelectualEntityDao() {
        if (entityDao == null) {
            if (postgres()) {
                entityDao = new IntelectualEntityDaoPostgres(connector);
            } else if (oracle()) {
                entityDao = null; //TODO
            }
        }
        return entityDao;
    }

    public IntEntIdentifierDAO intEntIdentifierDao() {
        if (intEntIdDao == null) {
            if (postgres()) {
                intEntIdDao = new IntEntIdentifierDaoPostgres(connector);
            } else if (oracle()) {
                intEntIdDao = null;//TODO   
            }
        }
        return intEntIdDao;
    }

    public DigitalDocumentDAO representationDao() {
        if (representationDao == null) {
            if (postgres()) {
                representationDao = new DigitalDocumentDaoPostgres(connector);
            } else if (oracle()) {
                representationDao = null;//TODO
            }
        }
        return representationDao;
    }

    public UserDAO userDao() {
        if (userDao == null) {
            if (postgres()) {
                userDao = new UserDaoPostgres(connector);
            } else if (oracle()) {
                userDao = null;//TODO
            }
        }
        return userDao;
    }

    public PublicationDAO publicationDao() {
        if (publicationDao == null) {
            if (postgres()) {
                publicationDao = new PublicationDaoPostgres(connector);
            } else if (oracle()) {
                publicationDao = null; //TODO
            }
        }
        return publicationDao;
    }

    public UrnNbnDAO urnDao() {
        if (urnDao == null) {
            if (postgres()) {
                urnDao = new UrnNbnDaoPostgres(connector);
            } else if (oracle()) {
                urnDao = null;//TODO
            }
        }
        return urnDao;
    }

    public CatalogDAO catalogDao() {
        if (catalogDao == null) {
            if (postgres()) {
                catalogDao = new CatalogDaoPostgres(connector);
            } else if (oracle()) {
            }
        }
        return catalogDao;
    }

    public DigDocIdentifierDAO digRepIdDao() {
        if (digRepId == null) {
            if (postgres()) {
                digRepId = new DigDocIdentifierDaoPostgres(connector);
            } else if (oracle()) {
            }
        }
        return digRepId;
    }

    public DigitalInstanceDAO digInstDao() {
        if (digInst == null) {
            if (postgres()) {
                digInst = new DigitalInstanceDaoPostgres(connector);
            } else if (oracle()) {
            }
        }
        return digInst;
    }

    public OriginatorDAO originatorDao() {
        if (originatorDao == null) {
            if (postgres()) {
                originatorDao = new OriginatorDaoPostgres(connector);
            } else if (oracle()) {
            }
        }
        return originatorDao;
    }

    public SourceDocumentDAO srcDocDao() {
        if (srcDocDao == null) {
            if (postgres()) {
                srcDocDao = new SourceDocumentDaoPostgres(connector);
            } else if (oracle()) {
            }
        }
        return srcDocDao;
    }

    public UrnNbnReservedDAO urnReservedDao() {
        if (urnReservedDao == null) {
            if (postgres()) {
                urnReservedDao = new UrnNbnReservedDaoPostgres(connector);
            } else if (oracle()) {
            }
        }
        return urnReservedDao;
    }

    public UrnNbnGeneratorDAO urnSearchDao() {
        if (urnSearchDao == null) {
            if (postgres()) {
                urnSearchDao = new UrnNbnGeneratorDaoPostgres(connector);
            } else if (oracle()) {
            }
        }
        return urnSearchDao;
    }
}
