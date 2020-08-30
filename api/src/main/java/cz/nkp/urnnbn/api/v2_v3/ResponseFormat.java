package cz.nkp.urnnbn.api.v2_v3;

/**
 * Format of the response. Allowed values differ among REST operations. This only concerns successfule responses. In case of error is the format
 * almost always xml with error code and description.
 *
 */
public enum ResponseFormat {
    HTML, XML;
}
