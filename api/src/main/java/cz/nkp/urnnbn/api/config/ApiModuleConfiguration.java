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
    //API V3
    private ValidatingXmlLoader digDocRegistrationDataValidatingLoaderV3;
    private Document digDocRegistrationXsdV3;
    private ValidatingXmlLoader digInstImportDataValidatingLoaderV3;
    private Document digInstImportXsdV3;
    private Document responseV3Xsd;
    //API V2
    private ValidatingXmlLoader digDocRegistrationDataValidatingLoaderV2;
    private Document digDocRegistrationXsdV2;
    private ValidatingXmlLoader digInstImportDataValidatingLoaderV2;
    private Document digInstImportXsdV2;
    //API V2 requests -> API V3 requests transformations
    private XsltXmlTransformer digInstImportV2ToV3DataTransformer;
    private XsltXmlTransformer digDocRegistrationV2ToV3DataTransformer;
    //API V3 responses -> API V2 responses transformations
    private XsltXmlTransformer errorResponseV3ToV2Transformer;
    private XsltXmlTransformer deactivateDigInstResponseV3ToV2Transformer;
    private XsltXmlTransformer deleteRegScopeIdResponseV3ToV2Transformer;
    private XsltXmlTransformer deleteRegScopeIdsResponseV3ToV2Transformer;
    private XsltXmlTransformer getDigDocResponseV3ToV2Transformer;
    private XsltXmlTransformer getDigDocsResponseV3ToV2Transformer;
    private XsltXmlTransformer getDigInstResponseV3ToV2Transformer;
    private XsltXmlTransformer getDigInstsResponseV3ToV2Transformer;
    private XsltXmlTransformer getRegistrarResponseV3ToV2Transformer;
    private XsltXmlTransformer getRegistrarsResponseV3ToV2Transformer;
    private XsltXmlTransformer getRegScopeIdResponseV3ToV2Transformer;
    private XsltXmlTransformer getRegScopeIdsResponseV3ToV2Transformer;
    private XsltXmlTransformer getUrnNbnResponseV3ToV2Transformer;
    private XsltXmlTransformer getUrnNbnReservationsResponseV3ToV2Transformer;
    private XsltXmlTransformer importDigitalInstanceResponseV3ToV2Transformer;
    private XsltXmlTransformer registerDigDocResponseV3ToV2Transformer;
    private XsltXmlTransformer reserveUrnNbnResponseV3ToV2Transformer;
    private XsltXmlTransformer setOrUpdateRegScopeIdResponseV3ToV2Transformer;

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
    public void initialize(String appName, PropertyLoader loader) throws IOException {
        super.initialize(appName, loader);
        logger.log(Level.INFO, "Initializing {0}", appName);
        urnReservationDefaultSize = loader.loadInt(PropertyKeys.URN_RESERVATION_DEFAULT_SIZE);
        urnReservationMaxSize = loader.loadInt(PropertyKeys.URN_RESERVATION_MAX_SIZE);
        maxReservedSizeToPrint = loader.loadInt(PropertyKeys.MAX_RESERVED_SIZE_TO_PRINT);
    }

    //API V3
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

    //API V2
    void initDigDocRegistrationXsdV2(InputStream in) throws ParsingException, ValidityException, IOException {
        digDocRegistrationXsdV2 = XOMUtils.loadDocumentWithoutValidation(in);
        digDocRegistrationDataValidatingLoaderV2 = new ExternalXsdValitatingXmlLoader(digDocRegistrationXsdV2.toXML());
    }

    void initDigInstImportXsdV2(InputStream in) throws ParsingException, ValidityException, IOException {
        digInstImportXsdV2 = XOMUtils.loadDocumentWithoutValidation(in);
        digInstImportDataValidatingLoaderV2 = new ExternalXsdValitatingXmlLoader(digInstImportXsdV2.toXML());
    }

    //API V2 requests -> API V3 requests transformations
    void initDigDocRegistrationV2ToV3DataTransformer(InputStream in) throws ParsingException, IOException, XSLException {
        this.digDocRegistrationV2ToV3DataTransformer = transformerFromXmlFromInputStream(in);
    }

    void initDigInstImportV2ToV3DataTransformer(InputStream in) throws ParsingException, IOException, XSLException {
        this.digInstImportV2ToV3DataTransformer = transformerFromXmlFromInputStream(in);
    }

    //API V3 responses -> API V2 responses transformations
    void initErrorResponseV3ToV2Transformer(InputStream in) throws ParsingException, IOException, XSLException {
        this.errorResponseV3ToV2Transformer = transformerFromXmlFromInputStream(in);
    }

    void initDeactivateDigInstResponseV3ToV2Transformer(InputStream in) throws ParsingException, IOException, XSLException {
        this.deactivateDigInstResponseV3ToV2Transformer = transformerFromXmlFromInputStream(in);
    }

    void initDeleteRegScopeIdResponseV3ToV2Transformer(InputStream in) throws ParsingException, IOException, XSLException {
        this.deleteRegScopeIdResponseV3ToV2Transformer = transformerFromXmlFromInputStream(in);
    }

    void initDeleteRegScopeIdsResponseV3ToV2Transformer(InputStream in) throws ParsingException, IOException, XSLException {
        this.deleteRegScopeIdsResponseV3ToV2Transformer = transformerFromXmlFromInputStream(in);
    }

    void initGetDigDocResponseV3ToV2Transformer(InputStream in) throws ParsingException, IOException, XSLException {
        this.getDigDocResponseV3ToV2Transformer = transformerFromXmlFromInputStream(in);
    }

    void initGetDigDocsResponseV3ToV2Transformer(InputStream in) throws ParsingException, IOException, XSLException {
        this.getDigDocsResponseV3ToV2Transformer = transformerFromXmlFromInputStream(in);
    }

    void initGetDigInstResponseV3ToV2Transformer(InputStream in) throws ParsingException, IOException, XSLException {
        this.getDigInstResponseV3ToV2Transformer = transformerFromXmlFromInputStream(in);
    }

    void initGetDigInstsResponseV3ToV2Transformer(InputStream in) throws ParsingException, IOException, XSLException {
        this.getDigInstsResponseV3ToV2Transformer = transformerFromXmlFromInputStream(in);
    }

    void initGetRegistrarResponseV3ToV2Transformer(InputStream in) throws ParsingException, IOException, XSLException {
        this.getRegistrarResponseV3ToV2Transformer = transformerFromXmlFromInputStream(in);
    }

    void initGetRegistrarsResponseV3ToV2Transformer(InputStream in) throws ParsingException, IOException, XSLException {
        this.getRegistrarsResponseV3ToV2Transformer = transformerFromXmlFromInputStream(in);
    }

    void initGetRegScopeIdResponseV3ToV2Transformer(InputStream in) throws ParsingException, IOException, XSLException {
        this.getRegScopeIdResponseV3ToV2Transformer = transformerFromXmlFromInputStream(in);
    }

    void initGetRegScopeIdsResponseV3ToV2Transformer(InputStream in) throws ParsingException, IOException, XSLException {
        this.getRegScopeIdsResponseV3ToV2Transformer = transformerFromXmlFromInputStream(in);
    }

    void initGetUrnNbnResponseV3ToV2Transformer(InputStream in) throws ParsingException, IOException, XSLException {
        this.getUrnNbnResponseV3ToV2Transformer = transformerFromXmlFromInputStream(in);
    }

    void initGetUrnNbnReservationsResponseV3ToV2Transformer(InputStream in) throws ParsingException, IOException, XSLException {
        this.getUrnNbnReservationsResponseV3ToV2Transformer = transformerFromXmlFromInputStream(in);
    }

    void initRegisterDigDocResponseV3ToV2Transformer(InputStream in) throws ParsingException, IOException, XSLException {
        this.registerDigDocResponseV3ToV2Transformer = transformerFromXmlFromInputStream(in);
    }

    void initImportDigitalInstanceResponseV3ToV2Transformer(InputStream in) throws ParsingException, IOException, XSLException {
        this.importDigitalInstanceResponseV3ToV2Transformer = transformerFromXmlFromInputStream(in);
    }

    void initReserveUrnNbnResponseV3ToV2Transformer(InputStream in) throws ParsingException, IOException, XSLException {
        this.reserveUrnNbnResponseV3ToV2Transformer = transformerFromXmlFromInputStream(in);
    }

    void initSetOrUpdateRegScopeIdResponseV3ToV2Transformer(InputStream in) throws ParsingException, IOException, XSLException {
        this.setOrUpdateRegScopeIdResponseV3ToV2Transformer = transformerFromXmlFromInputStream(in);
    }

    private XsltXmlTransformer transformerFromXmlFromInputStream(InputStream in) throws ParsingException, IOException, XSLException {
        Document xslt = XOMUtils.loadDocumentWithoutValidation(in);
        return new XsltXmlTransformer(xslt);
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

    public XsltXmlTransformer getErrorResponseV3ToV2Transformer() {
        return errorResponseV3ToV2Transformer;
    }

    public XsltXmlTransformer getDeactivateDigInstResponseV3ToV2Transformer() {
        return deactivateDigInstResponseV3ToV2Transformer;
    }

    public XsltXmlTransformer getDeleteRegScopeIdResponseV3ToV2Transformer() {
        return deleteRegScopeIdResponseV3ToV2Transformer;
    }

    public XsltXmlTransformer getDeleteRegScopeIdsResponseV3ToV2Transformer() {
        return deleteRegScopeIdsResponseV3ToV2Transformer;
    }

    public XsltXmlTransformer getGetDigDocResponseV3ToV2Transformer() {
        return getDigDocResponseV3ToV2Transformer;
    }

    public XsltXmlTransformer getGetDigDocsResponseV3ToV2Transformer() {
        return getDigDocsResponseV3ToV2Transformer;
    }

    public XsltXmlTransformer getGetDigInstResponseV3ToV2Transformer() {
        return getDigInstResponseV3ToV2Transformer;
    }

    public XsltXmlTransformer getGetDigInstsResponseV3ToV2Transformer() {
        return getDigInstsResponseV3ToV2Transformer;
    }

    public XsltXmlTransformer getGetRegistrarResponseV3ToV2Transformer() {
        return getRegistrarResponseV3ToV2Transformer;
    }

    public XsltXmlTransformer getGetRegistrarsResponseV3ToV2Transformer() {
        return getRegistrarsResponseV3ToV2Transformer;
    }

    public XsltXmlTransformer getGetRegScopeIdResponseV3ToV2Transformer() {
        return getRegScopeIdResponseV3ToV2Transformer;
    }

    public XsltXmlTransformer getGetRegScopeIdsResponseV3ToV2Transformer() {
        return getRegScopeIdsResponseV3ToV2Transformer;
    }

    public XsltXmlTransformer getGetUrnNbnResponseV3ToV2Transformer() {
        return getUrnNbnResponseV3ToV2Transformer;
    }

    public XsltXmlTransformer getGetUrnNbnReservationsResponseV3ToV2Transformer() {
        return getUrnNbnReservationsResponseV3ToV2Transformer;
    }

    public XsltXmlTransformer getRegisterDigDocResponseV3ToV2Transformer() {
        return registerDigDocResponseV3ToV2Transformer;
    }

    public XsltXmlTransformer getImportDigitalInstanceResponseV3ToV2Transformer() {
        return importDigitalInstanceResponseV3ToV2Transformer;
    }

    public XsltXmlTransformer getReserveUrnNbnResponseV3ToV2Transformer() {
        return reserveUrnNbnResponseV3ToV2Transformer;
    }

    public XsltXmlTransformer getSetOrUpdateRegScopeIdResponseV3ToV2Transformer() {
        return setOrUpdateRegScopeIdResponseV3ToV2Transformer;
    }
}