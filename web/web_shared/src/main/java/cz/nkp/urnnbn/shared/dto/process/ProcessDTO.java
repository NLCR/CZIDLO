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
    private String scheduled;
    private String started;
    private String finished;

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

    public String getScheduled() {
        return scheduled;
    }

    public void setScheduled(String scheduled) {
        this.scheduled = scheduled;
    }

    public String getStarted() {
        return started;
    }

    public void setStarted(String started) {
        this.started = started;
    }

    public String getFinished() {
        return finished;
    }

    public void setFinished(String finished) {
        this.finished = finished;
    }

    @Override
    public String toString() {
        return "ProcessDTO [id=" + id + ", ownerLogin=" + ownerLogin + ", type=" + type + ", state=" + state + ", params=" + Arrays.toString(params)
                + ", scheduled=" + scheduled + ", started=" + started + ", finished=" + finished + "]";
    }
}
