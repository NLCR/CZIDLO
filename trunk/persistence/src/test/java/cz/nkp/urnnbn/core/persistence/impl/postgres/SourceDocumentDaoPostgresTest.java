/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.SourceDocument;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.IdPart;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;

/**
 *
 * @author Martin Řehánek
 */
public class SourceDocumentDaoPostgresTest extends AbstractDaoTest {

    public SourceDocumentDaoPostgresTest(String testName) {
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
     * Test of insertSrcDoc method, of class SourceDocumentDaoPostgres.
     */
    public void testInsertSrcDoc() throws Exception {
        IntelectualEntity entity = entityPersisted();
        SourceDocument doc = builder.sourceDocumentWithoutId();
        doc.setIntEntId(entity.getId());
        srcDocDao.insertSrcDoc(doc);
    }

    public void testInsertSrcDoc_unknownEntityId() throws Exception {
        SourceDocument doc = builder.sourceDocumentWithoutId();
        doc.setIntEntId(ILLEGAL_ID);
        try {
            srcDocDao.insertSrcDoc(doc);
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }

    public void testInsertSourceDoc_twoSrcDocsForSameEntity() throws Exception {
        IntelectualEntity entity = entityPersisted();
        SourceDocument first = builder.sourceDocumentWithoutId();
        SourceDocument second = builder.sourceDocumentWithoutId();
        first.setIntEntId(entity.getId());
        second.setIntEntId(entity.getId());
        srcDocDao.insertSrcDoc(first);
        try {
            srcDocDao.insertSrcDoc(second);
            fail();
        } catch (AlreadyPresentException ex) {
            assertEquals(String.valueOf(second.getId()), ((IdPart) ex.getPresentObjectId()).getValue());
        }
    }

    /**
     * Test of getSrcDocById method, of class SourceDocumentDaoPostgres.
     */
    public void testGetSrcDocById() throws Exception {
        IntelectualEntity entity = entityPersisted();
        SourceDocument inserted = builder.sourceDocumentWithoutId();
        inserted.setIntEntId(entity.getId());
        srcDocDao.insertSrcDoc(inserted);
        SourceDocument fetched = srcDocDao.getSrcDocById(inserted.getId());
        assertEquals(inserted, fetched);
    }

    public void testGetSrcDocById_unknownId() throws Exception {
        try {
            srcDocDao.getSrcDocById(ILLEGAL_ID);
            fail();
        } catch (RecordNotFoundException e) {
            //OK
        }
    }

    /**
     * Test of updateSrcDoc method, of class SourceDocumentDaoPostgres.
     */
    public void testUpdateSrcDoc() throws Exception {
        IntelectualEntity entity = entityPersisted();
        SourceDocument inserted = builder.sourceDocumentWithoutId();
        inserted.setIntEntId(entity.getId());
        srcDocDao.insertSrcDoc(inserted);
        SourceDocument updated = new SourceDocument(inserted);
        updated.setTitle("Something else");
        updated.setPublicationYear(2010);
        updated.setOtherId("uuid:123");
        srcDocDao.updateSrcDoc(updated);
        SourceDocument fetched = srcDocDao.getSrcDocById(inserted.getId());
        assertEquals(updated, fetched);
        assertFalse(inserted.equals(fetched));
    }

    public void testUpdateSrcDoc_invalidEntityId() throws Exception {
        IntelectualEntity entity = entityPersisted();
        SourceDocument inserted = builder.sourceDocumentWithoutId();
        inserted.setIntEntId(entity.getId());
        srcDocDao.insertSrcDoc(inserted);
        //change id
        SourceDocument updated = new SourceDocument(inserted);
        updated.setTitle("Something else");
        updated.setPublicationYear(2010);
        updated.setOtherId("uuid:123");
        updated.setIntEntId(ILLEGAL_ID);
        //update
        try {
            srcDocDao.updateSrcDoc(updated);
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }
}
