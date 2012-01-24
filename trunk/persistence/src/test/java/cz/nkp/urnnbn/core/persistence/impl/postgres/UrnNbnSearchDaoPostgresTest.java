/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.UrnNbnSearch;
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
     * Test of insertUrnNbnSearch method, of class UrnNbnBookingDaoPostgres.
     */
    public void testInsertUrnNbnSearch() throws Exception {
        Registrar registrar = registrarPersisted();
        UrnNbnSearch search = new UrnNbnSearch();
        search.setRegistrarId(registrar.getId());
        urnSearchDao.insertUrnNbnSearch(search);
    }

    public void testInsertUrnNbnSearch_alreadyPresent() throws Exception {
        Registrar registrar = registrarPersisted();
        UrnNbnSearch first = new UrnNbnSearch();
        first.setRegistrarId(registrar.getId());
        urnSearchDao.insertUrnNbnSearch(first);

        UrnNbnSearch second = new UrnNbnSearch();
        second.setRegistrarId(registrar.getId());
        try {
            urnSearchDao.insertUrnNbnSearch(second);
            fail();
        } catch (AlreadyPresentException e) {
            //ok
        }
    }

    public void testInsertUrnNbnSearch_unknownRegistrar() throws Exception {
        UrnNbnSearch search = new UrnNbnSearch();
        search.setRegistrarId(ILLEGAL_ID);
        try {
            urnSearchDao.insertUrnNbnSearch(search);
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }

    /**
     * Test of getSearchByRegistrarId method, of class UrnNbnBookingDaoPostgres.
     */
    public void testGetSearchBySigla() throws Exception {
        //insert
        Registrar registrar = registrarPersisted();
        UrnNbnSearch search = new UrnNbnSearch();
        search.setRegistrarId(registrar.getId());
        urnSearchDao.insertUrnNbnSearch(search);
        //get
        UrnNbnSearch fetched = urnSearchDao.getSearchByRegistrarId(registrar.getId());
        assertEquals(search, fetched);
    }

    public void testGetSearchBySigla_unknownSigla() throws Exception {
        //get
        try {
            urnSearchDao.getSearchByRegistrarId(ILLEGAL_ID);
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }

    /**
     * Test of updateUrnNbnSearch method, of class UrnNbnBookingDaoPostgres.
     */
    public void testUpdateUrnNbnSearch() throws Exception {
        //insert
        Registrar registrar = registrarPersisted();
        UrnNbnSearch inserted = new UrnNbnSearch();
        inserted.setRegistrarId(registrar.getId());
        urnSearchDao.insertUrnNbnSearch(inserted);
        //fetch
        UrnNbnSearch fetched = urnSearchDao.getSearchByRegistrarId(registrar.getId());
        assertEquals(inserted, fetched);
        //update
        UrnNbnSearch updated = new UrnNbnSearch();
        updated.setRegistrarId(registrar.getId());
        updated.setLastFoundDocumentCode("   5");
        urnSearchDao.updateUrnNbnSearch(updated);
        //fetch
        UrnNbnSearch updatedAndFetched = urnSearchDao.getSearchByRegistrarId(registrar.getId());
        assertEquals(updatedAndFetched, updated);
        assertFalse(updatedAndFetched.equals(inserted));
    }

    public void testUpdateUrnNbnSearch_unknownBooking() throws Exception {
        Registrar registrar = registrarPersisted();
        UrnNbnSearch search = new UrnNbnSearch();
        search.setRegistrarId(registrar.getId());
        try {
            urnSearchDao.updateUrnNbnSearch(search);
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }
}
