package cz.nkp.urnnbn.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import cz.nkp.urnnbn.shared.dto.process.ProcessDTO;
import cz.nkp.urnnbn.shared.dto.process.ProcessDTOType;
import cz.nkp.urnnbn.shared.dto.process.XmlTransformationDTO;
import cz.nkp.urnnbn.shared.exceptions.ServerException;

@RemoteServiceRelativePath("process")
public interface ProcessService extends RemoteService {

	void scheduleProcess(ProcessDTOType type, String[] params) throws ServerException;

	List<ProcessDTO> getUsersProcesses() throws ServerException;

	List<ProcessDTO> getAllProcesses() throws ServerException;

	boolean killRunningProcess(Long processId) throws ServerException;

	boolean cancelScheduledProcess(Long processId) throws ServerException;

	void deleteFinishedProcess(Long processId) throws ServerException;

	void createXmlTransformation(XmlTransformationDTO transformation) throws ServerException;

	List<XmlTransformationDTO> getXmlTransformationsOfUser() throws ServerException;

	void deleteXmlTransformation(XmlTransformationDTO transformation) throws ServerException;
}
