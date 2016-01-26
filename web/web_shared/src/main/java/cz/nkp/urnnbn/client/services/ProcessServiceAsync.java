package cz.nkp.urnnbn.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import cz.nkp.urnnbn.shared.dto.process.ProcessDTO;
import cz.nkp.urnnbn.shared.dto.process.ProcessDTOType;
import cz.nkp.urnnbn.shared.dto.process.XmlTransformationDTO;

public interface ProcessServiceAsync {

	void scheduleProcess(ProcessDTOType type, String[] params, AsyncCallback<Void> callback);

	void getAllProcesses(AsyncCallback<List<ProcessDTO>> callback);

	void getUsersProcesses(AsyncCallback<List<ProcessDTO>> callback);

	void deleteFinishedProcess(Long processId, AsyncCallback<Void> callback);

	void killRunningProcess(Long processId, AsyncCallback<Boolean> callback);

	void cancelScheduledProcess(Long processId, AsyncCallback<Boolean> callback);

	void createXmlTransformation(XmlTransformationDTO transformation, AsyncCallback<Void> callback);

	void getXmlTransformationsOfUser(AsyncCallback<List<XmlTransformationDTO>> callback);

	void deleteXmlTransformation(XmlTransformationDTO transformation, AsyncCallback<Void> callback);

}
