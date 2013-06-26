package cz.nkp.urnnbn.client.search;

import cz.nkp.urnnbn.shared.dto.UserDTO;
import cz.nkp.urnnbn.shared.dto.ie.IntelectualEntityDTO;
import cz.nkp.urnnbn.shared.dto.ie.MonographVolumeDTO;

public class MonographVolumeBuilder extends EntityTreeItemBuilder {
	private final MonographVolumeDTO dto;

	public MonographVolumeBuilder(MonographVolumeDTO dto, UserDTO user, SearchPanel superPanel) {
		super(user, superPanel, dto.getPrimaryOriginator(),dto.getPublication(), null);
		this.dto = dto;
	}

	@Override
	void addRows() {
		appendAlephLinkIfEnabledAndCcnbPresent(dto.getCcnb());
		//addLabeledRowIfNotNull(constants.title(), buildTitle(dto.getMonographTitle(), dto.getVolumeTitle()));
		addLabeledRowIfNotNull(constants.monographTitle(), dto.getMonographTitle());
		addLabeledRowIfNotNull(constants.monographVolumeTitle(), dto.getVolumeTitle());
		addLabeledRowIfNotNull(constants.ccnb(), dto.getCcnb());
		addLabeledRowIfNotNull(constants.isbn(), dto.getIsbn());
		addLabeledRowIfNotNull(constants.otherId(), dto.getOtherId());
		addLabeledRowIfNotNull(constants.documentType(), dto.getDocumentType());
		addDigitalBorn(dto.isDigitalBorn());
		appendPrimaryOriginatorIfNotNull();
		addLabeledRowIfNotNull(constants.otherOriginator(), dto.getOtherOriginator());
		appendPublicationIfNotNull();
		addTimestamps(dto);
	}

	@Override
	String entityType() {
		return constants.monographVolume();
	}
	
	@Override
	String getAggregateTitle(){
		return buildTitle(dto.getMonographTitle(), dto.getVolumeTitle());
	}

	IntelectualEntityDTO getDto() {
		return dto;
	}
}
