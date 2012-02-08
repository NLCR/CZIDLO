/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.dto;

/**
 *
 * @author Martin Řehánek
 */
public class Registrar extends Archiver {

    private String code;

    public Registrar() {
        super();
    }

    public Registrar(Registrar original) {
        super(original);
        code = original.getCode();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code.toLowerCase();
    }

    public void loadDataFromArchiver(Archiver archiver) {
        setId(archiver.getId());
        setName(archiver.getName());
        setDescription(archiver.getDescription());
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
