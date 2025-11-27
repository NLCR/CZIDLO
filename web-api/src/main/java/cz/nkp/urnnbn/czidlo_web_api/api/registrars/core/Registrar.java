package cz.nkp.urnnbn.czidlo_web_api.api.registrars.core;

import cz.nkp.urnnbn.core.UrnNbnRegistrationMode;
import cz.nkp.urnnbn.czidlo_web_api.api.Utils;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@XmlRootElement(name = "registrar")
@XmlAccessorType(XmlAccessType.FIELD)
public class Registrar {
    private Long id;
    private String code;
    private String name;
    private String description;
    private Date created;
    private Date modified;
    private boolean allowedRegistrationModeByResolver;
    private boolean allowedRegistrationModeByReservation;
    private boolean allowedRegistrationModeByRegistrar;
    private boolean isHidden = false;
    private List<DigitalLibrary> digitalLibraries = new ArrayList<>();
    private List<Catalogue> catalogues = new ArrayList<>();

    public static Registrar from(cz.nkp.urnnbn.core.dto.Registrar reg, List<DigitalLibrary> libraries, List<Catalogue> catalogues) {
        Registrar result = new Registrar();
        result.setId(reg.getId());
        result.setCode(reg.getCode().toString());
        result.setName(reg.getName());
        result.setDescription(reg.getDescription());
        result.setCreated(Utils.dateTimeToDate(reg.getCreated()));
        result.setModified(Utils.dateTimeToDate(reg.getModified()));
        result.setAllowedRegistrationModeByResolver(reg.isRegistrationModeAllowed(UrnNbnRegistrationMode.BY_RESOLVER));
        result.setAllowedRegistrationModeByReservation(reg.isRegistrationModeAllowed(UrnNbnRegistrationMode.BY_RESERVATION));
        result.setAllowedRegistrationModeByRegistrar(reg.isRegistrationModeAllowed(UrnNbnRegistrationMode.BY_REGISTRAR));
        result.setHidden(reg.isHidden());
        result.setDigitalLibraries(libraries);
        result.setCatalogues(catalogues);
        return result;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public boolean isAllowedRegistrationModeByResolver() {
        return allowedRegistrationModeByResolver;
    }

    public void setAllowedRegistrationModeByResolver(boolean allowedRegistrationModeByResolver) {
        this.allowedRegistrationModeByResolver = allowedRegistrationModeByResolver;
    }

    public boolean isAllowedRegistrationModeByReservation() {
        return allowedRegistrationModeByReservation;
    }

    public void setAllowedRegistrationModeByReservation(boolean allowedRegistrationModeByReservation) {
        this.allowedRegistrationModeByReservation = allowedRegistrationModeByReservation;
    }

    public boolean isAllowedRegistrationModeByRegistrar() {
        return allowedRegistrationModeByRegistrar;
    }

    public void setAllowedRegistrationModeByRegistrar(boolean allowedRegistrationModeByRegistrar) {
        this.allowedRegistrationModeByRegistrar = allowedRegistrationModeByRegistrar;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        this.isHidden = hidden;
    }

    public List<DigitalLibrary> getDigitalLibraries() {
        return digitalLibraries;
    }

    public DigitalLibrary getDigitalLibrary(long id) {
        return digitalLibraries.stream().filter(x -> x.getId() == id).findFirst().orElse(null);
    }

    public void addDigitalLibraries(DigitalLibrary digitalLibrary) {
        this.digitalLibraries.add(digitalLibrary);
    }

    public List<Catalogue> getCatalogues() {
        return catalogues;
    }

    public Catalogue getCatalogue(long id) {
        return catalogues.stream().filter(x -> x.getId() == id).findFirst().orElse(null);
    }

    public void addCatalogue(Catalogue catalogue) {
        this.catalogues.add(catalogue);
    }

    public void setDigitalLibraries(List<DigitalLibrary> digitalLibraries) {
        this.digitalLibraries = digitalLibraries;
    }

    public void setCatalogues(List<Catalogue> catalogues) {
        this.catalogues = catalogues;
    }
}
