package cz.nkp.urnnbn.client.search;

import cz.nkp.urnnbn.shared.dto.UserDTO;
import cz.nkp.urnnbn.shared.dto.ie.IntelectualEntityDTO;
import cz.nkp.urnnbn.shared.dto.ie.PeriodicalVolumeDTO;

public class PeriodicalVolumeBuilder extends EntityTreeItemBuilder {
	private final PeriodicalVolumeDTO dto;

	public PeriodicalVolumeBuilder(PeriodicalVolumeDTO dto, UserDTO user, SearchPanel superPanel) {
		super(user, superPanel, dto.getPrimaryOriginator(), dto.getPublication(), null);
		this.dto = dto;
	}

	@Override
	void addRows() {
		appendAlephLinkIfEnabledAndCcnbPresent(dto.getCcnb());
		// addLabeledRowIfNotNull(constants.title(),buildTitle(dto.getPeriodicalTitle(), dto.getVolumeTitle()));
		addLabeledRowIfNotNull(constants.periodicalTitle(), dto.getPeriodicalTitle());
		addLabeledRowIfNotNull(constants.periodicalVolumeTitle(), dto.getVolumeTitle());
		addLabeledRowIfNotNull(constants.ccnb(), dto.getCcnb());
		addLabeledRowIfNotNull(constants.issn(), dto.getIssn());
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
		return constants.periodicalVolume();
	}

	@Override
	String getAggregateTitle() {
		return buildTitle(dto.getPeriodicalTitle(), dto.getVolumeTitle());
	}

	@Override
	IntelectualEntityDTO getDto() {
		return dto;
	}
}
