package cz.nkp.urnnbn.server.dtoTransformation;

import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.shared.dto.UrnNbnDTO;

public class DtoToUrnNbnTransformer {

	private final UrnNbnDTO original;

	public DtoToUrnNbnTransformer(UrnNbnDTO original) {
		this.original = original;
	}

	public UrnNbn transform() {
		return new UrnNbn(RegistrarCode.valueOf(original.getRegistrarCode()), original.getDocumentCode(), original.getDigdocId());
	}
}
