package cz.nkp.urnnbn.client.search;

import cz.nkp.urnnbn.shared.dto.UserDTO;
import cz.nkp.urnnbn.shared.dto.ie.AnalyticalDTO;
import cz.nkp.urnnbn.shared.dto.ie.IntelectualEntityDTO;

public class AnalyticalBuilder extends EntityTreeItemBuilder {

	private final AnalyticalDTO dto;

	public AnalyticalBuilder(AnalyticalDTO dto, UserDTO user, SearchPanel superPanel) {
		super(user, superPanel, dto.getPrimaryOriginator(), null, dto.getSourceDocument());
		this.dto = dto;
	}

	@Override
	void addRows() {
		addLabeledRowIfNotNull(constants.title(), dto.getTitle());
		addLabeledRowIfNotNull(constants.subTitle(), dto.getSubTitle());
		addLabeledRowIfNotNull(constants.ccnb(), dto.getCcnb());
		addLabeledRowIfNotNull(constants.isbn(), dto.getIsbn());
		addLabeledRowIfNotNull(constants.otherId(), dto.getOtherId());
		addLabeledRowIfNotNull(constants.documentType(), dto.getDocumentType());
		addLabeledRowIfNotNull(constants.otherOriginator(), dto.getOtherOriginator());
		appendPrimaryOriginatorIfNotNull();
		appendSourceDocumentIfNotNull();
		addTimestamps(dto);
	}

	@Override
	String entityType() {
		return constants.analytical();
	}
	
	@Override
	String getAggregateTitle(){
		return dto.getTitle();
	}

	@Override
	IntelectualEntityDTO getDto() {
		return dto;
	}
}
