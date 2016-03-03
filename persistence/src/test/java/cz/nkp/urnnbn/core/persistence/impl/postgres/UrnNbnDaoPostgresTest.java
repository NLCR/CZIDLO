/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import java.sql.Timestamp;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
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
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        UrnNbn urn = new UrnNbn(registrar.getCode(), "123456", doc.getId(), null);
        urnDao.insertUrnNbn(urn);
    }

    public void testInsertUrnNbn_unknownDigDoc() throws Exception {
        Registrar registrar = registrarPersisted();
        UrnNbn urn = new UrnNbn(registrar.getCode(), "123456", ILLEGAL_ID, null);
        try {
            urnDao.insertUrnNbn(urn);
            fail();
        } catch (RecordNotFoundException e) {
            // ok
        }
    }

    public void testInsertUrnNbn_alreadyPresent() throws Exception {
        String documentCode = "123456";
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        UrnNbn first = new UrnNbn(registrar.getCode(), documentCode, doc.getId(), null);
        urnDao.insertUrnNbn(first);
        UrnNbn second = new UrnNbn(registrar.getCode(), documentCode, doc.getId(), null);
        try {
            urnDao.insertUrnNbn(second);
            fail();
        } catch (AlreadyPresentException e) {
            IdPart[] id = (IdPart[]) e.getPresentObjectId();
            assertEquals(String.valueOf(doc.getId()), id[0].getValue());
            assertEquals(registrar.getCode().toString(), id[1].getValue());
            assertEquals(documentCode, id[2].getValue());
        }
    }

    public void testInsertUrnNbnPrecessor() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        UrnNbn predecessor = new UrnNbn(registrar.getCode(), "123456", documentPersisted(registrar.getId(), entity.getId()).getId(), null);
        UrnNbn successor = new UrnNbn(registrar.getCode(), "123457", documentPersisted(registrar.getId(), entity.getId()).getId(), null);
        try {
            urnDao.insertUrnNbnPredecessor(predecessor, successor, "test");
            fail();
        } catch (RecordNotFoundException e) {
            // ok
        }

        urnDao.insertUrnNbn(predecessor);
        try {
            urnDao.insertUrnNbnPredecessor(predecessor, successor, "test");
            fail();
        } catch (RecordNotFoundException e) {
            // ok
        }

        urnDao.insertUrnNbn(successor);
        urnDao.insertUrnNbnPredecessor(predecessor, successor, "test");
        try {
            urnDao.insertUrnNbnPredecessor(predecessor, successor, "test");
            fail();
        } catch (AlreadyPresentException e) {
            // ok
        }
    }

    /**
     * Test of getUrnNbnByDigDocId method, of class UrnNbnDaoPostgres.
     */
    public void testGetUrnNbnByDigDocId_ok() throws Exception {
        String documentCode = "123456";
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        UrnNbn inserted = new UrnNbn(registrar.getCode(), documentCode, doc.getId(), null);
        urnDao.insertUrnNbn(inserted);
        UrnNbn fetched = urnDao.getUrnNbnByDigDocId(doc.getId());
        assertNotNull(fetched);
        assertNotNull(fetched.getRegistered());
        assertNotNull(fetched.getRegistrarCode());
        assertNotNull(fetched.getDocumentCode());
        assertNotNull(fetched.getDigDocId());
        assertNotNull(fetched.getRegistered());
        assertNull(fetched.getReserved());
        assertNull(fetched.getDeactivated());
        assertEquals(inserted, fetched);
    }

    public void testGetUrnNbnByDigDocId_unknownDigDocId() throws Exception {
        try {
            urnDao.getUrnNbnByDigDocId(ILLEGAL_ID);
        } catch (RecordNotFoundException ex) {
            // ok
        }
    }

    /**
     * Test of getUrnNbnByRegistrarCodeAndDocumentCode method, of class UrnNbnDaoPostgres.
     */
    public void testGetUrnNbnByRegistrarCodeAndDocumentCode_ok() throws Exception {
        String documentCode = "123456";
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        UrnNbn inserted = new UrnNbn(registrar.getCode(), documentCode, doc.getId(), null);
        urnDao.insertUrnNbn(inserted);
        UrnNbn fetched = urnDao.getUrnNbnByRegistrarCodeAndDocumentCode(registrar.getCode(), documentCode);
        assertNotNull(fetched);
        assertNotNull(fetched.getRegistered());
        assertNotNull(fetched.getRegistrarCode());
        assertNotNull(fetched.getDocumentCode());
        assertNotNull(fetched.getDigDocId());
        assertNotNull(fetched.getRegistered());
        assertNull(fetched.getReserved());
        assertNull(fetched.getDeactivated());
        assertEquals(inserted, fetched);
    }

    public void testGetUrnNbnByRegistrarCodeAndDocumentCode_unknownRegistrarCode() throws Exception {
        String documentCode = "123456";
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        UrnNbn inserted = new UrnNbn(registrar.getCode(), documentCode, doc.getId(), null);
        urnDao.insertUrnNbn(inserted);
        try {
            urnDao.getUrnNbnByRegistrarCodeAndDocumentCode(RegistrarCode.valueOf("NOT99"), documentCode);
            fail();
        } catch (RecordNotFoundException ex) {
            // ok
        }
    }

    public void testGetUrnNbnPreviouslyReserved() throws Exception {
        String documentCode = "123456";
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        DateTime reserved = new DateTime();
        Timestamp reservedTs = DateTimeUtils.datetimeToTimestamp(reserved);
        UrnNbn inserted = new UrnNbn(registrar.getCode(), documentCode, doc.getId(), reserved);
        urnDao.insertUrnNbn(inserted);

        UrnNbn fetchedByRegistrarCodeDocumentCode = urnDao.getUrnNbnByRegistrarCodeAndDocumentCode(registrar.getCode(), documentCode);
        assertNotNull(fetchedByRegistrarCodeDocumentCode);
        assertNotNull(fetchedByRegistrarCodeDocumentCode.getRegistered());
        assertNotNull(fetchedByRegistrarCodeDocumentCode.getRegistrarCode());
        assertNotNull(fetchedByRegistrarCodeDocumentCode.getDocumentCode());
        assertNotNull(fetchedByRegistrarCodeDocumentCode.getDigDocId());
        assertNotNull(fetchedByRegistrarCodeDocumentCode.getReserved());
        // assertEquals(reservedTs, fetchedByRegistrarCodeDocumentCode.getReserved());
        assertNotNull(fetchedByRegistrarCodeDocumentCode.getRegistered());
        assertNull(fetchedByRegistrarCodeDocumentCode.getDeactivated());
        assertEquals(inserted, fetchedByRegistrarCodeDocumentCode);

        UrnNbn fetchedByDigDocId = urnDao.getUrnNbnByDigDocId(doc.getId());
        assertNotNull(fetchedByDigDocId);
        assertNotNull(fetchedByDigDocId.getRegistered());
        assertNotNull(fetchedByDigDocId.getRegistrarCode());
        assertNotNull(fetchedByDigDocId.getDocumentCode());
        assertNotNull(fetchedByDigDocId.getDigDocId());
        assertNotNull(fetchedByDigDocId.getReserved());
        // assertEquals(reservedTs, fetchedByDigDocId.getReserved());
        assertNotNull(fetchedByDigDocId.getRegistered());
        assertNull(fetchedByDigDocId.getDeactivated());
        assertEquals(inserted, fetchedByDigDocId);
    }

    public void testGetUrnNbnDeactivated() throws Exception {
        String documentCode = "123456";
        String deactivationNote = "fadfadfadf";
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        DateTime reserved = new DateTime(DateTimeZone.forOffsetHours(1));
        Timestamp reservedTs = DateTimeUtils.datetimeToTimestamp(reserved);
        UrnNbn inserted = new UrnNbn(registrar.getCode(), documentCode, doc.getId(), reserved);
        urnDao.insertUrnNbn(inserted);
        urnDao.deactivateUrnNbn(inserted.getRegistrarCode(), documentCode, deactivationNote);

        UrnNbn fetchedByRegistrarCodeDocumentCode = urnDao.getUrnNbnByRegistrarCodeAndDocumentCode(registrar.getCode(), documentCode);
        assertNotNull(fetchedByRegistrarCodeDocumentCode);
        assertNotNull(fetchedByRegistrarCodeDocumentCode.getRegistered());
        assertNotNull(fetchedByRegistrarCodeDocumentCode.getRegistrarCode());
        assertNotNull(fetchedByRegistrarCodeDocumentCode.getDocumentCode());
        assertNotNull(fetchedByRegistrarCodeDocumentCode.getDigDocId());
        assertNotNull(fetchedByRegistrarCodeDocumentCode.getReserved());
        // assertEquals(reservedTs, fetchedByRegistrarCodeDocumentCode.getReserved());
        assertNotNull(fetchedByRegistrarCodeDocumentCode.getRegistered());
        assertNotNull(fetchedByRegistrarCodeDocumentCode.getDeactivated());
        assertEquals(inserted, fetchedByRegistrarCodeDocumentCode);
        assertEquals(deactivationNote, fetchedByRegistrarCodeDocumentCode.getDeactivationNote());

        UrnNbn fetchedByDigDocId = urnDao.getUrnNbnByDigDocId(doc.getId());
        assertNotNull(fetchedByDigDocId);
        assertNotNull(fetchedByDigDocId.getRegistered());
        assertNotNull(fetchedByDigDocId.getRegistrarCode());
        assertNotNull(fetchedByDigDocId.getDocumentCode());
        assertNotNull(fetchedByDigDocId.getDigDocId());
        assertNotNull(fetchedByDigDocId.getReserved());
        // assertEquals(reservedTs, fetchedByDigDocId.getReserved());
        assertNotNull(fetchedByDigDocId.getRegistered());
        assertNotNull(fetchedByDigDocId.getDeactivated());
        assertEquals(inserted, fetchedByDigDocId);
        assertEquals(deactivationNote, fetchedByDigDocId.getDeactivationNote());
    }

    public void testGetUrnNbnByRegistrarCodeAndDocumentCode_unknownDocumentCode() throws Exception {
        String documentCode = "123456";
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        UrnNbn inserted = new UrnNbn(registrar.getCode(), documentCode, doc.getId(), null);
        urnDao.insertUrnNbn(inserted);
        try {
            urnDao.getUrnNbnByRegistrarCodeAndDocumentCode(registrar.getCode(), "NOT_USED");
            fail();
        } catch (RecordNotFoundException ex) {
            // ok
        }
    }

    public void testGetUrnNbnPredecessor() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();

        UrnNbn predecessor = new UrnNbn(registrar.getCode(), "123456", documentPersisted(registrar.getId(), entity.getId()).getId(), null);
        urnDao.insertUrnNbn(predecessor);

        UrnNbn successor = new UrnNbn(registrar.getCode(), "123457", documentPersisted(registrar.getId(), entity.getId()).getId(), null);
        urnDao.insertUrnNbn(successor);

        assertTrue(urnDao.getPredecessors(predecessor).isEmpty());
        assertTrue(urnDao.getPredecessors(successor).isEmpty());
        urnDao.insertUrnNbnPredecessor(predecessor, successor, "test");
        assertEquals(0, urnDao.getPredecessors(predecessor).size());
        assertEquals(1, urnDao.getPredecessors(successor).size());
        UrnNbnWithStatus predecessorFetched = urnDao.getPredecessors(successor).get(0);
        assertEquals(predecessor, predecessorFetched.getUrn());
        assertEquals("test", predecessorFetched.getNote());
    }

    public void testGetUrnNbnPredecessorWithEmptyNote() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();

        UrnNbn predecessor = new UrnNbn(registrar.getCode(), "123456", documentPersisted(registrar.getId(), entity.getId()).getId(), null);
        urnDao.insertUrnNbn(predecessor);

        UrnNbn successor = new UrnNbn(registrar.getCode(), "123457", documentPersisted(registrar.getId(), entity.getId()).getId(), null);
        urnDao.insertUrnNbn(successor);

        assertTrue(urnDao.getPredecessors(predecessor).isEmpty());
        assertTrue(urnDao.getPredecessors(successor).isEmpty());
        urnDao.insertUrnNbnPredecessor(predecessor, successor, null);
        assertEquals(0, urnDao.getPredecessors(predecessor).size());
        assertEquals(1, urnDao.getPredecessors(successor).size());

        UrnNbnWithStatus predecessorFetched = urnDao.getPredecessors(successor).get(0);
        assertEquals(predecessor, predecessorFetched.getUrn());
        assertNull(predecessorFetched.getNote());
    }

    public void testGetUrnNbnSuccessor() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();

        UrnNbn predecessor = new UrnNbn(registrar.getCode(), "123456", documentPersisted(registrar.getId(), entity.getId()).getId(), null);
        urnDao.insertUrnNbn(predecessor);

        UrnNbn successor = new UrnNbn(registrar.getCode(), "123457", documentPersisted(registrar.getId(), entity.getId()).getId(), null);
        urnDao.insertUrnNbn(successor);

        urnDao.insertUrnNbnPredecessor(predecessor, successor, "test");
        assertEquals(0, urnDao.getSuccessors(successor).size());
        assertEquals(1, urnDao.getSuccessors(predecessor).size());
        UrnNbnWithStatus successorFetched = urnDao.getSuccessors(predecessor).get(0);
        assertEquals(successor, successorFetched.getUrn());
        assertEquals("test", successorFetched.getNote());
    }

    public void testGetUrnNbnSuccessorWithEmptyNote() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();

        UrnNbn predecessor = new UrnNbn(registrar.getCode(), "123456", documentPersisted(registrar.getId(), entity.getId()).getId(), null);
        urnDao.insertUrnNbn(predecessor);

        UrnNbn successor = new UrnNbn(registrar.getCode(), "123457", documentPersisted(registrar.getId(), entity.getId()).getId(), null);
        urnDao.insertUrnNbn(successor);

        urnDao.insertUrnNbnPredecessor(predecessor, successor, null);
        assertEquals(0, urnDao.getSuccessors(successor).size());
        assertEquals(1, urnDao.getSuccessors(predecessor).size());

        UrnNbnWithStatus successorFetched = urnDao.getSuccessors(predecessor).get(0);
        assertEquals(successor, successorFetched.getUrn());
        assertNull(successorFetched.getNote());
    }

    public void testGetUrnNbnPredecessors() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();

        UrnNbn urn = new UrnNbn(registrar.getCode(), "12345x", documentPersisted(registrar.getId(), entity.getId()).getId(), null);
        urnDao.insertUrnNbn(urn);
        for (int i = 1; i < 5; i++) {
            UrnNbn otherUrn = new UrnNbn(registrar.getCode(), "12345" + i, documentPersisted(registrar.getId(), entity.getId()).getId(), null);
            urnDao.insertUrnNbn(otherUrn);
            urnDao.insertUrnNbnPredecessor(urn, otherUrn, "test");
            assertEquals(1, urnDao.getPredecessors(otherUrn).size());
            assertEquals(0, urnDao.getSuccessors(otherUrn).size());
            assertEquals(i, urnDao.getSuccessors(urn).size());
            assertEquals(0, urnDao.getPredecessors(urn).size());
        }
    }

    public void testIsPredecessor() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        UrnNbn predecessor = new UrnNbn(registrar.getCode(), "123456", documentPersisted(registrar.getId(), entity.getId()).getId(), null);
        urnDao.insertUrnNbn(predecessor);

        UrnNbn successor = new UrnNbn(registrar.getCode(), "123457", documentPersisted(registrar.getId(), entity.getId()).getId(), null);
        urnDao.insertUrnNbn(successor);

        UrnNbn other = new UrnNbn(registrar.getCode(), "123458", documentPersisted(registrar.getId(), entity.getId()).getId(), null);
        urnDao.insertUrnNbn(other);

        urnDao.insertUrnNbnPredecessor(predecessor, successor, "test");
        assertTrue(urnDao.isPredecessesor(predecessor, successor));
        assertFalse(urnDao.isPredecessesor(successor, predecessor));
        assertFalse(urnDao.isPredecessesor(other, predecessor));
        assertFalse(urnDao.isPredecessesor(other, successor));
        assertFalse(urnDao.isPredecessesor(predecessor, other));
        assertFalse(urnDao.isPredecessesor(successor, other));
    }

    public void testDeactivateUrnNbn() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        UrnNbn inserted = new UrnNbn(registrar.getCode(), "123456", doc.getId(), null);
        urnDao.insertUrnNbn(inserted);
        UrnNbn fetched = urnDao.getUrnNbnByRegistrarCodeAndDocumentCode(inserted.getRegistrarCode(), inserted.getDocumentCode());
        assertTrue(fetched.isActive());
        // deactivation
        String deactivationNote = "fasdfasdfasdfa";
        urnDao.deactivateUrnNbn(inserted.getRegistrarCode(), inserted.getDocumentCode(), deactivationNote);
        UrnNbn deactivated = urnDao.getUrnNbnByRegistrarCodeAndDocumentCode(inserted.getRegistrarCode(), inserted.getDocumentCode());
        assertFalse(deactivated.isActive());
        // another deactivation
        UrnNbn deactivatedAgain = urnDao.getUrnNbnByRegistrarCodeAndDocumentCode(inserted.getRegistrarCode(), inserted.getDocumentCode());
        assertFalse(deactivatedAgain.isActive());
        assertEquals(deactivationNote, deactivatedAgain.getDeactivationNote());
    }

    public void testReactivateUrnNbn() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        UrnNbn inserted = new UrnNbn(registrar.getCode(), "123456", documentPersisted(registrar.getId(), entity.getId()).getId(), null);
        urnDao.insertUrnNbn(inserted);
        UrnNbn fetched = urnDao.getUrnNbnByRegistrarCodeAndDocumentCode(inserted.getRegistrarCode(), inserted.getDocumentCode());
        assertTrue(fetched.isActive());
        // deactivation
        urnDao.deactivateUrnNbn(inserted.getRegistrarCode(), inserted.getDocumentCode(), "deactivated because of something");
        UrnNbn deactivated = urnDao.getUrnNbnByRegistrarCodeAndDocumentCode(inserted.getRegistrarCode(), inserted.getDocumentCode());
        assertFalse(deactivated.isActive());
        // reactivation
        urnDao.reactivateUrnNbn(deactivated.getRegistrarCode(), deactivated.getDocumentCode());
        UrnNbn reactivated = urnDao.getUrnNbnByRegistrarCodeAndDocumentCode(inserted.getRegistrarCode(), inserted.getDocumentCode());
        assertTrue(reactivated.isActive());
        assertNull(reactivated.getDeactivationNote());
    }

    public void testDeleteUrnNbn() throws Exception {
        String documentCode = "123456";
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        UrnNbn inserted = new UrnNbn(registrar.getCode(), documentCode, doc.getId(), null);
        urnDao.insertUrnNbn(inserted);
        urnDao.deleteUrnNbn(inserted.getRegistrarCode(), inserted.getDocumentCode());
        try {
            urnDao.getUrnNbnByDigDocId(registrar.getId());
        } catch (RecordNotFoundException ex) {
            // ok
        }
    }

    /**
     * Test of deleteAllUrnNbns method, of class UrnNbnDaoPostgres.
     */
    public void testDeleteAllUrnNbns() throws Exception {
        String documentCode = "123456";
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();
        DigitalDocument doc = documentPersisted(registrar.getId(), entity.getId());
        UrnNbn inserted = new UrnNbn(registrar.getCode(), documentCode, doc.getId(), null);
        urnDao.insertUrnNbn(inserted);
        urnDao.deleteAllUrnNbns();
        try {
            urnDao.getUrnNbnByDigDocId(registrar.getId());
        } catch (RecordNotFoundException ex) {
            // ok
        }
    }

    public void testDeleteSuccessors() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();

        UrnNbn urn = new UrnNbn(registrar.getCode(), "12345x", documentPersisted(registrar.getId(), entity.getId()).getId(), null);
        urnDao.insertUrnNbn(urn);
        for (int i = 0; i < 5; i++) {
            UrnNbn otherUrn = new UrnNbn(registrar.getCode(), "12345" + i, documentPersisted(registrar.getId(), entity.getId()).getId(), null);
            urnDao.insertUrnNbn(otherUrn);
            urnDao.insertUrnNbnPredecessor(urn, otherUrn, "test");
        }
        assertEquals(5, urnDao.getSuccessors(urn).size());
        urnDao.deleteSuccessors(urn);
        assertTrue(urnDao.getSuccessors(urn).isEmpty());
    }

    public void testDeletePredecessors() throws Exception {
        Registrar registrar = registrarPersisted();
        IntelectualEntity entity = entityPersisted();

        UrnNbn urn = new UrnNbn(registrar.getCode(), "12345x", documentPersisted(registrar.getId(), entity.getId()).getId(), null);
        urnDao.insertUrnNbn(urn);
        for (int i = 0; i < 5; i++) {
            UrnNbn otherUrn = new UrnNbn(registrar.getCode(), "12345" + i, documentPersisted(registrar.getId(), entity.getId()).getId(), null);
            urnDao.insertUrnNbn(otherUrn);
            urnDao.insertUrnNbnPredecessor(otherUrn, urn, "test");
        }
        assertEquals(5, urnDao.getPredecessors(urn).size());
        urnDao.deletePredecessors(urn);
        assertTrue(urnDao.getPredecessors(urn).isEmpty());
    }
}
