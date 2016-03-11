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
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.services.DataImportService;
import cz.nkp.urnnbn.services.DigDocRegistrationData;
import cz.nkp.urnnbn.services.exceptions.AccessException;
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
    public UrnNbn registerDigitalDocument(DigDocRegistrationData importData, String login) throws AccessException, UrnNotFromRegistrarException,
            UrnUsedException, UnknownRegistrarException, RegistarScopeIdentifierCollisionException, UnknownArchiverException, UnknownUserException,
            RegistrationModeNotAllowedException, IncorrectPredecessorStatus {
        authorization.checkAccessRights(importData.getRegistrarCode(), login);
        UrnNbn urnNbn = new DigitalDocumentRegistrar(factory, importData).run();
        AdminLogger.getLogger().info(String.format("User %s registered digital-document to %s.", login, urnNbn));
        return urnNbn;
    }

    @Override
    public DigitalInstance addDigitalInstance(DigitalInstance instance, String login) throws AccessException, UnknownDigLibException,
            UnknownDigDocException, UnknownUserException {
        try {
            long registrarId = registrarIdFromDigLibId(instance.getLibraryId());
            authorization.checkAccessRights(registrarId, login);
            instance = new DigitalInstanceImporter(factory, instance).run();
            UrnNbn urn;
            try {
                urn = factory.urnDao().getUrnNbnByDigDocId(instance.getDigDocId());
            } catch (RecordNotFoundException e) {
                throw new RuntimeException(e);
            }
            DigitalLibrary lib;
            try {
                lib = factory.diglLibDao().getLibraryById(instance.getLibraryId());
            } catch (RecordNotFoundException e) {
                throw new UnknownDigLibException(instance.getLibraryId());
            }
            // admin log
            AdminLogger.getLogger().info(
                    String.format("User %s imported digital-instance with id: %d, %s, library: %s.", login, instance.getId(), urn, lib.getName()));
            return instance;
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void addRegistrarScopeIdentifier(RegistrarScopeIdentifier id, String login) throws UnknownRegistrarException, UnknownDigDocException,
            AccessException, UnknownUserException, RegistarScopeIdentifierCollisionException {
        try {
            authorization.checkAccessRights(id.getRegistrarId(), login);
            Registrar registrar;
            try {
                registrar = factory.registrarDao().getRegistrarById(id.getRegistrarId());
            } catch (RecordNotFoundException e) {
                throw new UnknownRegistrarException(id.getRegistrarId());
            }
            UrnNbn urn;
            try {
                urn = factory.urnDao().getUrnNbnByDigDocId(id.getDigDocId());
            } catch (RecordNotFoundException e) {
                throw new RuntimeException(e);
            }
            try {
                factory.digDocIdDao().insertRegistrarScopeId(id);
            } catch (RecordNotFoundException e) {
                throw new UnknownDigDocException(id.getDigDocId());
            } catch (AlreadyPresentException e) {
                throw new RegistarScopeIdentifierCollisionException(id);
            }
            logRegistrarScopeIdCreated(login, id, registrar, urn);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void logRegistrarScopeIdCreated(String login, RegistrarScopeIdentifier id, Registrar registrar, UrnNbn urn) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("User %s created registrar-scope-id with registrar: %s", login, registrar.getName()));
        builder.append(String.format(", type: %s", id.getType()));
        builder.append(String.format(", value: %s", id.getValue()));
        builder.append(String.format(", %s", urn));
        builder.append(".");
        AdminLogger.getLogger().info(builder);
    }

    @Override
    public Archiver insertNewArchiver(Archiver archiver, String login) throws UnknownUserException, NotAdminException {
        try {
            authorization.checkAdminRights(login);
            Long id = factory.archiverDao().insertArchiver(archiver);
            archiver.setId(id);
            logArchiverCreated(login, archiver);
            return archiver;
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void logArchiverCreated(String login, Archiver archiver) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("User %s created archiver with id: %d", login, archiver.getId()));
        builder.append(String.format(", name: %s", archiver.getName()));
        if (archiver.getDescription() != null) {
            builder.append(String.format(", description: %s", archiver.getDescription()));
        }
        builder.append(".");
        AdminLogger.getLogger().info(builder);
    }

    @Override
    public Registrar insertNewRegistrar(Registrar registrar, String login) throws UnknownUserException, NotAdminException,
            RegistrarCollisionException {
        try {
            authorization.checkAdminRights(login);
            Long id = factory.registrarDao().insertRegistrar(registrar);
            registrar.setId(id);
            logRegistrarCreated(login, registrar);
            return registrar;
        } catch (AlreadyPresentException ex) {
            throw new RegistrarCollisionException(registrar.getCode().toString());
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void logRegistrarCreated(String login, Registrar registrar) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("User %s created registrar with code: %s", login, registrar.getCode()));
        builder.append(String.format(", name: %s", registrar.getName()));
        if (registrar.getDescription() != null) {
            builder.append(String.format(", description: %s", registrar.getDescription()));
        }
        builder.append(String.format(", registration modes: %s", registrar.modesToHumanReadableString()));
        builder.append(".");
        AdminLogger.getLogger().info(builder);
    }

    @Override
    public DigitalLibrary insertNewDigitalLibrary(DigitalLibrary library, long registrarId, String login) throws UnknownUserException,
            AccessException, UnknownRegistrarException {
        try {
            authorization.checkAccessRightsOrAdmin(registrarId, login);
            Long id = factory.diglLibDao().insertLibrary(library);
            library.setId(id);
            logLibraryCreated(login, library);
            return library;
        } catch (RecordNotFoundException ex) {
            throw new UnknownRegistrarException(registrarId);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void logLibraryCreated(String login, DigitalLibrary library) throws DatabaseException {
        Registrar registrar;
        try {
            registrar = factory.registrarDao().getRegistrarById(library.getRegistrarId());
        } catch (RecordNotFoundException e) {
            throw new RuntimeException(e);
        }
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("User %s created digital-library with id: %d", login, library.getId()));
        builder.append(String.format(", registrar: %s", registrar.getName()));
        builder.append(String.format(", name: %s", library.getName()));
        if (library.getDescription() != null) {
            builder.append(String.format(", description: %s", library.getDescription()));
        }
        if (library.getUrl() != null) {
            builder.append(String.format(", url: %s", library.getUrl()));
        }
        builder.append(".");
        AdminLogger.getLogger().info(builder);
    }

    @Override
    public Catalog insertNewCatalog(Catalog catalog, long registrarId, String login) throws UnknownUserException, AccessException,
            UnknownRegistrarException {
        try {
            authorization.checkAccessRightsOrAdmin(registrarId, login);
            Long id = factory.catalogDao().insertCatalog(catalog);
            catalog.setId(id);
            logCatalogCreated(login, catalog);
            return catalog;
        } catch (RecordNotFoundException ex) {
            throw new UnknownRegistrarException(registrarId);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void logCatalogCreated(String login, Catalog catalog) throws DatabaseException {
        Registrar registrar;
        try {
            registrar = factory.registrarDao().getRegistrarById(catalog.getRegistrarId());
        } catch (RecordNotFoundException e) {
            throw new RuntimeException(e);
        }
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("User %s created catalog with registrar: %s", login, registrar.getName()));
        builder.append(String.format(", name: %s", catalog.getName()));
        if (catalog.getDescription() != null) {
            builder.append(String.format(", description: %s", catalog.getDescription()));
        }
        builder.append(String.format(", url prefix: %s", catalog.getUrlPrefix()));
        builder.append(".");
        AdminLogger.getLogger().info(builder);
    }

    @Override
    public User addNewUser(User user, String login) throws UnknownUserException, NotAdminException, LoginConflictException {
        try {
            authorization.checkAdminRights(login);
            Long id = factory.userDao().insertUser(user);
            user.setId(id);
            logUserCreated(login, user);
            return user;
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (AlreadyPresentException ex) {
            throw new LoginConflictException(user.getLogin());
        }
    }

    private void logUserCreated(String actorLogin, User user) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("User %s created user with login: %s", actorLogin, user.getLogin()));
        builder.append(String.format(", email: %s", user.getEmail()));
        builder.append(String.format(", admin: %s", user.isAdmin()));
        builder.append(".");
        AdminLogger.getLogger().info(builder);
    }

    @Override
    public void addRegistrarRight(long userId, long registrarId, String login) throws UnknownUserException, NotAdminException,
            RegistrarRightCollisionException, UnknownRegistrarException {
        try {
            Registrar registrar;
            try {
                registrar = factory.registrarDao().getRegistrarById(registrarId);
            } catch (RecordNotFoundException e) {
                throw new UnknownRegistrarException(registrarId);
            }
            User user;
            try {
                user = factory.userDao().getUserById(userId);
            } catch (RecordNotFoundException e) {
                throw new UnknownUserException(userId);
            }
            try {
                factory.userDao().insertAdministrationRight(registrarId, userId);
            } catch (RecordNotFoundException e) {
                // user or registrar again
                throw new RuntimeException(e);
            } catch (AlreadyPresentException e) {
                throw new RegistrarRightCollisionException(userId, registrarId);
            }
            logRegistrarRightAssigned(login, user, registrar);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    private void logRegistrarRightAssigned(String login, User user, Registrar registrar) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("User %s assigned access-right for registrar %s to user %s.", login, registrar.getCode(), user.getLogin()));
        AdminLogger.getLogger().info(builder);
    }
}
