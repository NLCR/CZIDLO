/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider.repository;

import cz.nkp.urnnbn.core.dto.UrnNbn;

import java.util.Set;

/**
 *
 * @author Martin Řehánek
 */
public interface Repository {

    public static final String REGISTRAR_SET_PREFIX = "registrar:";

    public Set<UrnNbn> getUrns(MetadataFormat format, DateStamp from, DateStamp until);

    public Set<UrnNbn> getUrns(MetadataFormat format, String setSpec, DateStamp from, DateStamp until);

    public Record getRecord(UrnNbn urn, MetadataFormat format, boolean validate);

    public Record getRecord(Identifier item, MetadataFormat format, boolean validate);

    public Iterable<OaiSet> getSets();
}
