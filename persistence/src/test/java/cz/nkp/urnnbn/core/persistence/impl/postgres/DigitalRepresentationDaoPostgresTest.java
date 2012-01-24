/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.dto.Archiver;
import cz.nkp.urnnbn.core.dto.DigRepIdentifier;
import cz.nkp.urnnbn.core.dto.DigitalRepresentation;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import java.util.Random;
import org.joda.time.DateTime;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalRepresentationDaoPostgresTest extends AbstractDaoTest {
    
    public DigitalRepresentationDaoPostgresTest(String testName) {
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
     * Test of insertRepresentation method, of class DigitalRepresentationDaoPostgres.
     */
    public void testInsertRepresentation_ok() throws Exception {
        IntelectualEntity entity = entityPersisted();
        Archiver archiver = archiverPersisted();
        Registrar registrar = registrarPersisted();
        DigitalRepresentation rep = builder.digRepWithoutIds();
        rep.setIntEntId(entity.getId());
        rep.setRegistrarId(registrar.getId());
        rep.setArchiverId(archiver.getId());
        representationDao.insertRepresentation(rep);
    }
    
    public void testInsertRepresentation_ok_sameArchiverAndRegistrar() throws Exception {
        IntelectualEntity entity = entityPersisted();
        Registrar registrar = registrarPersisted();
        DigitalRepresentation rep = builder.digRepWithoutIds();
        rep.setIntEntId(entity.getId());
        rep.setRegistrarId(registrar.getId());
        rep.setArchiverId(registrar.getId());
        representationDao.insertRepresentation(rep);
    }
    
    public void testInsertRepresentation_invalidRegistrar() throws Exception {
        IntelectualEntity entity = entityPersisted();
        Archiver archiver = archiverPersisted();
        DigitalRepresentation rep = builder.digRepWithoutIds();
        rep.setIntEntId(entity.getId());
        rep.setRegistrarId(ILLEGAL_ID);
        rep.setArchiverId(archiver.getId());
        try {
            representationDao.insertRepresentation(rep);
            fail();
        } catch (RecordNotFoundException e) {
            //OK
        }
    }
    
    public void testInsertRepresentation_invalidArchiver() throws Exception {
        IntelectualEntity entity = entityPersisted();
        DigitalRepresentation rep = builder.digRepWithoutIds();
        Registrar registrar = registrarPersisted();
        rep.setIntEntId(entity.getId());
        rep.setRegistrarId(registrar.getId());
        rep.setArchiverId(ILLEGAL_ID);
        try {
            representationDao.insertRepresentation(rep);
            fail();
        } catch (RecordNotFoundException e) {
            //OK
        }
    }
    
    public void testInsertRepresentation_invalidEntity() throws Exception {
        DigitalRepresentation rep = builder.digRepWithoutIds();
        Registrar registrar = registrarPersisted();
        Archiver archiver = archiverPersisted();
        rep.setIntEntId(ILLEGAL_ID);
        rep.setArchiverId(archiver.getId());
        rep.setRegistrarId(registrar.getId());
        try {
            representationDao.insertRepresentation(rep);
            fail();
        } catch (RecordNotFoundException e) {
            //OK
        }
    }

    /**
     * Test of getRepresentationByDbId method, of class DigitalRepresentationDaoPostgres.
     */
    public void testGetRepresentationByDbId() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalRepresentation inserted = representationPersisted(registrar.getId(), entity.getId());
        DigitalRepresentation fetched = representationDao.getRepresentationByDbId(inserted.getId());
        assertEquals(inserted, fetched);
    }
    
    public void testGetRepresentationByDbId_unknownId() throws Exception {
        try {
            representationDao.getRepresentationByDbId(ILLEGAL_ID);
            fail();
        } catch (RecordNotFoundException ex) {
            //ok
        }
    }
    
    public void testGetDigRepCountByRegistrarId() throws Exception {
        Registrar registrar = registrarPersisted();
        Random rand = new Random();
        int inserted = rand.nextInt(5);
        for (int i = 0; i < inserted; i++) {
            //save digRep under registrar
            IntelectualEntity entity = entityPersisted();
            representationPersisted(registrar.getId(), entity.getId());
        }
        assertEquals(inserted, representationDao.getDigRepCount(registrar.getId()).intValue());
    }
    
    public void testGetDigRepCountByRegistrarId_unknownRegistrarId() throws Exception {
        try {
            representationDao.getDigRepCount(ILLEGAL_ID);
            fail();
        } catch (RecordNotFoundException ex) {
            //ok
        }
    }
    
    public void testGetDigRepDbIdByIdentifier() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalRepresentation inserted = representationPersisted(registrar.getId(), entity.getId());
        DigRepIdentifier identifier = builder.digRepIdentifierWithoutIds();
        identifier.setDigRepId(inserted.getId());
        identifier.setRegistrarId(registrar.getId());
        digRepIdDao.insertDigRepId(identifier);
        //fetch
        Long fetchedRepId = representationDao.getDigRepDbIdByIdentifier(identifier);
        assertEquals(inserted.getId(), fetchedRepId.longValue());
        //try find with unknown value
        DigRepIdentifier id2 = builder.digRepIdentifierWithoutIds();
        id2.setRegistrarId(registrar.getId());
        id2.setValue(identifier.getValue() + "-new");
        try {
            representationDao.getDigRepDbIdByIdentifier(id2);
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }
//    /**
//     * Test of updateRepresentation method, of class DigitalRepresentationDaoPostgres.
//     */
//    public void testUpdateRepresentation() throws Exception {
//        Registrar registrar = registrarPersisted();
//        IntelectualEntity entity = entityPersisted();
//        DigitalRepresentation inserted = representationPersisted(registrar.getId(), entity.getId());
//        DigitalRepresentation clone = new DigitalRepresentation(inserted);
//        clone.setColorDepth("24b");
//        clone.setExtent("123s.");
//        clone.setFormat("djvu");
//        clone.setAccessibility("toilet reading only");
//        representationDao.updateRepresentation(clone);
//        DigitalRepresentation fetched = representationDao.getRepresentationByDbId(inserted.getId());
//        assertEquals(clone, fetched);
//        assertFalse(fetched.equals(inserted));
//    }
//
//    /**
//     * Test of deleteRepresentation method, of class DigitalRepresentationDaoPostgres.
//     */
//    public void testDeleteRepresentation() throws Exception {
//        //create registrar with urn
//        Registrar registrar = registrarPersisted();
//        IntelectualEntity entity = entityPersisted();
//        DigitalRepresentation repInserted = representationPersisted(registrar.getId(), entity.getId());
//        UrnNbn urnInserted = new UrnNbn(registrar.getUrnInstitutionCode(), "BOA001", repInserted.getId(), new DateTime());
//        urnDao.insertUrnNbn(urnInserted);
//        try {
//            registrarDao.getRegistrarById(repInserted.getId());
//            fail();
//        } catch (RecordNotFoundException e) {
//            //ok
//        }
//        //URN not removed
//        UrnNbn urnFetched = urnDao.getUrnNbnByDigRegId(repInserted.getId());
//        assertEquals(urnInserted, urnFetched);
//    }
}
