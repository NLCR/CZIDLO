/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider.tools;

import java.util.Map;

import cz.nkp.urnnbn.oaipmhprovider.ErrorCode;
import cz.nkp.urnnbn.oaipmhprovider.OaiException;

/**
 *
 * @author Martin Řehánek (rehan at mzk.cz)
 */
public class ArgumentsChecker {

    private final Map<String, String[]> allArguments;
    private final String[] requiredArguments;
    private final String[] optionalArguments;
    private final String exclusiveArgument;

    public ArgumentsChecker(Map<String, String[]> arguments, String[] requiredArguments, String[] optionalArguments, String exclusiveArgument) {
        this.allArguments = arguments;
        this.requiredArguments = requiredArguments;
        this.optionalArguments = optionalArguments;
        this.exclusiveArgument = exclusiveArgument;
    }

    public void run() throws OaiException {
        if (exclusiveArgumentPresent()) {
            checkIfSingleValue(exclusiveArgument);
            checkNoOtherArgumentsButExclusive();
        } else {
            checkAllRequiredArgumentsPresent();
            checkOnlyRequiredOrOptionalArgumentsPresent();
        }
    }

    private boolean exclusiveArgumentPresent() {
        if (exclusiveArgument != null) {
            return allArguments.keySet().contains(exclusiveArgument);
        } else {
            return false;
        }
    }

    private void checkNoOtherArgumentsButExclusive() throws OaiException {
        for (String argument : allArguments.keySet()) {
            if (!exclusiveArgument.equals(argument)) {
                throw new OaiException(ErrorCode.badArgument, "Exclusive argument '" + exclusiveArgument
                        + "' present. No other arguments (apart from verb) allowed");
            }
        }
    }

    private void checkAllRequiredArgumentsPresent() throws OaiException {
        for (String argument : requiredArguments) {
            checkIfSingleValue(argument);
        }
    }

    private void checkIfSingleValue(String argument) throws OaiException {
        String[] values = allArguments.get(argument);
        if (values == null || values[0] == null) {
            throw new OaiException(ErrorCode.badArgument, "Missing argument '" + argument + "'");
        }
        if (values.length != 1) {
            throw new OaiException(ErrorCode.badArgument, "Multiple argument '" + argument + "'");
        }
    }

    private void checkOnlyRequiredOrOptionalArgumentsPresent() throws OaiException {
        for (String argument : allArguments.keySet()) {
            if (arrayContains(requiredArguments, argument)) {
                // do nothing. Already checked for single value
            } else if (arrayContains(optionalArguments, argument)) {
                checkIfSingleValue(argument);
            } else {
                throw new OaiException(ErrorCode.badArgument, "Illegal argument '" + argument + "'");
            }
        }
    }

    private boolean arrayContains(String[] array, String value) {
        for (String arrayItem : array) {
            if (arrayItem != null && arrayItem.equals(value)) {
                return true;
            }
        }
        return false;
    }
}
