package cz.nkp.urnnbn.czidlo_web_api.api.registrars.registrar_manager;

import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.BadArgumentException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.DuplicateRecordException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnknownRecordException;
import cz.nkp.urnnbn.czidlo_web_api.api.registrars.core.Catalogue;
import cz.nkp.urnnbn.czidlo_web_api.api.registrars.core.DigitalLibrary;
import cz.nkp.urnnbn.czidlo_web_api.api.registrars.core.Registrar;
import cz.nkp.urnnbn.services.*;
import cz.nkp.urnnbn.services.exceptions.*;

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
    public Registrar createRegistrar(String login, String registrarCodeStr, String name, String description,
                                     boolean allowedRegistrationModeByResolver, boolean allowedRegistrationModeByReservation, boolean allowedRegistrationModeByRegistrar)
            throws BadArgumentException, DuplicateRecordException {
        cz.nkp.urnnbn.core.dto.Registrar registrarDto = new cz.nkp.urnnbn.core.dto.Registrar();
        RegistrarCode registrarCode;
        try {
            registrarCode = RegistrarCode.valueOf(registrarCodeStr);
        } catch (IllegalArgumentException e) {
            throw new BadArgumentException("Invalid registrar code: " + registrarCodeStr + ": " + e.getMessage());
        }
        registrarDto.setCode(registrarCode);
        registrarDto.setName(name);
        registrarDto.setDescription(description);
        registrarDto.setRegistrationModeAllowed(cz.nkp.urnnbn.core.UrnNbnRegistrationMode.BY_RESOLVER, allowedRegistrationModeByResolver);
        registrarDto.setRegistrationModeAllowed(cz.nkp.urnnbn.core.UrnNbnRegistrationMode.BY_RESERVATION, allowedRegistrationModeByReservation);
        registrarDto.setRegistrationModeAllowed(cz.nkp.urnnbn.core.UrnNbnRegistrationMode.BY_REGISTRAR, allowedRegistrationModeByRegistrar);
        try {
            registrarDto = dataImportService().insertNewRegistrar(registrarDto, login);
            return convertDtoRegistrar(registrarDto);
        } catch (UnknownUserException e) { // should not happen, will be caught earlier
            throw new RuntimeException(e);
        } catch (NotAdminException e) { // should not happen, will be caught earlier
            throw new RuntimeException(e);
        } catch (RegistrarCollisionException e) {
            throw new DuplicateRecordException("Registrar with given code or name already exists: " + e.getMessage());
        }
    }

    @Override
    public Registrar getRegistrarByCode(String registrarCodeStr) throws UnknownRecordException, BadArgumentException {
        RegistrarCode registrarCode;
        try {
            registrarCode = RegistrarCode.valueOf(registrarCodeStr);
        } catch (IllegalArgumentException e) {
            throw new BadArgumentException("Invalid registrar code: " + registrarCodeStr + ": " + e.getMessage());
        }
        cz.nkp.urnnbn.core.dto.Registrar dtoRegistrar = dataAccessService().registrarByCode(registrarCode);
        if (dtoRegistrar == null) {
            throw new UnknownRecordException("Unknown registrar with registrar code: " + registrarCodeStr);
        }
        return convertDtoRegistrar(dtoRegistrar);
    }

    @Override
    public List<Registrar> getRegistrars() {
        List<cz.nkp.urnnbn.core.dto.Registrar> dtoRegs = dataAccessService().registrars();
        List<Registrar> regs = new ArrayList<>(dtoRegs.size());
        for (cz.nkp.urnnbn.core.dto.Registrar dtoReg : dtoRegs) {
            regs.add(convertDtoRegistrar(dtoReg));
        }
        return regs;
    }

    private Registrar convertDtoRegistrar(cz.nkp.urnnbn.core.dto.Registrar dtoRegistrar) {
        List<cz.nkp.urnnbn.core.dto.DigitalLibrary> dtoLibs = dataAccessService().librariesByRegistrarId(dtoRegistrar.getId());
        List<DigitalLibrary> libs = new ArrayList<>(dtoLibs.size());
        for (cz.nkp.urnnbn.core.dto.DigitalLibrary dtoLib : dtoLibs) {
            DigitalLibrary lib = DigitalLibrary.fromDto(dtoLib);
            libs.add(lib);
        }
        List<cz.nkp.urnnbn.core.dto.Catalog> dtoCats = dataAccessService().catalogsByRegistrarId(dtoRegistrar.getId());
        List<Catalogue> cats = new ArrayList<>(dtoCats.size());
        for (cz.nkp.urnnbn.core.dto.Catalog dtoCat : dtoCats) {
            Catalogue cat = Catalogue.fromDto(dtoCat);
            cats.add(cat);
        }
        return Registrar.from(dtoRegistrar, libs, cats);
    }


    @Override
    public Registrar updateRegistrar(String login, String registrarCode, String name, String description,
                                     boolean allowedRegistrationModeByResolver, boolean allowedRegistrationModeByReservation,
                                     boolean allowedRegistrationModeByRegistrar, boolean isHidden) throws
            UnknownRecordException, DuplicateRecordException, BadArgumentException {

        cz.nkp.urnnbn.core.dto.Registrar registrarDto = dataAccessService().registrarByCode(RegistrarCode.valueOf(registrarCode));
        if (registrarDto == null) {
            throw new UnknownRecordException("Unknown registrar with registrar code: " + registrarCode);
        }
        registrarDto.setName(name);
        registrarDto.setDescription(description);
        registrarDto.setRegistrationModeAllowed(cz.nkp.urnnbn.core.UrnNbnRegistrationMode.BY_RESOLVER, allowedRegistrationModeByResolver);
        registrarDto.setRegistrationModeAllowed(cz.nkp.urnnbn.core.UrnNbnRegistrationMode.BY_RESERVATION, allowedRegistrationModeByReservation);
        registrarDto.setRegistrationModeAllowed(cz.nkp.urnnbn.core.UrnNbnRegistrationMode.BY_REGISTRAR, allowedRegistrationModeByRegistrar);
        registrarDto.setHidden(isHidden);
        try {
            dataUpdateService().updateRegistrar(registrarDto, login);
            return getRegistrarByCode(registrarCode);
        } catch (UnknownUserException e) { // should not happen, will be caught earlier
            throw new RuntimeException(e);
        } catch (UnknownRegistrarException e) {
            throw new RuntimeException(e);
        } catch (AccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DigitalLibrary createLibrary(String login, String registrarCode, String name, String description, String
            url) throws UnknownRecordException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public DigitalLibrary updateLibrary(String login, String registrarCode, long libraryId, String name, String
            description, String url) throws UnknownRecordException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteLibrary(String login, String registrarCode, long libraryId) throws UnknownRecordException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Catalogue createCatalogue(String login, String registrarCode, String name, String description, String
            urlPrefix) throws UnknownRecordException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Catalogue updateCatalogue(String login, String registrarCode, long catalogueId, String name, String
            description, String urlPrefix) throws UnknownRecordException {
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
