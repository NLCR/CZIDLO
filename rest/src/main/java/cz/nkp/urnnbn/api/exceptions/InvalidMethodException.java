/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api.exceptions;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.Response.Status;

/**
 *
 * @author Martin Řehánek
 * @deprecated won't work because of Jersey lack of support
 */
public class InvalidMethodException extends ApiException {

    public static enum MethodAllowed {

        POST,
        GET,
        PUT,
        DELETE
    }

    public InvalidMethodException(MethodAllowed[] allowedMethods) {
        //unfortunatelly won't work because Status.fromCode(405) returns null;
        super(Status.fromStatusCode(405), "METHOD_NOT_PERMITTED", buildMessage(allowedMethods), buildHeaderAllowed(allowedMethods));
    }

    private static String buildMessage(MethodAllowed[] allowedMethods) {
        return "methods allowed: " + toString(allowedMethods);
    }

    private static Map<String, Object> buildHeaderAllowed(MethodAllowed[] allowedMethods) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("Allowed", toString(allowedMethods));
        return result;
    }

    private static String toString(MethodAllowed[] allowedMethods) {
        StringBuilder result = new StringBuilder(allowedMethods.length * 2 - 1);
        for (int i = 0; i < allowedMethods.length; i++) {
            result.append(allowedMethods[i]);
            if (i != allowedMethods.length) {
                result.append(", ");
            }
        }
        return result.toString();
    }
}
