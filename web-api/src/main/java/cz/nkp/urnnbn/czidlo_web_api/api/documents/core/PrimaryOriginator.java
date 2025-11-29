package cz.nkp.urnnbn.czidlo_web_api.api.documents.core;

import cz.nkp.urnnbn.core.OriginType;

public class PrimaryOriginator {
    private OriginType type;
    private String value;

    public static PrimaryOriginator from(cz.nkp.urnnbn.core.dto.Originator dto) {
        if (dto == null) {
            return null;
        }
        PrimaryOriginator result = new PrimaryOriginator();
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
}
