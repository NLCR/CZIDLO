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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;

public class App {

    static String driver = "org.postgresql.Driver";
    static String login = "testuser";
    static String password = "testpass";
    static String host = "localhost";
    static int port = 5432;
    static String database = "resolver";
    List<Registrar> registrars = new ArrayList<Registrar>();
    List<IntelectualEntity> entities = new ArrayList<IntelectualEntity>();
    List<DigitalDocument> representations = new ArrayList<DigitalDocument>();
    private final DAOFactory factory;
    private final DtoBuilder builder = new DtoBuilder();

    public App(DatabaseConnector connector) {
        this.factory = new DAOFactory(connector);
    }

    public static void main(String[] args) {
        try {
//            args = new String[1];
//            args[0] = "/home/martin/NetBeansProjects/persistence/src/main/resources/example.properties";
            App app = new App(databaseConnector(args));
            app.clearDatabase();
            System.out.println("database cleared");
            app.insertTestData();
            System.out.println("test data inserted");
        } catch (DatabaseException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static DatabaseConnector databaseConnector(String[] args) throws IOException {
        if (args.length == 0) {
            return DatabaseConnectorFactory.getConnector(driver, host, database, port, login, password);
        } else {
            File propertiesFile = new File(args[0]);
            return DatabaseConnectorFactory.getConnector(propertiesFile);
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
            //superadmin
            User superAdmin = new User();
            superAdmin.setLogin("superAdmin");
            superAdmin.setPassword("superAdminPass");
            superAdmin.setEmail("admin@resolver");
            superAdmin.setAdmin(true);
            superAdmin.setId(factory.userDao().insertUser(superAdmin));
            System.out.println("created user " + superAdmin.getLogin() + ":" + superAdmin.getPassword() + " as superadmin");

            //registrar mzk
            Registrar mzk = new Registrar();
            mzk.setName("MZK");
            mzk.setDescription("Moravská zemská knihovna");
            mzk.setCode("BOA001");
            mzk.setId(factory.registrarDao().insertRegistrar(mzk));
            System.out.println("created registrar MZK with id " + mzk.getId());

            //mzkAdmin
            User mzkAdmin = new User();
            mzkAdmin.setLogin("mzkAdmin");
            mzkAdmin.setPassword("mzkAdminPass");
            mzkAdmin.setEmail("admin@mzk.cz");
            mzkAdmin.setId(factory.userDao().insertUser(mzkAdmin));
            factory.registrarDao().addAdminOfRegistrar(mzk.getId(), mzkAdmin.getId());
            System.out.println("created user " + mzkAdmin.getLogin() + ":" + mzkAdmin.getPassword() + " with access to registrar MZK");

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
            Catalog alephMzk = new Catalog();
            alephMzk.setName("Aleph mzk");
            alephMzk.setUrlPrefix("http://iris.mzk.cz");
            alephMzk.setRegistrarId(mzk.getId());
            factory.catalogDao().insertCatalog(alephMzk);

            //registrar nkp
            Registrar nkp = new Registrar();
            nkp.setName("NKP");
            nkp.setDescription("Národní knihovna Praha");
            nkp.setCode("ABA001");
            nkp.setId(factory.registrarDao().insertRegistrar(nkp));
            System.out.println("created registrar NKP with id " + nkp.getId());
            //catalog aleph nkp
            Catalog alephNkp = new Catalog();
            alephNkp.setName("Aleph nkp");
            alephNkp.setUrlPrefix("http://hades.mzk.cz");
            alephNkp.setRegistrarId(nkp.getId());
            factory.catalogDao().insertCatalog(alephNkp);

            //nkpAdmin
            User nkpAdmin = new User();
            nkpAdmin.setLogin("nkpAdmin");
            nkpAdmin.setPassword("nkpAdminPass");
            nkpAdmin.setEmail("admin@nkp.cz");
            nkpAdmin.setId(factory.userDao().insertUser(nkpAdmin));
            factory.registrarDao().addAdminOfRegistrar(nkp.getId(), nkpAdmin.getId());
            System.out.println("created user " + nkpAdmin.getLogin() + ":" + nkpAdmin.getPassword() + " with access to registrar NKP");

            //library K4 nkp
            DigitalLibrary nkpK4 = new DigitalLibrary();
            nkpK4.setName("K4");
            nkpK4.setDescription("Kramerius4");
            nkpK4.setRegistrarId(nkp.getId());
            nkpK4.setUrl("http://kramerius4.nkp.cz");
            nkpK4.setId(factory.digitalLibraryDao().insertLibrary(nkpK4));

            //archivar MK
            Archiver archMk = new Archiver();
            archMk.setName("MK");
            archMk.setDescription("Mala knihovna ");
            archMk.setId(factory.archiverDao().insertArchiver(archMk));
            System.out.println("created archiver MK with id " + archMk.getId());

            //archivar MK2
            Archiver archMk2 = new Archiver();
            archMk2.setName("MK2");
            archMk2.setDescription("Mala knihovna 2");
            archMk2.setId(factory.archiverDao().insertArchiver(archMk2));
            System.out.println("created archiver MK2 with id " + archMk2.getId());

            //archivar MK3
            Archiver archMk3 = new Archiver();
            archMk3.setName("MK3");
            archMk3.setDescription("Mala knihovna 3");
            archMk3.setId(factory.archiverDao().insertArchiver(archMk3));
            System.out.println("created archiver MK3 with id " + archMk3.getId());

            //IE babicka
            IntelectualEntity babicka = new IntelectualEntity();
            babicka.setCreated(new DateTime());
            babicka.setDigitalBorn(false);
            babicka.setEntityType(EntityType.MONOGRAPH);
            babicka.setDocumentType("kniha");
            babicka.setOtherOriginator("Adolf Kašpar");
            babicka.setId(factory.intelectualEntityDao().insertIntelectualEntity(babicka));
            //title
            IntEntIdentifier babickaTitle = new IntEntIdentifier();
            babickaTitle.setIntEntDbId(babicka.getId());
            babickaTitle.setType(IntEntIdType.TITLE);
            babickaTitle.setValue("Babicka");
            factory.intEntIdentifierDao().insertIntEntId(babickaTitle);
            //subTitle
            IntEntIdentifier babickaSubtitle = new IntEntIdentifier();
            babickaSubtitle.setIntEntDbId(babicka.getId());
            babickaSubtitle.setType(IntEntIdType.SUB_TITLE);
            babickaSubtitle.setValue("obrazy z venkovského života");
            factory.intEntIdentifierDao().insertIntEntId(babickaSubtitle);
            //isbn
            IntEntIdentifier babickaIsbn = new IntEntIdentifier();
            babickaIsbn.setIntEntDbId(babicka.getId());
            babickaIsbn.setType(IntEntIdType.ISBN);
            babickaIsbn.setValue("8090119964");
            factory.intEntIdentifierDao().insertIntEntId(babickaIsbn);

            //publication
            Publication babickaPub = builder.publicationWithoutId();
            babickaPub.setIntEntId(babicka.getId());
            factory.publicationDao().insertPublication(babickaPub);
            //primary originator
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
            babickaMzk.setResolutionHorizontal(1280);
            babickaMzk.setResolutionVertical(1024);
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
            babushka.setCreated(new DateTime());
            babushka.setDigitalBorn(false);
            babushka.setEntityType(EntityType.MONOGRAPH);
            babushka.setDocumentType("kniga");
            babushka.setOtherOriginator("Adolf Kašpar");
            babushka.setId(factory.intelectualEntityDao().insertIntelectualEntity(babushka));
            //title
            IntEntIdentifier babushkaTitle = new IntEntIdentifier();
            babushkaTitle.setIntEntDbId(babushka.getId());
            babushkaTitle.setType(IntEntIdType.TITLE);
            babushkaTitle.setValue("Babushka");
            factory.intEntIdentifierDao().insertIntEntId(babushkaTitle);
            //subTitle
            IntEntIdentifier babushkaSubTitle = new IntEntIdentifier();
            babushkaSubTitle.setIntEntDbId(babushka.getId());
            babushkaSubTitle.setType(IntEntIdType.SUB_TITLE);
            babushkaSubTitle.setValue("kartiny iz selskoj zhizni");
            factory.intEntIdentifierDao().insertIntEntId(babushkaSubTitle);
            //isbn
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
}
