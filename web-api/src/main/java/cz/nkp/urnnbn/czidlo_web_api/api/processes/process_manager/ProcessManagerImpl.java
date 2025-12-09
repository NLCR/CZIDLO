package cz.nkp.urnnbn.czidlo_web_api.api.processes.process_manager;

import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.AccessRightException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.InvalidStateException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnknownRecordException;
import cz.nkp.urnnbn.czidlo_web_api.api.processes.core.Process;
import cz.nkp.urnnbn.czidlo_web_api.api.processes.core.ProcessState;
import cz.nkp.urnnbn.czidlo_web_api.api.processes.core.ProcessType;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ProcessManagerImpl implements ProcessManager {

    private cz.nkp.urnnbn.processmanager.control.ProcessManager rawProcessManager() {
        return cz.nkp.urnnbn.processmanager.control.ProcessManagerImpl.instanceOf();
    }


    @Override
    public void shutdown(boolean waitForJobsToFinish) {
        //TODO: implement shutdown
    }

    @Override
    public Process scheduleNewProcess(String login, ProcessType type, Map<String, Object> processParams) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Process getProcess(String login, Long processId) throws UnknownRecordException, AccessRightException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Process getProcess(Long processId) throws UnknownRecordException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public List<Process> getProcesses() {
        List<cz.nkp.urnnbn.processmanager.core.Process> rawProcesses = rawProcessManager().getProcesses();
        return
                List.copyOf(
                        rawProcesses.stream()
                                .map(this::rawProcessToProcess)
                                .toList()
                );
    }

    private Process rawProcessToProcess(cz.nkp.urnnbn.processmanager.core.Process rawProcess) {
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

    @Override
    public List<Process> getProcessesByState(ProcessState state) {
        //return List.of();
        throw new RuntimeException("Not implemented");
    }

    @Override
    public List<Process> getProcessesScheduledAfter(Date date) {
        //return List.of();
        throw new RuntimeException("Not implemented");
    }

    @Override
    public List<Process> getProcessesByOwner(String ownerLogin) {
        //return List.of();
        throw new RuntimeException("Not implemented");
    }

    @Override
    public List<Process> getProcessesByOwnerScheduledAfter(String ownerLogin, Date date) {
        //return List.of();
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean killRunningProcess(String login, Long processId) throws UnknownRecordException, AccessRightException, InvalidStateException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean cancelScheduledProcess(String login, Long processId) throws UnknownRecordException, AccessRightException, InvalidStateException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void deleteProcess(String login, Long processId) throws UnknownRecordException, AccessRightException, InvalidStateException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public FileInputStream getProcessLog(String login, Long processId) throws UnknownRecordException, AccessRightException, IOException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public ProcessInMemoryOutputFile getProcessOutput(String login, Long processId) throws UnknownRecordException, AccessRightException, InvalidStateException, IOException {
        throw new RuntimeException("Not implemented");
    }
}
