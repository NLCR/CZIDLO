package cz.nkp.urnnbn.server.services;

import java.util.ArrayList;
import java.util.List;

import cz.nkp.urnnbn.client.services.ProcessService;
import cz.nkp.urnnbn.processmanager.core.Process;
import cz.nkp.urnnbn.processmanager.persistence.ProcessDAO;
import cz.nkp.urnnbn.processmanager.persistence.ProcessDAOImpl;
import cz.nkp.urnnbn.processmanager.persistence.UnknownRecordException;
import cz.nkp.urnnbn.processmanager.services.ProcessManagerImpl;
import cz.nkp.urnnbn.server.dtoTransformation.process.ProcessDtoTransformer;
import cz.nkp.urnnbn.shared.dto.process.ProcessDTO;
import cz.nkp.urnnbn.shared.dto.process.ProcessDTOState;
import cz.nkp.urnnbn.shared.dto.process.ProcessDTOType;
import cz.nkp.urnnbn.shared.exceptions.ServerException;

public class ProcessServiceImpl extends AbstractService implements ProcessService {

	private static final long serialVersionUID = 5647859643995913008L;

	@Override
	public List<ProcessDTO> getAllProcesses() throws ServerException {
		// return testProcesses();
		// TODO: nepristupovat vubec pres DAO, jen pres ProcessManager
		ProcessDAO dao = ProcessDAOImpl.instanceOf();
		return transform(dao.getProcesses());
	}

	private List<ProcessDTO> transform(List<Process> processes) {
		List<ProcessDTO> result = new ArrayList<ProcessDTO>(processes.size());
		for (Process original : processes) {
			result.add(new ProcessDtoTransformer(original).transform());
		}
		return result;
	}

	private List<ProcessDTO> testProcesses() {
		List<ProcessDTO> result = new ArrayList<ProcessDTO>();
		ProcessDTO scheduled = new ProcessDTO();
		scheduled.setId(Long.valueOf(123));
		scheduled.setOwnerLogin("martin");
		scheduled.setType(ProcessDTOType.OAI_ADAPTER);
		scheduled.setState(ProcessDTOState.SCHEDULED);
		scheduled.setScheduled("18.01.2013 00:18:52");
		result.add(scheduled);

		ProcessDTO running = new ProcessDTO();
		running.setId(Long.valueOf(333));
		running.setOwnerLogin("michal");
		running.setType(ProcessDTOType.TEST);
		running.setState(ProcessDTOState.RUNNING);
		running.setScheduled("18.01.2013 00:18:52");
		running.setStarted("18.01.2013 00:18:52");
		result.add(running);

		ProcessDTO failed = new ProcessDTO();
		failed.setId(Long.valueOf(589));
		failed.setOwnerLogin("deili");
		failed.setType(ProcessDTOType.REGISTRARS_URN_NBN_CSV_EXPORT);
		failed.setState(ProcessDTOState.FAILED);
		failed.setScheduled("18.01.2013 00:18:52");
		failed.setStarted("18.01.2013 00:18:52");
		failed.setFinished("18.01.2013 00:18:52");
		result.add(failed);

		ProcessDTO finished = new ProcessDTO();
		finished.setId(Long.valueOf(589));
		finished.setOwnerLogin("deili");
		finished.setType(ProcessDTOType.REGISTRARS_URN_NBN_CSV_EXPORT);
		finished.setState(ProcessDTOState.FINISHED);
		finished.setScheduled("18.01.2013 00:18:52");
		finished.setStarted("18.01.2013 00:18:52");
		finished.setFinished("18.01.2013 00:18:52");
		result.add(finished);

		return result;
	}

	@Override
	public void deleteFinishedProcess(Long processId) throws ServerException {
		//TODO: nedelat pres DAO, ted se nemazou adresare procesu 
		//a planovani procesu stejne musi jit pres ProcessManager
		Process process = new Process();
		process.setPersistentId(processId);
		try {
			ProcessDAOImpl.instanceOf().deleteProcess(process);
		} catch (UnknownRecordException e) {
			//TODO
		}
		//ProcessManagerImpl.instanceOf().deleteProcess("TODO", processId);
	}

	@Override
	public void killRunningProcess(Long processId) throws ServerException {
		// TODO Auto-generated method stub

	}

}
