package cz.nkp.urnnbn.server.services;

import cz.nkp.urnnbn.client.services.ProcessService;
import cz.nkp.urnnbn.core.CountryCode;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.processmanager.control.ProcessManager;
import cz.nkp.urnnbn.processmanager.control.ProcessManagerImpl;
import cz.nkp.urnnbn.processmanager.core.Process;
import cz.nkp.urnnbn.processmanager.core.ProcessType;
import cz.nkp.urnnbn.processmanager.core.XmlTransformation;
import cz.nkp.urnnbn.processmanager.core.XmlTransformationType;
import cz.nkp.urnnbn.processmanager.persistence.XmlTransformationDAO;
import cz.nkp.urnnbn.processmanager.persistence.XmlTransformationDAOImpl;
import cz.nkp.urnnbn.server.dtoTransformation.process.ProcesDtoTypeTransformer;
import cz.nkp.urnnbn.server.dtoTransformation.process.ProcessDtoTransformer;
import cz.nkp.urnnbn.services.exceptions.NotAdminException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;
import cz.nkp.urnnbn.shared.dto.UserDTO;
import cz.nkp.urnnbn.shared.dto.process.ProcessDTO;
import cz.nkp.urnnbn.shared.dto.process.ProcessDTOType;
import cz.nkp.urnnbn.shared.dto.process.XmlTransformationDTO;
import cz.nkp.urnnbn.shared.dto.process.XmlTransformationDTOType;
import cz.nkp.urnnbn.shared.exceptions.ServerException;
import cz.nkp.urnnbn.shared.exceptions.SessionExpirationException;
import cz.nkp.urnnbn.webcommon.security.MemoryPasswordsStorage;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProcessServiceImpl extends AbstractService implements ProcessService {

    private static final long serialVersionUID = 5647859643995913008L;
    private static final Logger logger = Logger.getLogger(ProcessServiceImpl.class.getName());

    private ProcessManager processManager() {
        return ProcessManagerImpl.instanceOf();
    }

    private XmlTransformationDAO xmlTransformationDao() {
        return XmlTransformationDAOImpl.instanceOf();
    }

    @Override
    public void scheduleProcess(ProcessDTOType type, String[] paramsFromClient) throws ServerException {
        try {
            checkNotReadOnlyMode();
            ProcessType processType = new ProcesDtoTypeTransformer(type).transform();
            UserDTO user = getActiveUser();
            logger.info(user.toString());
            String[] processParams = buildProcessParams(paramsFromClient, processType);
            switch (processType) {
                case DI_URL_AVAILABILITY_CHECK:
                    if (!user.isSuperAdmin()) {
                        throw new ServerException("user " + user.getLogin() + ": access denied");
                    }
                    break;
                case INDEXATION:
                    if (!user.isSuperAdmin()) {
                        throw new ServerException("user " + user.getLogin() + ": access denied");
                    }
                    break;
                case REGISTRARS_URN_NBN_CSV_EXPORT:
                    if (!user.isLoggedUser()) {
                        throw new ServerException("user " + user.getLogin() + ": access denied");
                    } else {
                        if (!user.isSuperAdmin()) {
                            checkAccessRights(user.getLogin(), processParams[2].split(","));
                        }
                    }
                    break;
                // TODO: check acces rights for OAI Adapter and other future processes
                default:
                    break;
            }
            processManager().scheduleNewProcess(user.getLogin(), processType, processParams);
        } catch (Throwable e) {
            String message = e.getClass().getName() + ": " + e.getMessage();
            logger.log(Level.WARNING, message);
            e.printStackTrace();
            throw new ServerException(message);
        }
    }

    private void checkAccessRights(String login, String[] registrarCodes) throws UnknownUserException, NotAdminException, ServerException {
        User user = readService.userByLogin(login);
        List<Registrar> managedRegistrars = readService.registrarsManagedByUser(user.getId(), user.getLogin());
        Set<String> managedRegistrarCodes = toRegistrarCodes(managedRegistrars);
        for (String registrarCode : registrarCodes) {
            if (!managedRegistrarCodes.contains(registrarCode)) {
                throw new ServerException("user " + login + ": no access right to registrar " + registrarCode);
            }
        }
    }

    private Set<String> toRegistrarCodes(List<Registrar> registrars) {
        Set<String> result = new HashSet<>(registrars.size());
        for (Registrar registrar : registrars) {
            result.add(registrar.getCode().toString());
        }
        return result;
    }

    private String[] buildProcessParams(String[] paramsFromClient, ProcessType processType) throws Exception {
        String[] result;
        switch (processType) {
            case OAI_ADAPTER:
                String registrarCode = paramsFromClient[0];
                String oaiBaseUrl = paramsFromClient[1];
                String oaiMetadataprefix = paramsFromClient[2];
                String oaiSet = paramsFromClient[3];
                XmlTransformation ddRegistrationXslt = buildXslTransformation(Long.valueOf(paramsFromClient[4]));
                File ddRegistrationXsltFile = saveStringToTempFile(ddRegistrationXslt.getXslt(), ddRegistrationXslt.getId());
                XmlTransformation diImportXslt = buildXslTransformation(Long.valueOf(paramsFromClient[5]));
                File diImportXsltFile = saveStringToTempFile(diImportXslt.getXslt(), diImportXslt.getId());
                Boolean ddRegistrationRegisterDdsWithUrn = Boolean.valueOf(paramsFromClient[6]);
                Boolean ddRegistrationRegisterDdsWithoutUrn = Boolean.valueOf(paramsFromClient[7]);
                Boolean diImportMergeDis = Boolean.valueOf(paramsFromClient[8]);
                Boolean diImportIgnoreDifferenceInAccessibility = Boolean.valueOf(paramsFromClient[9]);
                Boolean diImportIgnoreDifferenceInFormat = Boolean.valueOf(paramsFromClient[10]);

                result = new String[15];
                //registrar
                result[0] = registrarCode;
                //czidlo api credentials
                result[1] = getUserLogin();
                result[2] = MemoryPasswordsStorage.instanceOf().getPassword(getUserLogin());
                //oai configuration
                result[3] = oaiBaseUrl;
                result[4] = oaiMetadataprefix;
                result[5] = oaiSet;
                //xslt
                result[6] = ddRegistrationXslt.getName();
                result[7] = ddRegistrationXsltFile.getAbsolutePath();
                result[8] = diImportXslt.getName();
                result[9] = diImportXsltFile.getAbsolutePath();
                //dd-registration and di-import flags
                result[10] = ddRegistrationRegisterDdsWithUrn.toString();
                result[11] = ddRegistrationRegisterDdsWithoutUrn.toString();
                result[12] = diImportMergeDis.toString();
                result[13] = diImportIgnoreDifferenceInAccessibility.toString();
                result[14] = diImportIgnoreDifferenceInFormat.toString();
                return result;
            case INDEXATION:
                String dateFrom = paramsFromClient[0];
                String dateTo = paramsFromClient[1];

                result = new String[2];
                result[0] = dateFrom;
                result[1] = dateTo;
                return result;
            case REGISTRARS_URN_NBN_CSV_EXPORT:
                // no czidlo api credentials needed
                result = new String[paramsFromClient.length + 1];
                for (int i = 0; i < paramsFromClient.length; i++) {
                    result[i] = paramsFromClient[i];
                }
                result[result.length - 1] = CountryCode.getCode();
                return result;
            case DI_URL_AVAILABILITY_CHECK:
                // no czidlo api credentials needed
                result = new String[paramsFromClient.length + 1];
                for (int i = 0; i < paramsFromClient.length; i++) {
                    result[i] = paramsFromClient[i];
                }
                result[result.length - 1] = CountryCode.getCode();
                return result;
            default:
                return paramsFromClient;
        }

    }

    private XmlTransformation buildXslTransformation(Long transformationId) throws Exception {
        XmlTransformation transformation = xmlTransformationDao().getTransformation(transformationId);
        if (!getActiveUser().getLogin().equals(transformation.getOwnerLogin())) {
            throw new Exception("Transformation " + transformationId + " doesn't belong to user " + getActiveUser());
        }
        return transformation;
    }

    private File saveStringToTempFile(String content, Long id) throws IOException {
        String filename = "xslt-" + id.toString() + new Date().getTime();
        File result = null;
        try {
            result = File.createTempFile(filename, ".xslt");
            writeToFile(result, content);
            // System.err.println("saving template to file " + result.getAbsolutePath());
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
        try {
            return transform(processManager().getProcesses());
        } catch (Throwable e) {
            throw new SecurityException(e.getMessage());
        }
    }

    @Override
    public List<ProcessDTO> getUsersProcesses() throws ServerException, SessionExpirationException {
        try {
            return transform(processManager().getProcessesByOwner(getUserLogin()));
        } catch (SessionExpirationException e) {
            throw e;
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new SecurityException(e.getMessage());
        }
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
            checkNotReadOnlyMode();
            processManager().deleteProcess(getUserLogin(), processId);
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    @Override
    public boolean killRunningProcess(Long processId) throws ServerException {
        try {
            checkNotReadOnlyMode();
            return processManager().killRunningProcess(getUserLogin(), processId);
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    @Override
    public boolean cancelScheduledProcess(Long processId) throws ServerException {
        try {
            checkNotReadOnlyMode();
            return processManager().cancelScheduledProcess(getUserLogin(), processId);
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    @Override
    public void createXmlTransformation(XmlTransformationDTO original) throws ServerException {
        try {
            checkNotReadOnlyMode();
            XmlTransformation transformation = new XmlTransformation();
            transformation.setName(original.getName());
            transformation.setDescription(original.getDescription());
            transformation.setOwnerLogin(getUserLogin());
            transformation.setType(transformType(original.getType()));
            transformation.setXslt(fileToString(original.getTemplateTemporaryFile()));
            removeFile(original.getTemplateTemporaryFile());
            xmlTransformationDao().saveTransformation(transformation);
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
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
    public List<XmlTransformationDTO> getXmlTransformationsOfUser() throws ServerException, SessionExpirationException {
        try {
            List<XmlTransformation> original = xmlTransformationDao().getTransformationsOfUser(getUserLogin());
            return transformTransformations(original);
        } catch (SessionExpirationException e) {
            throw e;
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
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

    private Long transformaDate(Date created) {
        if (created == null) {
            return null;
        } else {
            return created.getTime();
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
            xmlTransformationDao().deleteTransformation(transformation.getId());
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }
}
