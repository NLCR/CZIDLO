/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider.response.listRequests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cz.nkp.urnnbn.oaipmhprovider.repository.Record;

/**
 *
 * @author Martin Řehánek <rehan at mzk.cz>
 */
public class CompleteList {

    private final List<Record> records;

    public CompleteList(Collection<Record> records) {
        this.records = new ArrayList<Record>(records);
    }

    public int totalSize() {
        return records.size();
    }

    List<Record> getIdentifiers(int start, int maxEnd, int maxLength) {
        if (start < 0) {
            throw new IllegalArgumentException("start cannot be negative");
        }
        if (maxEnd <= 0) {
            throw new IllegalArgumentException("maxEnd must be positive");
        }
        if (maxLength <= 0) {
            throw new IllegalArgumentException("maxLength must be positive");
        }
        int realEnd = (start + maxLength) > records.size() ? records.size() // all remaining
                : (start + maxLength); // another part
        return records.subList(start, realEnd);
    }
}
