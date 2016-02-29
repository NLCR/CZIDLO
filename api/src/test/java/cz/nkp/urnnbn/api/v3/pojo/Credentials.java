package cz.nkp.urnnbn.api.v3.pojo;

public class Credentials {
    public final String login;
    public final String password;

    public Credentials(String login, String password) {
        this.login = login;
        this.password = password;
    }
}
