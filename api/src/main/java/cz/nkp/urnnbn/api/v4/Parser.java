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

import cz.nkp.urnnbn.api.v4.exceptions.IllegalFormatError;
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
    private static final Logger LOGGER = Logger.getLogger(Parser.class.getName());

    public static UrnNbn parseUrn(ResponseFormat format, String urnPar) {
        try {
            return UrnNbn.valueOf(urnPar.toLowerCase());
        } catch (RuntimeException e) {
            LOGGER.log(Level.INFO, e.getMessage());
            throw new InvalidUrnException(format, urnPar, "incorrect syntax: " + e.getMessage());
        }
    }

    public static RegistrarScopeIdType parseRegistrarScopeIdType(ResponseFormat format, String idTypeStr) {
        try {
            return RegistrarScopeIdType.valueOf(idTypeStr);
        } catch (IllegalArgumentException e) {
            throw new InvalidRegistrarScopeIdType(format, idTypeStr, e.getMessage());
        }
    }

    public static RegistrarScopeIdValue parseRegistrarScopeIdValue(ResponseFormat format, String idValueStr) {
        try {
            return RegistrarScopeIdValue.valueOf(idValueStr);
        } catch (IllegalArgumentException e) {
            throw new InvalidRegistrarScopeIdValue(format, idValueStr, e.getMessage());
        }
    }

    public static long parseDigInstId(ResponseFormat format, String digInstIdStr) {
        try {
            return Long.valueOf(digInstIdStr);
        } catch (RuntimeException e) {
            LOGGER.log(Level.INFO, e.getMessage());
            throw new InvalidDigInstanceIdException(format, digInstIdStr, e.getMessage());
        }
    }

    public static RegistrarCode parseRegistrarCode(ResponseFormat format, String siglaStr) {
        try {
            return RegistrarCode.valueOf(siglaStr);
        } catch (RuntimeException e) {
            throw new InvalidRegistrarCodeException(format, siglaStr, e.getMessage());
        }
    }

    public static int parseIntQueryParam(ResponseFormat format, String value, String paramName, int minValue, int maxValue) {
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
            LOGGER.log(Level.INFO, e.getMessage());
            throw new InvalidQueryParamValueException(format, paramName, value, e.getMessage());
        }
    }

    public static ResponseFormat parseResponseFormat(ResponseFormat format, String value, String paramName) {
        try {
            return ResponseFormat.valueOf(value.toUpperCase());
        } catch (RuntimeException e) {
            throw new InvalidQueryParamValueException(format, paramName, value, e.getMessage());
        }
    }

    public static Action parseAction(ResponseFormat format, String actionStr, String paramName) {
        try {
            return Action.valueOf(actionStr.toUpperCase());
        } catch (RuntimeException e) {
            throw new InvalidQueryParamValueException(format, paramName, actionStr, e.getMessage());
        }
    }

    @Deprecated
    public static boolean parseBooleanQueryParam(ResponseFormat format, String stringValue, String paramName) {
        Boolean trueByJre = Boolean.valueOf(stringValue);
        if (trueByJre) {
            return true;
        } else if ("false".equals(stringValue.toLowerCase())) {
            return false;
        } else {
            throw new InvalidQueryParamValueException(format, paramName, stringValue, "not boolean value");
        }
    }

    // TODO: asi by slo taky vyresit s prazdou hodnotou
    public static boolean parseBooleanQueryParamDefaultIfNullOrEmpty(ResponseFormat format, String stringValue, String paramName, boolean defaultValue) {
        if (stringValue == null || stringValue.isEmpty()) {
            return defaultValue;
        }
        Boolean trueByJre = Boolean.valueOf(stringValue);
        if (trueByJre) {
            return true;
        } else if ("false".equals(stringValue.toLowerCase())) {
            return false;
        } else {
            throw new InvalidQueryParamValueException(format, paramName, stringValue, "not boolean value");
        }
    }

    public static long parsePositiveLongQueryParam(ResponseFormat format, String stringValue, String paramName) {
        try {
            return Long.valueOf(stringValue);
        } catch (NumberFormatException e) {
            throw new InvalidQueryParamValueException(format, paramName, stringValue, e.getMessage());
        }
    }

    public static URL parseUrl(ResponseFormat format, String string) throws InvalidDataException {
        try {
            URL result = new URL(string);
            String protocol = result.getProtocol();
            if (!("http".equals(protocol) || "https".equals(protocol))) {
                throw new InvalidDataException(format, "unknown protocol '" + protocol + "'");
            }
            return result;
        } catch (MalformedURLException ex) {
            throw new InvalidDataException(format, String.format("%s is not valid url: %s", string, ex.toString()));
        }
    }

    public static ResponseFormat parseFormat(String formatStr) {
        try {
            return ResponseFormat.valueOf(formatStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalFormatError(ResponseFormat.XML, formatStr);
        }
    }
}
