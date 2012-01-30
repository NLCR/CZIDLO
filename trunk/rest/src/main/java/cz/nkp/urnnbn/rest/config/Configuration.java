/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.rest.config;

import cz.nkp.urnnbn.utils.PropertyLoader;
import cz.nkp.urnnbn.xml.commons.XOMUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

/**
 *
 * @author Martin Řehánek
 */
public class Configuration {

    private static final Logger logger = Logger.getLogger(Configuration.class.getName());
    public static String RECORD_IMPORT_XSD;
    public static int URN_RESERVATION_DEFAULT_SIZE;
    public static int URN_RESERVATION_MAX_SIZE;
    public static boolean SERVER_READ_ONLY;
    public static int MAX_RESERVED_SIZE_TO_PRINT;

    /**
     * 
     * @param properties InputStream containing properties
     * @throws IOException 
     */
    public static void initialize(InputStream properties) throws IOException {
        logger.info("Loading application configuration");
        PropertyLoader loader = new PropertyLoader(properties);
        URN_RESERVATION_DEFAULT_SIZE = loader.loadInt(PropertyKeys.URN_RESERVATION_DEFAULT_SIZE);
        URN_RESERVATION_MAX_SIZE = loader.loadInt(PropertyKeys.URN_RESERVATION_MAX_SIZE);
        SERVER_READ_ONLY = loader.loadBoolean(PropertyKeys.SERVER_READ_ONLY);
        MAX_RESERVED_SIZE_TO_PRINT = loader.loadInt(PropertyKeys.MAX_RESERVED_SIZE_TO_PRINT);
    }

    static void initRecordImportSchema(InputStream in) throws ParsingException, ValidityException, IOException {
        //RECORD_IMPORT_XSD = XOMUtils.loadDocumentValidByInternalXsd(in).toXML();
        RECORD_IMPORT_XSD = XOMUtils.loadDocumentWithoutValidation(in).toXML();
    }
}
