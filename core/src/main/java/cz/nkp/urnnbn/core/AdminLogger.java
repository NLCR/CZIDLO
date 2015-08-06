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

	private static Logger logger;
	private static File logFile;

	public static void initializeLogger(String loggerName, String loggerFileName) throws IOException {
		logFile = new File(loggerFileName);
		AdminLogger.logger = Logger.getLogger(loggerName);
		// Layout layout = new PatternLayout("%-5p [%d{dd. MM. yyyy HH:mm:ss}] %c: %m%n");
		Layout layout = new PatternLayout("[%d{dd.MM.yyyy, HH:mm:ss}] %c: %m%n");
		Appender appender = new FileAppender(layout, loggerFileName, true);
		logger.removeAllAppenders();
		logger.addAppender(appender);
	}

	public static Logger getLogger() {
		return logger;
	}

	public static File getLogFile() {
		return logFile;
	}
}
