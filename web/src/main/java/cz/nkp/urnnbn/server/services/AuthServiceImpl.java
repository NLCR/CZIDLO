package cz.nkp.urnnbn.server.services;

import java.util.logging.Level;
import java.util.logging.Logger;

import cz.nkp.urnnbn.client.services.AuthService;
import cz.nkp.urnnbn.shared.dto.UserDTO;
import cz.nkp.urnnbn.shared.exceptions.ServerException;

public class AuthServiceImpl extends AbstractService implements AuthService {

	private static final long serialVersionUID = 4860344375822437178L;
	private static final Logger logger = Logger.getLogger(AuthServiceImpl.class.getName());

	@Override
	public UserDTO getLoggedUser() throws ServerException {
		try {
			return super.getActiveUser();
		} catch (Throwable e) {
			logger.log(Level.SEVERE, null, e);
			throw new ServerException(e.getMessage());
		}
	}
}
