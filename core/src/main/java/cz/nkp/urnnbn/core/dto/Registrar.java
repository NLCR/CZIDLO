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
    private Boolean allowedToRegisterFreeUrnNbn = false;

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

    public Boolean isAllowedToRegisterFreeUrnNbn() {
        return allowedToRegisterFreeUrnNbn;
    }

    public void setAllowedToRegisterFreeUrnNbn(Boolean allowedToRegisterFreeUrnNbn) {
        this.allowedToRegisterFreeUrnNbn = allowedToRegisterFreeUrnNbn;
    }

    public void loadDataFromArchiver(Archiver archiver) {
        setId(archiver.getId());
        setName(archiver.getName());
        setDescription(archiver.getDescription());
        setCreated(archiver.getCreated());
        setModified(archiver.getModified());
    }
}
