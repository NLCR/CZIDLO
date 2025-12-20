package cz.nkp.urnnbn.czidlo_web_api.api.documents.core;

import cz.nkp.urnnbn.core.RegistrarScopeIdType;
import cz.nkp.urnnbn.core.RegistrarScopeIdValue;
import cz.nkp.urnnbn.core.dto.RegistrarScopeIdentifier;

import java.util.Date;
import java.util.List;

public class RsId {
    private Date created;
    private Date modified;
    private RegistrarScopeIdType type;
    private RegistrarScopeIdValue value;

    public static RsId from(RegistrarScopeIdentifier dto) {
        if (dto == null) {
            return null;
        }
        RsId result = new RsId();
        result.created = new Date(dto.getCreated().getMillis());
        result.modified = new Date(dto.getModified().getMillis());
        result.type = dto.getType();
        result.value = dto.getValue();
        return result;
    }

    public static List<RsId> fromList(java.util.List<RegistrarScopeIdentifier> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return java.util.List.of();
        }
        return dtos.stream().map(RsId::from).toList();
    }

    public Date getCreated() {
        return created;
    }

    public Date getModified() {
        return modified;
    }

    public String getType() {
        //musíme takto, protože jinak serializace RegistrarScopeIdType nefunguje, nestačí tam toString()
        return type == null ? null : type.toString();
    }

    public String getValue() {
        //musíme takto, protože jinak serializace RegistrarScopeIdValue nefunguje, nestačí tam toString()
        return value == null ? null : value.toString();
    }

    @Override
    public String toString() {
        return "RsId{" +
                "created=" + created +
                ", modified=" + modified +
                ", type=" + type +
                ", value=" + value +
                '}';
    }
}
