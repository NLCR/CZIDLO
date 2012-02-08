/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.dto.Archiver;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.core.persistence.ArchiverDAO;
import cz.nkp.urnnbn.core.persistence.CatalogDAO;
import cz.nkp.urnnbn.core.persistence.DAOFactory;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.DigDocIdentifierDAO;
import cz.nkp.urnnbn.core.persistence.DigitalInstanceDAO;
import cz.nkp.urnnbn.core.persistence.DigitalLibraryDAO;
import cz.nkp.urnnbn.core.persistence.DigitalDocumentDAO;
import cz.nkp.urnnbn.core.persistence.IntEntIdentifierDAO;
import cz.nkp.urnnbn.core.persistence.IntelectualEntityDAO;
import cz.nkp.urnnbn.core.persistence.OriginatorDAO;
import cz.nkp.urnnbn.core.persistence.PublicationDAO;
import cz.nkp.urnnbn.core.persistence.RegistrarDAO;
import cz.nkp.urnnbn.core.persistence.SourceDocumentDAO;
import cz.nkp.urnnbn.core.persistence.UrnNbnReservedDAO;
import cz.nkp.urnnbn.core.persistence.UrnNbnGeneratorDAO;
import cz.nkp.urnnbn.core.persistence.UrnNbnDAO;
import cz.nkp.urnnbn.core.persistence.UserDAO;
import cz.nkp.urnnbn.core.persistence.impl.DatabaseConnectorFactory;
import junit.framework.TestCase;

/**
 *
 * @author Martin Řehánek
 */
public abstract class AbstractDaoTest extends TestCase {

    static final long ILLEGAL_ID = -1;
    private DatabaseConnector con;
    DtoBuilder builder = new DtoBuilder();
    //DAOs
    final ArchiverDAO archiverDao;
    final CatalogDAO catalogDao;
    final DigDocIdentifierDAO digRepIdDao;
    final DigitalInstanceDAO digInstDao;
    final DigitalLibraryDAO libraryDao;
    final DigitalDocumentDAO representationDao;
    final IntelectualEntityDAO entityDao;
    final IntEntIdentifierDAO intEntIdDao;
    final OriginatorDAO originatorDao;
    final PublicationDAO publicationDao;
    final RegistrarDAO registrarDao;
    final SourceDocumentDAO srcDocDao;
    final UrnNbnDAO urnDao;
    final UserDAO userDao;
    final UrnNbnReservedDAO urnBookedDao;
    final UrnNbnGeneratorDAO urnSearchDao;

    public AbstractDaoTest(String testName) {
        super(testName);
        //con = new PostgresConnector("localhost", "resolver-persistenceUnitTests", "postgres", "poseruse");
        con = DatabaseConnectorFactory.getConnector("org.postgresql.Driver", "localhost", "resolver-persistenceUnitTests", "postgres", "poseruse");
        DAOFactory daoFactory = new DAOFactory(con);
        registrarDao = daoFactory.registrarDao();
        archiverDao = daoFactory.archiverDao();
        catalogDao = daoFactory.catalogDao();
        digRepIdDao = daoFactory.digRepIdDao();
        digInstDao = daoFactory.digInstDao();
        libraryDao = daoFactory.digitalLibraryDao();
        entityDao = daoFactory.intelectualEntityDao();
        intEntIdDao = daoFactory.intEntIdentifierDao();
        originatorDao = daoFactory.originatorDao();
        representationDao = daoFactory.documentDao();
        userDao = daoFactory.userDao();
        publicationDao = daoFactory.publicationDao();
        srcDocDao = daoFactory.srcDocDao();
        urnDao = daoFactory.urnDao();
        urnBookedDao = daoFactory.urnReservedDao();
        urnSearchDao = daoFactory.urnSearchDao();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        clearDatabase();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        clearDatabase();
    }

    public void clearDatabase() throws Exception {
        urnDao.deleteAllUrnNbns();
        representationDao.deleteAllRepresentations();
        entityDao.deleteAllEntities();
        //kaskadove by se mely pomazat identifikatory intelektualnich entit
        archiverDao.deleteAllArchivers();
        userDao.deleteAllUsers();
    }

    public Archiver archiverPersisted() throws Exception {
        Archiver archiver = builder.archiverWithoutId();
        long id = archiverDao.insertArchiver(archiver);
        archiver.setId(id);
        return archiver;
    }

    Registrar registrarPersisted() throws DatabaseException, AlreadyPresentException {
        Registrar registrar = builder.registrarWithoutId();
        long id = registrarDao.insertRegistrar(registrar);
        registrar.setId(id);
        return registrar;
    }

    public DigitalDocument representationPersisted(long registrarId, long intEntId) throws DatabaseException, RecordNotFoundException {
        DigitalDocument rep = builder.digRepWithoutIds();
        rep.setIntEntId(intEntId);
        rep.setRegistrarId(registrarId);
        rep.setArchiverId(registrarId);
        Long repId = representationDao.insertDocument(rep);
        rep.setId(repId);
        return rep;
    }

    public DigitalLibrary libraryPersisted() throws DatabaseException, RecordNotFoundException, AlreadyPresentException {
        Registrar registrar = builder.registrarWithoutId();
        Long registrarId = registrarDao.insertRegistrar(registrar);
        DigitalLibrary library = builder.digLibraryWithoutIdAndRegistrarId();
        library.setRegistrarId(registrarId);
        long id = libraryDao.insertLibrary(library);
        library.setId(id);
        return library;
    }

    public IntelectualEntity entityPersisted() throws DatabaseException {
        IntelectualEntity entity = builder.intEntityWithoutId();
        long id = entityDao.insertIntelectualEntity(entity);
        entity.setId(id);
        return entity;
    }

    public User userPersisted() throws DatabaseException, AlreadyPresentException {
        User user = builder.userWithoutId();
        long id = userDao.insertUser(user);
        user.setId(id);
        return user;
    }
}
