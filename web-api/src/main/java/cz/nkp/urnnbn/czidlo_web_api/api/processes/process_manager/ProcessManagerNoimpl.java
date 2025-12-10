package cz.nkp.urnnbn.czidlo_web_api.api.processes.process_manager;

import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.czidlo_web_api.api.processes.core.Process;
import cz.nkp.urnnbn.czidlo_web_api.api.processes.core.ProcessOutputFile;
import cz.nkp.urnnbn.czidlo_web_api.api.processes.core.ProcessState;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.AccessRightException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.InvalidStateException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnknownRecordException;
import cz.nkp.urnnbn.czidlo_web_api.api.processes.core.ProcessType;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ProcessManagerNoimpl implements ProcessManager {

    @Override
    public void shutdown(boolean waitForJobsToFinish) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public cz.nkp.urnnbn.czidlo_web_api.api.processes.core.Process scheduleNewProcess(String login, ProcessType type, Map<String, Object> processParams) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public cz.nkp.urnnbn.czidlo_web_api.api.processes.core.Process getProcess(User user, Long processId) throws UnknownRecordException, AccessRightException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public cz.nkp.urnnbn.czidlo_web_api.api.processes.core.Process getProcess(Long processId) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public List<cz.nkp.urnnbn.czidlo_web_api.api.processes.core.Process> getAllProcesses() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public List<cz.nkp.urnnbn.czidlo_web_api.api.processes.core.Process> getProcessesByState(ProcessState state) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public List<cz.nkp.urnnbn.czidlo_web_api.api.processes.core.Process> getProcessesScheduledAfter(Date date) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public List<cz.nkp.urnnbn.czidlo_web_api.api.processes.core.Process> getProcessesByOwner(String ownerLogin) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public List<Process> getProcessesByOwnerScheduledAfter(String ownerLogin, Date date) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean killRunningProcess(User user, Long processId) throws UnknownRecordException, AccessRightException, InvalidStateException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean cancelScheduledProcess(User user, Long processId) throws UnknownRecordException, AccessRightException, InvalidStateException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void deleteProcess(User user, Long processId) throws UnknownRecordException, AccessRightException, InvalidStateException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public FileInputStream getProcessLog(User user, Long id) throws UnknownRecordException, AccessRightException, IOException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public ProcessOutputFile getProcessOutput(User user, Long processId) throws UnknownRecordException, AccessRightException, InvalidStateException, IOException {
        throw new RuntimeException("Not implemented");
    }
}
