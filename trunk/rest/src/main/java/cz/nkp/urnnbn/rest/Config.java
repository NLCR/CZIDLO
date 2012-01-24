/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.rest;

import java.io.File;

/**
 *
 * @author Martin Řehánek
 */
public class Config {

    //TODO: tady budou relativni cesty v deploynute aplikaci
    public static final File DIGITAL_INSTANCE_IMPORT_XSD = new File("/home/martin/NetBeansProjects/xml/src/main/java/cz/nkp/urnnbn/xml/xsd/importDigitalInstance.xsd.xml");
    public static final File RECORD_IMPORT_XSD = new File("/home/martin/NetBeansProjects/xml/src/main/java/cz/nkp/urnnbn/xml/xsd/importRecord.xsd.xml");

    public static final int URN_RESERVATION_DEFAULT_SIZE = 10;
    public static final int URN_RESERVATION_MAX_SIZE = 100;
    //public static final boolean SERVER_READ_ONLY = true;
    public static final boolean SERVER_READ_ONLY = false;
    public static final int MAX_RESERVATION_SIZE = 10;
    public static final int MAX_RESERVED_SIZE_TO_PRINT = 100;
}
