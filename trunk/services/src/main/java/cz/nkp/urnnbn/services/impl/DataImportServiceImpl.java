/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.impl;

import cz.nkp.urnnbn.core.AdminLogger;
import cz.nkp.urnnbn.core.dto.Archiver;
import cz.nkp.urnnbn.core.dto.Catalog;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.RegistrarScopeIdentifier;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.RegistrarDAO;
import cz.nkp.urnnbn.core.persistence.RegistrarScopeIdentifierDAO;
import cz.nkp.urnnbn.core.persistence.UserDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.services.DataImportService;
import cz.nkp.urnnbn.services.DigDocRegistrationData;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.IdentifierConflictException;
import cz.nkp.urnnbn.services.exceptions.IncorrectPredecessorStatus;
import cz.nkp.urnnbn.services.exceptions.LoginConflictException;
import cz.nkp.urnnbn.services.exceptions.NotAdminException;
import cz.nkp.urnnbn.services.exceptions.RegistarScopeIdentifierCollisionException;
import cz.nkp.urnnbn.services.exceptions.RegistrarCollisionException;
import cz.nkp.urnnbn.services.exceptions.RegistrarRightCollisionException;
import cz.nkp.urnnbn.services.exceptions.RegistrationModeNotAllowedException;
import cz.nkp.urnnbn.services.exceptions.UnknownArchiverException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigDocException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigLibException;
import cz.nkp.urnnbn.services.exceptions.UnknownRegistrarException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;
import cz.nkp.urnnbn.services.exceptions.UrnNotFromRegistrarException;
import cz.nkp.urnnbn.services.exceptions.UrnUsedException;

/**
 * TODO: test
 * 
 * @author Martin Řehánek
 */
public class DataImportServiceImpl extends BusinessServiceImpl implements DataImportService {

	public DataImportServiceImpl(DatabaseConnector conn) {
		super(conn);
	}

	@Override
	public UrnNbn registerDigitalDocument(DigDocRegistrationData importData, String login) throws AccessException,
			UrnNotFromRegistrarException, UrnUsedException, UnknownRegistrarException, RegistarScopeIdentifierCollisionException,
			UnknownArchiverException, UnknownUserException, RegistrationModeNotAllowedException, IncorrectPredecessorStatus {
		authorization.checkAccessRights(importData.getRegistrarCode(), login);
		UrnNbn urnNbn = new DigitalDocumentRegistrar(factory, importData).run();
		AdminLogger.getLogger().info("user '" + login + "' registered digital document to " + urnNbn);
		return urnNbn;
	}

	@Override
	public DigitalInstance addDigitalInstance(DigitalInstance instance, String login) throws AccessException, UnknownDigLibException,
			UnknownDigDocException, UnknownUserException {
		try {
			long registrarId = registrarOfDigLibrary(instance.getLibraryId());
			authorization.checkAccessRights(registrarId, login);
			DigitalInstance digitalInstance = new DigitalInstanceAdder(factory, instance).run();
			AdminLogger.getLogger().info("user '" + login + "' imported " + digitalInstance);
			return digitalInstance;
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void addRegistrarScopeIdentifier(RegistrarScopeIdentifier id, String login) throws UnknownRegistrarException,
			UnknownDigDocException, IdentifierConflictException, AccessException, UnknownUserException {
		try {
			authorization.checkAccessRights(id.getRegistrarId(), login);
			factory.digDocIdDao().insertRegistrarScopeId(id);
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		} catch (RecordNotFoundException ex) {
			if (RegistrarScopeIdentifierDAO.TABLE_NAME.equals(ex.getTableName())) {
				throw new UnknownDigDocException(id.getDigDocId());
			} else if (RegistrarDAO.TABLE_NAME.equals(ex.getTableName())) {
				throw new UnknownRegistrarException(id.getRegistrarId());
			} else {
				throw new RuntimeException(ex);
			}
		} catch (AlreadyPresentException ex) {
			throw new IdentifierConflictException(id.getType().toString(), id.getValue());
		}
	}

	@Override
	public Archiver insertNewArchiver(Archiver archiver, String login) throws UnknownUserException, NotAdminException {
		try {
			authorization.checkAdminRights(login);
			Long id = factory.archiverDao().insertArchiver(archiver);
			archiver.setId(id);
			AdminLogger.getLogger().info("user '" + login + "' inserted " + archiver);
			return archiver;
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public Registrar insertNewRegistrar(Registrar registrar, String login) throws UnknownUserException, NotAdminException,
			RegistrarCollisionException {
		try {
			authorization.checkAdminRights(login);
			Long id = factory.registrarDao().insertRegistrar(registrar);
			registrar.setId(id);
			AdminLogger.getLogger().info("user '" + login + "' inserted " + registrar);
			return registrar;
		} catch (AlreadyPresentException ex) {
			throw new RegistrarCollisionException(registrar.getCode().toString());
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public DigitalLibrary insertNewDigitalLibrary(DigitalLibrary library, long registrarId, String login) throws UnknownUserException,
			AccessException, UnknownRegistrarException {
		try {
			authorization.checkAccessRightsOrAdmin(registrarId, login);
			Long id = factory.diglLibDao().insertLibrary(library);
			library.setId(id);
			AdminLogger.getLogger().info("user '" + login + "' inserted " + library);
			return library;
		} catch (RecordNotFoundException ex) {
			throw new UnknownRegistrarException(registrarId);
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public Catalog insertNewCatalog(Catalog catalog, long registrarId, String login) throws UnknownUserException, AccessException,
			UnknownRegistrarException {
		try {
			authorization.checkAccessRightsOrAdmin(registrarId, login);
			Long id = factory.catalogDao().insertCatalog(catalog);
			catalog.setId(id);
			AdminLogger.getLogger().info("user '" + login + "' inserted " + catalog);
			return catalog;
		} catch (RecordNotFoundException ex) {
			throw new UnknownRegistrarException(registrarId);
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public User addNewUser(User user, String login) throws UnknownUserException, NotAdminException, LoginConflictException {
		try {
			authorization.checkAdminRights(login);
			Long id = factory.userDao().insertUser(user);
			user.setId(id);
			AdminLogger.getLogger().info("user '" + login + "' created " + user);
			return user;
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		} catch (AlreadyPresentException ex) {
			throw new LoginConflictException(user.getLogin());
		}
	}

	@Override
	public void addRegistrarRight(long userId, long registrarId, String login) throws UnknownUserException, NotAdminException,
			RegistrarRightCollisionException, UnknownRegistrarException {
		try {
			authorization.checkAdminRights(login);
			factory.userDao().insertAdministrationRight(registrarId, userId);
			Registrar registrar = factory.registrarDao().getRegistrarById(registrarId);
			User user = factory.userDao().getUserById(userId);
			AdminLogger.getLogger()
					.info("user '" + login + "' added access right for registrar '" + registrar.getCode() + "' to user '" + user.getLogin()
							+ "'");
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		} catch (RecordNotFoundException ex) {
			if (ex.getTableName().equals(UserDAO.TABLE_NAME)) {
				throw new UnknownUserException(userId);
			} else if (ex.getTableName().equals(RegistrarDAO.TABLE_NAME)) {
				throw new UnknownRegistrarException(registrarId);
			} else {
				throw new RuntimeException(ex);
			}
		} catch (AlreadyPresentException ex) {
			throw new RegistrarRightCollisionException(userId, registrarId);
		}
	}
}
