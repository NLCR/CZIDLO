package cz.nkp.urnnbn.indexer.es.domain.assigning;

import java.time.LocalDateTime;
import java.util.List;

public class Assigning {

    public Long id;

    public List<UrnNbn> urnnbn;
    public String entitytype;
    public Registrar registrar;
    public Archiver archiver;

    public static class UrnNbn {

        public String documentcode;
        public String registrarcode;
        public LocalDateTime registered;
        public LocalDateTime reserved;
        public LocalDateTime deactivated;
        public String deactivationnote;
        public Boolean active;

        public String getUrnnbn() {
            if (registrarcode == null || documentcode == null) {
                return null;
            }
            return "urn:nbn:cz:" + registrarcode + "-" + documentcode;
        }
    }

    public static class Registrar {
        public Long id;
        public String name;
    }

    public static class Archiver {
        public Long id;
        public String name;
    }

}
