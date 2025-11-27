package cz.nkp.urnnbn.czidlo_web_api.api.registrars.registrar_manager;

import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.DuplicateRecordException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnknownRecordException;
import cz.nkp.urnnbn.czidlo_web_api.api.registrars.core.Catalogue;
import cz.nkp.urnnbn.czidlo_web_api.api.registrars.core.DigitalLibrary;
import cz.nkp.urnnbn.czidlo_web_api.api.registrars.core.Registrar;
import cz.nkp.urnnbn.services.*;

import java.util.ArrayList;
import java.util.List;

public class RegistrarManagerImpl implements RegistrarManager {

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


    @Override
    public Registrar createRegistrar(String login, String registrarCode, String name, String description, boolean allowedRegistrationModeByResolver, boolean allowedRegistrationModeByReservation, boolean allowedRegistrationModeByRegistrar) throws DuplicateRecordException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Registrar getRegistrarByCode(String registrarCode) throws UnknownRecordException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<Registrar> getRegistrars() {
        List<cz.nkp.urnnbn.core.dto.Registrar> registrars = dataAccessService().registrars();
        List<Registrar> result = new ArrayList<>(registrars.size());
        for (cz.nkp.urnnbn.core.dto.Registrar reg : registrars) {
            System.out.println("Registrar: " + reg.getCode() + ", name: " + reg.getName());
            Registrar resultItem = Registrar.from(reg, null, null);
            //TODO: populate libraries and catalogues
            result.add(resultItem);
        }
        return result;
    }

    @Override
    public Registrar updateRegistrar(String login, String registrarCode, String name, String description, boolean allowedRegistrationModeByResolver, boolean allowedRegistrationModeByReservation, boolean allowedRegistrationModeByRegistrar, boolean isHidden) throws UnknownRecordException, DuplicateRecordException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public DigitalLibrary createLibrary(String login, String registrarCode, String name, String description, String url) throws UnknownRecordException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public DigitalLibrary updateLibrary(String login, String registrarCode, long libraryId, String name, String description, String url) throws UnknownRecordException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteLibrary(String login, String registrarCode, long libraryId) throws UnknownRecordException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Catalogue createCatalogue(String login, String registrarCode, String name, String description, String urlPrefix) throws UnknownRecordException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Catalogue updateCatalogue(String login, String registrarCode, long catalogueId, String name, String description, String urlPrefix) throws UnknownRecordException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteCatalogue(String login, String registrarCode, long catalogueId) throws UnknownRecordException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteRegistrar(String login, String registrarCode) throws UnknownRecordException {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
