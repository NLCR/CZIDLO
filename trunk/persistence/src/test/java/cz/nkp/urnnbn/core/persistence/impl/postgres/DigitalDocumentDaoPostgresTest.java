/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.dto.Archiver;
import cz.nkp.urnnbn.core.dto.DigDocIdentifier;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import java.util.List;
import java.util.Random;
import org.joda.time.DateTime;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalDocumentDaoPostgresTest extends AbstractDaoTest {

    public DigitalDocumentDaoPostgresTest(String testName) {
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
     * Test of insertDocument method, of class DigitalRepresentationDaoPostgres.
     */
    public void testInsertDocument_ok() throws Exception {
        IntelectualEntity entity = entityPersisted();
        Archiver archiver = archiverPersisted();
        Registrar registrar = registrarPersisted();
        DigitalDocument doc = builder.digDocWithoutIds();
        doc.setIntEntId(entity.getId());
        doc.setRegistrarId(registrar.getId());
        doc.setArchiverId(archiver.getId());
        digDocDao.insertDocument(doc);
    }

    public void testInsertDocument_ok_sameArchiverAndRegistrar() throws Exception {
        IntelectualEntity entity = entityPersisted();
        Registrar registrar = registrarPersisted();
        DigitalDocument doc = builder.digDocWithoutIds();
        doc.setIntEntId(entity.getId());
        doc.setRegistrarId(registrar.getId());
        doc.setArchiverId(registrar.getId());
        digDocDao.insertDocument(doc);
    }

    public void testInsertDocument_invalidRegistrar() throws Exception {
        IntelectualEntity entity = entityPersisted();
        Archiver archiver = archiverPersisted();
        DigitalDocument doc = builder.digDocWithoutIds();
        doc.setIntEntId(entity.getId());
        doc.setRegistrarId(ILLEGAL_ID);
        doc.setArchiverId(archiver.getId());
        try {
            digDocDao.insertDocument(doc);
            fail();
        } catch (RecordNotFoundException e) {
            //OK
        }
    }

    public void testInsertDocument_invalidArchiver() throws Exception {
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = builder.digDocWithoutIds();
        Registrar registrar = registrarPersisted();
        doc.setIntEntId(entity.getId());
        doc.setRegistrarId(registrar.getId());
        doc.setArchiverId(ILLEGAL_ID);
        try {
            digDocDao.insertDocument(doc);
            fail();
        } catch (RecordNotFoundException e) {
            //OK
        }
    }

    public void testInsertDocument_invalidEntity() throws Exception {
        DigitalDocument doc = builder.digDocWithoutIds();
        Registrar registrar = registrarPersisted();
        Archiver archiver = archiverPersisted();
        doc.setIntEntId(ILLEGAL_ID);
        doc.setArchiverId(archiver.getId());
        doc.setRegistrarId(registrar.getId());
        try {
            digDocDao.insertDocument(doc);
            fail();
        } catch (RecordNotFoundException e) {
            //OK
        }
    }

    /**
     * Test of getDocumentByDbId method, of class DigitalRepresentationDaoPostgres.
     */
    public void testGetDocumentByDbId() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument inserted = documentPersisted(registrar.getId(), entity.getId());
        DigitalDocument fetched = digDocDao.getDocumentByDbId(inserted.getId());
        assertEquals(inserted, fetched);
    }

    public void testGetDocumentByDbId_unknownId() throws Exception {
        try {
            digDocDao.getDocumentByDbId(ILLEGAL_ID);
            fail();
        } catch (RecordNotFoundException ex) {
            //ok
        }
    }

    public void testGetDigDocCountByRegistrarId() throws Exception {
        Registrar registrar = registrarPersisted();
        Random rand = new Random();
        int inserted = rand.nextInt(5);
        for (int i = 0; i < inserted; i++) {
            //save digDoc under registrar
            IntelectualEntity entity = entityPersisted();
            documentPersisted(registrar.getId(), entity.getId());
        }
        assertEquals(inserted, digDocDao.getDigDocCount(registrar.getId()).intValue());
    }

    public void testGetDigDocCountByRegistrarId_unknownRegistrarId() throws Exception {
        try {
            digDocDao.getDigDocCount(ILLEGAL_ID);
            fail();
        } catch (RecordNotFoundException ex) {
            //ok
        }
    }

    public void testGetDocumentsOfIntEntity() throws Exception {
        IntelectualEntity entity = entityPersisted();
        Registrar registrar1 = registrarPersisted();
        DigitalDocument doc1 = documentPersisted(registrar1.getId(), entity.getId());
        Registrar registrar2 = registrarPersisted();
        DigitalDocument doc2 = documentPersisted(registrar2.getId(), entity.getId());
        List<DigitalDocument> fetched = digDocDao.getDocumentsOfIntEntity(entity.getId());
        assertEquals(2, fetched.size());
        assertTrue(fetched.contains(doc1));
        assertTrue(fetched.contains(doc2));
    }

    public void testGetDocumentsOfIntEntity_unknownEntityId() throws Exception {
        try {
            digDocDao.getDocumentsOfIntEntity(ILLEGAL_ID);
            fail();
        } catch (RecordNotFoundException ex) {
            //ok
        }
    }

    public void testGetDigDocDbIdByIdentifier() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument inserted = documentPersisted(registrar.getId(), entity.getId());
        DigDocIdentifier identifier = builder.digDocIdentifierWithoutIds();
        identifier.setDigDocId(inserted.getId());
        identifier.setRegistrarId(registrar.getId());
        digRepIdDao.insertDigDocId(identifier);
        //fetch
        Long fetchedRepId = digDocDao.getDigDocDbIdByIdentifier(identifier);
        assertEquals(inserted.getId(), fetchedRepId.longValue());
        //try find with unknown value
        DigDocIdentifier id2 = builder.digDocIdentifierWithoutIds();
        id2.setRegistrarId(registrar.getId());
        id2.setValue(identifier.getValue() + "-new");
        try {
            digDocDao.getDigDocDbIdByIdentifier(id2);
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }

    /**
     * Test of updateDocument method, of class DigitalRepresentationDaoPostgres.
     */
    public void testUpdateDocument() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument inserted = documentPersisted(registrar.getId(), entity.getId());
        DigitalDocument clone = new DigitalDocument(inserted);
        clone.setColorDepth(24);
        clone.setExtent("123s.");
        clone.setFormat("djvu");
        digDocDao.updateDocument(clone);
        DigitalDocument fetched = digDocDao.getDocumentByDbId(inserted.getId());
        assertEquals(clone, fetched);
    }

    /**
     * Test of deleteDocument method, of class DigitalRepresentationDaoPostgres.
     */
    public void testDeleteDocument() throws Exception {
        //create registrar with urn
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument repInserted = documentPersisted(registrar.getId(), entity.getId());
        //UrnNbn urnInserted = new UrnNbn(registrar.getCode(), "BOA001", repInserted.getId(), new DateTime());
        UrnNbn urnInserted = new UrnNbn(registrar.getCode(), "BOA001", repInserted.getId());
        urnDao.insertUrnNbn(urnInserted);
        try {
            registrarDao.getRegistrarById(repInserted.getId());
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
        //URN not removed
        UrnNbn urnFetched = urnDao.getUrnNbnByDigRegId(repInserted.getId());
        assertEquals(urnInserted, urnFetched);
    }
}
