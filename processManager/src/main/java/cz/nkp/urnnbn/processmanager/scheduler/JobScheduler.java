/*
 * Copyright (C) 2012 Martin Řehánek
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
package cz.nkp.urnnbn.processmanager.scheduler;

import cz.nkp.urnnbn.processmanager.core.Process;
import java.io.FileInputStream;

/**
 *
 * @author Martin Řehánek
 */
public interface JobScheduler {

    /**
     * Creates and runs job from given process. Assings the OS specific jobId to the process.
     *
     * @param process
     *            process to be run. Must have proces id set
     * @return OS specific id of the running job
     * @throws JobException
     */
    public Long runJob(Process process) throws JobException;

    /**
     * Kills job representing this process. If this method finishes without throwing exception, the job and process can now be considered finished.
     *
     * @param processId
     *            OS specific recyclable job id
     * @throws JobException
     */
    public void killJob(Long processId) throws JobException;

    /**
     * Informs whether job of process is running.
     *
     * @param jobId
     *            OS specific recyclable job id
     * @return true if OS job representing the process is running
     * @throws JobException
     */
    public boolean jobIsRunning(Long jobId) throws JobException;

    /**
     * Informs whether job of process finished correctly provided it is no longer running.
     *
     * @param process
     *            process with its jobId set
     * @param jobId
     *            OS specific recyclable job id
     * @param processId
     *            persistent process id
     * @return true if no longer running OS job representing the process finished its execution as expected
     * @throws JobException
     */
    public boolean jobFinishedCorrectly(Long jobId, Long processId) throws JobException;

    /**
     * Opens job file and returns its FileInputStream. The stream has to be closed by client after reading is finished.
     *
     * @param processId
     *            persistent process id
     * @param filename
     *            name of file in directory dedicated to process
     * @return
     * @throws JobException
     */
    public FileInputStream jobFileInputStream(Long processId, String filename) throws JobException;

    /**
     * Reinitializes the scheduler - removes data of all previously run processes.
     *
     * @throws JobException
     *             in case of error while cleaning scheduler data.
     */
    public void reinitializeScheduler() throws JobException;
}
