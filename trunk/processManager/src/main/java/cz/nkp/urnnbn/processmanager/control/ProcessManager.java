/*
 * Copyright (C) 2013 Martin Řehánek
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.nkp.urnnbn.processmanager.control;

import cz.nkp.urnnbn.processmanager.core.Process;
import cz.nkp.urnnbn.processmanager.core.ProcessState;
import cz.nkp.urnnbn.processmanager.core.ProcessType;
import cz.nkp.urnnbn.processmanager.persistence.UnknownRecordException;
import java.io.File;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Martin Řehánek
 */
public interface ProcessManager {

    /**
     * Closes opened resources of process manager and shuts down the process
     * manager.
     *
     * @param waitForJobsToFinish If true, process manager is closed after all
     * jobs finish. Otherwise still running jobs are killed.
     */
    public void shutdown(boolean waitForJobsToFinish);

    /**
     * Creates and schedules the process.
     *
     * @param userLogin login of user scheduling the process
     * @param type type of process
     * @param processParams parameters of the process
     * @return instance of scheduled process
     */
    public Process scheduleNewProcess(String login, ProcessType type, String[] processParams);

    /**
     * Returns process.
     *
     * @param login
     * @param processId
     * @return
     * @throws UnknownRecordException If no such process exists
     * @throws AccessRightException If user is not admin nor creator of the
     * process
     */
    public Process getProcess(String login, Long processId) throws UnknownRecordException, AccessRightException;

    public List<Process> getProcesses();

    public List<Process> getProcessesByState(ProcessState state);

    public List<Process> getProcessesScheduledAfter(Date date);

    public List<Process> getProcessesByOwner(String ownerLogin);

    public List<Process> getProcessesByOwnerScheduledAfter(String ownerLogin, Date date);

    /**
     * Returns process log file.
     *
     * @param login
     * @param processId
     * @return
     * @throws UnknownRecordException If no such process exists
     * @throws AccessRightException If user is not admin nor creator of the
     * process
     * @throws InvalidStateException If process is SCHEDULED
     */
    public File getProcessLogFile(String login, Long processId) throws UnknownRecordException, AccessRightException, InvalidStateException;

    /**
     * Returns output process output file.
     *
     * @param login
     * @param processId
     * @param filename
     * @return
     * @throws UnknownRecordException If no such process exists
     * @throws AccessRightException If user is not admin nor creator of the
     * process
     * @throws InvalidStateException If process is not FINISHED
     */
    public File getProcessOutputFile(String login, Long processId, String filename) throws UnknownRecordException, AccessRightException, InvalidStateException;

    /**
     * Kills running process.
     *
     * @param login
     * @param processId
     * @return
     * @throws UnknownRecordException If no such process exists
     * @throws AccessRightException If user is not admin nor creator of the
     * process
     * @throws InvalidStateException If process is not RUNNING.
     */
    public boolean killProcess(String login, Long processId) throws UnknownRecordException, AccessRightException, InvalidStateException;

    /**
     * Deletes process record from database.
     *
     * @param login
     * @param processId
     * @throws UnknownRecordException If no such process exists
     * @throws AccessRightException If user is not admin nor creator of the
     * process
     * @throws InvalidStateException If process is RUNNING or SCHEDULED.
     */
    public void deleteProcess(String login, Long processId) throws UnknownRecordException, AccessRightException, InvalidStateException;
}
