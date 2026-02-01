/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.indexer;

import cz.nkp.urnnbn.indexer.es.EsIndexer;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.logging.Logger;

/**
 * @author hanis
 */
public class ReportLogger {

    private static final Logger logger = Logger.getLogger(ReportLogger.class.getName());

    private PrintStream stream;

    public ReportLogger(OutputStream outputStream) {
        this.stream = outputStream == null ? null : new PrintStream(outputStream);
    }

    public void report(String message) {
        if (stream != null) {
            stream.println(message);
        }
    }

    public void report(String message, Throwable e) {
        if (stream != null) {
            stream.print(message + ": ");
            e.printStackTrace(stream);
        }
    }

    public void close() {
        logger.info("Closing ReportLogger");
        if (stream != null) {
            stream.close();
        }
    }

}
