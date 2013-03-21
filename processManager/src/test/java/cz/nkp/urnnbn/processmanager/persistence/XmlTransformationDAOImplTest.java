/*
 * Copyright (C) 2013 Martin Řehánek
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.nkp.urnnbn.processmanager.persistence;

import cz.nkp.urnnbn.processmanager.core.XmlTransformation;
import cz.nkp.urnnbn.processmanager.core.XmlTransformationType;
import java.util.Date;
import java.util.List;
import junit.framework.TestCase;
import org.hibernate.PropertyValueException;

/**
 *
 * @author Martin Řehánek
 */
public class XmlTransformationDAOImplTest extends TestCase {

    private XmlTransformationDAO dao;
    private LoginGenerator loginGenerator;

    public XmlTransformationDAOImplTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        dao = XmlTransformationDAOImpl.instanceOf();
        loginGenerator = new LoginGenerator();
        List<XmlTransformation> transformations = dao.getTransformations();
        for (XmlTransformation transformation : transformations) {
            dao.deleteTransformation(transformation);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private XmlTransformation buildTransformation() {
        return buildTransformation(loginGenerator.getUniqueLogin());
    }

    private XmlTransformation buildTransformation(String user) {
        XmlTransformation transformation = new XmlTransformation();
        transformation.setCreated(new Date());
        transformation.setOwnerLogin(user);
        transformation.setType(XmlTransformationType.DIGITAL_DOCUMENT_REGISTRATION);
        transformation.setXslt("TODO");
        return transformation;
    }

    /**
     * Test of saveTransformation method, of class XmlTransformationDAOImpl.
     */
    public void testSaveTransformation() {
        XmlTransformation saved = dao.saveTransformation(buildTransformation());
        assertNotNull(saved.getId());
    }

    public void testSaveTransformationEmptyLogin() {
        XmlTransformation saved = dao.saveTransformation(buildTransformation());
        saved.setOwnerLogin(null);
        try {
            dao.saveTransformation(saved);
            fail();
        } catch (PropertyValueException e) {
            assertEquals(XmlTransformation.class.getName(), e.getEntityName());
            assertEquals("ownerLogin", e.getPropertyName());
        }
    }

    public void testSaveTransformationEmptyXslt() {
        XmlTransformation saved = dao.saveTransformation(buildTransformation());
        saved.setXslt(null);
        try {
            dao.saveTransformation(saved);
            fail();
        } catch (PropertyValueException e) {
            assertEquals(XmlTransformation.class.getName(), e.getEntityName());
            assertEquals("xslt", e.getPropertyName());
        }
    }

    public void testGetTransformation() {
        XmlTransformation saved = dao.saveTransformation(buildTransformation());
        try {
            XmlTransformation fetched = dao.getTransformation(saved.getId());
            assertNotNull(fetched);
            assertEquals(saved, fetched);
        } catch (UnknownRecordException ex) {
            fail();
        }
    }

    public void testGetDeletedTransformation() {
        //insert
        XmlTransformation saved = dao.saveTransformation(buildTransformation());
        //delete
        try {
            dao.deleteTransformation(saved);
        } catch (UnknownRecordException ex) {
            throw new RuntimeException(ex);
        }
        //get deleted
        try {
            dao.getTransformation(saved.getId());
            fail();
        } catch (UnknownRecordException ex) {
            //ok
        }
    }

    public void testGetUnknownTransformations() {
        try {
            dao.getTransformation(Long.MAX_VALUE);
            fail();
        } catch (UnknownRecordException ex) {
            //ok
        }
    }

    public void testGetTransformations() {
        //insert
        XmlTransformation first = dao.saveTransformation(buildTransformation());
        XmlTransformation second = dao.saveTransformation(buildTransformation());
        XmlTransformation third = dao.saveTransformation(buildTransformation());
        //fetch
        List<XmlTransformation> allTransformations = dao.getTransformations();
        assertEquals(3, allTransformations.size());
        assertTrue(allTransformations.contains(first));
        assertTrue(allTransformations.contains(second));
        assertTrue(allTransformations.contains(third));
    }

    public void testGetTransformationsOfUser() {
        String user = loginGenerator.getUniqueLogin();
        //insert
        XmlTransformation first = buildTransformation(user);
        dao.saveTransformation(first);
        XmlTransformation second = buildTransformation(user);
        dao.saveTransformation(second);
        XmlTransformation ofOtherUser = dao.saveTransformation(buildTransformation());
        //fetch
        List<XmlTransformation> transformationsOfUser = dao.getTransformationsOfUser(user);
        assertEquals(2, transformationsOfUser.size());
        assertTrue(transformationsOfUser.contains(first));
        assertTrue(transformationsOfUser.contains(second));
        assertFalse(transformationsOfUser.contains(ofOtherUser));
    }

    public void testDeleteTransformation() {
        //insert
        XmlTransformation transformation = dao.saveTransformation(buildTransformation());
        //delete
        try {
            dao.deleteTransformation(transformation);
        } catch (UnknownRecordException ex) {
            throw new RuntimeException(ex);
        }
        //fetch
        try {
            dao.getTransformation(transformation.getId());
            fail();
        } catch (UnknownRecordException ex) {
            //ok
        }
    }

    public void testDeleteDeletedTransformation() {
        //insert
        XmlTransformation transformations = dao.saveTransformation(buildTransformation());
        //delete
        try {
            dao.deleteTransformation(transformations);
        } catch (UnknownRecordException ex) {
            throw new RuntimeException(ex);
        }
        //delete again
        try {
            dao.deleteTransformation(transformations);
            fail();
        } catch (UnknownRecordException ex) {
            //ok
        }
    }
}
