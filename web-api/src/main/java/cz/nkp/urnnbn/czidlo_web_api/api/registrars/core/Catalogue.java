package cz.nkp.urnnbn.czidlo_web_api.api.registrars.core;

import cz.nkp.urnnbn.czidlo_web_api.api.Utils;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.Date;

@XmlRootElement(name = "catalogue")
@XmlAccessorType(XmlAccessType.FIELD)
public class Catalogue {
    private Long id;
    private String name;
    private String description;
    private String urlPrefix;
    private Date created;
    private Date modified;

    public static Catalogue fromDto(cz.nkp.urnnbn.core.dto.Catalog dto) {
        Catalogue result = new Catalogue();
        result.setId(dto.getId());
        result.setName(dto.getName());
        result.setDescription(dto.getDescription());
        result.setUrlPrefix(dto.getUrlPrefix());
        result.setCreated(Utils.dateTimeToDate(dto.getCreated()));
        result.setModified(Utils.dateTimeToDate(dto.getModified()));
        return result;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getUrlPrefix() {
        return urlPrefix;
    }

    public void setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
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
}
