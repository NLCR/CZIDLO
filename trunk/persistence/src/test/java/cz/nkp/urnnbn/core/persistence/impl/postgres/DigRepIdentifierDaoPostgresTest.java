/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.DigRepIdType;
import cz.nkp.urnnbn.core.dto.DigRepIdentifier;
import cz.nkp.urnnbn.core.dto.DigitalRepresentation;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.persistence.DigRepIdentifierDAO;
import cz.nkp.urnnbn.core.persistence.DigitalRepresentationDAO;
import cz.nkp.urnnbn.core.persistence.RegistrarDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import java.util.List;

/**
 *
 * @author Martin Řehánek
 */
public class DigRepIdentifierDaoPostgresTest extends AbstractDaoTest {

    public DigRepIdentifierDaoPostgresTest(String testName) {
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
     * Test of insertDigRepId method, of class DigRepIdentifierDaoPostgres.
     */
    public void testInsertDigRepId() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalRepresentation digRep = representationPersisted(registrar.getId(), entity.getId());
        //insert identifier
        DigRepIdentifier id = builder.digRepIdentifierWithoutIds();
        id.setType(DigRepIdType.valueOf("K4_pid"));
        id.setValue("uuid:123");
        id.setDigRepId(digRep.getId());
        id.setRegistrarId(registrar.getId());
        digRepIdDao.insertDigRepId(id);
        //insert another typ of identifiere
        DigRepIdentifier id2 = builder.digRepIdentifierWithoutIds();
        id2.setType(DigRepIdType.valueOf("signatura"));
        id2.setValue("nevim,neco");
        id2.setDigRepId(digRep.getId());
        id2.setRegistrarId(registrar.getId());
        digRepIdDao.insertDigRepId(id2);
    }

    public void testInsertDigRepId_emptyValue() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalRepresentation digRep = representationPersisted(registrar.getId(), entity.getId());
        DigRepIdentifier id = builder.digRepIdentifierWithoutIds();
        id.setType(DigRepIdType.valueOf("K4_pid"));
        id.setValue(null);
        id.setDigRepId(digRep.getId());
        id.setRegistrarId(registrar.getId());
        try {
            digRepIdDao.insertDigRepId(id);
            fail();
        } catch (NullPointerException e) {
            //ok
        }

    }

    public void testInsertDigRepId_unknownDigRep() throws Exception {
        Registrar registrar = registrarPersisted();
        DigRepIdentifier id = builder.digRepIdentifierWithoutIds();
        id.setDigRepId(ILLEGAL_ID);
        id.setRegistrarId(registrar.getId());
        try {
            digRepIdDao.insertDigRepId(id);
            fail();
        } catch (RecordNotFoundException e) {
            assertEquals(DigitalRepresentationDAO.TABLE_NAME, e.getTableName());
        }
    }

    public void testInsertDigRepId_unknownRegistrar() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalRepresentation digRep = representationPersisted(registrar.getId(), entity.getId());
        DigRepIdentifier id = builder.digRepIdentifierWithoutIds();
        id.setDigRepId(digRep.getId());
        id.setRegistrarId(ILLEGAL_ID);
        try {
            digRepIdDao.insertDigRepId(id);
            fail();
        } catch (RecordNotFoundException e) {
            assertEquals(RegistrarDAO.TABLE_NAME, e.getTableName());
        }
    }

    public void testInsertDigRepId_insertTwiceSameIdTypeAndValueForSameDigRep() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalRepresentation digRep = representationPersisted(registrar.getId(), entity.getId());
        DigRepIdentifier identifier = builder.digRepIdentifierWithoutIds();
        identifier.setDigRepId(digRep.getId());
        identifier.setRegistrarId(registrar.getId());
        digRepIdDao.insertDigRepId(identifier);
        try {
            digRepIdDao.insertDigRepId(identifier);
            fail();
        } catch (AlreadyPresentException e) {
            //ok
        }
    }

    public void testInsertDigRepId_insertTwiceSameIdTypeAndValueForSameRegistrar() throws Exception {
        Registrar registrar = registrarPersisted();
        //first digRep & id
        IntelectualEntity entity1 = entityPersisted();
        DigitalRepresentation digRep1 = representationPersisted(registrar.getId(), entity1.getId());
        DigRepIdentifier id1 = builder.digRepIdentifierWithoutIds();
        id1.setDigRepId(digRep1.getId());
        id1.setRegistrarId(registrar.getId());
        digRepIdDao.insertDigRepId(id1);
        //second digRep & id
        IntelectualEntity entity2 = entityPersisted();
        DigitalRepresentation digRep2 = representationPersisted(registrar.getId(), entity2.getId());
        DigRepIdentifier id2 = builder.digRepIdentifierWithoutIds();
        id2.setDigRepId(digRep2.getId());
        id2.setRegistrarId(registrar.getId());
        try {
            digRepIdDao.insertDigRepId(id2);
            fail();
        } catch (AlreadyPresentException e) {
            //ok
        }
    }

    /**
     * Test of getIdList method, of class DigRepIdentifierDaoPostgres.
     */
    public void testGetIdList() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalRepresentation digRep = representationPersisted(registrar.getId(), entity.getId());
        //first the id list should be empty
        assertTrue(digRepIdDao.getIdList(digRep.getId()).isEmpty());
        //insert id OAI
        DigRepIdentifier oaiId = new DigRepIdentifier();
        oaiId.setType(DigRepIdType.valueOf("oai"));
        oaiId.setValue("123");
        oaiId.setDigRepId(digRep.getId());
        oaiId.setRegistrarId(registrar.getId());
        digRepIdDao.insertDigRepId(oaiId);
        //insert id K4_pid
        DigRepIdentifier k4pid = new DigRepIdentifier();
        k4pid.setType(DigRepIdType.valueOf("K4_pid"));
        k4pid.setValue("uuid:3456");
        k4pid.setDigRepId(digRep.getId());
        k4pid.setRegistrarId(registrar.getId());
        digRepIdDao.insertDigRepId(k4pid);
        //get ids
        List<DigRepIdentifier> idList = digRepIdDao.getIdList(digRep.getId());
        assertEquals(2, idList.size());
        assertTrue(idList.contains(oaiId));
        assertTrue(idList.contains(k4pid));
    }

    /**
     * Test of updateDigRepIdValue method, of class DigRepIdentifierDaoPostgres.
     */
    public void testUpdateDigRepIdValue() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalRepresentation digRep = representationPersisted(registrar.getId(), entity.getId());
        //insert identifier
        DigRepIdentifier inserted = builder.digRepIdentifierWithoutIds();
        inserted.setType(DigRepIdType.valueOf("my_Id"));
        inserted.setValue("oldValue");
        inserted.setDigRepId(digRep.getId());
        inserted.setRegistrarId(registrar.getId());
        digRepIdDao.insertDigRepId(inserted);
        //update
        DigRepIdentifier updated = new DigRepIdentifier(inserted);
        updated.setValue("newValue");
        digRepIdDao.updateDigRepIdValue(updated);
        //fetch
        DigRepIdentifier fetched = digRepIdDao.getIdList(digRep.getId()).get(0);
        assertEquals(updated, fetched);
        assertFalse(inserted.equals(fetched));
    }

    public void testUpdateDigRepIdValue_unknownRegistrarOrDigRep() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalRepresentation digRep = representationPersisted(registrar.getId(), entity.getId());
        //insert identifier
        DigRepIdentifier inserted = builder.digRepIdentifierWithoutIds();
        inserted.setDigRepId(digRep.getId());
        inserted.setRegistrarId(registrar.getId());
        digRepIdDao.insertDigRepId(inserted);
        //set incorrect registrar id and update
        DigRepIdentifier updated = new DigRepIdentifier(inserted);
        updated.setRegistrarId(ILLEGAL_ID);
        try {
            digRepIdDao.updateDigRepIdValue(updated);
            fail();
        } catch (RecordNotFoundException e) {
            assertEquals(RegistrarDAO.TABLE_NAME, e.getTableName());
        }
        //set incorrect digRepId and update
        updated.setRegistrarId(registrar.getId());
        updated.setDigRepId(ILLEGAL_ID);
        try {
            digRepIdDao.updateDigRepIdValue(updated);
            fail();
        } catch (RecordNotFoundException e) {
            assertEquals(DigitalRepresentationDAO.TABLE_NAME, e.getTableName());
        }
    }

    /**
     * Test of deleteDigRepIdentifier method, of class DigRepIdentifierDaoPostgres.
     */
    public void testDeleteDigRepIdentifier() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalRepresentation digRep = representationPersisted(registrar.getId(), entity.getId());
        //insert identifier
        DigRepIdentifier inserted = builder.digRepIdentifierWithoutIds();
        inserted.setDigRepId(digRep.getId());
        inserted.setRegistrarId(registrar.getId());
        digRepIdDao.insertDigRepId(inserted);
        assertTrue(digRepIdDao.getIdList(digRep.getId()).contains(inserted));
        //delete
        digRepIdDao.deleteDigRepIdentifier(digRep.getId(), inserted.getType());
        assertFalse(digRepIdDao.getIdList(digRep.getId()).contains(inserted));
    }

    /**
     * Test of deleteAllIdentifiersOfDigRep method, of class DigRepIdentifierDaoPostgres.
     */
    public void testDeleteAllDigRepIdsOfEntity() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalRepresentation digRep = representationPersisted(registrar.getId(), entity.getId());
        //first the id list should be empty
        assertTrue(digRepIdDao.getIdList(digRep.getId()).isEmpty());
        //insert id OAI
        DigRepIdentifier idOai = new DigRepIdentifier();
        idOai.setType(DigRepIdType.valueOf("oai"));
        idOai.setValue("123");
        idOai.setDigRepId(digRep.getId());
        idOai.setRegistrarId(registrar.getId());
        digRepIdDao.insertDigRepId(idOai);
        //insert id OTHER
        DigRepIdentifier idOther = new DigRepIdentifier();
        idOther.setType(DigRepIdType.valueOf("K4_pid"));
        idOther.setValue("uuid:3456");
        idOther.setDigRepId(digRep.getId());
        idOther.setRegistrarId(registrar.getId());
        digRepIdDao.insertDigRepId(idOther);
        //get ids
        assertEquals(2, digRepIdDao.getIdList(digRep.getId()).size());
        digRepIdDao.deleteAllIdentifiersOfDigRep(digRep.getId());
        assertTrue(digRepIdDao.getIdList(digRep.getId()).isEmpty());
    }

    public void testDeleteAllDigReIds_noIds() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalRepresentation digRep = representationPersisted(registrar.getId(), entity.getId());
        digRepIdDao.deleteAllIdentifiersOfDigRep(digRep.getId());
    }

    public void testDeleteAllDigRepIdsOfEntity_unknownDigRep() throws Exception {
        try {
            digRepIdDao.deleteAllIdentifiersOfDigRep(ILLEGAL_ID);
            fail();
        } catch (RecordNotFoundException e) {
            assertEquals(DigitalRepresentationDAO.TABLE_NAME, e.getTableName());
        }
    }
}
