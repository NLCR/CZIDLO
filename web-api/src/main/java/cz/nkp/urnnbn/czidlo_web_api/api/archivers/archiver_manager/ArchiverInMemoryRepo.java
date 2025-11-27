package cz.nkp.urnnbn.czidlo_web_api.api.archivers.archiver_manager;

import cz.nkp.urnnbn.czidlo_web_api.api.archivers.core.Archiver;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class ArchiverInMemoryRepo {
    private static ArchiverInMemoryRepo instance;

    private static final SortedMap<Long, Archiver> archivers = new TreeMap<>();
    private static final AtomicLong nextArchiverId = new AtomicLong(0);

    private ArchiverInMemoryRepo() {}

    public static ArchiverInMemoryRepo getInstance() {
        if (instance == null) {
            instance = new ArchiverInMemoryRepo();
        }
        return instance;
    }

    public Archiver create(String name, String description) {
        Archiver archiver = new Archiver();
        archiver.setId(nextArchiverId.getAndIncrement());
        Date date = new Date();
        archiver.setCreated(date);
        archiver.setModified(date);
        archiver.setName(name);
        archiver.setDescription(description);
        archivers.put(archiver.getId(), archiver);

        return archiver;
    }

    public Archiver getById(long id){
        return archivers.get(id);
    }

    public Archiver getByName(String name){
        return archivers.values().stream().filter(x -> x.getName().equals(name)).findFirst().orElse(null);
    }

    public List<Archiver> getAll(){
        return new ArrayList<>( archivers.values());
    }

    public Archiver update(long id, String name, String description, boolean hidden){
        Archiver archiver = getById(id);
        archiver.setModified(new Date());
        archiver.setName(name);
        archiver.setDescription(description);
        archiver.setHidden(hidden);

        return archiver;
    }

    public void delete(long id){
        archivers.remove(id);
    }
}
