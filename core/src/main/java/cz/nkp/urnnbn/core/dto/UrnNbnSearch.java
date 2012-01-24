/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.dto;

/**
 *
 * @author Martin Řehánek
 */
public class UrnNbnSearch {

    private Long registrarId;
    private String lastFoundDocumentCode = "zzzzzz";//so that first one used will be "000000"

    public String getLastFoundDocumentCode() {
        return lastFoundDocumentCode;
    }

    public void setLastFoundDocumentCode(String lastUsedDocumentCode) {
        this.lastFoundDocumentCode = lastUsedDocumentCode;
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
        final UrnNbnSearch other = (UrnNbnSearch) obj;
        if (this.registrarId != other.registrarId && (this.registrarId == null || !this.registrarId.equals(other.registrarId))) {
            return false;
        }
        if ((this.lastFoundDocumentCode == null) ? (other.lastFoundDocumentCode != null) : !this.lastFoundDocumentCode.equals(other.lastFoundDocumentCode)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.registrarId != null ? this.registrarId.hashCode() : 0);
        hash = 97 * hash + (this.lastFoundDocumentCode != null ? this.lastFoundDocumentCode.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "UrnNbnBooking{" + "registrarId=" + registrarId + ", lastUsedDocumentCode=" + lastFoundDocumentCode + '}';
    }
}
