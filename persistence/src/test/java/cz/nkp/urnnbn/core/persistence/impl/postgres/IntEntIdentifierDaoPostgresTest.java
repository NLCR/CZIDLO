/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import java.util.List;

import cz.nkp.urnnbn.core.IntEntIdType;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.IdPart;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;

/**
 *
 * @author Martin Řehánek
 */
public class IntEntIdentifierDaoPostgresTest extends AbstractDaoTest {

    public IntEntIdentifierDaoPostgresTest(String testName) {
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
     * Test of insertIntEntId method, of class IntEntIdentifierDaoPostgres.
     */
    public void testInsertIntEntId() throws Exception {
        IntelectualEntity entity = entityPersisted();
        IntEntIdentifier entityId = builder.intEntIdentifier(entity.getId());
        intEntIdDao.insertIntEntId(entityId);
        // only testing if no exception was thrown
    }

    public void testInsertIntEntId_invalidEntityDbId() throws Exception {
        try {
            IntEntIdentifier entityId = builder.intEntIdentifier(ILLEGAL_ID);
            intEntIdDao.insertIntEntId(entityId);
            fail();
        } catch (RecordNotFoundException e) {
            // OK
        }
    }

    public void testInsertIntEntId_idOfGivenTypeExists() throws Exception {
        IntelectualEntity entity = entityPersisted();
        IntEntIdentifier entityId = new IntEntIdentifier();
        entityId.setIntEntDbId(entity.getId());
        entityId.setType(IntEntIdType.OTHER);
        entityId.setValue("firstValue");
        intEntIdDao.insertIntEntId(entityId);
        // try to insert twice identifier of same type to same entity
        try {
            entityId.setValue("secondValue");
            intEntIdDao.insertIntEntId(entityId);
            fail();
        } catch (AlreadyPresentException ex) {
            IdPart[] id = (IdPart[]) ex.getPresentObjectId();
            assertEquals(String.valueOf(entity.getId()), id[0].getValue());
            assertEquals(String.valueOf(entityId.getType()), id[1].getValue());
        }
    }

    /**
     * Test of getIdList method, of class IntEntIdentifierDaoPostgres.
     */
    public void testGetIdList() throws Exception {
        IntelectualEntity entity = entityPersisted();
        // ISBN
        IntEntIdentifier isbn = new IntEntIdentifier();
        isbn.setIntEntDbId(entity.getId());
        isbn.setType(IntEntIdType.ISBN);
        isbn.setValue("isbnValue");
        intEntIdDao.insertIntEntId(isbn);
        // CCNB
        IntEntIdentifier ccnb = new IntEntIdentifier();
        ccnb.setIntEntDbId(entity.getId());
        ccnb.setType(IntEntIdType.CCNB);
        ccnb.setValue("ccnbValue");
        intEntIdDao.insertIntEntId(ccnb);
        // get all
        List<IntEntIdentifier> idList = intEntIdDao.getIdList(entity.getId());
        assertEquals(2, idList.size());
        assertTrue(idList.contains(isbn));
        assertTrue(idList.contains(ccnb));
    }

    /**
     * Test of updateIntEntIdValue method, of class IntEntIdentifierDaoPostgres.
     */
    public void testUpdateIntEntIdValue() throws Exception {
        IntelectualEntity entity = entityPersisted();
        // insert
        IntEntIdentifier inserted = new IntEntIdentifier();
        inserted.setIntEntDbId(entity.getId());
        inserted.setType(IntEntIdType.ISBN);
        inserted.setValue("oldValue");
        intEntIdDao.insertIntEntId(inserted);
        // update
        IntEntIdentifier second = new IntEntIdentifier();
        second.setIntEntDbId(entity.getId());
        second.setType(IntEntIdType.ISBN);
        second.setValue("newValue");
        intEntIdDao.updateIntEntIdValue(second);
        // check
        List<IntEntIdentifier> idList = intEntIdDao.getIdList(entity.getId());
        for (IntEntIdentifier id : idList) {
            if (id.getType() == IntEntIdType.ISBN) {
                assertEquals(second.getValue(), id.getValue());
            }
        }
    }

    /**
     * Test of deleteIntEntIdentifier method, of class IntEntIdentifierDaoPostgres.
     */
    public void testDeleteIntEntIdentifier() throws Exception {
        IntelectualEntity entity = entityPersisted();
        // ISBN
        IntEntIdentifier isbn = new IntEntIdentifier();
        isbn.setIntEntDbId(entity.getId());
        isbn.setType(IntEntIdType.ISBN);
        isbn.setValue("isbnValue");
        intEntIdDao.insertIntEntId(isbn);
        // fetch
        List<Long> ids = entityDao.getEntitiesDbIdListByIdentifier(isbn.getType(), isbn.getValue());
        assertEquals(1, ids.size());
        // delete
        intEntIdDao.deleteIntEntIdentifier(entity.getId(), isbn.getType());
        // fetch after deletion
        List<Long> idsAfterDeleted = entityDao.getEntitiesDbIdListByIdentifier(isbn.getType(), isbn.getValue());
        assertEquals(0, idsAfterDeleted.size());
    }

    /**
     * Test of deleteAllIntEntIdsOfEntity method, of class IntEntIdentifierDaoPostgres.
     */
    public void testDeleteAllIntEntIdsOfEntity() throws Exception {
        IntelectualEntity entity = entityPersisted();
        // ISBN
        IntEntIdentifier isbn = new IntEntIdentifier();
        isbn.setIntEntDbId(entity.getId());
        isbn.setType(IntEntIdType.ISBN);
        isbn.setValue("isbnValue");
        intEntIdDao.insertIntEntId(isbn);
        // CCNB
        IntEntIdentifier ccnb = new IntEntIdentifier();
        ccnb.setIntEntDbId(entity.getId());
        ccnb.setType(IntEntIdType.CCNB);
        ccnb.setValue("ccnbValue");
        intEntIdDao.insertIntEntId(ccnb);
        // get all
        List<IntEntIdentifier> idList = intEntIdDao.getIdList(entity.getId());
        assertEquals(2, idList.size());
        // delete all identifiers of entity
        intEntIdDao.deleteAllIntEntIdsOfEntity(entity.getId());
        List<IntEntIdentifier> idListAfterDeletion = intEntIdDao.getIdList(entity.getId());
        assertEquals(0, idListAfterDeletion.size());
    }

    public void testDeleteAllIntEntIdsOfEntity_invalidEntityId() throws Exception {
        // try to delete from non existing entity
        try {
            intEntIdDao.deleteAllIntEntIdsOfEntity(ILLEGAL_ID);
            fail();
        } catch (RecordNotFoundException e) {
            // OK
        }
    }
}
