package cz.nkp.urnnbn.client.search;

import cz.nkp.urnnbn.shared.dto.UserDTO;
import cz.nkp.urnnbn.shared.dto.ie.IntelectualEntityDTO;
import cz.nkp.urnnbn.shared.dto.ie.OtherEntityDTO;

public class OtherEntityBuilder extends EntityTreeItemBuilder {

    private final OtherEntityDTO dto;

    public OtherEntityBuilder(OtherEntityDTO dto, UserDTO user, SearchTab superPanel) {
        super(user, superPanel, dto.getPrimaryOriginator(), dto.getPublication(), null);
        this.dto = dto;
    }

    @Override
    void addRows() {
        appendAlephLinkIfEnabledAndCcnbPresent(dto.getCcnb());
        addLabeledRowIfNotNull(constants.title(), dto.getTitle());
        addLabeledRowIfNotNull(constants.subTitle(), dto.getSubTitle());
        addLabeledRowIfNotNull(constants.ccnb(), dto.getCcnb());
        addLabeledRowIfNotNull(constants.isbn(), dto.getIsbn());
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
        return constants.otherEntity();
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
