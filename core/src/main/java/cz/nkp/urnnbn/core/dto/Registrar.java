/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.dto;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.UrnNbnRegistrationMode;

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

    public void loadDataFromArchiver(Archiver archiver) {
        setId(archiver.getId());
        setName(archiver.getName());
        setDescription(archiver.getDescription());
        setCreated(archiver.getCreated());
        setModified(archiver.getModified());
        setOrder(archiver.getOrder());
        setHidden(archiver.isHidden());
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Registrar{");
        result.append("code=").append(code);
        result.append(", ");
        result.append("name=").append(getName());
        result.append(", ");
        if (getDescription() != null) {
            result.append("description=").append(getDescription());
            result.append(", ");
        }
        result.append("modesAllowed=").append(modesToString());
        result.append("}");
        return result.toString();
    }

    private String modesToString() {
        StringBuilder result = new StringBuilder();
        result.append("[");
        int counter = 0;
        for (UrnNbnRegistrationMode mode : modesAllowed.keySet()) {
            result.append(mode.toString()).append("=").append(modesAllowed.get(mode).toString());
            if (counter < modesAllowed.size() - 1) {
                result.append(", ");
            }
            counter++;
        }
        result.append("]");
        return result.toString();
    }

    public String modesToHumanReadableString() {
        List<UrnNbnRegistrationMode> modes = new ArrayList<UrnNbnRegistrationMode>(modesAllowed.size());
        for (UrnNbnRegistrationMode mode : modesAllowed.keySet()) {
            if (modesAllowed.get(mode)) {
                modes.add(mode);
            }
        }
        if (modes.isEmpty()) {
            return "none";
        } else {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < modes.size(); i++) {
                UrnNbnRegistrationMode mode = modes.get(i);
                builder.append(mode.toString());
                if (i != modes.size() - 1) {
                    builder.append(", ");
                }
            }
            return builder.toString();
        }
    }
}
