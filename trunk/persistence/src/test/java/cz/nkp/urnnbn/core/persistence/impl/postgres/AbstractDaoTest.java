/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.DtoBuilder;
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
    DtoBuilder builder = new DtoBuilder();
    //DAOs
    final ArchiverDAO archiverDao;
    final CatalogDAO catalogDao;
    final DigDocIdentifierDAO digRepIdDao;
    final DigitalInstanceDAO digInstDao;
    final DigitalLibraryDAO libraryDao;
    final DigitalDocumentDAO digDocDao;
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
        DatabaseConnector con = DatabaseConnectorFactory.getConnector(
                DatabaseConfig.DRIVER,
                DatabaseConfig.HOST,
                DatabaseConfig.DATABASE,
                DatabaseConfig.PORT,
                DatabaseConfig.LOGIN,
                DatabaseConfig.PASSWORD);
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
        digDocDao = daoFactory.documentDao();
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
        digDocDao.deleteAllDocuments();
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

    public DigitalDocument documentPersisted(long registrarId, long intEntId) throws DatabaseException, RecordNotFoundException {
        DigitalDocument doc = new DigitalDocument();
        doc.setIntEntId(intEntId);
        doc.setRegistrarId(registrarId);
        doc.setArchiverId(registrarId);
        Long repId = digDocDao.insertDocument(doc);
        doc.setId(repId);
        return doc;
    }

    public DigitalDocument documentPersisted(long registrarId, long archiverId, long intEntId) throws DatabaseException, RecordNotFoundException {
        DigitalDocument doc = new DigitalDocument();
        doc.setIntEntId(intEntId);
        doc.setRegistrarId(registrarId);
        doc.setArchiverId(archiverId);
        Long repId = digDocDao.insertDocument(doc);
        doc.setId(repId);
        return doc;
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
