package cz.nkp.urnnbn.czidlo_web_api.api.users.user_manager;

import cz.nkp.urnnbn.czidlo_web_api.api.users.core.User;
import cz.nkp.urnnbn.czidlo_web_api.api.users.core.UserDetails;

import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

public class UserInMemoryRepo {
    private static UserInMemoryRepo instance;

    private static final SortedMap<Long, User> users = new TreeMap<>();
    private static final AtomicLong nextUserId = new AtomicLong(0);

    private UserInMemoryRepo() {
    }

    public static UserInMemoryRepo getInstance() {
        if (instance == null) {
            instance = new UserInMemoryRepo();
            fill();
        }
        return instance;
    }

    private static void fill() {
        if (!instance.getAll().isEmpty()) {
            return;
        }
        //create some default users
        UserDetails u0 = instance.create("Admin", "admin@mail.com", hashPassword("abcdefgh"), true);
        u0.addRegistrarRight("ik");
        UserDetails u1 = instance.create("User1", "user1@mail.com", hashPassword("asdfghjk"), false);
        u1.addRegistrarRight("abe301");
        UserDetails u2 = instance.create("User2", "user2@mail.com", hashPassword("qwertyui"), false);
        u2.addRegistrarRight("ik");
        u2.addRegistrarRight("jig503");
        UserDetails u3 = instance.create("dummyUser", "dummy@mail.com", hashPassword("pass_pass_pass"), true);

    }

    public UserDetails create(String login, String email, String password, boolean isAdmin) {
        User user = new User();
        user.setId(nextUserId.getAndIncrement());
        user.setLogin(login);
        user.setEmail(email);
        user.setPassword(password);
        user.setAdmin(isAdmin);
        Date date = new Date();
        user.setCreated(date);
        user.setModified(date);
        users.put(user.getId(), user);

        return UserDetails.fromUser(user);
    }

    public UserDetails getById(long id){
        return UserDetails.fromUser(users.get(id));
    }

    private User getFullUserById(long id){
        return users.get(id);
    }

    public UserDetails getByLogin(String login){
        return UserDetails.fromUser(users.values().stream().filter(x -> x.getLogin().equals(login)).findFirst().orElse(null));
    }

    public UserDetails getByEmail(String email){
        return UserDetails.fromUser(users.values().stream().filter(x -> x.getEmail().equals(email)).findFirst().orElse(null));
    }

    public User getFullUserByEmail(String email){
        return users.values().stream().filter(x -> x.getEmail().equals(email)).findFirst().orElse(null);
    }

    public List<UserDetails> getAll(){
        return users.values().stream().map(UserDetails::fromUser).toList();
    }

    public UserDetails update(long id, String login, String email, String password, boolean isAdmin) {
        User user = getFullUserById(id);
        user.setLogin(login);
        user.setEmail(email);
        if (password != null && !password.isEmpty()) {
            user.setPassword(password);
        }
        user.setAdmin(isAdmin);
        user.setModified(new Date());

        return UserDetails.fromUser(user);
    }

    public UserDetails addRegistrarRightByCode(long id, String registrarCode) {
        UserDetails user = getById(id);
        user.addRegistrarRight(registrarCode);
        return user;
    }

    public UserDetails removeRegistrarRightByCode(long id, String registrarCode) {
        UserDetails user = getById(id);
        user.removeRegistrarRight(registrarCode);
        return user;
    }

    public List<String> getAllRegistrarRights(long id) {
        UserDetails user = getById(id);
        return user.getRegistrarRights();
    }

    public void delete(long id){
        users.remove(id);
    }

    //just for something to call when storing a password
    public static String hashPassword(String password) {
        char[] p = password.toCharArray();

        int hash = 7;

        StringBuilder sb = new StringBuilder();
        for (char c : p) {
            if (c % 2 == 0) {
                sb.append((char) (c + hash));
            } else {
                sb.append((char) (c - hash));
            }
        }
        return sb.toString();
    }
}