package cz.nkp.urnnbn.czidlo_web_api.api.processes.process_manager;


import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.czidlo_web_api.api.processes.core.Process;
import cz.nkp.urnnbn.czidlo_web_api.api.processes.core.ProcessState;
import cz.nkp.urnnbn.czidlo_web_api.api.processes.core.ProcessType;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.AccessRightException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.InvalidStateException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnknownRecordException;

import java.io.*;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;

import static cz.nkp.urnnbn.czidlo_web_api.api.processes.core.ProcessState.*;
import static cz.nkp.urnnbn.czidlo_web_api.api.processes.core.ProcessType.*;

public class ProcessManagerMockInMemory extends ProcessManagerNoimpl {
    private static final ProcessInMemoryRepo repo = ProcessInMemoryRepo.getInstance();

    public ProcessManagerMockInMemory() {
        Process p0 = repo.create("dummyUser", REGISTRARS_URN_NBN_CSV_EXPORT, sampleParams());
        p0.setState(SCHEDULED);
        p0.setScheduled(Date.from(Instant.parse("2025-06-07T10:11:12.028231961Z")));

        Process p1 = repo.create("dummyUser", OAI_ADAPTER, sampleParams());
        p1.setState(CANCELED);
        p1.setScheduled(Date.from(Instant.parse("2025-07-08T11:12:13.028231961Z")));
        writeToFile(p1, "log.txt", "Test log: " + p1.getState().toString());

        Process p2 = repo.create("dummyUser", DI_URL_AVAILABILITY_CHECK, sampleParams());
        p2.setState(RUNNING);
        p2.setScheduled(Date.from(Instant.parse("2025-08-09T12:13:14.028231961Z")));
        p2.setStarted(Date.from(Instant.parse("2025-08-09T12:14:15.028231961Z")));
        writeToFile(p2, "log.txt", "Test log: " + p2.getState().toString());

        //FAILED - typical
        Process p3 = repo.create("dummyUser", TEST, sampleParams());
        p3.setState(FAILED);
        p3.setScheduled(Date.from(Instant.parse("2025-10-11T14:15:16.028231961Z")));
        p3.setStarted(Date.from(Instant.parse("2025-10-11T14:16:17.028231961Z")));
        p3.setFinished(Date.from(Instant.parse("2025-10-11T14:17:18.028231961Z")));
        writeToFile(p3, "log.txt", "Test log: " + p3.getState().toString());

        //FAILED - missing log file
        Process p4 = repo.create("dummyUser", TEST, sampleParams());
        p4.setState(FAILED);
        p4.setScheduled(Date.from(Instant.parse("2025-10-11T14:25:16.028231961Z")));
        p4.setStarted(Date.from(Instant.parse("2025-10-11T14:26:27.028231961Z")));
        p4.setFinished(Date.from(Instant.parse("2025-10-11T14:28:18.028231961Z")));
        //missing log file (simulating internal error or log file being deleted some other way)

        Process p5 = repo.create("dummyUser", TEST, sampleParams());
        p5.setState(KILLED);
        p5.setScheduled(Date.from(Instant.parse("2025-11-12T15:16:17.028231961Z")));
        p5.setStarted(Date.from(Instant.parse("2025-11-12T15:17:18.028231961Z")));
        p5.setFinished(Date.from(Instant.parse("2025-11-12T15:22:19.028231961Z")));

        //FINISHED - REGISTRARS_URN_NBN_CSV_EXPORT
        Process p6 = repo.create("dummyUser", REGISTRARS_URN_NBN_CSV_EXPORT, sampleParams());
        p6.setState(FINISHED);
        p6.setScheduled(Date.from(Instant.parse("2025-09-10T13:14:15.028231961Z")));
        p6.setStarted(Date.from(Instant.parse("2025-09-10T13:15:16.028231961Z")));
        p6.setFinished(Date.from(Instant.parse("2025-09-10T13:16:17.028231961Z")));
        writeToFile(p6, "log.txt", "Test log: " + p6.getState().toString());
        writeToFile(p6, "output.csv", "\"id\",\"state\"\n\"" + p6.getId() + "\",\"" + p6.getState().toString() + "\"\n");

        //FINISHED - OAI_ADAPTER
        Process p7 = repo.create("dummyUser", OAI_ADAPTER, sampleParams());
        p7.setState(FINISHED);
        p7.setScheduled(Date.from(Instant.parse("2025-09-10T13:14:15.028231961Z")));
        p7.setStarted(Date.from(Instant.parse("2025-09-10T13:15:16.028231961Z")));
        p7.setFinished(Date.from(Instant.parse("2025-09-10T13:16:17.028231961Z")));
        writeToFile(p7, "log.txt", "Test log: " + p7.getState().toString());
        writeToFile(p7, "output.txt", "Test output file: " + p7.getState().toString());

        //FINISHED - DI_URL_AVAILABILITY_CHECK
        Process p8 = repo.create("dummyUser", DI_URL_AVAILABILITY_CHECK, sampleParams());
        p8.setState(FINISHED);
        p8.setScheduled(Date.from(Instant.parse("2025-09-10T13:14:15.028231961Z")));
        p8.setStarted(Date.from(Instant.parse("2025-09-10T13:15:16.028231961Z")));
        p8.setFinished(Date.from(Instant.parse("2025-09-10T13:16:17.028231961Z")));
        writeToFile(p8, "log.txt", "Test log: " + p8.getState().toString());
        writeToFile(p8, "output.csv", "\"id\",\"state\"\n\"" + p8.getId() + "\",\"" + p8.getState().toString() + "\"\n");

        //FINISHED - INDEXATION
        Process p9 = repo.create("dummyUser", INDEXATION, sampleParams());
        p9.setState(FINISHED);
        p9.setScheduled(Date.from(Instant.parse("2025-09-10T13:14:15.028231961Z")));
        p9.setStarted(Date.from(Instant.parse("2025-09-10T13:15:16.028231961Z")));
        p9.setFinished(Date.from(Instant.parse("2025-09-10T13:16:17.028231961Z")));
        writeToFile(p9, "log.txt", "Test log: " + p9.getState().toString());
        writeToFile(p9, "output.txt", "Test output file: " + p9.getState().toString());

        //FINISHED - TEST
        Process p10 = repo.create("dummyUser", TEST, sampleParams());
        p10.setState(FINISHED);
        p10.setScheduled(Date.from(Instant.parse("2025-09-10T13:14:15.028231961Z")));
        p10.setStarted(Date.from(Instant.parse("2025-09-10T13:15:16.028231961Z")));
        p10.setFinished(Date.from(Instant.parse("2025-09-10T13:16:17.028231961Z")));
        writeToFile(p10, "log.txt", "Test log: " + p10.getState().toString());
        writeToFile(p10, "output.json", "{Test output file: " + p10.getState().toString() + "}");
    }

    private Map<String, Object> sampleParams() {
        Map<String, Object> params = new HashMap<>();
        Random random = new Random();
        String randomName = switch (random.nextInt(3)) {
            case 0 -> "Alice";
            case 1 -> "Bob";
            case 2 -> "Charlie";
            default -> "Unknown";
        };
        params.put("name", randomName);
        params.put("coins", random.nextInt());
        params.put("size", random.nextFloat());
        params.put("smart", random.nextBoolean());
        return params;
    }

    private static void writeToFile(Process process, String filename, String content) {
        try {
            Path path = Path.of("data", Long.toString(process.getId()), filename);
            Files.createDirectories(path.getParent());
            Files.writeString(path, content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Process scheduleNewProcess(String login, ProcessType type, Map<String, Object> processParams) throws IllegalArgumentException {
        return repo.create(login, type, processParams);
    }

    @Override
    public Process getProcess(User user, Long processId) throws UnknownRecordException, AccessRightException {
        Process process = repo.getById(processId);

        if (process == null) {
            throw new UnknownRecordException("Unknown process: " + processId);
        }
        if (!process.getOwnerLogin().equals(user)) {
            throw new AccessRightException("Access denied to process: " + processId);
        }

        return process;
    }

    @Override
    public List<Process> getAllProcesses() {
        return repo.getAll();
    }

    @Override
    public List<Process> getProcessesByState(ProcessState state) {
        return repo.getByState(state);
    }

    @Override
    public List<Process> getProcessesByOwner(String ownerLogin) {
        return repo.getByOwner(ownerLogin);
    }

    @Override
    public boolean killRunningProcess(User user, Long processId) throws UnknownRecordException, AccessRightException, InvalidStateException {
        Process process = getProcess(user, processId);

        if (!process.getState().equals(RUNNING)) {
            throw new InvalidStateException("Cannot kill process: " + processId);
        }

        process.setState(KILLED);
        process.setFinished(new Date()); //timestamp finished je ukončení procesu jak korektním doběhnutím, tak i jeho zabitím
        return true;
    }

    @Override
    public boolean cancelScheduledProcess(User user, Long processId) throws UnknownRecordException, AccessRightException, InvalidStateException {
        Process process = getProcess(user, processId);

        if (!process.getState().equals(SCHEDULED)) {
            throw new InvalidStateException("Cannot cancel process: " + processId);
        }

        process.setState(CANCELED);
        return true;
    }

    @Override
    public void deleteProcess(User user, Long processId) throws UnknownRecordException, AccessRightException, InvalidStateException {
        Process process = getProcess(user, processId);

        if (List.of(SCHEDULED, RUNNING).contains(process.getState())) {
            throw new InvalidStateException("Cannot delete process: " + processId);
        }

        repo.remove(processId);
    }

    @Override
    public FileInputStream getProcessLog(User user, Long processId) throws UnknownRecordException, AccessRightException, IOException {
        Process process = getProcess(user, processId);

        Path path = Path.of("data", Long.toString(process.getId()), "log.txt");
        File file = new File(path.toUri());

        return new FileInputStream(file);
    }

    @Override
    public ProcessInMemoryOutputFile getProcessOutput(User user, Long processId) throws UnknownRecordException, AccessRightException, InvalidStateException, IOException {
        Process process = getProcess(user, processId);

        if (List.of(SCHEDULED, CANCELED, RUNNING).contains(process.getState())) {
            throw new InvalidStateException("In invalid state \"" + process.getState() + "\" to return output file for process: " + processId);
        }

        ProcessInMemoryOutputFile outputFile = new ProcessInMemoryOutputFile(processId, process.getType());

        if (!outputFile.getFile().exists()) {
            throw new FileNotFoundException("Output file not found for process: " + processId);
        }

        return outputFile;
    }


}
