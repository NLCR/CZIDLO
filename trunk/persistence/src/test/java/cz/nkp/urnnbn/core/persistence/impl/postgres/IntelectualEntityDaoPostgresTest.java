/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.IntEntIdType;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.dto.SourceDocument;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;

/**
 *
 * @author Martin Řehánek
 */
public class IntelectualEntityDaoPostgresTest extends AbstractDaoTest {

    public IntelectualEntityDaoPostgresTest(String testName) {
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
     * Test of insertIntelectualEntity method, of class IntelectualEntityDaoPostgres.
     */
    public void testInsertIntelectualEntity() throws Exception {
        IntelectualEntity entity = builder.intEntityWithoutId();
        long id = entityDao.insertIntelectualEntity(entity);
        assertTrue(id != ILLEGAL_ID);
    }

    /**
     * Test of getEntityByDbId method, of class IntelectualEntityDaoPostgres.
     */
    public void testGetEntityByDbId() throws Exception {
        IntelectualEntity entity = builder.intEntityWithoutId();
        Long id = entityDao.insertIntelectualEntity(entity);

        IntelectualEntity fetched = entityDao.getEntityByDbId(id);
        assertNotNull(fetched);
        assertNotNull(fetched.getCreated());
        assertNotNull(fetched.getId());
        assertEquals(id, fetched.getId());
    }

    public void testGetEntityByIllegalDbId() throws Exception {
        try {
            entityDao.getEntityByDbId(ILLEGAL_ID);
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }

    /**
     * Test of getEntityByIdentifier method, of class IntelectualEntityDaoPostgres.
     */
    public void testGetEntityByIdentifier() throws Exception {
        IntelectualEntity entity = entityPersisted();
        //ISBN
        IntEntIdentifier isbn = new IntEntIdentifier();
        isbn.setIntEntDbId(entity.getId());
        isbn.setType(IntEntIdType.ISBN);
        isbn.setValue("isbnValue");
        intEntIdDao.insertIntEntId(isbn);
        //find by ISBN
        List<Long> foundIds = entityDao.getEntitiesDbIdListByIdentifier(IntEntIdType.ISBN, isbn.getValue());
        assertEquals(1, foundIds.size());
        List<IntelectualEntity> foundEntities = new ArrayList<IntelectualEntity>(foundIds.size());
        for (Long dbId : foundIds) {
            foundEntities.add(entityDao.getEntityByDbId(dbId));
        }
        assertTrue(foundEntities.contains(entity));
    }

    /**
     * Test of getEntityByIdentifierValue method, of class IntelectualEntityDaoPostgres.
     */
    public void testGetEntityByIdentifierValue() throws Exception {
        String value = "something";
        //ISBN
        IntelectualEntity entity1 = entityPersisted();
        IntEntIdentifier isbn = new IntEntIdentifier();
        isbn.setIntEntDbId(entity1.getId());
        isbn.setType(IntEntIdType.ISBN);
        isbn.setValue(value);
        intEntIdDao.insertIntEntId(isbn);
        //CCNB
        IntelectualEntity entity2 = entityPersisted();
        IntEntIdentifier ccnb = new IntEntIdentifier();
        ccnb.setIntEntDbId(entity2.getId());
        ccnb.setType(IntEntIdType.CCNB);
        ccnb.setValue(value);
        intEntIdDao.insertIntEntId(ccnb);

        //find by the value
        List<Long> foundIds = entityDao.getEntitiesDbIdListByIdentifierValue(value);
        assertEquals(2, foundIds.size());
        assertTrue(foundIds.contains(entity1.getId()));
        assertTrue(foundIds.contains(entity2.getId()));
        List<IntelectualEntity> foundEntities = new ArrayList<IntelectualEntity>(foundIds.size());
        for (Long dbId : foundIds) {
            foundEntities.add(entityDao.getEntityByDbId(dbId));
        }
        assertTrue(foundEntities.contains(entity1));
        assertTrue(foundEntities.contains(entity2));
    }

    public void testGetEntitiesDbIdListByTimestamps_from_until() throws Exception {
        //before - firstEnt - between - secondEnt - after
        DateTime before = new DateTime();
        Thread.sleep(1000);
        IntelectualEntity firstEnt = entityPersisted();
        Thread.sleep(1000);
        DateTime between = new DateTime();
        Thread.sleep(1000);
        IntelectualEntity secondEnt = entityPersisted();
        Thread.sleep(1000);
        DateTime after = new DateTime();
        //before-before
        List<Long> idList = entityDao.getEntitiesDbIdListByTimestamps(before, before);
        assertTrue(idList.isEmpty());
        //before-first
        idList = entityDao.getEntitiesDbIdListByTimestamps(before, firstEnt.getModified());
        assertEquals(1, idList.size());
        assertTrue(idList.contains(firstEnt.getId()));
        //before-between
        idList = entityDao.getEntitiesDbIdListByTimestamps(before, between);
        assertEquals(1, idList.size());
        assertTrue(idList.contains(firstEnt.getId()));
        //before-second
        idList = entityDao.getEntitiesDbIdListByTimestamps(before, secondEnt.getModified());
        assertEquals(2, idList.size());
        assertTrue(idList.contains(firstEnt.getId()));
        assertTrue(idList.contains(secondEnt.getId()));
        //before-after
        idList = entityDao.getEntitiesDbIdListByTimestamps(before, after);
        assertEquals(2, idList.size());
        assertTrue(idList.contains(firstEnt.getId()));
        assertTrue(idList.contains(secondEnt.getId()));
        //between-between
        idList = entityDao.getEntitiesDbIdListByTimestamps(between, between);
        assertTrue(idList.isEmpty());
        //between-second
        idList = entityDao.getEntitiesDbIdListByTimestamps(between, secondEnt.getModified());
        assertEquals(1, idList.size());
        assertTrue(idList.contains(secondEnt.getId()));
        //betwen-after
        idList = entityDao.getEntitiesDbIdListByTimestamps(between, after);
        assertEquals(1, idList.size());
        assertTrue(idList.contains(secondEnt.getId()));
        //after-after
        idList = entityDao.getEntitiesDbIdListByTimestamps(after, after);
        assertTrue(idList.isEmpty());
    }

    public void testGetEntitiesDbIdListByTimestamps_from_only() throws Exception {
        //before - firstEnt - between - secondEnt - after
        DateTime before = new DateTime();
        Thread.sleep(1000);
        IntelectualEntity firstEnt = entityPersisted();
        Thread.sleep(1000);
        DateTime between = new DateTime();
        Thread.sleep(1000);
        IntelectualEntity secondEnt = entityPersisted();
        Thread.sleep(1000);
        DateTime after = new DateTime();
        //before
        List<Long> idList = entityDao.getEntitiesDbIdListByTimestamps(before, null);
        assertEquals(2, idList.size());
        assertTrue(idList.contains(firstEnt.getId()));
        assertTrue(idList.contains(secondEnt.getId()));
        //firstEnt
        idList = entityDao.getEntitiesDbIdListByTimestamps(firstEnt.getModified(), null);
        assertEquals(2, idList.size());
        assertTrue(idList.contains(firstEnt.getId()));
        assertTrue(idList.contains(secondEnt.getId()));
        //betwen
        idList = entityDao.getEntitiesDbIdListByTimestamps(between, null);
        assertEquals(1, idList.size());
        assertTrue(idList.contains(secondEnt.getId()));
        //secondEnt
        idList = entityDao.getEntitiesDbIdListByTimestamps(secondEnt.getModified(), null);
        assertEquals(1, idList.size());
        assertTrue(idList.contains(secondEnt.getId()));
        //after
        idList = entityDao.getEntitiesDbIdListByTimestamps(after, null);
        assertTrue(idList.isEmpty());
    }

    public void testGetEntitiesDbIdListByTimestamps_until_only() throws Exception {
        //before - firstEnt - between - secondEnt - after
        DateTime before = new DateTime();
        Thread.sleep(1000);
        IntelectualEntity firstEnt = entityPersisted();
        Thread.sleep(1000);
        DateTime between = new DateTime();
        Thread.sleep(1000);
        IntelectualEntity secondEnt = entityPersisted();
        Thread.sleep(1000);
        DateTime after = new DateTime();
        //before
        List<Long> idList = entityDao.getEntitiesDbIdListByTimestamps(null, before);
        assertTrue(idList.isEmpty());
        //firstEnt
        idList = entityDao.getEntitiesDbIdListByTimestamps(null, firstEnt.getModified());
        assertEquals(1, idList.size());
        assertTrue(idList.contains(firstEnt.getId()));
        //betwen
        idList = entityDao.getEntitiesDbIdListByTimestamps(null, between);
        assertEquals(1, idList.size());
        assertTrue(idList.contains(firstEnt.getId()));
        //secondEnt
        idList = entityDao.getEntitiesDbIdListByTimestamps(null, secondEnt.getModified());
        assertEquals(2, idList.size());
        assertTrue(idList.contains(firstEnt.getId()));
        assertTrue(idList.contains(secondEnt.getId()));
        //after
        idList = entityDao.getEntitiesDbIdListByTimestamps(null, after);
        assertEquals(2, idList.size());
        assertTrue(idList.contains(firstEnt.getId()));
        assertTrue(idList.contains(secondEnt.getId()));
    }

    /**
     * Test of getEntitiesCount method, of class IntelectualEntityDaoPostgres.
     */
    public void testGetEntitiesCount_0args() throws Exception {
        long total = 5;
        for (int i = 0; i < 5; i++) {
            IntelectualEntity entity = builder.intEntityWithoutId();
            entityDao.insertIntelectualEntity(entity);
        }
        assertEquals(Long.valueOf(total), entityDao.getEntitiesCount());
    }

    /**
     * Test of updateEntity method, of class IntelectualEntityDaoPostgres.
     */
    public void testUpdateEntity() throws Exception {
        IntelectualEntity inserted = builder.intEntityWithoutId();
        inserted.setDegreeAwardingInstitution("CUNI");
        long id = entityDao.insertIntelectualEntity(inserted);
        IntelectualEntity updated = new IntelectualEntity(inserted);
        updated.setDegreeAwardingInstitution("MUNI");
        entityDao.updateEntity(updated);

        IntelectualEntity fetched = entityDao.getEntityByDbId(id);
        assertEquals(updated.getDegreeAwardingInstitution(), fetched.getDegreeAwardingInstitution());
        assertFalse(inserted.getDegreeAwardingInstitution().equals(fetched.getDegreeAwardingInstitution()));
    }

    /**
     * Test of deleteEntity method, of class IntelectualEntityDaoPostgres.
     */
    public void testDeleteEntity() throws Exception {
        //insert entity
        IntelectualEntity entity = builder.intEntityWithoutId();
        long entityId = entityDao.insertIntelectualEntity(entity);
        //insert publication
        Publication publication = builder.publicationWithoutId();
        publication.setIntEntId(entity.getId());
        publicationDao.insertPublication(publication);
        //insert identifier
        IntEntIdentifier entIdentifier = builder.intEntIdentifier(entity.getId());
        intEntIdDao.insertIntEntId(entIdentifier);
        //insert originator
        Originator originator = builder.originatorWithoutId();
        originator.setIntEntId(entityId);
        originatorDao.insertOriginator(originator);
        //insert source document
        SourceDocument srcDoc = builder.sourceDocumentWithoutId();
        srcDoc.setIntEntId(entityId);
        srcDocDao.insertSrcDoc(srcDoc);
        //delete entity
        entityDao.deleteEntity(entityId);
        //try to fetch entity after deleted
        try {
            entityDao.getEntityByDbId(entityId);
            fail();
        } catch (RecordNotFoundException e) {
            //OK
        }
        //try to fetch identifiers after deleted
        try {
            intEntIdDao.getIdList(entityId);
            fail();
        } catch (RecordNotFoundException e) {
            //OK
        }
        //try to fetch publication after deleted
        try {
            publicationDao.getPublicationById(entityId);
            fail();
        } catch (RecordNotFoundException e) {
            //OK
        }
        //try to fetch originator
        try {
            originatorDao.getOriginatorById(entityId);
            fail();
        } catch (RecordNotFoundException e) {
            //OK
        }
        //try to fetch source document
        try {
            srcDocDao.getSrcDocById(entityId);
            fail();
        } catch (RecordNotFoundException e) {
            //OK
        }
    }

    public void testDeleteNotexistingEntity() throws Exception {
        try {
            entityDao.deleteEntity(ILLEGAL_ID);
            fail();
        } catch (RecordNotFoundException e) {
            //OK
        }
    }

    /**
     * Test of deleteAllEntities method, of class IntelectualEntityDaoPostgres.
     */
    public void testDeleteAllEntities() throws Exception {
        entityDao.insertIntelectualEntity(builder.intEntityWithoutId());
        entityDao.insertIntelectualEntity(builder.intEntityWithoutId());
        entityDao.insertIntelectualEntity(builder.intEntityWithoutId());
        assertEquals(Long.valueOf(3), entityDao.getEntitiesCount());
        entityDao.deleteAllEntities();
        assertEquals(Long.valueOf(0), entityDao.getEntitiesCount());
    }
}
