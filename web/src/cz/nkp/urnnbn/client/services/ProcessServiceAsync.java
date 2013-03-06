package cz.nkp.urnnbn.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import cz.nkp.urnnbn.shared.dto.process.ProcessDTO;

public interface ProcessServiceAsync {

	void getAllProcesses(AsyncCallback<List<ProcessDTO>> callback);

	void deleteFinishedProcess(Long processId, AsyncCallback<Void> callback);

	void killRunningProcess(Long processId, AsyncCallback<Void> callback);

}
