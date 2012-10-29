package cz.nkp.urnnbn.core.persistence;

import cz.nkp.urnnbn.core.DigDocIdType;
import cz.nkp.urnnbn.core.DtoBuilder;
import cz.nkp.urnnbn.core.EntityType;
import cz.nkp.urnnbn.core.IntEntIdType;
import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.UrnNbnRegistrationMode;
import cz.nkp.urnnbn.core.dto.Archiver;
import cz.nkp.urnnbn.core.dto.Catalog;
import cz.nkp.urnnbn.core.dto.DigDocIdentifier;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordReferencedException;
import cz.nkp.urnnbn.core.persistence.impl.DatabaseConnectorFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        try {
            factory.urnDao().deleteAllUrnNbns();
            factory.documentDao().deleteAllDocuments();
            factory.intelectualEntityDao().deleteAllEntities();
            //kaskadove by se mely pomazat identifikatory intelektualnich entit
            factory.archiverDao().deleteAllArchivers();
            factory.userDao().deleteAllUsers();
        } catch (RecordReferencedException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void insertTestData() throws Exception {
        try {

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

            Map<String, Registrar> registrarMap = insertRegistrars();

            //superadmin
            User superAdmin = new User();
            superAdmin.setLogin("superAdmin");
            superAdmin.setPassword("superAdminPass");
            superAdmin.setEmail("admin@resolver");
            superAdmin.setAdmin(true);
            superAdmin.setId(factory.userDao().insertUser(superAdmin));
            System.out.println("created user " + superAdmin.getLogin() + ":" + superAdmin.getPassword() + " as superadmin");

            //registrar tst03
            Registrar tst03 = registrarMap.get("tst03");
            //tst03 Admin
            User tst03Admin = createUser("tst03Admin", "tst03AdminPass", "admin@somewhere.cz");
            grantRegistrarToUser(tst03Admin, tst03);

            //registrar mzk
            Registrar mzk = registrarMap.get("tst02");
            //mzkAdmin
            User mzkAdmin = createUser("mzkAdmin", "mzkAdminPass", "admin@mzk.cz");
            grantRegistrarToUser(mzkAdmin, mzk);
            //library K4 mzk
            DigitalLibrary mzkK4 = insertLibrary("Kramerius 4", "http://kramerius4.mzk.cz", "testovací knihovna", mzk);
            //library K3 mzk
            DigitalLibrary mzkK3 = insertLibrary("Kramerius 3", "http://kramerius3.mzk.cz", "testovací knihovna", mzk);
            //catalog aleph mzk
            Catalog alephMzk = insertCatalog("Aleph mzk", "http://iris.mzk.cz", mzk);

            //registrar nkp
            Registrar nkp = registrarMap.get("tst01");
            //nkpAdmin
            User nkpAdmin = createUser("nkpAdmin", "nkpAdminPass", "admin@nkp.cz");
            grantRegistrarToUser(nkpAdmin, nkp);
            //library K4 nkp
            DigitalLibrary nkpK4 = insertLibrary("Kramerius 4", "testovací knihovna", "http://kramerius4.nkp.cz", nkp);
            //catalog aleph nkp
            Catalog alephNkp = insertCatalog("Aleph nkp", "http://hades.mzk.cz", nkp);

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
            babickaTitle.setValue("Babička");
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
            babickaMzk.setArchiverId(archMk.getId());
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
            factory.digDocIdDao().insertDigDocId(babickaMzkDrOaiId);
            //DR id type=OTHER
            DigDocIdentifier babickaMzkDrOtherId = new DigDocIdentifier();
            babickaMzkDrOtherId.setType(DigDocIdType.valueOf("signatura"));
            babickaMzkDrOtherId.setValue("3-1275.138");
            babickaMzkDrOtherId.setRegistrarId(mzk.getId());
            babickaMzkDrOtherId.setDigDocId(babickaMzk.getId());
            factory.digDocIdDao().insertDigDocId(babickaMzkDrOtherId);


            //babicka mzk v K4 mzk
            DigitalInstance babickaMzk_v_K4mzk = new DigitalInstance();
            babickaMzk_v_K4mzk.setDigDocId(babickaMzk.getId());
            babickaMzk_v_K4mzk.setLibraryId(mzkK4.getId());
            babickaMzk_v_K4mzk.setUrl("http://kramerius.mzk.cz/search/handle/uuid:123");
            babickaMzk_v_K4mzk.setFormat("djvu");
            babickaMzk_v_K4mzk.setAccessibility("veřejně přístupné");
            babickaMzk_v_K4mzk.setActive(Boolean.TRUE);
            factory.digInstDao().insertDigInstance(babickaMzk_v_K4mzk);
            //babicka mzk v K3 mzk
            DigitalInstance babickaMzk_v_K3mzk = new DigitalInstance();
            babickaMzk_v_K3mzk.setDigDocId(babickaMzk.getId());
            babickaMzk_v_K3mzk.setLibraryId(mzkK3.getId());
            babickaMzk_v_K3mzk.setUrl("http://kramerius3.mzk.cz/kramerius/handle/BOA001/935239");
            babickaMzk_v_K3mzk.setActive(Boolean.TRUE);
            factory.digInstDao().insertDigInstance(babickaMzk_v_K3mzk);
            //babicka mzk v K4 nkp
            DigitalInstance babickaMzk_v_K4nkp = new DigitalInstance();
            babickaMzk_v_K4nkp.setDigDocId(babickaMzk.getId());
            babickaMzk_v_K4nkp.setLibraryId(nkpK4.getId());
            babickaMzk_v_K4nkp.setUrl("http://kramerius.nkp.cz/search/handle/uuid:123");
            babickaMzk_v_K4nkp.setActive(Boolean.TRUE);
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
            babickaNkp_v_K4nkp.setActive(Boolean.TRUE);
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

    public DigitalDocument documentPersisted(long registrarId, long intEntId) throws DatabaseException, RecordNotFoundException {
        DigitalDocument doc = new DigitalDocument();
        doc.setIntEntId(intEntId);
        doc.setRegistrarId(registrarId);
        doc.setArchiverId(registrarId);
        Long repId = factory.documentDao().insertDocument(doc);
        doc.setId(repId);
        return doc;
    }

    public DigitalLibrary libraryPersisted() throws DatabaseException, RecordNotFoundException, AlreadyPresentException {
        Registrar registrar = builder.registrarWithoutId();
        Long registrarId = factory.registrarDao().insertRegistrar(registrar);
        DigitalLibrary library = builder.digLibraryWithoutIdAndRegistrarId();
        library.setRegistrarId(registrarId);
        long id = factory.diglLibDao().insertLibrary(library);
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

    private Map<String, Registrar> insertRegistrars() throws DatabaseException, AlreadyPresentException, RecordNotFoundException {
        Map<String, Registrar> result = new HashMap<String, Registrar>();
        Registrar nk =
                insertRegistrar("ABA001", "Národní knihovna České republiky", "", true);
        insertLibrary("Kramerius Národní knihovna ČR", "http://kramerius.nkp.cz", "kod_rd:dkknkcr", nk);
        addToMap(nk, result);
        addToMap(insertRegistrarWithDefaultLibrary("ABA006", "název chybí", "TODO:doplnit název"), result);
        addToMap(insertRegistrarWithDefaultLibrary("ABA007", "název chybí", "TODO:doplnit název"), result);
        addToMap(insertRegistrarWithDefaultLibrary("ABA008", "název chybí", "TODO:doplnit název"), result);
        addToMap(insertRegistrarWithDefaultLibrary("ABA010", "název chybí", "TODO:doplnit název"), result);
        addToMap(insertRegistrarWithDefaultLibrary("ABE045", "Vojenský historický ústav – knihovna", ""), result);
        addToMap(insertRegistrarWithDefaultLibrary("ABC135", "název chybí", "TODO:doplnit název"), result);
        addToMap(insertRegistrarWithDefaultLibrary("ABE190", "Gender Studies, o.p.s. – knihovna", ""), result);
        addToMap(insertRegistrarWithDefaultLibrary("ABE310", "název chybí", "TODO:doplnit název"), result);
        addToMap(insertRegistrarWithDefaultLibrary("ABE323", "název chybí", "TODO:doplnit název"), result);
        addToMap(insertRegistrarWithDefaultLibrary("ABE336", "název chybí", "TODO:doplnit název"), result);
        addToMap(insertRegistrarWithDefaultLibrary("ABE343", "Národní archiv v Praze – knihovna", ""), result);
        Registrar mzk =
                insertRegistrar("BOA001", "Moravská zemská knihovna", "");
        insertLibrary("Kramerius 4", "http://kramerius.mzk.cz", "aktuální verze knihovny Kramerius", mzk);
        insertLibrary("Kramerius 3", "http://kramerius3.mzk.cz", "předchozí verze knihovny Kramerius", mzk);
        addToMap(mzk, result);
        addToMap(insertRegistrarWithDefaultLibrary("BVE301", "Regionální muzeum v Mikulově", ""), result);
        addToMap(insertRegistrarWithDefaultLibrary("BVE302", "Regionální muzeum v Mikulově", ""), result);
        addToMap(insertRegistrarWithDefaultLibrary("CBE301", "Jihočeské muzeum v Českých Budějovicích - knihovna", ""), result);
        addToMap(insertRegistrarWithDefaultLibrary("GHE302", "Krajské muzeum Karlovarského kraje, příspěvková organizace, MUZEUM CHEB", ""), result);
        addToMap(insertRegistrarWithDefaultLibrary("HKA001", "název chybí", "TODO:doplnit název"), result);
        addToMap(insertRegistrarWithDefaultLibrary("HKE302", "Muzeum východních Čech v Hradci Králové - společenskovědní knihovna", ""), result);
        addToMap(insertRegistrarWithDefaultLibrary("JHE301", "Muzeum Jindřichohradecka – odborná knihovna", ""), result);
        addToMap(insertRegistrarWithDefaultLibrary("KMG001", "název chybí", "TODO:doplnit název"), result);
        addToMap(insertRegistrarWithDefaultLibrary("KTG503", "Městská knihovna Horažďovice", ""), result);
        addToMap(insertRegistrarWithDefaultLibrary("KVE303", "Krajské muzeum Karlovarského kraje,p.o.,Muzeum Karlovy Vary - Muzejní knihovna", ""), result);
        addToMap(insertRegistrarWithDefaultLibrary("KVG001", "název chybí", "TODO:doplnit název"), result);
        addToMap(insertRegistrarWithDefaultLibrary("LIA001", "Krajská vědecká knihovna v Liberci", ""), result);
        addToMap(insertRegistrarWithDefaultLibrary("LID001", "Technická univerzita v Liberci - Univerzitní knihovna", ""), result);
        addToMap(insertRegistrarWithDefaultLibrary("OLA001", "Vědecká knihovna v Olomouci", ""), result);
        addToMap(insertRegistrarWithDefaultLibrary("OSA001", "název chybí", "TODO:doplnit název"), result);
        addToMap(insertRegistrarWithDefaultLibrary("PAE302", "Státní okresní archiv Pardubice", ""), result);
        addToMap(insertRegistrarWithDefaultLibrary("PNA001", "Studijní a vědecká knihovna Plzeňského kraje", ""), result);
        addToMap(insertRegistrarWithDefaultLibrary("ROE301", "Muzeum Dr.Bohuslava Horáka v Rokycanech", ""), result);
        addToMap(insertRegistrarWithDefaultLibrary("ULG001", "Severočeská vědecká knihovna v Ústí nad Labem", ""), result);
        addToMap(insertRegistrarWithDefaultLibrary("ZLG001", "Krajská knihovna Františka Bartoše, příspěvková organizace", ""), result);//testovaci registratori
        addToMap(insertRegistrarWithDefaultLibrary("TST01", "Národní knihovna České republiky - test", "testovací registrátor"), result);
        addToMap(insertRegistrarWithDefaultLibrary("TST02", "Moravská zemská knihovna - test", "testovací registrátor"), result);
        addToMap(insertRegistrarWithDefaultLibrary("TST03", "Testovací registrátor", ""), result);
        //katalogy
        insertCatalog("Aleph NKP", "http://aleph.nkp.cz", nk);
        insertCatalog("Aleph MZK", "http://aleph.mzk.cz", mzk);
        User legacyDataImporter = createUser("legacy", "todo_zmenit", "legacy@resolver.nkp.cz");
        for (Registrar registrar : result.values()) {
            grantRegistrarToUser(legacyDataImporter, registrar);
        }
        return result;
    }

    private void addToMap(Registrar registrar, Map<String, Registrar> result) {
        result.put(registrar.getCode().toString(), registrar);
    }

    private Registrar insertRegistrar(String code, String name, String description) throws DatabaseException, AlreadyPresentException {
        //TODO: pozdeji rucne odstranit pravo registrovat volne urn:nbn vsem registratorum
        return insertRegistrar(code, name, description, true);
    }

    private Registrar insertRegistrar(String code, String name, String description, boolean allowedToRegisterFreeUrn) throws DatabaseException, AlreadyPresentException {
        Registrar result = new Registrar();
        result.setName(name);
        result.setDescription(description);
        result.setCode(RegistrarCode.valueOf(code));
        result.setRegistrationModeAllowed(UrnNbnRegistrationMode.BY_REGISTRAR, allowedToRegisterFreeUrn);
        result.setId(factory.registrarDao().insertRegistrar(result));
        System.out.println("created registrar " + result.getName() + " with code " + result.getCode());
        return result;
    }

    private Registrar insertRegistrarWithDefaultLibrary(String code, String name, String description) throws DatabaseException, AlreadyPresentException, RecordNotFoundException {
        Registrar registrar = insertRegistrar(code, name, description);
        insertLibrary(code + " default library", null, "defaultní digitální knihovna registrátora, prosím o editaci", registrar);
        return registrar;
    }

    private DigitalLibrary insertLibrary(String name, String url, String decription, Registrar registrar) throws DatabaseException, RecordNotFoundException {
        DigitalLibrary result = new DigitalLibrary();
        result.setName(name);
        result.setUrl(url);
        result.setDescription(decription);
        result.setRegistrarId(registrar.getId());
        result.setId(factory.diglLibDao().insertLibrary(result));
        System.out.println("created digital library " + result.getName() + " of registrar " + registrar.getName());
        return result;
    }

    private Catalog insertCatalog(String name, String url, Registrar registrar) throws DatabaseException, RecordNotFoundException, AlreadyPresentException {
        Catalog result = new Catalog();
        result.setName(name);
        result.setUrlPrefix(url);
        result.setRegistrarId(registrar.getId());
        factory.catalogDao().insertCatalog(result);
        System.out.println("created catalog " + result.getName() + " of registrar " + registrar.getName());
        return result;
    }

    private User createUser(String login, String password, String email) throws DatabaseException, AlreadyPresentException {
        User user = new User();
        user.setLogin(login);
        user.setPassword(password);
        user.setEmail(email);
        user.setId(factory.userDao().insertUser(user));
        System.out.println("created user " + user.getLogin() + ":" + user.getPassword());
        return user;
    }

    private void grantRegistrarToUser(User user, Registrar registrar) throws DatabaseException, RecordNotFoundException, AlreadyPresentException {
        factory.userDao().insertAdministrationRight(registrar.getId(), user.getId());
        System.out.println("granted access to registrar '" + registrar.getName() + "' with code " + registrar.getCode() + " to user " + user.getLogin());
    }
}
