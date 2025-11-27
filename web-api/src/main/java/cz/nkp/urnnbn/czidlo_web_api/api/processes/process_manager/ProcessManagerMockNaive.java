package cz.nkp.urnnbn.czidlo_web_api.api.processes.process_manager;

import cz.nkp.urnnbn.czidlo_web_api.api.processes.core.Process;
import cz.nkp.urnnbn.czidlo_web_api.api.processes.core.ProcessState;
import cz.nkp.urnnbn.czidlo_web_api.api.processes.core.ProcessType;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.AccessRightException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnknownRecordException;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class ProcessManagerMockNaive extends ProcessManagerNoimpl {

    private static final Random RANDOM = new Random();

    @Override
    public Process getProcess(String login, Long processId) throws UnknownRecordException, AccessRightException {
        //ignore login, will be used only for access right check
        if (processId == 666) {
            throw new AccessRightException("Access denied to process: " + processId);
        } else if (processId % 2 == 0) { // simulate that only processes with odd IDs exist
            throw new UnknownRecordException("No such process: " + processId);
        } else {
            Process process = buildRandomProcess();
            process.setId(processId.intValue());
            return process;
        }
    }

    @Override
    public List<Process> getProcesses() {
        return createDummyProcesses();
    }

    public static Process buildRandomProcess() {
        cz.nkp.urnnbn.czidlo_web_api.api.processes.core.Process process = new Process();
        process.setId(RANDOM.nextInt(100000));
        process.setFinished(generateRandomDateTime());
        process.setStarted(generateRandomDateTime());
        process.setScheduled(generateRandomDateTime());
        process.setState(getRandomState());
        process.setType(getRandomType());
        return process;
    }

    private static Date generateRandomDateTime() {
        // For simplicity, return the current date. In a real scenario, you might want to generate a random date.
        return Date.from(Instant.parse(generateRandomDateTimeString()));
    }

    private static String generateRandomDateTimeString() {
        int year = 2020 + RANDOM.nextInt(5);
        int month = 1 + RANDOM.nextInt(12);
        int day = 1 + RANDOM.nextInt(28);
        int hour = RANDOM.nextInt(24);
        int minute = RANDOM.nextInt(60);
        int second = RANDOM.nextInt(60);
        return String.format("%04d-%02d-%02dT%02d:%02d:%02d", year, month, day, hour, minute, second);
    }

    private static ProcessState getRandomState() {
        ProcessState[] states = ProcessState.values();
        return states[RANDOM.nextInt(states.length)];
    }

    private static ProcessType getRandomType() {
        ProcessType[] types = ProcessType.values();
        return types[RANDOM.nextInt(types.length)];
    }

    private List<Process> createDummyProcesses() {
        int size = new Random().nextInt(10);
        return java.util.stream.IntStream.range(0, size)
                .mapToObj(i -> buildRandomProcess())
                .toList();
    }

}
