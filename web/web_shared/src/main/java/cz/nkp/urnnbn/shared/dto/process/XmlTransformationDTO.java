package cz.nkp.urnnbn.shared.dto.process;

import java.io.Serializable;

public class XmlTransformationDTO implements Serializable {

    private static final long serialVersionUID = 8523834282286630993L;
    private Long id;
    private String name;
    private String description;
    private String ownerLogin;
    private XmlTransformationDTOType type;
    private Long created;
    private String templateTemporaryFile;

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

    public String getOwnerLogin() {
        return ownerLogin;
    }

    public void setOwnerLogin(String ownerLogin) {
        this.ownerLogin = ownerLogin;
    }

    public XmlTransformationDTOType getType() {
        return type;
    }

    public void setType(XmlTransformationDTOType type) {
        this.type = type;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public String getTemplateTemporaryFile() {
        return templateTemporaryFile;
    }

    public void setTemplateTemporaryFile(String templateTemporaryFile) {
        this.templateTemporaryFile = templateTemporaryFile;
    }

    @Override
    public String toString() {
        return "XmlTransformationDTO [id=" + id + ", name=" + name + ", description=" + description + ", ownerLogin=" + ownerLogin + ", type=" + type
                + ", created=" + created + ", templateTemporaryFile=" + templateTemporaryFile + "]";
    }
}
