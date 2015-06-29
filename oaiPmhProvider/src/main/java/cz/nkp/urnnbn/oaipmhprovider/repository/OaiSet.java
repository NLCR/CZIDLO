/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider.repository;

import cz.nkp.urnnbn.core.dto.Registrar;

/**
 *
 * @author Martin Řehánek
 */
public class OaiSet {

    private final String setName;
    private final String setSpec;

    public OaiSet(String setSpec, String setName) {
        this.setSpec = setSpec;
        this.setName = setName;
    }

    public OaiSet(Registrar registrar) {
        this.setSpec = Repository.REGISTRAR_SET_PREFIX + registrar.getCode().toString();
        this.setName = registrar.getName();
    }

    /**
     * @return the setName
     */
    public String getSetName() {
        return setName;
    }

    /**
     * @return the setSpec
     */
    public String getSetSpec() {
        return setSpec;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OaiSet other = (OaiSet) obj;
        if ((this.setSpec == null) ? (other.setSpec != null) : !this.setSpec.equals(other.setSpec)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (this.setSpec != null ? this.setSpec.hashCode() : 0);
        return hash;
    }
}
