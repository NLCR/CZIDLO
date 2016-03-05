package cz.nkp.urnnbn.api.v4;

import java.util.List;

import cz.nkp.urnnbn.api.config.ApiModuleConfiguration;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.UnknownRegistrarException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;
import cz.nkp.urnnbn.xml.apiv4.builders.UrnNbnReservationBuilder;
import cz.nkp.urnnbn.xml.apiv4.builders.UrnNbnReservationsBuilder;

public abstract class AbstractUrnNbnReservationsResource extends ApiV4Resource {

    private final Registrar registrar;

    protected AbstractUrnNbnReservationsResource(Registrar registrar) {
        this.registrar = registrar;
    }

    public abstract String getUrnNbnReservationsXmlRecord();

    protected final String getUrnNbnReservationsApiV4XmlRecord() throws UnknownRegistrarException {
        int maxBatchSize = urnReservationService().getMaxBatchSize();
        List<UrnNbn> reservedUrnNbnList = urnReservationService().getReservedUrnNbnList(registrar.getId());
        UrnNbnReservationsBuilder builder = selectBuilder(maxBatchSize, reservedUrnNbnList);
        return builder.buildDocumentWithResponseHeader().toXML();
    }

    private UrnNbnReservationsBuilder selectBuilder(int maxBatchSize, List<UrnNbn> reservedUrnNbnList) {
        int maxPrintSize = ApiModuleConfiguration.instanceOf().getMaxReservedSizeToPrint();
        if (reservedUrnNbnList.size() > maxPrintSize) {
            return new UrnNbnReservationsBuilder(maxBatchSize, ApiModuleConfiguration.instanceOf().getUrnReservationDefaultSize(),
                    reservedUrnNbnList.size(), reservedUrnNbnList.subList(0, maxPrintSize));
        } else {
            return new UrnNbnReservationsBuilder(maxBatchSize, ApiModuleConfiguration.instanceOf().getUrnReservationDefaultSize(),
                    reservedUrnNbnList.size(), reservedUrnNbnList);
        }
    }

    protected final String reserveUrnNbns(String login, int size) throws UnknownUserException, AccessException {
        List<UrnNbn> reserved = urnReservationService().reserveUrnNbnBatch(size, registrar, login);
        UrnNbnReservationBuilder builder = new UrnNbnReservationBuilder(reserved);
        return builder.buildDocumentWithResponseHeader().toXML();
    }

}
