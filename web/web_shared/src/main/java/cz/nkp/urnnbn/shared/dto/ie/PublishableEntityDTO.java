package cz.nkp.urnnbn.shared.dto.ie;

import cz.nkp.urnnbn.shared.dto.PublicationDTO;

public abstract class PublishableEntityDTO extends IntelectualEntityDTO {

    private static final long serialVersionUID = 3200022932857374973L;

    private PublicationDTO publication;

    public PublicationDTO getPublication() {
        return publication;
    }

    public void setPublication(PublicationDTO publication) {
        this.publication = publication;
    }


}
