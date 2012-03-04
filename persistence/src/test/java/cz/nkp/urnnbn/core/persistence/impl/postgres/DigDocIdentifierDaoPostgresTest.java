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
    public void testInsertDigRepId() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        //insert identifier
        DigDocIdentifier id = builder.digRepIdentifierWithoutIds();
        id.setType(DigDocIdType.valueOf("K4_pid"));
        id.setValue("uuid:123");
        id.setDigDocId(doc.getId());
        id.setRegistrarId(registrar.getId());
        digRepIdDao.insertDigDocId(id);
        //insert another typ of identifiere
        DigDocIdentifier id2 = builder.digRepIdentifierWithoutIds();
        id2.setType(DigDocIdType.valueOf("signatura"));
        id2.setValue("nevim,neco");
        id2.setDigDocId(doc.getId());
        id2.setRegistrarId(registrar.getId());
        digRepIdDao.insertDigDocId(id2);
    }

    public void testInsertDigRepId_emptyValue() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        DigDocIdentifier id = builder.digRepIdentifierWithoutIds();
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

    public void testInsertDigRepId_unknownDigRep() throws Exception {
        Registrar registrar = registrarPersisted();
        DigDocIdentifier id = builder.digRepIdentifierWithoutIds();
        id.setDigDocId(ILLEGAL_ID);
        id.setRegistrarId(registrar.getId());
        try {
            digRepIdDao.insertDigDocId(id);
            fail();
        } catch (RecordNotFoundException e) {
            assertEquals(DigitalDocumentDAO.TABLE_NAME, e.getTableName());
        }
    }

    public void testInsertDigRepId_unknownRegistrar() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        DigDocIdentifier id = builder.digRepIdentifierWithoutIds();
        id.setDigDocId(doc.getId());
        id.setRegistrarId(ILLEGAL_ID);
        try {
            digRepIdDao.insertDigDocId(id);
            fail();
        } catch (RecordNotFoundException e) {
            assertEquals(RegistrarDAO.TABLE_NAME, e.getTableName());
        }
    }

    public void testInsertDigRepId_insertTwiceSameIdTypeAndValueForSameDigRep() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        DigDocIdentifier identifier = builder.digRepIdentifierWithoutIds();
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

    public void testInsertDigRepId_insertTwiceSameIdTypeAndValueForSameRegistrar() throws Exception {
        Registrar registrar = registrarPersisted();
        //first digRep & id
        IntelectualEntity entity1 = entityPersisted();
        DigitalDocument doc1 = documentPersisted(registrar.getId(), entity1.getId());
        DigDocIdentifier id1 = builder.digRepIdentifierWithoutIds();
        id1.setDigDocId(doc1.getId());
        id1.setRegistrarId(registrar.getId());
        digRepIdDao.insertDigDocId(id1);
        //second digRep & id
        IntelectualEntity entity2 = entityPersisted();
        DigitalDocument doc2 = documentPersisted(registrar.getId(), entity2.getId());
        DigDocIdentifier id2 = builder.digRepIdentifierWithoutIds();
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
    }

    public void testUpdateDigRepIdValue() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        //insert identifier
        DigDocIdentifier inserted = builder.digRepIdentifierWithoutIds();
        inserted.setType(DigDocIdType.valueOf("my_Id"));
        inserted.setValue("oldValue");
        inserted.setDigDocId(doc.getId());
        inserted.setRegistrarId(registrar.getId());
        digRepIdDao.insertDigDocId(inserted);
        //update
        DigDocIdentifier updated = new DigDocIdentifier(inserted);
        updated.setValue("newValue");
        digRepIdDao.updateDigRepIdValue(updated);
        //fetch
        DigDocIdentifier fetched = digRepIdDao.getIdList(doc.getId()).get(0);
        assertEquals(updated, fetched);
        assertFalse(inserted.equals(fetched));
    }

    public void testUpdateDigRepIdValue_valueCollision() throws Exception {
        Registrar registrar = registrarPersisted();
        DigDocIdType idType = DigDocIdType.valueOf("some_id_type");
        String collidingValue = "collision";
        //first digRep
        IntelectualEntity entity1 = entityPersisted();
        DigitalDocument doc1 = documentPersisted(registrar.getId(), entity1.getId());
        //identifier of first digRep
        DigDocIdentifier digRep1Id = builder.digRepIdentifierWithoutIds();
        digRep1Id.setType(idType);
        digRep1Id.setValue(collidingValue);
        digRep1Id.setDigDocId(doc1.getId());
        digRep1Id.setRegistrarId(registrar.getId());
        digRepIdDao.insertDigDocId(digRep1Id);

        //second digRep
        IntelectualEntity entity2 = entityPersisted();
        DigitalDocument doc2 = documentPersisted(registrar.getId(), entity2.getId());
        //insert identifier to digRep2
        DigDocIdentifier digRep2Id = builder.digRepIdentifierWithoutIds();
        digRep2Id.setType(idType);
        digRep2Id.setValue("okValue");
        digRep2Id.setDigDocId(doc2.getId());
        digRep2Id.setRegistrarId(registrar.getId());
        digRepIdDao.insertDigDocId(digRep2Id);
        //update
        DigDocIdentifier updated = new DigDocIdentifier(digRep2Id);
        updated.setValue(collidingValue);
        try {
            digRepIdDao.updateDigRepIdValue(updated);
            fail();
        } catch (AlreadyPresentException e) {
            //ok
        }
    }

    public void testUpdateDigRepIdValue_unknownRegistrarOrDigRep() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        //insert identifier
        DigDocIdentifier inserted = builder.digRepIdentifierWithoutIds();
        inserted.setDigDocId(doc.getId());
        inserted.setRegistrarId(registrar.getId());
        digRepIdDao.insertDigDocId(inserted);
        //set incorrect registrar id and update
        DigDocIdentifier updated = new DigDocIdentifier(inserted);
        updated.setRegistrarId(ILLEGAL_ID);
        try {
            digRepIdDao.updateDigRepIdValue(updated);
            fail();
        } catch (RecordNotFoundException e) {
            assertEquals(RegistrarDAO.TABLE_NAME, e.getTableName());
        }
        //set incorrect digRepId and update
        updated.setRegistrarId(registrar.getId());
        updated.setDigDocId(ILLEGAL_ID);
        try {
            digRepIdDao.updateDigRepIdValue(updated);
            fail();
        } catch (RecordNotFoundException e) {
            assertEquals(DigitalDocumentDAO.TABLE_NAME, e.getTableName());
        }
    }

    public void testDeleteDigRepIdentifier() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        //insert identifier
        DigDocIdentifier inserted = builder.digRepIdentifierWithoutIds();
        inserted.setDigDocId(doc.getId());
        inserted.setRegistrarId(registrar.getId());
        digRepIdDao.insertDigDocId(inserted);
        assertTrue(digRepIdDao.getIdList(doc.getId()).contains(inserted));
        //delete
        digRepIdDao.deleteDigDocIdentifier(doc.getId(), inserted.getType());
        assertFalse(digRepIdDao.getIdList(doc.getId()).contains(inserted));
    }

    public void testDeleteDigRepIdentifier_unknownDigRep() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        //insert identifier
        DigDocIdentifier inserted = builder.digRepIdentifierWithoutIds();
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

    public void testDeleteDigRepIdentifier_deletedRecordDoesntExist() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        //insert identifier
        DigDocIdentifier inserted = builder.digRepIdentifierWithoutIds();
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

    public void testDeleteAllDigRepIdsOfEntity() throws Exception {
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

    public void testDeleteAllDigReIds_noIds() throws Exception {
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
