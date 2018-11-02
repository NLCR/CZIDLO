package cz.nkp.urnnbn.client.processes;

import cz.nkp.urnnbn.shared.dto.process.XmlTransformationDTO;

/**
 * Created by Martin Řehánek on 28.8.18.
 */
public class TransformationButtonAction {

    private final String btnImgUrl;
    private final String hint;
    private final Operation operation;

    public TransformationButtonAction(String btnImgUrl, String hint, Operation operation) {
        this.btnImgUrl = btnImgUrl;
        this.hint = hint;
        this.operation = operation;
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

    public interface Operation {
        void run(XmlTransformationDTO process);
    }
}
