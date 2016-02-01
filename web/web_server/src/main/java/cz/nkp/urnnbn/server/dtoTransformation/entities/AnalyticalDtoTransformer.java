package cz.nkp.urnnbn.server.dtoTransformation.entities;

import java.util.ArrayList;
import java.util.List;

import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.dto.SourceDocument;
import cz.nkp.urnnbn.shared.dto.DigitalDocumentDTO;
import cz.nkp.urnnbn.shared.dto.PublicationDTO;
import cz.nkp.urnnbn.shared.dto.ie.AnalyticalDTO;
import cz.nkp.urnnbn.shared.dto.ie.IntelectualEntityDTO;
import cz.nkp.urnnbn.shared.dto.ie.SourceDocumentDTO;

public class AnalyticalDtoTransformer extends EntityDtoTransformer {

    public AnalyticalDtoTransformer(IntelectualEntity entity, List<IntEntIdentifier> enityIds, Originator originator, SourceDocument srcDoc,
            ArrayList<DigitalDocumentDTO> docs) {
        super(entity, enityIds, null, originator, srcDoc, docs);
    }

    @Override
    IntelectualEntityDTO intEntToDto() {
        AnalyticalDTO result = new AnalyticalDTO();
        result.setId(entity.getId());
        for (IntEntIdentifier id : enityIds) {
            switch (id.getType()) {
            case TITLE:
                result.setTitle(id.getValue());
                break;
            case SUB_TITLE:
                result.setSubTitle(id.getValue());
                break;
            case ISBN:
                result.setIsbn(id.getValue());
                break;
            case ISSN:
                result.setIssn(id.getValue());
                break;
            case CCNB:
                result.setCcnb(id.getValue());
                break;
            case OTHER:
                result.setOtherId(id.getValue());
                break;
            }
        }
        transformTimestamps(entity, result);
        result.setDigitalBorn(entity.isDigitalBorn());
        result.setDocumentType(entity.getDocumentType());
        result.setOtherOriginator(entity.getOtherOriginator());
        result.setSourceDocument(sourceDocumentDto());
        return result;
    }

    private SourceDocumentDTO sourceDocumentDto() {
        SourceDocumentDTO dto = new SourceDocumentDTO();
        dto.setTitle(srcDoc.getTitle());
        dto.setVolumeTitle(srcDoc.getVolumeTitle());
        dto.setIssueTitle(srcDoc.getIssueTitle());
        dto.setCcnb(srcDoc.getCcnb());
        dto.setIsbn(srcDoc.getIsbn());
        dto.setIssn(srcDoc.getIssn());
        dto.setOtherId(srcDoc.getOtherId());
        dto.setPublication(transformPublication());
        return dto;
    }

    private PublicationDTO transformPublication() {
        PublicationDTO result = new PublicationDTO();
        result.setPublicationPlace(srcDoc.getPublicationPlace());
        result.setPublicationYear(srcDoc.getPublicationYear());
        result.setPublisher(srcDoc.getPublisher());
        return result.isEmpty() ? null : result;
    }
}
