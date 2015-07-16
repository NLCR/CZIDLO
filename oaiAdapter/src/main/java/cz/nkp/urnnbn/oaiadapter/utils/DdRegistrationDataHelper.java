/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter.utils;

import java.util.logging.Logger;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import cz.nkp.urnnbn.oaiadapter.OaiAdapter;
import cz.nkp.urnnbn.oaiadapter.czidlo.CzidloApiConnector;

/**
 *
 * @author hanis
 */
public class DdRegistrationDataHelper {

	private static final Logger logger = Logger.getLogger(DdRegistrationDataHelper.class.getName());

	private final Document ddRegistrationData;

	public DdRegistrationDataHelper(Document ddRegistrationData) {
		this.ddRegistrationData = ddRegistrationData;
	}

	public Document putRegistrarScopeIdentifier(String oaiIdentifier) {
		Element root = ddRegistrationData.getRootElement();
		Element digitalDocumentElement = root.getFirstChildElement("digitalDocument", CzidloApiConnector.CZIDLO_NAMESPACE);
		if (digitalDocumentElement == null) {
			digitalDocumentElement = new Element("r:digitalDocument", CzidloApiConnector.CZIDLO_NAMESPACE);
			root.appendChild(digitalDocumentElement);
		}
		Element registrarScopeIdentifiersElement = digitalDocumentElement.getFirstChildElement("registrarScopeIdentifiers",
				CzidloApiConnector.CZIDLO_NAMESPACE);
		if (registrarScopeIdentifiersElement == null) {
			registrarScopeIdentifiersElement = new Element("r:registrarScopeIdentifiers", CzidloApiConnector.CZIDLO_NAMESPACE);
			int archiverIdPosition = digitalDocumentElement.indexOf(digitalDocumentElement.getFirstChildElement("archiverId",
					CzidloApiConnector.CZIDLO_NAMESPACE));
			int urnnbnPosition = digitalDocumentElement.indexOf(digitalDocumentElement.getFirstChildElement("urnNbn",
					CzidloApiConnector.CZIDLO_NAMESPACE));
			int position = 0;
			if (urnnbnPosition != -1) {
				position = urnnbnPosition + 1;
			} else if (archiverIdPosition != -1) {
				position = archiverIdPosition + 1;
			}
			digitalDocumentElement.insertChild(registrarScopeIdentifiersElement, position);
		}
		Element oaiAdapterScopeElement = new Element("r:id", CzidloApiConnector.CZIDLO_NAMESPACE);
		oaiAdapterScopeElement.addAttribute(new Attribute("type", OaiAdapter.REGISTAR_SCOPE_ID));
		oaiAdapterScopeElement.appendChild(oaiIdentifier);
		registrarScopeIdentifiersElement.appendChild(oaiAdapterScopeElement);
		return ddRegistrationData;
	}

	public String getUrnnbnFromDocument() {
		Nodes nodes = ddRegistrationData.query("/r:import/r:digitalDocument/r:urnNbn/r:value", CzidloApiConnector.CONTEXT);
		// System.out.println(document.toXML().toString());
		// System.out.println(nodes.size());
		// System.out.println(ResolverConnector.CONTEXT);
		if (nodes.size() == 1) {
			return nodes.get(0).getValue();
		}
		return null;
	}

	// public static void main(String[] args) {
	// //File file = new File("/home/hanis/prace/resolver/oai/parser-test/t.xml");
	// File file = new File("/home/hanis/prace/resolver/oai/parser-test/docs/digitalDocument2.xml");
	// //File file = new File("/home/hanis/prace/resolver/oai/parser-test/monograph.xml");
	//
	// Builder builder = new Builder();
	// Document doc = null;
	// try {
	// doc = builder.build(file);
	// } catch (ParsingException ex) {
	// logger.log(Level.SEVERE, null, ex);
	// } catch (IOException ex) {
	// logger.log(Level.SEVERE, null, ex);
	// }
	// Refiner.refineDocument(doc);
	// ImportDocumentHandler.putRegistrarScopeIdentifier(doc, "oai:blablabla");
	// //String urnnbn = ImportDocumentHandler.getUrnnbnFromDocument(doc);
	// //System.out.println(urnnbn);
	// //System.out.println(doc.toXML());
	// }
}
