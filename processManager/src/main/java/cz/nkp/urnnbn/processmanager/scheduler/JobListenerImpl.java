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

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.JobListener;

import cz.nkp.urnnbn.core.AdminLogger;
import cz.nkp.urnnbn.processmanager.core.Process;
import cz.nkp.urnnbn.processmanager.core.ProcessState;
import cz.nkp.urnnbn.processmanager.core.ProcessType;
import cz.nkp.urnnbn.processmanager.scheduler.jobs.AbstractJob;
import cz.nkp.urnnbn.processmanager.scheduler.jobs.OaiAdapterJob;
import cz.nkp.urnnbn.processmanager.scheduler.jobs.ProcesStateUpdater;
import cz.nkp.urnnbn.processmanager.scheduler.jobs.UrnNbnCsvExportJob;

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
        getProcessParams(processData, getProcessType(processData));
        new ProcesStateUpdater(jobId).updateProcessStateToRunning();
        logProcessStarted(processData, jobId);
    }

    private void logProcessStarted(JobDataMap processData, Long jobId) {
        ProcessType type = getProcessType(processData);
        String processOwner = getProcessOwner(processData);
        String log = String.format("Started process %s of user %s with id: %d.", type.toString(), processOwner, jobId);
        AdminLogger.getLogger().info(log);
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

    private String getProcessParams(JobDataMap processData, ProcessType type) {
        switch (type) {
        case OAI_ADAPTER:
            return oaiAdapterParams(processData);
        case REGISTRARS_URN_NBN_CSV_EXPORT:
            return registrarsUrnNbnExportParams(processData);
        case DI_URL_AVAILABILITY_CHECK:
            return diUrlAvailabilityCheckparams(processData);
        default:
            return "[]";
        }
    }

    private String diUrlAvailabilityCheckparams(JobDataMap processData) {
        return "[]";
    }

    private String oaiAdapterParams(JobDataMap processData) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        appendKeyValue(processData, builder, "registration_mode", OaiAdapterJob.PARAM_CZIDLO_REGISTRATION_MODE);
        builder.append(", ");
        appendKeyValue(processData, builder, "registrar_code", OaiAdapterJob.PARAM_CZIDLO_REGISTRAR_CODE);
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
        appendKeyValue(processData, builder, "begin", UrnNbnCsvExportJob.PARAM_BEGIN);
        builder.append(", ");
        appendKeyValue(processData, builder, "end", UrnNbnCsvExportJob.PARAM_END);
        builder.append(", ");
        appendKeyValue(processData, builder, "registrars", UrnNbnCsvExportJob.PARAM_REGISTRARS_CODES);
        builder.append(", ");
        appendKeyValue(processData, builder, "intEntTypes", UrnNbnCsvExportJob.PARAM_ENT_TYPES);
        builder.append(", ");
        appendKeyValue(processData, builder, "missingCcnb", UrnNbnCsvExportJob.PARAM_MISSING_CCNB);
        builder.append(", ");
        appendKeyValue(processData, builder, "missingIssn", UrnNbnCsvExportJob.PARAM_MISSING_ISSN);
        builder.append(", ");
        appendKeyValue(processData, builder, "missingIsbn", UrnNbnCsvExportJob.PARAM_MISSING_ISBN);
        builder.append(", ");
        appendKeyValue(processData, builder, "returnActive", UrnNbnCsvExportJob.PARAM_RETURN_ACTIVE);
        builder.append(", ");
        appendKeyValue(processData, builder, "returnDeactivated", UrnNbnCsvExportJob.PARAM_RETURN_DEACTIVED);
        builder.append(", ");
        appendKeyValue(processData, builder, "withDigInstNum", UrnNbnCsvExportJob.PARAM_EXPORT_NUM_OF_DIG_INSTANCES);
        builder.append("]");
        return builder.toString();
    }
}
