/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.utils.CryptoUtils;

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

	public void testInsertAdministrationRight() throws Exception {
		Registrar registrar = registrarPersisted();
		User user = userPersisted();
		userDao.insertAdministrationRight(registrar.getId(), user.getId());
	}

	public void testInsertAdministrationRight_unknownRegistrarId() throws Exception {
		User user = userPersisted();
		try {
			userDao.insertAdministrationRight(ILLEGAL_ID, user.getId());
			fail();
		} catch (RecordNotFoundException e) {
			// OK
		}
	}

	public void testInsertAdministrationRight_unknownUserId() throws Exception {
		Registrar registrar = registrarPersisted();
		try {
			userDao.insertAdministrationRight(registrar.getId(), ILLEGAL_ID);
			fail();
		} catch (RecordNotFoundException e) {
			// OK
		}
	}

	/**
	 * Test of getUserById method, of class UserDaoPostgres.
	 */
	public void testGetUserById() throws Exception {
		User inserted = builder.userWithoutId();
		Long insertedId = userDao.insertUser(inserted);
		User fetched = userDao.getUserById(insertedId);
		assertNotNull(fetched);
		assertNotNull(fetched.getId());
		assertNotNull(fetched.getCreated());
		assertNotNull(fetched.getLogin());
		assertNotNull(fetched.getPasswordSalt());
		assertNotNull(fetched.getPasswordHash());
		assertEquals(inserted, fetched);
	}

	public void testGetUserById_illegalId() throws Exception {
		try {
			userDao.getUserById(ILLEGAL_ID);
			fail();
		} catch (RecordNotFoundException e) {
			// ok
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
		assertEquals(inserted.getEmail(), fetched.getEmail());
		assertEquals(inserted.isAdmin(), fetched.isAdmin());
		assertEquals(inserted.getLogin(), fetched.getLogin());
		assertEquals(inserted.getPasswordSalt(), fetched.getPasswordSalt());
		assertEquals(inserted.getPasswordHash(), fetched.getPasswordHash());
	}

	private void assertDeepEquals(User first, User second) {
		assertEquals(first, second);
		assertEquals(first.getCreated(), second.getCreated());
		assertEquals(first.getModified(), second.getModified());
		assertEquals(first.getEmail(), second.getEmail());
		assertEquals(first.isAdmin(), second.isAdmin());
		assertEquals(first.getLogin(), second.getLogin());
		assertEquals(first.getPasswordSalt(), second.getPasswordSalt());
		assertEquals(first.getPasswordHash(), second.getPasswordHash());
	}

	public void testGetUserByLogin_unknownLogin() throws Exception {
		try {
			userDao.getUserByLogin("someLoginThatIsNotInDatabase");
			fail();
		} catch (RecordNotFoundException e) {
			// ok
		}
	}

	public void testGetAdminsOfRegistrar() throws Exception {
		Registrar managedByTwo = registrarPersisted();
		Registrar managedByOne = registrarPersisted();
		User managesOne = userPersisted();
		User managesTwo = userPersisted();
		userDao.insertAdministrationRight(managedByTwo.getId(), managesOne.getId());
		userDao.insertAdministrationRight(managedByTwo.getId(), managesTwo.getId());
		userDao.insertAdministrationRight(managedByOne.getId(), managesTwo.getId());

		// user managing single registrar
		List<Long> adminsOfManagedByOne = userDao.getAdminsOfRegistrar(managedByOne.getId());
		assertEquals(1, adminsOfManagedByOne.size());
		assertTrue(adminsOfManagedByOne.contains(managesTwo.getId()));
		// user managing two registrars
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
			// ok
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
		// insert
		User inserted = builder.userWithoutId();
		inserted.setId(userDao.insertUser(inserted));
		// update
		User updated = new User();
		updated.setId(inserted.getId());
		updated.setAdmin(!inserted.isAdmin());
		updated.setEmail(inserted.getEmail() + "-new");
		String newPassword = "new-password";
		String newSalt = CryptoUtils.generateSalt();
		updated.setPasswordSalt(newSalt);
		updated.setPasswordHash(CryptoUtils.createSha256Hash(newPassword, newSalt));

		updated.setLogin(inserted.getLogin() + "-new");
		userDao.updateUser(updated);
		// fetch
		User fetched = userDao.getUserById(updated.getId());
		assertEquals(updated.getEmail(), fetched.getEmail());
		assertEquals(updated.isAdmin(), fetched.isAdmin());
		assertEquals(inserted.getLogin(), fetched.getLogin());
		assertEquals(inserted.getPasswordSalt(), fetched.getPasswordSalt());
		assertEquals(inserted.getPasswordHash(), fetched.getPasswordHash());
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
			// OK
		}
	}

	public void testDeleteUser_notReferencedFromRegistrar() throws Exception {
		// user without registrar
		User userWithoutRegistar = userPersisted();
		assertEquals(0, registrarDao.getRegistrarsManagedByUser(userWithoutRegistar.getId()).size());
		userDao.deleteUser(userWithoutRegistar.getId());
		try {
			registrarDao.getRegistrarsManagedByUser(userWithoutRegistar.getId()).size();
			fail();
		} catch (RecordNotFoundException e) {
			// OK
		}

		// user with single registrar
		User userWithSingleRegistrar = userPersisted();
		Registrar u1Registrar = registrarPersisted();
		userDao.insertAdministrationRight(u1Registrar.getId(), userWithSingleRegistrar.getId());
		List<Registrar> registrarList = registrarDao.getRegistrarsManagedByUser(userWithSingleRegistrar.getId());
		assertEquals(1, registrarList.size());
		assertTrue(registrarList.contains(u1Registrar));
		userDao.deleteUser(userWithSingleRegistrar.getId());
		try {
			registrarDao.getRegistrarsManagedByUser(userWithSingleRegistrar.getId());
			fail();
		} catch (RecordNotFoundException e) {
			// OK
		}

		// user with two registrars
		User userWithTwoRegistrars = userPersisted();
		Registrar u2Reg1 = registrarPersisted();
		userDao.insertAdministrationRight(u2Reg1.getId(), userWithTwoRegistrars.getId());
		Registrar u2Reg2 = registrarPersisted();
		userDao.insertAdministrationRight(u2Reg2.getId(), userWithTwoRegistrars.getId());
		List<Registrar> registrarList2 = registrarDao.getRegistrarsManagedByUser(userWithTwoRegistrars.getId());
		assertEquals(2, registrarList2.size());
		assertTrue(registrarList2.contains(u2Reg1));
		assertTrue(registrarList2.contains(u2Reg2));
		userDao.deleteUser(userWithTwoRegistrars.getId());
		try {
			registrarDao.getRegistrarsManagedByUser(userWithTwoRegistrars.getId());
			fail();
		} catch (RecordNotFoundException e) {
			// OK
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

	public void testDeleteAdministrationRights() throws Exception {
		Registrar registrar = registrarPersisted();
		User userOne = userPersisted();
		User userTwo = userPersisted();
		userDao.insertAdministrationRight(registrar.getId(), userOne.getId());
		userDao.insertAdministrationRight(registrar.getId(), userTwo.getId());
		// fetch
		List<Long> admins = userDao.getAdminsOfRegistrar(registrar.getId());
		assertEquals(2, admins.size());
		assertTrue(admins.contains(userOne.getId()));
		assertTrue(admins.contains(userTwo.getId()));
		// remove
		userDao.deleteAdministrationRight(registrar.getId(), userOne.getId());
		// fetch
		admins = userDao.getAdminsOfRegistrar(registrar.getId());
		assertEquals(1, admins.size());
		assertFalse(admins.contains(userOne.getId()));
		assertTrue(admins.contains(userTwo.getId()));
	}

	public void testDeleteAdministrationRights_unknownRegistrar() throws Exception {
		try {
			User userOne = userPersisted();
			userDao.deleteAdministrationRight(ILLEGAL_ID, userOne.getId());
			fail();
		} catch (RecordNotFoundException e) {
			// ok
		}
	}

	public void testDeleteAdministrationRights_unknownUser() throws Exception {
		try {
			Registrar registrar = registrarPersisted();
			userDao.deleteAdministrationRight(registrar.getId(), ILLEGAL_ID);
			fail();
		} catch (RecordNotFoundException e) {
			// ok
		}
	}

}
