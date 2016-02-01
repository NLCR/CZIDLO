package cz.nkp.urnnbn.core.persistence.impl.postgres;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;

import cz.nkp.urnnbn.core.CountryCode;
import cz.nkp.urnnbn.core.EntityType;
import cz.nkp.urnnbn.core.IntEntIdType;
import cz.nkp.urnnbn.core.UrnNbnExport;
import cz.nkp.urnnbn.core.UrnNbnExportFilter;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;

public class UrnNbnExportTest extends AbstractDaoTest {

    public UrnNbnExportTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public IntelectualEntity entityPersisted(EntityType type) throws DatabaseException {
        IntelectualEntity entity = builder.intEntityWithoutId();
        entity.setEntityType(type);
        long id = entityDao.insertIntelectualEntity(entity);
        try {
            return entityDao.getEntityByDbId(id);
        } catch (RecordNotFoundException ex) {
            Logger.getLogger(AbstractDaoTest.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private List<String> allEntityTypes() {
        List<String> result = new ArrayList<String>();
        for (EntityType type : EntityType.values()) {
            result.add(type.name());
        }
        return result;
    }

    private UrnNbn urnNbnPersisted(EntityType type, Registrar registrar, String documentCode) {
        try {
            IntelectualEntity entity = entityPersisted(type);
            DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
            UrnNbn urnNbn = new UrnNbn(registrar.getCode(), documentCode, doc.getId(), null);
            urnDao.insertUrnNbn(urnNbn);
            return urnNbn;
        } catch (Exception e) {
            return null;
        }
    }

    private List<String> toRegistrarList(Registrar... registrars) {
        List<String> result = new ArrayList<String>();
        for (Registrar registrar : registrars) {
            result.add(registrar.getCode().toString());
        }
        return result;
    }

    private List<String> toEntityTypeList(EntityType... types) {
        List<String> result = new ArrayList<String>();
        for (EntityType type : types) {
            result.add(type.name());
        }
        return result;
    }

    void busyWait(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    public void testSelectByCriteria_timestamps() throws Exception {
        Registrar registrar = registrarPersisted();
        // before
        urnNbnPersisted(EntityType.MONOGRAPH, registrar, "000001");
        // start
        DateTime begin = new DateTime();
        UrnNbn shouldBeReturned = urnNbnPersisted(EntityType.MONOGRAPH, registrar, "000002");
        DateTime end = new DateTime();
        // after
        urnNbnPersisted(EntityType.MONOGRAPH, registrar, "000003");
        UrnNbnExportFilter filter = new UrnNbnExportFilter(begin, end, toRegistrarList(registrar), allEntityTypes(), false, false, false, true, true);
        List<UrnNbnExport> selected = urnDao.selectByCriteria(CountryCode.getCode(), filter, false);
        assertEquals(1, selected.size());
        UrnNbnExport export = selected.get(0);
        assertEquals(shouldBeReturned.toString(), export.getUrnNbn());
    }

    public void testSelectByCriteria_timestampsAndRegistrars() throws Exception {
        Registrar registrar1 = registrarPersisted();
        Registrar registrar2 = registrarPersisted();
        // before
        urnNbnPersisted(EntityType.MONOGRAPH, registrar1, "000001");
        // start
        DateTime begin = new DateTime();
        UrnNbn first = urnNbnPersisted(EntityType.MONOGRAPH, registrar1, "000002");
        UrnNbn second = urnNbnPersisted(EntityType.MONOGRAPH, registrar2, "000002");
        DateTime end = new DateTime();
        // busyWait(2000);
        // after
        urnNbnPersisted(EntityType.MONOGRAPH, registrar1, "000003");
        // both registrars
        List<UrnNbnExport> selected = urnDao
                .selectByCriteria(CountryCode.getCode(), new UrnNbnExportFilter(begin, end, toRegistrarList(registrar1, registrar2),
                        allEntityTypes(), false, false, false, true, true), false);
        assertEquals(2, selected.size());
        // one registrar
        selected = urnDao.selectByCriteria(CountryCode.getCode(), new UrnNbnExportFilter(begin, end, toRegistrarList(registrar1),
                toEntityTypeList(EntityType.MONOGRAPH), false, false, false, true, true), false);
        assertEquals(1, selected.size());
    }

    public void testSelectByCriteria_entityTypes() throws Exception {
        Registrar registrar1 = registrarPersisted();
        // before
        urnNbnPersisted(EntityType.MONOGRAPH, registrar1, "000001");
        // start
        DateTime begin = new DateTime();
        UrnNbn first = urnNbnPersisted(EntityType.MONOGRAPH, registrar1, "000002");
        UrnNbn second = urnNbnPersisted(EntityType.PERIODICAL, registrar1, "000003");
        DateTime end = new DateTime();
        // busyWait(2000);
        // after
        urnNbnPersisted(EntityType.MONOGRAPH, registrar1, "000004");
        // both registrars
        List<UrnNbnExport> selected = urnDao.selectByCriteria(CountryCode.getCode(), new UrnNbnExportFilter(begin, end, toRegistrarList(registrar1),
                allEntityTypes(), false, false, false, true, true), false);
        assertEquals(2, selected.size());
        // one registrar
        selected = urnDao.selectByCriteria(CountryCode.getCode(), new UrnNbnExportFilter(begin, end, toRegistrarList(registrar1),
                toEntityTypeList(EntityType.MONOGRAPH), false, false, false, true, true), false);
        assertEquals(1, selected.size());
    }

    private void setIntEntId(UrnNbn urn, IntEntIdType type, String value) {
        try {
            DigitalDocument digDoc = digDocDao.getDocumentByDbId(urn.getDigDocId());
            IntEntIdentifier id = new IntEntIdentifier();
            id.setIntEntDbId(digDoc.getIntEntId());
            id.setType(type);
            id.setValue(value);
            intEntIdDao.insertIntEntId(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void testSelectByCriteria_ccnbIssnIsbn() throws Exception {
        Registrar registrar1 = registrarPersisted();
        // start
        DateTime begin = new DateTime();
        UrnNbn noId = urnNbnPersisted(EntityType.MONOGRAPH, registrar1, "000001");
        UrnNbn onlyCcnb = urnNbnPersisted(EntityType.MONOGRAPH, registrar1, "000002");
        setIntEntId(onlyCcnb, IntEntIdType.CCNB, "neco");
        UrnNbn onlyIssn = urnNbnPersisted(EntityType.MONOGRAPH, registrar1, "000003");
        setIntEntId(onlyIssn, IntEntIdType.ISSN, "neco");
        UrnNbn onlyIsbn = urnNbnPersisted(EntityType.MONOGRAPH, registrar1, "000004");
        setIntEntId(onlyIsbn, IntEntIdType.ISBN, "neco");
        UrnNbn ccnbIssn = urnNbnPersisted(EntityType.MONOGRAPH, registrar1, "000005");
        setIntEntId(ccnbIssn, IntEntIdType.CCNB, "neco");
        setIntEntId(ccnbIssn, IntEntIdType.ISSN, "neco");
        UrnNbn ccnbIsbn = urnNbnPersisted(EntityType.MONOGRAPH, registrar1, "000006");
        setIntEntId(ccnbIsbn, IntEntIdType.CCNB, "neco");
        setIntEntId(ccnbIsbn, IntEntIdType.ISBN, "neco");
        UrnNbn issnIsbn = urnNbnPersisted(EntityType.MONOGRAPH, registrar1, "000007");
        setIntEntId(issnIsbn, IntEntIdType.ISSN, "neco");
        setIntEntId(issnIsbn, IntEntIdType.ISBN, "neco");
        UrnNbn all = urnNbnPersisted(EntityType.MONOGRAPH, registrar1, "000008");
        setIntEntId(all, IntEntIdType.CCNB, "neco");
        setIntEntId(all, IntEntIdType.ISSN, "neco");
        setIntEntId(all, IntEntIdType.ISBN, "neco");

        DateTime end = new DateTime();
        // busyWait(2000);
        UrnNbnExportFilter noMmissingFilter = new UrnNbnExportFilter(begin, end, toRegistrarList(registrar1), toEntityTypeList(EntityType.MONOGRAPH),
                false, false, false, true, true);
        UrnNbnExportFilter missingCcnbFilter = new UrnNbnExportFilter(begin, end, toRegistrarList(registrar1),
                toEntityTypeList(EntityType.MONOGRAPH), true, false, false, true, true);
        UrnNbnExportFilter missingIssnFilter = new UrnNbnExportFilter(begin, end, toRegistrarList(registrar1),
                toEntityTypeList(EntityType.MONOGRAPH), false, true, false, true, true);
        UrnNbnExportFilter missingIsbnFilter = new UrnNbnExportFilter(begin, end, toRegistrarList(registrar1),
                toEntityTypeList(EntityType.MONOGRAPH), false, false, true, true, true);
        UrnNbnExportFilter missingCcnbAndIssnFilter = new UrnNbnExportFilter(begin, end, toRegistrarList(registrar1),
                toEntityTypeList(EntityType.MONOGRAPH), true, true, false, true, true);
        UrnNbnExportFilter missingCcnbAndIsbnFilter = new UrnNbnExportFilter(begin, end, toRegistrarList(registrar1),
                toEntityTypeList(EntityType.MONOGRAPH), true, false, true, true, true);
        UrnNbnExportFilter missingIssnAndIsbnFilter = new UrnNbnExportFilter(begin, end, toRegistrarList(registrar1),
                toEntityTypeList(EntityType.MONOGRAPH), false, true, true, true, true);
        UrnNbnExportFilter missingAllFilter = new UrnNbnExportFilter(begin, end, toRegistrarList(registrar1), toEntityTypeList(EntityType.MONOGRAPH),
                true, true, true, true, true);

        // missing all
        List<UrnNbnExport> missingAll = urnDao.selectByCriteria(CountryCode.getCode(), missingAllFilter, false);
        assertEquals(1, missingAll.size());
        for (UrnNbnExport export : missingAll) {
            assertEquals(noId.getDocumentCode(), export.getUrnNbn().split("-")[1]);
        }
        // no missing filter
        assertEquals(8, urnDao.selectByCriteria(CountryCode.getCode(), noMmissingFilter, false).size());
        // missing ccnb
        List<UrnNbnExport> missingCcnb = urnDao.selectByCriteria(CountryCode.getCode(), missingCcnbFilter, false);
        assertEquals(4, missingCcnb.size());
        for (UrnNbnExport export : missingCcnb) {
            assertTrue(export.getUrnNbn().equals(noId.toString()) || export.getUrnNbn().equals(onlyIsbn.toString())
                    || export.getUrnNbn().equals(onlyIssn.toString()) || export.getUrnNbn().equals(issnIsbn.toString()));
        }

        // missing issn
        List<UrnNbnExport> missingIssn = urnDao.selectByCriteria(CountryCode.getCode(), missingIssnFilter, false);
        assertEquals(4, missingIssn.size());
        for (UrnNbnExport export : missingAll) {
            assertTrue(export.getUrnNbn().equals(noId.toString()) || export.getUrnNbn().equals(onlyCcnb.toString())
                    || export.getUrnNbn().equals(onlyIsbn.toString()) || export.getUrnNbn().equals(ccnbIsbn.toString()));
        }

        // missing isbn
        List<UrnNbnExport> missingIsbn = urnDao.selectByCriteria(CountryCode.getCode(), missingIsbnFilter, false);
        assertEquals(4, missingIsbn.size());
        for (UrnNbnExport export : missingIsbn) {
            assertTrue(export.getUrnNbn().equals(noId.toString()) || export.getUrnNbn().equals(onlyCcnb.toString())
                    || export.getUrnNbn().equals(onlyIssn.toString()) || export.getUrnNbn().equals(ccnbIssn.toString()));
        }
        // missing ccnb, issn
        List<UrnNbnExport> missingCcnbIssn = urnDao.selectByCriteria(CountryCode.getCode(), missingCcnbAndIssnFilter, false);
        assertEquals(2, missingCcnbIssn.size());
        for (UrnNbnExport export : missingCcnbIssn) {
            assertTrue(export.getUrnNbn().equals(noId.toString()) || export.getUrnNbn().equals(onlyIsbn.toString()));
            // || export.getUrnNbn().equals(onlyIssn.toString()) || export.getUrnNbn().equals(ccnbIssn.toString()));
        }

        // missing ccnb, isbn
        List<UrnNbnExport> missingCcnbIsbn = urnDao.selectByCriteria(CountryCode.getCode(), missingCcnbAndIsbnFilter, false);
        assertEquals(2, missingCcnbIsbn.size());
        for (UrnNbnExport export : missingCcnbIsbn) {
            assertTrue(export.getUrnNbn().equals(noId.toString()) || export.getUrnNbn().equals(onlyIssn.toString()));
        }

        // missing ccnb, issn
        List<UrnNbnExport> missingIssnIsbn = urnDao.selectByCriteria(CountryCode.getCode(), missingIssnAndIsbnFilter, false);
        assertEquals(2, missingIssnIsbn.size());
        for (UrnNbnExport export : missingIssnIsbn) {
            assertTrue(export.getUrnNbn().equals(noId.toString()) || export.getUrnNbn().equals(onlyCcnb.toString()));
        }
    }

    public void testSelectByCriteria_activeDeactivated() throws Exception {
        Registrar registrar1 = registrarPersisted();
        // start
        DateTime begin = new DateTime();
        UrnNbn active = urnNbnPersisted(EntityType.MONOGRAPH, registrar1, "000002");
        UrnNbn deactivated = urnNbnPersisted(EntityType.PERIODICAL, registrar1, "000003");
        urnDao.deactivateUrnNbn(deactivated.getRegistrarCode(), deactivated.getDocumentCode(), "test");
        DateTime end = new DateTime();
        // after
        List<UrnNbnExport> both = urnDao.selectByCriteria(CountryCode.getCode(), new UrnNbnExportFilter(begin, end, toRegistrarList(registrar1),
                allEntityTypes(), false, false, false, true, true), false);
        assertEquals(2, both.size());

        List<UrnNbnExport> activeOnly = urnDao.selectByCriteria(CountryCode.getCode(), new UrnNbnExportFilter(begin, end,
                toRegistrarList(registrar1), allEntityTypes(), false, false, false, true, false), false);
        assertEquals(1, activeOnly.size());
        assertEquals(active.toString(), activeOnly.get(0).getUrnNbn());

        List<UrnNbnExport> deactivatedOnly = urnDao.selectByCriteria(CountryCode.getCode(), new UrnNbnExportFilter(begin, end,
                toRegistrarList(registrar1), allEntityTypes(), false, false, false, false, true), false);
        assertEquals(1, deactivatedOnly.size());
        assertEquals(deactivated.toString(), deactivatedOnly.get(0).getUrnNbn());
    }
}
