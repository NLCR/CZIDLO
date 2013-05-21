package cz.nkp.urnnbn.server.dtoTransformation;

import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.shared.dto.UserDTO;
import cz.nkp.urnnbn.shared.dto.UserDTO.ROLE;

public class DtoToUserTransformer {

	private final UserDTO dto;

	public DtoToUserTransformer(UserDTO dto) {
		this.dto = dto;
	}

	public User transform() {
		User result = new User();
		result.setEmail(dto.getEmail());
		result.setId(dto.getId());
		result.setLogin(dto.getLogin());
		result.setPassword(dto.getPassword());
		result.setAdmin(dto.getRole() == ROLE.SUPER_ADMIN);
		return result;
	}
}
