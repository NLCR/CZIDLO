/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.OriginType;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.IdPart;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;

/**
 *
 * @author Martin Řehánek
 */
public class OriginatorDaoPostgresTest extends AbstractDaoTest {

    public OriginatorDaoPostgresTest(String testName) {
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
     * Test of insertOriginator method, of class OriginatorDaoPostgres.
     */
    public void testInsertOriginator_ok() throws Exception {
        IntelectualEntity entity = entityPersisted();
        Originator orig = builder.originatorWithoutId();
        orig.setIntEntId(entity.getId());
        originatorDao.insertOriginator(orig);
    }

    public void testInsertOriginator_unknownEntityId() throws Exception {
        Originator orig = builder.originatorWithoutId();
        orig.setIntEntId(ILLEGAL_ID);
        try {
            originatorDao.insertOriginator(orig);
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }

    public void testInsertOriginaotr_twoOriginatorsForSameEntity() throws Exception {
        IntelectualEntity entity = entityPersisted();
        Originator first = builder.originatorWithoutId();
        Originator second = builder.originatorWithoutId();
        first.setIntEntId(entity.getId());
        second.setIntEntId(entity.getId());
        originatorDao.insertOriginator(first);
        try {
            originatorDao.insertOriginator(second);
            fail();
        } catch (AlreadyPresentException ex) {
            assertEquals(String.valueOf(second.getId()), ((IdPart) ex.getPresentObjectId()).getValue());
        }
    }

    /**
     * Test of getOriginatorById method, of class OriginatorDaoPostgres.
     */
    public void testGetOriginatorById() throws Exception {
        IntelectualEntity entity = entityPersisted();
        Originator inserted = builder.originatorWithoutId();
        inserted.setIntEntId(entity.getId());
        originatorDao.insertOriginator(inserted);
        Originator fetched = originatorDao.getOriginatorById(inserted.getId());
        assertNotNull(fetched);
        assertNotNull(fetched.getId());
        assertNotNull(fetched.getIntEntId());
        assertEquals(inserted, fetched);
    }

    public void testGetOriginatorById_unknownId() throws Exception {
        try {
            originatorDao.getOriginatorById(ILLEGAL_ID);
            fail();
        } catch (RecordNotFoundException e) {
            //OK
        }
    }

    /**
     * Test of updateOriginator method, of class OriginatorDaoPostgres.
     */
    public void testUpdateOriginator() throws Exception {
        IntelectualEntity entity = entityPersisted();
        Originator inserted = builder.originatorWithoutId();
        inserted.setIntEntId(entity.getId());
        originatorDao.insertOriginator(inserted);
        //update only value
        Originator valueUpdated = new Originator(inserted);
        valueUpdated.setValue(inserted.getValue() + "-new");
        originatorDao.updateOriginator(valueUpdated);
        Originator fetchedValueUpdated = originatorDao.getOriginatorById(inserted.getId());
        assertEquals(valueUpdated.getValue(), fetchedValueUpdated.getValue());
        assertFalse(inserted.getValue().equals(fetchedValueUpdated.getValue()));
        //update type and value
        Originator updated = new Originator(inserted);
        updated.setType(inserted.getType() == OriginType.AUTHOR ? OriginType.CORPORATION : OriginType.AUTHOR);
        updated.setValue(inserted.getValue() + "-another");
        originatorDao.updateOriginator(updated);
        Originator fetchedUpdated = originatorDao.getOriginatorById(inserted.getId());
        assertEquals(updated.getValue(), fetchedUpdated.getValue());
        assertFalse(inserted.getValue().equals(fetchedUpdated.getValue()));
    }

    public void testUpdateOriginator_invalidEntityId() throws Exception {
        IntelectualEntity entity = entityPersisted();
        Originator inserted = builder.originatorWithoutId();
        inserted.setIntEntId(entity.getId());
        originatorDao.insertOriginator(inserted);
        //change id
        Originator updated = new Originator(inserted);
        updated.setIntEntId(ILLEGAL_ID);
        //update 
        try {
            originatorDao.updateOriginator(updated);
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }
}
