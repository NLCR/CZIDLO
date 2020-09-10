/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api.config;

import cz.nkp.urnnbn.utils.PropertyLoader;
import cz.nkp.urnnbn.webcommon.config.ApplicationConfiguration;
import cz.nkp.urnnbn.xml.commons.ExternalXsdValitatingXmlLoader;
import cz.nkp.urnnbn.xml.commons.ValidatingXmlLoader;
import cz.nkp.urnnbn.xml.commons.XOMUtils;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import nu.xom.xslt.XSLException;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Martin Řehánek
 */
public class ApiModuleConfiguration extends ApplicationConfiguration {

    private static final Logger logger = Logger.getLogger(ApiModuleConfiguration.class.getName());
    private static ApiModuleConfiguration instance = null;
    private Integer urnReservationDefaultSize;
    private Integer urnReservationMaxSize;
    private Integer maxReservedSizeToPrint;
    private String webSearchUrlPrefix;
    // API V6
    private ValidatingXmlLoader digDocRegistrationDataValidatingLoaderV6;
    private Document digDocRegistrationXsdV6;
    private ValidatingXmlLoader digInstImportDataValidatingLoaderV6;
    private Document digInstImportXsdV6;
    private Document responseV6Xsd;
    // API V5
    private ValidatingXmlLoader digDocRegistrationDataValidatingLoaderV5;
    private Document digDocRegistrationXsdV5;
    private ValidatingXmlLoader digInstImportDataValidatingLoaderV5;
    private Document digInstImportXsdV5;
    private Document responseV5Xsd;
    // API V4
    private ValidatingXmlLoader digDocRegistrationDataValidatingLoaderV4;
    private Document digDocRegistrationXsdV4;
    private ValidatingXmlLoader digInstImportDataValidatingLoaderV4;
    private Document digInstImportXsdV4;
    private Document responseV4Xsd;
    // API V3
    private ValidatingXmlLoader digDocRegistrationDataValidatingLoaderV3;
    private Document digDocRegistrationXsdV3;
    private ValidatingXmlLoader digInstImportDataValidatingLoaderV3;
    private Document digInstImportXsdV3;
    private Document responseV3Xsd;

    static public ApiModuleConfiguration instanceOf() {
        if (instance == null) {
            instance = new ApiModuleConfiguration();
        }
        return instance;
    }

    /**
     * @param appName
     * @param loader
     * @throws IOException
     */
    @Override
    public void initialize(String appName, PropertyLoader loader) throws IOException {
        super.initialize(appName, loader);
        logger.log(Level.INFO, "Initializing {0}", appName);
        urnReservationDefaultSize = loader.loadInt(PropertyKeys.URN_RESERVATION_DEFAULT_SIZE);
        urnReservationMaxSize = loader.loadInt(PropertyKeys.URN_RESERVATION_MAX_SIZE);
        maxReservedSizeToPrint = loader.loadInt(PropertyKeys.MAX_RESERVED_SIZE_TO_PRINT);
        webSearchUrlPrefix = loader.loadString(PropertyKeys.WEB_SEARCH_URL_PREFIX);
    }

    // API V6
    void initDigDocRegistrationXsdV6(InputStream in) throws ParsingException, ValidityException, IOException {
        digDocRegistrationXsdV6 = XOMUtils.loadDocumentWithoutValidation(in);
        digDocRegistrationDataValidatingLoaderV6 = new ExternalXsdValitatingXmlLoader(digDocRegistrationXsdV6.toXML());
    }

    void initDigInstImportXsdV6(InputStream in) throws ParsingException, ValidityException, IOException {
        digInstImportXsdV6 = XOMUtils.loadDocumentWithoutValidation(in);
        digInstImportDataValidatingLoaderV6 = new ExternalXsdValitatingXmlLoader(digInstImportXsdV6.toXML());
    }

    void initResponseV6Xsd(InputStream in) throws ParsingException, IOException, XSLException {
        this.responseV6Xsd = XOMUtils.loadDocumentWithoutValidation(in);
    }

    // API V5
    void initDigDocRegistrationXsdV5(InputStream in) throws ParsingException, ValidityException, IOException {
        digDocRegistrationXsdV5 = XOMUtils.loadDocumentWithoutValidation(in);
        digDocRegistrationDataValidatingLoaderV5 = new ExternalXsdValitatingXmlLoader(digDocRegistrationXsdV5.toXML());
    }

    void initDigInstImportXsdV5(InputStream in) throws ParsingException, ValidityException, IOException {
        digInstImportXsdV5 = XOMUtils.loadDocumentWithoutValidation(in);
        digInstImportDataValidatingLoaderV5 = new ExternalXsdValitatingXmlLoader(digInstImportXsdV5.toXML());
    }

    void initResponseV5Xsd(InputStream in) throws ParsingException, IOException, XSLException {
        this.responseV5Xsd = XOMUtils.loadDocumentWithoutValidation(in);
    }

    // API V4
    void initDigDocRegistrationXsdV4(InputStream in) throws ParsingException, ValidityException, IOException {
        digDocRegistrationXsdV4 = XOMUtils.loadDocumentWithoutValidation(in);
        digDocRegistrationDataValidatingLoaderV4 = new ExternalXsdValitatingXmlLoader(digDocRegistrationXsdV4.toXML());
    }

    void initDigInstImportXsdV4(InputStream in) throws ParsingException, ValidityException, IOException {
        digInstImportXsdV4 = XOMUtils.loadDocumentWithoutValidation(in);
        digInstImportDataValidatingLoaderV4 = new ExternalXsdValitatingXmlLoader(digInstImportXsdV4.toXML());
    }

    void initResponseV4Xsd(InputStream in) throws ParsingException, IOException, XSLException {
        this.responseV4Xsd = XOMUtils.loadDocumentWithoutValidation(in);
    }

    // API V3
    void initDigDocRegistrationXsdV3(InputStream in) throws ParsingException, ValidityException, IOException {
        digDocRegistrationXsdV3 = XOMUtils.loadDocumentWithoutValidation(in);
        digDocRegistrationDataValidatingLoaderV3 = new ExternalXsdValitatingXmlLoader(digDocRegistrationXsdV3.toXML());
    }

    void initDigInstImportXsdV3(InputStream in) throws ParsingException, ValidityException, IOException {
        digInstImportXsdV3 = XOMUtils.loadDocumentWithoutValidation(in);
        digInstImportDataValidatingLoaderV3 = new ExternalXsdValitatingXmlLoader(digInstImportXsdV3.toXML());
    }

    void initResponseV3Xsd(InputStream in) throws ParsingException, IOException, XSLException {
        this.responseV3Xsd = XOMUtils.loadDocumentWithoutValidation(in);
    }

    public String getWebSearchUrlPrefix() {
        return webSearchUrlPrefix;
    }

    public Integer getMaxReservedSizeToPrint() {
        return maxReservedSizeToPrint;
    }

    public Integer getUrnReservationDefaultSize() {
        return urnReservationDefaultSize;
    }

    public Integer getUrnReservationMaxSize() {
        return urnReservationMaxSize;
    }

    public ValidatingXmlLoader getDigDocRegistrationDataValidatingLoaderV3() {
        return digDocRegistrationDataValidatingLoaderV3;
    }

    public ValidatingXmlLoader getDigDocRegistrationDataValidatingLoaderV4() {
        return digDocRegistrationDataValidatingLoaderV4;
    }

    public ValidatingXmlLoader getDigDocRegistrationDataValidatingLoaderV5() {
        return digDocRegistrationDataValidatingLoaderV5;
    }

    public ValidatingXmlLoader getDigDocRegistrationDataValidatingLoaderV6() {
        return digDocRegistrationDataValidatingLoaderV6;
    }

    public ValidatingXmlLoader getDigInstImportDataValidatingLoaderV3() {
        return digInstImportDataValidatingLoaderV3;
    }

    public ValidatingXmlLoader getDigInstImportDataValidatingLoaderV4() {
        return digInstImportDataValidatingLoaderV4;
    }

    public ValidatingXmlLoader getDigInstImportDataValidatingLoaderV5() {
        return digInstImportDataValidatingLoaderV5;
    }

    public ValidatingXmlLoader getDigInstImportDataValidatingLoaderV6() {
        return digInstImportDataValidatingLoaderV6;
    }

    public Document getResponseV3Xsd() {
        return responseV3Xsd;
    }

    public Document getResponseV4Xsd() {
        return responseV4Xsd;
    }

    public Document getResponseV5Xsd() {
        return responseV5Xsd;
    }

    public Document getResponseV6Xsd() {
        return responseV6Xsd;
    }

    public Document getDigDocRegistrationXsdV3() {
        return digDocRegistrationXsdV3;
    }

    public Document getDigDocRegistrationXsdV4() {
        return digDocRegistrationXsdV4;
    }

    public Document getDigDocRegistrationXsdV5() {
        return digDocRegistrationXsdV5;
    }

    public Document getDigDocRegistrationXsdV6() {
        return digDocRegistrationXsdV6;
    }

    public Document getDigInstImportXsdV3() {
        return digInstImportXsdV3;
    }

    public Document getDigInstImportXsdV4() {
        return digInstImportXsdV4;
    }

    public Document getDigInstImportXsdV5() {
        return digInstImportXsdV5;
    }

    public Document getDigInstImportXsdV6() {
        return digInstImportXsdV6;
    }

}