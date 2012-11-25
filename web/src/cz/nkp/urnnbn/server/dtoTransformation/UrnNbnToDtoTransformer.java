package cz.nkp.urnnbn.server.dtoTransformation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.nkp.urnnbn.core.CountryCode;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.shared.dto.UrnNbnDTO;

public class UrnNbnToDtoTransformer extends DtoTransformer {

	private final UrnNbn original;

	public UrnNbnToDtoTransformer(UrnNbn original) {
		this.original = original;
	}

	@Override
	public UrnNbnDTO transform() {
		return new UrnNbnDTO(CountryCode.getCode(), original.getRegistrarCode().toString(), original.getDocumentCode(),
				original.getDigDocId(), original.isActive(), dateTimeToStringOrNull(original.getCreated()),
				dateTimeToStringOrNull(original.getModified()), transformList(original.getPredecessors()),
				transformList(original.getSuccessors()));
	}

	private static List<UrnNbnDTO> transformList(List<UrnNbn> originalList) {
		if (originalList == null || originalList.isEmpty()) {
			return Collections.<UrnNbnDTO> emptyList();
		} else {
			List<UrnNbnDTO> result = new ArrayList<UrnNbnDTO>(originalList.size());
			for (UrnNbn original : originalList) {
				result.add(new UrnNbnToDtoTransformer(original).transform());
			}
			return result;
		}
	}
}
