package cz.nkp.urnnbn.server.dtoTransformation.entities;

import java.util.ArrayList;
import java.util.List;

import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.shared.dto.DigitalDocumentDTO;
import cz.nkp.urnnbn.shared.dto.ie.OtherEntityDTO;
import cz.nkp.urnnbn.shared.dto.ie.PublishableEntityDTO;

public class OtherEntityDtoTransformer extends PublishableIntEntityDtoTransformer {

	public OtherEntityDtoTransformer(IntelectualEntity entity, List<IntEntIdentifier> enityIds, Publication publication,
			Originator originator, ArrayList<DigitalDocumentDTO> docs) {
		super(entity, enityIds, publication, originator, docs);
	}

	@Override
	PublishableEntityDTO publishableEntityDto() {
		OtherEntityDTO result = new OtherEntityDTO();
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
		return result;
	}
}
