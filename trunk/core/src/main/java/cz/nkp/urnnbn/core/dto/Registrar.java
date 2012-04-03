/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.dto;

import cz.nkp.urnnbn.core.RegistrarCode;

/**
 *
 * @author Martin Řehánek
 */
public class Registrar extends Archiver {

    private RegistrarCode code;
    private boolean allowedToRegisterFreeUrnNbn = false;

    public Registrar() {
        super();
    }

    public Registrar(Registrar original) {
        super(original);
        code = original.getCode();
    }

    public RegistrarCode getCode() {
        return code;
    }

    public void setCode(RegistrarCode code) {
        this.code = code;
    }

    public boolean isAllowedToRegisterFreeUrnNbn() {
        return allowedToRegisterFreeUrnNbn;
    }

    public void setAllowedToRegisterFreeUrnNbn(boolean allowedToRegisterFreeUrnNbn) {
        this.allowedToRegisterFreeUrnNbn = allowedToRegisterFreeUrnNbn;
    }

    public void loadDataFromArchiver(Archiver archiver) {
        setId(archiver.getId());
        setName(archiver.getName());
        setDescription(archiver.getDescription());
        setCreated(archiver.getCreated());
        setModified(archiver.getModified());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Registrar other = (Registrar) obj;
        if (this.getId() != other.getId()) {
            return false;
        }
        if ((this.getName() == null) ? (other.getName() != null) : !this.getName().equals(other.getName())) {
            return false;
        }
        if ((this.getDescription() == null) ? (other.getDescription() != null) : !this.getDescription().equals(other.getDescription())) {
            return false;
        }
        if ((this.code == null) ? (other.code != null) : !this.code.equals(other.code)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (int) (this.getId() ^ (this.getId() >>> 32));
        hash = 79 * hash + (this.getName() != null ? this.getName().hashCode() : 0);
        hash = 79 * hash + (this.getDescription() != null ? this.getDescription().hashCode() : 0);
        hash = 79 * hash + (this.code != null ? this.code.hashCode() : 0);
        return hash;
    }
}
