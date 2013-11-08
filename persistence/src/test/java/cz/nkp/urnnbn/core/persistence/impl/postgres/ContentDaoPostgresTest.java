/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.dto.Content;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;

/**
 *
 * @author xrosecky
 * @author Martin Řehánek
 */
public class ContentDaoPostgresTest extends AbstractDaoTest {

    public ContentDaoPostgresTest(String testName) {
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

    public void testInsertContent() throws Exception {
        Content inserted = new Content();
        inserted.setLanguage("cz");
        inserted.setName("info");
        inserted.setContent("hello, world!");
        contentDao.insertContent(inserted);
    }

    public void testGetContentByNameAndLanguage() throws Exception {
        //insert
        Content insertedCzInfo = new Content();
        insertedCzInfo.setLanguage("cz");
        insertedCzInfo.setName("info");
        insertedCzInfo.setContent("hello, world!");
        contentDao.insertContent(insertedCzInfo);
        //fetch
        Content fetchedCzInfo = contentDao.getContentByNameAndLanguage(insertedCzInfo.getName(), insertedCzInfo.getLanguage());
        assertNotNull(fetchedCzInfo);
        assertEquals(insertedCzInfo, fetchedCzInfo);
        assertEquals(insertedCzInfo.getId(), fetchedCzInfo.getId());
        assertEquals(insertedCzInfo.getContent(), fetchedCzInfo.getContent());
        assertEquals(insertedCzInfo.getLanguage(), fetchedCzInfo.getLanguage());
        assertEquals(insertedCzInfo.getName(), fetchedCzInfo.getName());
        //insert
        Content insertedDeSomething = new Content();
        insertedDeSomething.setLanguage("de");
        insertedDeSomething.setName("somethingElse");
        insertedDeSomething.setContent("hello, world!");
        contentDao.insertContent(insertedDeSomething);
        //fetch
        Content fetchedDeSomething = contentDao.getContentByNameAndLanguage(insertedDeSomething.getName(), insertedDeSomething.getLanguage());
        assertNotNull(fetchedDeSomething);
        assertEquals(insertedDeSomething, fetchedDeSomething);
        assertEquals(insertedDeSomething.getId(), fetchedDeSomething.getId());
        assertEquals(insertedDeSomething.getContent(), fetchedDeSomething.getContent());
        assertEquals(insertedDeSomething.getLanguage(), fetchedDeSomething.getLanguage());
        assertEquals(insertedDeSomething.getName(), fetchedDeSomething.getName());
    }

    public void testGetContentByNameAndLanguageUnknownName() throws Exception {
        //tables are empty before each test - that is ensured by AbstractDaoTest.setUp() and .tearDown()
        //insert
        Content inserted = new Content();
        inserted.setLanguage("cz");
        inserted.setName("info");
        inserted.setContent("hello, world!");
        contentDao.insertContent(inserted);
        //fetch
        try {
            contentDao.getContentByNameAndLanguage("some other name", inserted.getLanguage());
            fail();
        } catch (RecordNotFoundException e) {
            //ok   
        }
        try {
            contentDao.getContentByNameAndLanguage(null, inserted.getLanguage());
            fail();
        } catch (RecordNotFoundException e) {
            //ok   
        }

    }

    public void testGetContentByNameAndLanguageUnknownLanguage() throws Exception {
        //insert
        Content inserted = new Content();
        inserted.setLanguage("cz");
        inserted.setName("info");
        inserted.setContent("hello, world!");
        contentDao.insertContent(inserted);
        //fetch
        try {
            contentDao.getContentByNameAndLanguage(inserted.getName(), "some other language");
            fail();
        } catch (RecordNotFoundException e) {
            //ok   
        }
        try {
            contentDao.getContentByNameAndLanguage(inserted.getName(), null);
            fail();
        } catch (RecordNotFoundException e) {
            //ok   
        }
    }

    public void testUpdateContent() throws Exception {
        //insert
        Content inserted = new Content();
        inserted.setLanguage("cz");
        inserted.setName("info");
        inserted.setContent("hello, world!");
        contentDao.insertContent(inserted);
        //update
        Content updated = new Content();
        updated.setId(inserted.getId());
        updated.setLanguage("de");
        updated.setName("conditions");
        updated.setContent("so long, world!");
        //update
        contentDao.updateContent(updated);
        //fetch
        Content fetched = contentDao.getContentByNameAndLanguage(updated.getName(), updated.getLanguage());
        assertEquals(updated, fetched);
        assertEquals(updated.getId(), fetched.getId());
        assertEquals(updated.getLanguage(), fetched.getLanguage());
        assertEquals(updated.getName(), fetched.getName());
        assertEquals(updated.getContent(), fetched.getContent());
    }

    public void testUpdateNonexistingContent() throws Exception {
        //update
        Content updated = new Content();
        updated.setId(ILLEGAL_ID);
        updated.setLanguage("de");
        updated.setName("conditions");
        updated.setContent("so long, world!");
        //update
        try {
            contentDao.updateContent(updated);
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }

    public void testDeleteContent() throws Exception {
        //insert
        Content inserted = new Content();
        inserted.setLanguage("cz");
        inserted.setName("info");
        inserted.setContent("hello, world!");
        inserted.setId(contentDao.insertContent(inserted));
        //delete
        contentDao.deleteContent(inserted.getId());
        //fetch
        try {
            contentDao.getContentByNameAndLanguage(inserted.getName(), inserted.getLanguage());
            fail();
        } catch (RecordNotFoundException e) {
            //ok, already deleted
        }
        //delete again
        try {
            contentDao.deleteContent(inserted.getId());
            fail();
        } catch (RecordNotFoundException e) {
            //ok, already deleted
        }
    }

    public void testDeleteAllContent() throws Exception {
        //insert
        Content first = new Content();
        first.setLanguage("cz");
        first.setName("info");
        first.setContent("hello, world!");
        first.setId(contentDao.insertContent(first));
        Content second = new Content();
        second.setLanguage("de");
        second.setName("info");
        second.setContent("hello, world!");
        second.setId(contentDao.insertContent(second));
        contentDao.deleteAllContent();
        //fetch
        try {
            contentDao.getContentByNameAndLanguage(second.getName(), second.getLanguage());
            fail();
        } catch (RecordNotFoundException e) {
            //ok, already deleted
        }
        try {
            contentDao.getContentByNameAndLanguage(first.getName(), first.getLanguage());
            fail();
        } catch (RecordNotFoundException e) {
            //ok, already deleted
        }
    }
}
