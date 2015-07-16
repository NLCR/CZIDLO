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
 * 
 * @author hanis
 */
public class CzidloApiConnector {

	public static final String ERROR_CODE_REGISTAR = "UNKNOWN_REGISTRAR";
	public static final String ERROR_CODE_DOCUMENT = "UNKNOWN_DIGITAL_DOCUMENT";
	public static final String CZIDLO_NAMESPACE = "http://resolver.nkp.cz/v3/";
	public static final XPathContext CONTEXT = new XPathContext("r", CZIDLO_NAMESPACE);
	public final String czidloApiUrl;
	private final Credentials credentials;

	public CzidloApiConnector(String czidloApiUrl, Credentials credentials) {
		this.czidloApiUrl = czidloApiUrl + "/v3/";
		this.credentials = credentials;
	}

	public String getCzidloApiUrl() {
		return czidloApiUrl;
	}

	private String getDigitalDocumentByRegistrarScopeIdUrl(String registrar, String identifier, String registarScopeId) {
		String url = "https://" + czidloApiUrl + "registrars/" + registrar + "/digitalDocuments/registrarScopeIdentifier/"
				+ registarScopeId + "/" + identifier + "?format=xml&action=show";
		return url;
	}

	private String getRegistrarUrl(String registrarCode) {
		String url = "https://" + czidloApiUrl + "registrars/" + registrarCode;
		return url;
	}

	private String getDigDocRegistrationUrl(String registrarCode) {
		String url = "https://" + czidloApiUrl + "registrars/" + registrarCode + "/digitalDocuments";
		return url;
	}

	private String getUrnnbnReservationUrl(String registrarCode, int size) {
		String url = "https://" + czidloApiUrl + "registrars/" + registrarCode + "/urnNbnReservations?size=" + size;
		return url;
	}

	private String getUrnnbnStatusUrl(String urnnbn) {
		String url = "https://" + czidloApiUrl + "urnnbn/" + urnnbn;
		return url;
	}

	private String getDigitalInsatancesUrl(String urnnbn) {
		String url = "https://" + czidloApiUrl + "resolver/" + urnnbn + "/digitalInstances";
		return url;
	}

	private String getRegistrarScopeIdentifierUrl(String urnnbn, String registrarScopeId) {
		String url = "https://" + czidloApiUrl + "resolver/" + urnnbn + "/registrarScopeIdentifiers/" + registrarScopeId;
		return url;
	}

	private String getDigitalInstanceUrl(String diId) {
		String url = "https://" + czidloApiUrl + "digitalInstances/id/" + diId;
		return url;
	}

	public String getUrnnbnByTriplet(String registrar, String identifier, String registarScopeId) throws CzidloConnectionException {
		String url = getDigitalDocumentByRegistrarScopeIdUrl(registrar, registarScopeId, identifier);
		Document document;
		try {
			document = XmlTools.getDocument(url, credentials, true);
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
		List<String> list = new ArrayList<String>();
		String url = getDigitalInsatancesUrl(urnnbn);
		// System.out.println("getDigitalInstancesIdList " + url);
		Document document = XmlTools.getDocument(url, credentials, false);
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
		// List<String> list = new ArrayList<String>();
		String url = getDigitalInstanceUrl(id);
		// System.out.println("getDigitailInstanceById " + url);
		Document document = XmlTools.getDocument(url, credentials, false);
		return document;
	}

	public UrnnbnStatus getUrnnbnStatus(String urnnbn) {
		String url = getUrnnbnStatusUrl(urnnbn);
		Document document = null;
		try {
			document = XmlTools.getDocument(url, credentials, true);
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
		String url = getRegistrarUrl(registrarCode);
		Document document = null;
		try {
			document = XmlTools.getDocument(url, credentials, true);
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

	public List<String> reserveUrnnbnBundle(String registarCode, int bundleSize) throws IOException, CzidloConnectionException,
			ParsingException {
		List<String> urnnbnList = new ArrayList<String>();
		String url = getUrnnbnReservationUrl(registarCode, bundleSize);
		HttpsURLConnection connection = XmlTools.getAuthConnection(url, credentials, HttpMethod.POST, true);
		int responseCode = connection.getResponseCode();
		if (responseCode != 201) { // TODO pokud ok, pak vzdy 200??
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

	public String registerDigitalDocument(Document digDocRegistrationData, String registarCode) throws IOException, ParsingException,
			CzidloConnectionException {
		String url = getDigDocRegistrationUrl(registarCode);
		HttpsURLConnection connection = XmlTools.getAuthConnection(url, credentials, HttpMethod.POST, true);
		OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
		wr.write(digDocRegistrationData.toXML());
		wr.flush();
		wr.close();
		int responseCode = connection.getResponseCode();
		if (responseCode != 200) { // TODO pokud ok, pak vzdy 200??
			Builder builder = new Builder();
			String message = getErrorMessage(builder.build(connection.getErrorStream()));
			if (message == null) {
				message = "Importing record document: response code expected 200, found " + responseCode;
			}
			throw new CzidloConnectionException(message);
		}
		InputStream is = connection.getInputStream();
		Builder builder = new Builder();
		Document responseDocument = builder.build(is);
		String urnnbn = getAllocatedURNNBN(responseDocument);
		return urnnbn;
	}

	public void importDigitalInstance(Document diImportData, String urnnbn) throws IOException, ParsingException, CzidloConnectionException {
		String url = getDigitalInsatancesUrl(urnnbn);
		HttpsURLConnection connection = XmlTools.getAuthConnection(url, credentials, HttpMethod.POST, true);
		OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
		wr.write(diImportData.toXML());
		wr.flush();
		wr.close();
		int responseCode = connection.getResponseCode();
		if (responseCode != 201) { // TODO pokud ok, pak vzdy 201??
			throw new CzidloConnectionException("Putting digital instance: response code expected 201, found " + responseCode);
		}
	}

	public void putRegistrarScopeIdentifier(String urnnbn, String documentId, String registrarScopeId) throws IOException,
			CzidloConnectionException {
		String url = getRegistrarScopeIdentifierUrl(urnnbn, registrarScopeId);
		HttpsURLConnection connection = XmlTools.getAuthConnection(url, credentials, HttpMethod.PUT, true);
		OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
		wr.write(documentId);
		wr.flush();
		wr.close();
		int responseCode = connection.getResponseCode();
		if (responseCode != 201) { // TODO pokud ok, pak vzdy 201??
			throw new CzidloConnectionException("Putting registrar scope identifier: response code expected 201, found " + responseCode);
		}

	}

	public void removeDigitalInstance(String id) throws CzidloConnectionException {
		try {
			String url = getDigitalInsatancesUrl(id);
			HttpsURLConnection connection = XmlTools.getAuthConnection(url, credentials, HttpMethod.DELETE, false);
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