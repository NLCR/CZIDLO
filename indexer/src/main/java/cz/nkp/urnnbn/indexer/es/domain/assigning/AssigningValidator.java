package cz.nkp.urnnbn.indexer.es.domain.assigning;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssigningValidator {

    private static final Logger log = LoggerFactory.getLogger(AssigningValidator.class);

    public void validate(Assigning value) throws IllegalArgumentException {
        try {
            if (value == null) {
                throw new IllegalArgumentException("value cannot be null");
            }

            if (value.urnnbn == null || value.urnnbn.isEmpty()) {
                throw new IllegalArgumentException("UrnNbn cannot be null or empty, id = " + value.id);
            }
            if (value.urnnbn.size() != 1) {
                log.warn("UrnNbn size must be 1, but got {}, id = {}", value.urnnbn.size(), value.id);
            }
            if (value.urnnbn.getFirst().getUrnnbn() == null) {
                log.warn("Consturcted UrnNbn cannot be null, missing required parameters, id = {}", value.id);
            }
        } catch (Throwable e) {
            log.error("Validation error for Assigning id = {}: {}", value != null ? value.id : "null", e.getMessage());
            throw e;
        }
    }

}
