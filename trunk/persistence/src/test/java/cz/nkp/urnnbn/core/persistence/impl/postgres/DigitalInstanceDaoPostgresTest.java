/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import java.util.List;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalInstanceDaoPostgresTest extends AbstractDaoTest {

    public DigitalInstanceDaoPostgresTest(String testName) {
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
     * Test of insertDigInstance method, of class DigitalInstanceDaoPostgres.
     */
    public void testInsertDigInstance() throws Exception {
        DigitalLibrary lib = libraryPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(lib.getRegistrarId(), entity.getId());
        DigitalInstance instance = new DigitalInstance();
        instance.setDigDocId(doc.getId());
        instance.setLibraryId(lib.getId());
        instance.setUrl("http://something");
        digInstDao.insertDigInstance(instance);
    }

    public void testInsertDigInstance_unknownLibrary() throws Exception {
        IntelectualEntity entity = entityPersisted();
        DigitalDocument rep = documentPersisted(registrarPersisted().getId(), entity.getId());
        DigitalInstance instance = new DigitalInstance();
        instance.setDigDocId(rep.getId());
        instance.setLibraryId(ILLEGAL_ID);
        instance.setUrl("http://something");
        try {
            digInstDao.insertDigInstance(instance);
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }

    public void testInsertDigInstance_unknownDigRep() throws Exception {
        DigitalLibrary lib = libraryPersisted();
        DigitalInstance instance = new DigitalInstance();
        instance.setDigDocId(ILLEGAL_ID);
        instance.setLibraryId(lib.getId());
        instance.setUrl("http://something");
        try {
            digInstDao.insertDigInstance(instance);
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }

    public void testInsertDigInstance_twoInstancesForSameLibraryAndDigRep() throws Exception {
        DigitalLibrary lib = libraryPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument rep = documentPersisted(lib.getRegistrarId(), entity.getId());
        //first instance
        DigitalInstance first = new DigitalInstance();
        first.setDigDocId(rep.getId());
        first.setLibraryId(lib.getId());
        first.setUrl("http://something");
        digInstDao.insertDigInstance(first);
        //second
        DigitalInstance second = new DigitalInstance();
        second.setDigDocId(rep.getId());
        second.setLibraryId(lib.getId());
        second.setUrl("http://somethingElse");
        digInstDao.insertDigInstance(second);
        //check that have been inserted
        List<DigitalInstance> instances = digInstDao.getDigitalInstancesOfDigDoc(rep.getId());
        assertTrue(instances.contains(first));
        assertTrue(instances.contains(second));
    }

    /**
     * Test of getDigInstanceById method, of class DigitalInstanceDaoPostgres.
     */
    public void testGetDigInstanceById() throws Exception {
        DigitalLibrary lib = libraryPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument rep = documentPersisted(lib.getRegistrarId(), entity.getId());
        //insert
        DigitalInstance inserted = new DigitalInstance();
        inserted.setDigDocId(rep.getId());
        inserted.setLibraryId(lib.getId());
        inserted.setUrl("http://something");
        digInstDao.insertDigInstance(inserted);
        DigitalInstance fetched = digInstDao.getDigInstanceById(inserted.getId());
        assertEquals(inserted, fetched);
        assertNotNull(fetched);
        assertNotNull(fetched.getCreated());
        assertNotNull(fetched.getId());
        assertNotNull(fetched.getLibraryId());
        assertNotNull(fetched.getDigDocId());
    }

    public void testGetDigInstanceById_unknownId() throws Exception {
        try {
            digInstDao.getDigInstanceById(ILLEGAL_ID);
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }

    public void testGetDigitalInstancesOfDigRep() throws Exception {
        //uz se testuje v testInsertDigInstance_twoInstancesForSameLibraryAndDigRep
    }

    public void testGetDigitalInstancesOfDigRep_unknownId() throws Exception {
        try {
            digInstDao.getDigitalInstancesOfDigDoc(ILLEGAL_ID);
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }

    /**
     * Test of deleteDigInstance method, of class DigitalInstanceDaoPostgres.
     */
    public void testDeleteDigInstance() throws Exception {
        DigitalLibrary lib = libraryPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument rep = documentPersisted(lib.getRegistrarId(), entity.getId());
        //insert
        DigitalInstance inserted = new DigitalInstance();
        inserted.setDigDocId(rep.getId());
        inserted.setLibraryId(lib.getId());
        inserted.setUrl("http://something");
        digInstDao.insertDigInstance(inserted);
        digInstDao.getDigInstanceById(inserted.getId());
        //delete
        digInstDao.deleteDigInstance(inserted.getId());
        try {
            digInstDao.getDigInstanceById(inserted.getId());
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }
}
