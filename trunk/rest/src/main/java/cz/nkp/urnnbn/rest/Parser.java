/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.rest;

import cz.nkp.urnnbn.core.DigDocIdType;
import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.rest.exceptions.InvalidDataException;
import cz.nkp.urnnbn.rest.exceptions.InvalidDigDocIdType;
import cz.nkp.urnnbn.rest.exceptions.InvalidDigInstanceIdException;
import cz.nkp.urnnbn.rest.exceptions.InvalidQueryParamValueException;
import cz.nkp.urnnbn.rest.exceptions.InvalidRegistrarCodeException;
import cz.nkp.urnnbn.rest.exceptions.InvalidUrnException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Řehánek
 */
public class Parser {

    private static final Logger logger = Logger.getLogger(Parser.class.getName());

    static UrnNbn parseUrn(String urnPar) {
        try {
            return UrnNbn.valueOf(urnPar.toLowerCase());
        } catch (RuntimeException e) {
            logger.log(Level.INFO, e.getMessage());
            throw new InvalidUrnException(urnPar, "incorrect syntax: " + e.getMessage());
        }
    }

    static DigDocIdType parseDigRepIdType(String idTypeStr) {
        try {
            return DigDocIdType.valueOf(idTypeStr);
        } catch (RuntimeException e) {
            throw new InvalidDigDocIdType(idTypeStr, e.getMessage());
        }
    }

    static long parseDigInstId(String digInstIdStr) {
        try {
            return Long.valueOf(digInstIdStr);
        } catch (RuntimeException e) {
            logger.log(Level.INFO, e.getMessage());
            throw new InvalidDigInstanceIdException(digInstIdStr, e.getMessage());
        }
    }

    static RegistrarCode parseSigla(String siglaStr) {
        try {
            return RegistrarCode.valueOf(siglaStr);
        } catch (RuntimeException e) {
            throw new InvalidRegistrarCodeException(siglaStr, e.getMessage());
        }
    }

    static int parseIntQueryParam(String value, String paramName, int minValue, int maxValue) {
        try {
            Integer result = Integer.valueOf(value);
            if (result < minValue) {
                throw new RuntimeException("parameter " + paramName + " must have at least value " + minValue);
            }
            if (result > maxValue) {
                throw new RuntimeException("parameter " + paramName + " must have at most value " + maxValue);
            }
            return result;
        } catch (RuntimeException e) {
            logger.log(Level.INFO, e.getMessage());
            throw new InvalidQueryParamValueException(paramName, value, e.getMessage());
        }
    }

    static ResponseFormat parseResponseFormat(String value, String paramName) {
        try {
            return ResponseFormat.valueOf(value.toUpperCase());
        } catch (RuntimeException e) {
            throw new InvalidQueryParamValueException(paramName, value, e.getMessage());
        }
    }

    static Action parseAction(String actionStr, String paramName) {
        try {
            return Action.valueOf(actionStr.toUpperCase());
        } catch (RuntimeException e) {
            throw new InvalidQueryParamValueException(paramName, actionStr, e.getMessage());
        }
    }

    static boolean parseBooleanQueryParam(String stringValue, String paramName) {
        Boolean trueByJre = Boolean.valueOf(stringValue);
        if (trueByJre) {
            return true;
        } else if ("false".equals(stringValue.toLowerCase())) {
            return false;
        } else {
            throw new InvalidQueryParamValueException(paramName, stringValue, "not boolean value '" + stringValue + "'");
        }
    }

    static long parsePositiveLongQueryParam(String stringValue, String paramName) {
        try {
            Long value = Long.valueOf(stringValue);
            if (value <= 0) {
                throw new RuntimeException(stringValue + " is not positive number");
            }
            return value;
        } catch (RuntimeException e) {
            throw new InvalidQueryParamValueException(paramName, stringValue, e.getMessage());
        }
    }

    static URL parseUrl(String string, int maxUrlLength) throws InvalidDataException {
        try {
            if (string != null && string.length() > maxUrlLength) {
                throw new InvalidDataException("url '" + string + "' too long. Maximal length is " + maxUrlLength);
            }
            URL result = new URL(string);
            String protocol = result.getProtocol();
            if (!("http".equals(protocol) || "https".equals(protocol))) {
                throw new InvalidDataException("unknown protocol '" + protocol + "'");
            }
            return result;
        } catch (MalformedURLException ex) {
            throw new InvalidDataException("'" + string + "' is not valid url: " + ex.toString());
        }
    }
}
