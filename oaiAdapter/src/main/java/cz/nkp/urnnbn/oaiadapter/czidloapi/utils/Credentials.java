package cz.nkp.urnnbn.oaiadapter.czidloapi.utils;

public class Credentials {

    private final String login;
    private final String password;
    private String basicAccessAuthToken = null;

    public Credentials(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getBasicAccessAuthorizationHeader() {
        if (basicAccessAuthToken == null) {
            String concatenated = login + ":" + password;
            String base64Encoded = new sun.misc.BASE64Encoder().encode(concatenated.getBytes());
            basicAccessAuthToken = "Basic " + base64Encoded;
        }
        return basicAccessAuthToken;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

}
