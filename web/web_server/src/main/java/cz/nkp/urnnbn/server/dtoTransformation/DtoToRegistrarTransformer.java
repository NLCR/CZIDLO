package cz.nkp.urnnbn.server.dtoTransformation;

import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.UrnNbnRegistrationMode;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;

public class DtoToRegistrarTransformer {

    private final RegistrarDTO registrar;

    public DtoToRegistrarTransformer(RegistrarDTO archiver) {
        this.registrar = archiver;
    }

    public Registrar transform() {
        Registrar result = new Registrar();
        if (registrar.getId() != null) {
            result.setId(registrar.getId());
        }
        result.setName(registrar.getName());
        result.setCode(RegistrarCode.valueOf(registrar.getCode()));
        result.setDescription(registrar.getDescription());
        result.setRegistrationModeAllowed(UrnNbnRegistrationMode.BY_REGISTRAR, registrar.isRegModeByRegistrarAllowed());
        result.setRegistrationModeAllowed(UrnNbnRegistrationMode.BY_RESOLVER, registrar.isRegModeByResolverAllowed());
        result.setRegistrationModeAllowed(UrnNbnRegistrationMode.BY_RESERVATION, registrar.isRegModeByReservationAllowed());
        result.setHidden(registrar.isHidden());
        return result;
    }
}
