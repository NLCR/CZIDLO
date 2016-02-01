package cz.nkp.urnnbn.server.dtoTransformation.entities;

import java.util.ArrayList;
import java.util.List;

import cz.nkp.urnnbn.core.OriginType;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.dto.SourceDocument;
import cz.nkp.urnnbn.server.dtoTransformation.DtoTransformer;
import cz.nkp.urnnbn.shared.dto.DigitalDocumentDTO;
import cz.nkp.urnnbn.shared.dto.ie.IntelectualEntityDTO;
import cz.nkp.urnnbn.shared.dto.ie.PrimaryOriginatorDTO;
import cz.nkp.urnnbn.shared.dto.ie.PrimaryOriginatorType;

public abstract class EntityDtoTransformer extends DtoTransformer {
    final IntelectualEntity entity;
    final List<IntEntIdentifier> enityIds;
    final Publication publication;
    final Originator originator;
    final SourceDocument srcDoc;
    final ArrayList<DigitalDocumentDTO> docs;

    public EntityDtoTransformer(IntelectualEntity entity, List<IntEntIdentifier> enityIds, Publication publication, Originator originator,
            SourceDocument srcDoc, ArrayList<DigitalDocumentDTO> docs) {
        this.entity = entity;
        this.enityIds = enityIds;
        this.publication = publication;
        this.originator = originator;
        this.srcDoc = srcDoc;
        this.docs = docs;
    }

    public static EntityDtoTransformer instanceOf(IntelectualEntity entity, List<IntEntIdentifier> enityIds, Publication publication,
            Originator originator, SourceDocument srcDoc, ArrayList<DigitalDocumentDTO> docs) {
        switch (entity.getEntityType()) {
        case MONOGRAPH:
            return new MonographDtoTransformer(entity, enityIds, publication, originator, docs);
        case MONOGRAPH_VOLUME:
            return new MonographVolumeDtoTransformer(entity, enityIds, publication, originator, docs);
        case PERIODICAL:
            return new PeriodicalDtoTransformer(entity, enityIds, publication, originator, docs);
        case PERIODICAL_VOLUME:
            return new PeriodicalVolumeDtoTransformer(entity, enityIds, publication, originator, docs);
        case PERIODICAL_ISSUE:
            return new PeriodicalIssueDtoTransformer(entity, enityIds, publication, originator, docs);
        case ANALYTICAL:
            return new AnalyticalDtoTransformer(entity, enityIds, originator, srcDoc, docs);
        case THESIS:
            return new ThesisDtoTransformer(entity, enityIds, publication, originator, docs);
        case OTHER:
            return new OtherEntityDtoTransformer(entity, enityIds, publication, originator, docs);
        default:
            // cannot happen
            return null;
        }
    }

    public IntelectualEntityDTO transform() {
        IntelectualEntityDTO dto = intEntToDto();
        if (originator != null) {
            dto.setPrimaryOriginator(transformOriginator());
        }
        dto.setDocuments(docs);
        return dto;
    }

    abstract IntelectualEntityDTO intEntToDto();

    PrimaryOriginatorDTO transformOriginator() {
        PrimaryOriginatorDTO originatorDto = new PrimaryOriginatorDTO();
        originatorDto.setType(toWebType(originator.getType()));
        originatorDto.setValue(originator.getValue());
        return originatorDto;
    }

    void transformTimestamps(IntelectualEntity entity, IntelectualEntityDTO result) {
        result.setCreated(dateTimeToStringOrNull(entity.getCreated()));
        result.setModified(dateTimeToStringOrNull(entity.getModified()));
    }

    PrimaryOriginatorType toWebType(OriginType type) {
        switch (type) {
        case AUTHOR:
            return PrimaryOriginatorType.AUTHOR;
        case CORPORATION:
            return PrimaryOriginatorType.CORPORATION;
        case EVENT:
            return PrimaryOriginatorType.EVENT;
        default:
            return null;
        }
    }

}
