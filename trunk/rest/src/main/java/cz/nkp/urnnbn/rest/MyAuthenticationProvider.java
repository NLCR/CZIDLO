/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.rest;

import java.util.ArrayList;
import java.util.List;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

/**
 *
 * @author Martin Řehánek
 */
public class MyAuthenticationProvider implements AuthenticationProvider {

    static final List<GrantedAuthority> ADMIN = new ArrayList<GrantedAuthority>();
    static final List<GrantedAuthority> USER = new ArrayList<GrantedAuthority>();

    static {
        ADMIN.add(new GrantedAuthorityImpl("ROLE_ADMIN"));
        USER.add(new GrantedAuthorityImpl("ROLE_USER"));
    }
    private static List<User> admins = adminList();
    private static List<User> users = userList();

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        User user = new User((String) auth.getPrincipal(), (String) auth.getCredentials());
        System.err.println(user);
//        System.err.println("name:" + auth.getName());
//        System.err.println("details:" + auth.getDetails());
//        WebAuthenticationDetails details = (WebAuthenticationDetails) auth.getDetails();
//        System.err.println("authenticated: " + auth.isAuthenticated());
        if (admins.contains(user)) {
            return new UsernamePasswordAuthenticationToken(auth.getName(),
                    auth.getCredentials(), ADMIN);
        } else if (users.contains(user)) {
            return new UsernamePasswordAuthenticationToken(auth.getName(),
                    auth.getCredentials(), USER);
        }
        throw new BadCredentialsException("Bad Credentials");
    }

    @Override
    public boolean supports(Class<?> type) {
        return true;
    }

    private static List<User> adminList() {
        List<User> result = new ArrayList<User>();
        result.add(new User("admin", "adminpass"));
        return result;
    }

    private static List<User> userList() {
        List<User> result = new ArrayList<User>();
        result.add(new User("user", "userpass"));
        return result;
    }
}
