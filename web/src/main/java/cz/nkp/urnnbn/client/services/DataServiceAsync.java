package cz.nkp.urnnbn.client.services;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

import cz.nkp.urnnbn.shared.dto.DigitalDocumentDTO;
import cz.nkp.urnnbn.shared.dto.DigitalInstanceDTO;
import cz.nkp.urnnbn.shared.dto.RegistrarScopeIdDTO;
import cz.nkp.urnnbn.shared.dto.TechnicalMetadataDTO;
import cz.nkp.urnnbn.shared.dto.UrnNbnDTO;
import cz.nkp.urnnbn.shared.dto.ie.IntelectualEntityDTO;

public interface DataServiceAsync {

	void updateDigitalDocument(DigitalDocumentDTO doc, TechnicalMetadataDTO technical, AsyncCallback<Void> callback);

	void updateIntelectualEntity(IntelectualEntityDTO entity, AsyncCallback<Void> callback);

	void saveRecord(IntelectualEntityDTO intEnt, DigitalDocumentDTO digDoc, UrnNbnDTO urnNbn,
			ArrayList<RegistrarScopeIdDTO> registrarScopeIdentifiers, AsyncCallback<UrnNbnDTO> callback);

	void saveDigitalInstance(DigitalInstanceDTO instance, UrnNbnDTO urn, AsyncCallback<DigitalInstanceDTO> callback);

	void deactivateDigitalInstance(DigitalInstanceDTO instance, AsyncCallback<Void> callback);

	void deactivateUrnNbn(UrnNbnDTO urnNbn,  AsyncCallback<Void> callback);
	
	void updateDigitalInstance(UrnNbnDTO urnNbn, DigitalInstanceDTO instance, AsyncCallback<Void> callback);
	
}
