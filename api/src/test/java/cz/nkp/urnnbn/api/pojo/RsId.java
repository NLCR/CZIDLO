package cz.nkp.urnnbn.api.pojo;

public class RsId {
    public final String registrarCode;
    public final String type;
    public final String value;

    public RsId(String registrarCode, String type, String value) {
        this.registrarCode = registrarCode;
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return "RsId [registrarCode=" + registrarCode + ", type=" + type + ", value=" + value + "]";
    }
}
