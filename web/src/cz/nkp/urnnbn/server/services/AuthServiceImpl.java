package cz.nkp.urnnbn.server.services;

import cz.nkp.urnnbn.client.services.AuthService;
import cz.nkp.urnnbn.shared.dto.UserDTO;

public class AuthServiceImpl extends AbstractService implements AuthService {

	private static final long serialVersionUID = 4860344375822437178L;

	@Override
	public UserDTO getActiveUser() {
		return super.getActiveUser();
	}
}
