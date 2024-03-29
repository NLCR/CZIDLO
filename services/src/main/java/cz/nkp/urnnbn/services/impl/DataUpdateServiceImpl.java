/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.impl;

import cz.nkp.urnnbn.core.AdminLogger;
import cz.nkp.urnnbn.core.dto.*;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.services.DataUpdateService;
import cz.nkp.urnnbn.services.exceptions.*;
import cz.nkp.urnnbn.solr_indexer.SolrIndexer;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Martin Řehánek
 */
public class DataUpdateServiceImpl extends BusinessServiceImpl implements DataUpdateService {

    private static final Logger LOGGER = Logger.getLogger(DataUpdateServiceImpl.class.getName());

    private final SolrIndexer solrIndexer;

    public DataUpdateServiceImpl(DatabaseConnector conn, SolrIndexer solrIndexer) {
        super(conn);
        this.solrIndexer = solrIndexer;
    }

    @Override
    public void updateRegistrarScopeIdentifier(String login, RegistrarScopeIdentifier id) throws UnknownRegistrarException, UnknownDigDocException,
            AccessException, UnknownUserException, RegistrarScopeIdentifierCollisionException, RegistrarScopeIdentifierNotDefinedException {
        try {
            authorization.checkAccessRightsOrAdmin(id.getRegistrarId(), login);
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
                throw new UnknownDigDocException(id.getDigDocId());
            }
            //update rsId
            try {
                factory.digDocIdDao().updateRegistrarScopeIdValue(id);
            } catch (RecordNotFoundException e) {
                //digital document does not have registrar-scope id with this type
                throw new RegistrarScopeIdentifierNotDefinedException(id.getDigDocId(), id.getType());
            } catch (AlreadyPresentException e) {
                //such registrarScope id already exists (for another digital document)
                throw new RegistrarScopeIdentifierCollisionException(registrar.getCode(), id.getType(), id.getValue());
            }
            //update digital-document timestamp
            try {
                factory.documentDao().updateDocumentDatestamp(id.getDigDocId());
            } catch (RecordNotFoundException e) {
                //should never happen, digital document has been located already
                throw new UnknownDigDocException(id.getDigDocId());
            }
            logRegistrarScopeIdUpdated(login, id, registrar, urn);
            //reindexace
            reindexDigitalDocument(id.getDigDocId(), urn);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void logRegistrarScopeIdUpdated(String login, RegistrarScopeIdentifier id, Registrar registrar, UrnNbn urn) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("User %s updated registrar-scope-id with registrar: %s", login, registrar.getName()));
        builder.append(String.format(", type: %s", id.getType()));
        builder.append(String.format(", value: %s", id.getValue()));
        builder.append(String.format(", %s", urn));
        builder.append(String.format(", created: %s", formatDateTime(id.getCreated())));
        builder.append(".");
        AdminLogger.getLogger().info(builder);
    }

    @Override
    public void updateDigitalDocument(DigitalDocument doc, String login) throws AccessException, UnknownUserException, UnknownDigDocException {
        try {
            authorization.checkAccessRightsOrAdmin(doc.getRegistrarId(), login);
            factory.documentDao().updateDocument(doc);
            UrnNbn urn = factory.urnDao().getUrnNbnByDigDocId(doc.getId());
            AdminLogger.getLogger().info(String.format("User %s updated digital-document of %s.", login, urn));
            reindexDigitalDocument(doc.getId(), urn);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            throw new UnknownDigDocException(doc.getId());
        }
    }

    private void reindexDigitalDocument(long digDocId, UrnNbn urnNbn) { //this should never break the update itself
        try {
            solrIndexer.indexDocument(digDocId);
            LOGGER.log(Level.INFO, "Indexed {0} ", urnNbn.toString());
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, "Error indexing " + urnNbn.toString(), e);
        }
    }

    @Override
    public void updateRegistrar(Registrar registrar, String login) throws UnknownUserException, AccessException, UnknownRegistrarException {
        try {
            authorization.checkAccessRightsOrAdmin(registrar.getId(), login);
            factory.registrarDao().updateRegistrar(registrar);
            logRegistrarUpdated(login, registrar);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            throw new UnknownRegistrarException(registrar.getCode(), registrar.getId());
        }
    }

    private void logRegistrarUpdated(String login, Registrar registrar) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("User %s updated registrar with code: %s", login, registrar.getCode()));
        builder.append(String.format(", name: %s", registrar.getName()));
        if (registrar.getDescription() != null) {
            builder.append(String.format(", description: %s", registrar.getDescription()));
        }
        // TODO: nejak reg. mody nenatahuji z db
        builder.append(String.format(", registration modes: %s", registrar.modesToHumanReadableString()));
        builder.append(String.format(", created: %s", formatDateTime(registrar.getCreated())));
        builder.append(".");
        AdminLogger.getLogger().info(builder);
    }

    @Override
    public void updateArchiver(Archiver archiver, String login) throws UnknownUserException, NotAdminException, UnknownArchiverException {
        try {
            authorization.checkAdminRights(login);
            try {
                factory.archiverDao().updateArchiver(archiver);
            } catch (RecordNotFoundException e) {
                throw new UnknownArchiverException(archiver.getId());
            }
            try {
                factory.registrarDao().getRegistrarById(archiver.getId());
            } catch (RecordNotFoundException e) {
                // archiver is not registrar as well
                logArchiverUpdated(login, archiver);
            }
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void logArchiverUpdated(String login, Archiver archiver) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("User %s updated archiver with id: %d", login, archiver.getId()));
        builder.append(String.format(", name: %s", archiver.getName()));
        if (archiver.getDescription() != null) {
            builder.append(String.format(", description: %s", archiver.getDescription()));
        }
        builder.append(String.format(", created: %s", formatDateTime(archiver.getCreated())));
        builder.append(".");
        AdminLogger.getLogger().info(builder);
    }

    @Override
    public void updateDigitalLibrary(DigitalLibrary library, String login) throws UnknownUserException, AccessException, UnknownDigLibException {
        try {
            long registrarId = registrarIdFromDigLibId(library.getId());
            authorization.checkAccessRightsOrAdmin(registrarId, login);
            factory.diglLibDao().updateLibrary(library);
            // because library object only contains data to be updated and not datestampes,
            // registrar id
            logLibraryUpdated(login, factory.diglLibDao().getLibraryById(library.getId()));
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            throw new UnknownDigLibException(library.getId());
        }
    }

    private void logLibraryUpdated(String login, DigitalLibrary library) throws DatabaseException {
        Registrar registrar;
        try {
            System.out.println(library);
            registrar = factory.registrarDao().getRegistrarById(library.getRegistrarId());
        } catch (RecordNotFoundException e) {
            throw new RuntimeException(e);
        }

        StringBuilder builder = new StringBuilder();
        builder.append(String.format("User %s updated digital-library with id: %d", login, library.getId()));
        builder.append(String.format(", registrar: %s", registrar.getName()));
        builder.append(String.format(", name: %s", library.getName()));
        if (library.getDescription() != null) {
            builder.append(String.format(", description: %s", library.getDescription()));
        }
        if (library.getUrl() != null) {
            builder.append(String.format(", url: %s", library.getUrl()));
        }
        builder.append(String.format(", created: %s", formatDateTime(library.getCreated())));
        builder.append(".");
        AdminLogger.getLogger().info(builder);
    }

    @Override
    public void updateCatalog(Catalog catalog, String login) throws UnknownUserException, AccessException, UnknownCatalogException {
        try {
            long registrarId = registrarOfCatalog(catalog.getId());
            authorization.checkAccessRightsOrAdmin(registrarId, login);
            factory.catalogDao().updateCatalog(catalog);
            logCatalogUpdated(login, catalog);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            throw new UnknownCatalogException(catalog.getId());
        }
    }

    private void logCatalogUpdated(String login, Catalog catalog) throws DatabaseException {
        // Web: user martin updated catalog with registrar: Národní knihovna, name:
        // test-katalog, description: testovaci kagalog, urlPrefix: http://aleph.novadomena.cz,
        // created: 03.08.2015 15:35:29
        Registrar registrar;
        try {
            registrar = factory.registrarDao().getRegistrarById(catalog.getRegistrarId());
        } catch (RecordNotFoundException e) {
            throw new RuntimeException(e);
        }
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("User %s updated catalog with registrar: %s", login, registrar.getName()));
        builder.append(String.format(", name: %s", catalog.getName()));
        if (catalog.getDescription() != null) {
            builder.append(String.format(", description: %s", catalog.getDescription()));
        }
        builder.append(String.format(", url prefix: %s", catalog.getUrlPrefix()));
        builder.append(String.format(", created: %s", formatDateTime(catalog.getCreated())));
        builder.append(".");
        AdminLogger.getLogger().info(builder);
    }

    @Override
    public void updateIntelectualEntity(IntelectualEntity entity,
                                        Originator originator,
                                        Publication publication,
                                        SourceDocument srcDoc,
                                        Collection<IntEntIdentifier> identifiers,
                                        String login) throws UnknownUserException, UnknownIntelectualEntity, AccessException {
        UrnNbn urn = null;
        Long digDocId = null;
        try {
            List<DigitalDocument> digDocs = factory.documentDao().getDocumentsOfIntEntity(entity.getId());
            // there is always exactly one digital document, even though data model allows more
            // dig-docs for single int-entity
            if (digDocs == null || digDocs.isEmpty()) {
                urn = null;
            } else {
                urn = factory.urnDao().getUrnNbnByDigDocId(digDocs.get(0).getId());
            }
            for (DigitalDocument doc : digDocs) {
                urn = factory.urnDao().getUrnNbnByDigDocId(doc.getId());
                digDocId = doc.getId();
                authorization.checkAccessRightsOrAdmin(urn.getRegistrarCode(), login);
            }
        } catch (RecordNotFoundException e) {
            throw new RuntimeException(e);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
        new IntelectualEntityUpdater(factory, solrIndexer).run(entity, originator, publication, srcDoc, identifiers, urn, digDocId);
        AdminLogger.getLogger().info(String.format("User %s updated intelectual-entity of %s.", login, urn));
    }

    @Override
    public void updateUser(User user, String login) throws UnknownUserException, NotAdminException {
        try {
            authorization.checkAdminRights(login);
            User fromDb = factory.userDao().getUserByLogin(user.getLogin());
            if (user.getPasswordSalt() == null) {
                user.setPasswordSalt(fromDb.getPasswordSalt());
            }
            if (user.getPasswordHash() == null) {
                user.setPasswordHash(fromDb.getPasswordHash());
            }
            factory.userDao().updateUser(user);
            logUserUpdated(login, user);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            throw new UnknownUserException(user.getId());
        }
    }

    public void updateUserSelf(User user, String login) throws UnknownUserException, InvalidUserException {
        try {
            if (!login.equals(user.getLogin())) {
                throw new InvalidUserException(login);
            }
            User fromDb = factory.userDao().getUserByLogin(login);
            //self cannot update administrator flag
            user.setAdmin(fromDb.isAdmin());
            if (user.getPasswordSalt() == null) {
                user.setPasswordSalt(fromDb.getPasswordSalt());
            }
            if (user.getPasswordHash() == null) {
                user.setPasswordHash(fromDb.getPasswordHash());
            }
            factory.userDao().updateUser(user);
            logUserUpdated(login, user);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            throw new UnknownUserException(user.getId());
        }
    }

    private void logUserUpdated(String actorLogin, User user) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("User %s created user with login: %s", actorLogin, user.getLogin()));
        builder.append(String.format(", email: %s", user.getEmail()));
        builder.append(String.format(", admin: %s", user.isAdmin()));
        builder.append(String.format(", created: %s", formatDateTime(user.getCreated())));
        builder.append(".");
        AdminLogger.getLogger().info(builder);
    }

    @Override
    public void updateContent(Content content, String login) throws UnknownUserException, NotAdminException, ContentNotFoundException {
        try {
            authorization.checkAdminRights(login);
            factory.contentDao().updateContent(content);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            throw new ContentNotFoundException(content.getLanguage(), content.getName(), ex);
        }
    }

    @Override
    public void updateDigitalInstance(DigitalInstance instance, String login) throws UnknownUserException, AccessException, UnknownDigInstException {
        try {
            factory.digInstDao().updateDigInstance(instance);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            throw new UnknownDigInstException(instance.getId());
        }
    }

}
