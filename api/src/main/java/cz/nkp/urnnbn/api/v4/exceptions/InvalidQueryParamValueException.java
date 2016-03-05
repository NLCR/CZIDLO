/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api.v4.exceptions;

import javax.ws.rs.core.Response.Status;

/**
 *
 * @author Martin Řehánek
 */
public class InvalidQueryParamValueException extends ApiV4Exception {

    public InvalidQueryParamValueException(String paramName, String paramValue, String message) {
        super(Status.BAD_REQUEST, "INVALID_QUERY_PARAM_VALUE", "Invalid value '" + paramValue + "' of query parameter '" + paramName + "': "
                + message);
    }
}
