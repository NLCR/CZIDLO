/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.IdPart;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;

/**
 *
 * @author Martin Řehánek
 */
public class PublicationDaoPostgresTest extends AbstractDaoTest {

    public PublicationDaoPostgresTest(String testName) {
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
     * Test of insertPublication method, of class PublicationDaoPostgres.
     */
    public void testInsertPublication() throws Exception {
        IntelectualEntity entity = entityPersisted();
        Publication publication = builder.publicationWithoutId();
        publication.setIntEntId(entity.getId());
        publicationDao.insertPublication(publication);
    }

    public void testInsertPublication_unknownEntityId() throws Exception {
        Publication publication = builder.publicationWithoutId();
        publication.setIntEntId(ILLEGAL_ID);
        try {
            publicationDao.insertPublication(publication);
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }

    public void testInsertPublication_twoPublicationsForSameIntEntity() throws Exception {
        IntelectualEntity entity = entityPersisted();
        Publication first = builder.publicationWithoutId();
        first.setIntEntId(entity.getId());
        publicationDao.insertPublication(first);

        Publication second = builder.publicationWithoutId();
        second.setIntEntId(entity.getId());
        try {
            publicationDao.insertPublication(second);
            fail();
        } catch (AlreadyPresentException ex) {
            assertEquals(String.valueOf(second.getId()), ((IdPart) ex.getPresentObjectId()).getValue());
        }
    }

    /**
     * Test of getPublicationById method, of class PublicationDaoPostgres.
     */
    public void testGetPublicationById() throws Exception {
        IntelectualEntity entity = entityPersisted();
        Publication inserted = builder.publicationWithoutId();
        inserted.setIntEntId(entity.getId());
        publicationDao.insertPublication(inserted);
        Publication fetched = publicationDao.getPublicationById(inserted.getId());
        assertNotNull(fetched);
        assertNotNull(fetched.getId());
        assertNotNull(fetched.getIntEntId());
        assertEquals(inserted, fetched);
    }

    public void testGetPublicationById_unknownId() throws Exception {
        try {
            publicationDao.getPublicationById(ILLEGAL_ID);
            fail();
        } catch (RecordNotFoundException e) {
            //OK
        }
    }

    /**
     * Test of updatePublication method, of class PublicationDaoPostgres.
     */
    public void testUpdatePublication() throws Exception {
        IntelectualEntity entity = entityPersisted();
        Publication original = builder.publicationWithoutId();
        original.setIntEntId(entity.getId());
        publicationDao.insertPublication(original);
        Publication updated = new Publication(original);
        updated.setPublisher("someoneElse");
        updated.setPlace("somewhereElse");
        updated.setYear(1984);
        publicationDao.updatePublication(updated);
        Publication fetched = publicationDao.getPublicationById(updated.getId());
        assertEquals(updated.getPublisher(), fetched.getPublisher());
        assertEquals(updated.getPlace(), fetched.getPlace());
        assertEquals(updated.getYear(), fetched.getYear());
        
        assertFalse(original.getPublisher().equals(fetched.getPublisher()));
        assertFalse(original.getPlace().equals(fetched.getPlace()));
        assertFalse(original.getYear().equals(fetched.getYear()));
    }
}
