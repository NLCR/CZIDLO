package cz.nkp.urnnbn.processmanager;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.quartz.SchedulerException;

import cz.nkp.urnnbn.core.UrnNbnRegistrationMode;
import cz.nkp.urnnbn.processmanager.conf.Configuration;
import cz.nkp.urnnbn.processmanager.control.AccessRightException;
import cz.nkp.urnnbn.processmanager.control.InvalidStateException;
import cz.nkp.urnnbn.processmanager.control.ProcessManager;
import cz.nkp.urnnbn.processmanager.control.ProcessManagerImpl;
import cz.nkp.urnnbn.processmanager.core.Process;
import cz.nkp.urnnbn.processmanager.core.ProcessState;
import cz.nkp.urnnbn.processmanager.core.ProcessType;
import cz.nkp.urnnbn.processmanager.persistence.UnknownRecordException;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) throws IOException, SchedulerException {
        // SessionFactory factory = Tests.initFactory();
        // Tests.createSomeObjects(factory);
        // Tests.createAndUpdateObject(factory);
        // Tests.createAndDeleteObject(factory);
        // test();
        Configuration.init(new File("processManager/src/main/resources/scheduler.properties"));
        scheduleOaiAdapter();
        // scheduleCvsExportProcesses(1,"tst02");
        // justStartProcessManager();
        // scheduleAndCancel();
        // scheduleWaitKill();
        // sleep(25000);
        // shutdownProcessManager(true);
    }

    private static Process scheduledTestProcess(String login) {
        Process process = new Process();
        process.setOwnerLogin(login);
        process.setType(ProcessType.TEST);
        process.setParams(new String[] {});
        process.setState(ProcessState.SCHEDULED);
        return process;
    }

    private static Process scheduledUrnNbnCsvExportProcess(String login) {
        Process process = new Process();
        process.setOwnerLogin(login);
        process.setType(ProcessType.REGISTRARS_URN_NBN_CSV_EXPORT);
        process.setParams(new String[] { "tst02" });
        process.setState(ProcessState.SCHEDULED);
        return process;
    }

    private static void sleep(int i) {
        try {
            System.err.println("SLEEPING " + i + " ms");
            Thread.sleep(i);
        } catch (InterruptedException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void setStartedIfNull(Process process) {
        // pokud se nepodarilo zastihnout proces v bezicim stavu, nastavi se started na finished, resp. mirne driv
        if (process.getStarted() == null) {
            process.setStarted(new Date());
        }
    }

    private static void scheduleCvsExportProcesses(int count, String registrarCode) {

        // manager.scheduleNewProcess("nkpAdmin", ProcessType.REGISTRARS_URN_NBN_CSV_EXPORT, new String[]{"tst01"});
        // ProcessDAO processDao = ProcessDAOImpl.instanceOf();
        // Process process = new Process();
        // process.setOwnerLogin("nkpAdmin");
        // process.setType(ProcessType.REGISTRARS_URN_NBN_CSV_EXPORT);
        // process.setParams(new String[]{"tst01"});
        // process.setState(ProcessState.SCHEDULED);
        // process.setScheduled(new Date());
        // processDao.saveProcess(process);

        ProcessManager manager = ProcessManagerImpl.instanceOf();
        for (int i = 0; i < count; i++) {
            manager.scheduleNewProcess("nkpAdmin", ProcessType.REGISTRARS_URN_NBN_CSV_EXPORT, new String[] { registrarCode });
        }
    }

    private static void scheduleOaiAdapter() throws SchedulerException {
        ProcessManager manager = ProcessManagerImpl.instanceOf();
        int procesess = 5;
        // for (int i = 0; i < procesess; i++) {
        // //manager.scheduleNewProcess("Martin", ProcessType.TEST, new String[]{});
        // manager.scheduleNewProcess("nkpAdmin", ProcessType.REGISTRARS_URN_NBN_CSV_EXPORT, new String[]{"tst01"});
        // manager.scheduleNewProcess("superAdmin", ProcessType.REGISTRARS_URN_NBN_CSV_EXPORT, new String[]{"tst02"});
        // }
        // manager.scheduleNewProcess("nkpAdmin", ProcessType.REGISTRARS_URN_NBN_CSV_EXPORT, new String[]{"tst01"});
        manager.scheduleNewProcess("superAdmin", ProcessType.OAI_ADAPTER, new String[] { "oaiAdapter-rehan-test", "dlhIob5z",
                UrnNbnRegistrationMode.BY_REGISTRAR.toString(), "duha", "http://duha.mzk.cz/oai", "oai_dc", null,
                "oaiAdapter/src/main/resources/cz/nkp/urnnbn/oaiadapter/stylesheets/dc_duha_import.xsl",
                "oaiAdapter/src/main/resources/cz/nkp/urnnbn/oaiadapter/stylesheets/dc_duha_digital_instance.xsl",
                "~/tmp/oaiAdapter/report.txt" });
        // int checks = 100;
        // int checks = 3;
        // for (int i = 0; i < checks; i++) {
        // manager.runScheduledProcessIfPossible();
        // sleep(10);
        // }

        // po chvili zavrit
        sleep(10000);
        manager.shutdown(true);
    }

    private static void justStartProcessManager() {
        ProcessManager manager = ProcessManagerImpl.instanceOf();
    }

    private static void shutdownProcessManager(boolean wait) {
        ProcessManager manager = ProcessManagerImpl.instanceOf();
        manager.shutdown(true);
    }

    private static void scheduleAndCancel() {
        ProcessManager manager = ProcessManagerImpl.instanceOf();
        Process process = manager.scheduleNewProcess("nkpAdmin", ProcessType.REGISTRARS_URN_NBN_CSV_EXPORT, new String[] { "tst01" });
        try {
            manager.cancelScheduledProcess("nkpAdmin", process.getId());
        } catch (UnknownRecordException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AccessRightException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidStateException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void scheduleWaitKill() {
        try {
            ProcessManager manager = ProcessManagerImpl.instanceOf();
            Process process = manager.scheduleNewProcess("nkpAdmin", ProcessType.REGISTRARS_URN_NBN_CSV_EXPORT, new String[] { "tst01" });
            sleep(7000);
            boolean killed = manager.killRunningProcess("nkpAdmin", process.getId());
            System.err.println("killed: " + killed);
            sleep(5000);
            manager.shutdown(true);

        } catch (UnknownRecordException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AccessRightException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidStateException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
