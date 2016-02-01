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

//import java.util.logging.Level;
//import java.util.logging.Logger;
import java.io.File;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.quartz.InterruptableJob;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Martin Řehánek
 */
public class TestJob implements InterruptableJob {

    private static final int cycles = 3;
    private static final int sleepMillis = 1000;
    private String jobId = null;
    // private Logger logger = null;
    // Logger.getLogger(App.class.getName());
    private Logger logger;

    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap data = context.getMergedJobDataMap();
        jobId = data.getString("id");
        createAppender(new File("/home/martin/tmp/quartzTestLogs"), jobId);
        logger = LoggerFactory.getLogger("TestJob." + jobId);
        // logger = Logger.getLogger("TestJob." + jobId);
        out("executing");
        // err("executing");
        for (int i = 0; i < cycles; i++) {
            out("iteration " + i);
            // err("iteration " + i);
            // logger.log(Level.INFO, "iteration {0}", i);
            logger.info("iteration " + i);
            sleep(sleepMillis);
        }
        out("finished");
        // err("finished");
        context.setResult("OK");
    }

    private void out(String text) {
        System.out.println("TestJob(" + jobId + ").OUT: " + text);
    }

    private void err(String text) {
        System.err.println("TestJob(" + jobId + ").OUT: " + text);
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            // logger.log(Level.SEVERE, null, ex);
        }
    }

    private void createAppender(File file, String jobId) {
        FileAppender fa = new FileAppender();
        fa.setName("FileLogger");
        fa.setFile(file.getAbsolutePath() + File.separatorChar + jobId + ".log");
        fa.setLayout(new PatternLayout("%d %-5p [%c{1}] %m%n"));
        fa.setThreshold(Level.DEBUG);
        fa.setAppend(true);
        fa.activateOptions();

        // add appender to any Logger (here is root)
        org.apache.log4j.Logger.getRootLogger().addAppender(fa);
    }

    public void interrupt() throws UnableToInterruptJobException {
        // See the org.quartz.InterruptableJob interface, and the Scheduler.interrupt(String, String) method.
        // TODO: ulozit stav do databaze (KILLED)
        // dalsi info ruzne:
        // priorita - resi se jen pro ruzny cas, proto budu muset delat sam
        // misfire - trigger nemohl odpalit (napr malo vlaken) http://quartz-scheduler.org/documentation/quartz-2.1.x/tutorials/tutorial-lesson-04
        // listeneres - asi delat listeners na joby - pred nahozenim, po konci
        // http://quartz-scheduler.org/documentation/quartz-2.1.x/tutorials/tutorial-lesson-07
        // jak potom rozlisovat u jobu finished a failed? Ze by taky souborem?

        // po startu zjistim stavy procesu v databazi
        // ty, co maji stav RUNNING, zmenim na failed
        // ty, co maji stav scheduled pridam do fronty naplanovanych
    }

}
