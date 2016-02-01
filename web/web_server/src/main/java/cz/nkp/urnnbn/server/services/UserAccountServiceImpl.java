package cz.nkp.urnnbn.server.services;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.nkp.urnnbn.client.services.UserAccountService;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.server.dtoTransformation.DtoToUserTransformer;
import cz.nkp.urnnbn.server.dtoTransformation.DtoTransformer;
import cz.nkp.urnnbn.server.dtoTransformation.UserDtoTransformer;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;
import cz.nkp.urnnbn.shared.dto.UserDTO;
import cz.nkp.urnnbn.shared.exceptions.ServerException;

public class UserAccountServiceImpl extends AbstractService implements UserAccountService {

    private static final long serialVersionUID = 7347325403481758583L;
    private static final Logger logger = Logger.getLogger(UserAccountServiceImpl.class.getName());

    @Override
    public ArrayList<UserDTO> getAllUsers() throws ServerException {
        try {
            List<User> users = readService.users(getUserLogin());
            return convertUsers(users);
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    private ArrayList<UserDTO> convertUsers(List<User> users) {
        ArrayList<UserDTO> result = new ArrayList<UserDTO>(users.size());
        for (User original : users) {
            result.add(new UserDtoTransformer(original).transform());
        }
        return result;
    }

    @Override
    public UserDTO insertUser(UserDTO user) throws ServerException {
        try {
            checkNotReadOnlyMode();
            User transformed = new DtoToUserTransformer(user).transform();
            User inserted = createService.addNewUser(transformed, getUserLogin());
            return new UserDtoTransformer(inserted).transform();
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    @Override
    public void deleteUser(Long userId) throws ServerException {
        try {
            checkNotReadOnlyMode();
            deleteService.removeUser(userId, getUserLogin());
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    @Override
    public void updateUser(UserDTO user) throws ServerException {
        try {
            checkNotReadOnlyMode();
            User transformed = new DtoToUserTransformer(user).transform();
            updateService.updateUser(transformed, getUserLogin());
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    @Override
    public ArrayList<RegistrarDTO> getAllRegistrars() throws ServerException {
        try {
            List<Registrar> registrars = readService.registrars();
            return transformRegistrars(registrars);
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    @Override
    public ArrayList<RegistrarDTO> getRegistrarsManagedByUser(Long userId) throws ServerException {
        try {
            List<Registrar> registrars = readService.registrarsManagedByUser(userId, getUserLogin());
            return transformRegistrars(registrars);
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    @Override
    public ArrayList<RegistrarDTO> getRegistrarsManagedByUser() throws ServerException {
        try {
            User userByLogin = readService.userByLogin(getUserLogin());
            List<Registrar> registrars = readService.registrarsManagedByUser(userByLogin.getId(), getUserLogin());
            return transformRegistrars(registrars);
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    private ArrayList<RegistrarDTO> transformRegistrars(List<Registrar> registrars) {
        ArrayList<RegistrarDTO> result = new ArrayList<RegistrarDTO>(registrars.size());
        for (Registrar original : registrars) {
            RegistrarDTO transformed = DtoTransformer.transformRegistrar(original);
            result.add(transformed);
        }
        return result;
    }

    @Override
    public void insertRegistrarRight(long userId, long registrarId) throws ServerException {
        try {
            checkNotReadOnlyMode();
            createService.addRegistrarRight(userId, registrarId, getUserLogin());
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    @Override
    public void deleteRegistrarRight(long userId, long registrarId) throws ServerException {
        try {
            checkNotReadOnlyMode();
            deleteService.removeRegistrarRight(userId, registrarId, getUserLogin());
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }
}
