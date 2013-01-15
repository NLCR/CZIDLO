/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
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
        instance.setActive(Boolean.TRUE);
        digInstDao.insertDigInstance(instance);
    }

    public void testInsertDigInstance_unknownLibrary() throws Exception {
        IntelectualEntity entity = entityPersisted();
        DigitalDocument rep = documentPersisted(registrarPersisted().getId(), entity.getId());
        DigitalInstance instance = new DigitalInstance();
        instance.setDigDocId(rep.getId());
        instance.setLibraryId(ILLEGAL_ID);
        instance.setUrl("http://something");
        instance.setActive(Boolean.TRUE);
        try {
            digInstDao.insertDigInstance(instance);
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }

    public void testInsertDigInstance_unknownDigDoc() throws Exception {
        DigitalLibrary lib = libraryPersisted();
        DigitalInstance instance = new DigitalInstance();
        instance.setDigDocId(ILLEGAL_ID);
        instance.setLibraryId(lib.getId());
        instance.setUrl("http://something");
        instance.setActive(Boolean.TRUE);
        try {
            digInstDao.insertDigInstance(instance);
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }

    public void testInsertDigInstance_twoInstancesForSameLibraryAndDigDoc() throws Exception {
        DigitalLibrary lib = libraryPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(lib.getRegistrarId(), entity.getId());
        //first instance
        DigitalInstance first = new DigitalInstance();
        first.setDigDocId(doc.getId());
        first.setLibraryId(lib.getId());
        first.setActive(Boolean.TRUE);
        first.setUrl("http://something");
        digInstDao.insertDigInstance(first);
        //second
        DigitalInstance second = new DigitalInstance();
        second.setDigDocId(doc.getId());
        second.setLibraryId(lib.getId());
        second.setUrl("http://somethingElse");
        second.setActive(Boolean.TRUE);
        digInstDao.insertDigInstance(second);
        //check that have been inserted
        List<DigitalInstance> instances = digInstDao.getDigitalInstancesOfDigDoc(doc.getId());
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
        inserted.setActive(Boolean.TRUE);
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

    public void testGetDigitalInstancesOfDigDoc() throws Exception {
        //uz se testuje v testInsertDigInstance_twoInstancesForSameLibraryAndDigDoc
    }

    public void testGetDigitalInstancesOfDigDoc_unknownId() throws Exception {
        try {
            digInstDao.getDigitalInstancesOfDigDoc(ILLEGAL_ID);
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }

    /**
     * Test of deactivateDigInstance method, of class
     * DigitalInstanceDaoPostgres.
     */
    public void testDeactivateDigitalInstance() throws Exception {
        DigitalLibrary lib = libraryPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument rep = documentPersisted(lib.getRegistrarId(), entity.getId());
        //insert
        DigitalInstance inserted = new DigitalInstance();
        inserted.setDigDocId(rep.getId());
        inserted.setLibraryId(lib.getId());
        inserted.setUrl("http://something");
        inserted.setActive(Boolean.TRUE);
        digInstDao.insertDigInstance(inserted);
        //fetch
        DigitalInstance beforeDeactivation = digInstDao.getDigInstanceById(inserted.getId());
        assertTrue(beforeDeactivation.isActive());
        assertNotNull(beforeDeactivation.getCreated());
        assertNull(beforeDeactivation.getDeactivated());
        
        //deactivate
        digInstDao.deactivateDigInstance(inserted.getId());
        DigitalInstance deactivated = digInstDao.getDigInstanceById(inserted.getId());
        assertFalse(deactivated.isActive());
        assertNotNull(deactivated.getCreated());
        assertNotNull(deactivated.getDeactivated());
        
        //timestamps
        assertTrue(beforeDeactivation.getCreated().isBefore(deactivated.getDeactivated()));
    }
}
