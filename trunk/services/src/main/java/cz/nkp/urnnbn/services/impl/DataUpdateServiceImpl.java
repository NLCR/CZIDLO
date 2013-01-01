/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.impl;

import cz.nkp.urnnbn.core.dto.Archiver;
import cz.nkp.urnnbn.core.dto.Catalog;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.RegistrarScopeIdentifier;
import cz.nkp.urnnbn.core.dto.SourceDocument;
import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.RegistrarDAO;
import cz.nkp.urnnbn.core.persistence.RegistrarScopeIdentifierDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.services.DataUpdateService;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.IdentifierConflictException;
import cz.nkp.urnnbn.services.exceptions.NotAdminException;
import cz.nkp.urnnbn.services.exceptions.UnknownArchiverException;
import cz.nkp.urnnbn.services.exceptions.UnknownCatalogException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigDocException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigLibException;
import cz.nkp.urnnbn.services.exceptions.UnknownIntelectualEntity;
import cz.nkp.urnnbn.services.exceptions.UnknownRegistrarException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;
import java.util.Collection;

/**
 *
 * @author Martin Řehánek
 */
public class DataUpdateServiceImpl extends BusinessServiceImpl implements DataUpdateService {

    public DataUpdateServiceImpl(DatabaseConnector conn) {
        super(conn);
    }

    @Override
    public void updateDigDocIdentifier(RegistrarScopeIdentifier id) throws UnknownRegistrarException, UnknownDigDocException, IdentifierConflictException {
        try {
            factory.digDocIdDao().updateRegistrarScopeIdValue(id);
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
    public void updateDigitalDocument(DigitalDocument doc, String login) throws AccessException, UnknownUserException, UnknownDigDocException {
        try {
            authorization.checkAccessRights(doc.getRegistrarId(), login);
            factory.documentDao().updateDocument(doc);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            throw new UnknownDigDocException(doc.getId());
        }
    }

    @Override
    public void updateRegistrar(Registrar registrar, String login) throws UnknownUserException, AccessException, UnknownRegistrarException {
        try {
            authorization.checkAccessRightsOrAdmin(registrar.getId(), login);
            factory.registrarDao().updateRegistrar(registrar);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            throw new UnknownRegistrarException(registrar.getCode(), registrar.getId());
        }
    }

    @Override
    public void updateArchiver(Archiver archiver, String login) throws UnknownUserException, NotAdminException, UnknownArchiverException {
        try {
            authorization.checkAdminRights(login);
            factory.archiverDao().updateArchiver(archiver);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            throw new UnknownArchiverException(archiver.getId());
        }
    }

    @Override
    public void updateDigitalLibrary(DigitalLibrary library, String login) throws UnknownUserException, AccessException, UnknownDigLibException {
        try {
            long registrarId = registrarOfDigLibrary(library.getId());
            authorization.checkAccessRightsOrAdmin(registrarId, login);
            factory.diglLibDao().updateLibrary(library);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            throw new UnknownDigLibException(library.getId());
        }
    }

    @Override
    public void updateCatalog(Catalog catalog, String login) throws UnknownUserException, AccessException, UnknownCatalogException {
        try {
            long registrarId = registrarOfCatalog(catalog.getId());
            authorization.checkAccessRightsOrAdmin(registrarId, login);
            factory.catalogDao().updateCatalog(catalog);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            throw new UnknownCatalogException(catalog.getId());
        }
    }

    @Override
    public void updateIntelectualEntity(IntelectualEntity entity, Originator originator, Publication publication, SourceDocument srcDoc, Collection<IntEntIdentifier> identifiers, String login) throws UnknownUserException, NotAdminException, UnknownIntelectualEntity, IdentifierConflictException {
        authorization.checkAdminRights(login);
        new IntelectualEntityUpdater(factory).run(entity, originator, publication, srcDoc, identifiers);
    }

    @Override
    public void updateUser(User user, String login) throws UnknownUserException, NotAdminException {
        try {
            authorization.checkAdminRights(login);
            factory.userDao().updateUser(user);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            throw new UnknownUserException(user.getId());
        }
    }
}
