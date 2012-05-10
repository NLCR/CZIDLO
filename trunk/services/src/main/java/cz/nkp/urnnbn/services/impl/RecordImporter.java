/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.impl;

import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.persistence.DAOFactory;
import cz.nkp.urnnbn.core.dto.DigDocIdentifier;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.SourceDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.ArchiverDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.services.DataImportService;
import cz.nkp.urnnbn.services.RecordImport;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.DigDocIdentifierCollisionException;
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
public class RecordImporter {

    private static final Logger logger = Logger.getLogger(DataImportService.class.getName());
    private final DAOFactory factory;
    private final RecordImport data;
    private final IntelectualEntityMerger merger;
    private final UrnNbnFinder finder;

    RecordImporter(DAOFactory factory, RecordImport data) throws UnknownRegistrarException {
        this.data = data;
        this.factory = factory;
        this.merger = new IntelectualEntityMerger(factory);
        this.finder = initFinder(factory, data);
    }

    private UrnNbnFinder initFinder(DAOFactory factory, RecordImport data) throws UnknownRegistrarException {
        try {
            Registrar registrar = factory.registrarDao().getRegistrarByCode(data.getRegistrarCode());
            return new UrnNbnFinder(factory, registrar);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            throw new UnknownRegistrarException(data.getRegistrarCode());
        }
    }

    public UrnNbn run() throws AccessException, UrnNotFromRegistrarException, UrnUsedException, DigDocIdentifierCollisionException, UnknownArchiverException {
        synchronized (RecordImporter.class) {
            RollbackRecord transactionLog = new RollbackRecord();
            UrnNbn urn = urnToBeUsed(transactionLog);
            long ieId = findOrImportWithRollbackIntelectualEntity(transactionLog);
            long digDocId = importDigitalRepersentationWithRollback(transactionLog, ieId);
            importDigDocIdentifiersWithRollback(transactionLog, digDocId);
            importUrnNbnWithRollback(urn, transactionLog, digDocId);
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
            logger.log(Level.INFO, "urn found in import data: {0}", urnInInputData);
            //selected by registrar
            checkUrnBelongsToRegistrar(urnInInputData);
            checkUrnIsFree(urnInInputData);
            if (isReserved(urnInInputData)) {
                logger.info("urn was reserved");
                removeFromReservedList(urnInInputData, rollback);
            } else {
                rollback.setUrnAssignedByResolverOrRegistrar(urnInInputData);
            }
            return urnInInputData;
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

    private boolean isReserved(UrnNbn urn) {
        try {
            factory.urnReservedDao().getUrn(urn.getRegistrarCode(), urn.getDocumentCode());
            //when RecordNotFound is not thrown the urn:nbn is reserved
            return true;
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            return false;
        }
    }

    private void removeFromReservedList(UrnNbn urn, RollbackRecord rollback) {
        try {
            factory.urnReservedDao().deleteUrn(urn);
            rollback.setUrnFromBookedList(urn);
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

    private long findOrImportWithRollbackIntelectualEntity(RollbackRecord transactionLog) {
        IntelectualEntity iePresent = merger.getIntEntForMergingOrNull(data.getEntity());
        if (iePresent != null) {//suitable IE found
            logger.log(Level.INFO, "digital instance will be attached to existing intelectual entity with id {0}", iePresent.getId());
            return iePresent.getId();
        } else {//IE will be created
            logger.info("new intelectual entity will be created");
            long id = importIntelectualEntityWithRollback(transactionLog);
            return id;
        }
    }

    private Long importIntelectualEntityWithRollback(RollbackRecord transactionLog) {
        try {
            Long ieId = importIntelectualEntity();
            logger.log(Level.INFO, "intelectual entity was imported with id {0}", ieId);
            transactionLog.setInsertedIntEntId(ieId);
            return ieId;
        } catch (Throwable ex) {
            logger.info("failed to import intelectual entity, rolling back");
            rollbackTransaction(transactionLog);
            throw new RuntimeException(ex);
        }
    }

    private long importDigitalRepersentationWithRollback(RollbackRecord transactionLog, long ieId) throws UnknownArchiverException {
        try {
            Long digRepId = importDigitalDocument(ieId);
            logger.log(Level.INFO, "digital document was imported with id {0}", digRepId);
            transactionLog.setDigRepId(digRepId);
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

    private void importDigDocIdentifiersWithRollback(RollbackRecord transactionLog, long digRepId) throws DigDocIdentifierCollisionException {
        try {
            List<DigDocIdentifier> ids = importDigRepIdentifiers(digRepId);
            logger.log(Level.INFO, "digital document identifiers inserted: {0}", digRepIdListToString(ids));
        } catch (DigDocIdentifierCollisionException ex) {
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

    private void importUrnNbnWithRollback(UrnNbn urn, RollbackRecord transactionLog, long digRepId) {
        try {
            importUrnNbn(urn, digRepId);
            logger.log(Level.INFO, "{0} was inserted", urn);
        } catch (Throwable ex) {
            logger.log(Level.INFO, "failed to insert {0}, rolling back", urn);
            rollbackTransaction(transactionLog);
            throw new RuntimeException(ex);
        }
    }

    private Long importIntelectualEntity() throws DatabaseException, RecordNotFoundException, AlreadyPresentException {
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

    private Long importDigitalDocument(long ieId) throws DatabaseException, RecordNotFoundException, UnknownArchiverException {
        DigitalDocument digRep = data.getDigitalDocument();
        digRep.setIntEntId(ieId);
        try {
            return factory.documentDao().insertDocument(data.getDigitalDocument());
        } catch (RecordNotFoundException e) {
            if (e.getTableName().equals(ArchiverDAO.TABLE_NAME)) {
                logger.log(Level.INFO, "unkown archiver with id {0}", digRep.getArchiverId());
                throw new UnknownArchiverException(digRep.getArchiverId());
            } else {
                throw e;
            }
        }
    }

    private List<DigDocIdentifier> importDigRepIdentifiers(long digRepId) throws DigDocIdentifierCollisionException, DatabaseException, RecordNotFoundException {
        Registrar registrar = factory.registrarDao().getRegistrarByCode(data.getRegistrarCode());
        List<DigDocIdentifier> result = new ArrayList<DigDocIdentifier>();
        for (DigDocIdentifier id : data.getDigDogIdentifiers()) {
            id.setDigDocId(digRepId);
            id.setRegistrarId(registrar.getId());
            try {
                factory.digDocIdDao().insertDigDocId(id);
                result.add(id);
            } catch (AlreadyPresentException ex) {
                logger.log(Level.SEVERE, "identifier collision for {0}", id);
                throw new DigDocIdentifierCollisionException(registrar, id);
            }
        }
        return result;
    }

    private void importUrnNbn(UrnNbn urn, long digRepId) throws DatabaseException, AlreadyPresentException, RecordNotFoundException {
        UrnNbn withDigRepId = new UrnNbn(urn.getRegistrarCode(), urn.getDocumentCode(), digRepId);
        factory.urnDao().insertUrnNbn(withDigRepId);
    }

    private void rollbackTransaction(RollbackRecord rollback) {
        if (rollback.getUrnAssignedByResolverOrRegistrar() != null) {
            logger.info("removing assigned urn:nbn");
            removeInsertedUrnNbn(rollback.getUrnAssignedByResolverOrRegistrar());
        }
        if (rollback.getUrnFromReservedList() != null) {
            logger.info("returning reserved urn:nbn");
            putBackToReservedTable(rollback.getUrnFromReservedList());
        }
        if (rollback.getDigRepId() != null) {
            logger.info("removing created digital document");
            removeInsertedDigitalDocument(rollback.getDigRepId());
        }
        if (rollback.getInsertedIntEntId() != null) {
            logger.info("removing created intelectual entity");
            removeInsertedIntelectualEntity(rollback.getInsertedIntEntId());
        }
    }

    private void removeInsertedUrnNbn(UrnNbn urn) {
        try {
            factory.urnDao().deleteUrnNbn(urn);
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

    private String digRepIdListToString(List<DigDocIdentifier> ids) {
        StringBuilder builder = new StringBuilder();
        builder.append('{');
        for (int i = 0; i < ids.size(); i++) {
            DigDocIdentifier id = ids.get(i);
            builder.append('\'').append(id.getType()).append("':'").append(id.getValue()).append('\'');
            if (i < ids.size() - 1) {
                builder.append(",");
            }
        }
        builder.append('}');
        return builder.toString();
    }
}
