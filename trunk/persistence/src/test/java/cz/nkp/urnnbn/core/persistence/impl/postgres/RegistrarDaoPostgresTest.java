/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.Sigla;
import cz.nkp.urnnbn.core.dto.Archiver;
import cz.nkp.urnnbn.core.dto.Catalog;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.UrnNbnGenerator;
import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.IdPart;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
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
        String code = "clsn";
        first.setCode(code);
        registrarDao.insertRegistrar(first);
        Registrar second = builder.registrarWithoutId();
        second.setCode(code);
        try {
            registrarDao.insertRegistrar(second);
            fail();
        } catch (AlreadyPresentException ex) {
            assertEquals(code, ((IdPart) ex.getPresentObjectId()).getValue());
        }
    }

    public void testGetRegistrarBySigla() throws Exception {
        Registrar inserted = builder.registrarWithoutId();
        registrarDao.insertRegistrar(inserted);
        Sigla sigla = Sigla.valueOf(inserted.getCode());
        Registrar fetched = registrarDao.getRegistrarBySigla(sigla);
        assertEquals(fetched, inserted);
    }

    public void testGetRegistrarBySigla_unknownSigla() throws Exception {
        try {
            Sigla sigla = Sigla.valueOf("xxx123");
            registrarDao.getRegistrarBySigla(sigla);
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
        long id = registrarDao.insertRegistrar(inserted);

        Registrar returned = registrarDao.getRegistrarById(id);
        assertNotNull(returned);
        assertEquals(id, returned.getId());
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
        List<Long> managedByUser1 = registrarDao.getRegistrarsIdManagedByUser(user1.getId());
        List<Long> managedByUser2 = registrarDao.getRegistrarsIdManagedByUser(user2.getId());

        assertEquals(2, managedByUser1.size());
        assertEquals(1, managedByUser2.size());
        assertTrue(managedByUser1.contains(managedByOne.getId()));
        assertTrue(managedByUser1.contains(managedByTwo.getId()));
        assertFalse(managedByUser2.contains(managedByOne.getId()));
        assertTrue(managedByUser2.contains(managedByTwo.getId()));
    }

    public void testGetRegistrarsIdManagedByUser_IllegalId() throws Exception {
        try {
            registrarDao.getRegistrarsIdManagedByUser(ILLEGAL_ID);
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
        updated.setName("Narodni knihovna v Praze");
        registrarDao.updateRegistrar(updated);
        //get by id
        Archiver returned = registrarDao.getRegistrarById(original.getId());
        assertEquals(updated, returned);
        assertFalse(original.equals(returned));
    }

    public void testUpdateRegistrar_tryUpdatingUrnRegistrarCode() throws Exception {
        //insert first registrar
        Registrar first = builder.registrarWithoutId();
        first.setCode("boa001");
        registrarDao.insertRegistrar(first);
        //insert second registrar
        Registrar second = builder.registrarWithoutId();
        second.setCode("boa002");
        registrarDao.insertRegistrar(second);
        //update second registrar to cause collision
        second.setCode("boa001");
        registrarDao.updateRegistrar(second);
        //fetch second registrar that has been updated
        Registrar fetchedSecond = registrarDao.getRegistrarById(second.getId());
        assertEquals("boa002", fetchedSecond.getCode());
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

//    public void testActivateRegistrar() throws Exception {
//        Registrar inserted = builder.registrarWithoutId();
//        long id = registrarDao.insertRegistrar(inserted);
//        registrarDao.activateRegistrar(id);
//        Registrar fetched = registrarDao.getRegistrarById(id);
//        assertTrue(fetched.isActivated());
//        //deactivate
//        fetched.setActivated(false);
//        registrarDao.updateRegistrar(fetched);
//        Registrar fetched2 = registrarDao.getRegistrarById(id);
//        assertFalse(fetched2.isActivated());
//    }
    /**
     * Test of deleteRegistrar method, of class RegistrarDaoPostgres.
     */
    public void testDeleteRegistrar() throws Exception {
        long registrarId = registrarDao.insertRegistrar(builder.registrarWithoutId());
        User admin1 = userPersisted();
        User admin2 = userPersisted();
        registrarDao.addAdminOfRegistrar(registrarId, admin1.getId());
        registrarDao.deleteRegistrar(registrarId);
        //users no longer manage the removed registrar
        List<Long> admin1RegistrarsIds = registrarDao.getRegistrarsIdManagedByUser(admin1.getId());
        assertFalse(admin1RegistrarsIds.contains(registrarId));
        List<Long> admin2RegistrarsIds = registrarDao.getRegistrarsIdManagedByUser(admin2.getId());
        assertFalse(admin2RegistrarsIds.contains(registrarId));
    }

    public void testDeleteRegistrar_getDeleted() throws Exception {
        Registrar registrar = builder.registrarWithoutId();
        long id = registrarDao.insertRegistrar(registrar);
        //add catalog
        Catalog catalog = builder.catalogueWithoutIdAndRegistrarId();
        catalog.setRegistrarId(id);
        Long catalogId = catalogDao.insertCatalog(catalog);
        //add digital library
        DigitalLibrary lib = builder.digLibraryWithoutIdAndRegistrarId();
        lib.setRegistrarId(id);
        Long libId = libraryDao.insertLibrary(lib);
        //add urnNbnBooking
        UrnNbnGenerator search = new UrnNbnGenerator();
        search.setRegistrarId(registrar.getId());
        urnSearchDao.insertUrnNbnSearch(search);

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
            urnSearchDao.getSearchByRegistrarId(registrar.getId());
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
