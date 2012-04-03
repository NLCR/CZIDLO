/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.dto.Archiver;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordReferencedException;
import java.util.List;

/**
 *
 * @author Martin Řehánek
 */
public class ArchiverDaoPostgresTest extends AbstractDaoTest {

    public ArchiverDaoPostgresTest(String testName) {
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
     * Test of insertArchiver method, of class ArchiverDaoPostgres.
     */
    public void testInsertArchiver() throws Exception {
        Archiver archiver = builder.archiverWithoutId();
        long id = archiverDao.insertArchiver(archiver);
        assertTrue(id != ILLEGAL_ID);
    }

    public void testInsertArchiverWithIdInDto() throws Exception {
        Archiver archiver = builder.archiverWithoutId();
        archiver.setId(ILLEGAL_ID);
        long assignedId = archiverDao.insertArchiver(archiver);
        //id in archiver has not been used
        assertFalse(archiver.getId() == ILLEGAL_ID);
        //new id propagated to dto
        assertTrue(archiver.getId() == assignedId);

        //trying to force same id to another entity
        Archiver second = builder.archiverWithoutId();
        archiver.setId(assignedId);
        long secondAssignedId = archiverDao.insertArchiver(second);
        assertFalse(secondAssignedId == assignedId);
    }

    /**
     * Test of getArchiverById method, of class ArchiverDaoPostgres.
     */
    public void testGetArchiverById() throws Exception {
        Archiver archiver = builder.archiverWithoutId();
        long id = archiverDao.insertArchiver(archiver);

        Archiver returned = archiverDao.getArchiverById(id);
        assertNotNull(returned);
        assertEquals(id, returned.getId());
    }

    public void testGetArchiverByIllegalId() throws Exception {
        try {
            archiverDao.getArchiverById(ILLEGAL_ID);
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }

    public void testGetAllArchivers() throws Exception {
        long first = archiverDao.insertArchiver(builder.archiverWithoutId());
        long second = archiverDao.insertArchiver(builder.archiverWithoutId());
        long third = archiverDao.insertArchiver(builder.archiverWithoutId());
        List<Long> idList = archiverDao.getAllArchiversId();
        assertTrue(idList.contains(first));
        assertTrue(idList.contains(second));
        assertTrue(idList.contains(third));
        assertEquals(3, idList.size());
    }

    public void testUpdateArchiver() throws Exception {
        Archiver original = builder.archiverWithoutId();
        original.setName("NKP");
        archiverDao.insertArchiver(original);
        Archiver updated = new Archiver(original);
        updated.setName("Narodni knihovna v Praze");
        archiverDao.updateArchiver(updated);
        //get by id
        Archiver returned = archiverDao.getArchiverById(original.getId());
        assertEquals(updated.getName(), returned.getName());
        assertFalse(original.getName().equals(returned.getName()));
    }

    public void testUpdateNonexistingArchiver() throws Exception {
        Archiver archiver = builder.archiverWithoutId();
        archiver.setId(ILLEGAL_ID);
        try {
            archiverDao.updateArchiver(archiver);
            fail();
        } catch (RecordNotFoundException e) {
            //OK
        }
    }

    public void testDeleteArchiver() throws Exception {
        Archiver original = builder.archiverWithoutId();
        long id = archiverDao.insertArchiver(original);
        archiverDao.deleteArchiver(id);
        try {
            archiverDao.getArchiverById(id);
            fail();
        } catch (RecordNotFoundException e) {
            //OK
        }
    }

    public void testDeleteNotexistingArchiver() throws Exception {
        try {
            archiverDao.deleteArchiver(ILLEGAL_ID);
            fail();
        } catch (RecordNotFoundException e) {
            //OK
        }
    }

    public void testDeleteArchiverArchivingDocument() throws Exception {
        //insert archiver
        Archiver archiver = builder.archiverWithoutId();
        archiver.setId(archiverDao.insertArchiver(archiver));
        //insert document archived by the archiver
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        documentPersisted(registrar.getId(), archiver.getId(), entity.getId());
        //try and delete archiver
        try {
            archiverDao.deleteArchiver(archiver.getId());
            fail();
        } catch (RecordReferencedException e) {
            //OK
        }
    }

    public void testDeleteAllArchivers() throws Exception {
        archiverDao.insertArchiver(builder.archiverWithoutId());
        archiverDao.insertArchiver(builder.archiverWithoutId());
        archiverDao.insertArchiver(builder.archiverWithoutId());
        archiverDao.deleteAllArchivers();
        List<Long> idList = archiverDao.getAllArchiversId();
        assertTrue(idList.isEmpty());
    }
}
