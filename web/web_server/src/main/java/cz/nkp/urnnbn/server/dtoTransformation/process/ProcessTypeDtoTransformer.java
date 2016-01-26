package cz.nkp.urnnbn.server.dtoTransformation.process;

import cz.nkp.urnnbn.processmanager.core.ProcessType;
import cz.nkp.urnnbn.server.dtoTransformation.DtoTransformer;
import cz.nkp.urnnbn.shared.dto.process.ProcessDTOType;

public class ProcessTypeDtoTransformer extends DtoTransformer {
	private final ProcessType original;

	public ProcessTypeDtoTransformer(ProcessType original) {
		this.original = original;
	}

	@Override
	public ProcessDTOType transform() {
		if (original == null) {
			return null;
		} else {
			switch (original) {
			case OAI_ADAPTER:
				return ProcessDTOType.OAI_ADAPTER;
			case REGISTRARS_URN_NBN_CSV_EXPORT:
				return ProcessDTOType.REGISTRARS_URN_NBN_CSV_EXPORT;
			case TEST:
				return ProcessDTOType.TEST;
			default:
				return null;
			}
		}
	}
}
