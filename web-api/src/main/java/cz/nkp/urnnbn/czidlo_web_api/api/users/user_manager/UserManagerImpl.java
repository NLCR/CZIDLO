package cz.nkp.urnnbn.czidlo_web_api.api.users.user_manager;

import cz.nkp.urnnbn.core.DtoBuilder;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.AccessRightException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.BadArgumentException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.DuplicateRecordException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnknownRecordException;
import cz.nkp.urnnbn.czidlo_web_api.api.users.core.UserDetails;
import cz.nkp.urnnbn.services.*;
import cz.nkp.urnnbn.services.exceptions.LoginConflictException;
import cz.nkp.urnnbn.services.exceptions.NotAdminException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;
import cz.nkp.urnnbn.utils.CryptoUtils;

import java.security.NoSuchAlgorithmException;
import java.util.List;

public class UserManagerImpl implements UserManager {

    protected DataAccessService dataAccessService() {
        return Services.instanceOf().dataAccessService();
    }

    protected DataImportService dataImportService() {
        return Services.instanceOf().dataImportService();
    }

    protected DataRemoveService dataRemoveService() {
        return Services.instanceOf().dataRemoveService();
    }

    protected DataUpdateService dataUpdateService() {
        return Services.instanceOf().dataUpdateService();
    }

    @Override
    public UserDetails createUser(String loginOfUserPerformingThisOperation, String login, String email, String password, boolean isAdmin) throws DuplicateRecordException, AccessRightException, BadArgumentException {
        User user = new User();
        user.setLogin(login);
        try {
            user.setPasswordSalt(CryptoUtils.generateSalt());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        user.setPasswordHash(CryptoUtils.createSha256Hash(password, user.getPasswordSalt()));
        user.setAdmin(isAdmin);
        user.setEmail(email);
        try {
            User created = dataImportService().addNewUser(user, loginOfUserPerformingThisOperation);
            return UserDetails.fromUserDto(created, List.of());
        } catch (UnknownUserException e) {
            throw new RuntimeException(e);
        } catch (NotAdminException e) {
            throw new RuntimeException(e);
        } catch (LoginConflictException e) {
            throw new DuplicateRecordException("User with login " + login + " already exists.");
        }
    }

    @Override
    public UserDetails getUser(String loginOfUserPerformingThisOperation, long userId) throws UnknownRecordException, AccessRightException {
        try {
            User userDto = dataAccessService().userById(userId);
            List<Registrar> dtoRegistrars = dataAccessService().registrarsManagedByUser(userDto.getId(), userDto.getLogin());
            return UserDetails.fromUserDto(userDto, dtoRegistrars);
        } catch (UnknownUserException e) {
            throw new UnknownRecordException("Unknown user with id: " + userId);
        } catch (NotAdminException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserDetails getUser(String loginOfUserPerformingThisOperation, String login) throws UnknownRecordException, AccessRightException {
        try {
            User userDto = dataAccessService().userByLogin(login);
            List<Registrar> dtoRegistrars = dataAccessService().registrarsManagedByUser(userDto.getId(), null);
            return UserDetails.fromUserDto(userDto, dtoRegistrars);
        } catch (UnknownUserException e) {
            throw new UnknownRecordException("Unknown user with login: " + login);
        } catch (NotAdminException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<UserDetails> getUsers(String loginOfUserPerformingThisOperation) throws AccessRightException {
        try {
            List<User> dtoUsers = dataAccessService().users(loginOfUserPerformingThisOperation);
            List<UserDetails> users = new java.util.ArrayList<>();
            for (User userDto : dtoUsers) {
                List<Registrar> dtoRegistrars = dataAccessService().registrarsManagedByUser(userDto.getId(), userDto.getLogin());
                users.add(UserDetails.fromUserDto(userDto, dtoRegistrars));
            }
            return users;
        } catch (UnknownUserException e) {
            throw new RuntimeException(e);
        } catch (NotAdminException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserDetails updateUser(String loginOfUserPerformingThisOperation, long userId, String login, String email, boolean isAdmin) throws UnknownRecordException, DuplicateRecordException, AccessRightException, BadArgumentException {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public UserDetails updateUserPassword(String loginOfUserPerformingThisOperation, long userId, String newPassword) throws UnknownRecordException, AccessRightException, BadArgumentException {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public UserDetails addRegistrarRight(String loginOfUserPerformingThisOperation, long userId, String registrarCode) throws UnknownRecordException, AccessRightException {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public UserDetails removeRegistrarRight(String loginOfUserPerformingThisOperation, long userId, String registrarCode) throws UnknownRecordException, AccessRightException {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public List<String> getRegistrarRights(String loginOfUserPerformingThisOperation, long userId) throws UnknownRecordException, AccessRightException {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public void deleteUser(String loginOfUserPerformingThisOperation, long userId) throws UnknownRecordException, AccessRightException {
        throw new RuntimeException("Not implemented yet");
    }
}
