/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.impl;

import cz.nkp.urnnbn.core.Sigla;
import cz.nkp.urnnbn.core.persistence.DAOFactory;
import cz.nkp.urnnbn.core.dto.DigRepIdentifier;
import cz.nkp.urnnbn.core.dto.DigitalRepresentation;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.SourceDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.services.DataImportService;
import cz.nkp.urnnbn.services.RecordImport;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.ImportFailedException;
import cz.nkp.urnnbn.services.exceptions.UnknownRegistrarException;
import cz.nkp.urnnbn.services.exceptions.UrnNotFromRegistrarException;
import cz.nkp.urnnbn.services.exceptions.UrnUsedException;
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
    private final long userId;
    private final IntelectualEntityMerger merger;
    private final UrnNbnFinder finder;

    RecordImporter(DAOFactory factory, RecordImport data, long userId) throws UnknownRegistrarException {
        this.data = data;
        this.userId = userId;
        this.factory = factory;
        this.merger = new IntelectualEntityMerger(factory);
        this.finder = initFinder(factory, data);
    }

    private UrnNbnFinder initFinder(DAOFactory factory, RecordImport data) throws UnknownRegistrarException {
        try {
            Registrar registrar = factory.registrarDao().getRegistrarBySigla(data.getRegistrarSigla());
            return new UrnNbnFinder(factory, registrar);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (RecordNotFoundException ex) {
            throw new UnknownRegistrarException(data.getRegistrarSigla());
        }
    }

    public UrnNbn run() throws AccessException, UrnNotFromRegistrarException, UrnUsedException {
        synchronized (RecordImporter.class) {
            RollbackRecord transactionLog = new RollbackRecord();
            UrnNbn urn = urnToBeUsed(transactionLog);
            long ieId = findOrImportWithRollbackIntelectualEntity(transactionLog);
            long digRepId = importDigitalRepersentationWithRollback(transactionLog, ieId);
            importUrnNbnWithRollback(urn, transactionLog, digRepId);
            return urn;
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
            logger.log(Level.INFO, "no urn found in import data, assigning");
            UrnNbn assignedByResolver = assignUrnNbn();
            rollback.setUrnAssignedByResolverOrRegistrar(assignedByResolver);
            return assignedByResolver;
        }
    }

    private void checkUrnBelongsToRegistrar(UrnNbn urn) throws UrnNotFromRegistrarException {
        String sigla = data.getRegistrarSigla().toString();
        if (!urn.getRegistrarCode().equals(sigla)) {
            throw new UrnNotFromRegistrarException(sigla, urn);
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
        Sigla sigla = Sigla.valueOf(urn.getRegistrarCode());
        try {
            factory.urnReservedDao().getUrn(sigla, urn.getDocumentCode());
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

    private UrnNbn assignUrnNbn() {
        try {
            return finder.findNewUrnNbn();
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
            Long ieId = importIntelectualEntity(data);
            logger.log(Level.INFO, "intelectual entity was imported with id {0}", ieId);
            transactionLog.setInsertedIntEntId(ieId);
            return ieId;
        } catch (Throwable ex) {
            logger.info("failed to import intelectual entity, rolling back");
            rollbackTransaction(transactionLog);
            throw new RuntimeException(ex);
        }
    }

    private long importDigitalRepersentationWithRollback(RollbackRecord transactionLog, long ieId) {
        try {
            Long digRepId = importDigitalRepresentation(data, ieId);
            logger.log(Level.INFO, "digital representation was imported with id {0}", digRepId);
            transactionLog.setDigRepId(digRepId);
            return digRepId;
        } catch (Throwable ex) {
            logger.info("failed to import digital representation, rolling back");
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

    private Long importIntelectualEntity(RecordImport data) throws DatabaseException, RecordNotFoundException, AlreadyPresentException {
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

    private Long importDigitalRepresentation(RecordImport data, long ieId) throws ImportFailedException, DatabaseException, RecordNotFoundException, AlreadyPresentException {
        DigitalRepresentation digRep = data.getRepresentation();
        digRep.setIntEntId(ieId);
        Registrar registrar = factory.registrarDao().getRegistrarBySigla(data.getRegistrarSigla());
        digRep.setRegistrarId(registrar.getId());
        digRep.setArchiverId(data.getArchiverId() != null
                ? data.getArchiverId() : registrar.getId());
        Long digRepId = factory.representationDao().insertRepresentation(data.getRepresentation());
        for (DigRepIdentifier id : data.getDigRepIds()) {
            id.setDigRepId(digRepId);
            id.setRegistrarId(registrar.getId());
            factory.digRepIdDao().insertDigRepId(id);
        }
        return digRepId;
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
            logger.info("removing created digital representation");
            removeInsertedDigitalRepresentation(rollback.getDigRepId());
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
            Sigla sigla = Sigla.valueOf(urn.getRegistrarCode());
            Registrar registrar = factory.registrarDao().getRegistrarBySigla(sigla);
            factory.urnReservedDao().insertUrnNbn(urn, registrar.getId());
        } catch (DatabaseException ex) {
            logger.log(Level.SEVERE, "rollback: Failed insert " + urn + " into the reserved table", ex);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.SEVERE, "rollback: Failed insert " + urn + " into the reserved table", ex);
        } catch (AlreadyPresentException ex) {
            logger.log(Level.SEVERE, "rollback: Failed insert " + urn + " into the reserved table", ex);
        }
    }

    private void removeInsertedDigitalRepresentation(Long digRepId) {
        try {
            factory.representationDao().deleteRepresentation(digRepId);
        } catch (Throwable ex) {
            logger.log(Level.SEVERE, "rollback: Failed to remove digital representation with id " + digRepId, ex);
        }
    }

    private void removeInsertedIntelectualEntity(Long ieId) {
        try {
            factory.intelectualEntityDao().deleteEntity(ieId);
        } catch (Throwable ex) {
            logger.log(Level.SEVERE, "rollback: Failed to remove intelectual entity with id " + ieId, ex);
        }
    }
}
