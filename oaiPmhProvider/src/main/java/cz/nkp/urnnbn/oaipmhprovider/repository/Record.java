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
public interface Record {

    public Identifier getId();

    public boolean isDeleted();

    public DateStamp getDateStamp();

    public Set<OaiSet> getOaiSets();

    public MetadataFormat getMetadataFormat();
}
