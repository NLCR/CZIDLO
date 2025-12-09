package cz.nkp.urnnbn.czidlo_web_api.api.processes.core;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.Date;
import java.util.Map;

@XmlRootElement(name = "process")
@XmlAccessorType(XmlAccessType.FIELD)
public class Process {

    private long id;
    private String ownerLogin;
    private ProcessState state;
    private ProcessType type;
    private Map<String, Object> params;
    private Date scheduled;
    private Date started;
    private Date finished;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ProcessType getType() {
        return type;
    }

    public void setType(ProcessType type) {
        this.type = type;
    }

    public String getOwnerLogin() {
        return ownerLogin;
    }

    public void setOwnerLogin(String ownerLogin) {
        this.ownerLogin = ownerLogin;
    }

    public ProcessState getState() {
        return state;
    }

    public void setState(ProcessState state) {
        this.state = state;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Date getScheduled() {
        return scheduled;
    }

    public void setScheduled(Date scheduled) {
        this.scheduled = scheduled;
    }

    public Date getStarted() {
        return started;
    }

    public void setStarted(Date started) {
        this.started = started;
    }

    public Date getFinished() {
        return finished;
    }

    public void setFinished(Date finished) {
        this.finished = finished;
    }

    public static Process fromRawProcess(cz.nkp.urnnbn.processmanager.core.Process rawProcess) {
        Process process = new Process();
        process.setId(rawProcess.getId());
        process.setOwnerLogin(rawProcess.getOwnerLogin());
        process.setType(ProcessType.valueOf(rawProcess.getType().name()));
        process.setState(ProcessState.valueOf(rawProcess.getState().name()));
        process.setScheduled(rawProcess.getScheduled());
        process.setStarted(rawProcess.getStarted());
        process.setFinished(rawProcess.getFinished());
        //process.setParams(rawProcess.getParams());
        return process;
    }

}
