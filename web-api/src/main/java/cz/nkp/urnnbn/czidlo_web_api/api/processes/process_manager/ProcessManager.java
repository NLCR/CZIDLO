package cz.nkp.urnnbn.czidlo_web_api.api.processes.process_manager;


import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.czidlo_web_api.api.processes.core.Process;
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

/**
 * @author Martin Řehánek
 */
public interface ProcessManager {

    /**
     * Closes opened resources of process manager and shuts down the process manager.
     *
     * @param waitForJobsToFinish If true, process manager is closed after all jobs finish. Otherwise still running jobs are killed.
     */
    public void shutdown(boolean waitForJobsToFinish);

    /**
     * Creates and schedules the process.
     *
     * @param login         login of user scheduling the process
     * @param type          type of process
     * @param processParams parameters of the process
     * @return instance of scheduled process
     */
    public Process scheduleNewProcess(String login, ProcessType type, Map<String, Object> processParams);

    /**
     * Returns process.
     *
     * @param user      user requesting the process
     * @param processId
     * @return Process object if user has access rights (admin or owner)
     * @throws UnknownRecordException If no such process exists
     * @throws AccessRightException   If user is not admin nor creator of the process
     */
    public Process getProcess(User user, Long processId) throws UnknownRecordException, AccessRightException;

    /**
     * Returns process.
     *
     * @param processId
     * @return
     * @throws UnknownRecordException If no such process exists
     */
    public Process getProcess(Long processId) throws UnknownRecordException;

    public List<Process> getAllProcesses();

    public List<Process> getProcessesByState(ProcessState state);

    public List<Process> getProcessesScheduledAfter(Date date);

    public List<Process> getProcessesByOwner(String ownerLogin);

    public List<Process> getProcessesByOwnerScheduledAfter(String ownerLogin, Date date);

    /**
     * Kills running process.
     *
     * @param user      user requesting the kill
     * @param processId process id
     * @return if the process was successfully killed
     * @throws UnknownRecordException If no such process exists
     * @throws AccessRightException   If user is not admin nor creator of the process
     * @throws InvalidStateException  If process is not in RUNNING state
     */
    public boolean killRunningProcess(User user, Long processId) throws UnknownRecordException, AccessRightException, InvalidStateException;

    /**
     * @param user      user requesting the cancel
     * @param processId process id
     * @return if the process was successfully canceled
     * @throws UnknownRecordException If no such process exists
     * @throws AccessRightException   If user is not admin nor creator of the process
     * @throws InvalidStateException  If process is not in SCHEDULED state
     */
    public boolean cancelScheduledProcess(User user, Long processId) throws UnknownRecordException, AccessRightException, InvalidStateException;

    /**
     * Deletes process record from database.
     *
     * @param user      user requesting the delete
     * @param processId process id
     * @throws UnknownRecordException If no such process exists
     * @throws AccessRightException   If user is not admin nor creator of the process
     * @throws InvalidStateException  If process is in RUNNING or SCHEDULED state
     */
    public void deleteProcess(User user, Long processId) throws UnknownRecordException, AccessRightException, InvalidStateException;

    /**
     * Returns process log from database.
     *
     * @param user      user requesting the log
     * @param processId process id
     * @return FileInputStream of process log
     * @throws UnknownRecordException If no such process exists
     * @throws AccessRightException   If user is not admin nor creator of the process
     * @throws IOException            If process log is missing or failed to read
     */
    public FileInputStream getProcessLog(User user, Long processId) throws UnknownRecordException, AccessRightException, IOException;

    /**
     * Returns process output file.
     *
     * @param user      user requesting the output file
     * @param processId process id
     * @return ProcessInMemoryOutputFile A custom class holding the output file and its MIME type
     * @throws UnknownRecordException If no such process exists
     * @throws AccessRightException   If user is not admin nor creator of the process
     * @throws InvalidStateException  If process is in RUNNING, CANCELED or SCHEDULED state
     * @throws IOException            If process output file is missing
     */
    public ProcessInMemoryOutputFile getProcessOutput(User user, Long processId) throws UnknownRecordException, AccessRightException, InvalidStateException, IOException;

}