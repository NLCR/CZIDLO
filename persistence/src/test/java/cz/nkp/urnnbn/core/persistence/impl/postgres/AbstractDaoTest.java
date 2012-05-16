/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.DigDocIdType;
import cz.nkp.urnnbn.core.DtoBuilder;
import cz.nkp.urnnbn.core.dto.Archiver;
import cz.nkp.urnnbn.core.dto.DigDocIdentifier;
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
import java.util.logging.Level;
import java.util.logging.Logger;
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
    final DigDocIdentifierDAO digDocIdDao;
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
    final UrnNbnGeneratorDAO urnGeneratorDao;

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
        digDocIdDao = daoFactory.digDocIdDao();
        digInstDao = daoFactory.digInstDao();
        libraryDao = daoFactory.diglLibDao();
        entityDao = daoFactory.intelectualEntityDao();
        intEntIdDao = daoFactory.intEntIdentifierDao();
        originatorDao = daoFactory.originatorDao();
        digDocDao = daoFactory.documentDao();
        userDao = daoFactory.userDao();
        publicationDao = daoFactory.publicationDao();
        srcDocDao = daoFactory.srcDocDao();
        urnDao = daoFactory.urnDao();
        urnBookedDao = daoFactory.urnReservedDao();
        urnGeneratorDao = daoFactory.urnSearchDao();
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
        Long id = digDocDao.insertDocument(doc);
        return digDocDao.getDocumentByDbId(id);
    }

    public DigDocIdentifier digDocIdentifierPersisted(long registrarId, long digDocId) throws DatabaseException, RecordNotFoundException, AlreadyPresentException {
        DigDocIdentifier inserted = builder.digDocIdentifierWithoutIds();
        inserted.setDigDocId(digDocId);
        inserted.setRegistrarId(registrarId);
        digDocIdDao.insertDigDocId(inserted);
        return digDocIdDao.getIdentifer(digDocId, inserted.getType());
    }

    public DigDocIdentifier digDocIdentifierPersisted(long registrarId, long digDocId, String type) throws DatabaseException, RecordNotFoundException, AlreadyPresentException {
        DigDocIdentifier inserted = builder.digDocIdentifierWithoutIds();
        inserted.setDigDocId(digDocId);
        inserted.setRegistrarId(registrarId);
        inserted.setType(DigDocIdType.valueOf(type));
        digDocIdDao.insertDigDocId(inserted);
        return digDocIdDao.getIdentifer(digDocId, inserted.getType());
    }

    public DigitalDocument documentPersisted(long registrarId, long archiverId, long intEntId) throws DatabaseException, RecordNotFoundException {
        DigitalDocument doc = new DigitalDocument();
        doc.setIntEntId(intEntId);
        doc.setRegistrarId(registrarId);
        doc.setArchiverId(archiverId);
        Long id = digDocDao.insertDocument(doc);
        return digDocDao.getDocumentByDbId(id);
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
        try {
            return entityDao.getEntityByDbId(id);
        } catch (RecordNotFoundException ex) {
            Logger.getLogger(AbstractDaoTest.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public User userPersisted() throws DatabaseException, AlreadyPresentException {
        User user = builder.userWithoutId();
        user.setId(userDao.insertUser(user));
        return user;
    }
}
