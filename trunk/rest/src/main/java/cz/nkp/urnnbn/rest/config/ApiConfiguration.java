/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.rest.config;

import cz.nkp.urnnbn.services.Services;
import cz.nkp.urnnbn.utils.PropertyLoader;
import cz.nkp.urnnbn.webcommon.config.ApplicationConfiguration;
import cz.nkp.urnnbn.webcommon.config.PropertyKeys;
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
public class ApiConfiguration implements ApplicationConfiguration {

    private static final Logger logger = Logger.getLogger(ApiConfiguration.class.getName());
//    public static String RECORD_IMPORT_XSD;
//    public static int URN_RESERVATION_DEFAULT_SIZE;
//    public static int URN_RESERVATION_MAX_SIZE;
//    public static boolean SERVER_READ_ONLY;
//    public static int MAX_RESERVED_SIZE_TO_PRINT;
//    public static boolean WEB_REDIRECTION_SSL;
//    /**
//     * 
//     * @param properties InputStream containing properties
//     * @throws IOException 
//     */
//    static void initialize(InputStream properties) throws IOException {
//        logger.info("Loading application configuration");
//        PropertyLoader loader = new PropertyLoader(properties);
//        URN_RESERVATION_DEFAULT_SIZE = loader.loadInt(PropertyKeys.URN_RESERVATION_DEFAULT_SIZE);
//        URN_RESERVATION_MAX_SIZE = loader.loadInt(PropertyKeys.URN_RESERVATION_MAX_SIZE);
//        SERVER_READ_ONLY = loader.loadBoolean(PropertyKeys.SERVER_READ_ONLY);
//        MAX_RESERVED_SIZE_TO_PRINT = loader.loadInt(PropertyKeys.MAX_RESERVED_SIZE_TO_PRINT);
//        WEB_REDIRECTION_SSL = loader.loadBooleanFalseIfNullOrEmpty(PropertyKeys.WEB_REDIRECTION_SSL);
//    }
//
//    static void initRecordImportSchema(InputStream in) throws ParsingException, ValidityException, IOException {
//        //RECORD_IMPORT_XSD = XOMUtils.loadDocumentValidByInternalXsd(in).toXML();
//        RECORD_IMPORT_XSD = XOMUtils.loadDocumentWithoutValidation(in).toXML();
//    }
    private static ApiConfiguration instance = null;
    private Boolean serverReadOnly;
    private Boolean develMode;
    private Integer urnReservationDefaultSize;
    private Integer urnReservationMaxSize;
    private Integer maxReservedSizeToPrint;
    private String recordImportSchema;

    static public ApiConfiguration instanceOf() {
        if (instance == null) {
            instance = new ApiConfiguration();
        }
        return instance;
    }

    /**
     * 
     * @param properties InputStream containing properties
     * @throws IOException 
     */
    @Override
    public void initialize(InputStream properties) throws IOException {
        logger.info("Loading application configuration");
        PropertyLoader loader = new PropertyLoader(properties);
        serverReadOnly = loader.loadBoolean(PropertyKeys.SERVER_READ_ONLY);
        develMode = loader.loadBooleanFalseIfNullOrEmpty(PropertyKeys.DEVEL);
        urnReservationDefaultSize = loader.loadInt(PropertyKeys.URN_RESERVATION_DEFAULT_SIZE);
        urnReservationMaxSize = loader.loadInt(PropertyKeys.URN_RESERVATION_MAX_SIZE);
        maxReservedSizeToPrint = loader.loadInt(PropertyKeys.MAX_RESERVED_SIZE_TO_PRINT);
        Services.init(develMode, urnReservationMaxSize);
    }

    void initRecordImportSchema(InputStream in) throws ParsingException, ValidityException, IOException {
        //RECORD_IMPORT_XSD = XOMUtils.loadDocumentValidByInternalXsd(in).toXML();
        recordImportSchema = XOMUtils.loadDocumentWithoutValidation(in).toXML();
    }

    @Override
    public Boolean isServerReadOnly() {
        return serverReadOnly;
    }

    @Override
    public Boolean isDevelMode() {
        return develMode;
    }

    public Integer getMaxReservedSizeToPrint() {
        return maxReservedSizeToPrint;
    }

    public String getRecordImportSchema() {
        return recordImportSchema;
    }

    public Integer getUrnReservationDefaultSize() {
        return urnReservationDefaultSize;
    }

    public Integer getUrnReservationMaxSize() {
        return urnReservationMaxSize;
    }
}