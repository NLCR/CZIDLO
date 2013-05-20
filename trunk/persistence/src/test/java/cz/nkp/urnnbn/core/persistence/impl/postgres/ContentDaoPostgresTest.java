/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.dto.Content;

/**
 *
 * @author xrosecky
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
    
     public void testContent() throws Exception {
        Content content = new Content();
        content.setLanguage("cz");
        content.setName("info");
        content.setContent("hello, world!");
        contentDao.insertContent(content);
        Content foundContent = contentDao.getContentByNameAndLanguage(content.getName(), content.getLanguage());
        assertNotNull(foundContent);
    }
}
