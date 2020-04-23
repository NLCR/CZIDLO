package cz.nkp.urnnbn.api_client.v5.utils;

import java.nio.charset.StandardCharsets;

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
            String base64Encoded = base64Encode(concatenated);
            basicAccessAuthToken = "Basic " + base64Encoded;
        }
        return basicAccessAuthToken;
    }

    private String base64Encode(String concatenated) {
        byte[] bytes = concatenated.getBytes();
        //funguje do javy <=7, v novejsi jave nelze zkompilovat
        //return new sun.misc.BASE64Encoder().encode(bytes);
        //funguje od javy 8+
        return new String(java.util.Base64.getEncoder().encode(bytes), StandardCharsets.UTF_8);
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

}
