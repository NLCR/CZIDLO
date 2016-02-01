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

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.impl.matchers.GroupMatcher.jobGroupEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import cz.nkp.urnnbn.core.AdminLogger;
import cz.nkp.urnnbn.processmanager.conf.Configuration;
import cz.nkp.urnnbn.processmanager.core.Process;
import cz.nkp.urnnbn.processmanager.core.ProcessState;
import cz.nkp.urnnbn.processmanager.core.ProcessType;
import cz.nkp.urnnbn.processmanager.persistence.AuthorizingProcessDAO;
import cz.nkp.urnnbn.processmanager.persistence.AuthorizingProcessDAOImpl;
import cz.nkp.urnnbn.processmanager.persistence.UnknownRecordException;
import cz.nkp.urnnbn.processmanager.scheduler.JobListenerImpl;
import cz.nkp.urnnbn.processmanager.scheduler.jobs.AbstractJob;
import cz.nkp.urnnbn.processmanager.scheduler.jobs.OaiAdapterJob;
import cz.nkp.urnnbn.processmanager.scheduler.jobs.ProcesStateUpdater;
import cz.nkp.urnnbn.processmanager.scheduler.jobs.ProcessFileUtils;
import cz.nkp.urnnbn.processmanager.scheduler.jobs.UrnNbnCsvExportJob;

/**
 * 
 * @author Martin Řehánek
 */
public class ProcessManagerImpl implements ProcessManager {

    private static final Logger logger = Logger.getLogger(ProcessManagerImpl.class.getName());
    private static final String PROCESS_GROUP_JOBS = "process";
    private static final String PROCESS_GROUP_SYSTEM = "system";
    private static volatile ProcessManagerImpl instance;
    private final int maxAdminJobsRunning;
    private final int maxUserJobsRunning;
    private static final String ADMIN_PROCESS_QUEUE_NAME = "admin-job-queue";
    private static final String USER_PROCESS_QUEUE_NAME = "user-job-queue";
    private final Queue<Process> adminProcessQueue = new LinkedBlockingQueue<Process>();
    private final Queue<Process> userProcessQueue = new LinkedBlockingQueue<Process>();
    private final AuthorizingProcessDAO processDao = AuthorizingProcessDAOImpl.instanceOf();
    private Scheduler scheduler;

    public static synchronized ProcessManagerImpl instanceOf() {
        try {
            if (instance == null) {
                instance = new ProcessManagerImpl();
            }
            return instance;
        } catch (SchedulerException ex) {
            throw new RuntimeException(ex);
        }
    }

    private ProcessManagerImpl() throws SchedulerException {
        logger.info("initializing Process manager");
        this.maxAdminJobsRunning = Configuration.getMaxRunningAdminProcesses();
        logger.log(Level.INFO, "max number of adming jobs running: {0}", this.maxAdminJobsRunning);
        this.maxUserJobsRunning = Configuration.getMaxRunningUserProcesses();
        logger.log(Level.INFO, "max number of user jobs running: {0}", this.maxUserJobsRunning);
        initScheduler();
        runJobChecker();
        // TODO
        // delat _nejak_ pri inicializaci. No jenze pokud se inicializuje vic vlaken, tak je tam
        // stejne problem
        // mozna tohle proste ignorovat alespon prozatim a mazat procesy, ktere evidentne nejedou
        // zpetne rovnou v
        // databazi
        setRunningProcessesFromDatabaseStateToFailed();
        // napodobne
        enqueueScheduledProcessesFromDatabase();
    }

    private void initScheduler() throws SchedulerException {
        if (scheduler == null) {
            logger.log(Level.INFO, "initializing scheduler (by thread {0})", Thread.currentThread().getName());
            SchedulerFactory sf = new StdSchedulerFactory();
            // System.err.println("schedule factory intitialized");
            scheduler = sf.getScheduler();
            scheduler.start();
            // System.err.println("Scheduler started: " + result.isStarted());
            scheduler.getListenerManager().addJobListener(new JobListenerImpl(), jobGroupEquals(PROCESS_GROUP_JOBS));
        } else {
            logger.info("scheduler already initialized");
        }
    }

    private void runJobChecker() throws SchedulerException {
        JobKey key = new JobKey(JobCheckerJob.JOB_NAME, PROCESS_GROUP_SYSTEM);
        if (!scheduler.checkExists(key)) {
            logger.log(Level.INFO, "scheduling {0}", JobCheckerJob.class.getSimpleName());
            JobDetail job = newJob(JobCheckerJob.class).withIdentity(key).build();

            Random rand = new Random();
            Date soon = new Date(new Date().getTime() + 1000 + rand.nextInt(2000));
            Trigger trigger = newTrigger().startAt(soon)
            // .startNow()
                    .withSchedule(simpleSchedule().withIntervalInMilliseconds(500).repeatForever()).withIdentity("trigger-jobChecker", "group1")
                    // .startNow()
                    .build();
            scheduler.scheduleJob(job, trigger);
        } else {
            logger.log(Level.INFO, "{0} already scheduled", JobCheckerJob.class.getSimpleName());
        }
    }

    private void enqueueScheduledProcessesFromDatabase() {
        List<Process> scheduledFromDatabase = processDao.getProcessesByState(ProcessState.SCHEDULED);
        for (Process process : scheduledFromDatabase) {
            enqueueScheduledProcess(process);
        }
    }

    private void setRunningProcessesFromDatabaseStateToFailed() {
        List<Process> runningFromDatabase = processDao.getProcessesByState(ProcessState.RUNNING);
        for (Process process : runningFromDatabase) {
            new ProcesStateUpdater(process.getId()).updateProcessStateToFailed();
        }
    }

    public synchronized Process scheduleNewProcess(String userLogin, ProcessType type, String[] processParams) {
        Process process = processDao.saveProcess(Process.buildScheduledProcess(userLogin, type, processParams));
        enqueueScheduledProcess(process);
        logProcessScheduled(process);
        return process;
    }

    private void logProcessScheduled(Process process) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("Scheduled process %s of user %s with id: %d", process.getType().toString(), process.getOwnerLogin(),
                process.getId()));
        builder.append(String.format(", parameters: %s", paramsToString(process.getType(), process.getParams())));
        builder.append(".");
        AdminLogger.getLogger().info(builder);
    }

    private String paramsToString(ProcessType processType, String[] params) {
        // use generalParamsString to debug
        switch (processType) {
        case REGISTRARS_URN_NBN_CSV_EXPORT:
            return urnNbnExportProcessParamsToString(params);
        case OAI_ADAPTER:
            return oaiAdapterProcessParamsToString(params);
        default:
            return null;
        }
    }

    private String generalParamsString(String[] params) {
        StringBuilder builder = new StringBuilder();
        builder.append(", general:[");
        for (int i = 0; i < params.length; i++) {
            builder.append(String.format("%d: %s", i, params[i]));
            if (i != params.length - 1) {
                builder.append("; ");
            }
        }
        builder.append("]");
        return builder.toString();
    }

    private String oaiAdapterProcessParamsToString(String[] params) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append("registrar_code: " + params[3]);
        builder.append(", registration_mode: " + params[2]);
        builder.append("; oai_metadata_prefix: " + params[5]);
        String oaiSet = params[6];
        if (oaiSet != null) {
            builder.append("; oai_set: " + params[6]);
        }
        builder.append("; oai_base_url: " + params[4]);
        builder.append("]");
        return builder.toString();
    }

    private String urnNbnExportProcessParamsToString(String[] params) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append("registrars: " + params[2]);
        builder.append("; intelectual_entity_types: " + params[3]);

        Boolean missingCcnb = Boolean.valueOf(params[4]);
        Boolean missingIssn = Boolean.valueOf(params[5]);
        Boolean missingIsbn = Boolean.valueOf(params[6]);
        if (missingCcnb || missingIsbn || missingIssn) {
            builder.append("; filter_only_missing_ids: " + toMissingIdsString(missingCcnb, missingIsbn, missingIssn));
        }
        boolean returnActive = Boolean.valueOf(params[7]);
        boolean returnDeactivated = Boolean.valueOf(params[8]);
        if (!(returnActive && returnDeactivated)) {
            if (returnActive) {
                builder.append("; filter_by_urn_state: active_only");
            } else if (returnDeactivated) {
                builder.append("; filter_by_urn_state: deactivated_only");
            }
        }
        builder.append("; include_number_of_di: " + params[9]);
        builder.append("; begin: " + params[0]);
        builder.append("; end: " + params[1]);
        builder.append("]");
        return builder.toString();
    }

    private String toMissingIdsString(Boolean missingCcnb, Boolean missingIsbn, Boolean missingIssn) {
        List<String> missing = new ArrayList<String>();
        if (missingCcnb) {
            missing.add("cCNB");
        }
        if (missingIsbn) {
            missing.add("ISBN");
        }
        if (missingIssn) {
            missing.add("ISSN");
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < missing.size(); i++) {
            builder.append(missing.get(i));
            if (i != missing.size() - 1) {
                builder.append("+");
            }
        }
        return builder.toString();
    }

    private void enqueueScheduledProcess(Process process) {
        if (AuthentizationUtils.isAdmin(process.getOwnerLogin())) {
            enqueueScheduledProcessSynchronized(adminProcessQueue, process, ADMIN_PROCESS_QUEUE_NAME);
        } else {
            enqueueScheduledProcessSynchronized(userProcessQueue, process, USER_PROCESS_QUEUE_NAME);
        }
    }

    private void enqueueScheduledProcessSynchronized(Queue<Process> queue, Process process, String queueName) {
        synchronized (queue) {
            queue.add(process);
        }
        logger.log(Level.INFO, "adding process (id={0}, type={1}, ownerLogin={2}) to queue {3}", new Object[] { process.getId(), process.getType(),
                process.getOwnerLogin(), queueName });
    }

    void runScheduledProcessIfPossible() {
        runScheduledProcessIfPossible(userProcessQueue, maxUserJobsRunning, numberOfRunningUserProcesses(), USER_PROCESS_QUEUE_NAME);
        runScheduledProcessIfPossible(adminProcessQueue, maxAdminJobsRunning, numberOfRunningAdminProcesses(), ADMIN_PROCESS_QUEUE_NAME);
    }

    private void runScheduledProcessIfPossible(Queue<Process> queue, int maxSize, int actualSize, String queueName) {
        Process process = dequeueProcessOrNullSynchronized(queue, maxSize, actualSize, queueName);
        if (process != null) {
            runProcess(process);
        }
    }

    private Process dequeueProcessOrNullSynchronized(Queue<Process> queue, int maxSize, int actualSize, String queueName) {
        synchronized (queue) {
            if (queue.peek() != null) {
                if (actualSize < maxSize) {
                    return queue.poll();
                } else {
                    logger.log(Level.FINE, "{0}: There is process to be run but too many running already", queueName);
                    return null;
                }
            } else {
                logger.log(Level.FINE, "{0}: There is no scheduled process to be run", queueName);
                return null;
            }
        }
    }

    private int numberOfRunningAdminProcesses() {
        List<Process> runningProcesses = processDao.getProcessesByState(ProcessState.RUNNING);
        List<Process> runningAdminProcesses = filterOnlyRunByAdmins(runningProcesses);
        return runningAdminProcesses.size();
    }

    private List<Process> filterOnlyRunByAdmins(List<Process> runningProcesses) {
        List<Process> result = new ArrayList<Process>();
        for (Process process : runningProcesses) {
            if (AuthentizationUtils.isAdmin(process.getOwnerLogin())) {
                result.add(process);
            }
        }
        return result;
    }

    private int numberOfRunningUserProcesses() {
        List<Process> runningProcesses = processDao.getProcessesByState(ProcessState.RUNNING);
        List<Process> runningUserProcesses = filterOnlyRunByUsers(runningProcesses);
        return runningUserProcesses.size();
    }

    private List<Process> filterOnlyRunByUsers(List<Process> runningProcesses) {
        List<Process> result = new ArrayList<Process>();
        for (Process process : runningProcesses) {
            if (!AuthentizationUtils.isAdmin(process.getOwnerLogin())) {
                result.add(process);
            }
        }
        return result;
    }

    private void runProcess(Process process) {
        try {
            logger.log(Level.INFO, "running process {0} ({1})", new Object[] { process.getId(), process.getType().toString() });
            JobDetail job = buildJobDetail(process);
            // System.err.println("jobdetail ready");
            Trigger trigger = trigger(process.getId());
            // System.err.println("trigger ready");
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
            Logger.getLogger(ProcessManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private JobDetail buildJobDetail(Process process) {
        String id = process.getId().toString();
        System.err.println(parematersToString(process.getParams()));
        switch (process.getType()) {
        case REGISTRARS_URN_NBN_CSV_EXPORT:
            return newJob(UrnNbnCsvExportJob.class)
                    // .withIdentity(process.getId().toString(), (asAdmin ? GROUP_ADMIN :
                    // GROUP_USER) +
                    // process.getId().toString())
                    // .withIdentity(id, group)
                    .withIdentity(new JobKey(id, PROCESS_GROUP_JOBS)).usingJobData(AbstractJob.PARAM_PROCESS_ID_KEY, process.getId())
                    .usingJobData(AbstractJob.PARAM_PROCESS_TYPE, process.getType().toString())
                    .usingJobData(AbstractJob.PARAM_OWNER_LOGIN, process.getOwnerLogin())

                    .usingJobData(UrnNbnCsvExportJob.PARAM_BEGIN, process.getParams()[0])
                    .usingJobData(UrnNbnCsvExportJob.PARAM_END, process.getParams()[1])
                    .usingJobData(UrnNbnCsvExportJob.PARAM_REGISTRARS_CODES, process.getParams()[2])
                    .usingJobData(UrnNbnCsvExportJob.PARAM_ENT_TYPES, process.getParams()[3])
                    .usingJobData(UrnNbnCsvExportJob.PARAM_MISSING_CCNB, process.getParams()[4])
                    .usingJobData(UrnNbnCsvExportJob.PARAM_MISSING_ISSN, process.getParams()[5])
                    .usingJobData(UrnNbnCsvExportJob.PARAM_MISSING_ISBN, process.getParams()[6])
                    .usingJobData(UrnNbnCsvExportJob.PARAM_RETURN_ACTIVE, process.getParams()[7])
                    .usingJobData(UrnNbnCsvExportJob.PARAM_RETURN_DEACTIVED, process.getParams()[8])
                    .usingJobData(UrnNbnCsvExportJob.PARAM_EXPORT_NUM_OF_DIG_INSTANCES, process.getParams()[9])
                    .usingJobData(UrnNbnCsvExportJob.PARAM_COUNTRY_CODE, process.getParams()[10]).build();
        case OAI_ADAPTER:
            String apiBaseUrl = Configuration.getCzidloApiBaseUrl();
            String apiLogin = process.getParams()[0];
            String apiPassword = process.getParams()[1];
            String registrationMode = process.getParams()[2];
            String registrarCode = process.getParams()[3];
            String oaiBaseUrl = process.getParams()[4];
            String metadataPrefix = process.getParams()[5];
            String oaiSet = process.getParams()[6];
            String ddXslFile = process.getParams()[7];
            String diXslFile = process.getParams()[8];
            String ddRegistrationXsdUrl = Configuration.getDigDocRegistrationXsdUrl();
            String diImportXsdUrl = Configuration.getDigInstImportXsdUrl();

            return newJob(OaiAdapterJob.class).withIdentity(new JobKey(id, PROCESS_GROUP_JOBS))
                    .usingJobData(AbstractJob.PARAM_PROCESS_ID_KEY, process.getId())
                    .usingJobData(AbstractJob.PARAM_PROCESS_TYPE, process.getType().toString())
                    .usingJobData(AbstractJob.PARAM_OWNER_LOGIN, process.getOwnerLogin())
                    .usingJobData(OaiAdapterJob.PARAM_CZIDLO_API_BASE_URL, apiBaseUrl).usingJobData(OaiAdapterJob.PARAM_CZIDLO_API_LOGIN, apiLogin)
                    .usingJobData(OaiAdapterJob.PARAM_CZIDLO_API_PASSWORD, apiPassword)
                    .usingJobData(OaiAdapterJob.PARAM_CZIDLO_REGISTRATION_MODE, registrationMode)
                    .usingJobData(OaiAdapterJob.PARAM_CZIDLO_REGISTRAR_CODE, registrarCode)
                    .usingJobData(OaiAdapterJob.PARAM_OAI_BASE_URL, oaiBaseUrl).usingJobData(OaiAdapterJob.PARAM_OAI_METADATA_PREFIX, metadataPrefix)
                    .usingJobData(OaiAdapterJob.PARAM_OAI_SET, oaiSet).usingJobData(OaiAdapterJob.PARAM_DD_XSL_FILE, ddXslFile)
                    .usingJobData(OaiAdapterJob.PARAM_DI_XSL_FILE, diXslFile).usingJobData(OaiAdapterJob.PARAM_DD_XSD_URL, ddRegistrationXsdUrl)
                    .usingJobData(OaiAdapterJob.PARAM_DI_XSD_URL, diImportXsdUrl).build();
        case TEST:
        default:
            return null;
        }
    }

    private String parematersToString(String[] params) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < params.length; i++) {
            builder.append("\"").append(params[i]).append("\"");
            if (i != params.length - 1) {
                builder.append(";");
            }
        }
        return builder.toString();
    }

    private Trigger trigger(Long persistentId) {
        Random rand = new Random();
        Date soon = new Date(new Date().getTime() + 1000 + rand.nextInt(2000));
        return newTrigger().startAt(soon).withIdentity("trigger" + persistentId.toString(), "jobTriggers")
        // .startNow()
                .build();
    }

    public Process getProcess(String login, Long processId) throws UnknownRecordException, AccessRightException {
        return processDao.getProcess(login, processId);
    }

    public Process getProcess(Long processId) throws UnknownRecordException {
        return processDao.getProcess(processId);
    }

    public List<Process> getProcesses() {
        return processDao.getProcesses();
    }

    public List<Process> getProcessesByState(ProcessState state) {
        return processDao.getProcessesByState(state);
    }

    public List<Process> getProcessesScheduledAfter(Date date) {
        return processDao.getProcessesScheduledAfter(date);
    }

    public List<Process> getProcessesByOwner(String ownerLogin) {
        return processDao.getProcessesOfUser(ownerLogin);
    }

    public List<Process> getProcessesByOwnerScheduledAfter(String ownerLogin, Date date) {
        return processDao.getProcessesOfUserScheduledAfter(ownerLogin, date);
    }

    public synchronized boolean killRunningProcess(String login, Long processId) throws UnknownRecordException, AccessRightException,
            InvalidStateException {
        Process process = getProcess(login, processId);
        if (process.getState() != ProcessState.RUNNING) {
            throw new InvalidStateException(processId, process.getState());
        }

        try {
            JobKey jobKey = new JobKey(processId.toString(), PROCESS_GROUP_JOBS);
            if (scheduler.checkExists(jobKey)) {
                // System.err.println("OK, running");
                if (!AuthentizationUtils.isAdminOrOwner(login, process)) {
                    throw new AccessRightException(login, processId);
                }
                scheduler.interrupt(jobKey);
                return true;
            } else {
                throw new UnknownRecordException(Process.class.getName() + " with id " + processId);
                // System.err.println("NOT RUNNING");
            }
        } catch (SchedulerException ex) {
            logger.log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public synchronized boolean cancelScheduledProcess(String login, Long processId) throws UnknownRecordException, AccessRightException,
            InvalidStateException {
        Process process = processDao.getProcess(processId);
        // remove from queue
        boolean removedFromQueue = false;
        if (AuthentizationUtils.isAdmin(process.getOwnerLogin())) {
            removeFromQueueSynchronized(adminProcessQueue, process, ADMIN_PROCESS_QUEUE_NAME);
        } else {
            removeFromQueueSynchronized(userProcessQueue, process, USER_PROCESS_QUEUE_NAME);
        }
        // change process state in db
        new ProcesStateUpdater(processId).upadateProcessStateToCanceled();
        logProcessCanceled(process);
        return removedFromQueue;
    }

    private void logProcessCanceled(Process process) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("Canceled process %s of user %s with id: %d", process.getType().toString(), process.getOwnerLogin(),
                process.getId()));
        builder.append(".");
        AdminLogger.getLogger().info(builder);
    }

    private boolean removeFromQueueSynchronized(Queue<Process> queue, Process processToBeRemoved, String queueName) {
        boolean removed = false;
        synchronized (queue) {
            removed = queue.remove(processToBeRemoved);
        }
        if (removed) {
            logger.log(Level.INFO, "process {0} removed from {1} queue", new Object[] { processToBeRemoved, queueName });
        }
        return removed;
    }

    public void shutdown(boolean waitForJobsToFinish) {
        try {
            if (!waitForJobsToFinish) {
                killAllRunningJobs();
            }
            scheduler.shutdown(true);
        } catch (SchedulerException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void killAllRunningJobs() {
        try {
            Set<JobKey> jobKeys = scheduler.getJobKeys(jobGroupEquals(PROCESS_GROUP_JOBS));
            for (JobKey key : jobKeys) {
                scheduler.interrupt(key);
            }
        } catch (SchedulerException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public void deleteProcess(String userLogin, Long processId) throws UnknownRecordException, AccessRightException, InvalidStateException {
        Process process = getProcess(userLogin, processId);
        if (process.getState() == ProcessState.RUNNING || process.getState() == ProcessState.SCHEDULED) {
            throw new InvalidStateException(processId, process.getState());
        } else {
            processDao.deleteProcess(process);
            File failedToDelete = ProcessFileUtils.deleteProcessDir(processId);
            if (failedToDelete != null) {
                logger.log(Level.WARNING, "Cannot delete file {0}", failedToDelete.getAbsolutePath());
            } else {
                logger.log(Level.INFO, "Deleted data of process {0}", processId);
            }
            logProcessDeleted(process, processId);
        }
    }

    private void logProcessDeleted(Process process, Long processId) {
        String log = String.format("Deleted process %s of user %s with id: %d.", process.getType(), process.getOwnerLogin(), processId);
        AdminLogger.getLogger().info(log);
    }
}
