package cz.nkp.urnnbn.shared;

import cz.nkp.urnnbn.shared.dto.ie.IntelectualEntityDTO;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Martin Řehánek on 30.1.18.
 */
public class SearchResult implements Serializable {

    private static final long serialVersionUID = -913399132104731244L;

    private Long numFound;
    private Long start;
    private List<IntelectualEntityDTO> intelectualEntities;

    public Long getNumFound() {
        return numFound;
    }

    public void setNumFound(Long numFound) {
        this.numFound = numFound;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public List<IntelectualEntityDTO> getIntelectualEntities() {
        return intelectualEntities;
    }

    public void setIntelectualEntities(List<IntelectualEntityDTO> intelectualEntities) {
        this.intelectualEntities = intelectualEntities;
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "numFound=" + numFound +
                ", start=" + start +
                ", intelectualEntities=" + intelectualEntities +
                '}';
    }
}
