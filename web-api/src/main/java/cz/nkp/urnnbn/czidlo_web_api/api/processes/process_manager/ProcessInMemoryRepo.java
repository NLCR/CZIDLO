package cz.nkp.urnnbn.czidlo_web_api.api.processes.process_manager;

import cz.nkp.urnnbn.czidlo_web_api.api.processes.core.Process;
import cz.nkp.urnnbn.czidlo_web_api.api.processes.core.ProcessState;
import cz.nkp.urnnbn.czidlo_web_api.api.processes.core.ProcessType;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static cz.nkp.urnnbn.czidlo_web_api.api.processes.core.ProcessState.SCHEDULED;

public class ProcessInMemoryRepo {
    private static ProcessInMemoryRepo instance;

    private static final SortedMap<Long, Process> processes = new TreeMap<>();
    private static final AtomicLong nextProcessId = new AtomicLong(0);

    private ProcessInMemoryRepo() {
    }

    public static ProcessInMemoryRepo getInstance() {
        if (instance == null) {
            instance = new ProcessInMemoryRepo();
        }
        return instance;
    }

    public Process create(String login, ProcessType type, Map<String, Object> processParams) {
        Process process = new Process();
        process.setId(nextProcessId.getAndIncrement());
        process.setOwnerLogin(login);
        process.setState(SCHEDULED);
        process.setType(type);
        process.setParams(processParams);
        process.setScheduled(new Date());
        process.setStarted(null);
        process.setFinished(null);

        processes.put(process.getId(), process);
        return process;
    }

    public List<Process> getAll() {
        return new ArrayList<>(processes.values());
    }

    public Process getById(long id) {
        return processes.get(id);
    }

    public List<Process> getByState(ProcessState state) {
        return processes.values().stream().filter(x -> x.getState().equals(state)).toList();
    }

    public List<Process> getByOwner(String login) {
        return processes.values().stream().filter(x -> x.getOwnerLogin().equals(login)).toList();
    }

    public void update(long id, Process process) {
        processes.put(id, process);
    }

    public void remove(long id) {
        processes.remove(id);
    }

}
