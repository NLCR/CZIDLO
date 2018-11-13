package cz.nkp.urnnbn.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import cz.nkp.urnnbn.shared.dto.*;
import cz.nkp.urnnbn.shared.dto.ie.IntelectualEntityDTO;

import java.util.ArrayList;

public interface DataServiceAsync {

    void updateDigitalDocument(DigitalDocumentDTO doc, TechnicalMetadataDTO technical, AsyncCallback<Void> callback);

    void updateIntelectualEntity(IntelectualEntityDTO entity, AsyncCallback<Void> callback);

    void saveRecord(IntelectualEntityDTO intEnt, DigitalDocumentDTO digDoc, UrnNbnDTO urnNbn,
                    ArrayList<RegistrarScopeIdDTO> registrarScopeIdentifiers, AsyncCallback<UrnNbnDTO> callback);

    void addRegistrarScopeIdentifier(RegistrarScopeIdDTO rsId, AsyncCallback<RegistrarScopeIdDTO> callback);

    void removeRegistrarScopeIdentifier(RegistrarScopeIdDTO rsId, AsyncCallback<Void> callback);

    void saveDigitalInstance(UrnNbnDTO urn, DigitalInstanceDTO instance, AsyncCallback<DigitalInstanceDTO> callback);

    void deactivateDigitalInstance(DigitalInstanceDTO instance, AsyncCallback<Void> callback);

    void deactivateUrnNbn(UrnNbnDTO urnNbn, AsyncCallback<Void> callback);

    void updateDigitalInstance(UrnNbnDTO urnNbn, DigitalInstanceDTO instance, AsyncCallback<Void> callback);

}
