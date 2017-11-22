package cz.nkp.urnnbn.server.dtoTransformation;

import cz.nkp.urnnbn.core.dto.Archiver;
import cz.nkp.urnnbn.shared.dto.ArchiverDTO;

public class DtoToArchiverTransformer {
    private final ArchiverDTO dto;

    public DtoToArchiverTransformer(ArchiverDTO dto) {
        this.dto = dto;
    }

    public Archiver transform() {
        Archiver result = new Archiver();
        if (dto.getId() != null) {
            result.setId(dto.getId());
        }
        result.setName(dto.getName());
        result.setDescription(dto.getDescription());
        result.setHidden(dto.isHidden());
        return result;
    }
}
