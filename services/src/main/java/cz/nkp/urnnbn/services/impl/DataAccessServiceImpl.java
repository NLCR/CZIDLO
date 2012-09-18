/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.impl;

import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.dto.Archiver;
import cz.nkp.urnnbn.core.dto.Catalog;
import cz.nkp.urnnbn.core.dto.DigDocIdentifier;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.SourceDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.services.DataAccessService;
import cz.nkp.urnnbn.services.exceptions.NotAdminException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.joda.time.DateTime;

/**
 *
 * @author Martin Řehánek
 */
public class DataAccessServiceImpl extends BusinessServiceImpl implements DataAccessService {

    public DataAccessServiceImpl(DatabaseConnector con) {
        super(con);
    }

    @Override
    public UrnNbn urnByDigDocId(long digRepId) throws DatabaseException {
        try {
            return factory.urnDao().getUrnNbnByDigDocId(digRepId);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            return null;
        }
    }

    @Override
    public UrnNbnWithStatus urnByRegistrarCodeAndDocumentCode(RegistrarCode code, String documentCode) throws DatabaseException {
        try {
            UrnNbn urn = factory.urnDao().getUrnNbnByRegistrarCodeAndDocumentCode(code, documentCode);
            return new UrnNbnWithStatus(urn, UrnNbnWithStatus.Status.ACTIVE);
        } catch (RecordNotFoundException ex) { //urn:nb not in table urn:nbn
            try {
                UrnNbn urn = factory.urnReservedDao().getUrn(code, documentCode);
                return new UrnNbnWithStatus(urn, UrnNbnWithStatus.Status.RESERVED);
            } catch (RecordNotFoundException ex2) { //urn:nbn also not reserved
                try {
                    //TODO: actually search in abandonedUrnNbn table
                    UrnNbn urn = null;
                    if (true) {
                        throw new RecordNotFoundException();
                    }
                    return new UrnNbnWithStatus(urn, UrnNbnWithStatus.Status.ABANDONED);
                } catch (RecordNotFoundException ex3) { //urn:nbn not even ebandoned
                    UrnNbn urn = new UrnNbn(code, documentCode, null);
                    return new UrnNbnWithStatus(urn, UrnNbnWithStatus.Status.FREE);
                }
            }
        }
    }

    @Override
    public DigitalDocument digDocByInternalId(long digRepId) throws DatabaseException {
        try {
            return factory.documentDao().getDocumentByDbId(digRepId);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            return null;
        }
    }

    @Override
    public List<DigDocIdentifier> digDocIdentifiersByDigDocId(long id) throws DatabaseException {
        try {
            return factory.digDocIdDao().getIdList(id);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            return Collections.<DigDocIdentifier>emptyList();
        }
    }

    @Override
    public Registrar registrarById(long id) throws DatabaseException {
        try {
            return factory.registrarDao().getRegistrarById(id);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            return null;
        }
    }

    @Override
    public Archiver archiverById(long id) throws DatabaseException {
        try {
            return factory.archiverDao().getArchiverById(id);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            return null;
        }
    }

    @Override
    public IntelectualEntity entityById(long id) throws DatabaseException {
        try {
            return factory.intelectualEntityDao().getEntityByDbId(id);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            return null;
        }
    }

    @Override
    public List<IntelectualEntity> entitiesByIdValue(String value) throws DatabaseException {
        List<Long> idList = factory.intelectualEntityDao().getEntitiesDbIdListByIdentifierValue(value);
        List<IntelectualEntity> result = new ArrayList<IntelectualEntity>(idList.size());
        for (long id : idList) {
            IntelectualEntity entity = entityById(id);
            if (entity != null) {
                result.add(entity);
            }
        }
        return result;
    }

    @Override
    public List<IntEntIdentifier> intEntIdentifiersByIntEntId(long intEntId) throws DatabaseException {
        try {
            return factory.intEntIdentifierDao().getIdList(intEntId);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            return Collections.<IntEntIdentifier>emptyList();
        }
    }

    @Override
    public Publication publicationByIntEntId(long intEntId) throws DatabaseException {
        try {
            return factory.publicationDao().getPublicationById(intEntId);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.FINE, "no publication found for entity {0}", intEntId);
            return null;
        }
    }

    @Override
    public Originator originatorByIntEntId(long intEntId) throws DatabaseException {
        try {
            return factory.originatorDao().getOriginatorById(intEntId);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.FINE, "no primary originator found for entity {0}", intEntId);
            return null;
        }
    }

    @Override
    public SourceDocument sourceDocumentByIntEntId(long intEntId) throws DatabaseException {
        try {
            return factory.srcDocDao().getSrcDocById(intEntId);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.FINE, "no souce document found for entity {0}", intEntId);
            return null;
        }
    }

    @Override
    public Registrar registrarByCode(RegistrarCode code) throws DatabaseException {
        try {
            return factory.registrarDao().getRegistrarByCode(code);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            return null;
        }
    }

    @Override
    public List<DigitalLibrary> librariesByRegistrarId(long registrarId) throws DatabaseException {
        try {
            return factory.diglLibDao().getLibraries(registrarId);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            return Collections.<DigitalLibrary>emptyList();
        }
    }

    @Override
    public List<Catalog> catalogs() throws DatabaseException {
        return factory.catalogDao().getCatalogs();
    }

    @Override
    public List<Catalog> catalogsByRegistrarId(long registrarId) throws DatabaseException {
        try {
            return factory.catalogDao().getCatalogs(registrarId);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            return Collections.<Catalog>emptyList();
        }
    }

    @Override
    public List<Registrar> registrars() throws DatabaseException {
        return factory.registrarDao().getAllRegistrars();
    }

    @Override
    public int digitalDocumentsCount(long registrarId) throws DatabaseException {
        try {
            return factory.documentDao().getDigDocCount(registrarId);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            return 0;
        }
    }

    @Override
    public DigitalDocument digDocByIdentifier(DigDocIdentifier id) throws DatabaseException {
        try {
            Long digRepId = factory.documentDao().getDigDocDbIdByIdentifier(id);
            return factory.documentDao().getDocumentByDbId(digRepId);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            return null;
        }
    }

    @Override
    public List<DigitalDocument> digDocsOfIntEnt(long intEntId) throws DatabaseException {
        try {
            return factory.documentDao().getDocumentsOfIntEntity(intEntId);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            return Collections.<DigitalDocument>emptyList();
        }
    }

    @Override
    public long digitalInstancesCount() throws DatabaseException {
        return factory.digInstDao().getTotalCount();
    }

    @Override
    public List<DigitalInstance> digInstancesByDigDocId(long digDocId) throws DatabaseException {
        try {
            return factory.digInstDao().getDigitalInstancesOfDigDoc(digDocId);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            return Collections.<DigitalInstance>emptyList();
        }
    }

    @Override
    public DigitalInstance digInstanceByInternalId(long id) throws DatabaseException {
        try {
            return factory.digInstDao().getDigInstanceById(id);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            return null;
        }
    }

    @Override
    public DigitalLibrary libraryByInternalId(long libraryId) throws DatabaseException {
        try {
            return factory.diglLibDao().getLibraryById(libraryId);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            return null;
        }
    }

    @Override
    public List<Archiver> archivers() throws DatabaseException {
        List<Archiver> includingRegistrars = factory.archiverDao().getAllArchivers();
        List<Long> registrarIdList = factory.registrarDao().getAllRegistrarsId();
        List<Archiver> result = new ArrayList<Archiver>(includingRegistrars.size() - registrarIdList.size());
        for (Archiver archiverOrRegistrar : includingRegistrars) {
            if (!registrarIdList.contains(archiverOrRegistrar.getId())) {
                result.add(archiverOrRegistrar);
            }
        }
        return result;
    }

    @Override
    public List<User> users(String login, boolean includePasswords) throws UnknownUserException, NotAdminException {
        try {
            authorization.checkAdminRights(login);
            return factory.userDao().getAllUsers(includePasswords);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public User userByLogin(String login, boolean includePassword) throws UnknownUserException {
        try {
            return factory.userDao().getUserByLogin(login, includePassword);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            throw new UnknownUserException(login);
        }
    }

    @Override
    public List<Registrar> registrarsManagedByUser(long userId, String login) throws UnknownUserException, NotAdminException {
        try {
            return factory.registrarDao().getRegistrarsManagedByUser(userId);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            throw new UnknownUserException(userId);
        }
    }

    @Override
    public Set<UrnNbn> urnNbnsOfChangedRecordsOfRegistrar(Registrar registrar, DateTime from, DateTime until) {
        try {
            Set<DigitalDocument> foundDigitalDocuments = new HashSet<DigitalDocument>();
            //1. vsechny zmenene DD, pak odfiltrovat podle id registratora, vytahnout urn:nbn
            foundDigitalDocuments.addAll(findChangedDigDocs(registrar, from, until));
            //2. vsechny zmenene identifikatory DD a pro kazdy nacist DD
            foundDigitalDocuments.addAll(findDigDocsWithChangedIdentifier(from, until));
            //3. digitalni dokumenty zmenenych dig. instanci
            foundDigitalDocuments.addAll(findDigDocsOfChangedDigInstances(from, until));
            //4. digitalni dokumenty zmenenych int. entit
            foundDigitalDocuments.addAll(findDigDocsOfChangedIntEntities(from, until));
            //remove dig docs of other registrars
            Set<DigitalDocument> digDocsOfRegistrar = removeDigDocsOfOtherRegistrars(foundDigitalDocuments, registrar);
            Set<UrnNbn> result = toUrnNbnSet(digDocsOfRegistrar);
            //add changed urn:nbns
            result.addAll(findChangedUrnNbns(registrar, from, until));
            return result;
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Set<UrnNbn> urnNbnsOfChangedRecords(DateTime from, DateTime until) {
        try {
            Set<DigitalDocument> foundDigitalDocuments = new HashSet<DigitalDocument>();
            //1. vsechny zmenene DD, pak odfiltrovat podle id registratora, vytahnout urn:nbn
            foundDigitalDocuments.addAll(findChangedDigDocs(from, until));
            //2. vsechny zmenene identifikatory DD a pro kazdy nacist DD
            foundDigitalDocuments.addAll(findDigDocsWithChangedIdentifier(from, until));
            //3. digitalni dokumenty zmenenych dig. instanci
            foundDigitalDocuments.addAll(findDigDocsOfChangedDigInstances(from, until));
            //4. digitalni dokumenty zmenenych int. entit
            foundDigitalDocuments.addAll(findDigDocsOfChangedIntEntities(from, until));
            Set<UrnNbn> result = toUrnNbnSet(foundDigitalDocuments);
            //add changed urn:nbns
            result.addAll(findChangedUrnNbns(from, until));
            return result;
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Collection<DigitalDocument> findDigDocsOfChangedIntEntities(DateTime from, DateTime until) throws DatabaseException {
        List<Long> entityDbList = factory.intelectualEntityDao().getEntitiesDbIdListByTimestamps(from, until);
        Set<DigitalDocument> result = new HashSet<DigitalDocument>();
        for (Long entityId : entityDbList) {
            result.addAll(digDocsOfIntEnt(entityId));
        }
        return result;
    }

    private List<DigitalDocument> findChangedDigDocs(Registrar registrar, DateTime from, DateTime until) throws DatabaseException {
        try {
            return factory.documentDao().getDigDocsByRegistrarIdAndTimestamps(registrar.getId(), from, until);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
            return Collections.<DigitalDocument>emptyList();
        }
    }

    private List<DigitalDocument> findChangedDigDocs(DateTime from, DateTime until) throws DatabaseException {
        return factory.documentDao().getDigDocsByTimestamps(from, until);
    }

    private Set<DigitalDocument> findDigDocsWithChangedIdentifier(DateTime from, DateTime until) throws DatabaseException {
        List<DigDocIdentifier> identifiers = factory.digDocIdDao().getIdListByTimestamps(from, until);
        Set<DigitalDocument> result = new HashSet<DigitalDocument>();
        for (DigDocIdentifier id : identifiers) {
            try {
                result.add(factory.documentDao().getDocumentByDbId(id.getDigDocId()));
            } catch (RecordNotFoundException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }

    private Set<DigitalDocument> findDigDocsOfChangedDigInstances(DateTime from, DateTime until) throws DatabaseException {
        List<DigitalInstance> digInstances = factory.digInstDao().getDigitalInstancesByTimestamps(from, until);
        Set<DigitalDocument> result = new HashSet<DigitalDocument>();
        for (DigitalInstance digInst : digInstances) {
            try {
                result.add(factory.documentDao().getDocumentByDbId(digInst.getDigDocId()));
            } catch (RecordNotFoundException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }

    private Set<DigitalDocument> removeDigDocsOfOtherRegistrars(Set<DigitalDocument> original, Registrar registrar) {
        Set<DigitalDocument> result = new HashSet<DigitalDocument>(original.size());
        for (DigitalDocument doc : original) {
            if (doc.getRegistrarId() == registrar.getId()) {
                result.add(doc);
            }
        }
        return result;
    }

    private Set<UrnNbn> toUrnNbnSet(Set<DigitalDocument> docs) throws DatabaseException {
        Set<UrnNbn> result = new HashSet<UrnNbn>(docs.size());
        for (DigitalDocument doc : docs) {
            try {
                result.add(factory.urnDao().getUrnNbnByDigDocId(doc.getId()));
            } catch (RecordNotFoundException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }

    private List<UrnNbn> findChangedUrnNbns(DateTime from, DateTime until) throws DatabaseException {
        return factory.urnDao().getUrnNbnsByTimestamps(from, until);
    }

    private List<UrnNbn> findChangedUrnNbns(Registrar registrar, DateTime from, DateTime until) throws DatabaseException {
        return factory.urnDao().getUrnNbnsByRegistrarCodeAndTimestamps(registrar.getCode(), from, until);
    }
}
