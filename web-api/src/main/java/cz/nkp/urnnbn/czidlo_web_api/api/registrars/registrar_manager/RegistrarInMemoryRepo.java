package cz.nkp.urnnbn.czidlo_web_api.api.registrars.registrar_manager;

import cz.nkp.urnnbn.czidlo_web_api.api.registrars.core.Catalogue;
import cz.nkp.urnnbn.czidlo_web_api.api.registrars.core.DigitalLibrary;
import cz.nkp.urnnbn.czidlo_web_api.api.registrars.core.Registrar;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class RegistrarInMemoryRepo {
    private static RegistrarInMemoryRepo instance;

    private static final SortedMap<String, Registrar> registrars = new TreeMap<>();
    private static final AtomicLong nextRegistrarId = new AtomicLong(0);

    private static final AtomicLong nextDigitalLibraryId = new AtomicLong(0);
    private static final AtomicLong nextCatalogueId = new AtomicLong(0);

    private RegistrarInMemoryRepo() {}

    public static RegistrarInMemoryRepo getInstance() {
        if (instance == null) {
            instance = new RegistrarInMemoryRepo();
            fill();

        }
        return instance;
    }

    private static void fill() {

        Registrar a0 = instance.create("abe301", "Český rozhlas - Rešeršní oddělení a knihovna", null, true, true, false);
        a0.setCreated(Date.from(Instant.parse("2025-06-07T10:11:12.028231961Z")));
        a0.setModified(Date.from(Instant.parse("2025-06-07T11:12:13.028231961Z")));
        DigitalLibrary b0 = instance.createDigitalLibrary(a0.getCode(),"test", null, "https://test.com");
        b0.setCreated(Date.from(Instant.parse("2025-06-07T11:12:13.028231961Z")));
        b0.setModified(Date.from(Instant.parse("2025-06-07T11:12:13.028231961Z")));

        Registrar a1 = instance.create("jig503", "Husova knihovna Polná", null, true, false, false);
        a1.setCreated(Date.from(Instant.parse("2025-07-08T12:13:14.028231961Z")));
        a1.setModified(Date.from(Instant.parse("2025-07-08T13:14:15.028231961Z")));

        Registrar a2 = instance.create("khe303", "České muzeum stříbra - knihovna", "info@cms-kh.cz", true, true, false);
        a2.setCreated(Date.from(Instant.parse("2025-08-09T14:15:16.028231961Z")));
        a2.setModified(Date.from(Instant.parse("2025-08-09T14:16:17.028231961Z")));
        a2.setHidden(true);

        Registrar a3 = instance.create("ik", "Ikaros", "http://ikaros.cz/", true, false, true);
        a3.setCreated(Date.from(Instant.parse("2025-08-10T14:15:16.028231961Z")));
        a3.setModified(Date.from(Instant.parse("2025-08-10T14:16:17.028231961Z")));
        a3.setHidden(true);
        DigitalLibrary b3 = instance.createDigitalLibrary(a3.getCode(), "Ikaros", "digitální knihovna časopisu Ikaros", "http://www.ikaros.cz/");
        b3.setCreated(Date.from(Instant.parse("2025-08-10T14:16:16.028231961Z")));
        b3.setModified(Date.from(Instant.parse("2025-08-10T14:16:17.028231961Z")));
    }

    public Registrar create(String code, String name,  String description, boolean setAllowedRegistrationModeByResolver, boolean allowedRegistrationModeByReservation, boolean allowedRegistrationModeByRegistrar) {
        Registrar registrar = new Registrar();
        registrar.setId(nextRegistrarId.getAndIncrement());
        registrar.setCode(code);
        registrar.setName(name);
        Date date = new Date();
        registrar.setCreated(date);
        registrar.setModified(date);
        registrar.setAllowedRegistrationModeByResolver(setAllowedRegistrationModeByResolver);
        registrar.setAllowedRegistrationModeByReservation(allowedRegistrationModeByReservation);
        registrar.setAllowedRegistrationModeByRegistrar(allowedRegistrationModeByRegistrar);
        registrar.setDescription(description);
        registrars.put(registrar.getCode(), registrar);

        return registrar;
    }

    public Registrar getByCode(String code){
        return registrars.get(code);
    }

    public Registrar getByName(String name){
        return registrars.values().stream().filter(x -> x.getName().equals(name)).findFirst().orElse(null);
    }

    public List<Registrar> getAll(){
        return new ArrayList<>( registrars.values());
    }

    public Registrar update(String code, String name, String description, boolean allowedRegistrationModeByResolver, boolean allowedRegistrationModeByReservation, boolean allowedRegistrationModeByRegistrar, boolean isHidden){
        Registrar registrar = getByCode(code);
        registrar.setModified(new Date());
        registrar.setName(name);
        registrar.setDescription(description);
        registrar.setHidden(isHidden);
        registrar.setAllowedRegistrationModeByResolver(allowedRegistrationModeByResolver);
        registrar.setAllowedRegistrationModeByReservation(allowedRegistrationModeByReservation);
        registrar.setAllowedRegistrationModeByRegistrar(allowedRegistrationModeByRegistrar);

        return registrar;
    }

    public void delete(String code){
        registrars.remove(code);
    }

    public DigitalLibrary createDigitalLibrary(String code, String name, String description, String url){
        Registrar registrar = getByCode(code);
        DigitalLibrary digitalLibrary = new DigitalLibrary();
        digitalLibrary.setId(nextDigitalLibraryId.getAndIncrement());
        digitalLibrary.setName(name);
        digitalLibrary.setDescription(description);
        digitalLibrary.setUrl(url);
        Date date = new Date();
        digitalLibrary.setCreated(date);
        digitalLibrary.setModified(date);
        registrar.addDigitalLibraries(digitalLibrary);
        registrar.setModified(date);

        return digitalLibrary;
    }

    public DigitalLibrary updateDigitalLibrary(String code, long libraryId, String name, String description, String url){
        Registrar registrar = getByCode(code);
        DigitalLibrary digitalLibrary = registrar.getDigitalLibrary(libraryId);
        if (digitalLibrary == null) {
            return null;
        }
        digitalLibrary.setName(name);
        digitalLibrary.setDescription(description);
        digitalLibrary.setUrl(url);
        Date date = new Date();
        digitalLibrary.setModified(date);
        registrar.setModified(date);

        return digitalLibrary;
    }

    public void deleteDigitalLibrary(String code, long libraryId){
        Registrar registrar = getByCode(code);
        registrar.getDigitalLibraries().removeIf(x -> x.getId().equals(libraryId));
        Date date = new Date();
        registrar.setModified(date);
    }

    public Catalogue createCatalogue(String code, String name, String description, String urlPrefix){
        Registrar registrar = getByCode(code);
        Catalogue catalogue = new Catalogue();
        catalogue.setId(nextCatalogueId.getAndIncrement());
        catalogue.setName(name);
        catalogue.setDescription(description);
        catalogue.setUrlPrefix(urlPrefix);
        Date date = new Date();
        catalogue.setCreated(date);
        catalogue.setModified(date);
        registrar.addCatalogue(catalogue);
        registrar.setModified(date);

        return catalogue;
    }

    public Catalogue updateCatalogue(String code, long catalogueId, String name, String description, String urlPrefix){
        Registrar registrar = getByCode(code);
        Catalogue catalogue = registrar.getCatalogue(catalogueId);
        if (catalogue == null) {
            return null;
        }
        catalogue.setName(name);
        catalogue.setDescription(description);
        catalogue.setUrlPrefix(urlPrefix);
        Date date = new Date();
        catalogue.setModified(date);
        registrar.setModified(date);

        return catalogue;
    }

    public void deleteCatalogue(String code, long catalogueId){
        Registrar registrar = getByCode(code);
        registrar.getCatalogues().removeIf(x -> x.getId().equals(catalogueId));
        Date date = new Date();
        registrar.setModified(date);
    }
}
