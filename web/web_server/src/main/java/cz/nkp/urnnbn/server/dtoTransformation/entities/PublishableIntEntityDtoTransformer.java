package cz.nkp.urnnbn.server.dtoTransformation.entities;

import java.util.ArrayList;
import java.util.List;

import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.shared.dto.DigitalDocumentDTO;
import cz.nkp.urnnbn.shared.dto.PublicationDTO;
import cz.nkp.urnnbn.shared.dto.ie.IntelectualEntityDTO;
import cz.nkp.urnnbn.shared.dto.ie.PublishableEntityDTO;

public abstract class PublishableIntEntityDtoTransformer extends EntityDtoTransformer {

	public PublishableIntEntityDtoTransformer(IntelectualEntity entity, List<IntEntIdentifier> enityIds, Publication publication,
			Originator originator, ArrayList<DigitalDocumentDTO> docs) {
		super(entity, enityIds, publication, originator, null, docs);
	}

	@Override
	final IntelectualEntityDTO intEntToDto() {
		PublishableEntityDTO result = publishableEntityDto();
		if (publication != null) {
			PublicationDTO publicationDto = new PublicationDTO();
			publicationDto.setPublisher(publication.getPublisher());
			publicationDto.setPublicationPlace(publication.getPlace());
			publicationDto.setPublicationYear(publication.getYear());
			result.setPublication(publicationDto);
		}
		return result;
	}

	abstract PublishableEntityDTO publishableEntityDto();
}
