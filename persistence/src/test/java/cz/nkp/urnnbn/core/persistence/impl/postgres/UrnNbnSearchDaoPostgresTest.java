/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.UrnNbnGenerator;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;

/**
 *
 * @author Martin Řehánek
 */
public class UrnNbnSearchDaoPostgresTest extends AbstractDaoTest {

    public UrnNbnSearchDaoPostgresTest(String testName) {
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
     * Test of insertGenerator method, of class UrnNbnBookingDaoPostgres.
     */
    public void testInsertUrnNbnSearch() throws Exception {
        Registrar registrar = registrarPersisted();
        UrnNbnGenerator search = new UrnNbnGenerator();
        search.setRegistrarId(registrar.getId());
        urnSearchDao.insertGenerator(search);
    }

    public void testInsertUrnNbnSearch_alreadyPresent() throws Exception {
        Registrar registrar = registrarPersisted();
        UrnNbnGenerator first = new UrnNbnGenerator();
        first.setRegistrarId(registrar.getId());
        urnSearchDao.insertGenerator(first);

        UrnNbnGenerator second = new UrnNbnGenerator();
        second.setRegistrarId(registrar.getId());
        try {
            urnSearchDao.insertGenerator(second);
            fail();
        } catch (AlreadyPresentException e) {
            //ok
        }
    }

    public void testInsertUrnNbnSearch_unknownRegistrar() throws Exception {
        UrnNbnGenerator search = new UrnNbnGenerator();
        search.setRegistrarId(ILLEGAL_ID);
        try {
            urnSearchDao.insertGenerator(search);
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }

    /**
     * Test of getGeneratorByRegistrarId method, of class UrnNbnBookingDaoPostgres.
     */
    public void testGetSearchByCode() throws Exception {
        //insert
        Registrar registrar = registrarPersisted();
        UrnNbnGenerator search = new UrnNbnGenerator();
        search.setRegistrarId(registrar.getId());
        urnSearchDao.insertGenerator(search);
        //get
        UrnNbnGenerator fetched = urnSearchDao.getGeneratorByRegistrarId(registrar.getId());
        assertEquals(search, fetched);
    }

    public void testGetSearchByCode_unknownCode() throws Exception {
        //get
        try {
            urnSearchDao.getGeneratorByRegistrarId(ILLEGAL_ID);
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }

    /**
     * Test of updateGenerator method, of class UrnNbnBookingDaoPostgres.
     */
    public void testUpdateUrnNbnSearch() throws Exception {
        //insert
        Registrar registrar = registrarPersisted();
        UrnNbnGenerator inserted = new UrnNbnGenerator();
        inserted.setRegistrarId(registrar.getId());
        urnSearchDao.insertGenerator(inserted);
        //fetch
        UrnNbnGenerator fetched = urnSearchDao.getGeneratorByRegistrarId(registrar.getId());
        assertEquals(inserted, fetched);
        //update
        UrnNbnGenerator updated = new UrnNbnGenerator();
        updated.setRegistrarId(registrar.getId());
        updated.setLastDocumentCode("   5");
        urnSearchDao.updateGenerator(updated);
        //fetch
        UrnNbnGenerator updatedAndFetched = urnSearchDao.getGeneratorByRegistrarId(registrar.getId());
        assertEquals(updatedAndFetched, updated);
        assertFalse(updatedAndFetched.equals(inserted));
    }

    public void testUpdateUrnNbnSearch_unknownBooking() throws Exception {
        Registrar registrar = registrarPersisted();
        UrnNbnGenerator search = new UrnNbnGenerator();
        search.setRegistrarId(registrar.getId());
        try {
            urnSearchDao.updateGenerator(search);
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }
}
