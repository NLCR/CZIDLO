/*
 * Copyright (C) 2013 Martin Řehánek
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.nkp.urnnbn.api.v2_v3;

import java.util.List;

import cz.nkp.urnnbn.api.config.ApiModuleConfiguration;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.UnknownRegistrarException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;
import cz.nkp.urnnbn.xml.apiv3.builders.UrnNbnReservationBuilder;
import cz.nkp.urnnbn.xml.apiv3.builders.UrnNbnReservationsBuilder;

/**
 *
 * @author Martin Řehánek
 */
public abstract class AbstractUrnNbnReservationsResource extends ApiV2V3Resource {

    private final Registrar registrar;

    protected AbstractUrnNbnReservationsResource(Registrar registrar) {
        this.registrar = registrar;
    }

    public abstract String getUrnNbnReservationsXmlRecord();

    protected final String getUrnNbnReservationsApiV3XmlRecord() throws UnknownRegistrarException {
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
