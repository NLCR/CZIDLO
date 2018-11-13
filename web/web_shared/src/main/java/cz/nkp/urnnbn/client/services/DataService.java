package cz.nkp.urnnbn.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import cz.nkp.urnnbn.shared.dto.*;
import cz.nkp.urnnbn.shared.dto.ie.IntelectualEntityDTO;
import cz.nkp.urnnbn.shared.exceptions.ServerException;

import java.util.ArrayList;

@RemoteServiceRelativePath("data")
public interface DataService extends RemoteService {

    void updateDigitalDocument(DigitalDocumentDTO doc, TechnicalMetadataDTO technical) throws ServerException;

    void updateIntelectualEntity(IntelectualEntityDTO entity) throws ServerException;

    UrnNbnDTO saveRecord(IntelectualEntityDTO intEnt, DigitalDocumentDTO digDoc, UrnNbnDTO urnNbn,
                         ArrayList<RegistrarScopeIdDTO> registrarScopeIdentifiers) throws ServerException;

    RegistrarScopeIdDTO addRegistrarScopeIdentifier(RegistrarScopeIdDTO rsId) throws ServerException;

    void removeRegistrarScopeIdentifier(RegistrarScopeIdDTO rsId) throws ServerException;

    DigitalInstanceDTO saveDigitalInstance(UrnNbnDTO urn, DigitalInstanceDTO instance) throws ServerException;

    void deactivateDigitalInstance(DigitalInstanceDTO instance) throws ServerException;

    void deactivateUrnNbn(UrnNbnDTO urnNbn) throws ServerException;

    void updateDigitalInstance(UrnNbnDTO urnNbn, DigitalInstanceDTO instance) throws ServerException;

}
