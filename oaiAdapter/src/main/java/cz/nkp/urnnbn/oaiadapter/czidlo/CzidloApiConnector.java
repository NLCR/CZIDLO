/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter.czidlo;

import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.oaiadapter.utils.DiBuilder;
import cz.nkp.urnnbn.oaiadapter.utils.XmlTools;
import nu.xom.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Jan Rychtář
 * @author Martin Řehánek
 */
public class CzidloApiConnector {

    private static final Logger logger = Logger.getLogger(CzidloApiConnector.class.getName());

    public static final String ERROR_CODE_REGISTAR = "UNKNOWN_REGISTRAR";
    public static final String ERROR_CODE_DOCUMENT = "UNKNOWN_DIGITAL_DOCUMENT";
    public static final String CZIDLO_NAMESPACE = "http://resolver.nkp.cz/v4/";
    public static final XPathContext CONTEXT = new XPathContext("r", CZIDLO_NAMESPACE);
    public final String baseUrl;
    private final Credentials credentials;
    private final boolean ignoreInvalidCertificate;
    private final HttpConnector httpConnector = new HttpConnector();
    private final XmlTools xmlTools = new XmlTools();

    public CzidloApiConnector(String baseUrl, Credentials credentials, boolean ignoreInvalidCertificate) {
        this.baseUrl = "https://" + baseUrl + "/v4/";
        this.credentials = credentials;
        this.ignoreInvalidCertificate = ignoreInvalidCertificate;
    }

    public String getCzidloApiUrl() {
        return baseUrl;
    }

    public String getLogin() {
        return credentials.getLogin();
    }


    /**
     * @param registrarCode
     * @param registrarScopeIdType
     * @param registrarScopeIdValue
     * @param withDigitalInstances
     * @return digital-document identified by registrar-scope-identifier for given registrar or null if no such digital-document exists
     * @throws CzidloApiErrorException in case of API error response
     * @throws ParsingException        in case of parsing xml from API response body
     * @throws IOException             in case of network error
     */
    public Document getDigitalDocumentByRegistrarScopeId(String registrarCode, String registrarScopeIdType, String registrarScopeIdValue, boolean withDigitalInstances) throws CzidloApiErrorException, ParsingException, IOException {
        String url = baseUrl
                + "registrars/" + registrarCode
                + "/digitalDocuments/registrarScopeIdentifier/" + registrarScopeIdType + "/" + registrarScopeIdValue
                + "?format=xml&digitalInstances=" + withDigitalInstances;
        ApiResponse apiResponse = httpConnector.httpGet(url, credentials, ignoreInvalidCertificate);
        if (apiResponse.getHttpCode() == 200) { //ok, document found
            Document document = new Builder().build(apiResponse.getBody(), null);
            return document;
        } else {
            try {
                Document document = new Builder().build(apiResponse.getBody(), null);
                CzidloApiError apiError = xmlTools.parseErrorMessage(document);
                if (apiResponse.getHttpCode() == 404 && "UNKNOWN_DIGITAL_DOCUMENT".equals(apiError.getErrorCode())) { //document not found
                    return null;
                } else { //other error
                    throw new CzidloApiErrorException(url, apiResponse.getHttpCode(), apiError);
                }
            } catch (ParsingException | IOException e) { //other error but failed to parse body
                throw new CzidloApiErrorException(url, apiResponse.getHttpCode(), null);
            }
        }
    }

    /**
     * @param registrarCode
     * @param registrarScopeIdType
     * @param registrarScopeIdValue
     * @return URN:NBN for digital-document identified by registrar-scope-identifier for given registrar or null if no such digital-document exists
     * @throws ParsingException        in case of parsing xml from API response body (getting digital-document by registrar-scope-id)
     * @throws CzidloApiErrorException in case of API error response (getting digital-document by registrar-scope-id)
     * @throws IOException             in case of network error when (getting digital-document by registrar-scope-id)
     */
    public String getUrnnbnByRegistrarScopeId(String registrarCode, String registrarScopeIdType, String registrarScopeIdValue)
            throws ParsingException, IOException, CzidloApiErrorException {
        Document document = getDigitalDocumentByRegistrarScopeId(registrarCode, registrarScopeIdType, registrarScopeIdValue, false);
        if (document == null) {
            return null;
        } else {
            return document.query("/r:response/r:digitalDocument/r:urnNbn/r:value", CONTEXT).get(0).getValue();
        }
    }

    /**
     * @param urnNbn
     * @return URN:NBN details, i.e. state, possibly datestamps, deactivation note, etc.
     * @throws CzidloApiErrorException in case of API error response
     * @throws ParsingException        in case of parsing xml from API response body
     * @throws IOException             in case of network error
     */
    public Document getUrnnbnDetails(String urnNbn) throws CzidloApiErrorException, ParsingException, IOException {
        String url = baseUrl + "urnnbn/" + urnNbn + "?format=xml";
        ApiResponse apiResponse = httpConnector.httpGet(url, credentials, ignoreInvalidCertificate);
        if (apiResponse.getHttpCode() == 200) { //ok, record
            Document document = new Builder().build(apiResponse.getBody(), null);
            return document;
        } else {
            try {//error
                Document document = new Builder().build(apiResponse.getBody(), null);
                CzidloApiError apiError = xmlTools.parseErrorMessage(document);
                throw new CzidloApiErrorException(url, apiResponse.getHttpCode(), apiError);
            } catch (ParsingException | IOException e) { //error but failed to parse body
                throw new CzidloApiErrorException(url, apiResponse.getHttpCode(), null);
            }
        }
    }

    /**
     * @param urnnbn
     * @return
     * @throws CzidloApiErrorException in case of API error response (getting URN:NBN details)
     * @throws ParsingException        in case of parsing xml from API response body (getting URN:NBN details)
     * @throws IOException             in case of network error (getting URN:NBN details)
     */
    public UrnnbnStatus getUrnnbnStatus(String urnnbn) throws ParsingException, CzidloApiErrorException, IOException {
        Document doc = getUrnnbnDetails(urnnbn);
        System.err.println(doc.toXML());
        Nodes statusNode = doc.query("/r:response/r:urnNbn/r:status", CONTEXT);
        return UrnnbnStatus.valueOf(statusNode.get(0).getValue());
    }

    /**
     * @param urnNbn
     * @return digital-instances record for digital-document identified by URN:NBN or null if no such digital-document exists
     * @throws CzidloApiErrorException in case of API error response
     * @throws ParsingException        in case of parsing xml from API response body
     * @throws IOException             in case of network error
     */
    public Document getDigitalInstancesByUrnnbn(String urnNbn) throws CzidloApiErrorException, ParsingException, IOException {
        String url = baseUrl + "resolver/" + urnNbn + "/digitalInstances?format=xml";
        ApiResponse apiResponse = httpConnector.httpGet(url, credentials, ignoreInvalidCertificate);
        if (apiResponse.getHttpCode() == 200) { //ok, record
            Document document = new Builder().build(apiResponse.getBody(), null);
            return document;
        } else {
            try {//error
                Document document = new Builder().build(apiResponse.getBody(), null);
                CzidloApiError apiError = xmlTools.parseErrorMessage(document);
                if (apiResponse.getHttpCode() == 404) { //document not found
                    return null;
                } else { //other error
                    throw new CzidloApiErrorException(url, apiResponse.getHttpCode(), apiError);
                }
            } catch (ParsingException | IOException e) { //error but failed to parse body
                throw new CzidloApiErrorException(url, apiResponse.getHttpCode(), null);
            }
        }
    }

    /**
     * @param urnnbn
     * @return list of identifiers of all active digital-instances of digital-document identified by URN:NBN or empty list if no such digital-document exists
     * @throws CzidloApiErrorException in case of API error response (getting digital-instances record)
     * @throws ParsingException        in case of parsing xml from API response body (getting digital-instances record)
     * @throws IOException             in case of network error (getting digital-instances record)
     */
    public List<Long> getActiveDigitalInstancesIdList(String urnnbn) throws IOException, ParsingException, CzidloApiErrorException {
        Document doc = getDigitalInstancesByUrnnbn(urnnbn);
        if (doc == null) {
            return Collections.emptyList();
        } else {
            Nodes idNodes = doc.query("/r:response/r:digitalInstances/r:digitalInstance[@active='true']/@id", CONTEXT);
            List<Long> list = new ArrayList<>(idNodes.size());
            for (int i = 0; i < idNodes.size(); i++) {
                list.add(Long.valueOf(idNodes.get(i).getValue()));
            }
            return list;
        }
    }


    /**
     * @param urnnbn
     * @param libraryId
     * @return active digital instance of digital-document identifier by URN:NBN or null if no such digital-document od digital-instance exist
     * @throws CzidloApiErrorException in case of API error response (getting digital-instances record)
     * @throws ParsingException        in case of parsing xml from API response body (getting digital-instances record)
     * @throws IOException             in case of network error (getting digital-instances record)
     */
    public DigitalInstance getActiveDigitalInstanceByUrnnbnAndLibraryId(String urnnbn, Long libraryId) throws IOException, ParsingException, CzidloApiErrorException {
        Document doc = getDigitalInstancesByUrnnbn(urnnbn);
        if (doc == null) {
            return null;
        } else {
            List<DigitalInstance> digitalInstances = DiBuilder.buildDisFromGetDigitalInstancesByUrnNbn(doc);
            for (DigitalInstance di : digitalInstances) {
                if (di.isActive() && di.getLibraryId().equals(libraryId)) {
                    return di;
                }
            }
            return null;
        }
    }

    /**
     * @param digDocRegistrationData
     * @param registrarCode
     * @return URN:NBN assigned/confirmed to now registered digital document
     * @throws CzidloApiErrorException in case of API error response
     * @throws ParsingException        in case of parsing xml from API response body
     * @throws IOException             in case of network error
     */
    public String registerDigitalDocument(Document digDocRegistrationData, String registrarCode) throws IOException, ParsingException, CzidloApiErrorException {
        String url = baseUrl + "registrars/" + registrarCode + "/digitalDocuments"; //+"?format=xml";
        ApiResponse apiResponse = httpConnector.httpPost(url, digDocRegistrationData.toXML(), credentials, ignoreInvalidCertificate);
        if (apiResponse.getHttpCode() == 201) {
            Document responseDoc = new Builder().build(apiResponse.getBody(), null);
            return responseDoc.query("/r:response/r:urnNbn/r:value", CONTEXT).get(0).getValue();
        } else {
            try {//error
                Document errorDoc = new Builder().build(apiResponse.getBody(), null);
                CzidloApiError apiError = xmlTools.parseErrorMessage(errorDoc);
                throw new CzidloApiErrorException(url, apiResponse.getHttpCode(), apiError);
            } catch (ParsingException | IOException e) { //error but failed to parse body
                throw new CzidloApiErrorException(url, apiResponse.getHttpCode(), null);
            }
        }
    }

    /**
     * @param diImportData
     * @param urnnbn
     * @throws CzidloApiErrorException in case of API error response
     * @throws IOException             in case of network error
     */
    public void importDigitalInstance(Document diImportData, String urnnbn) throws CzidloApiErrorException, IOException {
        String url = baseUrl + "resolver/" + urnnbn + "/digitalInstances";
        ApiResponse apiResponse = httpConnector.httpPost(url, diImportData.toXML(), credentials, ignoreInvalidCertificate);
        if (apiResponse.getHttpCode() == 201) {
            //ok, imported
        } else {
            try {//error
                Document errorDoc = new Builder().build(apiResponse.getBody(), null);
                CzidloApiError apiError = xmlTools.parseErrorMessage(errorDoc);
                throw new CzidloApiErrorException(url, apiResponse.getHttpCode(), apiError);
            } catch (ParsingException | IOException e) { //error but failed to parse body
                throw new CzidloApiErrorException(url, apiResponse.getHttpCode(), null);
            }
        }
    }

    /**
     * @param urnnbn
     * @param idValue
     * @param idType
     * @throws CzidloApiErrorException in case of API error response
     * @throws IOException             in case of network error
     */
    public void putRegistrarScopeIdentifier(String urnnbn, String idType, String idValue) throws CzidloApiErrorException, IOException {
        String url = baseUrl + "resolver/" + urnnbn + "/registrarScopeIdentifiers/" + idType;
        ApiResponse apiResponse = httpConnector.httpPut(url, idValue, credentials, ignoreInvalidCertificate);
        if (apiResponse.getHttpCode() == 200 || apiResponse.getHttpCode() == 201) {
            //ok, set/updated
        } else {
            try {//error
                Document errorDoc = new Builder().build(apiResponse.getBody(), null);
                CzidloApiError apiError = xmlTools.parseErrorMessage(errorDoc);
                throw new CzidloApiErrorException(url, apiResponse.getHttpCode(), apiError);
            } catch (ParsingException | IOException e) { //error but failed to parse body
                throw new CzidloApiErrorException(url, apiResponse.getHttpCode(), null);
            }
        }
    }

    /**
     * @param digitalInstanceId
     * @throws CzidloApiErrorException in case of API error response
     * @throws IOException             in case of network error
     */
    public void deactivateDigitalInstance(Long digitalInstanceId) throws CzidloApiErrorException, IOException {
        String url = baseUrl + "digitalInstances/id/" + digitalInstanceId;
        ApiResponse apiResponse = httpConnector.httpDelete(url, credentials, ignoreInvalidCertificate);
        if (apiResponse.getHttpCode() == 200) {
            //ok, deactivated
        } else {
            try {//error
                Document errorDoc = new Builder().build(apiResponse.getBody(), null);
                CzidloApiError apiError = xmlTools.parseErrorMessage(errorDoc);
                throw new CzidloApiErrorException(url, apiResponse.getHttpCode(), apiError);
            } catch (ParsingException | IOException e) { //error but failed to parse body
                throw new CzidloApiErrorException(url, apiResponse.getHttpCode(), null);
            }
        }
    }

}
