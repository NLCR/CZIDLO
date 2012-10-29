package cz.nkp.urnnbn.server.dtoTransformation;

import cz.nkp.urnnbn.core.UrnNbnRegistrationMode;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;

public class RegistrarDtoTransformer extends DtoTransformer {

	private final Registrar original;

	public RegistrarDtoTransformer(Registrar registrar) {
		this.original = registrar;
	}

	public RegistrarDTO transform() {
		RegistrarDTO result = new RegistrarDTO();
		result.setId(original.getId());
		result.setCreated(dateTimeToStringOrNull(original.getCreated()));
		result.setModified(dateTimeToStringOrNull(original.getModified()));
		result.setCode(original.getCode().toString());
		result.setName(original.getName());
		result.setDescription(original.getDescription());
		result.setRegModeByRegistrarAllowed(original.isRegistrationModeAllowed(UrnNbnRegistrationMode.BY_REGISTRAR));
		result.setRegModeByResolverAllowed(original.isRegistrationModeAllowed(UrnNbnRegistrationMode.BY_RESOLVER));
		result.setRegModeByReservationAllowed(original.isRegistrationModeAllowed(UrnNbnRegistrationMode.BY_RESERVATION));
		return result;
	}
}
