package cz.nkp.urnnbn.server.dtoTransformation;

import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.shared.dto.UrnNbnDTO;

public class UrnNbnToDtoTransformer extends DtoTransformer {

	private final UrnNbn original;

	public UrnNbnToDtoTransformer(UrnNbn original) {
		this.original = original;
	}

	@Override
	public UrnNbnDTO transform() {
		return new UrnNbnDTO(original.getRegistrarCode().toString(), original.getDocumentCode(), original.getDigDocId());
	}
}
