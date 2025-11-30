package cz.nkp.urnnbn.czidlo_web_api.api.documents;

import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.dto.*;
import cz.nkp.urnnbn.czidlo_web_api.api.archivers.core.Archiver;
import cz.nkp.urnnbn.czidlo_web_api.api.documents.core.*;
import cz.nkp.urnnbn.czidlo_web_api.api.documents.core.Record;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.BadArgumentException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.InsufficientRightsException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnknownRecordException;
import cz.nkp.urnnbn.czidlo_web_api.api.registrars.core.Registrar;
import cz.nkp.urnnbn.services.*;
import cz.nkp.urnnbn.services.exceptions.*;
import jakarta.ws.rs.BadRequestException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    public UrnNbn createRecord(RecordToBeImported record, String login) throws
            BadArgumentException, UnknownUserException, RegistrarScopeIdentifierCollisionException, UnknownArchiverException,
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

    private DigDocRegistrationData convert(RecordToBeImported record) {
        System.out.println("Converting RecordToBeImported to DigDocRegistrationData...");
        System.out.println(record);
        DigDocRegistrationData result = new DigDocRegistrationData();
        //intellectual entity
        result.setEntity(record.intelectualEntity.toDtoIntEnt());
        result.setIntEntIds(record.intelectualEntity.toDtoIeIds());
        result.setPublication(record.intelectualEntity.toDtoPublication());
        result.setOriginator(record.intelectualEntity.toDtoOriginator());
        result.setSourceDoc(record.intelectualEntity.toDtoSrcDoc());
        //digital document
        result.setDigitalDocument(record.digitalDocument.toDtoDigDoc());
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
            //set registrarId and archiverId in digital document from registrarCode and archiverId (from record)
            cz.nkp.urnnbn.core.dto.Registrar registrar = dataAccessService().registrarByCode(result.getRegistrarCode());
            if (registrar == null) {
                throw new BadRequestException("Unknown registrar code: " + record.registrarCode);
            }
            if (result.getDigitalDocument().getRegistrarId() == null) {
                result.getDigitalDocument().setRegistrarId(registrar.getId());
            }
            result.getDigitalDocument().setArchiverId(record.archiverId == null ? registrar.getId() : record.archiverId);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid registrar code: " + record.registrarCode);
        }
        return result;
    }
}
