package cz.nkp.urnnbn.indexer.es.domain.searching;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class SearchingValidator {

    private static final Logger log = LoggerFactory.getLogger(SearchingValidator.class);

    public void validate(Searching value) throws IllegalArgumentException {
        try {
            if (value == null) {
                throw new IllegalArgumentException("value cannot be null");
            }

            // urnnbn
            if (value.urnnbn == null || value.urnnbn.isEmpty()) {
                throw new IllegalArgumentException("UrnNbn cannot be null or empty, id = " + value.id);
            }
            if (value.urnnbn.size() != 1) {
                log.warn("UrnNbn size must be 1, but got {}, id = {}", value.urnnbn.size(), value.id);
            }
            if (value.urnnbn.getFirst().getUrnnbn() == null) {
                log.warn("Consturcted UrnNbn cannot be null, missing required parameters, id = {}", value.id);
            }

            // originator
            if (value.originator != null && !value.originator.isEmpty()) {
                if (value.originator.size() != 1) {
                    log.warn("Originator size must be 1, but got {}, id = {}", value.originator.size(), value.id);
                }

                if (!Searching.OriginatorTypes.getValidTypes().contains(value.originator.getFirst().type)) {
                    log.warn("Originator of invalid type {}, id = {}", value.originator.getFirst().type, value.id);
                }
            }
            //publication
            if (value.publication != null && !value.publication.isEmpty()) {
                if (value.publication.size() != 1) {
                    log.warn("Publication size must be 1, but got {}, id = {}", value.publication.size(), value.id);
                }
            }

            // sourcedocument
            if (value.sourcedocument != null && !value.sourcedocument.isEmpty()) {
                if (value.sourcedocument.size() != 1) {
                    log.warn("Sourcedocument size must be 1, but got {}, id = {}", value.sourcedocument.size(), value.id);
                }
            }

            // ieidentifier
            if (value.ieidentifiers == null) value.ieidentifiers = Map.of(); // sanitize null ieidentifiers map
            for (var ieidentifier : value.ieidentifiers.entrySet()) {
                if (ieidentifier.getValue().size() != 1) {
                    log.warn("Ieidentifier of type {} size must be 1, but got {}, id = {}", ieidentifier.getKey(), ieidentifier.getValue().size(), value.id);
                }

                if (!Searching.IeIdentifierTypes.getValidTypes().contains(ieidentifier.getKey())) {
                    log.warn("Ieidentifier of invalid type {}, id = {}", ieidentifier.getKey(), value.id);
                }
            }
        } catch (Throwable e) {
            log.error("Validation error for Searching id = {}: {}", value != null ? value.id : "null", e.getMessage());
            throw e;
        }
    }

}
