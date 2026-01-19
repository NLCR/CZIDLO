/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider.response.listRequests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.oaipmhprovider.repository.MetadataFormat;

/**
 *
 * @author Martin Řehánek <rehan at mzk.cz>
 */
public class CompleteList {

    private final List<UrnNbn> urns;
    private final MetadataFormat format;

    public CompleteList(Collection<UrnNbn> urns, MetadataFormat format) {
        this.urns = new ArrayList<UrnNbn>(urns);
        this.format = format;
    }

    public int totalSize() {
        return urns.size();
    }

    List<UrnNbn> getUrnNbns(int start, int maxEnd, int maxLength) {
        if (start < 0) {
            throw new IllegalArgumentException("start cannot be negative");
        }
        if (maxEnd <= 0) {
            throw new IllegalArgumentException("maxEnd must be positive");
        }
        if (maxLength <= 0) {
            throw new IllegalArgumentException("maxLength must be positive");
        }
        int realEnd = (start + maxLength) > urns.size() ? urns.size() // all remaining
                : (start + maxLength); // another part
        return urns.subList(start, realEnd);
    }

    public MetadataFormat getFormat() {
        return format;
    }
}
