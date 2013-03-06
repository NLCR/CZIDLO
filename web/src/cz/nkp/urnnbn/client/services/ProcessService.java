package cz.nkp.urnnbn.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import cz.nkp.urnnbn.shared.dto.process.ProcessDTO;
import cz.nkp.urnnbn.shared.exceptions.ServerException;

@RemoteServiceRelativePath("process")
public interface ProcessService extends RemoteService {

	List<ProcessDTO> getAllProcesses() throws ServerException;
	
	void deleteFinishedProcess(Long processId) throws ServerException;
	
	void killRunningProcess(Long processId) throws ServerException;
}
