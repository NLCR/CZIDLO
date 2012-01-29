package cz.nkp.urnnbn.core.persistence;

import cz.nkp.urnnbn.core.DigRepIdType;
import cz.nkp.urnnbn.core.DtoBuilder;
import cz.nkp.urnnbn.core.EntityType;
import cz.nkp.urnnbn.core.IntEntIdType;
import cz.nkp.urnnbn.core.dto.User;

import cz.nkp.urnnbn.core.dto.Archiver;
import cz.nkp.urnnbn.core.dto.Catalog;
import cz.nkp.urnnbn.core.dto.DigRepIdentifier;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.dto.DigitalRepresentation;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.SourceDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.exceptions.PersistenceException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.impl.DatabaseConnectorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;

public class App {

    static String driver = "org.postgresql.Driver";
    static String login = "postgres";
    static String password = "poseruse";
    static String host = "localhost";
    static int port = 5432;
    static String database = "resolver-restTests";
    List<Registrar> registrars = new ArrayList<Registrar>();
    List<IntelectualEntity> entities = new ArrayList<IntelectualEntity>();
    List<DigitalRepresentation> representations = new ArrayList<DigitalRepresentation>();
    DAOFactory factory = daoFactory();
    DtoBuilder builder = new DtoBuilder();

    public DAOFactory daoFactory() {
        DatabaseConnector connector = DatabaseConnectorFactory.getConnector(driver, host, database, port, login, password);
        return new DAOFactory(connector);
    }

    public static void main(String[] args) {
        try {
            //debugArchiver();
            //debugRegistrar();
            App app = new App();
            app.clearDatabase();
            //app.debugRegistrar();
            app.insertTestData();
        } catch (DatabaseException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void clearDatabase() throws DatabaseException {
        factory.urnDao().deleteAllUrnNbns();
        factory.representationDao().deleteAllRepresentations();
        factory.intelectualEntityDao().deleteAllEntities();
        //kaskadove by se mely pomazat identifikatory intelektualnich entit
        factory.archiverDao().deleteAllArchivers();
        factory.userDao().deleteAllUsers();
    }

    private void debugArchiver() {
        try {
            ArchiverDAO archiverDao = daoFactory().archiverDao();
            //insert
            Archiver archiver = builder.archiverWithoutId();
            long id = archiverDao.insertArchiver(archiver);
            //get
            archiver = archiverDao.getArchiverById(id);
            if (archiver != null) {
                System.err.println("archiver: " + archiver.getName());
            } else {
                System.err.println("App: not found archiver with id " + id);
            }
            //update
            archiver.setDescription("blabla");
            archiverDao.updateArchiver(archiver);
            //delete
            archiverDao.deleteArchiver(id);
        } catch (PersistenceException e) {
            e.printStackTrace();
        } catch (DatabaseException ex) {
            System.err.println("couldn connect to database");
        }
    }

    private void debugRegistrar() {
        try {
            RegistrarDAO registrarDao = daoFactory().registrarDao();
            //insert
            Registrar registrar = builder.registrarWithoutId();
            long id = registrarDao.insertRegistrar(registrar);
//            //get
//            archiver = archiverDao.getArchiverById(id);
//            if (archiver != null) {
//                System.err.println("archiver: " + archiver.getName());
//            } else {
//                System.err.println("App: not found archiver with id " + id);
//            }
//            //update
//            archiver.setDescription("blabla");
//            archiverDao.updateArchiver(archiver);
//            //delete
//            archiverDao.deleteArchiver(id);
        } catch (PersistenceException e) {
            e.printStackTrace();
        } catch (DatabaseException ex) {
            System.err.println("couldn connect to database");
        }

    }

    private void insertTestData() throws Exception {
        try {
            //registrar mzk
            Registrar mzk = new Registrar();
            mzk.setName("MZK");
            mzk.setDescription("Moravská zemská knihovna");
            mzk.setUrnInstitutionCode("BOA001");
            mzk.setId(factory.registrarDao().insertRegistrar(mzk));
            //mzkAdmin
            User mzkAdmin = new User();
            mzkAdmin.setLogin("martin");
            mzkAdmin.setPassword("fdfadfa");
            mzkAdmin.setEmail("martin@home.com");
            mzkAdmin.setId(factory.userDao().insertUser(mzkAdmin));
            factory.registrarDao().addAdminOfRegistrar(mzk.getId(), mzkAdmin.getId());
            //library K4 mzk
            DigitalLibrary mzkK4 = new DigitalLibrary();
            mzkK4.setName("K4");
            mzkK4.setDescription("Kramerius4");
            mzkK4.setRegistrarId(mzk.getId());
            mzkK4.setUrl("http://kramerius4.mzk.cz");
            mzkK4.setId(factory.digitalLibraryDao().insertLibrary(mzkK4));
            //library K3 mzk
            DigitalLibrary mzkK3 = new DigitalLibrary();
            mzkK3.setName("K3");
            mzkK3.setDescription("Kramerius 3");
            mzkK3.setUrl("http://kramerius3.mzk.cz");
            mzkK3.setRegistrarId(mzk.getId());
            mzkK3.setId(factory.digitalLibraryDao().insertLibrary(mzkK3));
            //catalog aleph mzk
            Catalog catalog = builder.CatalogueWithoutIdAndRegistrarId();
            catalog.setRegistrarId(mzk.getId());
            factory.catalogDao().insertCatalog(catalog);

            //registrar nkp
            Registrar nkp = new Registrar();
            nkp.setName("NKP");
            nkp.setDescription("Národní knihovna Praha");
            nkp.setUrnInstitutionCode("ABA001");
            nkp.setId(factory.registrarDao().insertRegistrar(nkp));
            //library K4 nkp
            DigitalLibrary nkpK4 = new DigitalLibrary();
            nkpK4.setName("K4");
            nkpK4.setDescription("Kramerius4");
            nkpK4.setRegistrarId(nkp.getId());
            nkpK4.setUrl("http://kramerius4.nkp.cz");
            nkpK4.setId(factory.digitalLibraryDao().insertLibrary(nkpK4));

            //archivar MK
            Archiver arch = new Archiver();
            arch.setName("MK");
            arch.setDescription("Mala knihovna ");
            arch.setId(factory.archiverDao().insertArchiver(arch));

            //IE babicka
            IntelectualEntity babicka = new IntelectualEntity();
            babicka.setTitle("Babicka");
            babicka.setAlternativeTitle("obrazy z venkovského života");
            babicka.setCreated(new DateTime());
            babicka.setDigitalBorn(false);
            babicka.setEntityType(EntityType.MONOGRAPH);
            babicka.setId(factory.intelectualEntityDao().insertIntelectualEntity(babicka));
            //babicka isbn
            IntEntIdentifier babickaIsbn = new IntEntIdentifier();
            babickaIsbn.setIntEntDbId(babicka.getId());
            babickaIsbn.setType(IntEntIdType.ISBN);
            babickaIsbn.setValue("8090119964");
            factory.intEntIdentifierDao().insertIntEntId(babickaIsbn);
            //publication
            Publication babickaPub = builder.publicationWithoutId();
            babickaPub.setIntEntId(babicka.getId());
            factory.publicationDao().insertPublication(babickaPub);
            //Originator
            Originator babickaAutor = builder.originatorWithoutId();
            babickaAutor.setIntEntId(babicka.getId());
            factory.originatorDao().insertOriginator(babickaAutor);
            //source document
            SourceDocument babickaSrcDoc = builder.sourceDocumentWithoutId();
            babickaSrcDoc.setIntEntId(babicka.getId());
            factory.srcDocDao().insertSrcDoc(babickaSrcDoc);

            //DR babickaMzk
            DigitalRepresentation babickaMzk = new DigitalRepresentation();
            babickaMzk.setArchiverId(mzk.getId());
            babickaMzk.setRegistrarId(mzk.getId());
            babickaMzk.setFinancedFrom("norské fondy");
            babickaMzk.setIntEntId(babicka.getId());
            babickaMzk.setFormat("djvu");
            babickaMzk.setId(factory.representationDao().insertRepresentation(babickaMzk));
            //urn:nbn:cz:boa001-000001
            UrnNbn babickaUrn = new UrnNbn(mzk.getUrnInstitutionCode(), "000001", babickaMzk.getId());
            factory.urnDao().insertUrnNbn(babickaUrn);
            //DR id type=OAI
            DigRepIdentifier babickaMzkDrOaiId = new DigRepIdentifier();
            babickaMzkDrOaiId.setType(DigRepIdType.valueOf("K4_pid"));
            babickaMzkDrOaiId.setValue("uuid:123");
            babickaMzkDrOaiId.setRegistrarId(mzk.getId());
            babickaMzkDrOaiId.setDigRepId(babickaMzk.getId());
            factory.digRepIdDao().insertDigRepId(babickaMzkDrOaiId);
            //DR id type=OTHER
            DigRepIdentifier babickaMzkDrOtherId = new DigRepIdentifier();
            babickaMzkDrOtherId.setType(DigRepIdType.valueOf("signatura"));
            babickaMzkDrOtherId.setValue("3-1275.138");
            babickaMzkDrOtherId.setRegistrarId(mzk.getId());
            babickaMzkDrOtherId.setDigRepId(babickaMzk.getId());
            factory.digRepIdDao().insertDigRepId(babickaMzkDrOtherId);


            //babicka mzk v K4 mzk
            DigitalInstance babickaMzk_v_K4mzk = new DigitalInstance();
            babickaMzk_v_K4mzk.setDigRepId(babickaMzk.getId());
            babickaMzk_v_K4mzk.setLibraryId(mzkK4.getId());
            babickaMzk_v_K4mzk.setUrl("http://kramerius.mzk.cz/search/handle/uuid:123");
            factory.digInstDao().insertDigInstance(babickaMzk_v_K4mzk);
            //babicka mzk v K3 mzk
            DigitalInstance babickaMzk_v_K3mzk = new DigitalInstance();
            babickaMzk_v_K3mzk.setDigRepId(babickaMzk.getId());
            babickaMzk_v_K3mzk.setLibraryId(mzkK3.getId());
            babickaMzk_v_K3mzk.setUrl("http://kramerius3.mzk.cz/kramerius/handle/BOA001/935239");
            factory.digInstDao().insertDigInstance(babickaMzk_v_K3mzk);
            //babicka mzk v K4 nkp
            DigitalInstance babickaMzk_v_K4nkp = new DigitalInstance();
            babickaMzk_v_K4nkp.setDigRepId(babickaMzk.getId());
            babickaMzk_v_K4nkp.setLibraryId(nkpK4.getId());
            babickaMzk_v_K4nkp.setUrl("http://kramerius.nkp.cz/search/handle/uuid:123");
            factory.digInstDao().insertDigInstance(babickaMzk_v_K4nkp);

            //DR babicka nkp
            DigitalRepresentation babickaNkp = new DigitalRepresentation();
            babickaNkp.setArchiverId(nkp.getId());
            babickaNkp.setRegistrarId(nkp.getId());
            babickaNkp.setFinancedFrom("VISK");
            babickaNkp.setIntEntId(babicka.getId());
            babickaNkp.setFormat("jpeg");
            babickaNkp.setId(factory.representationDao().insertRepresentation(babickaNkp));
            UrnNbn babickaNkpUrn = new UrnNbn(nkp.getUrnInstitutionCode(), "000001", babickaNkp.getId());
            factory.urnDao().insertUrnNbn(babickaNkpUrn);
            DigitalInstance babickaNkp_v_K4nkp = new DigitalInstance();
            babickaNkp_v_K4nkp.setDigRepId(babickaNkp.getId());
            babickaNkp_v_K4nkp.setLibraryId(nkpK4.getId());
            babickaNkp_v_K4nkp.setUrl("http://kramerius.nkp.cz/search/handle/uuid:456");
            factory.digInstDao().insertDigInstance(babickaNkp_v_K4nkp);


            //babushka
            IntelectualEntity babushka = new IntelectualEntity();
            babushka.setTitle("Babushka");
            babushka.setAlternativeTitle("kartiny iz selskoj zhizni");
            babushka.setCreated(new DateTime());
            babushka.setDigitalBorn(false);
            babushka.setEntityType(EntityType.MONOGRAPH);
            babushka.setId(factory.intelectualEntityDao().insertIntelectualEntity(babushka));
            //babushka isbn
            IntEntIdentifier babushkaIsbn = new IntEntIdentifier();
            babushkaIsbn.setIntEntDbId(babushka.getId());
            babushkaIsbn.setType(IntEntIdType.ISBN);
            babushkaIsbn.setValue("8090119964");
            factory.intEntIdentifierDao().insertIntEntId(babushkaIsbn);
            //babushka nkp
            DigitalRepresentation babushkaNkp = new DigitalRepresentation();
            babushkaNkp.setArchiverId(nkp.getId());
            babushkaNkp.setRegistrarId(nkp.getId());
            babushkaNkp.setFinancedFrom("VISK");
            babushkaNkp.setIntEntId(babushka.getId());
            babushkaNkp.setFormat("jpeg");
            babushkaNkp.setId(factory.representationDao().insertRepresentation(babushkaNkp));
            factory.representationDao().insertRepresentation(babushkaNkp);
            UrnNbn babushaUrn = new UrnNbn(nkp.getUrnInstitutionCode(), "123456", babushkaNkp.getId());
            factory.urnDao().insertUrnNbn(babushaUrn);

        } catch (RecordNotFoundException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DatabaseException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AlreadyPresentException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Archiver archiverPersisted() throws Exception {
        Archiver archiver = builder.archiverWithoutId();
        long id = factory.archiverDao().insertArchiver(archiver);
        archiver.setId(id);
        return archiver;
    }

    Registrar registrarPersisted() throws Exception {
        Registrar registrar = builder.registrarWithoutId();
        long id = factory.registrarDao().insertRegistrar(registrar);
        registrar.setId(id);
        return registrar;
    }

    public DigitalRepresentation representationPersisted(long registrarId, long intEntId) throws DatabaseException, RecordNotFoundException {
        DigitalRepresentation rep = builder.digRepWithoutIds();
        rep.setIntEntId(intEntId);
        rep.setRegistrarId(registrarId);
        rep.setArchiverId(registrarId);
        Long repId = factory.representationDao().insertRepresentation(rep);
        rep.setId(repId);
        return rep;
    }

    public DigitalLibrary libraryPersisted() throws DatabaseException, RecordNotFoundException, AlreadyPresentException {
        Registrar registrar = builder.registrarWithoutId();
        Long registrarId = factory.registrarDao().insertRegistrar(registrar);
        DigitalLibrary library = builder.digLibraryWithoutIdAndRegistrarId();
        library.setRegistrarId(registrarId);
        long id = factory.digitalLibraryDao().insertLibrary(library);
        library.setId(id);
        return library;
    }

    public IntelectualEntity entityPersisted() throws DatabaseException {
        IntelectualEntity entity = builder.intEntityWithoutId();
        long id = factory.intelectualEntityDao().insertIntelectualEntity(entity);
        entity.setId(id);
        return entity;
    }

    public User userPersisted() throws DatabaseException, AlreadyPresentException {
        User user = builder.userWithoutId();
        long id = factory.userDao().insertUser(user);
        user.setId(id);
        return user;
    }

    private void testMethod() {
        //babickaMzk
    }
}
