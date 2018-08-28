package cz.nkp.urnnbn.client.processes;

import cz.nkp.urnnbn.shared.dto.process.ProcessDTO;
import cz.nkp.urnnbn.shared.dto.process.ProcessDTOState;

/**
 * Created by Martin Řehánek on 28.8.18.
 */
public class ProcessButtonAction {

    private final String btnImgUrl;
    private final String hint;
    private final Operation operation;
    private final ProcessDTOState[] states;


    public ProcessButtonAction(String btnImgUrl, String hint, Operation operation, ProcessDTOState... states) {
        this.btnImgUrl = btnImgUrl;
        this.hint = hint;
        this.operation = operation;
        this.states = states;
    }

    public String getBtnImgUrl() {
        return btnImgUrl;
    }

    public String getHint() {
        return hint;
    }

    public Operation getOperation() {
        return operation;
    }

    public ProcessDTOState[] getStates() {
        return states;
    }

    public interface Operation {
        void run(ProcessDTO process);
    }
}
