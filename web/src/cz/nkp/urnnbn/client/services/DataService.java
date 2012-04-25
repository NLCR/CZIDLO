package cz.nkp.urnnbn.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import cz.nkp.urnnbn.shared.dto.DigitalDocumentDTO;
import cz.nkp.urnnbn.shared.dto.TechnicalMetadataDTO;
import cz.nkp.urnnbn.shared.dto.ie.IntelectualEntityDTO;
import cz.nkp.urnnbn.shared.exceptions.ServerException;

@RemoteServiceRelativePath("data")
public interface DataService extends RemoteService {

	void updateDigitalDocument(DigitalDocumentDTO doc, TechnicalMetadataDTO technical) throws ServerException;

	void updateIntelectualEntity(IntelectualEntityDTO entity) throws ServerException;

}
