package cz.nkp.urnnbn.shared.dto;

import java.io.Serializable;

public class RegistrarScopeIdDTO implements Serializable {

    private static final long serialVersionUID = 7712499256636990045L;
    private Long digDocId;
    private Long registrarId;
    private String type;
    private String value;


    public Long getDigDocId() {
        return digDocId;
    }

    public void setDigDocId(Long digDocId) {
        this.digDocId = digDocId;
    }

    public Long getRegistrarId() {
        return registrarId;
    }

    public void setRegistrarId(Long registrarId) {
        this.registrarId = registrarId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "RegistrarScopeIdDTO{" +
                "digDocId=" + digDocId +
                ", registrarId=" + registrarId +
                ", type='" + type + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
