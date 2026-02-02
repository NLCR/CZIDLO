package cz.nkp.urnnbn.core;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

public class AdminLogger {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(AdminLogger.class.getName());
    private static final Object LOCK = new Object();

    private static Logger adminLogger;
    private static File adminLogFile;
    private static Appender appender;

    public static void initializeLogger(String loggerName, File loggerFile) throws IOException {
        synchronized (LOCK) {
            String canonicalPath = canonicalPath(loggerFile);
            String appenderName = appenderName(loggerName, canonicalPath);

            // získáme logger instance (log4j singleton dle jména v rámci log4j contextu)
            Logger l = Logger.getLogger(loggerName);

            // pokud je už náš appender přítomen, máme hotovo (idempotentní init)
            Appender existing = l.getAppender(appenderName);
            if (existing != null) {
                adminLogger = l;
                adminLogFile = loggerFile;
                appender = existing;
                // pojistky, kdyby se konfigurace změnila
                adminLogger.setAdditivity(false);
                adminLogger.setLevel(Level.INFO);
                return;
            }

            logger.info("Initializing admin logger '" + loggerName + "' with log file: " + canonicalPath);
            logger.info("AdminLogger loaded from " + AdminLogger.class.getProtectionDomain().getCodeSource().getLocation());

            // zavři jen "naše" staré appendery (pokud existují), nešahej na cizí
            closeOurAppenders(l);

            // nastav logger tak, aby nebyl závislý na root loggeru
            l.setAdditivity(false);
            l.setLevel(Level.INFO);

            Layout layout = new PatternLayout("[%d{dd.MM.yyyy, HH:mm:ss}] %c: %m%n");
            FileAppender fa = new FileAppender(layout, canonicalPath, true);
            fa.setName(appenderName);
            fa.setThreshold(Level.INFO);

            l.addAppender(fa);

            adminLogger = l;
            adminLogFile = loggerFile;
            appender = fa;
        }
    }

    public static void shutdown() {
        synchronized (LOCK) {
            try {
                logger.info(() -> "Shutting down AdminLogger, logger=" + adminLogger + ", file=" + (adminLogFile == null ? "null" : adminLogFile.getAbsolutePath()));
                if (adminLogger != null) {
                    // zavři jen to, co AdminLogger vytvořil
                    if (appender != null) {
                        adminLogger.removeAppender(appender);
                        safeClose(appender);
                    } else {
                        // fallback: zavři všechny "naše" appendery podle prefixu jména
                        closeOurAppenders(adminLogger);
                    }
                }
            } catch (Exception ex) {
                logger.warning("Failed to shutdown AdminLogger: " + ex.getMessage());
            } finally {
                adminLogger = null;
                adminLogFile = null;
                appender = null;
            }
        }
    }

    public static Logger getLogger() {
        return adminLogger;
    }

    public static File getLogFile() {
        return adminLogFile;
    }

    private static void closeOurAppenders(Logger l) {
        Enumeration<?> e = l.getAllAppenders();
        while (e != null && e.hasMoreElements()) {
            Object x = e.nextElement();
            if (x instanceof Appender ap) {
                String n = ap.getName();
                if (n != null && n.startsWith("ADMIN::")) {
                    l.removeAppender(ap);
                    safeClose(ap);
                }
            }
        }
    }

    private static void safeClose(Appender ap) {
        try {
            ap.close();
        } catch (Exception ignored) {
        }
    }

    private static String canonicalPath(File f) throws IOException {
        // canonical je lepší pro detekci "stejného souboru" přes symlinky/relativní cesty
        return f.getCanonicalPath();
    }

    private static String appenderName(String loggerName, String canonicalPath) {
        // jméno appenderu musí být stabilní a unikátní pro (loggerName + soubor)
        // a zároveň nepoužívat OS-problémové znaky
        String safePath = canonicalPath.replace('\\', '/').replace(':', '_');
        return "ADMIN::" + loggerName + "::" + safePath;
    }
}
