/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import java.util.List;

/**
 * 
 * @author Martin Řehánek
 */
public class DigitalLibraryDaoPostgresTest extends AbstractDaoTest {

    public DigitalLibraryDaoPostgresTest(String testName) {
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
     * Test of insertLibrary method, of class DigitalLibraryDaoPostgres.
     */
    public void testInsertLibrary() throws Exception {
        Registrar registrar = builder.registrarWithoutId();
        Long registrarId = registrarDao.insertRegistrar(registrar);
        DigitalLibrary library = builder.digLibraryWithoutIdAndRegistrarId();
        library.setRegistrarId(registrarId);
        long libraryId = libraryDao.insertLibrary(library);
        assertTrue(libraryId != ILLEGAL_ID);
    }

    public void testInsertLibraryInvalidRegistrarId() throws Exception {
        try {
            DigitalLibrary library = builder.digLibraryWithoutIdAndRegistrarId();
            library.setRegistrarId(ILLEGAL_ID);
            libraryDao.insertLibrary(library);
            fail();
        } catch (RecordNotFoundException e) {
            // OK
        }
    }

    /**
     * Test of getLibraryById method, of class DigitalLibraryDaoPostgres.
     */
    public void testGetLibraryById() throws Exception {
        // insert
        DigitalLibrary inserted = libraryPersisted();
        // fetch
        DigitalLibrary fetched = libraryDao.getLibraryById(inserted.getId());
        assertEquals(inserted, fetched);
        assertNotNull(fetched.getCreated());
        assertNotNull(fetched.getId());
        assertNotNull(fetched.getRegistrarId());
    }

    public void testGetLibraryByIllegalId() throws Exception {
        try {
            libraryDao.getLibraryById(ILLEGAL_ID);
            fail();
        } catch (RecordNotFoundException e) {
            // ok
        }
    }

    public void testGetLibrariesByRegistrarId() throws Exception {
        // registrar
        Registrar registrar = builder.registrarWithoutId();
        Long registrarId = registrarDao.insertRegistrar(registrar);
        // first library
        DigitalLibrary first = builder.digLibraryWithoutIdAndRegistrarId();
        first.setRegistrarId(registrarId);
        libraryDao.insertLibrary(first);
        // second library
        DigitalLibrary second = builder.digLibraryWithoutIdAndRegistrarId();
        second.setRegistrarId(registrarId);
        libraryDao.insertLibrary(second);
        List<DigitalLibrary> libraries = libraryDao.getLibraries(registrarId);
        assertEquals(2, libraries.size());
        assertTrue(libraries.contains(first));
        assertTrue(libraries.contains(second));
    }

    public void testGetLibrariesByRegistrarId_unkownId() throws Exception {
        try {
            libraryDao.getLibraries(ILLEGAL_ID);
            fail();
        } catch (RecordNotFoundException e) {
            // ok
        }
    }

    public void testGetAllLibraries() throws Exception {
        long first = libraryPersisted().getId();
        long second = libraryPersisted().getId();
        long third = libraryPersisted().getId();
        List<Long> idList = libraryDao.getAllLibrariesId();
        assertTrue(idList.contains(first));
        assertTrue(idList.contains(second));
        assertTrue(idList.contains(third));
        assertEquals(3, idList.size());
    }

    /**
     * Test of updateLibrary method, of class DigitalLibraryDaoPostgres.
     */
    public void testUpdateLibrary() throws Exception {
        // registrar
        Registrar registrar = builder.registrarWithoutId();
        Long registrarId = registrarDao.insertRegistrar(registrar);
        // original library
        DigitalLibrary original = builder.digLibraryWithoutIdAndRegistrarId();
        original.setName("Kramerius4");
        original.setRegistrarId(registrarId);
        libraryDao.insertLibrary(original);
        // create updated
        DigitalLibrary updated = new DigitalLibrary(original);
        updated.setName("Kramerius3");
        updated.setName("Stary kramerius");
        libraryDao.updateLibrary(updated);
        // get by id
        DigitalLibrary returned = libraryDao.getLibraryById(original.getId());
        assertEquals(updated.getName(), returned.getName());
        assertFalse(original.getName().equals(returned.getName()));
    }

    /**
     * Test of deleteLibrary method, of class DigitalLibraryDaoPostgres.
     */
    public void testDeleteLibrary() throws Exception {
        DigitalLibrary library = libraryPersisted();
        // exists
        libraryDao.getLibraryById(library.getId());
        // delete
        libraryDao.deleteLibrary(library.getId());
        try {
            libraryDao.getLibraryById(library.getId());
            fail();
        } catch (RecordNotFoundException e) {
            // OK
        }
    }

    public void testDeleteNotexistingLibrary() throws Exception {
        try {
            libraryDao.deleteLibrary(ILLEGAL_ID);
            fail();
        } catch (RecordNotFoundException e) {
            // OK
        }
    }

    @SuppressWarnings("unused")
    public void testDeleteAllLibraries() throws Exception {
        long first = libraryPersisted().getId();
        long second = libraryPersisted().getId();
        long third = libraryPersisted().getId();
        // delete all
        libraryDao.deleteAllLibraries();
        List<Long> idList = libraryDao.getAllLibrariesId();
        assertTrue(idList.isEmpty());
    }
}
