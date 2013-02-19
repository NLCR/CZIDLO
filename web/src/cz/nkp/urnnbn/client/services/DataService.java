package cz.nkp.urnnbn.client.services;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import cz.nkp.urnnbn.shared.dto.DigitalDocumentDTO;
import cz.nkp.urnnbn.shared.dto.DigitalInstanceDTO;
import cz.nkp.urnnbn.shared.dto.RegistrarScopeIdDTO;
import cz.nkp.urnnbn.shared.dto.TechnicalMetadataDTO;
import cz.nkp.urnnbn.shared.dto.UrnNbnDTO;
import cz.nkp.urnnbn.shared.dto.ie.IntelectualEntityDTO;
import cz.nkp.urnnbn.shared.exceptions.ServerException;

@RemoteServiceRelativePath("data")
public interface DataService extends RemoteService {

	void updateDigitalDocument(DigitalDocumentDTO doc, TechnicalMetadataDTO technical) throws ServerException;

	void updateIntelectualEntity(IntelectualEntityDTO entity) throws ServerException;

	UrnNbnDTO saveRecord(IntelectualEntityDTO intEnt, DigitalDocumentDTO digDoc, UrnNbnDTO urnNbn,
			ArrayList<RegistrarScopeIdDTO> registrarScopeIdentifiers) throws ServerException;

	DigitalInstanceDTO saveDigitalInstance(DigitalInstanceDTO instance, UrnNbnDTO urn) throws ServerException;

	void deactivateDigitalInstance(DigitalInstanceDTO instance) throws ServerException;
	
	void deactivateUrnNbn(UrnNbnDTO urnNbn) throws ServerException;
	
}
