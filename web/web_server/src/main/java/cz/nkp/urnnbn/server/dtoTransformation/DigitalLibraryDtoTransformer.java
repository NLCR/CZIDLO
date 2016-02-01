package cz.nkp.urnnbn.server.dtoTransformation;

import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.shared.dto.DigitalLibraryDTO;

public class DigitalLibraryDtoTransformer extends DtoTransformer {

    private final DigitalLibrary library;

    public DigitalLibraryDtoTransformer(DigitalLibrary library) {
        this.library = library;
    }

    public DigitalLibraryDTO transform() {
        DigitalLibraryDTO result = new DigitalLibraryDTO();
        result.setId(library.getId());
        result.setRegistrarId(library.getRegistrarId());
        result.setCreated(dateTimeToStringOrNull(library.getCreated()));
        result.setModified(dateTimeToStringOrNull(library.getModified()));
        result.setName(library.getName());
        result.setDescription(library.getDescription());
        result.setUrl(library.getUrl());
        return result;
    }
}
