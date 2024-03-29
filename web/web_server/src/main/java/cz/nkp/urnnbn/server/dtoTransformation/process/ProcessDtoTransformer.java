package cz.nkp.urnnbn.server.dtoTransformation.process;

import cz.nkp.urnnbn.processmanager.core.Process;
import cz.nkp.urnnbn.server.dtoTransformation.DtoTransformer;
import cz.nkp.urnnbn.shared.dto.process.ProcessDTO;

public class ProcessDtoTransformer extends DtoTransformer {

    private final Process original;

    public ProcessDtoTransformer(Process original) {
        this.original = original;
    }

    public ProcessDTO transform() {
        if (original == null) {
            return null;
        } else {
            ProcessDTO result = new ProcessDTO();
            result.setId(original.getId());
            result.setOwnerLogin(original.getOwnerLogin());
            result.setParams(original.getParams());
            result.setScheduled(dateToMillisOrNull(original.getScheduled()));
            result.setStarted(dateToMillisOrNull(original.getStarted()));
            result.setFinished(dateToMillisOrNull(original.getFinished()));
            result.setType(new ProcessTypeDtoTransformer(original.getType()).transform());
            result.setState(new ProcessStateDtoTransformer(original.getState()).transform());
            return result;
        }
    }
}
