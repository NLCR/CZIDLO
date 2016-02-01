package cz.nkp.urnnbn.server.dtoTransformation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.nkp.urnnbn.core.CountryCode;
import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.shared.dto.UrnNbnDTO;

public class UrnNbnToDtoTransformer extends DtoTransformer {

    private final UrnNbnWithStatus original;

    public UrnNbnToDtoTransformer(UrnNbnWithStatus original) {
        this.original = original;
    }

    @Override
    public UrnNbnDTO transform() {
        return new UrnNbnDTO(CountryCode.getCode(), original.getUrn().getRegistrarCode().toString(), original.getUrn().getDocumentCode(), original
                .getUrn().getDigDocId(), original.getUrn().isActive(), dateTimeToStringOrNull(original.getUrn().getReserved()),
                dateTimeToStringOrNull(original.getUrn().getRegistered()), dateTimeToStringOrNull(original.getUrn().getDeactivated()),
                transformList(original.getUrn().getPredecessors()), transformList(original.getUrn().getSuccessors()), original.getNote(), original
                        .getUrn().getDeactivationNote());
    }

    private static List<UrnNbnDTO> transformList(List<UrnNbnWithStatus> originalList) {
        if (originalList == null || originalList.isEmpty()) {
            return Collections.<UrnNbnDTO> emptyList();
        } else {
            List<UrnNbnDTO> result = new ArrayList<UrnNbnDTO>(originalList.size());
            for (UrnNbnWithStatus original : originalList) {
                result.add(new UrnNbnToDtoTransformer(original).transform());
            }
            return result;
        }
    }
}
