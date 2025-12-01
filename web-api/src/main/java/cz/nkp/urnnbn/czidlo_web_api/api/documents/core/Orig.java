package cz.nkp.urnnbn.czidlo_web_api.api.documents.core;

import cz.nkp.urnnbn.core.OriginType;

public class Orig {

    public OriginType type;
    public String value;

    public static Orig from(cz.nkp.urnnbn.core.dto.Originator dto) {
        if (dto == null) {
            return null;
        }
        Orig result = new Orig();
        result.type = dto.getType();
        result.value = dto.getValue();
        return result;
    }

    public OriginType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Orig{" +
                "type=" + type +
                ", value='" + value + '\'' +
                '}';
    }
}
