package cz.nkp.urnnbn.czidlo_web_api.api.documents;

import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.dto.*;
import cz.nkp.urnnbn.czidlo_web_api.api.archivers.core.Archiver;
import cz.nkp.urnnbn.czidlo_web_api.api.documents.core.*;
import cz.nkp.urnnbn.czidlo_web_api.api.documents.core.Record;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnknownRecordException;
import cz.nkp.urnnbn.czidlo_web_api.api.registrars.core.Registrar;
import cz.nkp.urnnbn.services.*;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigDocException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;

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
                    digitalInstances.add(DigInst.from(di, digitalLibrary));
                }
            }
        }
        return Record.from(urn, doc, entity, registrar, archiver, rsIds, digitalInstances);
    }

    @Override
    public boolean deactivateRecord(UrnNbn urnNbn, String note, String loginOfUserPerformingOperation) throws UnknownRecordException {
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
            throw new RuntimeException(e);
        } catch (UnknownDigDocException e) {
            throw new UnknownRecordException("Digital document with URN:NBN " + urnNbn + " not found");
        }
    }

    @Override
    public boolean reactivateRecord(UrnNbn urnNbn, String login) throws UnknownRecordException {
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
            throw new RuntimeException(e);
        } catch (UnknownDigDocException e) {
            throw new UnknownRecordException("Digital document with URN:NBN " + urnNbn + " not found");
        }
    }
}
