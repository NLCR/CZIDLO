package cz.nkp.urnnbn.czidlo_web_api.api.documents;

import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.dto.*;
import cz.nkp.urnnbn.czidlo_web_api.api.archivers.core.Archiver;
import cz.nkp.urnnbn.czidlo_web_api.api.documents.core.*;
import cz.nkp.urnnbn.czidlo_web_api.api.documents.core.Record;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.BadArgumentException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.ConflictException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.InsufficientRightsException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnknownRecordException;
import cz.nkp.urnnbn.czidlo_web_api.api.registrars.core.Registrar;
import cz.nkp.urnnbn.services.*;
import cz.nkp.urnnbn.services.exceptions.*;
import jakarta.ws.rs.BadRequestException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class DocumentManagerImpl implements DocumentManager {

    protected DataAccessService dataAccessService() {
        return Services.instanceOf().dataAccessService();
    }

    protected DataImportService dataImportService() {
        return Services.instanceOf().dataImportService();
    }

    protected UrnNbnReservationService urnReservationService() {
        return Services.instanceOf().urnReservationService();
    }

    protected DataRemoveService dataRemoveService() {
        return Services.instanceOf().dataRemoveService();
    }

    protected DataUpdateService dataUpdateService() {
        return Services.instanceOf().dataUpdateService();
    }

    protected StatisticService statisticService() {
        return Services.instanceOf().statisticService();
    }


    @Override
    public Record getRecord(UrnNbn urnNbn) {
        //URN:NBN
        UrnNbnWithStatus urnNbnWithStatus = dataAccessService().urnByRegistrarCodeAndDocumentCode(urnNbn.getRegistrarCode(), urnNbn.getDocumentCode(), true);
        if (urnNbnWithStatus == null) {
            return null;
        }
        Urn urn = Urn.from(urnNbnWithStatus);
        if (urnNbnWithStatus.getStatus() == UrnNbnWithStatus.Status.FREE
                || urnNbnWithStatus.getStatus() == UrnNbnWithStatus.Status.RESERVED) {
            return null;
        }
        //DIGITAL DOCUMENT
        DigDoc doc = null;
        IntEnt entity = null;
        Registrar registrar = null;
        Archiver archiver = null;
        List<RsId> rsIds = null;
        List<DigInst> digitalInstances = null;
        Long digDocId = urnNbnWithStatus.getUrn().getDigDocId();
        if (digDocId != null) {
            DigitalDocument digDoc = dataAccessService().digDocByInternalId(digDocId);
            doc = DigDoc.from(digDoc);
            //INTELECTUAL ENTITY
            IntelectualEntity ie = dataAccessService().entityById(digDoc.getIntEntId());
            if (ie != null) {
                Orig originator = Orig.from(dataAccessService().originatorByIntEntId(ie.getId()));
                Publ publication = Publ.from(dataAccessService().publicationByIntEntId(ie.getId()));
                SrcDoc srcDoc = SrcDoc.from(dataAccessService().sourceDocumentByIntEntId(ie.getId()));
                List<IeId> ieIds = IeId.fromlist(dataAccessService().intEntIdentifiersByIntEntId(ie.getId()));
                entity = IntEnt.from(ie, originator, publication, srcDoc, ieIds);
            }
            registrar = Registrar.from(dataAccessService().registrarById(digDoc.getRegistrarId()), null, null);
            if (digDoc.getArchiverId() != null && !Objects.equals(digDoc.getRegistrarId(), digDoc.getArchiverId())) {
                archiver = Archiver.fromDto(dataAccessService().archiverById(digDoc.getArchiverId()));
            }
            rsIds = RsId.fromList(dataAccessService().registrarScopeIdentifiers(digDocId));
            List<DigitalInstance> dtoDis = dataAccessService().digInstancesByDigDocId(digDocId);
            if (dtoDis == null || dtoDis.isEmpty()) {
                digitalInstances = List.of();
            } else {
                digitalInstances = new ArrayList<>();
                for (DigitalInstance di : dtoDis) {
                    DigitalLibrary digitalLibrary = dataAccessService().libraryByInternalId(di.getLibraryId());
                    cz.nkp.urnnbn.core.dto.Registrar registrarOfDi = dataAccessService().registrarById(digitalLibrary.getRegistrarId());
                    digitalInstances.add(DigInst.from(di, digitalLibrary, registrarOfDi.getCode().toString()));
                }
            }
        }
        return Record.from(urn, doc, entity, registrar, archiver, rsIds, digitalInstances);
    }

    @Override
    public boolean deactivateRecord(UrnNbn urnNbn, String note, String loginOfUserPerformingOperation) throws UnknownRecordException, InsufficientRightsException {
        UrnNbnWithStatus urnNbnWithStatus = dataAccessService().urnByRegistrarCodeAndDocumentCode(urnNbn.getRegistrarCode(), urnNbn.getDocumentCode(), true);
        if (urnNbnWithStatus == null || urnNbnWithStatus.getStatus() == UrnNbnWithStatus.Status.FREE
                || urnNbnWithStatus.getStatus() == UrnNbnWithStatus.Status.RESERVED) {
            throw new UnknownRecordException("Digital document with URN:NBN " + urnNbn + " not found");
        }
        if (urnNbnWithStatus.getStatus() == UrnNbnWithStatus.Status.DEACTIVATED) {
            return false; // already deactivated
        }
        try {
            dataUpdateService().deactivateUrnNbn(urnNbnWithStatus.getUrn(), loginOfUserPerformingOperation, note);
            return true; //deactivated now
        } catch (UnknownUserException e) {
            throw new RuntimeException(e);
        } catch (AccessException e) {
            throw new InsufficientRightsException("User with login " + loginOfUserPerformingOperation + " has insufficient rights to deactivate record with URN:NBN " + urnNbn);
        } catch (UnknownDigDocException e) {
            throw new UnknownRecordException("Digital document with URN:NBN " + urnNbn + " not found");
        }
    }

    @Override
    public boolean reactivateRecord(UrnNbn urnNbn, String login) throws UnknownRecordException, InsufficientRightsException {
        UrnNbnWithStatus urnNbnWithStatus = dataAccessService().urnByRegistrarCodeAndDocumentCode(urnNbn.getRegistrarCode(), urnNbn.getDocumentCode(), true);
        if (urnNbnWithStatus == null || urnNbnWithStatus.getStatus() == UrnNbnWithStatus.Status.FREE
                || urnNbnWithStatus.getStatus() == UrnNbnWithStatus.Status.RESERVED) {
            throw new UnknownRecordException("Digital document with URN:NBN " + urnNbn + " not found");
        }
        if (urnNbnWithStatus.getStatus() == UrnNbnWithStatus.Status.ACTIVE) {
            return false; // already active
        }
        try {
            dataUpdateService().reactivateUrnNbn(urnNbnWithStatus.getUrn(), login);
            return true; // reactivated now
        } catch (UnknownUserException e) {
            throw new RuntimeException(e);
        } catch (AccessException e) {
            throw new InsufficientRightsException("User with login " + login + " has insufficient rights to reactivate record with URN:NBN " + urnNbn);
        } catch (UnknownDigDocException e) {
            throw new UnknownRecordException("Digital document with URN:NBN " + urnNbn + " not found");
        }
    }

    @Override
    public UrnNbn createRecord(RecordToBeCreatedOrUpdated record, String login) throws
            BadArgumentException, UnknownUserException, RegistrarScopeIdentifierCollisionException, UnknownArchiverException, ArchiverIsRegistrarException,
            IncorrectPredecessorStatus, UnknownRecordException, InsufficientRightsException {
        try {
            DigDocRegistrationData docData = convert(record);
            return dataImportService().registerDigitalDocument(docData, login);
        } catch (UnknownRegistrarException e) {
            throw new UnknownRecordException("Unknown registrar: " + e.getMessage());
        } catch (AccessException e) {
            throw new InsufficientRightsException("Insufficient rights: " + e.getMessage());
        } catch (UrnNotFromRegistrarException e) {
            throw new BadArgumentException("URN:NBN not matching registrar: " + e.getMessage());
        } catch (UrnUsedException e) {
            throw new BadArgumentException("URN:NBN already used: " + e.getMessage());
        } catch (RegistrationModeNotAllowedException e) {
            throw new BadArgumentException("Registration mode not allowed: " + e.getMessage());
        }
    }

    @Override
    public void updateRecord(RecordToBeCreatedOrUpdated recordToBeUpdated, String login) throws InsufficientRightsException, UnknownRecordException {
        DigDocRegistrationData docData = convert(recordToBeUpdated);
        try {
            dataUpdateService().updateIntelectualEntity(docData.getEntity(), docData.getOriginator(), docData.getPublication(), docData.getSourceDoc(), docData.getIntEntIds(), login);
            dataUpdateService().updateDigitalDocument(docData.getDigitalDocument(), login);
        } catch (UnknownUserException e) {
            throw new RuntimeException(e);
        } catch (AccessException e) {
            throw new InsufficientRightsException("Insufficient rights: " + e.getMessage());
        } catch (UnknownIntelectualEntity e) {
            throw new UnknownRecordException("Unknown intellectual entity: " + e.getMessage());
        } catch (UnknownDigDocException e) {
            throw new UnknownRecordException("Unknown digital document: " + e.getMessage());
        }
    }

    @Override
    public void addPredecessorSuccessorRelation(UrnNbn predecessorIn, UrnNbn successorIn, String note, String login) throws UnknownRecordException, InsufficientRightsException, IncorrectPredecessorStatus, ConflictException {
        UrnNbnWithStatus predecessorWithStatus = dataAccessService().urnByRegistrarCodeAndDocumentCode(predecessorIn.getRegistrarCode(), predecessorIn.getDocumentCode(), true);
        UrnNbnWithStatus successorWithStatus = dataAccessService().urnByRegistrarCodeAndDocumentCode(successorIn.getRegistrarCode(), successorIn.getDocumentCode(), true);
        UrnNbn predecessor = predecessorWithStatus.getUrn();
        UrnNbn successor = successorWithStatus.getUrn();
        detectCycle(predecessorWithStatus, successorWithStatus);
        switch (predecessorWithStatus.getStatus()) {
            case FREE:
            case RESERVED:
                throw new UnknownRecordException("document " + predecessor + " not found");
        }
        switch (successorWithStatus.getStatus()) {
            case FREE:
            case RESERVED:
                throw new UnknownRecordException("document " + successor + " not found");
        }
        try {
            dataUpdateService().addRelationPredecessorSuccessor(predecessor, successor, note, login);
            dataUpdateService().deactivateUrnNbn(predecessor, login, null);
        } catch (UnknownUserException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (NotAdminException e) {
            throw new InsufficientRightsException("Insufficient rights: " + e.getMessage());
        } catch (AccessException e) {
            throw new InsufficientRightsException("Insufficient rights: " + e.getMessage());
        } catch (UnknownDigDocException e) {
            throw new UnknownRecordException("Unknown digital document: " + e.getMessage());
        }
    }

    /**
     * Checks that adding new predecessor -> successor relation doesn't create a cycle in predecessor -> successor relations graph.
     * Example: U -> u1 -> u2 -> ... -> un -> P. Now adding P -> U would create a cycle.
     *
     * @param newPredecessor the predecessor that would be added to urnNbn
     * @param urnNbn         the successor to which newPredecessor would be added as predecessor
     * @throws BadArgumentException if there would be a cycle
     */
    private void detectCycle(UrnNbnWithStatus newPredecessor, UrnNbnWithStatus urnNbn) throws ConflictException {
        System.out.println("Searching for " + urnNbn.getUrn() + " -> ... -> " + newPredecessor.getUrn());
        List<UrnNbnWithStatus> toBeChecked = new ArrayList<>();
        toBeChecked.add(newPredecessor);
        while (!toBeChecked.isEmpty()) {
            UrnNbnWithStatus next = toBeChecked.removeFirst();
            if (next != null) {
                //System.out.println("checking: " + next.getUrn());
                //make sure that predecessors are always loaded
                next = dataAccessService().urnByRegistrarCodeAndDocumentCode(next.getUrn().getRegistrarCode(), next.getUrn().getDocumentCode(), true);
                List<UrnNbnWithStatus> predecessors = next.getUrn().getPredecessors();
                //System.out.println(next.getUrn() + " predecessors: " + predecessors);
                if (predecessors != null) {
                    toBeChecked.addAll(predecessors);
                }
                if (next.getUrn().equals(urnNbn.getUrn())) {
                    System.out.println("CYCLE DETECTED: " + urnNbn.getUrn() + " is already a predecessor of " + newPredecessor.getUrn());
                    //cycle detected
                    throw new ConflictException("Adding predeccessor->successor relation " + newPredecessor.getUrn() + " -> " + urnNbn.getUrn() + " would create a cycle in predecessor-successor relation graph");
                }
            } else {
                //System.out.println("next is null");
            }
        }
    }

    @Override
    public void removePredecessorSuccessorRelation(UrnNbn predecessor, UrnNbn successor, String login) throws UnknownRecordException, InsufficientRightsException {
        try {
            dataUpdateService().removeRelationPredecessorSuccessor(predecessor, successor, login);
        } catch (UnknownUserException e) {
            throw new UnknownRecordException("Unknown digital document: " + e.getMessage());
        } catch (NotAdminException e) {
            throw new InsufficientRightsException("Insufficient rights: " + e.getMessage());
        }
    }

    private DigDocRegistrationData convert(RecordToBeCreatedOrUpdated record) {
        //System.out.println("Converting RecordToBeImported to DigDocRegistrationData...");
        //System.out.println(record);
        DigDocRegistrationData result = new DigDocRegistrationData();
        //intellectual entity
        long ieId = record.intelectualEntity.id;
        result.setEntity(record.intelectualEntity.toDtoIntEnt());
        result.setIntEntIds(record.intelectualEntity.toDtoIeIds(ieId));
        result.setPublication(record.intelectualEntity.toDtoPublication(ieId));
        result.setOriginator(record.intelectualEntity.toDtoOriginator(ieId));
        result.setSourceDoc(record.intelectualEntity.toDtoSrcDoc(ieId));
        //digital document
        result.setDigitalDocument(record.digitalDocument.toDtoDigDoc(ieId));
        //registrar-scope identifiers (not used here)
        result.setDigDocIdentifiers(List.of());
        //predecessors (not used here)
        result.setPredecessors(List.of());
        //urn:nbn
        if (record.urnNbn != null) {
            try {
                result.setUrn(UrnNbn.valueOf(record.urnNbn));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid URN:NBN format: " + record.urnNbn);
            }
        }
        //registrar-code
        if (record.registrarCode == null) {
            throw new BadRequestException("Registrar code is required");
        }
        try {
            result.setRegistrarCode(RegistrarCode.valueOf(record.registrarCode));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid registrar code: " + record.registrarCode);
        }
        //set registrarId and archiverId in digital document from registrarCode and archiverId (from record)
        cz.nkp.urnnbn.core.dto.Registrar registrar = dataAccessService().registrarByCode(result.getRegistrarCode());
        if (registrar == null) {
            throw new BadRequestException("Unknown registrar code: " + record.registrarCode);
        }
        if (result.getDigitalDocument().getRegistrarId() == null) {
            result.getDigitalDocument().setRegistrarId(registrar.getId());
        }
        result.getDigitalDocument().setArchiverId(record.archiverId == null ? registrar.getId() : record.archiverId);
        try {
            new MetadataStructureEnforcer(result).check();
        } catch (MetadataStructureEnforcer.MetadataStructureException e) {
            throw new BadRequestException("Metadata structure error: " + e.getMessage());
        }
        return result;
    }
}
