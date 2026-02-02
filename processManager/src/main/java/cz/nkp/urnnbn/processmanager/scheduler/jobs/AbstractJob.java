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

import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.impl.postgres.PostgresPooledConnector;
import cz.nkp.urnnbn.processmanager.core.ProcessType;
import cz.nkp.urnnbn.services.Services;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.joda.time.DateTime;
import org.quartz.InterruptableJob;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.UnableToInterruptJobException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

/**
 * @author Martin Řehánek
 */
public abstract class AbstractJob implements InterruptableJob {

    public static String PARAM_PROCESS_ID_KEY = "processId";
    public static String PARAM_PROCESS_TYPE = "processType";
    public static String PARAM_OWNER_LOGIN = "processOwner";
    public static String PARAM_CZIDLO_DB_HOST = "czidloDbHost";
    public static String PARAM_CZIDLO_DB_PORT = "czidloDbPort";
    public static String PARAM_CZIDLO_DB_DATABASE = "czidloDbDatabase";
    public static String PARAM_CZIDLO_DB_LOGIN = "czidloDbLogin";
    public static String PARAM_CZIDLO_DB_PASSWORD = "czidloDbPassword";
    protected Long processId;
    protected Logger logger;
    protected boolean interrupted = false;

    public void interrupt() throws UnableToInterruptJobException {
        this.interrupted = true;
    }

    abstract void close();

    protected void closeLogger() {
        if (logger == null) return;
        String appenderName = APPENDER_PREFIX + logger.getName();
        Appender ap = logger.getAppender(appenderName);
        if (ap != null) {
            logger.removeAppender(ap);
            try {
                ap.close();
            } catch (Exception ignored) {
            }
        }
    }

    protected void init(JobDataMap mergedJobDataMap, ProcessType processType) {
        this.processId = (Long) mergedJobDataMap.get(PARAM_PROCESS_ID_KEY);
        initLogger(processId, processType);
        ProcessFileUtils.initProcessDir(processId);
        initLogAppender();
    }

    private void initLogger(Long processId, ProcessType processType) {
        logger = org.apache.log4j.Logger.getLogger(processType.toString() + ":" + processId);
    }

    private static final String APPENDER_PREFIX = "ProcessFileAppender::";

    private void initLogAppender() {
        File logFile = ProcessFileUtils.buildLogFile(processId);

        // 1) základní sanity checks
        try {
            File dir = logFile.getParentFile();
            if (dir != null) dir.mkdirs();
            if (!logFile.exists()) logFile.createNewFile();
            if (!logFile.canWrite()) {
                System.err.println("Process log file is not writable: " + logFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("Failed to prepare process log file: " + logFile.getAbsolutePath() + " - " + e);
        }

        // 2) logger nezávislý na root konfiguraci
        logger.setAdditivity(false);
        logger.setLevel(org.apache.log4j.Level.INFO); // nebo DEBUG

        // 3) unikátní appender per process (ať se to neplete)
        String appenderName = APPENDER_PREFIX + logger.getName();

        // 4) pokud už existuje, zavři/odpoj (např. opakované spuštění jobu se stejným loggerem)
        org.apache.log4j.Appender old = logger.getAppender(appenderName);
        if (old != null) {
            logger.removeAppender(old);
            try {
                old.close();
            } catch (Exception ignored) {
            }
        }

        FileAppender fa = new FileAppender();
        fa.setName(appenderName);
        fa.setFile(logFile.getAbsolutePath());
        fa.setLayout(new PatternLayout("%d %-5p [%c{1}] %m%n"));
        fa.setThreshold(org.apache.log4j.Level.INFO); // match logger level
        fa.setAppend(true);

        // Pokud by došlo k chybě při otevření file streamu, chceme to vidět
        try {
            fa.activateOptions();
        } catch (Exception e) {
            System.err.println("Failed to activate FileAppender for " + logFile.getAbsolutePath() + ": " + e);
        }

        logger.addAppender(fa);

        // 5) jednorázová diagnostika (můžeš pak odstranit)
        logger.info("Process logger initialized. file=" + logFile.getAbsolutePath()
                + " effectiveLevel=" + logger.getEffectiveLevel()
                + " infoEnabled=" + logger.isInfoEnabled());
    }


    protected File createWriteableProcessFile(String filename) throws IOException {
        return ProcessFileUtils.createWriteableProcessFile(processId, filename);
    }


    // just for testing
    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    PrintWriter openCsvWriter(File file) throws FileNotFoundException {
        return new PrintWriter(file);
    }

    Services initServices() {
        Services.init(initDatabaseConnector(), null);
        return Services.instanceOf();
    }

    private DatabaseConnector initDatabaseConnector() {
        return new PostgresPooledConnector();
    }

    DateTime parseDatetimeOrNullFromContext(String key, JobExecutionContext context, DateFormat dateFormat) throws ParseException {
        if (context.getMergedJobDataMap().containsKey(key)) {
            String val = context.getMergedJobDataMap().getString(key);
            if (val != null) {
                return new DateTime(dateFormat.parse(val));
            } else {
                return null;
            }
        } else {
            throw new IllegalStateException("no data for key '" + key + "' found");
        }
    }

    List<String> parseStringListOrNullFromContext(String key, JobExecutionContext context) {
        if (context.getMergedJobDataMap().containsKey(key)) {
            String val = context.getMergedJobDataMap().getString(key);
            if (val != null) {
                return Arrays.asList(val.split(","));
            } else {
                return null;
            }
        } else {
            throw new IllegalStateException("no data for key '" + key + "' found");
        }
    }


    String listOfStringsToString(List<String> list) {
        if (list == null) {
            return null;
        } else {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                builder.append(list.get(i));
                if (i != list.size() - 1) {
                    builder.append(',');
                }
            }
            return builder.toString();
        }
    }

}
