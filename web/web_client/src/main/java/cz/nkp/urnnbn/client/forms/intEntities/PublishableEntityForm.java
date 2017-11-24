package cz.nkp.urnnbn.client.forms.intEntities;

import cz.nkp.urnnbn.client.forms.Field;
import cz.nkp.urnnbn.client.forms.FormFields;
import cz.nkp.urnnbn.client.forms.TextInputValueField;
import cz.nkp.urnnbn.shared.dto.PublicationDTO;
import cz.nkp.urnnbn.shared.dto.ie.PrimaryOriginatorDTO;
import cz.nkp.urnnbn.shared.dto.ie.PublishableEntityDTO;
import cz.nkp.urnnbn.client.validation.LimitedLengthValidator;
import cz.nkp.urnnbn.client.validation.YearValidator;

public abstract class PublishableEntityForm extends IntelectualEntityForm {

    public PublishableEntityForm(PrimaryOriginatorDTO originatorDto) {
        super(originatorDto);
    }

    void addPublicationFieldsToFormFields(FormFields result, PublishableEntityDTO dto) {
        PublicationDTO publication = dto.getPublication() != null ? dto.getPublication() : new PublicationDTO();
        Field publisher = new TextInputValueField(new LimitedLengthValidator(50), constants.publisher(), publication.getPublisher(), false);
        result.addField("publisher", publisher);
        Field pubPlace = new TextInputValueField(new LimitedLengthValidator(50), constants.publicationPlace(), publication.getPublicationPlace(),
                false);
        result.addField("pubPlace", pubPlace);
        Field pubYear = new TextInputValueField(new YearValidator(), constants.publicationYear(), publication.getPublicationYear(), false);
        result.addField("pubYear", pubYear);
    }

    void setPublicationDataFromFormFields(PublishableEntityDTO result) {
        PublicationDTO publication = new PublicationDTO();
        publication.setPublisher(getStringFieldValue("publisher"));
        publication.setPublicationPlace(getStringFieldValue("pubPlace"));
        String yearStr = getStringFieldValue("pubYear");
        if (yearStr != null) {
            publication.setPublicationYear(Integer.valueOf(yearStr));
        }
        if (!publication.isEmpty()) {
            result.setPublication(publication);
        }
    }
}
