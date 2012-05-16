/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.DigDocIdType;
import cz.nkp.urnnbn.core.dto.DigDocIdentifier;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.persistence.DigitalDocumentDAO;
import cz.nkp.urnnbn.core.persistence.RegistrarDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import java.util.List;
import org.joda.time.DateTime;

/**
 *
 * @author Martin Řehánek
 */
public class DigDocIdentifierDaoPostgresTest extends AbstractDaoTest {

    public DigDocIdentifierDaoPostgresTest(String testName) {
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

    /**
     * Test of insertDigDocId method, of class DigRepIdentifierDaoPostgres.
     */
    public void testInsertDigDocId() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        //insert identifier
        DigDocIdentifier id = builder.digDocIdentifierWithoutIds();
        id.setType(DigDocIdType.valueOf("K4_pid"));
        id.setValue("uuid:123");
        id.setDigDocId(doc.getId());
        id.setRegistrarId(registrar.getId());
        digDocIdDao.insertDigDocId(id);
        //insert another typ of identifiere
        DigDocIdentifier id2 = builder.digDocIdentifierWithoutIds();
        id2.setType(DigDocIdType.valueOf("signatura"));
        id2.setValue("nevim,neco");
        id2.setDigDocId(doc.getId());
        id2.setRegistrarId(registrar.getId());
        digDocIdDao.insertDigDocId(id2);
    }

    public void testInsertDigDocId_emptyValue() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        DigDocIdentifier id = builder.digDocIdentifierWithoutIds();
        id.setType(DigDocIdType.valueOf("K4_pid"));
        id.setValue(null);
        id.setDigDocId(doc.getId());
        id.setRegistrarId(registrar.getId());
        try {
            digDocIdDao.insertDigDocId(id);
            fail();
        } catch (NullPointerException e) {
            //ok
        }

    }

    public void testInsertDigDocId_unknownDigDoc() throws Exception {
        Registrar registrar = registrarPersisted();
        DigDocIdentifier id = builder.digDocIdentifierWithoutIds();
        id.setDigDocId(ILLEGAL_ID);
        id.setRegistrarId(registrar.getId());
        try {
            digDocIdDao.insertDigDocId(id);
            fail();
        } catch (RecordNotFoundException e) {
            assertEquals(DigitalDocumentDAO.TABLE_NAME, e.getTableName());
        }
    }

    public void testInsertDigDocId_unknownRegistrar() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        DigDocIdentifier id = builder.digDocIdentifierWithoutIds();
        id.setDigDocId(doc.getId());
        id.setRegistrarId(ILLEGAL_ID);
        try {
            digDocIdDao.insertDigDocId(id);
            fail();
        } catch (RecordNotFoundException e) {
            assertEquals(RegistrarDAO.TABLE_NAME, e.getTableName());
        }
    }

    public void testInsertDigDocId_insertTwiceSameIdTypeAndValueForSameDigDoc() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        DigDocIdentifier identifier = builder.digDocIdentifierWithoutIds();
        identifier.setDigDocId(doc.getId());
        identifier.setRegistrarId(registrar.getId());
        digDocIdDao.insertDigDocId(identifier);
        try {
            digDocIdDao.insertDigDocId(identifier);
            fail();
        } catch (AlreadyPresentException e) {
            //ok
        }
    }

    public void testInsertDigDocId_insertTwiceSameIdTypeAndValueForSameRegistrar() throws Exception {
        Registrar registrar = registrarPersisted();
        //first digRep & id
        IntelectualEntity entity1 = entityPersisted();
        DigitalDocument doc1 = documentPersisted(registrar.getId(), entity1.getId());
        DigDocIdentifier id1 = builder.digDocIdentifierWithoutIds();
        id1.setDigDocId(doc1.getId());
        id1.setRegistrarId(registrar.getId());
        digDocIdDao.insertDigDocId(id1);
        //second digDoc & digDocId
        IntelectualEntity entity2 = entityPersisted();
        DigitalDocument doc2 = documentPersisted(registrar.getId(), entity2.getId());
        DigDocIdentifier id2 = builder.digDocIdentifierWithoutIds();
        id2.setDigDocId(doc2.getId());
        id2.setRegistrarId(registrar.getId());
        id2.setValue(id1.getValue());
        try {
            digDocIdDao.insertDigDocId(id2);
            fail();
        } catch (AlreadyPresentException e) {
            //ok
        }
    }

    /**
     * Test of getIdList method, of class DigRepIdentifierDaoPostgres.
     */
    public void testGetIdList() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        //first the id list should be empty
        assertTrue(digDocIdDao.getIdList(doc.getId()).isEmpty());
        //insert id OAI
        DigDocIdentifier oaiId = new DigDocIdentifier();
        oaiId.setType(DigDocIdType.valueOf("oai"));
        oaiId.setValue("123");
        oaiId.setDigDocId(doc.getId());
        oaiId.setRegistrarId(registrar.getId());
        digDocIdDao.insertDigDocId(oaiId);
        //insert id K4_pid
        DigDocIdentifier k4pid = new DigDocIdentifier();
        k4pid.setType(DigDocIdType.valueOf("K4_pid"));
        k4pid.setValue("uuid:3456");
        k4pid.setDigDocId(doc.getId());
        k4pid.setRegistrarId(registrar.getId());
        digDocIdDao.insertDigDocId(k4pid);
        //get ids
        List<DigDocIdentifier> idList = digDocIdDao.getIdList(doc.getId());
        assertEquals(2, idList.size());
        assertTrue(idList.contains(oaiId));
        assertTrue(idList.contains(k4pid));
        DigDocIdentifier fetchedFirst = idList.get(0);
        DigDocIdentifier fetchedSecond = idList.get(1);
        assertNotNull(fetchedFirst);
        assertNotNull(fetchedFirst.getCreated());
        assertNotNull(fetchedFirst.getDigDocId());
        assertNotNull(fetchedFirst.getRegistrarId());
        assertNotNull(fetchedFirst.getType());
        assertNotNull(fetchedFirst.getValue());
        assertNotNull(fetchedSecond);
        assertNotNull(fetchedSecond.getCreated());
        assertNotNull(fetchedSecond.getDigDocId());
        assertNotNull(fetchedSecond.getRegistrarId());
        assertNotNull(fetchedSecond.getType());
        assertNotNull(fetchedSecond.getValue());
    }

    public void testGetIdListByTimestamps_from_until() throws Exception {
        Long registrarId = registrarPersisted().getId();
        Long digDocId = documentPersisted(registrarId, entityPersisted().getId()).getId();
        DigDocIdentifier second = digDocIdentifierPersisted(registrarId, digDocId, "second");
        //before - firstEnt - between - secondEnt - after
        DateTime before = new DateTime();
        Thread.sleep(1000);
        //first
        DigDocIdentifier first = digDocIdentifierPersisted(registrarId, digDocId, "first");
        Thread.sleep(1000);
        DateTime between = new DateTime();
        Thread.sleep(1000);
        //second
        second.setValue("newValue");
        digDocIdDao.updateDigDocIdValue(second);
        second = digDocIdDao.getIdentifer(digDocId, second.getType());
        Thread.sleep(1000);
        DateTime after = new DateTime();
        //before-before
        List<DigDocIdentifier> idList = digDocIdDao.getIdListByTimestamps(before, before);
        assertTrue(idList.isEmpty());
        //before-first
        idList = digDocIdDao.getIdListByTimestamps(before, first.getModified());
        assertEquals(1, idList.size());
        assertTrue(idList.contains(first));
        //before-between
        idList = digDocIdDao.getIdListByTimestamps(before, between);
        assertEquals(1, idList.size());
        assertTrue(idList.contains(first));
        //before-second
        idList = digDocIdDao.getIdListByTimestamps(before, second.getModified());
        assertEquals(2, idList.size());
        assertTrue(idList.contains(first));
        assertTrue(idList.contains(second));
        //before-after
        idList = digDocIdDao.getIdListByTimestamps(before, after);
        assertEquals(2, idList.size());
        assertTrue(idList.contains(first));
        assertTrue(idList.contains(second));
        //between-between
        idList = digDocIdDao.getIdListByTimestamps(between, between);
        assertTrue(idList.isEmpty());
        //between-second
        idList = digDocIdDao.getIdListByTimestamps(between, second.getModified());
        assertEquals(1, idList.size());
        assertTrue(idList.contains(second));
        //betwen-after
        idList = digDocIdDao.getIdListByTimestamps(between, after);
        assertEquals(1, idList.size());
        assertTrue(idList.contains(second));
        //after-after
        idList = digDocIdDao.getIdListByTimestamps(after, after);
        assertTrue(idList.isEmpty());
    }

    public void testGetIdListByTimestamps_from_only() throws Exception {
        Long registrarId = registrarPersisted().getId();
        Long digDocId = documentPersisted(registrarId, entityPersisted().getId()).getId();
        DigDocIdentifier second = digDocIdentifierPersisted(registrarId, digDocId, "second");
        //before - firstEnt - between - secondEnt - after
        DateTime before = new DateTime();
        Thread.sleep(1000);
        //first
        DigDocIdentifier first = digDocIdentifierPersisted(registrarId, digDocId, "first");
        Thread.sleep(1000);
        DateTime between = new DateTime();
        Thread.sleep(1000);
        //second
        second.setValue("newValue");
        digDocIdDao.updateDigDocIdValue(second);
        second = digDocIdDao.getIdentifer(digDocId, second.getType());
        Thread.sleep(1000);
        DateTime after = new DateTime();
        //before
        List<DigDocIdentifier> idList = digDocIdDao.getIdListByTimestamps(before, null);
        assertEquals(2, idList.size());
        assertTrue(idList.contains(first));
        assertTrue(idList.contains(second));
        //betwen
        idList = digDocIdDao.getIdListByTimestamps(between, null);
        assertEquals(1, idList.size());
        assertTrue(idList.contains(second));
        //after
        idList = digDocIdDao.getIdListByTimestamps(after, null);
        assertTrue(idList.isEmpty());
    }

    public void testGetIdListByTimestamps_until_only() throws Exception {
        Long registrarId = registrarPersisted().getId();
        Long digDocId = documentPersisted(registrarId, entityPersisted().getId()).getId();
        DigDocIdentifier second = digDocIdentifierPersisted(registrarId, digDocId, "second");
        //before - firstEnt - between - secondEnt - after
        DateTime before = new DateTime();
        Thread.sleep(1000);
        //first
        DigDocIdentifier first = digDocIdentifierPersisted(registrarId, digDocId, "first");
        Thread.sleep(1000);
        DateTime between = new DateTime();
        Thread.sleep(1000);
        //second
        second.setValue("newValue");
        digDocIdDao.updateDigDocIdValue(second);
        second = digDocIdDao.getIdentifer(digDocId, second.getType());
        Thread.sleep(1000);
        DateTime after = new DateTime();
        //before
        List<DigDocIdentifier> idList = digDocIdDao.getIdListByTimestamps(null, before);
        assertTrue(idList.isEmpty());
        //first
        idList = digDocIdDao.getIdListByTimestamps(null, first.getModified());
        assertEquals(1, idList.size());
        assertTrue(idList.contains(first));
        //between
        idList = digDocIdDao.getIdListByTimestamps(null, between);
        assertEquals(1, idList.size());
        assertTrue(idList.contains(first));
        //second
        idList = digDocIdDao.getIdListByTimestamps(null, second.getModified());
        assertEquals(2, idList.size());
        assertTrue(idList.contains(first));
        assertTrue(idList.contains(second));
        //after
        idList = digDocIdDao.getIdListByTimestamps(null, after);
        assertEquals(2, idList.size());
        assertTrue(idList.contains(first));
        assertTrue(idList.contains(second));
    }

    public void testUpdateDigDocIdValue() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        //insert identifier
        DigDocIdentifier inserted = builder.digDocIdentifierWithoutIds();
        inserted.setType(DigDocIdType.valueOf("my_Id"));
        inserted.setValue("oldValue");
        inserted.setDigDocId(doc.getId());
        inserted.setRegistrarId(registrar.getId());
        digDocIdDao.insertDigDocId(inserted);
        //update
        DigDocIdentifier updated = new DigDocIdentifier(inserted);
        updated.setValue("newValue");
        digDocIdDao.updateDigDocIdValue(updated);
        //fetch
        DigDocIdentifier fetched = digDocIdDao.getIdList(doc.getId()).get(0);
        assertEquals(updated.getValue(), fetched.getValue());
        assertFalse(inserted.getValue().equals(fetched.getValue()));
    }

    public void testUpdateDigDocIdValue_valueCollision() throws Exception {
        Registrar registrar = registrarPersisted();
        DigDocIdType idType = DigDocIdType.valueOf("some_id_type");
        String collidingValue = "collision";
        //first digRep
        IntelectualEntity entity1 = entityPersisted();
        DigitalDocument doc1 = documentPersisted(registrar.getId(), entity1.getId());
        //identifier of first digRep
        DigDocIdentifier digRep1Id = builder.digDocIdentifierWithoutIds();
        digRep1Id.setType(idType);
        digRep1Id.setValue(collidingValue);
        digRep1Id.setDigDocId(doc1.getId());
        digRep1Id.setRegistrarId(registrar.getId());
        digDocIdDao.insertDigDocId(digRep1Id);

        //second digRep
        IntelectualEntity entity2 = entityPersisted();
        DigitalDocument doc2 = documentPersisted(registrar.getId(), entity2.getId());
        //insert identifier to digRep2
        DigDocIdentifier digRep2Id = builder.digDocIdentifierWithoutIds();
        digRep2Id.setType(idType);
        digRep2Id.setValue("okValue");
        digRep2Id.setDigDocId(doc2.getId());
        digRep2Id.setRegistrarId(registrar.getId());
        digDocIdDao.insertDigDocId(digRep2Id);
        //update
        DigDocIdentifier updated = new DigDocIdentifier(digRep2Id);
        updated.setValue(collidingValue);
        try {
            digDocIdDao.updateDigDocIdValue(updated);
            fail();
        } catch (AlreadyPresentException e) {
            //ok
        }
    }

    public void testUpdateDigDocIdValue_unknownRegistrarOrDigRep() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        //insert identifier
        DigDocIdentifier inserted = builder.digDocIdentifierWithoutIds();
        inserted.setDigDocId(doc.getId());
        inserted.setRegistrarId(registrar.getId());
        digDocIdDao.insertDigDocId(inserted);
        //set incorrect registrar id and update
        DigDocIdentifier updated = new DigDocIdentifier(inserted);
        updated.setRegistrarId(ILLEGAL_ID);
        try {
            digDocIdDao.updateDigDocIdValue(updated);
            fail();
        } catch (RecordNotFoundException e) {
            assertEquals(RegistrarDAO.TABLE_NAME, e.getTableName());
        }
        //set incorrect digRepId and update
        updated.setRegistrarId(registrar.getId());
        updated.setDigDocId(ILLEGAL_ID);
        try {
            digDocIdDao.updateDigDocIdValue(updated);
            fail();
        } catch (RecordNotFoundException e) {
            assertEquals(DigitalDocumentDAO.TABLE_NAME, e.getTableName());
        }
    }

    public void testDeleteDigDocIdentifier() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        //insert identifier
        DigDocIdentifier inserted = builder.digDocIdentifierWithoutIds();
        inserted.setDigDocId(doc.getId());
        inserted.setRegistrarId(registrar.getId());
        digDocIdDao.insertDigDocId(inserted);
        assertTrue(digDocIdDao.getIdList(doc.getId()).contains(inserted));
        //delete
        digDocIdDao.deleteDigDocIdentifier(doc.getId(), inserted.getType());
        assertFalse(digDocIdDao.getIdList(doc.getId()).contains(inserted));
    }

    public void testDeleteDigDocIdentifier_unknownDigDoc() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        //insert identifier
        DigDocIdentifier inserted = builder.digDocIdentifierWithoutIds();
        inserted.setDigDocId(doc.getId());
        inserted.setRegistrarId(registrar.getId());
        digDocIdDao.insertDigDocId(inserted);
        assertTrue(digDocIdDao.getIdList(doc.getId()).contains(inserted));
        //delete
        try {
            digDocIdDao.deleteDigDocIdentifier(ILLEGAL_ID, inserted.getType());
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }

    public void testDeleteDigDocIdentifier_deletedRecordDoesntExist() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        //insert identifier
        DigDocIdentifier inserted = builder.digDocIdentifierWithoutIds();
        inserted.setDigDocId(doc.getId());
        inserted.setRegistrarId(registrar.getId());
        digDocIdDao.insertDigDocId(inserted);
        assertTrue(digDocIdDao.getIdList(doc.getId()).contains(inserted));
        //delete
        try {
            DigDocIdType otherType = DigDocIdType.valueOf("otherType");
            digDocIdDao.deleteDigDocIdentifier(doc.getId(), otherType);
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }

    public void testDeleteAllDigDocIdsOfEntity() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        //first the id list should be empty
        assertTrue(digDocIdDao.getIdList(doc.getId()).isEmpty());
        //insert id OAI
        DigDocIdentifier idOai = new DigDocIdentifier();
        idOai.setType(DigDocIdType.valueOf("oai"));
        idOai.setValue("123");
        idOai.setDigDocId(doc.getId());
        idOai.setRegistrarId(registrar.getId());
        digDocIdDao.insertDigDocId(idOai);
        //insert id OTHER
        DigDocIdentifier idOther = new DigDocIdentifier();
        idOther.setType(DigDocIdType.valueOf("K4_pid"));
        idOther.setValue("uuid:3456");
        idOther.setDigDocId(doc.getId());
        idOther.setRegistrarId(registrar.getId());
        digDocIdDao.insertDigDocId(idOther);
        //get ids
        assertEquals(2, digDocIdDao.getIdList(doc.getId()).size());
        digDocIdDao.deleteAllIdentifiersOfDigDoc(doc.getId());
        assertTrue(digDocIdDao.getIdList(doc.getId()).isEmpty());
    }

    public void testDeleteAllDigDocIds_noIds() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        digDocIdDao.deleteAllIdentifiersOfDigDoc(doc.getId());
    }

    public void testDeleteAllDigRepIdsOfEntity_unknownDigRep() throws Exception {
        try {
            digDocIdDao.deleteAllIdentifiersOfDigDoc(ILLEGAL_ID);
            fail();
        } catch (RecordNotFoundException e) {
            assertEquals(DigitalDocumentDAO.TABLE_NAME, e.getTableName());
        }
    }
}
