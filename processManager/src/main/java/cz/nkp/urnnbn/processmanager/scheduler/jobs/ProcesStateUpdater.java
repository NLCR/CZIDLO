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
package cz.nkp.urnnbn.processmanager.scheduler.jobs;

import cz.nkp.urnnbn.processmanager.core.ProcessState;
import cz.nkp.urnnbn.processmanager.persistence.ProcessDAOImpl;
import cz.nkp.urnnbn.processmanager.persistence.UnknownRecordException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Řehánek
 */
public class ProcesStateUpdater {

    private final Long processId;

    public ProcesStateUpdater(Long processId) {
        this.processId = processId;
    }

    public void updateProcessStateToKilled() {
        updateProcessStatToFinished(ProcessState.KILLED);
    }

    public void updateProcessStateToFinished() {
        updateProcessStatToFinished(ProcessState.FINISHED);
    }

    public void updateProcessStateToFailed() {
        updateProcessStatToFinished(ProcessState.FAILED);
    }

    public void updateProcessStatToFinished(ProcessState state) {
        try {
            cz.nkp.urnnbn.processmanager.core.Process process = ProcessDAOImpl.instanceOf().getProcess(processId);
            process.setState(state);
            process.setFinished(new Date());
            ProcessDAOImpl.instanceOf().updateProcess(process);
        } catch (UnknownRecordException ex) {
            //TODO:logovat, ale stejne, jako u ostatnich procesu
            System.err.println("cannot set process state  to " + state + "process not found " + processId);
            Logger.getLogger(AbstractJob.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateProcessStateToRunning() {
        try {
            cz.nkp.urnnbn.processmanager.core.Process process = ProcessDAOImpl.instanceOf().getProcess(processId);
            process.setState(ProcessState.RUNNING);
            process.setStarted(new Date());
            ProcessDAOImpl.instanceOf().updateProcess(process);
        } catch (UnknownRecordException ex) {
            //TODO:logovat, ale stejne, jako u ostatnich procesu
            System.err.println("cannot set process state  to " + ProcessState.RUNNING + "process not found " + processId);
            Logger.getLogger(AbstractJob.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void upadateProcessStateToCanceled() {
        try {
            cz.nkp.urnnbn.processmanager.core.Process process = ProcessDAOImpl.instanceOf().getProcess(processId);
            process.setState(ProcessState.CANCELED);
            ProcessDAOImpl.instanceOf().updateProcess(process);
        } catch (UnknownRecordException ex) {
            //TODO:logovat, ale stejne, jako u ostatnich procesu
            System.err.println("cannot set process state  to " + ProcessState.CANCELED + "process not found " + processId);
            Logger.getLogger(AbstractJob.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
