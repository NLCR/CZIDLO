/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import java.util.List;

/**
 *
 * @author Martin Řehánek
 */
public class UserDaoPostgresTest extends AbstractDaoTest {

    public UserDaoPostgresTest(String testName) {
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
     * Test of insertUser method, of class UserDaoPostgres.
     */
    public void testInsertUser() throws Exception {
        User user = builder.userWithoutId();
        long id = userDao.insertUser(user);
        assertTrue(id != ILLEGAL_ID);
    }

    public void testInsertUser_loginCollision() throws Exception {
        User first = builder.userWithoutId();
        Long firstId = userDao.insertUser(first);
        User second = builder.userWithoutId();
        second.setLogin(first.getLogin());
        try {
            userDao.insertUser(second);
            fail();
        } catch (AlreadyPresentException e) {
            assertEquals(firstId.longValue(), e.getPresentObjectId());
        }
    }

    /**
     * Test of getUserById method, of class UserDaoPostgres.
     */
    public void testGetUserById() throws Exception {
        User inserted = builder.userWithoutId();
        Long insertedId = userDao.insertUser(inserted);
        User fetched = userDao.getUserById(insertedId);
        assertEquals(inserted, fetched);
    }

    public void testGetUserById_illegalId() throws Exception {
        try {
            userDao.getUserById(ILLEGAL_ID);
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }

    /**
     * Test of getUserByLogin method, of class UserDaoPostgres.
     */
    public void testGetUserByLogin() throws Exception {
        User inserted = builder.userWithoutId();
        userDao.insertUser(inserted);
        User fetched = userDao.getUserByLogin(inserted.getLogin());
        assertEquals(inserted, fetched);
    }

    public void testGetUserByLogin_unknownLogin() throws Exception {
        try {
            userDao.getUserByLogin("someLoginThatIsNotInDatabase");
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }

    public void testGetAdminsOfRegistrar() throws Exception {
        Registrar managedByTwo = registrarPersisted();
        Registrar managedByOne = registrarPersisted();
        User managesOne = userPersisted();
        User managesTwo = userPersisted();
        registrarDao.addAdminOfRegistrar(managedByTwo.getId(), managesOne.getId());
        registrarDao.addAdminOfRegistrar(managedByTwo.getId(), managesTwo.getId());
        registrarDao.addAdminOfRegistrar(managedByOne.getId(), managesTwo.getId());

        //user managing single registrar
        List<Long> adminsOfManagedByOne = userDao.getAdminsOfRegistrar(managedByOne.getId());
        assertEquals(1, adminsOfManagedByOne.size());
        assertTrue(adminsOfManagedByOne.contains(managesTwo.getId()));
        //user managing two registrars
        List<Long> adminsOfManagedByTwo = userDao.getAdminsOfRegistrar(managedByTwo.getId());
        assertEquals(2, adminsOfManagedByTwo.size());
        assertTrue(adminsOfManagedByTwo.contains(managesOne.getId()));
        assertTrue(adminsOfManagedByTwo.contains(managesTwo.getId()));
    }

    public void testGetAdminsOfRegistrar_unknownId() throws Exception {
        try {
            userDao.getAdminsOfRegistrar(ILLEGAL_ID);
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }

    /**
     * Test of getAllUsersId method, of class UserDaoPostgres.
     */
    public void testGetAllUsersId() throws Exception {
        long first = userDao.insertUser(builder.userWithoutId());
        long second = userDao.insertUser(builder.userWithoutId());
        List<Long> idList = userDao.getAllUsersId();
        assertTrue(idList.contains(first));
        assertTrue(idList.contains(second));
    }

    /**
     * Test of updateUser method, of class UserDaoPostgres.
     */
    public void testUpdateUser() throws Exception {
        //TODO: implement when tested method is implemented
    }

    /**
     * Test of deleteUser method, of class UserDaoPostgres.
     */
    public void testDeleteUser() throws Exception {
        User user = builder.userWithoutId();
        Long id = userDao.insertUser(user);
        userDao.deleteUser(id);
        try {
            userDao.getUserById(id);
            fail();
        } catch (RecordNotFoundException e) {
            //OK
        }
    }

    public void testDeleteUser_notReferencedFromRegistrar() throws Exception {
        //user without registrar
        User userWithoutRegistar = userPersisted();
        assertEquals(0, registrarDao.getRegistrarsIdManagedByUser(userWithoutRegistar.getId()).size());
        userDao.deleteUser(userWithoutRegistar.getId());
        try {
            registrarDao.getRegistrarsIdManagedByUser(userWithoutRegistar.getId()).size();
            fail();
        } catch (RecordNotFoundException e) {
            //OK
        }

        //user with single registrar
        User userWithSingleRegistrar = userPersisted();
        Registrar u1Registrar = registrarPersisted();
        registrarDao.addAdminOfRegistrar(u1Registrar.getId(), userWithSingleRegistrar.getId());
        List<Long> registrarIdList = registrarDao.getRegistrarsIdManagedByUser(userWithSingleRegistrar.getId());
        assertEquals(1, registrarIdList.size());
        assertTrue(registrarIdList.contains(u1Registrar.getId()));
        userDao.deleteUser(userWithSingleRegistrar.getId());
        try {
            registrarDao.getRegistrarsIdManagedByUser(userWithSingleRegistrar.getId());
            fail();
        } catch (RecordNotFoundException e) {
            //OK
        }

        //user with two registrars
        User userWithTwoRegistrars = userPersisted();
        Registrar u2Reg1 = registrarPersisted();
        registrarDao.addAdminOfRegistrar(u2Reg1.getId(), userWithTwoRegistrars.getId());
        Registrar u2Reg2 = registrarPersisted();
        registrarDao.addAdminOfRegistrar(u2Reg2.getId(), userWithTwoRegistrars.getId());
        List<Long> registrarIdList2 = registrarDao.getRegistrarsIdManagedByUser(userWithTwoRegistrars.getId());
        assertEquals(2, registrarIdList2.size());
        assertTrue(registrarIdList2.contains(u2Reg1.getId()));
        assertTrue(registrarIdList2.contains(u2Reg2.getId()));
        userDao.deleteUser(userWithTwoRegistrars.getId());
        try {
            registrarDao.getRegistrarsIdManagedByUser(userWithTwoRegistrars.getId());
            fail();
        } catch (RecordNotFoundException e) {
            //OK
        }
    }

    /**
     * Test of deleteAllUsers method, of class UserDaoPostgres.
     */
    public void testDeleteAllUsers() throws Exception {
        long first = userDao.insertUser(builder.userWithoutId());
        long second = userDao.insertUser(builder.userWithoutId());

        userDao.deleteAllUsers();
        List<Long> idList = userDao.getAllUsersId();
        assertFalse(idList.contains(first));
        assertFalse(idList.contains(second));
    }
}