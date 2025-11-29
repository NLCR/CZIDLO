package cz.nkp.urnnbn.czidlo_web_api.api.documents;

import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.czidlo_web_api.api.documents.core.*;
import cz.nkp.urnnbn.czidlo_web_api.api.documents.core.Record;
import cz.nkp.urnnbn.services.*;

import java.util.List;

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
        Document doc = null;
        Entity entity = null;
        Long digDocId = urnNbnWithStatus.getUrn().getDigDocId();
        if (digDocId != null) {
            DigitalDocument digDoc = dataAccessService().digDocByInternalId(digDocId);
            doc = Document.from(digDoc);
            //INTELECTUAL ENTITY
            IntelectualEntity ie = dataAccessService().entityById(digDoc.getIntEntId());
            if (ie != null) {
                Orig originator = Orig.from(dataAccessService().originatorByIntEntId(ie.getId()));
                Publ publication = Publ.from(dataAccessService().publicationByIntEntId(ie.getId()));
                SrcDoc srcDoc = SrcDoc.from(dataAccessService().sourceDocumentByIntEntId(ie.getId()));
                List<IeId> ieIds = IeId.fromlist(dataAccessService().intEntIdentifiersByIntEntId(ie.getId()));
                entity = Entity.from(ie, originator, publication, srcDoc, ieIds);
            }
        }
        return Record.from(urn, doc, entity);
    }
}
