package cz.nkp.urnnbn.czidlo_web_api.api.registrars.registrar_manager;

import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.DuplicateRecordException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnknownRecordException;
import cz.nkp.urnnbn.czidlo_web_api.api.registrars.core.Catalogue;
import cz.nkp.urnnbn.czidlo_web_api.api.registrars.core.DigitalLibrary;
import cz.nkp.urnnbn.czidlo_web_api.api.registrars.core.Registrar;

import java.util.List;

public class RegistrarManagerMockInMemory implements RegistrarManager {
    private static final RegistrarInMemoryRepo repo = RegistrarInMemoryRepo.getInstance();

    public RegistrarManagerMockInMemory() {
    }

    @Override
    public Registrar createRegistrar(String login, String registrarCode, String name, String description, boolean allowedRegistrationModeByResolver, boolean allowedRegistrationModeByReservation, boolean allowedRegistrationModeByRegistrar) throws DuplicateRecordException {
        if (repo.getByCode(registrarCode) != null) {
            throw new DuplicateRecordException("Registrar with code \"" + registrarCode + "\" already exists");
        }

        return repo.create(registrarCode, name, description, allowedRegistrationModeByResolver, allowedRegistrationModeByReservation, allowedRegistrationModeByRegistrar);
    }

    @Override
    public Registrar getRegistrarByCode(String registrarCode) throws UnknownRecordException {
        Registrar registrar = repo.getByCode(registrarCode);

        if (registrar == null) {
            throw new UnknownRecordException("Unknown registrar: " + registrarCode);
        }
        return registrar;
    }

    @Override
    public List<Registrar> getRegistrars() {
        return repo.getAll();
    }

    @Override
    public Registrar updateRegistrar(String login, String registrarCode, String name, String description, boolean allowedRegistrationModeByResolver, boolean allowedRegistrationModeByReservation, boolean allowedRegistrationModeByRegistrar, boolean isHidden) throws UnknownRecordException, DuplicateRecordException {
        return repo.update(registrarCode, name, description, allowedRegistrationModeByResolver, allowedRegistrationModeByReservation, allowedRegistrationModeByRegistrar, isHidden);
    }

    @Override
    public DigitalLibrary createLibrary(String login, String registrarCode, String name, String description, String url) throws UnknownRecordException {
        Registrar registrar = getRegistrarByCode(registrarCode);
        return repo.createDigitalLibrary(registrarCode, name, description, url);
    }

    @Override
    public DigitalLibrary updateLibrary(String login, String registrarCode, long libraryId, String name, String description, String url) throws UnknownRecordException {
        Registrar registrar = getRegistrarByCode(registrarCode);
        DigitalLibrary library = registrar.getDigitalLibrary(libraryId);

        if (library == null) {
            throw new UnknownRecordException("Unknown library with id: " + libraryId);
        }

        return repo.updateDigitalLibrary(registrarCode, libraryId, name, description, url);
    }

    @Override
    public void deleteLibrary(String login, String registrarCode, long libraryId) throws UnknownRecordException {
        Registrar registrar = getRegistrarByCode(registrarCode);
        DigitalLibrary library = registrar.getDigitalLibrary(libraryId);

        if (library == null) {
            throw new UnknownRecordException("Unknown library with id: " + libraryId);
        }

        repo.deleteDigitalLibrary(registrarCode, libraryId);
    }

    @Override
    public Catalogue createCatalogue(String login, String registrarCode, String name, String description, String url) throws UnknownRecordException {
        getRegistrarByCode(registrarCode);
        return repo.createCatalogue(registrarCode, name, description, url);
    }

    @Override
    public Catalogue updateCatalogue(String login, String registrarCode, long catalogueId, String name, String description, String url) throws UnknownRecordException {
        Registrar registrar = getRegistrarByCode(registrarCode);
        Catalogue catalogue = registrar.getCatalogue(catalogueId);

        if (catalogue == null) {
            throw new UnknownRecordException("Unknown catalogue with id: " + catalogueId);
        }

        return repo.updateCatalogue(registrarCode, catalogueId, name, description, url);
    }

    @Override
    public void deleteCatalogue(String login, String registrarCode, long catalogueId) throws UnknownRecordException {
        Registrar registrar = getRegistrarByCode(registrarCode);
        Catalogue catalogue = registrar.getCatalogue(catalogueId);

        if (catalogue == null) {
            throw new UnknownRecordException("Unknown catalogue with id: " + catalogueId);
        }

        repo.deleteCatalogue(registrarCode, catalogueId);
    }

    @Override
    public void deleteRegistrar(String login, String registrarCode) throws UnknownRecordException {
        getRegistrarByCode(registrarCode);
        repo.delete(registrarCode);
    }
}
