/*
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
package cz.nkp.urnnbn.api.v4;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.nkp.urnnbn.api.Action;
import cz.nkp.urnnbn.api.ResponseFormat;
import cz.nkp.urnnbn.api.v4.exceptions.InvalidDataException;
import cz.nkp.urnnbn.api.v4.exceptions.InvalidDigInstanceIdException;
import cz.nkp.urnnbn.api.v4.exceptions.InvalidQueryParamValueException;
import cz.nkp.urnnbn.api.v4.exceptions.InvalidRegistrarCodeException;
import cz.nkp.urnnbn.api.v4.exceptions.InvalidRegistrarScopeIdType;
import cz.nkp.urnnbn.api.v4.exceptions.InvalidRegistrarScopeIdValue;
import cz.nkp.urnnbn.api.v4.exceptions.InvalidUrnException;
import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.RegistrarScopeIdType;
import cz.nkp.urnnbn.core.RegistrarScopeIdValue;
import cz.nkp.urnnbn.core.dto.UrnNbn;

public class Parser {
    private static final Logger logger = Logger.getLogger(Parser.class.getName());

    public static UrnNbn parseUrn(String urnPar) {
        try {
            return UrnNbn.valueOf(urnPar.toLowerCase());
        } catch (RuntimeException e) {
            logger.log(Level.INFO, e.getMessage());
            throw new InvalidUrnException(urnPar, "incorrect syntax: " + e.getMessage());
        }
    }

    public static RegistrarScopeIdType parseRegistrarScopeIdType(String idTypeStr) {
        try {
            return RegistrarScopeIdType.valueOf(idTypeStr);
        } catch (IllegalArgumentException e) {
            throw new InvalidRegistrarScopeIdType(idTypeStr, e.getMessage());
        }
    }

    public static RegistrarScopeIdValue parseRegistrarScopeIdValue(String idValueStr) {
        try {
            return RegistrarScopeIdValue.valueOf(idValueStr);
        } catch (IllegalArgumentException e) {
            throw new InvalidRegistrarScopeIdValue(idValueStr, e.getMessage());
        }
    }

    public static long parseDigInstId(String digInstIdStr) {
        try {
            return Long.valueOf(digInstIdStr);
        } catch (RuntimeException e) {
            logger.log(Level.INFO, e.getMessage());
            throw new InvalidDigInstanceIdException(digInstIdStr, e.getMessage());
        }
    }

    public static RegistrarCode parseRegistrarCode(String siglaStr) {
        try {
            return RegistrarCode.valueOf(siglaStr);
        } catch (RuntimeException e) {
            throw new InvalidRegistrarCodeException(siglaStr, e.getMessage());
        }
    }

    public static int parseIntQueryParam(String value, String paramName, int minValue, int maxValue) {
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

    public static ResponseFormat parseResponseFormat(String value, String paramName) {
        try {
            return ResponseFormat.valueOf(value.toUpperCase());
        } catch (RuntimeException e) {
            throw new InvalidQueryParamValueException(paramName, value, e.getMessage());
        }
    }

    public static Action parseAction(String actionStr, String paramName) {
        try {
            return Action.valueOf(actionStr.toUpperCase());
        } catch (RuntimeException e) {
            throw new InvalidQueryParamValueException(paramName, actionStr, e.getMessage());
        }
    }

    public static boolean parseBooleanQueryParam(String stringValue, String paramName) {
        Boolean trueByJre = Boolean.valueOf(stringValue);
        if (trueByJre) {
            return true;
        } else if ("false".equals(stringValue.toLowerCase())) {
            return false;
        } else {
            throw new InvalidQueryParamValueException(paramName, stringValue, "not boolean value '" + stringValue + "'");
        }
    }

    public static long parsePositiveLongQueryParam(String stringValue, String paramName) {
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

    public static URL parseUrl(String string) throws InvalidDataException {
        try {
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
