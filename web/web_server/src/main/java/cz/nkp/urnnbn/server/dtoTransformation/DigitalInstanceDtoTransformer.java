package cz.nkp.urnnbn.server.dtoTransformation;

import cz.nkp.urnnbn.core.AccessRestriction;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.shared.dto.DigitalInstanceDTO;

public class DigitalInstanceDtoTransformer extends DtoTransformer {

    private final DigitalInstance instance;
    private final DigitalLibrary library;

    public DigitalInstanceDtoTransformer(DigitalInstance instance, DigitalLibrary library) {
        this.instance = instance;
        this.library = library;
    }

    public DigitalInstanceDTO transform() {
        DigitalInstanceDTO result = new DigitalInstanceDTO();
        result.setAccessibility(instance.getAccessibility());
        result.setAccessRestriction(convertAcessRestriction(instance.getAccessRestriction()));
        result.setFormat(instance.getFormat());
        result.setId(instance.getId());
        result.setCreated(dateTimeToStringOrNull(instance.getCreated()));
        result.setDeactivated(dateTimeToStringOrNull(instance.getDeactivated()));
        result.setUrl(instance.getUrl());
        result.setActive(instance.isActive());
        result.setLibrary(new DigitalLibraryDtoTransformer(library).transform());
        return result;
    }

    private DigitalInstanceDTO.ACCESS_RESTRICTION convertAcessRestriction(AccessRestriction accessRestriction) {
        if (accessRestriction == null) {
            return null;
        } else {
            switch (accessRestriction) {
                case UNKNOWN:
                    return DigitalInstanceDTO.ACCESS_RESTRICTION.UNKNOWN;
                case LIMITED_ACCESS:
                    return DigitalInstanceDTO.ACCESS_RESTRICTION.LIMITED_ACCESS;
                case UNLIMITED_ACCESS:
                    return DigitalInstanceDTO.ACCESS_RESTRICTION.UNLIMITED_ACCESS;
                default:
                    return null;
            }
        }
    }

}
