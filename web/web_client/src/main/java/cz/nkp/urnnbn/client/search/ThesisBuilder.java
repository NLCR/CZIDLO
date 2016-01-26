package cz.nkp.urnnbn.client.search;

import cz.nkp.urnnbn.shared.dto.UserDTO;
import cz.nkp.urnnbn.shared.dto.ie.IntelectualEntityDTO;
import cz.nkp.urnnbn.shared.dto.ie.ThesisDTO;

public class ThesisBuilder extends EntityTreeItemBuilder {

	private final ThesisDTO dto;

	public ThesisBuilder(ThesisDTO dto, UserDTO user, SearchPanel superPanel) {
		super(user, superPanel, dto.getPrimaryOriginator(), dto.getPublication(), null);
		this.dto = dto;
	}

	@Override
	void addRows() {
		appendAlephLinkIfEnabledAndCcnbPresent(dto.getCcnb());
		addLabeledRowIfNotNull(constants.title(), dto.getTitle());
		addLabeledRowIfNotNull(constants.subTitle(), dto.getSubTitle());
		addLabeledRowIfNotNull(constants.otherId(), dto.getOtherId());
		addLabeledRowIfNotNull(constants.documentType(), dto.getDocumentType());
		addDigitalBorn(dto.isDigitalBorn());
		appendPrimaryOriginatorIfNotNull();
		addLabeledRowIfNotNull(constants.otherOriginator(), dto.getOtherOriginator());
		appendPublicationIfNotNull();
		addLabeledRowIfNotNull(constants.thesisInstitution(), dto.getDegreeAwardingInstitution());
		addTimestamps(dto);
	}

	@Override
	String entityType() {
		return constants.thesis();
	}

	@Override
	String getAggregateTitle() {
		return dto.getTitle();
	}

	@Override
	IntelectualEntityDTO getDto() {
		return dto;
	}
}
