package cz.nkp.urnnbn.core;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static java.nio.file.StandardOpenOption.*;

public class AdminLoggerSimple {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(AdminLoggerSimple.class.getName());

    private static final Object INIT_LOCK = new Object();

    private static volatile boolean initialized = false;
    private static volatile Path logPath;
    private static volatile String loggerName; // např. WEB-API

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss");

    private AdminLoggerSimple() {
        logger.info("AdminLoggerSimple has been created.");
    }

    public static void initializeLogger(String name, File file) {
        logger.info("AdminLoggerSimple is being initialized.");
        synchronized (INIT_LOCK) {
            try {
                Path p = file.toPath().toAbsolutePath().normalize();

                // if already initialized, be defensive
                if (initialized && logPath != null) {
                    Path current = logPath;
                    if (!current.equals(p)) {
                        logger.warning(
                                "AdminLoggerSimple is already initialized. " +
                                        "Ignoring re-initialization with different file. " +
                                        "current=" + current + ", requested=" + p +
                                        ", currentLoggerName=" + loggerName + ", requestedLoggerName=" + name
                        );
                        return;
                    }
                    // same file -> idempotent;
                    if (loggerName != null && !loggerName.equals(name)) {
                        logger.warning(
                                "AdminLoggerSimple already initialized for same file but different loggerName. " +
                                        "Keeping existing. existing=" + loggerName + ", requested=" + name
                        );
                    }
                    return;
                }

                // make sure parent directories exist
                Path parent = p.getParent();
                if (parent != null) {
                    Files.createDirectories(parent);
                }

                // make sure log file exists
                if (!Files.exists(p)) {
                    Files.createFile(p);
                }

                // check writability
                if (!Files.isWritable(p)) {
                    throw new IllegalStateException("Admin log file is not writable: " + p);
                }

                loggerName = name;
                logPath = p;
                initialized = true;

            } catch (Exception e) {
                throw new RuntimeException("Failed to initialize AdminLogger", e);
            }
        }
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static void shutdown() {
        logger.info("AdminLoggerSimple has been shutdown.");
        //nothing
    }

    public static void info(String msg) {
        write("INFO", msg, null);
    }

    public static void warn(String msg) {
        write("WARN", msg, null);
    }

    public static void error(String msg, Throwable t) {
        write("ERROR", msg, t);
    }

    private static void write(String level, String msg, Throwable t) {
        if (!initialized || logPath == null || loggerName == null) {
            // fallback to stderr
            System.err.println("AdminLogger not initialized: " + level + " " + msg);
            if (t != null) t.printStackTrace(System.err);
            return;
        }

        String ts = ZonedDateTime.now().format(TS);

        StringBuilder sb = new StringBuilder(256);
        sb.append('[').append(ts).append("] ")
                .append(loggerName).append(": ")
                .append(msg);

        if (t != null) {
            sb.append(System.lineSeparator());
            sb.append(stacktrace(t));
        }
        sb.append(System.lineSeparator());

        byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);

        // open-write-close (always to current inode)
        try (FileChannel ch = FileChannel.open(logPath, CREATE, WRITE, APPEND);
             FileLock lock = ch.lock()) {
            int written = ch.write(ByteBuffer.wrap(bytes));
            ch.force(false);
        } catch (Exception e) {
            // poslední instance: stderr
            System.err.println("AdminLogger write failed: " + e);
        }
    }

    private static String stacktrace(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

}
