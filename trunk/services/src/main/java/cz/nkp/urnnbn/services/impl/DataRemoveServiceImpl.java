/*F
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.impl;

import cz.nkp.urnnbn.core.RegistrarScopeIdType;
import cz.nkp.urnnbn.core.dto.UrnNbn;
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
    public void removeDigitalDocumentIdentifiers(long digDocId, String login) throws UnknownUserException, AccessException, UnknownDigDocException {
        try {
            long registrarId = registrarOfDigDoc(digDocId);
            authorization.checkAccessRights(registrarId, login);
            factory.digDocIdDao().deleteRegistrarScopeIds(digDocId);
            factory.documentDao().updateDocumentDatestamp(digDocId);
        } catch (RecordNotFoundException ex) {
            throw new UnknownDigDocException(digDocId);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void removeDigitalDocumentId(long digDocId, RegistrarScopeIdType type, String login) throws UnknownUserException, AccessException, UnknownDigDocException, RegistrarScopeIdentifierNotDefinedException {
        try {
            long registrarId = registrarOfDigDoc(digDocId);
            authorization.checkAccessRights(registrarId, login);
            factory.digDocIdDao().deleteRegistrarScopeId(digDocId, type);
            factory.documentDao().updateDocumentDatestamp(digDocId);
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
    public void removeArchiver(long archiverId, String login) throws UnknownArchiverException, CannotBeRemovedException, NotAdminException, UnknownUserException {
        try {
            authorization.checkAdminRights(login);
            factory.archiverDao().deleteArchiver(archiverId);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            throw new UnknownArchiverException(archiverId);
        } catch (RecordReferencedException ex) {
            throw new CannotBeRemovedException(ex.getMessage());
        }
    }

    @Override
    public void removeRegistrar(long registrarId, String login) throws UnknownRegistrarException, CannotBeRemovedException, NotAdminException, UnknownUserException {
        try {
            authorization.checkAdminRights(login);
            factory.registrarDao().deleteRegistrar(registrarId);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            throw new UnknownRegistrarException(registrarId);
        } catch (RecordReferencedException ex) {
            throw new CannotBeRemovedException(ex.getMessage());
        }
    }

    @Override
    public void removeDigitalLibrary(long libraryId, String login) throws UnknownUserException, AccessException, UnknownDigLibException, CannotBeRemovedException {
        try {
            long registrarId = registrarOfDigLibrary(libraryId);
            authorization.checkAccessRightsOrAdmin(registrarId, login);
            factory.diglLibDao().deleteLibrary(libraryId);
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
            factory.catalogDao().deleteCatalog(catalogId);
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
            factory.userDao().deleteUser(userId);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            throw new UnknownUserException(userId);
        }
    }

    @Override
    public void removeRegistrarRight(long userId, long registrarId, String login) throws UnknownUserException, NotAdminException, UnknownRegistrarException {
        try {
            authorization.checkAdminRights(login);
            factory.userDao().deleteAdministrationRight(registrarId, userId);
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
    public void deactivateDigitalInstance(long instanceId, String login) throws UnknownUserException, AccessException, UnknownDigInstException {
        try {
            long registrarId = registrarOfDigInstance(instanceId);
            authorization.checkAccessRights(registrarId, login);
            factory.digInstDao().deactivateDigInstance(instanceId);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            throw new UnknownDigInstException(instanceId);
        }
    }

    @Override
    public void deactivateUrnNbn(UrnNbn urn, String login) throws UnknownUserException, AccessException, UnknownDigDocException {
        try {
            long registrarId = registrarOfDigDoc(urn.getDigDocId());
            authorization.checkAccessRights(registrarId, login);
            factory.urnDao().deactivateUrnNbn(urn.getRegistrarCode(), urn.getDocumentCode());
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        }
    }
}
