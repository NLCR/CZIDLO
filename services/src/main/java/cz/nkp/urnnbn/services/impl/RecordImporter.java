/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.impl;

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

    RecordImporter(DAOFactory factory, RecordImport data, long userId) throws DatabaseException, UnknownRegistrarException {
        try {
            Registrar registrar = factory.registrarDao().getRegistrarBySigla(data.getRegistrarSigla());
            //TODO: zkontrolovat prava uzivatele k registratorovi
            finder = new UrnNbnFinder(factory, registrar);
        } catch (RecordNotFoundException ex) {
            throw new UnknownRegistrarException(data.getRegistrarSigla());
        }
        this.data = data;
        this.userId = userId;
        this.factory = factory;
        merger = new IntelectualEntityMerger(factory);
    }

    public UrnNbn run() throws AccessException, DatabaseException, UrnNotFromRegistrarException, UrnUsedException, ImportFailedException {
        synchronized (RecordImporter.class) {
            RollbackRecord rollback = new RollbackRecord();
            UrnNbn urn = urnToBeUsed(rollback);
            long ieId = findOrImportWithRollbackIntelectualEntity(rollback);
            long digRepId = importDigitalInstanceWithRollback(rollback, ieId);
            importUrnNbnWithRollback(urn, rollback, digRepId);
            return urn;
        }
    }

    private UrnNbn urnToBeUsed(RollbackRecord rollback) throws UrnNotFromRegistrarException, UrnUsedException, DatabaseException {
        UrnNbn urnInInputData = data.getUrn();
        if (urnInInputData != null) {
            //selected by registrar
            checkUrnBelongsToRegistrar(urnInInputData);
            checkUrnIsFree(urnInInputData);
            if (isBooked(urnInInputData)) {
                removeUrnFromBookedlist(urnInInputData, rollback);
            } else {
                rollback.setUrnAssignedByResolverOrRegistrar(urnInInputData);
            }
            return urnInInputData;
        } else {//urn will be assigned by registrar
            UrnNbn assignedByResolver = assignUrnNbn();
            rollback.setUrnAssignedByResolverOrRegistrar(assignedByResolver);
            return assignedByResolver;
        }
    }

    private void checkUrnBelongsToRegistrar(UrnNbn urn) throws DatabaseException, UrnNotFromRegistrarException {
        String sigla = data.getRegistrarSigla().toString();
        if (urn.getRegistrarCode().equals(sigla)) {
            throw new UrnNotFromRegistrarException(sigla, urn);
        }
    }

    private void checkUrnIsFree(UrnNbn urn) throws DatabaseException, UrnUsedException {
        try {
            factory.urnDao().getUrnNbnByRegistrarCodeAndDocumentCode(urn.getRegistrarCode(), urn.getDocumentCode());
            throw new UrnUsedException(urn);
        } catch (RecordNotFoundException ex) {
            //ok, urnNbn is free
        }
    }

    private boolean isBooked(UrnNbn urn) {
        //TODO: podivat se, jestli je v seznamu zamluvenych
        return false;
    }

    private void removeUrnFromBookedlist(UrnNbn urn, RollbackRecord rollback) {
        //TODO: actually removed from booked urn table
        rollback.setUrnFromBookedList(urn);
    }

    private UrnNbn assignUrnNbn() throws DatabaseException {
        return finder.findNewUrnNbn();
    }

    private long findOrImportWithRollbackIntelectualEntity(RollbackRecord rollback) throws ImportFailedException {
        IntelectualEntity iePresent = merger.getIntEntForMergingOrNull(data.getEntity());
        if (iePresent != null) {//suitable IE found
            logger.log(Level.INFO, "digital instance will be attached to existing intelectual entity");
            return iePresent.getId();
        } else {//IE will be created
            logger.log(Level.INFO, "new inelectual entity will be created");
            long id = importIntelectualEntityWithRollback(rollback);
            return id;
        }
    }

    private Long importIntelectualEntityWithRollback(RollbackRecord rollback) throws ImportFailedException {
        try {
            Long ieId = importIntelectualEntity(data);
            rollback.setInsertedIntEntId(ieId);
            return ieId;
        } catch (ImportFailedException ex) {
            rollbackTransaction(rollback);
            throw new ImportFailedException(ex.getMessage());
        }
    }

    private long importDigitalInstanceWithRollback(RollbackRecord rollback, long ieId) throws ImportFailedException {
        try {
            Long digRepId = importDigitalRepresentation(data, ieId);
            rollback.setDigRepId(digRepId);
            return digRepId;
        } catch (ImportFailedException ex) {
            rollbackTransaction(rollback);
            throw new ImportFailedException(ex.getMessage());
        }
    }

    private void importUrnNbnWithRollback(UrnNbn urn, RollbackRecord rollback, long digRepId) throws ImportFailedException {
        try {
            importUrnNbn(urn, digRepId);
        } catch (ImportFailedException ex) {
            rollbackTransaction(rollback);
            throw new ImportFailedException(ex.getMessage());
        }
    }

    private Long importIntelectualEntity(RecordImport data) throws ImportFailedException {
        Long ieId = null;
        try {
            ieId = factory.intelectualEntityDao().insertIntelectualEntity(data.getEntity());
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
        } catch (Throwable ex) {
            logger.log(Level.SEVERE, "Failed to import intelectual entity", ex);
            throw new ImportFailedException(ieId);
        }
    }

    private Long importDigitalRepresentation(RecordImport data, long ieId) throws ImportFailedException {
        Long digRepId = null;
        try {
            DigitalRepresentation digRep = data.getRepresentation();
            digRep.setIntEntId(ieId);
            Registrar registrar = factory.registrarDao().getRegistrarBySigla(data.getRegistrarSigla());
            digRep.setRegistrarId(registrar.getId());
            digRep.setArchiverId(data.getArchiverId() != null
                    ? data.getArchiverId() : registrar.getId());
            digRepId = factory.representationDao().insertRepresentation(data.getRepresentation());
            for (DigRepIdentifier id : data.getDigRepIds()) {
                id.setDigRepId(digRepId);
                id.setRegistrarId(registrar.getId());
                factory.digRepIdDao().insertDigRepId(id);
            }
            return digRepId;
        } catch (Throwable ex) {
            logger.log(Level.SEVERE, "Failed to import digital representation", ex);
            throw new ImportFailedException(digRepId);
        }
    }

    private void importUrnNbn(UrnNbn urn, long digRepId) throws ImportFailedException {
        try {
            UrnNbn withDigRepId = new UrnNbn(urn.getRegistrarCode(), urn.getDocumentCode(), digRepId);
            factory.urnDao().insertUrnNbn(withDigRepId);
        } catch (Throwable ex) {
            logger.log(Level.SEVERE, null, ex);
            throw new ImportFailedException();
        }
    }

    private void rollbackTransaction(RollbackRecord rollback) {
        if (rollback.getUrnAssignedByResolverOrRegistrar() != null) {
            removeInsertedUrnNbn(rollback.getUrnAssignedByResolverOrRegistrar());
        }
        if (rollback.getUrnFromBookedList() != null) {
            putBackToBookedTable(rollback.getUrnFromBookedList());
        }
        if (rollback.getDigRepId() != null) {
            removeInsertedDigitalRepresentation(rollback.getDigRepId());
        }
        if (rollback.getInsertedIntEntId() != null) {
            removeInsertedIntelectualEntity(rollback.getInsertedIntEntId());
        }
    }

    private void removeInsertedUrnNbn(UrnNbn urn) {
        try {
            factory.urnDao().deleteUrnNbn(urn);
        } catch (Throwable ex) {
            logger.log(Level.SEVERE, "Rollback: Failed to remove " + urn.toString(), ex);
        }
    }

    private void putBackToBookedTable(UrnNbn urnFromBookedList) {
        //TODO
    }

    private void removeInsertedDigitalRepresentation(Long digRepId) {
        try {
            factory.representationDao().deleteRepresentation(digRepId);
        } catch (Throwable ex) {
            logger.log(Level.SEVERE, "Rollback: Failed to remove digital representation with id " + digRepId, ex);
        }
    }

    private void removeInsertedIntelectualEntity(Long ieId) {
        try {
            factory.intelectualEntityDao().deleteEntity(ieId);
        } catch (Throwable ex) {
            logger.log(Level.SEVERE, "Rollback: Failed to remove intelectual entity with id " + ieId, ex);
        }
    }
}
