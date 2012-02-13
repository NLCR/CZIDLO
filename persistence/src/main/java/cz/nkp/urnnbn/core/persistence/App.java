package cz.nkp.urnnbn.core.persistence;

import cz.nkp.urnnbn.core.DigDocIdType;
import cz.nkp.urnnbn.core.DtoBuilder;
import cz.nkp.urnnbn.core.EntityType;
import cz.nkp.urnnbn.core.IntEntIdType;
import cz.nkp.urnnbn.core.dto.User;

import cz.nkp.urnnbn.core.dto.Archiver;
import cz.nkp.urnnbn.core.dto.Catalog;
import cz.nkp.urnnbn.core.dto.DigDocIdentifier;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.UrnNbn;
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
    List<DigitalDocument> representations = new ArrayList<DigitalDocument>();
    DAOFactory factory = daoFactory();
    DtoBuilder builder = new DtoBuilder();

    public DAOFactory daoFactory() {
        DatabaseConnector connector = DatabaseConnectorFactory.getConnector(driver, host, database, port, login, password);
        return new DAOFactory(connector);
    }

    public static void main(String[] args) {
        try {
            App app = new App();
            app.clearDatabase();
            app.insertTestData();
        } catch (DatabaseException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void clearDatabase() throws DatabaseException {
        factory.urnDao().deleteAllUrnNbns();
        factory.documentDao().deleteAllDocuments();
        factory.intelectualEntityDao().deleteAllEntities();
        //kaskadove by se mely pomazat identifikatory intelektualnich entit
        factory.archiverDao().deleteAllArchivers();
        factory.userDao().deleteAllUsers();
    }

    private void insertTestData() throws Exception {
        try {
            //registrar mzk
            Registrar mzk = new Registrar();
            mzk.setName("MZK");
            mzk.setDescription("Moravská zemská knihovna");
            mzk.setCode("BOA001");
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
            nkp.setCode("ABA001");
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
            babicka.setDocumentType("kniha");
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

            //DR babickaMzk
            DigitalDocument babickaMzk = new DigitalDocument();
            babickaMzk.setIntEntId(babicka.getId());
            babickaMzk.setArchiverId(mzk.getId());
            babickaMzk.setRegistrarId(mzk.getId());
            babickaMzk.setExtent("223 stran");
            babickaMzk.setFinancedFrom("norské fondy");
            babickaMzk.setContractNumber("123");
            babickaMzk.setFormat("JPEG");
            babickaMzk.setFormatVersion("1.0");
            babickaMzk.setResolutionWidth(1280);
            babickaMzk.setResolutionHeight(1024);
            babickaMzk.setCompression("LZW");
            babickaMzk.setCompressionRatio(Double.valueOf(0.3));
            babickaMzk.setColorModel("RGB");
            babickaMzk.setColorDepth(24);
            babickaMzk.setIccProfile("some ICC profile");
            babickaMzk.setPictureWidth(600);
            babickaMzk.setPictureHeight(1000);
            babickaMzk.setId(factory.documentDao().insertDocument(babickaMzk));
            //urn:nbn:cz:boa001-000001
            UrnNbn babickaUrn = new UrnNbn(mzk.getCode(), "000001", babickaMzk.getId());
            factory.urnDao().insertUrnNbn(babickaUrn);
            //DR id type=OAI
            DigDocIdentifier babickaMzkDrOaiId = new DigDocIdentifier();
            babickaMzkDrOaiId.setType(DigDocIdType.valueOf("K4_pid"));
            babickaMzkDrOaiId.setValue("uuid:123");
            babickaMzkDrOaiId.setRegistrarId(mzk.getId());
            babickaMzkDrOaiId.setDigDocId(babickaMzk.getId());
            factory.digRepIdDao().insertDigDocId(babickaMzkDrOaiId);
            //DR id type=OTHER
            DigDocIdentifier babickaMzkDrOtherId = new DigDocIdentifier();
            babickaMzkDrOtherId.setType(DigDocIdType.valueOf("signatura"));
            babickaMzkDrOtherId.setValue("3-1275.138");
            babickaMzkDrOtherId.setRegistrarId(mzk.getId());
            babickaMzkDrOtherId.setDigDocId(babickaMzk.getId());
            factory.digRepIdDao().insertDigDocId(babickaMzkDrOtherId);


            //babicka mzk v K4 mzk
            DigitalInstance babickaMzk_v_K4mzk = new DigitalInstance();
            babickaMzk_v_K4mzk.setDigDocId(babickaMzk.getId());
            babickaMzk_v_K4mzk.setLibraryId(mzkK4.getId());
            babickaMzk_v_K4mzk.setUrl("http://kramerius.mzk.cz/search/handle/uuid:123");
            babickaMzk_v_K4mzk.setFormat("djvu");
            babickaMzk_v_K4mzk.setAccessibility("veřejně přístupné");
            factory.digInstDao().insertDigInstance(babickaMzk_v_K4mzk);
            //babicka mzk v K3 mzk
            DigitalInstance babickaMzk_v_K3mzk = new DigitalInstance();
            babickaMzk_v_K3mzk.setDigDocId(babickaMzk.getId());
            babickaMzk_v_K3mzk.setLibraryId(mzkK3.getId());
            babickaMzk_v_K3mzk.setUrl("http://kramerius3.mzk.cz/kramerius/handle/BOA001/935239");
            factory.digInstDao().insertDigInstance(babickaMzk_v_K3mzk);
            //babicka mzk v K4 nkp
            DigitalInstance babickaMzk_v_K4nkp = new DigitalInstance();
            babickaMzk_v_K4nkp.setDigDocId(babickaMzk.getId());
            babickaMzk_v_K4nkp.setLibraryId(nkpK4.getId());
            babickaMzk_v_K4nkp.setUrl("http://kramerius.nkp.cz/search/handle/uuid:123");
            factory.digInstDao().insertDigInstance(babickaMzk_v_K4nkp);

            //DR babicka nkp
            DigitalDocument babickaNkp = new DigitalDocument();
            babickaNkp.setArchiverId(nkp.getId());
            babickaNkp.setRegistrarId(nkp.getId());
            babickaNkp.setFinancedFrom("VISK");
            babickaNkp.setIntEntId(babicka.getId());
            babickaNkp.setContractNumber("123456");
            babickaNkp.setId(factory.documentDao().insertDocument(babickaNkp));
            UrnNbn babickaNkpUrn = new UrnNbn(nkp.getCode(), "000001", babickaNkp.getId());
            factory.urnDao().insertUrnNbn(babickaNkpUrn);
            DigitalInstance babickaNkp_v_K4nkp = new DigitalInstance();
            babickaNkp_v_K4nkp.setDigDocId(babickaNkp.getId());
            babickaNkp_v_K4nkp.setLibraryId(nkpK4.getId());
            babickaNkp_v_K4nkp.setFormat("jpg");
            babickaNkp_v_K4nkp.setAccessibility("public");
            babickaNkp_v_K4nkp.setUrl("http://kramerius.nkp.cz/search/handle/uuid:456");
            factory.digInstDao().insertDigInstance(babickaNkp_v_K4nkp);


            //babushka
            IntelectualEntity babushka = new IntelectualEntity();
            babushka.setTitle("Babushka");
            babushka.setAlternativeTitle("kartiny iz selskoj zhizni");
            babushka.setCreated(new DateTime());
            babushka.setDigitalBorn(false);
            babushka.setEntityType(EntityType.MONOGRAPH);
            babushka.setDocumentType("kniga");
            babushka.setId(factory.intelectualEntityDao().insertIntelectualEntity(babushka));
            //babushka isbn
            IntEntIdentifier babushkaIsbn = new IntEntIdentifier();
            babushkaIsbn.setIntEntDbId(babushka.getId());
            babushkaIsbn.setType(IntEntIdType.ISBN);
            babushkaIsbn.setValue("8090119964");
            factory.intEntIdentifierDao().insertIntEntId(babushkaIsbn);
            //babushka nkp
            DigitalDocument babushkaNkp = new DigitalDocument();
            babushkaNkp.setArchiverId(nkp.getId());
            babushkaNkp.setRegistrarId(nkp.getId());
            babushkaNkp.setFinancedFrom("VISK");
            babushkaNkp.setIntEntId(babushka.getId());
            babushkaNkp.setId(factory.documentDao().insertDocument(babushkaNkp));
            factory.documentDao().insertDocument(babushkaNkp);
            UrnNbn babushaUrn = new UrnNbn(nkp.getCode(), "123456", babushkaNkp.getId());
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

    public DigitalDocument representationPersisted(long registrarId, long intEntId) throws DatabaseException, RecordNotFoundException {
        DigitalDocument rep = builder.digDocWithoutIds();
        rep.setIntEntId(intEntId);
        rep.setRegistrarId(registrarId);
        rep.setArchiverId(registrarId);
        Long repId = factory.documentDao().insertDocument(rep);
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
