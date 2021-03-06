/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter;

import cz.nkp.urnnbn.api_client.v5.CzidloApiConnector;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;

import java.util.logging.Logger;

/**
 * @author Jan Rychtář
 * @author Martin Řehánek
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
        oaiAdapterScopeElement.addAttribute(new Attribute("type", OaiAdapter.REGISTRAR_SCOPE_ID_TYPE));
        oaiAdapterScopeElement.appendChild(oaiIdentifier);
        registrarScopeIdentifiersElement.appendChild(oaiAdapterScopeElement);
        return ddRegistrationData;
    }

    public String getUrnnbnFromDocument() {
        Nodes nodes = ddRegistrationData.query("/r:import/r:digitalDocument/r:urnNbn/r:value", CzidloApiConnector.CONTEXT);
        if (nodes.size() == 1) {
            return nodes.get(0).getValue();
        }
        return null;
    }

}
