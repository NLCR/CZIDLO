/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api;

/**
 * Format of the response. Allowed values differ among REST operations. This only concerns successfule responses. In case of error is the format
 * allmost always xml with error code and description.
 *
 * @author Martin Řehánek
 */
public enum ResponseFormat {

    RAW, HTML, XML
}
