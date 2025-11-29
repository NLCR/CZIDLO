package cz.nkp.urnnbn.czidlo_web_api.api.documents.core;

import cz.nkp.urnnbn.czidlo_web_api.api.Utils;

import java.util.Date;

public class DigLib {

    private Long id;
    private Long registrarId;
    private Date created;
    private Date modified;
    private String name;
    private String description;
    private String url;

    public static DigLib from(cz.nkp.urnnbn.core.dto.DigitalLibrary dto) {
        if (dto == null) {
            return null;
        }
        DigLib result = new DigLib();
        result.id = dto.getId();
        result.registrarId = dto.getRegistrarId();
        result.created = Utils.dateTimeToDate(dto.getCreated());
        result.modified = Utils.dateTimeToDate(dto.getModified());
        result.name = dto.getName();
        result.description = dto.getDescription();
        result.url = dto.getUrl();
        return result;
    }

    public Long getId() {
        return id;
    }

    public Long getRegistrarId() {
        return registrarId;
    }

    public Date getCreated() {
        return created;
    }

    public Date getModified() {
        return modified;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }
}
