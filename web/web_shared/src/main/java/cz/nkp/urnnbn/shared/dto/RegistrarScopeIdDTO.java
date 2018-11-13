package cz.nkp.urnnbn.shared.dto;

import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RegistrarScopeIdDTO)) return false;
        RegistrarScopeIdDTO that = (RegistrarScopeIdDTO) o;
        return Objects.equals(digDocId, that.digDocId) &&
                Objects.equals(registrarId, that.registrarId) &&
                Objects.equals(type, that.type) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {

        return Objects.hash(digDocId, registrarId, type, value);
    }
}
