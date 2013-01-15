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
import cz.nkp.urnnbn.xml.commons.XsltXmlTransformer;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import nu.xom.xslt.XSLException;

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
    private ValidatingXmlLoader digDocRegistrationDataValidatingLoaderV2;
    private Document digDocRegistrationXsdV2;
    private ValidatingXmlLoader digDocRegistrationDataValidatingLoaderV3;
    private Document digDocRegistrationXsdV3;
    private XsltXmlTransformer digDocRegistrationV2ToV3DataTransformer;
    private ValidatingXmlLoader digInstImportDataValidatingLoaderV2;
    private Document digInstImportXsdV2;
    private ValidatingXmlLoader digInstImportDataValidatingLoaderV3;
    private Document digInstImportXsdV3;
    private XsltXmlTransformer digInstImportV2ToV3DataTransformer;
    private Document responseV3Xsd;

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
        logger.log(Level.INFO, "Initializing {0}", ApiModuleConfiguration.class.getName());
        urnReservationDefaultSize = loader.loadInt(PropertyKeys.URN_RESERVATION_DEFAULT_SIZE);
        urnReservationMaxSize = loader.loadInt(PropertyKeys.URN_RESERVATION_MAX_SIZE);
        maxReservedSizeToPrint = loader.loadInt(PropertyKeys.MAX_RESERVED_SIZE_TO_PRINT);
    }

    void initDigDocRegistrationXsdV2(InputStream in) throws ParsingException, ValidityException, IOException {
        digDocRegistrationXsdV2 = XOMUtils.loadDocumentWithoutValidation(in);
        digDocRegistrationDataValidatingLoaderV2 = new ExternalXsdValitatingXmlLoader(digDocRegistrationXsdV2.toXML());
    }

    void initDigDocRegistrationXsdV3(InputStream in) throws ParsingException, ValidityException, IOException {
        digDocRegistrationXsdV3 = XOMUtils.loadDocumentWithoutValidation(in);
        digDocRegistrationDataValidatingLoaderV3 = new ExternalXsdValitatingXmlLoader(digDocRegistrationXsdV3.toXML());
    }

    void initDigDocRegistrationV2ToV3DataTransformer(InputStream in) throws ParsingException, IOException, XSLException {
        Document xslt = XOMUtils.loadDocumentWithoutValidation(in);
        this.digDocRegistrationV2ToV3DataTransformer = new XsltXmlTransformer(xslt);
    }

    void initDigInstImportXsdV2(InputStream in) throws ParsingException, ValidityException, IOException {
        digInstImportXsdV2 = XOMUtils.loadDocumentWithoutValidation(in);
        digInstImportDataValidatingLoaderV2 = new ExternalXsdValitatingXmlLoader(digInstImportXsdV2.toXML());
    }

    void initDigInstImportXsdV3(InputStream in) throws ParsingException, ValidityException, IOException {
        digInstImportXsdV3 = XOMUtils.loadDocumentWithoutValidation(in);
        digInstImportDataValidatingLoaderV3 = new ExternalXsdValitatingXmlLoader(digInstImportXsdV3.toXML());
    }

    void initDigInstImportV2ToV3DataTransformer(InputStream in) throws ParsingException, IOException, XSLException {
        Document xslt = XOMUtils.loadDocumentWithoutValidation(in);
        this.digInstImportV2ToV3DataTransformer = new XsltXmlTransformer(xslt);
    }

    void initResponseV3Xsd(InputStream in) throws ParsingException, IOException, XSLException {
        this.responseV3Xsd = XOMUtils.loadDocumentWithoutValidation(in);
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

    public ValidatingXmlLoader getDigDocRegistrationDataValidatingLoaderV2() {
        return digDocRegistrationDataValidatingLoaderV2;
    }

    public XsltXmlTransformer getDigDocRegistrationV2ToV3DataTransformer() {
        return digDocRegistrationV2ToV3DataTransformer;
    }

    public ValidatingXmlLoader getDigDocRegistrationDataValidatingLoaderV3() {
        return digDocRegistrationDataValidatingLoaderV3;
    }

    public ValidatingXmlLoader getDigInstImportDataValidatingLoaderV2() {
        return digInstImportDataValidatingLoaderV2;
    }

    public XsltXmlTransformer getDigInstImportV2ToV3DataTransformer() {
        return digInstImportV2ToV3DataTransformer;
    }

    public ValidatingXmlLoader getDigInstImportDataValidatingLoaderV3() {
        return digInstImportDataValidatingLoaderV3;
    }

    public Document getResponseV3Xsd() {
        return responseV3Xsd;
    }

    public Document getDigDocRegistrationXsdV2() {
        return digDocRegistrationXsdV2;
    }

    public Document getDigDocRegistrationXsdV3() {
        return digDocRegistrationXsdV3;
    }

    public Document getDigInstImportXsdV2() {
        return digInstImportXsdV2;
    }

    public Document getDigInstImportXsdV3() {
        return digInstImportXsdV3;
    }
}