/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.dto.Archiver;
import cz.nkp.urnnbn.core.dto.Catalog;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.UrnNbnGenerator;
import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.IdPart;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordReferencedException;
import java.util.List;

/**
 *
 * @author Martin Řehánek
 */
public class RegistrarDaoPostgresTest extends AbstractDaoTest {

    public RegistrarDaoPostgresTest(String testName) {
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
     * Test of insertRegistrar method, of class RegistrarDaoPostgres.
     */
    public void testInsertRegistrar() throws Exception {
        Registrar registrar = builder.registrarWithoutId();
        long id = registrarDao.insertRegistrar(registrar);
        assertTrue(id != ILLEGAL_ID);
        //check if there exists Archiver with such id
        try {
            archiverDao.getArchiverById(id);
        } catch (RecordNotFoundException e) {
            fail();
        }
    }

    public void testInsertRegistrarWithIdInDto() throws Exception {
        Registrar registrar = builder.registrarWithoutId();
        registrar.setId(ILLEGAL_ID);
        long assignedId = registrarDao.insertRegistrar(registrar);
        //id in archiver has not been used
        assertFalse(registrar.getId() == ILLEGAL_ID);
        //new id propagated to dto
        assertTrue(registrar.getId() == assignedId);

        //trying to force same id to another entity
        Registrar second = builder.registrarWithoutId();

        registrar.setId(assignedId);
        long secondAssignedId = registrarDao.insertRegistrar(second);
        assertFalse(secondAssignedId == assignedId);
    }

    public void testInsertRegistrarUrnCodeCollision() throws Exception {
        Registrar first = builder.registrarWithoutId();
        RegistrarCode code = RegistrarCode.valueOf("clsn");
        first.setCode(code);
        registrarDao.insertRegistrar(first);
        Registrar second = builder.registrarWithoutId();
        second.setCode(code);
        try {
            registrarDao.insertRegistrar(second);
            fail();
        } catch (AlreadyPresentException ex) {
            assertEquals(code.toString(), ((IdPart) ex.getPresentObjectId()).getValue());
        }
    }

    public void testGetRegistrarByCode() throws Exception {
        Registrar inserted = builder.registrarWithoutId();
        inserted.setId(registrarDao.insertRegistrar(inserted));
        Registrar fetched = registrarDao.getRegistrarByCode(inserted.getCode());
        assertNotNull(fetched);
        assertNotNull(fetched.getCreated());
        assertNotNull(fetched.getId());
        assertNotNull(fetched.getCode());
        assertEquals(fetched, inserted);
    }

    public void testGetRegistrarByCode_unknownCode() throws Exception {
        try {
            RegistrarCode code = RegistrarCode.valueOf("xxx123");
            registrarDao.getRegistrarByCode(code);
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }

    /**
     * Test of getRegistrarById method, of class RegistrarDaoPostgres.
     */
    public void testGetRegistrarById() throws Exception {
        Registrar inserted = builder.registrarWithoutId();
        inserted.setId(registrarDao.insertRegistrar(inserted));
        
        Registrar returned = registrarDao.getRegistrarById(inserted.getId());
        assertNotNull(returned);
        assertEquals(inserted.getId(), returned.getId());
    }

    public void testGetRegistrarById_IllegalId() throws Exception {
        try {
            registrarDao.getRegistrarById(ILLEGAL_ID);
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }

    public void testGetRegistrarsIdManagedByUser() throws Exception {
        Registrar managedByOne = registrarPersisted();
        Registrar managedByTwo = registrarPersisted();
        User user1 = userPersisted();
        User user2 = userPersisted();
        registrarDao.addAdminOfRegistrar(managedByOne.getId(), user1.getId());
        registrarDao.addAdminOfRegistrar(managedByTwo.getId(), user1.getId());
        registrarDao.addAdminOfRegistrar(managedByTwo.getId(), user2.getId());
        List<Registrar> managedByUser1 = registrarDao.getRegistrarsManagedByUser(user1.getId());
        List<Registrar> managedByUser2 = registrarDao.getRegistrarsManagedByUser(user2.getId());

        assertEquals(2, managedByUser1.size());
        assertEquals(1, managedByUser2.size());
        assertTrue(managedByUser1.contains(managedByOne));
        assertTrue(managedByUser1.contains(managedByTwo));
        assertFalse(managedByUser2.contains(managedByOne));
        assertTrue(managedByUser2.contains(managedByTwo));
    }

    public void testGetRegistrarsManagedByUser_IllegalId() throws Exception {
        try {
            registrarDao.getRegistrarsManagedByUser(ILLEGAL_ID);
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }

    public void testGetAllRegistrars() throws Exception {
        Registrar first = builder.registrarWithoutId();
        long id = registrarDao.insertRegistrar(first);
        first.setId(id);
        Registrar second = builder.registrarWithoutId();
        id = registrarDao.insertRegistrar(second);
        second.setId(id);
        Registrar third = builder.registrarWithoutId();
        id = registrarDao.insertRegistrar(third);
        third.setId(id);
        List<Registrar> registrars = registrarDao.getAllRegistrars();
        System.err.println("REGISTARS:" + registrars.size());
        assertTrue(registrars.contains(first));
        assertTrue(registrars.contains(second));
        assertTrue(registrars.contains(third));
        assertEquals(3, registrars.size());
        //also archivers should be created
        List<Long> archiverIdList = archiverDao.getAllArchiversId();
        assertTrue(archiverIdList.contains(first.getId()));
        assertTrue(archiverIdList.contains(second.getId()));
        assertTrue(archiverIdList.contains(third.getId()));
    }

    /**
     * Test of updateArchiver method, of class RegistrarDaoPostgres.
     */
    public void testUpdateRegistrar() throws Exception {
        Registrar original = builder.registrarWithoutId();
        registrarDao.insertRegistrar(original);
        Registrar updated = new Registrar(original);
        updated.setName("NKP");
        updated.setDescription("Narodni knihovna v Praze");
        registrarDao.updateRegistrar(updated);
        //get by id
        Archiver returned = registrarDao.getRegistrarById(original.getId());
        assertEquals(updated, returned);
        assertFalse(original.getName().equals(returned.getName()));
        assertFalse(original.getDescription().equals(returned.getDescription()));
    }

    public void testUpdateRegistrar_tryUpdatingUrnRegistrarCode() throws Exception {
        //insert first registrar
        Registrar first = builder.registrarWithoutId();
        first.setCode(RegistrarCode.valueOf("boa001"));
        registrarDao.insertRegistrar(first);
        //insert second registrar
        Registrar second = builder.registrarWithoutId();
        second.setCode(RegistrarCode.valueOf("boa002"));
        registrarDao.insertRegistrar(second);
        //update second registrar to cause collision
        second.setCode(RegistrarCode.valueOf("boa001"));
        registrarDao.updateRegistrar(second);
        //fetch second registrar that has been updated
        Registrar fetchedSecond = registrarDao.getRegistrarById(second.getId());
        assertEquals(RegistrarCode.valueOf("boa002"), fetchedSecond.getCode());
    }

    public void testUpdateRegistrar_unknownRegistrarId() throws Exception {
        Registrar registrar = builder.registrarWithoutId();
        registrar.setId(ILLEGAL_ID);
        try {
            registrarDao.updateRegistrar(registrar);
            fail();
        } catch (RecordNotFoundException e) {
            //OK
        }
    }

    public void testAddAdminOfRegistrar() throws Exception {
        Registrar registrar = registrarPersisted();
        User user = userPersisted();
        registrarDao.addAdminOfRegistrar(registrar.getId(), user.getId());
    }

    public void testAddAdminOfRegistrar_unknownRegistrarId() throws Exception {
        User user = userPersisted();
        try {
            registrarDao.addAdminOfRegistrar(ILLEGAL_ID, user.getId());
            fail();
        } catch (RecordNotFoundException e) {
            //OK
        }
    }

    public void testAddAdminOfRegistrar_unknownUserId() throws Exception {
        Registrar registrar = registrarPersisted();
        try {
            registrarDao.addAdminOfRegistrar(registrar.getId(), ILLEGAL_ID);
            fail();
        } catch (RecordNotFoundException e) {
            //OK
        }
    }

    /**
     * Test of deleteRegistrar method, of class RegistrarDaoPostgres.
     */
    public void testDeleteRegistrar() throws Exception {
        Registrar registrar = builder.registrarWithoutId();
        registrar.setId(registrarDao.insertRegistrar(registrar));
        User admin1 = userPersisted();
        User admin2 = userPersisted();
        registrarDao.addAdminOfRegistrar(registrar.getId(), admin1.getId());
        registrarDao.deleteRegistrar(registrar.getId());
        //users no longer manage the removed registrar
        List<Registrar> admin1Registrars = registrarDao.getRegistrarsManagedByUser(admin1.getId());
        assertFalse(admin1Registrars.contains(registrar));
        List<Registrar> admin2Registrars = registrarDao.getRegistrarsManagedByUser(admin2.getId());
        assertFalse(admin2Registrars.contains(registrar));
    }

    public void testDeleteRegistrar_withRegisteredDocument() throws Exception {
        IntelectualEntity intEntity = entityPersisted();
        Archiver archiver = archiverPersisted();
        long registrarId = registrarDao.insertRegistrar(builder.registrarWithoutId());
        DigitalDocument doc = documentPersisted(registrarId, archiver.getId(), intEntity.getId());
        try {
            registrarDao.deleteRegistrar(registrarId);
            fail();
        } catch (RecordReferencedException e) {
            //ok
        }
    }

    public void testDeleteRegistrar_getDeleted() throws Exception {
        Registrar registrar = builder.registrarWithoutId();
        long id = registrarDao.insertRegistrar(registrar);
        //add catalog
        Catalog catalog = builder.catalogWithoutIdAndRegistrarId();
        catalog.setRegistrarId(id);
        Long catalogId = catalogDao.insertCatalog(catalog);
        //add digital library
        DigitalLibrary lib = builder.digLibraryWithoutIdAndRegistrarId();
        lib.setRegistrarId(id);
        Long libId = libraryDao.insertLibrary(lib);
        //add urnNbnBooking
        UrnNbnGenerator search = new UrnNbnGenerator();
        search.setRegistrarId(registrar.getId());
        urnGeneratorDao.insertGenerator(search);

        //DELETE registrar
        registrarDao.deleteRegistrar(id);
        try {
            registrarDao.getRegistrarById(id);
            fail();
        } catch (RecordNotFoundException e) {
            //OK
        }
        //also archiver should be deleted
        try {
            archiverDao.getArchiverById(id);
            fail();
        } catch (RecordNotFoundException e) {
            //OK
        }
        //also catalog should be deleted
        try {
            catalogDao.getCatalogById(catalogId);
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
        //also library should be deleted
        try {
            libraryDao.getLibraries(id);
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
        //also urn:nbn booking should be deleted
        try {
            urnGeneratorDao.getGeneratorByRegistrarId(registrar.getId());
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }

    public void testDeleteAllRegistrars() throws Exception {
        //registrar with 2 admins
        Registrar registrarWithTwoAdmins = builder.registrarWithoutId();
        long id = registrarDao.insertRegistrar(registrarWithTwoAdmins);
        registrarWithTwoAdmins.setId(id);
        User admin1 = userPersisted();
        User admin2 = userPersisted();
        registrarDao.addAdminOfRegistrar(registrarWithTwoAdmins.getId(), admin1.getId());
        registrarDao.addAdminOfRegistrar(registrarWithTwoAdmins.getId(), admin2.getId());

        //registrar with single admin
        Registrar registrarWithSingleAdmin = builder.registrarWithoutId();
        id = registrarDao.insertRegistrar(registrarWithSingleAdmin);
        registrarWithSingleAdmin.setId(id);
        User admin3 = userPersisted();
        registrarDao.addAdminOfRegistrar(registrarWithSingleAdmin.getId(), admin3.getId());

        //registrar without admin
        Registrar registrarWithoutAdmin = builder.registrarWithoutId();
        id = registrarDao.insertRegistrar(registrarWithoutAdmin);
        registrarWithoutAdmin.setId(id);
        registrarDao.deleteAllRegistrars();
        List<Registrar> idList = registrarDao.getAllRegistrars();
        assertTrue(idList.isEmpty());

        //registrars should be deleted
        List<Registrar> registrars = registrarDao.getAllRegistrars();
        assertFalse(registrars.contains(registrarWithTwoAdmins));
        assertFalse(registrars.contains(registrarWithSingleAdmin));
        assertFalse(registrars.contains(registrarWithoutAdmin));

        //also given archivers should be deleted
        List<Long> archiverIdList = archiverDao.getAllArchiversId();
        assertFalse(archiverIdList.contains(registrarWithTwoAdmins.getId()));
        assertFalse(archiverIdList.contains(registrarWithSingleAdmin.getId()));
        assertFalse(archiverIdList.contains(registrarWithoutAdmin.getId()));

        //users formaly administrating the registrars should not be deleted
        List<Long> usersIds = userDao.getAllUsersId();
        assertTrue(usersIds.contains(admin1.getId()));
        assertTrue(usersIds.contains(admin2.getId()));
        assertTrue(usersIds.contains(admin3.getId()));
    }
}
