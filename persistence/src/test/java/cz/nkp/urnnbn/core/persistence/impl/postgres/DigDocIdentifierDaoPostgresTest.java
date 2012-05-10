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
        digRepIdDao.insertDigDocId(id);
        //insert another typ of identifiere
        DigDocIdentifier id2 = builder.digDocIdentifierWithoutIds();
        id2.setType(DigDocIdType.valueOf("signatura"));
        id2.setValue("nevim,neco");
        id2.setDigDocId(doc.getId());
        id2.setRegistrarId(registrar.getId());
        digRepIdDao.insertDigDocId(id2);
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
            digRepIdDao.insertDigDocId(id);
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
            digRepIdDao.insertDigDocId(id);
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
            digRepIdDao.insertDigDocId(id);
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
        digRepIdDao.insertDigDocId(identifier);
        try {
            digRepIdDao.insertDigDocId(identifier);
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
        digRepIdDao.insertDigDocId(id1);
        //second digRep & id
        IntelectualEntity entity2 = entityPersisted();
        DigitalDocument doc2 = documentPersisted(registrar.getId(), entity2.getId());
        DigDocIdentifier id2 = builder.digDocIdentifierWithoutIds();
        id2.setDigDocId(doc2.getId());
        id2.setRegistrarId(registrar.getId());
        try {
            digRepIdDao.insertDigDocId(id2);
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
        assertTrue(digRepIdDao.getIdList(doc.getId()).isEmpty());
        //insert id OAI
        DigDocIdentifier oaiId = new DigDocIdentifier();
        oaiId.setType(DigDocIdType.valueOf("oai"));
        oaiId.setValue("123");
        oaiId.setDigDocId(doc.getId());
        oaiId.setRegistrarId(registrar.getId());
        digRepIdDao.insertDigDocId(oaiId);
        //insert id K4_pid
        DigDocIdentifier k4pid = new DigDocIdentifier();
        k4pid.setType(DigDocIdType.valueOf("K4_pid"));
        k4pid.setValue("uuid:3456");
        k4pid.setDigDocId(doc.getId());
        k4pid.setRegistrarId(registrar.getId());
        digRepIdDao.insertDigDocId(k4pid);
        //get ids
        List<DigDocIdentifier> idList = digRepIdDao.getIdList(doc.getId());
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
        digRepIdDao.insertDigDocId(inserted);
        //update
        DigDocIdentifier updated = new DigDocIdentifier(inserted);
        updated.setValue("newValue");
        digRepIdDao.updateDigDocIdValue(updated);
        //fetch
        DigDocIdentifier fetched = digRepIdDao.getIdList(doc.getId()).get(0);
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
        digRepIdDao.insertDigDocId(digRep1Id);

        //second digRep
        IntelectualEntity entity2 = entityPersisted();
        DigitalDocument doc2 = documentPersisted(registrar.getId(), entity2.getId());
        //insert identifier to digRep2
        DigDocIdentifier digRep2Id = builder.digDocIdentifierWithoutIds();
        digRep2Id.setType(idType);
        digRep2Id.setValue("okValue");
        digRep2Id.setDigDocId(doc2.getId());
        digRep2Id.setRegistrarId(registrar.getId());
        digRepIdDao.insertDigDocId(digRep2Id);
        //update
        DigDocIdentifier updated = new DigDocIdentifier(digRep2Id);
        updated.setValue(collidingValue);
        try {
            digRepIdDao.updateDigDocIdValue(updated);
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
        digRepIdDao.insertDigDocId(inserted);
        //set incorrect registrar id and update
        DigDocIdentifier updated = new DigDocIdentifier(inserted);
        updated.setRegistrarId(ILLEGAL_ID);
        try {
            digRepIdDao.updateDigDocIdValue(updated);
            fail();
        } catch (RecordNotFoundException e) {
            assertEquals(RegistrarDAO.TABLE_NAME, e.getTableName());
        }
        //set incorrect digRepId and update
        updated.setRegistrarId(registrar.getId());
        updated.setDigDocId(ILLEGAL_ID);
        try {
            digRepIdDao.updateDigDocIdValue(updated);
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
        digRepIdDao.insertDigDocId(inserted);
        assertTrue(digRepIdDao.getIdList(doc.getId()).contains(inserted));
        //delete
        digRepIdDao.deleteDigDocIdentifier(doc.getId(), inserted.getType());
        assertFalse(digRepIdDao.getIdList(doc.getId()).contains(inserted));
    }

    public void testDeleteDigDocIdentifier_unknownDigDoc() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        //insert identifier
        DigDocIdentifier inserted = builder.digDocIdentifierWithoutIds();
        inserted.setDigDocId(doc.getId());
        inserted.setRegistrarId(registrar.getId());
        digRepIdDao.insertDigDocId(inserted);
        assertTrue(digRepIdDao.getIdList(doc.getId()).contains(inserted));
        //delete
        try {
            digRepIdDao.deleteDigDocIdentifier(ILLEGAL_ID, inserted.getType());
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
        digRepIdDao.insertDigDocId(inserted);
        assertTrue(digRepIdDao.getIdList(doc.getId()).contains(inserted));
        //delete
        try {
            DigDocIdType otherType = DigDocIdType.valueOf("otherType");
            digRepIdDao.deleteDigDocIdentifier(doc.getId(), otherType);
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
        assertTrue(digRepIdDao.getIdList(doc.getId()).isEmpty());
        //insert id OAI
        DigDocIdentifier idOai = new DigDocIdentifier();
        idOai.setType(DigDocIdType.valueOf("oai"));
        idOai.setValue("123");
        idOai.setDigDocId(doc.getId());
        idOai.setRegistrarId(registrar.getId());
        digRepIdDao.insertDigDocId(idOai);
        //insert id OTHER
        DigDocIdentifier idOther = new DigDocIdentifier();
        idOther.setType(DigDocIdType.valueOf("K4_pid"));
        idOther.setValue("uuid:3456");
        idOther.setDigDocId(doc.getId());
        idOther.setRegistrarId(registrar.getId());
        digRepIdDao.insertDigDocId(idOther);
        //get ids
        assertEquals(2, digRepIdDao.getIdList(doc.getId()).size());
        digRepIdDao.deleteAllIdentifiersOfDigDoc(doc.getId());
        assertTrue(digRepIdDao.getIdList(doc.getId()).isEmpty());
    }

    public void testDeleteAllDigDocIds_noIds() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        digRepIdDao.deleteAllIdentifiersOfDigDoc(doc.getId());
    }

    public void testDeleteAllDigRepIdsOfEntity_unknownDigRep() throws Exception {
        try {
            digRepIdDao.deleteAllIdentifiersOfDigDoc(ILLEGAL_ID);
            fail();
        } catch (RecordNotFoundException e) {
            assertEquals(DigitalDocumentDAO.TABLE_NAME, e.getTableName());
        }
    }
}
