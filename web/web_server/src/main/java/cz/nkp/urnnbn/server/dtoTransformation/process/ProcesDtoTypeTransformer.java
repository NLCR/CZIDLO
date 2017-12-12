package cz.nkp.urnnbn.server.dtoTransformation.process;

import cz.nkp.urnnbn.processmanager.core.ProcessType;
import cz.nkp.urnnbn.shared.dto.process.ProcessDTOType;

public class ProcesDtoTypeTransformer {
    private final ProcessDTOType original;

    public ProcesDtoTypeTransformer(ProcessDTOType original) {
        this.original = original;
    }

    public ProcessType transform() {
        switch (original) {
            case OAI_ADAPTER:
                return ProcessType.OAI_ADAPTER;
            case REGISTRARS_URN_NBN_CSV_EXPORT:
                return ProcessType.REGISTRARS_URN_NBN_CSV_EXPORT;
            case DI_URL_AVAILABILITY_CHECK:
                return ProcessType.DI_URL_AVAILABILITY_CHECK;
            case INDEXATION:
                return ProcessType.INDEXATION;
            case TEST:
                return ProcessType.TEST;
            default:
                return null;
        }
    }
}
