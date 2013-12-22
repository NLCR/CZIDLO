/*F
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.impl;

import cz.nkp.urnnbn.core.AdminLogger;
import cz.nkp.urnnbn.core.RegistrarScopeIdType;
import cz.nkp.urnnbn.core.dto.Archiver;
import cz.nkp.urnnbn.core.dto.Catalog;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.DigitalDocumentDAO;
import cz.nkp.urnnbn.core.persistence.RegistrarDAO;
import cz.nkp.urnnbn.core.persistence.RegistrarScopeIdentifierDAO;
import cz.nkp.urnnbn.core.persistence.UserDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordReferencedException;
import cz.nkp.urnnbn.services.DataRemoveService;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.CannotBeRemovedException;
import cz.nkp.urnnbn.services.exceptions.NotAdminException;
import cz.nkp.urnnbn.services.exceptions.RegistrarScopeIdentifierNotDefinedException;
import cz.nkp.urnnbn.services.exceptions.UnknownArchiverException;
import cz.nkp.urnnbn.services.exceptions.UnknownCatalogException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigDocException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigInstException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigLibException;
import cz.nkp.urnnbn.services.exceptions.UnknownRegistrarException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;

/**
 * 
 * @author Martin Řehánek
 */
public class DataRemoveServiceImpl extends BusinessServiceImpl implements DataRemoveService {

	public DataRemoveServiceImpl(DatabaseConnector conn) {
		super(conn);
	}

	@Override
	public void removeRegistrarScopeIdentifiers(long digDocId, String login) throws UnknownUserException, AccessException,
			UnknownDigDocException {
		try {
			long registrarId = registrarOfDigDoc(digDocId);
			authorization.checkAccessRights(registrarId, login);
			factory.digDocIdDao().deleteRegistrarScopeIds(digDocId);
			factory.documentDao().updateDocumentDatestamp(digDocId);
			AdminLogger.getLogger().info(
					"user '" + login + "' deleted registrar-scope identifiers of digital document with id '" + digDocId + "'");
		} catch (RecordNotFoundException ex) {
			throw new UnknownDigDocException(digDocId);
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void removeRegistrarScopeIdentifier(long digDocId, RegistrarScopeIdType type, String login) throws UnknownUserException,
			AccessException, UnknownDigDocException, RegistrarScopeIdentifierNotDefinedException {
		try {
			long registrarId = registrarOfDigDoc(digDocId);
			authorization.checkAccessRights(registrarId, login);
			factory.digDocIdDao().deleteRegistrarScopeId(digDocId, type);
			factory.documentDao().updateDocumentDatestamp(digDocId);
			AdminLogger.getLogger().info(
					"user '" + login + "' deleted registrar-scope identifier (type=" + type + ") of digital document with id '" + digDocId
							+ "'");
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		} catch (RecordNotFoundException ex) {
			if (DigitalDocumentDAO.TABLE_NAME.equals(ex.getTableName())) {
				throw new UnknownDigDocException(digDocId);
			} else if (RegistrarScopeIdentifierDAO.TABLE_NAME.equals(ex.getTableName())) {
				throw new RegistrarScopeIdentifierNotDefinedException(type);
			} else {
				throw new RuntimeException(ex);
			}
		}
	}

	@Override
	public void removeArchiver(long archiverId, String login) throws UnknownArchiverException, CannotBeRemovedException, NotAdminException,
			UnknownUserException {
		try {
			authorization.checkAdminRights(login);
			Archiver archiver = factory.archiverDao().getArchiverById(archiverId);
			factory.archiverDao().deleteArchiver(archiverId);
			AdminLogger.getLogger().info("user '" + login + "' deleted " + archiver + "'");
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		} catch (RecordNotFoundException ex) {
			throw new UnknownArchiverException(archiverId);
		} catch (RecordReferencedException ex) {
			throw new CannotBeRemovedException(ex.getMessage());
		}
	}

	@Override
	public void removeRegistrar(long registrarId, String login) throws UnknownRegistrarException, CannotBeRemovedException,
			NotAdminException, UnknownUserException {
		try {
			authorization.checkAdminRights(login);
			Registrar registrar = factory.registrarDao().getRegistrarById(registrarId);
			factory.registrarDao().deleteRegistrar(registrarId);
			AdminLogger.getLogger().info("user '" + login + "' deleted " + registrar + "'");
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		} catch (RecordNotFoundException ex) {
			throw new UnknownRegistrarException(registrarId);
		} catch (RecordReferencedException ex) {
			throw new CannotBeRemovedException("There are digital documents registered by this registrar");
		}
	}

	@Override
	public void removeDigitalLibrary(long libraryId, String login) throws UnknownUserException, AccessException, UnknownDigLibException,
			CannotBeRemovedException {
		try {
			long registrarId = registrarOfDigLibrary(libraryId);
			authorization.checkAccessRightsOrAdmin(registrarId, login);
			DigitalLibrary library = factory.diglLibDao().getLibraryById(libraryId);
			factory.diglLibDao().deleteLibrary(libraryId);
			AdminLogger.getLogger().info("user '" + login + "' deleted " + library + "'");
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		} catch (RecordNotFoundException ex) {
			throw new UnknownDigLibException(libraryId);
		} catch (RecordReferencedException ex) {
			throw new CannotBeRemovedException(ex.getMessage());
		}
	}

	@Override
	public void removeCatalog(long catalogId, String login) throws UnknownUserException, AccessException, UnknownCatalogException {
		try {
			long registrarId = registrarOfCatalog(catalogId);
			authorization.checkAccessRightsOrAdmin(registrarId, login);
			Catalog catalog = factory.catalogDao().getCatalogById(catalogId);
			factory.catalogDao().deleteCatalog(catalogId);
			AdminLogger.getLogger().info("user '" + login + "' deleted " + catalog + "'");
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		} catch (RecordNotFoundException ex) {
			throw new UnknownCatalogException(catalogId);
		}
	}

	@Override
	public void removeUser(long userId, String login) throws UnknownUserException, NotAdminException, UnknownUserException {
		try {
			authorization.checkAdminRights(login);
			User user = factory.userDao().getUserById(userId, false);
			factory.userDao().deleteUser(userId);
			AdminLogger.getLogger().info("user '" + login + "' deleted " + user + "'");
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		} catch (RecordNotFoundException ex) {
			throw new UnknownUserException(userId);
		}
	}

	@Override
	public void removeRegistrarRight(long userId, long registrarId, String login) throws UnknownUserException, NotAdminException,
			UnknownRegistrarException {
		try {
			authorization.checkAdminRights(login);
			User user = factory.userDao().getUserById(userId, false);
			Registrar registrar = factory.registrarDao().getRegistrarById(registrarId);
			factory.userDao().deleteAdministrationRight(registrarId, userId);
			AdminLogger.getLogger().info(
					"user '" + login + "' deleted access right for registrar '" + registrar.getCode() + "' to user '" + user.getLogin()
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
		}
	}

	@Override
	public void deactivateDigitalInstance(long instanceId, String login) throws UnknownUserException, AccessException,
			UnknownDigInstException {
		try {
			long registrarId = registrarOfDigInstance(instanceId);
			authorization.checkAccessRights(registrarId, login);
			DigitalInstance digInstance = factory.digInstDao().getDigInstanceById(instanceId);
			factory.digInstDao().deactivateDigInstance(instanceId);
			AdminLogger.getLogger().info("user '" + login + "' deactivated " + digInstance + "'");
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		} catch (RecordNotFoundException ex) {
			throw new UnknownDigInstException(instanceId);
		}
	}

	@Override
	public void deactivateUrnNbn(UrnNbn urn, String login, String note) throws UnknownUserException, AccessException,
			UnknownDigDocException {
		try {
			long registrarId = registrarOfDigDoc(urn.getDigDocId());
			authorization.checkAccessRights(registrarId, login);
			factory.urnDao().deactivateUrnNbn(urn.getRegistrarCode(), urn.getDocumentCode(), note);
			if (note != null) {
				AdminLogger.getLogger().info("user '" + login + "' deactivated " + urn + "' with note: \"" + note + "\"");
			} else {
				AdminLogger.getLogger().info("user '" + login + "' deactivated " + urn + "'");
			}
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		}
	}
}
