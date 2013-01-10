/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.RegistrarScopeIdType;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.RegistrarScopeIdentifier;
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
public class RegistrarScopeIdentifierDaoPostgresTest extends AbstractDaoTest {

    public RegistrarScopeIdentifierDaoPostgresTest(String testName) {
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

    public void testInsertDigDocId() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        //insert identifier
        RegistrarScopeIdentifier id = builder.registrarScopeIdentifierWithoutIds();
        id.setType(RegistrarScopeIdType.valueOf("K4_pid"));
        id.setValue("uuid:123");
        id.setDigDocId(doc.getId());
        id.setRegistrarId(registrar.getId());
        registrarScopeIdDao.insertRegistrarScopeId(id);
        //insert another typ of identifiere
        RegistrarScopeIdentifier id2 = builder.registrarScopeIdentifierWithoutIds();
        id2.setType(RegistrarScopeIdType.valueOf("signatura"));
        id2.setValue("nevim,neco");
        id2.setDigDocId(doc.getId());
        id2.setRegistrarId(registrar.getId());
        registrarScopeIdDao.insertRegistrarScopeId(id2);
    }

    public void testInsertDigDocId_emptyValue() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        RegistrarScopeIdentifier id = builder.registrarScopeIdentifierWithoutIds();
        id.setType(RegistrarScopeIdType.valueOf("K4_pid"));
        id.setValue(null);
        id.setDigDocId(doc.getId());
        id.setRegistrarId(registrar.getId());
        try {
            registrarScopeIdDao.insertRegistrarScopeId(id);
            fail();
        } catch (NullPointerException e) {
            //ok
        }

    }

    public void testInsertDigDocId_unknownDigDoc() throws Exception {
        Registrar registrar = registrarPersisted();
        RegistrarScopeIdentifier id = builder.registrarScopeIdentifierWithoutIds();
        id.setDigDocId(ILLEGAL_ID);
        id.setRegistrarId(registrar.getId());
        try {
            registrarScopeIdDao.insertRegistrarScopeId(id);
            fail();
        } catch (RecordNotFoundException e) {
            assertEquals(DigitalDocumentDAO.TABLE_NAME, e.getTableName());
        }
    }

    public void testInsertDigDocId_unknownRegistrar() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        RegistrarScopeIdentifier id = builder.registrarScopeIdentifierWithoutIds();
        id.setDigDocId(doc.getId());
        id.setRegistrarId(ILLEGAL_ID);
        try {
            registrarScopeIdDao.insertRegistrarScopeId(id);
            fail();
        } catch (RecordNotFoundException e) {
            assertEquals(RegistrarDAO.TABLE_NAME, e.getTableName());
        }
    }

    public void testInsertDigDocId_insertTwiceSameIdTypeAndValueForSameDigDoc() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        RegistrarScopeIdentifier identifier = builder.registrarScopeIdentifierWithoutIds();
        identifier.setDigDocId(doc.getId());
        identifier.setRegistrarId(registrar.getId());
        registrarScopeIdDao.insertRegistrarScopeId(identifier);
        try {
            registrarScopeIdDao.insertRegistrarScopeId(identifier);
            fail();
        } catch (AlreadyPresentException e) {
            //ok
        }
    }

    public void testInsertDigDocId_insertTwiceSameIdTypeAndValueForSameRegistrar() throws Exception {
        Registrar registrar = registrarPersisted();
        //first digDoc & id
        IntelectualEntity entity1 = entityPersisted();
        DigitalDocument doc1 = documentPersisted(registrar.getId(), entity1.getId());
        RegistrarScopeIdentifier id1 = builder.registrarScopeIdentifierWithoutIds();
        id1.setDigDocId(doc1.getId());
        id1.setRegistrarId(registrar.getId());
        registrarScopeIdDao.insertRegistrarScopeId(id1);
        //second digDoc & digDocId
        IntelectualEntity entity2 = entityPersisted();
        DigitalDocument doc2 = documentPersisted(registrar.getId(), entity2.getId());
        RegistrarScopeIdentifier id2 = builder.registrarScopeIdentifierWithoutIds();
        id2.setDigDocId(doc2.getId());
        id2.setRegistrarId(registrar.getId());
        id2.setValue(id1.getValue());
        try {
            registrarScopeIdDao.insertRegistrarScopeId(id2);
            fail();
        } catch (AlreadyPresentException e) {
            //ok
        }
    }

    public void testGetIdList() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        //first the id list should be empty
        assertTrue(registrarScopeIdDao.getRegistrarScopeIds(doc.getId()).isEmpty());
        //insert id OAI
        RegistrarScopeIdentifier oaiId = new RegistrarScopeIdentifier();
        oaiId.setType(RegistrarScopeIdType.valueOf("oai"));
        oaiId.setValue("123");
        oaiId.setDigDocId(doc.getId());
        oaiId.setRegistrarId(registrar.getId());
        registrarScopeIdDao.insertRegistrarScopeId(oaiId);
        //insert id K4_pid
        RegistrarScopeIdentifier k4pid = new RegistrarScopeIdentifier();
        k4pid.setType(RegistrarScopeIdType.valueOf("K4_pid"));
        k4pid.setValue("uuid:3456");
        k4pid.setDigDocId(doc.getId());
        k4pid.setRegistrarId(registrar.getId());
        registrarScopeIdDao.insertRegistrarScopeId(k4pid);
        //get ids
        List<RegistrarScopeIdentifier> idList = registrarScopeIdDao.getRegistrarScopeIds(doc.getId());
        assertEquals(2, idList.size());
        assertTrue(idList.contains(oaiId));
        assertTrue(idList.contains(k4pid));
        RegistrarScopeIdentifier fetchedFirst = idList.get(0);
        RegistrarScopeIdentifier fetchedSecond = idList.get(1);
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
        RegistrarScopeIdentifier second = registrarScopeIdPersisted(registrarId, digDocId, "second");
        //before - firstEnt - between - secondEnt - after
        DateTime before = new DateTime();
        Thread.sleep(1000);
        //first
        RegistrarScopeIdentifier first = registrarScopeIdPersisted(registrarId, digDocId, "first");
        Thread.sleep(1000);
        DateTime between = new DateTime();
        Thread.sleep(1000);
        //second
        second.setValue("newValue");
        registrarScopeIdDao.updateRegistrarScopeIdValue(second);
        second = registrarScopeIdDao.getRegistrarScopeId(digDocId, second.getType());
        Thread.sleep(1000);
        DateTime after = new DateTime();
        //before-before
        List<RegistrarScopeIdentifier> idList = registrarScopeIdDao.getRegistrarScopeIdsByTimestamps(before, before);
        assertTrue(idList.isEmpty());
        //before-first
        idList = registrarScopeIdDao.getRegistrarScopeIdsByTimestamps(before, first.getModified());
        assertEquals(1, idList.size());
        assertTrue(idList.contains(first));
        //before-between
        idList = registrarScopeIdDao.getRegistrarScopeIdsByTimestamps(before, between);
        assertEquals(1, idList.size());
        assertTrue(idList.contains(first));
        //before-second
        idList = registrarScopeIdDao.getRegistrarScopeIdsByTimestamps(before, second.getModified());
        assertEquals(2, idList.size());
        assertTrue(idList.contains(first));
        assertTrue(idList.contains(second));
        //before-after
        idList = registrarScopeIdDao.getRegistrarScopeIdsByTimestamps(before, after);
        assertEquals(2, idList.size());
        assertTrue(idList.contains(first));
        assertTrue(idList.contains(second));
        //between-between
        idList = registrarScopeIdDao.getRegistrarScopeIdsByTimestamps(between, between);
        assertTrue(idList.isEmpty());
        //between-second
        idList = registrarScopeIdDao.getRegistrarScopeIdsByTimestamps(between, second.getModified());
        assertEquals(1, idList.size());
        assertTrue(idList.contains(second));
        //betwen-after
        idList = registrarScopeIdDao.getRegistrarScopeIdsByTimestamps(between, after);
        assertEquals(1, idList.size());
        assertTrue(idList.contains(second));
        //after-after
        idList = registrarScopeIdDao.getRegistrarScopeIdsByTimestamps(after, after);
        assertTrue(idList.isEmpty());
    }

    public void testGetIdListByTimestamps_from_only() throws Exception {
        Long registrarId = registrarPersisted().getId();
        Long digDocId = documentPersisted(registrarId, entityPersisted().getId()).getId();
        RegistrarScopeIdentifier second = registrarScopeIdPersisted(registrarId, digDocId, "second");
        //before - firstEnt - between - secondEnt - after
        DateTime before = new DateTime();
        Thread.sleep(1000);
        //first
        RegistrarScopeIdentifier first = registrarScopeIdPersisted(registrarId, digDocId, "first");
        Thread.sleep(1000);
        DateTime between = new DateTime();
        Thread.sleep(1000);
        //second
        second.setValue("newValue");
        registrarScopeIdDao.updateRegistrarScopeIdValue(second);
        second = registrarScopeIdDao.getRegistrarScopeId(digDocId, second.getType());
        Thread.sleep(1000);
        DateTime after = new DateTime();
        //before
        List<RegistrarScopeIdentifier> idList = registrarScopeIdDao.getRegistrarScopeIdsByTimestamps(before, null);
        assertEquals(2, idList.size());
        assertTrue(idList.contains(first));
        assertTrue(idList.contains(second));
        //betwen
        idList = registrarScopeIdDao.getRegistrarScopeIdsByTimestamps(between, null);
        assertEquals(1, idList.size());
        assertTrue(idList.contains(second));
        //after
        idList = registrarScopeIdDao.getRegistrarScopeIdsByTimestamps(after, null);
        assertTrue(idList.isEmpty());
    }

    public void testGetIdListByTimestamps_until_only() throws Exception {
        Long registrarId = registrarPersisted().getId();
        Long digDocId = documentPersisted(registrarId, entityPersisted().getId()).getId();
        RegistrarScopeIdentifier second = registrarScopeIdPersisted(registrarId, digDocId, "second");
        //before - firstEnt - between - secondEnt - after
        DateTime before = new DateTime();
        Thread.sleep(1000);
        //first
        RegistrarScopeIdentifier first = registrarScopeIdPersisted(registrarId, digDocId, "first");
        Thread.sleep(1000);
        DateTime between = new DateTime();
        Thread.sleep(1000);
        //second
        second.setValue("newValue");
        registrarScopeIdDao.updateRegistrarScopeIdValue(second);
        second = registrarScopeIdDao.getRegistrarScopeId(digDocId, second.getType());
        Thread.sleep(1000);
        DateTime after = new DateTime();
        //before
        List<RegistrarScopeIdentifier> idList = registrarScopeIdDao.getRegistrarScopeIdsByTimestamps(null, before);
        assertTrue(idList.isEmpty());
        //first
        idList = registrarScopeIdDao.getRegistrarScopeIdsByTimestamps(null, first.getModified());
        assertEquals(1, idList.size());
        assertTrue(idList.contains(first));
        //between
        idList = registrarScopeIdDao.getRegistrarScopeIdsByTimestamps(null, between);
        assertEquals(1, idList.size());
        assertTrue(idList.contains(first));
        //second
        idList = registrarScopeIdDao.getRegistrarScopeIdsByTimestamps(null, second.getModified());
        assertEquals(2, idList.size());
        assertTrue(idList.contains(first));
        assertTrue(idList.contains(second));
        //after
        idList = registrarScopeIdDao.getRegistrarScopeIdsByTimestamps(null, after);
        assertEquals(2, idList.size());
        assertTrue(idList.contains(first));
        assertTrue(idList.contains(second));
    }

    public void testUpdateDigDocIdValue() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        //insert identifier
        RegistrarScopeIdentifier inserted = builder.registrarScopeIdentifierWithoutIds();
        inserted.setType(RegistrarScopeIdType.valueOf("my_Id"));
        inserted.setValue("oldValue");
        inserted.setDigDocId(doc.getId());
        inserted.setRegistrarId(registrar.getId());
        registrarScopeIdDao.insertRegistrarScopeId(inserted);
        //update
        RegistrarScopeIdentifier updated = new RegistrarScopeIdentifier(inserted);
        updated.setValue("newValue");
        registrarScopeIdDao.updateRegistrarScopeIdValue(updated);
        //fetch
        RegistrarScopeIdentifier fetched = registrarScopeIdDao.getRegistrarScopeIds(doc.getId()).get(0);
        assertEquals(updated.getValue(), fetched.getValue());
        assertFalse(inserted.getValue().equals(fetched.getValue()));
    }

    public void testUpdateDigDocIdValue_valueCollision() throws Exception {
        Registrar registrar = registrarPersisted();
        RegistrarScopeIdType idType = RegistrarScopeIdType.valueOf("some_id_type");
        String collidingValue = "collision";
        //first digDoc
        IntelectualEntity entity1 = entityPersisted();
        DigitalDocument doc1 = documentPersisted(registrar.getId(), entity1.getId());
        //identifier of first digDoc
        RegistrarScopeIdentifier digDoc1Id = builder.registrarScopeIdentifierWithoutIds();
        digDoc1Id.setType(idType);
        digDoc1Id.setValue(collidingValue);
        digDoc1Id.setDigDocId(doc1.getId());
        digDoc1Id.setRegistrarId(registrar.getId());
        registrarScopeIdDao.insertRegistrarScopeId(digDoc1Id);

        //second digDoc
        IntelectualEntity entity2 = entityPersisted();
        DigitalDocument doc2 = documentPersisted(registrar.getId(), entity2.getId());
        //insert identifier to digDoc2
        RegistrarScopeIdentifier digDoc2Id = builder.registrarScopeIdentifierWithoutIds();
        digDoc2Id.setType(idType);
        digDoc2Id.setValue("okValue");
        digDoc2Id.setDigDocId(doc2.getId());
        digDoc2Id.setRegistrarId(registrar.getId());
        registrarScopeIdDao.insertRegistrarScopeId(digDoc2Id);
        //update
        RegistrarScopeIdentifier updated = new RegistrarScopeIdentifier(digDoc2Id);
        updated.setValue(collidingValue);
        try {
            registrarScopeIdDao.updateRegistrarScopeIdValue(updated);
            fail();
        } catch (AlreadyPresentException e) {
            //ok
        }
    }

    public void testUpdateDigDocIdValue_unknownRegistrarOrDigDoc() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        //insert identifier
        RegistrarScopeIdentifier inserted = builder.registrarScopeIdentifierWithoutIds();
        inserted.setDigDocId(doc.getId());
        inserted.setRegistrarId(registrar.getId());
        registrarScopeIdDao.insertRegistrarScopeId(inserted);
        //set incorrect registrar id and update
        RegistrarScopeIdentifier updated = new RegistrarScopeIdentifier(inserted);
        updated.setRegistrarId(ILLEGAL_ID);
        try {
            registrarScopeIdDao.updateRegistrarScopeIdValue(updated);
            fail();
        } catch (RecordNotFoundException e) {
            assertEquals(RegistrarDAO.TABLE_NAME, e.getTableName());
        }
        //set incorrect digDocId and update
        updated.setRegistrarId(registrar.getId());
        updated.setDigDocId(ILLEGAL_ID);
        try {
            registrarScopeIdDao.updateRegistrarScopeIdValue(updated);
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
        RegistrarScopeIdentifier inserted = builder.registrarScopeIdentifierWithoutIds();
        inserted.setDigDocId(doc.getId());
        inserted.setRegistrarId(registrar.getId());
        registrarScopeIdDao.insertRegistrarScopeId(inserted);
        assertTrue(registrarScopeIdDao.getRegistrarScopeIds(doc.getId()).contains(inserted));
        //delete
        registrarScopeIdDao.deleteRegistrarScopeId(doc.getId(), inserted.getType());
        assertFalse(registrarScopeIdDao.getRegistrarScopeIds(doc.getId()).contains(inserted));
    }

    public void testDeleteDigDocIdentifier_unknownDigDoc() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        //insert identifier
        RegistrarScopeIdentifier inserted = builder.registrarScopeIdentifierWithoutIds();
        inserted.setDigDocId(doc.getId());
        inserted.setRegistrarId(registrar.getId());
        registrarScopeIdDao.insertRegistrarScopeId(inserted);
        assertTrue(registrarScopeIdDao.getRegistrarScopeIds(doc.getId()).contains(inserted));
        //delete
        try {
            registrarScopeIdDao.deleteRegistrarScopeId(ILLEGAL_ID, inserted.getType());
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
        RegistrarScopeIdentifier inserted = builder.registrarScopeIdentifierWithoutIds();
        inserted.setDigDocId(doc.getId());
        inserted.setRegistrarId(registrar.getId());
        registrarScopeIdDao.insertRegistrarScopeId(inserted);
        assertTrue(registrarScopeIdDao.getRegistrarScopeIds(doc.getId()).contains(inserted));
        //delete
        try {
            RegistrarScopeIdType otherType = RegistrarScopeIdType.valueOf("otherType");
            registrarScopeIdDao.deleteRegistrarScopeId(doc.getId(), otherType);
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
        assertTrue(registrarScopeIdDao.getRegistrarScopeIds(doc.getId()).isEmpty());
        //insert id OAI
        RegistrarScopeIdentifier idOai = new RegistrarScopeIdentifier();
        idOai.setType(RegistrarScopeIdType.valueOf("oai"));
        idOai.setValue("123");
        idOai.setDigDocId(doc.getId());
        idOai.setRegistrarId(registrar.getId());
        registrarScopeIdDao.insertRegistrarScopeId(idOai);
        //insert id OTHER
        RegistrarScopeIdentifier idOther = new RegistrarScopeIdentifier();
        idOther.setType(RegistrarScopeIdType.valueOf("K4_pid"));
        idOther.setValue("uuid:3456");
        idOther.setDigDocId(doc.getId());
        idOther.setRegistrarId(registrar.getId());
        registrarScopeIdDao.insertRegistrarScopeId(idOther);
        //get ids
        assertEquals(2, registrarScopeIdDao.getRegistrarScopeIds(doc.getId()).size());
        registrarScopeIdDao.deleteRegistrarScopeIds(doc.getId());
        assertTrue(registrarScopeIdDao.getRegistrarScopeIds(doc.getId()).isEmpty());
    }

    public void testDeleteAllDigDocIds_noIds() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        registrarScopeIdDao.deleteRegistrarScopeIds(doc.getId());
    }

    public void testDeleteAllDigDocIdsOfEntity_unknownDigDoc() throws Exception {
        try {
            registrarScopeIdDao.deleteRegistrarScopeIds(ILLEGAL_ID);
            fail();
        } catch (RecordNotFoundException e) {
            assertEquals(DigitalDocumentDAO.TABLE_NAME, e.getTableName());
        }
    }
}
