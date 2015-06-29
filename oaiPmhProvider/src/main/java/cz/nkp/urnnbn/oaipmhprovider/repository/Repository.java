/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider.repository;

import java.util.Set;

/**
 *
 * @author Martin Řehánek
 */
public interface Repository {

    public static final String REGISTRAR_SET_PREFIX = "registrar:";

    public Set<Record> getRecords(MetadataFormat format, DateStamp from, DateStamp until);

    public Set<Record> getRecords(MetadataFormat format, String setSpec, DateStamp from, DateStamp until);

    public Record getRecord(Identifier item, MetadataFormat format, boolean validate);

    public Iterable<OaiSet> getSets();
}
