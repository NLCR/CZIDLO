package cz.nkp.urnnbn.czidlo_web_api.api.processes.process_manager;

import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.AccessRightException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.InvalidStateException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnknownRecordException;
import cz.nkp.urnnbn.czidlo_web_api.api.processes.core.*;
import cz.nkp.urnnbn.czidlo_web_api.api.processes.core.Process;
import cz.nkp.urnnbn.processmanager.control.ProcessResultManager;
import cz.nkp.urnnbn.processmanager.control.ProcessResultManagerImpl;
import cz.nkp.urnnbn.processmanager.scheduler.jobs.DiUrlAvailabilityCheckJob;
import cz.nkp.urnnbn.processmanager.scheduler.jobs.IndexationJob;
import cz.nkp.urnnbn.processmanager.scheduler.jobs.OaiAdapterJob;
import cz.nkp.urnnbn.processmanager.scheduler.jobs.UrnNbnCsvExportJob;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static cz.nkp.urnnbn.czidlo_web_api.api.processes.core.ProcessState.*;

public class ProcessManagerImpl implements ProcessManager {

    private cz.nkp.urnnbn.processmanager.control.ProcessManager rawProcessManager() {
        return cz.nkp.urnnbn.processmanager.control.ProcessManagerImpl.instanceOf();
    }

    @Override
    public void shutdown(boolean waitForJobsToFinish) {
        rawProcessManager().shutdown(waitForJobsToFinish);
    }

    @Override
    public Process scheduleNewProcess(String login, ProcessType type, Map<String, Object> processParams) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Process getProcess(User user, Long processId) throws UnknownRecordException, AccessRightException {
        try {
            cz.nkp.urnnbn.processmanager.core.Process process = rawProcessManager().getProcess(processId);
            if (process == null) {
                throw new UnknownRecordException("No such process with id=" + processId);
            }
            if (!user.isAdmin() && !user.getLogin().equals(process.getOwnerLogin())) {
                throw new AccessRightException("User " + user.getLogin() + " has no access rights to process with id=" + processId);
            }
            return rawProcessToProcess(process);
        } catch (cz.nkp.urnnbn.processmanager.persistence.UnknownRecordException e) {
            throw new UnknownRecordException(e.getMessage());
        }
    }

    @Override
    public Process getProcess(Long processId) throws UnknownRecordException {
        try {
            cz.nkp.urnnbn.processmanager.core.Process process = rawProcessManager().getProcess(processId);
            return rawProcessToProcess(process);
        } catch (cz.nkp.urnnbn.processmanager.persistence.UnknownRecordException e) {
            throw new UnknownRecordException(e.getMessage());
        }
    }

    @Override
    public List<Process> getAllProcesses() {
        List<cz.nkp.urnnbn.processmanager.core.Process> rawProcesses = rawProcessManager().getProcesses();
        return List.copyOf(
                rawProcesses.stream()
                        .map(this::rawProcessToProcess)
                        .toList()
        );
    }

    @Override
    public List<Process> getProcessesByState(ProcessState state) {
        List<cz.nkp.urnnbn.processmanager.core.Process> rawProcesses = rawProcessManager().getProcessesByState(state.rawValue());
        return List.copyOf(
                rawProcesses.stream()
                        .map(this::rawProcessToProcess)
                        .toList()
        );
    }

    @Override
    public List<Process> getProcessesScheduledAfter(Date date) {
        List<cz.nkp.urnnbn.processmanager.core.Process> rawProcesses = rawProcessManager().getProcessesScheduledAfter(date);
        return List.copyOf(
                rawProcesses.stream()
                        .map(this::rawProcessToProcess)
                        .toList()
        );
    }

    @Override
    public List<Process> getProcessesByOwner(String ownerLogin) {
        List<cz.nkp.urnnbn.processmanager.core.Process> rawProcesses = rawProcessManager().getProcessesByOwner(ownerLogin);
        return List.copyOf(
                rawProcesses.stream()
                        .map(this::rawProcessToProcess)
                        .toList()
        );
    }

    @Override
    public List<Process> getProcessesByOwnerScheduledAfter(String ownerLogin, Date date) {
        List<cz.nkp.urnnbn.processmanager.core.Process> rawProcesses = rawProcessManager().getProcessesByOwnerScheduledAfter(ownerLogin, date);
        return List.copyOf(
                rawProcesses.stream()
                        .map(this::rawProcessToProcess)
                        .toList()
        );
    }

    private Process rawProcessToProcess(cz.nkp.urnnbn.processmanager.core.Process rawProcess) {
        return Process.fromRawProcess(rawProcess);
    }

    @Override
    public boolean killRunningProcess(User user, Long processId) throws UnknownRecordException, AccessRightException, InvalidStateException {
        try {
            return rawProcessManager().killRunningProcess(user.getLogin(), processId);
        } catch (cz.nkp.urnnbn.processmanager.persistence.UnknownRecordException e) {
            throw new UnknownRecordException(e.getMessage());
        } catch (cz.nkp.urnnbn.processmanager.control.AccessRightException e) {
            throw new AccessRightException(e.getMessage());
        } catch (cz.nkp.urnnbn.processmanager.control.InvalidStateException e) {
            throw new InvalidStateException(e.getMessage());
        }
    }

    @Override
    public boolean cancelScheduledProcess(User user, Long processId) throws UnknownRecordException, AccessRightException, InvalidStateException {
        try {
            return rawProcessManager().cancelScheduledProcess(user.getLogin(), processId);
        } catch (cz.nkp.urnnbn.processmanager.persistence.UnknownRecordException e) {
            throw new UnknownRecordException(e.getMessage());
        } catch (cz.nkp.urnnbn.processmanager.control.AccessRightException e) {
            throw new AccessRightException(e.getMessage());
        } catch (cz.nkp.urnnbn.processmanager.control.InvalidStateException e) {
            throw new InvalidStateException(e.getMessage());
        }
    }

    @Override
    public void deleteProcess(User user, Long processId) throws UnknownRecordException, AccessRightException, InvalidStateException {
        try {
            rawProcessManager().deleteProcess(user.getLogin(), processId);
        } catch (cz.nkp.urnnbn.processmanager.persistence.UnknownRecordException e) {
            throw new UnknownRecordException(e.getMessage());
        } catch (cz.nkp.urnnbn.processmanager.control.AccessRightException e) {
            throw new AccessRightException(e.getMessage());
        } catch (cz.nkp.urnnbn.processmanager.control.InvalidStateException e) {
            throw new InvalidStateException(e.getMessage());
        }
    }

    @Override
    public FileInputStream getProcessLog(User user, Long processId) throws UnknownRecordException, AccessRightException, IOException {
        try {
            File file = getProcessResultManager().getProcessLogFile(user.getLogin(), processId);
            //System.out.println("Process log file path: " + file.getAbsolutePath());
            return new FileInputStream(file);
        } catch (cz.nkp.urnnbn.processmanager.persistence.UnknownRecordException e) {
            throw new UnknownRecordException(e.getMessage());
        } catch (cz.nkp.urnnbn.processmanager.control.InvalidStateException e) {
            throw new IOException(e.getMessage());
        } catch (cz.nkp.urnnbn.processmanager.control.AccessRightException e) {
            throw new AccessRightException(e.getMessage());
        }
    }

    @Override
    public ProcessOutputFile getProcessOutput(User user, Long processId) throws UnknownRecordException, AccessRightException, InvalidStateException, IOException {
        try {
            cz.nkp.urnnbn.processmanager.core.Process rawProcess = rawProcessManager().getProcess(user.getLogin(), processId);// for access right check
            Process process = rawProcessToProcess(rawProcess);
            if (List.of(SCHEDULED, CANCELED, RUNNING).contains(process.getState())) {
                throw new InvalidStateException("In invalid state \"" + process.getState() + "\" to return output file for process: " + processId);
            }
            ProcessOutputFileInfo outputFileInfo = new ProcessOutputFileInfo(process.getType());
            //System.err.println("output file name: " + outputFileInfo.getFilename() + ", mimeType: \"" + outputFileInfo.getMimetype() + "\"");
            File outputFile = getProcessResultManager().getProcessOutputFile(user.getLogin(), processId, outputFileInfo.getFilename());
            if (!outputFile.exists()) {
                throw new FileNotFoundException("Output file not found for process: " + processId);
            }
            return new ProcessOutputFileImpl(outputFileInfo.getMimetype(), outputFileInfo.getFilename(), outputFile);
        } catch (cz.nkp.urnnbn.processmanager.persistence.UnknownRecordException e) {
            throw new UnknownRecordException(e.getMessage());
        } catch (cz.nkp.urnnbn.processmanager.control.AccessRightException e) {
            throw new AccessRightException(e.getMessage());
        } catch (cz.nkp.urnnbn.processmanager.control.InvalidStateException e) {
            throw new InvalidStateException(e.getMessage());
        }
    }

    private ProcessResultManager getProcessResultManager() {
        return ProcessResultManagerImpl.instanceOf();
    }

    private static class ProcessOutputFileInfo {

        private final ProcessType type;

        private ProcessOutputFileInfo(ProcessType type) {
            this.type = type;
        }

        private String getMimetype() {
            switch (type) {
                case REGISTRARS_URN_NBN_CSV_EXPORT:
                case DI_URL_AVAILABILITY_CHECK:
                    return "text/csv; charset=UTF-8";
                case OAI_ADAPTER:
                case INDEXATION:
                    return "text/plain; charset=UTF-8";
                default:
                    throw new RuntimeException("MIME type of process output for process type " + type + " not defined");
            }
        }

        private String getFilename() {
            switch (type) {
                case REGISTRARS_URN_NBN_CSV_EXPORT:
                    return UrnNbnCsvExportJob.CSV_EXPORT_FILE_NAME;
                case OAI_ADAPTER:
                    return OaiAdapterJob.PARAM_REPORT_FILE;
                case DI_URL_AVAILABILITY_CHECK:
                    return DiUrlAvailabilityCheckJob.CSV_EXPORT_FILE_NAME;
                case INDEXATION:
                    return IndexationJob.PARAM_REPORT_FILE;
                default:
                    throw new RuntimeException("Filename of process output for process type " + type + " not defined");
            }
        }
    }
}
