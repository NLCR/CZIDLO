/*F
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.impl;

import cz.nkp.urnnbn.core.AdminLogger;
import cz.nkp.urnnbn.core.RegistrarScopeIdType;
import cz.nkp.urnnbn.core.dto.*;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordReferencedException;
import cz.nkp.urnnbn.services.DataRemoveService;
import cz.nkp.urnnbn.services.exceptions.*;

/**
 * @author Martin Řehánek
 */
public class DataRemoveServiceImpl extends BusinessServiceImpl implements DataRemoveService {

    public DataRemoveServiceImpl(DatabaseConnector conn) {
        super(conn);
    }

    @Override
    public void removeRegistrarScopeIdentifiers(long digDocId, String login) throws UnknownUserException, AccessException, UnknownDigDocException {
        try {
            long registrarId = registrarOfDigDoc(digDocId);
            authorization.checkAccessRights(registrarId, login);
            UrnNbn urn;
            try {
                urn = factory.urnDao().getUrnNbnByDigDocId(digDocId);
            } catch (RecordNotFoundException e) {
                throw new RuntimeException(e);
            }
            try {
                factory.digDocIdDao().deleteRegistrarScopeIds(digDocId);
            } catch (RecordNotFoundException e) {
                throw new UnknownDigDocException(digDocId);
            }
            try {
                factory.documentDao().updateDocumentDatestamp(digDocId);
            } catch (RecordNotFoundException e) {
                throw new UnknownDigDocException(digDocId);
            }
            logRegistrarScopeIdsDeleted(login, urn);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void logRegistrarScopeIdsDeleted(String login, UrnNbn urn) {
        AdminLogger.getLogger().info(String.format("User %s deleted all registrar-scope-ids of %s.", login, urn));
    }

    @Override
    public void removeRegistrarScopeIdentifier(long digDocId, RegistrarScopeIdType type, String login) throws UnknownUserException, AccessException,
            UnknownDigDocException, RegistrarScopeIdentifierNotDefinedException {
        try {
            long registrarId = registrarOfDigDoc(digDocId);
            authorization.checkAccessRights(registrarId, login);
            Registrar registrar;
            try {
                registrar = factory.registrarDao().getRegistrarById(registrarId);
            } catch (RecordNotFoundException e) {
                // throw new UnknownRegistrarException(registrarId);
                throw new RuntimeException(e);
            }
            UrnNbn urn;
            try {
                urn = factory.urnDao().getUrnNbnByDigDocId(digDocId);
            } catch (RecordNotFoundException e) {
                throw new RuntimeException(e);
            }
            RegistrarScopeIdentifier registrarScopeId;
            try {
                registrarScopeId = factory.digDocIdDao().getRegistrarScopeId(digDocId, type);
            } catch (RecordNotFoundException e1) {
                throw new RegistrarScopeIdentifierNotDefinedException(digDocId, type);
            }
            try {
                factory.digDocIdDao().deleteRegistrarScopeId(digDocId, type);
            } catch (RecordNotFoundException e) {
                throw new UnknownDigDocException(digDocId);
            }
            try {
                factory.documentDao().updateDocumentDatestamp(digDocId);
            } catch (RecordNotFoundException e) {
                throw new UnknownDigDocException(digDocId);
            }
            // TODO: 13.11.18 update digDoc timestamp
            // TODO: 13.11.18 reindex
            logRegistrarScopeIdDeleted(login, registrarScopeId, registrar, urn);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void logRegistrarScopeIdDeleted(String login, RegistrarScopeIdentifier id, Registrar registrar, UrnNbn urn) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("User %s deleted registrar-scope-id with registrar: %s", login, registrar.getName()));
        builder.append(String.format(", type: %s", id.getType()));
        builder.append(String.format(", value: %s", id.getValue()));
        builder.append(String.format(", %s", urn));
        builder.append(String.format(", created: %s", formatDateTime(id.getCreated())));
        if (!id.getCreated().equals(id.getModified())) {
            builder.append(String.format(", modified: %s", formatDateTime(id.getModified())));
        }
        builder.append(".");
        AdminLogger.getLogger().info(builder);
    }

    @Override
    public void removeArchiver(long archiverId, String login) throws UnknownArchiverException, CannotBeRemovedException, NotAdminException,
            UnknownUserException {
        try {
            authorization.checkAdminRights(login);
            Archiver archiver = factory.archiverDao().getArchiverById(archiverId);
            factory.archiverDao().deleteArchiver(archiverId);
            logArchiverDeleted(login, archiver);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            throw new UnknownArchiverException(archiverId);
        } catch (RecordReferencedException ex) {
            throw new CannotBeRemovedException(ex.getMessage());
        }
    }

    private void logArchiverDeleted(String login, Archiver archiver) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("User %s deleted archiver with id: %d", login, archiver.getId()));
        builder.append(String.format(", name: %s", archiver.getName()));
        if (archiver.getDescription() != null) {
            builder.append(String.format(", description: %s", archiver.getDescription()));
        }
        builder.append(String.format(", created: %s", formatDateTime(archiver.getCreated())));
        if (!archiver.getCreated().equals(archiver.getModified())) {
            builder.append(String.format(", modified: %s", formatDateTime(archiver.getCreated())));
        }
        builder.append(".");
        AdminLogger.getLogger().info(builder);
    }

    @Override
    public void removeRegistrar(long registrarId, String login) throws UnknownRegistrarException, CannotBeRemovedException, NotAdminException,
            UnknownUserException {
        try {
            authorization.checkAdminRights(login);
            Registrar registrar = factory.registrarDao().getRegistrarById(registrarId);
            factory.registrarDao().deleteRegistrar(registrarId);
            logRegistrarDeleted(login, registrar);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            throw new UnknownRegistrarException(registrarId);
        } catch (RecordReferencedException ex) {
            throw new CannotBeRemovedException("There are digital documents registered by this registrar");
        }
    }

    private void logRegistrarDeleted(String login, Registrar registrar) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("User %s deleted registrar with code: %s", login, registrar.getCode()));
        builder.append(String.format(", name: %s", registrar.getName()));
        if (registrar.getDescription() != null) {
            builder.append(String.format(", description: %s", registrar.getDescription()));
        }
        builder.append(String.format(", registration modes: %s", registrar.modesToHumanReadableString()));
        builder.append(String.format(", created: %s", formatDateTime(registrar.getCreated())));
        if (!registrar.getCreated().equals(registrar.getModified())) {
            builder.append(String.format(", modified: %s", formatDateTime(registrar.getModified())));
        }
        builder.append(".");
        AdminLogger.getLogger().info(builder);
    }

    @Override
    public void removeDigitalLibrary(long libraryId, String login) throws UnknownUserException, AccessException, UnknownDigLibException,
            CannotBeRemovedException {
        try {
            long registrarId = registrarIdFromDigLibId(libraryId);
            authorization.checkAccessRightsOrAdmin(registrarId, login);
            DigitalLibrary library = factory.diglLibDao().getLibraryById(libraryId);
            factory.diglLibDao().deleteLibrary(libraryId);
            logLibraryDeleted(login, library);
            // AdminLogger.getLogger().info("user '" + login + "' deleted " + library + "'");
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            throw new UnknownDigLibException(libraryId);
        } catch (RecordReferencedException ex) {
            throw new CannotBeRemovedException(ex.getMessage());
        }
    }

    private void logLibraryDeleted(String login, DigitalLibrary library) throws DatabaseException {
        Registrar registrar;
        try {
            registrar = factory.registrarDao().getRegistrarById(library.getRegistrarId());
        } catch (RecordNotFoundException e) {
            throw new RuntimeException(e);
        }
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("User %s deleted digital-library with id: %d", login, library.getId()));
        builder.append(String.format(", registrar: %s", registrar.getName()));
        builder.append(String.format(", name: %s", library.getName()));
        if (library.getDescription() != null) {
            builder.append(String.format(", description: %s", library.getDescription()));
        }
        if (library.getUrl() != null) {
            builder.append(String.format(", url: %s", library.getUrl()));
        }
        builder.append(String.format(", created: %s", formatDateTime(library.getCreated())));
        if (!library.getCreated().equals(library.getModified())) {
            builder.append(String.format(", modified: %s", formatDateTime(library.getModified())));
        }
        builder.append(".");
        AdminLogger.getLogger().info(builder);
    }

    @Override
    public void removeCatalog(long catalogId, String login) throws UnknownUserException, AccessException, UnknownCatalogException {
        try {
            long registrarId = registrarOfCatalog(catalogId);
            authorization.checkAccessRightsOrAdmin(registrarId, login);
            Catalog catalog = factory.catalogDao().getCatalogById(catalogId);
            factory.catalogDao().deleteCatalog(catalogId);
            logCatalogDeleted(login, catalog);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            throw new UnknownCatalogException(catalogId);
        }
    }

    private void logCatalogDeleted(String login, Catalog catalog) throws DatabaseException {
        Registrar registrar;
        try {
            registrar = factory.registrarDao().getRegistrarById(catalog.getRegistrarId());
        } catch (RecordNotFoundException e) {
            throw new RuntimeException(e);
        }
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("User %s deleted catalog with registrar: %s", login, registrar.getName()));
        builder.append(String.format(", name: %s", catalog.getName()));
        if (catalog.getDescription() != null) {
            builder.append(String.format(", description: %s", catalog.getDescription()));
        }
        builder.append(String.format(", url prefix: %s", catalog.getUrlPrefix()));
        builder.append(String.format(", created: %s", formatDateTime(catalog.getCreated())));
        if (!catalog.getCreated().equals(catalog.getModified())) {
            builder.append(String.format(", modified: %s", formatDateTime(catalog.getModified())));
        }
        builder.append(".");
        AdminLogger.getLogger().info(builder);
    }

    @Override
    public void removeUser(long userId, String login) throws UnknownUserException, NotAdminException, UnknownUserException {
        try {
            authorization.checkAdminRights(login);
            User user = factory.userDao().getUserById(userId);
            factory.userDao().deleteUser(userId);
            logUserDeleted(login, user);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            throw new UnknownUserException(userId);
        }
    }

    private void logUserDeleted(String actorLogin, User user) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("User %s deleted user with login: %s", actorLogin, user.getLogin()));
        builder.append(String.format(", email: %s", user.getEmail()));
        builder.append(String.format(", admin: %s", user.isAdmin()));
        builder.append(String.format(", created: %s", formatDateTime(user.getCreated())));
        if (!user.getCreated().equals(user.getModified())) {
            builder.append(String.format(", modified: %s", formatDateTime(user.getModified())));
        }
        builder.append(".");
        AdminLogger.getLogger().info(builder);
    }

    @Override
    public void removeRegistrarRight(long userId, long registrarId, String login) throws UnknownUserException, NotAdminException,
            UnknownRegistrarException {
        try {
            authorization.checkAdminRights(login);
            User user;
            try {
                user = factory.userDao().getUserById(userId);
            } catch (RecordNotFoundException e) {
                throw new UnknownUserException(userId);
            }
            Registrar registrar;
            try {
                registrar = factory.registrarDao().getRegistrarById(registrarId);
            } catch (RecordNotFoundException e) {
                throw new UnknownRegistrarException(registrarId);
            }
            try {
                factory.userDao().deleteAdministrationRight(registrarId, userId);
            } catch (RecordNotFoundException e) {
                // no access right
                throw new RuntimeException(e);
            }
            logRegistrarRightRemoved(login, user, registrar);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    private void logRegistrarRightRemoved(String login, User user, Registrar registrar) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("User %s removed access-right for registrar %s from user %s.", login, registrar.getCode(), user.getLogin()));
        AdminLogger.getLogger().info(builder);
    }

    @Override
    public void deactivateDigitalInstance(long instanceId, String login) throws UnknownUserException, AccessException, UnknownDigInstException {
        try {
            DigitalInstance digInstance;
            try {
                digInstance = factory.digInstDao().getDigInstanceById(instanceId);
            } catch (RecordNotFoundException e) {
                throw new UnknownDigInstException(instanceId);
            }
            DigitalLibrary lib;
            try {
                lib = factory.diglLibDao().getLibraryById(digInstance.getLibraryId());
            } catch (RecordNotFoundException e) {
                throw new RuntimeException(e);
            }
            UrnNbn urn;
            try {
                urn = factory.urnDao().getUrnNbnByDigDocId(digInstance.getDigDocId());
            } catch (RecordNotFoundException e) {
                throw new RuntimeException(e);
            }
            authorization.checkAccessRights(lib.getRegistrarId(), login);
            try {
                factory.digInstDao().deactivateDigInstance(instanceId);
            } catch (RecordNotFoundException e) {
                throw new UnknownDigInstException(instanceId);
            }
            logDigitalInstanceDeactivated(login, digInstance, urn, lib);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void logDigitalInstanceDeactivated(String login, DigitalInstance instance, UrnNbn urn, DigitalLibrary lib) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("User %s deactivated digital-instance with id: %d", login, instance.getId()));
        builder.append(String.format(", %s", urn));
        builder.append(String.format(", library: %s", lib.getName()));
        builder.append(String.format(", created: %s", formatDateTime(instance.getCreated())));
        builder.append(".");
        AdminLogger.getLogger().info(builder);
    }

    @Override
    public void deactivateUrnNbn(UrnNbn urn, String login, String note) throws UnknownUserException, AccessException, UnknownDigDocException {
        try {
            long registrarId = registrarOfDigDoc(urn.getDigDocId());
            authorization.checkAccessRights(registrarId, login);
            if ("".equals(note)) {
                note = null;
            }
            factory.urnDao().deactivateUrnNbn(urn.getRegistrarCode(), urn.getDocumentCode(), note);
            if (note != null) {
                AdminLogger.getLogger().info(String.format("User %s deactivated %s with note: %s.", login, urn, note));
            } else {
                AdminLogger.getLogger().info(String.format("User %s deactivated %s.", login, urn));
            }
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        }
    }
}
