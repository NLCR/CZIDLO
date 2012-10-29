/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.dto;

import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.UrnNbnRegistrationMode;
import java.util.EnumMap;
import java.util.Map;

/**
 *
 * @author Martin Řehánek
 */
public final class Registrar extends Archiver {

    private RegistrarCode code;
    private Map<UrnNbnRegistrationMode, Boolean> modesAllowed = new EnumMap<UrnNbnRegistrationMode, Boolean>(UrnNbnRegistrationMode.class);

    public Registrar() {
        super();
    }

    public Registrar(Registrar original) {
        super(original);
        this.code = original.getCode();
        for (UrnNbnRegistrationMode mode : UrnNbnRegistrationMode.values()) {
            Boolean allowed = original.isRegistrationModeAllowed(mode);
            this.setRegistrationModeAllowed(mode, allowed);
        }
    }

    public RegistrarCode getCode() {
        return code;
    }

    public void setCode(RegistrarCode code) {
        this.code = code;
    }

    public Boolean isRegistrationModeAllowed(UrnNbnRegistrationMode mode) {
        return modesAllowed.get(mode);
    }

    public void setRegistrationModeAllowed(UrnNbnRegistrationMode mode, Boolean allowed) {
        modesAllowed.put(mode, allowed);
    }

    @Override
    public String toString() {
        return "Registrar{" + "code=" + code + '}';
    }

    public void loadDataFromArchiver(Archiver archiver) {
        setId(archiver.getId());
        setName(archiver.getName());
        setDescription(archiver.getDescription());
        setCreated(archiver.getCreated());
        setModified(archiver.getModified());
    }
}
