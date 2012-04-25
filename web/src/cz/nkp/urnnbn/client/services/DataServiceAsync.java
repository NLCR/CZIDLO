package cz.nkp.urnnbn.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import cz.nkp.urnnbn.shared.dto.DigitalDocumentDTO;
import cz.nkp.urnnbn.shared.dto.TechnicalMetadataDTO;
import cz.nkp.urnnbn.shared.dto.ie.IntelectualEntityDTO;

public interface DataServiceAsync {

	void updateDigitalDocument(DigitalDocumentDTO doc, TechnicalMetadataDTO technical, AsyncCallback<Void> callback);

	void updateIntelectualEntity(IntelectualEntityDTO entity, AsyncCallback<Void> callback);

}
