package cz.nkp.urnnbn.czidlo_web_api.api.documents.core;

import cz.nkp.urnnbn.core.AccessRestriction;
import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.czidlo_web_api.api.Utils;

import java.util.Date;

public class DigInst {
    private Long id;
    private Date created;
    private Date deactivated;
    private Boolean active;
    private String url;
    private String format;
    private String accessibility;
    private AccessRestriction accessRestriction;
    private DigLib library;
    private String registrarCode;

    public static DigInst from(DigitalInstance dto, DigitalLibrary digitalLibrary, String registrarCode) {
        if (dto == null) {
            return null;
        }
        DigInst result = new DigInst();
        result.id = dto.getId();
        result.created = Utils.dateTimeToDate(dto.getCreated());
        result.deactivated = Utils.dateTimeToDate(dto.getDeactivated());
        result.active = dto.isActive();
        result.url = dto.getUrl();
        result.format = dto.getFormat();
        result.accessibility = dto.getAccessibility();
        result.accessRestriction = dto.getAccessRestriction();
        result.library = DigLib.from(digitalLibrary);
        result.registrarCode = registrarCode;
        return result;
    }

    public Long getId() {
        return id;
    }

    public Date getCreated() {
        return created;
    }

    public Date getDeactivated() {
        return deactivated;
    }

    public Boolean getActive() {
        return active;
    }

    public String getUrl() {
        return url;
    }

    public String getFormat() {
        return format;
    }

    public String getAccessibility() {
        return accessibility;
    }

    public AccessRestriction getAccessRestriction() {
        return accessRestriction;
    }

    public DigLib getLibrary() {
        return library;
    }

    public String getRegistrarCode() {
        return registrarCode;
    }
}
