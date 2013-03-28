package cz.nkp.urnnbn.server.dtoTransformation.process;

import cz.nkp.urnnbn.processmanager.core.ProcessState;
import cz.nkp.urnnbn.server.dtoTransformation.DtoTransformer;
import cz.nkp.urnnbn.shared.dto.process.ProcessDTOState;

public class ProcessStateDtoTransformer extends DtoTransformer {

	private final ProcessState original;

	public ProcessStateDtoTransformer(ProcessState original) {
		this.original = original;
	}

	@Override
	public ProcessDTOState transform() {
		if (original == null) {
			return null;
		} else {
			switch (original) {
			case SCHEDULED:
				return ProcessDTOState.SCHEDULED;
			case CANCELED:
				return ProcessDTOState.CANCELED;
			case RUNNING:
				return ProcessDTOState.RUNNING;
			case FINISHED:
				return ProcessDTOState.FINISHED;
			case FAILED:
				return ProcessDTOState.FAILED;
			case KILLED:
				return ProcessDTOState.KILLED;
			default:
				return null;
			}
		}
	}

}
