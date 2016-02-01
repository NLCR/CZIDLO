/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.dto;

/**
 *
 * @author Martin Řehánek
 */
public class UrnNbnGenerator {

    private Long registrarId;
    private String lastDocumentCode = "zzzzzz";// so that first one used will be "000000"

    public String getLastDocumentCode() {
        return lastDocumentCode;
    }

    public void setLastDocumentCode(String lastDocumentCode) {
        this.lastDocumentCode = lastDocumentCode;
    }

    public Long getRegistrarId() {
        return registrarId;
    }

    public void setRegistrarId(Long registrarId) {
        this.registrarId = registrarId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UrnNbnGenerator other = (UrnNbnGenerator) obj;
        if (this.registrarId != other.registrarId && (this.registrarId == null || !this.registrarId.equals(other.registrarId))) {
            return false;
        }
        if ((this.lastDocumentCode == null) ? (other.lastDocumentCode != null) : !this.lastDocumentCode.equals(other.lastDocumentCode)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.registrarId != null ? this.registrarId.hashCode() : 0);
        hash = 97 * hash + (this.lastDocumentCode != null ? this.lastDocumentCode.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "UrnNbnBooking{" + "registrarId=" + registrarId + ", lastUsedDocumentCode=" + lastDocumentCode + '}';
    }
}
