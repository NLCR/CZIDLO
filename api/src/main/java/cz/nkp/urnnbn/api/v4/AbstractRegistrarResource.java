package cz.nkp.urnnbn.api.v4;

import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.xml.apiv4.builders.RegistrarBuilder;

public abstract class AbstractRegistrarResource extends ApiV4Resource {
    protected final Registrar registrar;

    public AbstractRegistrarResource(Registrar registrar) {
        this.registrar = registrar;
    }

    protected String getRegistrarApiV4XmlRecord(boolean addDigitalLibraries, boolean addCatalogs) throws DatabaseException {
        RegistrarBuilder builder = registrarBuilder(registrar, addDigitalLibraries, addCatalogs);
        return builder.buildDocumentWithResponseHeader().toXML();
    }

    public abstract AbstractDigitalDocumentsResource getDigitalDocuments();

    public abstract AbstractUrnNbnReservationsResource getUrnNbnReservations();

}
