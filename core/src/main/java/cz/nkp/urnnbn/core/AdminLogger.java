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
package cz.nkp.urnnbn.core;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 *
 * @author Martin Řehánek
 */
public class AdminLogger {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(AdminLogger.class.getName());

    private static Logger adminLogger;
    private static File adminLogFile;

    public static void initializeLogger(String loggerName, File loggerFile) throws IOException {
        logger.info("Initializing admin logger '" + loggerName + "' with log file: " + loggerFile.getAbsolutePath());
        adminLogFile = loggerFile;
        AdminLogger.adminLogger = Logger.getLogger(loggerName);
        // Layout layout = new PatternLayout("%-5p [%d{dd. MM. yyyy HH:mm:ss}] %c: %m%n");
        Layout layout = new PatternLayout("[%d{dd.MM.yyyy, HH:mm:ss}] %c: %m%n");
        Appender appender = new FileAppender(layout, loggerFile.getAbsolutePath(), true);
        adminLogger.removeAllAppenders();
        adminLogger.addAppender(appender);
    }

    public static Logger getLogger() {
        return adminLogger;
    }

    public static File getLogFile() {
        return adminLogFile;
    }
}
