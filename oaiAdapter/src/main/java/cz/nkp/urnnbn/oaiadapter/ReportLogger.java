/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 *
 * @author hanis
 */
public class ReportLogger {

    private PrintStream stream;

    public ReportLogger(OutputStream outputStream) {
        this.stream = new PrintStream(outputStream);
    }

    public void report(String message) {
        stream.println(message);
    }

    public void report(String message, Throwable e) {
        stream.print(message);
        e.printStackTrace(stream);
    }

    public void close() {
        stream.close();
    }

}
