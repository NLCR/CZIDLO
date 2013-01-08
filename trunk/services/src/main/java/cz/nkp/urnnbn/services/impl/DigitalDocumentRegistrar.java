/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.impl;

import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.UrnNbnWithStatus.Status;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.RegistrarScopeIdentifier;
import cz.nkp.urnnbn.core.dto.SourceDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.ArchiverDAO;
import cz.nkp.urnnbn.core.persistence.DAOFactory;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.services.DataImportService;
import cz.nkp.urnnbn.services.DigDocRegistrationData;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.RegistarScopeIdentifierCollisionException;
import cz.nkp.urnnbn.services.exceptions.UnknownArchiverException;
import cz.nkp.urnnbn.services.exceptions.UnknownRegistrarException;
import cz.nkp.urnnbn.services.exceptions.UrnNotFromRegistrarException;
import cz.nkp.urnnbn.services.exceptions.UrnUsedException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalDocumentRegistrar {

    private static final Logger logger = Logger.getLogger(DataImportService.class.getName());
    private final DAOFactory factory;
    private final DigDocRegistrationData data;
    private final IntelectualEntityMerger merger;
    private final UrnNbnFinder finder;

    DigitalDocumentRegistrar(DAOFactory factory, DigDocRegistrationData data) throws UnknownRegistrarException {
        this.data = data;
        this.factory = factory;
        this.merger = new IntelectualEntityMerger(factory);
        this.finder = initFinder(factory, data);
    }

    private UrnNbnFinder initFinder(DAOFactory factory, DigDocRegistrationData data) throws UnknownRegistrarException {
        try {
            Registrar registrar = factory.registrarDao().getRegistrarByCode(data.getRegistrarCode());
            return new UrnNbnFinder(factory, registrar);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            throw new UnknownRegistrarException(data.getRegistrarCode());
        }
    }

    public UrnNbn run() throws AccessException, UrnNotFromRegistrarException, UrnUsedException, RegistarScopeIdentifierCollisionException, UnknownArchiverException {
        synchronized (DigitalDocumentRegistrar.class) {
            checkPredecessorsFromSameRegistrar();
            RollbackRecord transactionLog = new RollbackRecord();
            UrnNbn urn = urnToBeUsed(transactionLog);
            long ieId = findOrImportIntelectualEntityWithRollback(transactionLog);
            long digDocId = persistDigDocWithRollback(transactionLog, ieId);
            persistDigDocIdentifiersWithRollback(transactionLog, digDocId);
            persistUrnNbnWithRollback(urn, transactionLog, digDocId);
            persistUrnNbnPredecessorsWithRollback(urn, transactionLog);
            try {
                return factory.urnDao().getUrnNbnByDigDocId(digDocId);
            } catch (DatabaseException ex) {
                throw new RuntimeException(ex);
            } catch (RecordNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private UrnNbn urnToBeUsed(RollbackRecord rollback) throws UrnNotFromRegistrarException, UrnUsedException {
        UrnNbn urnInInputData = data.getUrn();
        if (urnInInputData != null) {
            logger.log(Level.INFO, "{0} found in import data", urnInInputData);
            //selected by registrar
            checkUrnBelongsToRegistrar(urnInInputData);
            checkUrnIsFree(urnInInputData);
            UrnNbn urnNbnReserved = urnNbnReserved(urnInInputData);
            if (urnNbnReserved != null) {
                logger.log(Level.INFO, "{0} was reserved", urnInInputData);
                removeFromReservedList(urnInInputData, rollback);
                return urnNbnReserved;
            } else {
                rollback.setUrnAssignedByResolverOrRegistrar(urnInInputData);
                return urnInInputData;
            }
        } else {//urn will be assigned by registrar
            logger.log(Level.INFO, "no urn found in import data, assigning new one");
            UrnNbn assignedByResolver = assignFreeUrnNbn();
            rollback.setUrnAssignedByResolverOrRegistrar(assignedByResolver);
            return assignedByResolver;
        }
    }

    private void checkUrnBelongsToRegistrar(UrnNbn urn) throws UrnNotFromRegistrarException {
        RegistrarCode code = data.getRegistrarCode();
        if (!urn.getRegistrarCode().equals(code)) {
            throw new UrnNotFromRegistrarException(code, urn);
        }
    }

    private void checkUrnIsFree(UrnNbn urn) throws UrnUsedException {
        try {
            factory.urnDao().getUrnNbnByRegistrarCodeAndDocumentCode(urn.getRegistrarCode(), urn.getDocumentCode());
            throw new UrnUsedException(urn);
        } catch (RecordNotFoundException ex) {
            //ok, urnNbn is free
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        }
    }

    private UrnNbn urnNbnReserved(UrnNbn urn) {
        try {
            return factory.urnReservedDao().getUrn(urn.getRegistrarCode(), urn.getDocumentCode());
            //when RecordNotFound is not thrown the urn:nbn is reserved
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            return null;
        }
    }

    private void removeFromReservedList(UrnNbn urn, RollbackRecord rollback) {
        try {
            factory.urnReservedDao().deleteUrn(urn);
            rollback.setUrnFromReservedList(urn);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    private UrnNbn assignFreeUrnNbn() {
        try {
            UrnNbn found = finder.findNewUrnNbn();
            logger.log(Level.INFO, "found free {0}", found);
            return found;
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        }
    }

    private long findOrImportIntelectualEntityWithRollback(RollbackRecord transactionLog) {
        IntelectualEntity iePresent = merger.getIntEntForMergingOrNull(data.getEntity());
        if (iePresent != null) {//suitable IE found
            logger.log(Level.INFO, "digital instance will be attached to existing intelectual entity with id {0}", iePresent.getId());
            return iePresent.getId();
        } else {//IE will be created
            logger.info("new intelectual entity will be created");
            long id = persistIntelectualEntityWithRollback(transactionLog);
            return id;
        }
    }

    private Long persistIntelectualEntityWithRollback(RollbackRecord transactionLog) {
        try {
            Long ieId = persistIntelectualEntity();
            logger.log(Level.INFO, "intelectual entity was imported with id {0}", ieId);
            transactionLog.setInsertedIntEntId(ieId);
            return ieId;
        } catch (Throwable ex) {
            logger.info("failed to import intelectual entity, rolling back");
            rollbackTransaction(transactionLog);
            throw new RuntimeException(ex);
        }
    }

    private long persistDigDocWithRollback(RollbackRecord transactionLog, long ieId) throws UnknownArchiverException {
        try {
            Long digRepId = persistDigitalDocument(ieId);
            logger.log(Level.INFO, "digital document was imported with id {0}", digRepId);
            transactionLog.setDigDocId(digRepId);
            return digRepId;
        } catch (UnknownArchiverException ex) {
            logger.log(Level.INFO, "failed to import digital document, rolling back");
            rollbackTransaction(transactionLog);
            throw ex;
        } catch (Throwable ex) {
            logger.info("failed to import digital document, rolling back");
            rollbackTransaction(transactionLog);
            throw new RuntimeException(ex);
        }
    }

    private void persistDigDocIdentifiersWithRollback(RollbackRecord transactionLog, long digRepId) throws RegistarScopeIdentifierCollisionException {
        try {
            List<RegistrarScopeIdentifier> ids = persistDigDocIdentifiers(digRepId);
            logger.log(Level.INFO, "digital document identifiers inserted: {0}", digRepIdListToString(ids));
        } catch (RegistarScopeIdentifierCollisionException ex) {
            //no need to specifically remove identifiers so far imported 
            //because it will be removed together with registrar in cascade
            logger.info("failed to import digital document identifiers, rolling back");
            rollbackTransaction(transactionLog);
            throw ex;
        } catch (Throwable ex) {
            logger.info("failed to import digital document identifiers, rolling back");
            rollbackTransaction(transactionLog);
            throw new RuntimeException(ex);
        }
    }

    private void persistUrnNbnWithRollback(UrnNbn urn, RollbackRecord transactionLog, long digRepId) {
        try {
            persistUrnNbn(urn, digRepId);
            logger.log(Level.INFO, "{0} was inserted", urn);
        } catch (Throwable ex) {
            logger.log(Level.INFO, "failed to insert {0}, rolling back", urn);
            rollbackTransaction(transactionLog);
            throw new RuntimeException(ex);
        }
    }

    private void persistUrnNbnPredecessorsWithRollback(UrnNbn urn, RollbackRecord transactionLog) {
        deactivateActivePredecessors(urn, transactionLog);
        try {
            persistPredecessors(data.getPredecessors(), urn);
            logger.log(Level.INFO, "{0} predecessors of {1} inserted", new Object[]{data.getPredecessors().size(), urn.toString()});
        } catch (Throwable ex) {
            logger.log(Level.INFO, "failed to insert {0} predecessors of {1}, rolling back", new Object[]{data.getPredecessors().size(), urn.toString()});
            rollbackTransaction(transactionLog);
            throw new RuntimeException(ex);
        }
    }

    private Long persistIntelectualEntity() throws DatabaseException, RecordNotFoundException, AlreadyPresentException {
        Long ieId = factory.intelectualEntityDao().insertIntelectualEntity(data.getEntity());
        for (IntEntIdentifier id : data.getIntEntIds()) {
            id.setIntEntDbId(ieId);
            factory.intEntIdentifierDao().insertIntEntId(id);
        }
        Originator originator = data.getOriginator();
        if (originator != null) {
            originator.setIntEntId(ieId);
            factory.originatorDao().insertOriginator(originator);
        }
        Publication publication = data.getPublication();
        if (publication != null) {
            publication.setIntEntId(ieId);
            factory.publicationDao().insertPublication(publication);
        }
        SourceDocument sourceDoc = data.getSourceDoc();
        if (sourceDoc != null) {
            sourceDoc.setIntEntId(ieId);
            factory.srcDocDao().insertSrcDoc(sourceDoc);
        }
        return ieId;
    }

    private Long persistDigitalDocument(long ieId) throws DatabaseException, RecordNotFoundException, UnknownArchiverException {
        DigitalDocument digDoc = data.getDigitalDocument();
        digDoc.setIntEntId(ieId);
        try {
            return factory.documentDao().insertDocument(data.getDigitalDocument());
        } catch (RecordNotFoundException e) {
            if (e.getTableName().equals(ArchiverDAO.TABLE_NAME)) {
                logger.log(Level.INFO, "unkown archiver with id {0}", digDoc.getArchiverId());
                throw new UnknownArchiverException(digDoc.getArchiverId());
            } else {
                throw e;
            }
        }
    }

    private List<RegistrarScopeIdentifier> persistDigDocIdentifiers(long digRepId) throws RegistarScopeIdentifierCollisionException, DatabaseException, RecordNotFoundException {
        Registrar registrar = factory.registrarDao().getRegistrarByCode(data.getRegistrarCode());
        List<RegistrarScopeIdentifier> result = new ArrayList<RegistrarScopeIdentifier>();
        for (RegistrarScopeIdentifier id : data.getDigDogIdentifiers()) {
            id.setDigDocId(digRepId);
            id.setRegistrarId(registrar.getId());
            try {
                factory.digDocIdDao().insertRegistrarScopeId(id);
                result.add(id);
            } catch (AlreadyPresentException ex) {
                logger.log(Level.SEVERE, "identifier collision for {0}", id);
                throw new RegistarScopeIdentifierCollisionException(registrar, id);
            }
        }
        return result;
    }

    private void persistUrnNbn(UrnNbn urn, long digDocId) throws DatabaseException, AlreadyPresentException, RecordNotFoundException {
        UrnNbn withDigDocId = new UrnNbn(urn.getRegistrarCode(), urn.getDocumentCode(), digDocId, urn.getReserved());
        factory.urnDao().insertUrnNbn(withDigDocId);
    }

    private void persistPredecessors(List<UrnNbnWithStatus> predecessors, UrnNbn successor) throws DatabaseException {
        for (UrnNbnWithStatus predecessor : predecessors) {
            try {
                factory.urnDao().insertUrnNbnPredecessor(predecessor.getUrn(), successor, predecessor.getNote());
            } catch (RecordNotFoundException e) {
                logger.log(Level.WARNING, "{0} or {1} doesn't exist", new Object[]{predecessor.getUrn().toString(), successor.toString()});
            } catch (AlreadyPresentException e) {
                logger.log(Level.WARNING, "Predecessor - successor relation {0} - {1} already present, ignoring", new Object[]{predecessor.getUrn().toString(), successor.toString()});
            }
        }
    }

    private void rollbackTransaction(RollbackRecord rollback) {
        if (rollback.getUrnAssignedByResolverOrRegistrar() != null) {
            UrnNbn urnNbn = rollback.getUrnAssignedByResolverOrRegistrar();
            logger.log(Level.INFO, "removing predecessors of {0}", urnNbn);
            removePredecessors(urnNbn);
            logger.log(Level.INFO, "removing {0}", urnNbn.toString());
            removeInsertedUrnNbn(urnNbn);
            if (!rollback.getPredecessorsDeactivated().isEmpty()) {
                reactivateUrnNbns(rollback.getPredecessorsDeactivated(), urnNbn);
            }
        }
        if (rollback.getUrnFromReservedList() != null) {
            UrnNbn urnNbn = rollback.getUrnFromReservedList();
            logger.log(Level.INFO, "removing predecessors of {0}", urnNbn);
            removePredecessors(urnNbn);
            logger.log(Level.INFO, "returning reserved {0}", urnNbn.toString());
            putBackToReservedTable(urnNbn);
            if (!rollback.getPredecessorsDeactivated().isEmpty()) {
                reactivateUrnNbns(rollback.getPredecessorsDeactivated(), urnNbn);
            }
        }
        if (rollback.getDigDocId() != null) {
            logger.info("removing created digital document");
            removeInsertedDigitalDocument(rollback.getDigDocId());
        }
        if (rollback.getInsertedIntEntId() != null) {
            logger.info("removing created intelectual entity");
            removeInsertedIntelectualEntity(rollback.getInsertedIntEntId());
        }
    }

    private void removeInsertedUrnNbn(UrnNbn urn) {
        try {
            factory.urnDao().deleteUrnNbn(urn.getRegistrarCode(), urn.getDocumentCode());
        } catch (Throwable ex) {
            logger.log(Level.SEVERE, "rollback: Failed to remove " + urn.toString(), ex);
        }
    }

    private void putBackToReservedTable(UrnNbn urn) {
        try {
            Registrar registrar = factory.registrarDao().getRegistrarByCode(urn.getRegistrarCode());
            factory.urnReservedDao().insertUrnNbn(urn, registrar.getId());
        } catch (DatabaseException ex) {
            logger.log(Level.SEVERE, "rollback: Failed insert " + urn + " into the reserved table", ex);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.SEVERE, "rollback: Failed insert " + urn + " into the reserved table", ex);
        } catch (AlreadyPresentException ex) {
            logger.log(Level.SEVERE, "rollback: Failed insert " + urn + " into the reserved table", ex);
        }
    }

    private void removeInsertedDigitalDocument(Long digRepId) {
        try {
            factory.documentDao().deleteDocument(digRepId);
        } catch (Throwable ex) {
            logger.log(Level.SEVERE, "rollback: Failed to remove digital document with id " + digRepId, ex);
        }
    }

    private void removeInsertedIntelectualEntity(Long ieId) {
        try {
            factory.intelectualEntityDao().deleteEntity(ieId);
        } catch (Throwable ex) {
            logger.log(Level.SEVERE, "rollback: Failed to remove intelectual entity with id " + ieId, ex);
        }
    }

    private String digRepIdListToString(List<RegistrarScopeIdentifier> ids) {
        StringBuilder builder = new StringBuilder();
        builder.append('{');
        for (int i = 0; i < ids.size(); i++) {
            RegistrarScopeIdentifier id = ids.get(i);
            builder.append('\'').append(id.getType()).append("':'").append(id.getValue()).append('\'');
            if (i < ids.size() - 1) {
                builder.append(",");
            }
        }
        builder.append('}');
        return builder.toString();
    }

    private void removePredecessors(UrnNbn urnNbn) {
        try {
            factory.urnDao().deletePredecessors(urnNbn);
        } catch (Throwable ex) {
            logger.log(Level.SEVERE, "rollback: Failed to remove predecessors of {0}", urnNbn.toString());
        }
    }

    private void reactivateUrnNbns(List<UrnNbn> predecessorsDeactivated, UrnNbn urn) {
        for (UrnNbn predecessor : predecessorsDeactivated) {
            try {
                logger.log(Level.INFO, "reactivating deactivated predecessor {0} of {1}", new Object[]{predecessor.toString(), urn.toString()});
                factory.urnDao().reactivateUrnNbn(predecessor.getRegistrarCode(), predecessor.getDocumentCode());
            } catch (Throwable ex) {
                logger.log(Level.SEVERE, "rollback: Failed to reactivat predecessor {0} of {1}", new Object[]{predecessor.toString(), urn.toString()});
            }
        }
    }

    private void deactivateActivePredecessors(UrnNbn urn, RollbackRecord transactionLog) {
        List<UrnNbn> predecessorsDeactivated = new ArrayList<UrnNbn>();
        for (UrnNbnWithStatus predecessor : data.getPredecessors()) {
            if (predecessor.getStatus() == Status.ACTIVE) {
                try {
                    UrnNbn urnDeactivated = predecessor.getUrn();
                    logger.log(Level.INFO, "deactivating {0} (predecessor of {1})", new Object[]{urnDeactivated, urn});
                    factory.urnDao().deactivateUrnNbn(urnDeactivated.getRegistrarCode(), urnDeactivated.getDocumentCode());
                    predecessorsDeactivated.add(urnDeactivated);
                } catch (DatabaseException ex) {
                    logger.log(Level.INFO, "error deactivating predecessor {0} of {1}", new Object[]{predecessor.toString(), urn.toString()});
                }
            }else{
                logger.log(Level.INFO, "predecessor {0} of {1} already deactivated", new Object[]{predecessor, urn});
            }
        }
        transactionLog.setPredecessorsDeactivated(predecessorsDeactivated);
    }

    private void checkPredecessorsFromSameRegistrar() throws UrnNotFromRegistrarException {
        List<UrnNbnWithStatus> predecessors = data.getPredecessors();
        for (UrnNbnWithStatus predecessor : predecessors) {
            checkUrnBelongsToRegistrar(predecessor.getUrn());
        }
    }
}
