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
import cz.nkp.urnnbn.processmanager.scheduler.jobs.OaiAdapterJob;
import cz.nkp.urnnbn.processmanager.scheduler.jobs.ProcesStateUpdater;
import cz.nkp.urnnbn.processmanager.scheduler.jobs.UrnNbnCsvExportJob;
import org.quartz.JobDataMap;
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
        JobDataMap processData = context.getMergedJobDataMap();
        ProcessType type = getProcessType(processData);
        AdminLogger.getLogger().info("process " + type + " of user '" + getProcessOwner(processData)
                + "' with parameters " + getProcessParams(processData, type)
                + " will start now");
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
        JobDataMap processData = context.getMergedJobDataMap();
        ProcessType type = getProcessType(processData);
        AdminLogger.getLogger().info("process " + type + " of user '" + getProcessOwner(processData)
                + "' with parameters " + getProcessParams(processData, type)
                + " ended as " + finalState);
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

    private String getProcessParams(JobDataMap processData, ProcessType type) {
        switch (type) {
            case OAI_ADAPTER:
                return oaiAdapterParams(processData);
            case REGISTRARS_URN_NBN_CSV_EXPORT:
                return registrarsUrnNbnExportParams(processData);
            default:
                return "[]";
        }
    }

    private String oaiAdapterParams(JobDataMap processData) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        appendKeyValue(processData, builder, "registrarion_mode", OaiAdapterJob.PARAM_RESOLVER_REGISTRATION_MODE);
        builder.append(", ");
        appendKeyValue(processData, builder, "registrar_code", OaiAdapterJob.PARAM_RESOLVER_REGISTRAR_CODE);
        builder.append(", ");
        appendKeyValue(processData, builder, "oai_base_url", OaiAdapterJob.PARAM_OAI_BASE_URL);
        builder.append(", ");
        appendKeyValue(processData, builder, "oai_metadata_prefix", OaiAdapterJob.PARAM_OAI_METADATA_PREFIX);
        builder.append(", ");
        appendKeyValue(processData, builder, "oai_set", OaiAdapterJob.PARAM_OAI_SET);
        builder.append("]");
        return builder.toString();
    }

    private void appendKeyValue(JobDataMap data, StringBuilder builder, String attrName, String jobKey) {
        builder.append(attrName).append('=');
        builder.append('\"').append((String) data.get(jobKey)).append('\"');
    }

    private String registrarsUrnNbnExportParams(JobDataMap processData) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        appendKeyValue(processData, builder, "registrar_code", UrnNbnCsvExportJob.PARAM_REGISTRARS_CODE_KEY);
        builder.append("]");
        return builder.toString();
    }
}
