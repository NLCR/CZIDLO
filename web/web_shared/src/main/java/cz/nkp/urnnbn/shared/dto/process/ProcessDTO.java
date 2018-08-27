package cz.nkp.urnnbn.shared.dto.process;

import java.io.Serializable;
import java.util.Arrays;

public class ProcessDTO implements Serializable {

    private static final long serialVersionUID = -5696841071497272003L;

    private Long id;
    private String ownerLogin;
    private ProcessDTOType type;
    private ProcessDTOState state;
    private String[] params;
    // timestamps
    private Long scheduled;
    private Long started;
    private Long finished;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOwnerLogin() {
        return ownerLogin;
    }

    public void setOwnerLogin(String ownerLogin) {
        this.ownerLogin = ownerLogin;
    }

    public ProcessDTOType getType() {
        return type;
    }

    public void setType(ProcessDTOType type) {
        this.type = type;
    }

    public ProcessDTOState getState() {
        return state;
    }

    public void setState(ProcessDTOState state) {
        this.state = state;
    }

    public String[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }

    public Long getScheduled() {
        return scheduled;
    }

    public void setScheduled(Long scheduled) {
        this.scheduled = scheduled;
    }

    public Long getStarted() {
        return started;
    }

    public void setStarted(Long started) {
        this.started = started;
    }

    public Long getFinished() {
        return finished;
    }

    public void setFinished(Long finished) {
        this.finished = finished;
    }

    @Override
    public String toString() {
        return "ProcessDTO [id=" + id + ", ownerLogin=" + ownerLogin + ", type=" + type + ", state=" + state + ", params=" + Arrays.toString(params)
                + ", scheduled=" + scheduled + ", started=" + started + ", finished=" + finished + "]";
    }
}
