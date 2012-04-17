/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.IdPart;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;

/**
 *
 * @author Martin Řehánek
 */
public class UrnNbnDaoPostgresTest extends AbstractDaoTest {
    
    public UrnNbnDaoPostgresTest(String testName) {
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
     * Test of insertUrnNbn method, of class UrnNbnDaoPostgres.
     */
    public void testInsertUrnNbn() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument rep = documentPersisted(registrar.getId(), entity.getId());
        UrnNbn urn = new UrnNbn(registrar.getCode(), "123456", rep.getId());
        urnDao.insertUrnNbn(urn);
    }
    
    public void testInsertUrnNbn_unknownDigRep() throws Exception {
        Registrar registrar = registrarPersisted();
        UrnNbn urn = new UrnNbn(registrar.getCode(), "123456", ILLEGAL_ID);
        try {
            urnDao.insertUrnNbn(urn);
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }
    
    public void testInsertUrnNbn_alreadyPresent() throws Exception {
        String documentCode = "123456";
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument rep = documentPersisted(registrar.getId(), entity.getId());
        UrnNbn first = new UrnNbn(registrar.getCode(), documentCode, rep.getId());
        urnDao.insertUrnNbn(first);
        UrnNbn second = new UrnNbn(registrar.getCode(), documentCode, rep.getId());
        try {
            urnDao.insertUrnNbn(second);
            fail();
        } catch (AlreadyPresentException e) {
            IdPart[] id = (IdPart[]) e.getPresentObjectId();
            assertEquals(String.valueOf(rep.getId()), id[0].getValue());
            assertEquals(registrar.getCode().toString(), id[1].getValue());
            assertEquals(documentCode, id[2].getValue());
        }
    }

    /**
     * Test of getUrnNbnByDigRegId method, of class UrnNbnDaoPostgres.
     */
    public void testGetUrnNbnByDigRegId_ok() throws Exception {
        String documentCode = "123456";
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument rep = documentPersisted(registrar.getId(), entity.getId());
        UrnNbn inserted = new UrnNbn(registrar.getCode(), documentCode, rep.getId());
        urnDao.insertUrnNbn(inserted);
        UrnNbn fetched = urnDao.getUrnNbnByDigRegId(rep.getId());
        assertNotNull(fetched);
        assertNotNull(fetched.getCreated());
        assertNotNull(fetched.getRegistrarCode());
        assertNotNull(fetched.getDocumentCode());
        assertNotNull(fetched.getDigDocId());
        assertEquals(inserted, fetched);
    }
    
    public void testGetUrnNbnByDigRegId_unknownDigRepId() throws Exception {
        try {
            urnDao.getUrnNbnByDigRegId(ILLEGAL_ID);
        } catch (RecordNotFoundException ex) {
            //ok
        }
    }

    /**
     * Test of getUrnNbnByRegistrarCodeAndDocumentCode method, of class UrnNbnDaoPostgres.
     */
    public void testGetUrnNbnByRegistrarCodeAndDocumentCode_ok() throws Exception {
        String documentCode = "123456";
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument rep = documentPersisted(registrar.getId(), entity.getId());
        UrnNbn inserted = new UrnNbn(registrar.getCode(), documentCode, rep.getId());
        urnDao.insertUrnNbn(inserted);
        UrnNbn fetched = urnDao.getUrnNbnByRegistrarCodeAndDocumentCode(registrar.getCode(),documentCode);
        assertEquals(inserted, fetched);
    }
    
    public void testGetUrnNbnByRegistrarCodeAndDocumentCode_unknownRegistrarCode() throws Exception {
        String documentCode = "123456";
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument rep = documentPersisted(registrar.getId(), entity.getId());
        UrnNbn inserted = new UrnNbn(registrar.getCode(), documentCode, rep.getId());
        urnDao.insertUrnNbn(inserted);
        try {
            urnDao.getUrnNbnByRegistrarCodeAndDocumentCode(RegistrarCode.valueOf("NOT99"), documentCode);
            fail();
        } catch (RecordNotFoundException ex) {
            //ok
        }
    }
    
    public void testGetUrnNbnByRegistrarCodeAndDocumentCode_unknownDocumentCode() throws Exception {
        String documentCode = "123456";
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument rep = documentPersisted(registrar.getId(), entity.getId());
        UrnNbn inserted = new UrnNbn(registrar.getCode(), documentCode, rep.getId());
        urnDao.insertUrnNbn(inserted);
        try {
            urnDao.getUrnNbnByRegistrarCodeAndDocumentCode(registrar.getCode(), "NOT_USED");
            fail();
        } catch (RecordNotFoundException ex) {
            //ok
        }
    }
    
    public void testChangeUrnNbnStatus() throws Exception {
        
    }

    /**
     * Test of deleteAllUrnNbns method, of class UrnNbnDaoPostgres.
     */
    public void testDeleteAllUrnNbns() throws Exception {
        String documentCode = "123456";
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument rep = documentPersisted(registrar.getId(), entity.getId());
        UrnNbn inserted = new UrnNbn(registrar.getCode(), documentCode, rep.getId());
        urnDao.insertUrnNbn(inserted);
        urnDao.deleteAllUrnNbns();
        try {
            urnDao.getUrnNbnByDigRegId(registrar.getId());
        } catch (RecordNotFoundException ex) {
            //ok
        }
    }
    
    public void testDeleteUrnNbn() throws Exception {
        String documentCode = "123456";
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument rep = documentPersisted(registrar.getId(), entity.getId());
        UrnNbn inserted = new UrnNbn(registrar.getCode(), documentCode, rep.getId());
        urnDao.insertUrnNbn(inserted);
        urnDao.deleteUrnNbn(inserted);
        try {
            urnDao.getUrnNbnByDigRegId(registrar.getId());
        } catch (RecordNotFoundException ex) {
            //ok
        }
    }
}
