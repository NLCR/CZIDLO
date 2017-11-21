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
package cz.nkp.urnnbn.processmanager.scheduler;

import cz.nkp.urnnbn.core.AdminLogger;
import cz.nkp.urnnbn.processmanager.core.ProcessState;
import cz.nkp.urnnbn.processmanager.core.ProcessType;
import cz.nkp.urnnbn.processmanager.scheduler.jobs.AbstractJob;
import cz.nkp.urnnbn.processmanager.scheduler.jobs.ProcesStateUpdater;
import org.quartz.*;

/**
 * @author Martin Řehánek
 */
public class JobListenerImpl implements JobListener {

    @Override
    public String getName() {
        return JobListenerImpl.class.getName();
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        Long jobId = getJobId(context);
        JobDataMap processData = context.getMergedJobDataMap();
        new ProcesStateUpdater(jobId).updateProcessStateToRunning();
        logProcessStarted(processData, jobId);
    }

    private void logProcessStarted(JobDataMap processData, Long jobId) {
        ProcessType type = getProcessType(processData);
        String processOwner = getProcessOwner(processData);
        String log = String.format("Started process %s of user %s with id: %d.", type.toString(), processOwner, jobId);
        AdminLogger.getLogger().info(log);
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        Long jobId = getJobId(context);
        System.err.println("job " + jobId + " was vetoed");
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        Long jobId = getJobId(context);
        ProcessState finalState = (ProcessState) context.getResult();
        new ProcesStateUpdater(jobId).updateProcessStatToFinished(finalState);
        JobDataMap processData = context.getMergedJobDataMap();
        logProcessFinished(processData, finalState, jobId);
    }

    private void logProcessFinished(JobDataMap processData, ProcessState finalState, Long jobId) {
        ProcessType type = getProcessType(processData);
        String processOwner = getProcessOwner(processData);
        String log = String.format("Ended process %s of user %s with id: %d, state: %s.", type.toString(), processOwner, jobId, finalState);
        AdminLogger.getLogger().info(log);
    }

    private Long getJobId(JobExecutionContext context) {
        JobKey key = context.getJobDetail().getKey();
        return Long.valueOf(key.getName());
    }

    private ProcessType getProcessType(JobDataMap processData) {
        return ProcessType.valueOf((String) processData.get(AbstractJob.PARAM_PROCESS_TYPE));
    }

    private String getProcessOwner(JobDataMap processData) {
        return (String) processData.get(AbstractJob.PARAM_OWNER_LOGIN);
    }

}
