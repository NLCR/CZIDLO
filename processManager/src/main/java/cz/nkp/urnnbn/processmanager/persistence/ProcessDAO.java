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
package cz.nkp.urnnbn.processmanager.persistence;

import cz.nkp.urnnbn.processmanager.core.Process;
import cz.nkp.urnnbn.processmanager.core.ProcessState;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Martin Řehánek
 */
public interface ProcessDAO {

    /**
     * Persists the preocess.
     *
     * @param newProcess
     * @return
     */
    public Process saveProcess(Process newProcess);

    /**
     * Returns process by id or null.
     *
     * @param processId
     *            process id (primary key)
     * @return Process
     * @throws UnknownRecordException
     *             if no such process exists
     */
    public Process getProcess(Long processId) throws UnknownRecordException;

    /**
     * Returns all processes.
     *
     * @return all processes
     */
    public List<Process> getProcesses();

    public List<Process> getProcessesByState(ProcessState state);

    /**
     * Returns list of processes with timestamp SCHEDULED after this date.
     *
     * @param date
     *            all returned process have SCHEDULED date after this
     * @return list of processes with timestamp SCHEDULED after this date
     */
    public List<Process> getProcessesScheduledAfter(Date date);

    /**
     * List of process of given user.
     *
     * @param userLogin
     *            login of the user
     * @return list of processes of given user
     */
    public List<Process> getProcessesOfUser(String userLogin);

    /**
     * Returns list of processes that belong to given user with timestamp SCHEDULED after this date.
     *
     * @param userLogin
     *            login of user that processes belong to
     * @param date
     *            all returned process have SCHEDULED date after this
     * @return list of processes belonging to given user with timestamp SCHEDULED after this date
     */
    public List<Process> getProcessesOfUserScheduledAfter(String userLogin, Date date);

    /**
     * Updates process in database.
     *
     * @param updated
     *            process to be updated
     * @throws UnknownRecordException
     *             if the process doesn't exist
     */
    public void updateProcess(Process updated) throws UnknownRecordException;

    /**
     *
     * @param process
     * @throws UnknownRecordException
     *             if the process doesn't exist
     */
    public void deleteProcess(Process process) throws UnknownRecordException;
}
