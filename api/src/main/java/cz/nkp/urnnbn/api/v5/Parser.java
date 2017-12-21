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
package cz.nkp.urnnbn.api.v5;

import cz.nkp.urnnbn.api.v5.exceptions.*;
import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.RegistrarScopeIdType;
import cz.nkp.urnnbn.core.RegistrarScopeIdValue;
import cz.nkp.urnnbn.core.dto.UrnNbn;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Parser {
    private static final Logger LOGGER = Logger.getLogger(Parser.class.getName());

    public static UrnNbn parseUrn(ResponseFormat format, String urnPar) {
        try {
            return UrnNbn.valueOf(urnPar.toLowerCase());
        } catch (RuntimeException e) {
            throw new InvalidUrnException(format, urnPar, "incorrect syntax: " + e.getMessage());
        }
    }

    public static RegistrarScopeIdType parseRegistrarScopeIdType(ResponseFormat format, String idTypeStr) {
        try {
            return RegistrarScopeIdType.valueOf(idTypeStr);
        } catch (IllegalArgumentException e) {
            throw new InvalidRegistrarScopeIdTypeException(format, idTypeStr, e.getMessage());
        }
    }

    public static RegistrarScopeIdValue parseRegistrarScopeIdValue(ResponseFormat format, String idValueStr) {
        try {
            return RegistrarScopeIdValue.valueOf(idValueStr);
        } catch (IllegalArgumentException e) {
            throw new InvalidRegistrarScopeIdValueException(format, idValueStr, e.getMessage());
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

    public static long parseDigDocId(ResponseFormat format, String digDocIdStr) {
        try {
            return Long.valueOf(digDocIdStr);
        } catch (RuntimeException e) {
            LOGGER.log(Level.INFO, e.getMessage());
            throw new InvalidDigDocIdException(format, digDocIdStr, e.getMessage());
        }
    }

    public static RegistrarCode parseRegistrarCode(ResponseFormat format, String registrarCode) {
        try {
            return RegistrarCode.valueOf(registrarCode);
        } catch (RuntimeException e) {
            throw new InvalidRegistrarCodeException(format, registrarCode, e.getMessage());
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
            throw new IllegalFormatException(formatStr);
        }
    }
}
