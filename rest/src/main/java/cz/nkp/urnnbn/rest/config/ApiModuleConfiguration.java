/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.rest.config;

import cz.nkp.urnnbn.utils.PropertyLoader;
import cz.nkp.urnnbn.webcommon.config.ApplicationConfiguration;

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
public class ApiModuleConfiguration extends ApplicationConfiguration {

    private static final Logger logger = Logger.getLogger(ApiModuleConfiguration.class.getName());
    private static ApiModuleConfiguration instance = null;
    private Integer urnReservationDefaultSize;
    private Integer urnReservationMaxSize;
    private Integer maxReservedSizeToPrint;
    private String recordImportSchema;

    static public ApiModuleConfiguration instanceOf() {
        if (instance == null) {
            instance = new ApiModuleConfiguration();
        }
        return instance;
    }

    /**
     * 
     * @param properties InputStream containing properties
     * @throws IOException 
     */
    @Override
    public void initialize(PropertyLoader loader) throws IOException {
        super.initialize(loader);
        logger.info("Loading configuration");
        urnReservationDefaultSize = loader.loadInt(PropertyKeys.URN_RESERVATION_DEFAULT_SIZE);
        urnReservationMaxSize = loader.loadInt(PropertyKeys.URN_RESERVATION_MAX_SIZE);
        maxReservedSizeToPrint = loader.loadInt(PropertyKeys.MAX_RESERVED_SIZE_TO_PRINT);
    }

    void initRecordImportSchema(InputStream in) throws ParsingException, ValidityException, IOException {
        recordImportSchema = XOMUtils.loadDocumentWithoutValidation(in).toXML();
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