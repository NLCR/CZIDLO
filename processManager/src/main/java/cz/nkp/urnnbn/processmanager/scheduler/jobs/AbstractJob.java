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

import cz.nkp.urnnbn.processmanager.core.ProcessType;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.quartz.InterruptableJob;
import org.quartz.JobDataMap;
import org.quartz.UnableToInterruptJobException;

/**
 *
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

    protected void init(JobDataMap mergedJobDataMap, ProcessType processType) {
        this.processId = (Long) mergedJobDataMap.get(PARAM_PROCESS_ID_KEY);
        initLogger(processId, processType);
        ProcessFileUtils.initProcessDir(processId);
        initLogAppender();
    }

    private void initLogger(Long processId, ProcessType processType) {
        logger = org.apache.log4j.Logger.getLogger(processType.toString() + ":" + processId);
    }

    private void initLogAppender() {
        FileAppender fa = new FileAppender();
        fa.setName("ProcessFileAppender");
        fa.setFile(ProcessFileUtils.buildLogFile(processId).getAbsolutePath());
        fa.setLayout(new PatternLayout("%d %-5p [%c{1}] %m%n"));
        fa.setThreshold(org.apache.log4j.Level.DEBUG);
        // fa.setThreshold(org.apache.log4j.Level.INFO);
        fa.setAppend(true);
        fa.activateOptions();
        // add appender to any Logger (here is root)
        // org.apache.log4j.Logger.getRootLogger().addAppender(fa);
        logger.addAppender(fa);
        // logger.info("apender added");
    }

    protected File createWriteableProcessFile(String filename) throws IOException {
        return ProcessFileUtils.createWriteableProcessFile(processId, filename);
    }

    protected OutputStream fileToOutputStream(File file) throws FileNotFoundException {
        return new FileOutputStream(file);
    }

    // just for testing
    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
