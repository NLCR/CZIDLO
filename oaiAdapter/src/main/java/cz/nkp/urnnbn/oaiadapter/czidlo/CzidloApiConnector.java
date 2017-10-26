/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter.czidlo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import nu.xom.XPathContext;
import cz.nkp.urnnbn.core.UrnNbnRegistrationMode;
import cz.nkp.urnnbn.oaiadapter.DigitalInstance;
import cz.nkp.urnnbn.oaiadapter.utils.DiApiResponseDocHelper;
import cz.nkp.urnnbn.oaiadapter.utils.XmlTools;

/**
 * @author hanis, Martin Řehánek
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

    public String getUrnnbnByRegistrarScopeId(String registrarCode, String idType, String idValue) throws CzidloConnectionException {
        String url = baseUrl
                + "registrars/" + registrarCode
                + "/digitalDocuments/registrarScopeIdentifier/" + idType + "/" + idValue
                + "?format=xml&digitalInstances=true";
        Document document;
        try {
            document = XmlTools.getDocumentAccept404Data(url, credentials, ignoreInvalidCertificate);
        } catch (IOException ex) {
            throw new CzidloConnectionException("IOException occured while getting urnnbn by OAI_ADAPTER ID");
        } catch (ParsingException ex) {
            throw new CzidloConnectionException("ParsingException occured while getting urnnbn by OAI_ADAPTER ID");
        }
        Nodes nodes = document.query("//r:digitalDocument/r:urnNbn/r:value", CONTEXT);
        if (nodes.size() > 0) {
            return nodes.get(0).getValue();
        }
        return null;
    }

    // public static boolean isDocumentAlreadyImported(String registrar, String identifier, String
    // registarScopeId)
    // throws IOException, ParsingException {
    // String url = getDigitalDocumentUrl(registrar, identifier, registarScopeId);
    // Document document = XmlTools.getDocument(url, true);
    // Element rootElement = document.getRootElement();
    // if ("digitalDocument".equals(rootElement.getLocalName())) {
    // return true;
    // } else if ("error".equals(rootElement.getLocalName())) {
    // Nodes codeNode = rootElement.query("//r:code", CONTEXT);
    // if (codeNode.size() > 0) {
    // String code = codeNode.get(0).getValue();
    // //System.out.println("code:" + code);
    // if (!(ERROR_CODE_DOCUMENT.equals(code) || ERROR_CODE_REGISTAR.equals(code))) {
    // //TODO spatne error code - neco je spatne ...staci kontrolovat jen tyto dva kody?
    // throw new RuntimeException();
    // } else {
    // return false;
    // }
    // } else {
    // //TODO spatna struktura dokumentu
    // throw new RuntimeException();
    // }
    // } else {
    // //TODO spatna struktura dokumentu
    // throw new RuntimeException();
    // }
    // }
    public List<String> getDigitalInstancesIdList(String urnnbn) throws IOException, ParsingException {
        List<String> list = new ArrayList<>();
        String url = baseUrl + "resolver/" + urnnbn + "/digitalInstances?format=xml";
        // System.out.println("getDigitalInstancesIdList " + url);
        Document document = XmlTools.getDocument(url, credentials, ignoreInvalidCertificate);
        Element rootElement = document.getRootElement();
        Nodes idNodes = rootElement.query("//r:digitalInstance[@active='true']/@id", CONTEXT);
        // Nodes idNodes = rootElement.query("//r:digitalInstance/r:id", CONTEXT);
        for (int i = 0; i < idNodes.size(); i++) {
            list.add(idNodes.get(i).getValue());
        }
        return list;
    }

    public String getErrorMessage(Document document) throws IOException, ParsingException {
        Element rootElement = document.getRootElement();
        Nodes codeNodes = rootElement.query("//r:error/r:code", CONTEXT);
        String code = "";
        if (codeNodes.size() == 1) {
            code = codeNodes.get(0).getValue();
        }
        Nodes messageNodes = rootElement.query("//r:error/r:message", CONTEXT);
        String message = "";
        if (messageNodes.size() == 1) {
            message = messageNodes.get(0).getValue();
        }
        if (code.isEmpty() && message.isEmpty()) {
            return null;
        }
        return code + ": " + message;

    }

    public Document getDigitailInstanceById(String id) throws IOException, ParsingException {
        String url = baseUrl + "digitalInstances/id/" + id + "?format=xml";
        Document document = XmlTools.getDocument(url, credentials, ignoreInvalidCertificate);
        return document;
    }

    public UrnnbnStatus getUrnnbnStatus(String urnnbn) {
        String url = baseUrl + "urnnbn/" + urnnbn + "?format=xml";
        Document document = null;
        try {
            document = XmlTools.getDocumentAccept404Data(url, credentials, ignoreInvalidCertificate);
        } catch (Exception ex) {
            return UrnnbnStatus.UNDEFINED;
        }
        Element rootElement = document.getRootElement();
        Nodes statusNode = rootElement.query("//r:status", CONTEXT);
        if (statusNode.size() > 0) {
            String status = statusNode.get(0).getValue();
            return UrnnbnStatus.valueOf(status);
        }
        return UrnnbnStatus.UNDEFINED;
    }

    public boolean checkRegistrarMode(String registrarCode, UrnNbnRegistrationMode mode) throws CzidloConnectionException {
        String url = baseUrl + "registrars/" + registrarCode + "?format=xml";
        Document document = null;
        try {
            document = XmlTools.getDocumentAccept404Data(url, credentials, ignoreInvalidCertificate);
        } catch (Exception ex) {
            throw new CzidloConnectionException(ex);
        }
        Element rootElement = document.getRootElement();
        String modeString = "";
        switch (mode) {
            case BY_REGISTRAR:
                modeString = "BY_REGISTRAR";
                break;
            case BY_RESOLVER:
                modeString = "BY_RESOLVER";
                break;
            case BY_RESERVATION:
                modeString = "BY_RESERVATION";
                break;
        }
        Nodes modeEnabledNode = rootElement.query("//r:registrationModes/r:mode[@name='" + modeString + "']/@enabled", CONTEXT);
        if (modeEnabledNode.size() > 0) {
            return "true".equals(modeEnabledNode.get(0).getValue());
        }
        return false;
    }

    public List<String> reserveUrnnbnBundle(String registrarCode, int bundleSize) throws IOException, CzidloConnectionException, ParsingException {
        List<String> urnnbnList = new ArrayList<>();
        String url = baseUrl + "registrars/" + registrarCode + "/urnNbnReservations?size=" + bundleSize;// + "&format=xml";
        HttpsURLConnection connection = XmlTools.getWritableAuthConnection(url, credentials, HttpMethod.POST, ignoreInvalidCertificate);
        int responseCode = connection.getResponseCode();
        if (responseCode != 201) {
            throw new CzidloConnectionException("URNNBN reservation: response code expected 201, found " + responseCode);
        }
        InputStream is = connection.getInputStream();
        Builder builder = new Builder();
        Document responseDocument = builder.build(is);
        Element rootElement = responseDocument.getRootElement();
        Nodes nodes = rootElement.query("//r:urnNbn", CONTEXT);
        for (int i = 0; i < nodes.size(); i++) {
            urnnbnList.add(nodes.get(i).getValue());
        }
        return urnnbnList;

    }

    public String registerDigitalDocument(Document digDocRegistrationData, String registrarCode) throws IOException, ParsingException,
            CzidloConnectionException {
        String url = baseUrl + "registrars/" + registrarCode + "/digitalDocuments"; //+"?format=xml";
        HttpsURLConnection connection = XmlTools.getWritableAuthConnection(url, credentials, HttpMethod.POST, ignoreInvalidCertificate);
        OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
        wr.write(digDocRegistrationData.toXML());
        wr.flush();
        wr.close();
        int responseCode = connection.getResponseCode();
        if (responseCode != 201) {
            // see https://github.com/NLCR/CZIDLO/issues/111
            if (responseCode != 200) {
                logger.warning("Unexpected response code: " + responseCode);
                Builder builder = new Builder();
                InputStream in = connection.getErrorStream();
                if (in != null) {
                    String message = getErrorMessage(builder.build(in));
                    if (message == null) {
                        message = "Registering digital document: response code expected 201, found " + responseCode;
                    }
                    throw new CzidloConnectionException(message);
                } else {
                    throw new CzidloConnectionException("unexpected response code: " + responseCode);
                }
            } else {
                logger.warning("urn:nbn registration response code should be 201, not 200");
            }
        }
        InputStream is = connection.getInputStream();
        Builder builder = new Builder();
        Document responseDocument = builder.build(is);
        String urnnbn = getAllocatedURNNBN(responseDocument);
        return urnnbn;
    }

    public void importDigitalInstance(Document diImportData, String urnnbn) throws IOException, ParsingException, CzidloConnectionException {
        String url = baseUrl + "resolver/" + urnnbn + "/digitalInstances";
        HttpsURLConnection connection = XmlTools.getWritableAuthConnection(url, credentials, HttpMethod.POST, ignoreInvalidCertificate);
        OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
        wr.write(diImportData.toXML());
        wr.flush();
        wr.close();
        int responseCode = connection.getResponseCode();
        if (responseCode != 201) { // TODO pokud ok, pak vzdy 201??
            throw new CzidloConnectionException("Putting digital instance: response code expected 201, found " + responseCode);
        }
    }

    // TODO: why not used??
    public void putRegistrarScopeIdentifier(String urnnbn, String idValue, String idType) throws IOException, CzidloConnectionException {
        String url = baseUrl + "resolver/" + urnnbn + "/registrarScopeIdentifiers/" + idType;
        HttpsURLConnection connection = XmlTools.getWritableAuthConnection(url, credentials, HttpMethod.PUT, ignoreInvalidCertificate);
        OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
        wr.write(idValue);
        wr.flush();
        wr.close();
        int responseCode = connection.getResponseCode();
        if (responseCode != 201) {
            // TODO: bude tahle metoda delat jen vkladani, nebo i aktualizaci?
            // 201 - aktualizace, 200 - vlozeni nove hodnoty
            throw new CzidloConnectionException("Putting registrar scope identifier: response code expected 201, found " + responseCode);
        }
    }

    public void deactivateDigitalInstance(String id) throws CzidloConnectionException {
        try {
            String url = baseUrl + "digitalInstances/id/" + id;
            HttpsURLConnection connection = XmlTools.getAuthConnection(url, credentials, HttpMethod.DELETE, ignoreInvalidCertificate);
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                throw new CzidloConnectionException("Removing digital instance: response code expected 200, found " + responseCode);
            }
        } catch (IOException ex) {
            throw new CzidloConnectionException("IOException occured while removing DI with id: " + id);
        }
    }

    private String writeInputStream(InputStream is) {
        Builder builder = new Builder();
        try {
            Document responseDocument = builder.build(is);
            return "RD:" + responseDocument.toXML();
        } catch (ValidityException ex) {
            return "V:" + ex.getMessage();
        } catch (ParsingException ex) {
            return "P:" + ex.getMessage();
        } catch (IOException ex) {
            return "IO:" + ex.getMessage();
        }
    }

    public String getAllocatedURNNBN(Document document) {
        Element rootElement = document.getRootElement();
        Nodes node = rootElement.query("//r:value", CONTEXT);
        if (node.size() < 1) {
            // TODO spatna struktura dokumentu
            throw new RuntimeException();
        }
        return node.get(0).getValue();
    }

    public DigitalInstance getDigitalInstanceByLibraryId(String urnnbn, DigitalInstance newDi) throws IOException, ParsingException {
        List<String> idList = getDigitalInstancesIdList(urnnbn);
        for (String id : idList) {
            Document diDoc = getDigitailInstanceById(id);
            DigitalInstance oldDi = new DiApiResponseDocHelper(diDoc).buildDi();
            // System.out.println("odi:" + oldDi);
            if (oldDi.getDigitalLibraryId().equals(newDi.getDigitalLibraryId())) {
                return oldDi;
            }
        }
        return null;
    }
}
