package cz.nkp.urnnbn.server.dtoTransformation;

import cz.nkp.urnnbn.core.RegistrarScopeIdType;
import cz.nkp.urnnbn.core.RegistrarScopeIdValue;
import cz.nkp.urnnbn.core.dto.RegistrarScopeIdentifier;
import cz.nkp.urnnbn.shared.dto.RegistrarScopeIdDTO;

/**
 * Created by Martin Řehánek on 13.11.18.
 */
public class DtoToRegistrarScopeIdTransformer {

    private final RegistrarScopeIdDTO dto;

    public DtoToRegistrarScopeIdTransformer(RegistrarScopeIdDTO dto) {
        this.dto = dto;
    }

    public RegistrarScopeIdentifier transform() {
        RegistrarScopeIdentifier result = new RegistrarScopeIdentifier();
        result.setRegistrarId(dto.getRegistrarId());
        result.setDigDocId(dto.getDigDocId());
        result.setType(RegistrarScopeIdType.valueOf(dto.getType()));
        result.setValue(RegistrarScopeIdValue.valueOf(dto.getValue()));
        return result;
    }
}
