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

import cz.nkp.urnnbn.processmanager.core.ProcessState;
import cz.nkp.urnnbn.processmanager.scheduler.jobs.ProcesStateUpdater;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.JobListener;

/**
 *
 * @author Martin Řehánek
 */
public class JobListenerImpl implements JobListener {

    public String getName() {
        return JobListenerImpl.class.getName();
    }

    public void jobToBeExecuted(JobExecutionContext context) {
        Long jobId = getJobId(context);
        System.err.println("job " + jobId + " is going to be executed");
        new ProcesStateUpdater(jobId).updateProcessStateToRunning();
    }

    public void jobExecutionVetoed(JobExecutionContext context) {
        Long jobId = getJobId(context);
        System.err.println("job " + jobId + " was vetoed");
    }

    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        Long jobId = getJobId(context);
        ProcessState finalState = (ProcessState) context.getResult();
        new ProcesStateUpdater(jobId).updateProcessStatToFinished(finalState);
        System.err.println("job " + jobId + " was executed with result " + finalState);

//        Object testResult = context.getResult();
//        System.err.println("job " + jobId + " was executed with result " + testResult);
    }

    private Long getJobId(JobExecutionContext context) {
        JobKey key = context.getJobDetail().getKey();
        return Long.valueOf(key.getName());
    }
}
