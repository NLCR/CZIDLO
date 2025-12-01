package cz.nkp.urnnbn.czidlo_web_api.api.documents.core;

import cz.nkp.urnnbn.core.IntEntIdType;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;

import java.util.List;

public class IeId {

    public IntEntIdType type;
    public String value;

    public static IeId from(IntEntIdentifier dto) {
        if (dto == null) {
            return null;
        }
        IeId result = new IeId();
        result.type = dto.getType();
        result.value = dto.getValue();
        return result;
    }

    public IntEntIdentifier toDto() {
        IntEntIdentifier dto = new IntEntIdentifier();
        dto.setType(type);
        dto.setValue(value);
        return dto;
    }

    public static List<IeId> fromlist(List<IntEntIdentifier> intEntIdentifiers) {
        if (intEntIdentifiers == null || intEntIdentifiers.isEmpty()) {
            return List.of();
        }
        return intEntIdentifiers.stream().map(IeId::from).toList();
    }

    public IntEntIdType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
