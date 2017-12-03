package cz.nkp.urnnbn.server.dtoTransformation;

import cz.nkp.urnnbn.core.AccessRestriction;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.shared.dto.DigitalInstanceDTO;
import cz.nkp.urnnbn.shared.dto.UrnNbnDTO;

public class DtoToDigitalInstanceTransformer {

    private final DigitalInstanceDTO original;
    private final UrnNbnDTO urn;

    public DtoToDigitalInstanceTransformer(DigitalInstanceDTO original, UrnNbnDTO urn) {
        this.original = original;
        this.urn = urn;
    }

    public DigitalInstance transform() {
        DigitalInstance result = new DigitalInstance();
        result.setAccessibility(original.getAccessibility());
        result.setAccessRestriction(convertAcessRestriction(original.getAccessRestriction()));
        result.setDigDocId(urn.getDigdocId());
        result.setFormat(original.getFormat());
        result.setId(original.getId());
        result.setActive(original.isActive());
        result.setLibraryId(original.getLibrary().getId());
        result.setUrl(original.getUrl());
        return result;
    }

    private AccessRestriction convertAcessRestriction(DigitalInstanceDTO.ACCESS_RESTRICTION accessRestriction) {
        if (accessRestriction == null) {
            return null;
        } else {
            switch (accessRestriction) {
                case UNKNOWN:
                    return AccessRestriction.UNKNOWN;
                case LIMITED_ACCESS:
                    return AccessRestriction.LIMITED_ACCESS;
                case UNLIMITED_ACCESS:
                    return AccessRestriction.UNLIMITED_ACCESS;
                default:
                    return null;
            }
        }
    }
}
