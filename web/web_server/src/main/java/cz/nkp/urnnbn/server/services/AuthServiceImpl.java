package cz.nkp.urnnbn.server.services;

import cz.nkp.urnnbn.client.services.AuthService;
import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.shared.dto.UserDTO;
import cz.nkp.urnnbn.shared.exceptions.ServerException;
import cz.nkp.urnnbn.utils.CryptoUtils;
import cz.nkp.urnnbn.webcommon.security.MemoryPasswordsStorage;

import java.util.logging.Level;
import java.util.logging.Logger;

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

    @Override
    public Boolean checkPasswordMatch(String login, String password) throws ServerException {
        try {
            User user = readService.userByLogin(login);
            String newHash = CryptoUtils.createSha256Hash(password, user.getPasswordSalt());
            return user.getPasswordHash().equals(newHash);
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    @Override
    public void changePassword(String login, String newPassword) throws ServerException {
        try {
            User user = readService.userByLogin(login);
            String newSalt = CryptoUtils.generateSalt();
            String newHash = CryptoUtils.createSha256Hash(newPassword, newSalt);
            user.setPasswordSalt(newSalt);
            user.setPasswordHash(newHash);
            updateService.updateUserSelf(user, login);
            MemoryPasswordsStorage.instanceOf().storePassword(login, newPassword);
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }
}
