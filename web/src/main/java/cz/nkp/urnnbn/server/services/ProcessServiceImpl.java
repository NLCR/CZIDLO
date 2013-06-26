package cz.nkp.urnnbn.server.services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.nkp.urnnbn.client.services.ProcessService;
import cz.nkp.urnnbn.processmanager.control.AccessRightException;
import cz.nkp.urnnbn.processmanager.control.InvalidStateException;
import cz.nkp.urnnbn.processmanager.control.ProcessManager;
import cz.nkp.urnnbn.processmanager.control.ProcessManagerImpl;
import cz.nkp.urnnbn.processmanager.core.Process;
import cz.nkp.urnnbn.processmanager.core.ProcessType;
import cz.nkp.urnnbn.processmanager.core.XmlTransformation;
import cz.nkp.urnnbn.processmanager.core.XmlTransformationType;
import cz.nkp.urnnbn.processmanager.persistence.UnknownRecordException;
import cz.nkp.urnnbn.processmanager.persistence.XmlTransformationDAO;
import cz.nkp.urnnbn.processmanager.persistence.XmlTransformationDAOImpl;
import cz.nkp.urnnbn.server.dtoTransformation.process.ProcesDtoTypeTransformer;
import cz.nkp.urnnbn.server.dtoTransformation.process.ProcessDtoTransformer;
import cz.nkp.urnnbn.shared.dto.process.ProcessDTO;
import cz.nkp.urnnbn.shared.dto.process.ProcessDTOType;
import cz.nkp.urnnbn.shared.dto.process.XmlTransformationDTO;
import cz.nkp.urnnbn.shared.dto.process.XmlTransformationDTOType;
import cz.nkp.urnnbn.shared.exceptions.ServerException;

public class ProcessServiceImpl extends AbstractService implements ProcessService {

	private static final long serialVersionUID = 5647859643995913008L;

	private ProcessManager processManager() {
		return ProcessManagerImpl.instanceOf();
	}

	private XmlTransformationDAO xmlTransforamtionDao() {
		return XmlTransformationDAOImpl.instanceOf();
	}

	@Override
	public void scheduleProcess(ProcessDTOType type, String[] params) throws ServerException {
		ProcessType processType = new ProcesDtoTypeTransformer(type).transform();
		String login = getActiveUser().getLogin();
		if (processType == ProcessType.OAI_ADAPTER) {
			params = updateParamsForOai(params);
		}
		processManager().scheduleNewProcess(login, processType, params);
	}

	private String[] updateParamsForOai(String[] params) {
		try {
			String registrarCode = params[0];
			String registrationMode = params[1];
			String oaiBaseUrl = params[2];
			String oaiMetadataprefix = params[3];
			String oaiSet = params[4];
			File ddRegistrationXslt = saveTemplateToTempFile(params[5]);
			File diImportXslt = saveTemplateToTempFile(params[6]);

			String[] result = new String[9];
			result[0] = getUserLogin();
			result[1] = getActiveUser().getPassword();
			result[2] = registrationMode;
			result[3] = registrarCode;
			result[4] = oaiBaseUrl;
			result[5] = oaiMetadataprefix;
			result[6] = oaiSet;
			result[7] = ddRegistrationXslt.getAbsolutePath();
			result[8] = diImportXslt.getAbsolutePath();
			return result;
		} catch (Throwable e) {
			throw new SecurityException(e.getMessage());
		}
	}

	private File saveTemplateToTempFile(String transformationIdStr) throws Exception {
		Long id = Long.valueOf(transformationIdStr);
		XmlTransformation transformation = xmlTransforamtionDao().getTransformation(id);
		if (!getActiveUser().getLogin().equals(transformation.getOwnerLogin())) {
			throw new Exception("Transformation " + transformationIdStr + " doesn't belong to user " + getActiveUser());
		}
		return saveStringToTempFile(transformation.getXslt(), id);
	}

	private File saveStringToTempFile(String content, Long id) throws IOException {
		String filename = "xslt-" + id.toString() + new Date().getTime();
		File result = null;
		try {
			result = File.createTempFile(filename, ".xslt");
			writeToFile(result, content);
			//System.err.println("saving template to file " + result.getAbsolutePath());
			return result;
		} finally {
			if (result != null) {
				result.deleteOnExit();
			}
		}
	}

	private void writeToFile(File file, String content) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		bw.write(content);
		bw.close();
	}

	@Override
	public List<ProcessDTO> getAllProcesses() throws ServerException {
		return transform(processManager().getProcesses());
	}

	@Override
	public List<ProcessDTO> getUsersProcesses() throws ServerException {
		return transform(processManager().getProcessesByOwner(getUserLogin()));
	}

	private List<ProcessDTO> transform(List<Process> processes) {
		List<ProcessDTO> result = new ArrayList<ProcessDTO>(processes.size());
		for (Process original : processes) {
			result.add(new ProcessDtoTransformer(original).transform());
		}
		return result;
	}

	@Override
	public void deleteFinishedProcess(Long processId) throws ServerException {
		try {
			processManager().deleteProcess(getUserLogin(), processId);
		} catch (UnknownRecordException e) {
			throw new ServerException(e.getMessage());
		} catch (AccessRightException e) {
			throw new ServerException(e.getMessage());
		} catch (InvalidStateException e) {
			throw new ServerException(e.getMessage());
		}
	}

	@Override
	public boolean killRunningProcess(Long processId) throws ServerException {
		try {
			return processManager().killRunningProcess(getUserLogin(), processId);
		} catch (UnknownRecordException e) {
			e.printStackTrace();
			throw new ServerException(e.getMessage());
		} catch (AccessRightException e) {
			e.printStackTrace();
			throw new ServerException(e.getMessage());
		} catch (InvalidStateException e) {
			e.printStackTrace();
			throw new ServerException(e.getMessage());
		}
	}

	@Override
	public boolean cancelScheduledProcess(Long processId) throws ServerException {
		try {
			return processManager().cancelScheduledProcess(getUserLogin(), processId);
		} catch (UnknownRecordException e) {
			e.printStackTrace();
			throw new ServerException(e.getMessage());
		} catch (AccessRightException e) {
			e.printStackTrace();
			throw new ServerException(e.getMessage());
		} catch (InvalidStateException e) {
			e.printStackTrace();
			throw new ServerException(e.getMessage());
		}
	}

	@Override
	public void createXmlTransformation(XmlTransformationDTO original) throws ServerException {
		try {
			XmlTransformation transformation = new XmlTransformation();
			transformation.setName(original.getName());
			transformation.setDescription(original.getDescription());
			transformation.setOwnerLogin(getUserLogin());
			transformation.setType(transformType(original.getType()));
			transformation.setXslt(fileToString(original.getTemplateTemporaryFile()));
			removeFile(original.getTemplateTemporaryFile());
			xmlTransforamtionDao().saveTransformation(transformation);
		} catch (Throwable e) {
			// e.printStackTrace();
			throw new ServerException(e.getMessage());
		}
	}

	private void removeFile(String templateTemporaryFile) {
		File file = new File(templateTemporaryFile);
		boolean removed = file.delete();
		// System.err.println("removing file " + templateTemporaryFile +
		// ", removed: " + removed);
	}

	private String fileToString(String templateTemporaryFile) throws IOException {
		FileInputStream stream = new FileInputStream(new File(templateTemporaryFile));
		try {
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			return Charset.defaultCharset().decode(bb).toString();
		} finally {
			stream.close();
		}
	}

	private XmlTransformationType transformType(XmlTransformationDTOType original) {
		switch (original) {
		case DIGITAL_DOCUMENT_REGISTRATION:
			return XmlTransformationType.DIGITAL_DOCUMENT_REGISTRATION;
		case DIGITAL_INSTANCE_IMPORT:
			return XmlTransformationType.DIGITAL_INSTANCE_IMPORT;
		default:
			return null;
		}
	}

	@Override
	public List<XmlTransformationDTO> getXmlTransformationsOfUser() throws ServerException {
		List<XmlTransformation> original = xmlTransforamtionDao().getTransformationsOfUser(getUserLogin());
		return transformTransformations(original);
	}

	private List<XmlTransformationDTO> transformTransformations(List<XmlTransformation> originalList) {
		List<XmlTransformationDTO> result = new ArrayList<XmlTransformationDTO>(originalList.size());
		for (XmlTransformation original : originalList) {
			result.add(transformationToDto(original));
		}
		return result;
	}

	private XmlTransformationDTO transformationToDto(XmlTransformation original) {
		XmlTransformationDTO result = new XmlTransformationDTO();
		result.setId(original.getId());
		result.setName(original.getName());
		result.setDescription(original.getDescription());
		result.setType(transformTypeToDto(original.getType()));
		result.setCreated(transformaDate(original.getCreated()));
		return result;
	}

	private String transformaDate(Date created) {
		if (created == null) {
			return null;
		} else {
			return created.toString();
		}
	}

	private XmlTransformationDTOType transformTypeToDto(XmlTransformationType original) {
		switch (original) {
		case DIGITAL_DOCUMENT_REGISTRATION:
			return XmlTransformationDTOType.DIGITAL_DOCUMENT_REGISTRATION;
		case DIGITAL_INSTANCE_IMPORT:
			return XmlTransformationDTOType.DIGITAL_INSTANCE_IMPORT;
		default:
			return null;
		}
	}

	@Override
	public void deleteXmlTransformation(XmlTransformationDTO transformation) throws ServerException {
		try {
			xmlTransforamtionDao().deleteTransformation(transformation.getId());
		} catch (Throwable e) {
			throw new ServerException(e.getMessage());
		}
	}
}
