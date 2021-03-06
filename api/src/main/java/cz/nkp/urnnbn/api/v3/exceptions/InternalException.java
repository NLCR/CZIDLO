/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api.v3.exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.ws.rs.core.Response.Status;

/**
 *
 * @author Martin Řehánek
 */
public class InternalException extends ApiV3Exception {

    public InternalException(String errorMessage) {
        super(Status.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", errorMessage);
    }

    public InternalException(Throwable e) {
        super(Status.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", buildMessage(e));
        e.printStackTrace();
    }

    private static String buildMessage(Throwable e) {
        StringWriter writer = new StringWriter();
        PrintWriter out = new PrintWriter(writer);
        if (e.getMessage() != null) {
            out.write(e.getMessage());
        }
        e.printStackTrace(out);
        out.flush();
        return writer.toString();
    }
}
