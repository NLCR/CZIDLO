package cz.nkp.urnnbn.server.dtoTransformation;

import cz.nkp.urnnbn.core.dto.RegistrarScopeIdentifier;
import cz.nkp.urnnbn.shared.dto.RegistrarScopeIdDTO;

/**
 * Created by Martin Řehánek on 7.11.18.
 */
public class RegistrarScopeIdDtoTransformer extends DtoTransformer {

    private final RegistrarScopeIdentifier original;

    public RegistrarScopeIdDtoTransformer(RegistrarScopeIdentifier original) {
        this.original = original;
    }

    @Override
    public RegistrarScopeIdDTO transform() {
        RegistrarScopeIdDTO result = new RegistrarScopeIdDTO();
        result.setRegistrarId(original.getRegistrarId());
        result.setDigDocId(original.getDigDocId());
        result.setType(original.getType().toString());
        result.setValue(original.getValue().toString());
        return result;
    }

}
