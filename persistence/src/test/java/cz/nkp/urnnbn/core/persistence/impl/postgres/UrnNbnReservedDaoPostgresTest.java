/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import java.util.List;

/**
 *
 * @author Martin Řehánek
 */
public class UrnNbnReservedDaoPostgresTest extends AbstractDaoTest {

    public UrnNbnReservedDaoPostgresTest(String testName) {
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
     * Test of insertUrnNbn method, of class UrnNbnBookedDaoPostgres.
     */
    public void testInsertUrnNbn() throws Exception {
        Registrar registrar = registrarPersisted();
        UrnNbn urn = new UrnNbn(registrar.getCode(), "000004", null);
        urnBookedDao.insertUrnNbn(urn, registrar.getId());
    }

    public void testInsertUrnNbn_alreadyExists() throws Exception {
        Registrar registrar = registrarPersisted();
        UrnNbn first = new UrnNbn(registrar.getCode(), "000004", null);
        urnBookedDao.insertUrnNbn(first, registrar.getId());
        UrnNbn second = new UrnNbn(registrar.getCode(), "000004", null);
        try {
            urnBookedDao.insertUrnNbn(second, registrar.getId());
            fail();
        } catch (AlreadyPresentException e) {
            //ok
        }
    }

    public void testInsertUrnNbn_unknownRegistrarId() throws Exception {
        UrnNbn urn = new UrnNbn(RegistrarCode.valueOf("aaa000"), "000004", null);
        try {
            urnBookedDao.insertUrnNbn(urn, ILLEGAL_ID);
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }

    /**
     * Test of getUrn method, of class UrnNbnBookedDaoPostgres.
     */
    public void testGetUrn() throws Exception {
        //insert
        Registrar registrar = registrarPersisted();
        UrnNbn inserted = new UrnNbn(registrar.getCode(), "000004", null);
        urnBookedDao.insertUrnNbn(inserted, registrar.getId());
        //fetch
        UrnNbn fetched = urnBookedDao.getUrn(inserted.getRegistrarCode(), inserted.getDocumentCode());
        assertEquals(fetched.getRegistrarCode(), inserted.getRegistrarCode());
        assertEquals(fetched.getDocumentCode(), inserted.getDocumentCode());
        assertNotNull(fetched.getCreated());
    }

    public void testGetUrn_unknownSearch() throws Exception {
        //insert
        Registrar registrar = registrarPersisted();
        UrnNbn inserted = new UrnNbn(registrar.getCode(), "000004", null);
        urnBookedDao.insertUrnNbn(inserted, registrar.getId());
        try {
            urnBookedDao.getUrn(RegistrarCode.valueOf("aaa000"), inserted.getDocumentCode());
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }

    public void testGetUrn_unknownDocumentCode() throws Exception {
        //insert
        Registrar registrar = registrarPersisted();
        UrnNbn inserted = new UrnNbn(registrar.getCode(), "000004", null);
        urnBookedDao.insertUrnNbn(inserted, registrar.getId());
        try {
            urnBookedDao.getUrn(inserted.getRegistrarCode(), "000005");
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }

    public void testGetUrnNbnList() throws Exception {
        //insert
        Registrar registrar = registrarPersisted();
        UrnNbn first = new UrnNbn(registrar.getCode(), "000004", null);
        urnBookedDao.insertUrnNbn(first, registrar.getId());
        UrnNbn second = new UrnNbn(registrar.getCode(), "000007", null);
        urnBookedDao.insertUrnNbn(second, registrar.getId());
        UrnNbn third = new UrnNbn(registrar.getCode(), "00z007", null);
        urnBookedDao.insertUrnNbn(third, registrar.getId());
        List<UrnNbn> urnNbnList = urnBookedDao.getUrnNbnList(registrar.getId());
        assertEquals(3, urnNbnList.size());
        assertTrue(urnNbnList.contains(first));
        assertTrue(urnNbnList.contains(second));
        assertTrue(urnNbnList.contains(third));
    }

    /**
     * Test of deleteUrn method, of class UrnNbnBookedDaoPostgres.
     */
    public void testDeleteUrn() throws Exception {
        //insert
        Registrar registrar = registrarPersisted();
        UrnNbn inserted = new UrnNbn(registrar.getCode(), "000004", null);
        urnBookedDao.insertUrnNbn(inserted, registrar.getId());
        //delete
        urnBookedDao.deleteUrn(inserted);
        //fetch
        try {
            urnBookedDao.getUrn(inserted.getRegistrarCode(), inserted.getDocumentCode());
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }
}
