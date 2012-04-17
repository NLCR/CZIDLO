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
public class UrnNbnGeneratorDaoPostgresTest extends AbstractDaoTest {

    public UrnNbnGeneratorDaoPostgresTest(String testName) {
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
    public void testInsertUrnNbnGenerator() throws Exception {
        Registrar registrar = registrarPersisted();
        UrnNbnGenerator generator = new UrnNbnGenerator();
        generator.setRegistrarId(registrar.getId());
        urnGeneratorDao.insertGenerator(generator);
    }

    public void testInsertUrnNbnGenerator_alreadyPresent() throws Exception {
        Registrar registrar = registrarPersisted();
        UrnNbnGenerator first = new UrnNbnGenerator();
        first.setRegistrarId(registrar.getId());
        urnGeneratorDao.insertGenerator(first);

        UrnNbnGenerator second = new UrnNbnGenerator();
        second.setRegistrarId(registrar.getId());
        try {
            urnGeneratorDao.insertGenerator(second);
            fail();
        } catch (AlreadyPresentException e) {
            //ok
        }
    }

    public void testInsertUrnNbnGenerator_unknownRegistrar() throws Exception {
        UrnNbnGenerator generator = new UrnNbnGenerator();
        generator.setRegistrarId(ILLEGAL_ID);
        try {
            urnGeneratorDao.insertGenerator(generator);
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }

    /**
     * Test of getGeneratorByRegistrarId method, of class UrnNbnBookingDaoPostgres.
     */
    public void testGetGeneratorByCode() throws Exception {
        //insert
        Registrar registrar = registrarPersisted();
        UrnNbnGenerator generator = new UrnNbnGenerator();
        generator.setRegistrarId(registrar.getId());
        urnGeneratorDao.insertGenerator(generator);
        //get
        UrnNbnGenerator fetched = urnGeneratorDao.getGeneratorByRegistrarId(registrar.getId());
        assertNotNull(fetched);
        assertNotNull(fetched.getRegistrarId());
        assertNotNull(fetched.getLastDocumentCode());
        assertEquals(generator, fetched);
    }

    public void testGetGeneratorByCode_unknownCode() throws Exception {
        //get
        try {
            urnGeneratorDao.getGeneratorByRegistrarId(ILLEGAL_ID);
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }

    /**
     * Test of updateGenerator method, of class UrnNbnBookingDaoPostgres.
     */
    public void testUpdateUrnNbnGenerator() throws Exception {
        //insert
        Registrar registrar = registrarPersisted();
        UrnNbnGenerator inserted = new UrnNbnGenerator();
        inserted.setRegistrarId(registrar.getId());
        urnGeneratorDao.insertGenerator(inserted);
        //fetch
        UrnNbnGenerator fetched = urnGeneratorDao.getGeneratorByRegistrarId(registrar.getId());
        assertEquals(inserted, fetched);
        //update
        UrnNbnGenerator updated = new UrnNbnGenerator();
        updated.setRegistrarId(registrar.getId());
        updated.setLastDocumentCode("   5");
        urnGeneratorDao.updateGenerator(updated);
        //fetch
        UrnNbnGenerator updatedAndFetched = urnGeneratorDao.getGeneratorByRegistrarId(registrar.getId());
        assertEquals(updatedAndFetched, updated);
        assertFalse(updatedAndFetched.equals(inserted));
    }

    public void testUpdateUrnNbnGenerator_unknownBooking() throws Exception {
        Registrar registrar = registrarPersisted();
        UrnNbnGenerator generator = new UrnNbnGenerator();
        generator.setRegistrarId(registrar.getId());
        try {
            urnGeneratorDao.updateGenerator(generator);
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }
}
