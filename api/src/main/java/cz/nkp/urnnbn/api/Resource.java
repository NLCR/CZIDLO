package cz.nkp.urnnbn.api;

import cz.nkp.urnnbn.services.DataAccessService;
import cz.nkp.urnnbn.services.DataImportService;
import cz.nkp.urnnbn.services.DataRemoveService;
import cz.nkp.urnnbn.services.DataUpdateService;
import cz.nkp.urnnbn.services.Services;
import cz.nkp.urnnbn.services.StatisticService;
import cz.nkp.urnnbn.services.UrnNbnReservationService;

public class Resource {

    protected DataAccessService dataAccessService() {
        return Services.instanceOf().dataAccessService();
    }

    protected DataImportService dataImportService() {
        return Services.instanceOf().dataImportService();
    }

    protected UrnNbnReservationService urnReservationService() {
        return Services.instanceOf().urnReservationService();
    }

    protected DataRemoveService dataRemoveService() {
        return Services.instanceOf().dataRemoveService();
    }

    protected DataUpdateService dataUpdateService() {
        return Services.instanceOf().dataUpdateService();
    }

    protected StatisticService statisticService() {
        return Services.instanceOf().statisticService();
    }

}
