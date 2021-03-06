/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.webcommon.security;

import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.services.AuthenticationService;
import cz.nkp.urnnbn.services.Services;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Martin Řehánek
 */
public class CzidloAuthenticationProvider implements AuthenticationProvider {

    private static final Logger logger = Logger.getLogger(CzidloAuthenticationProvider.class.getName());
    static final List<GrantedAuthority> ADMIN = new ArrayList<>();
    static final List<GrantedAuthority> USER = new ArrayList<>();

    static {
        ADMIN.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        USER.add(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        String login = ((String) auth.getPrincipal());
        String password = ((String) auth.getCredentials());
        AuthenticationService ser = Services.instanceOf().authenticationService();
        User authenticated = ser.autheticatedUserOrNull(login, password);
        if (authenticated == null) {
            throw new BadCredentialsException("Bad Credentials");
        } else {
            MemoryPasswordsStorage.instanceOf().storePassword(login, password);
        }
        if (authenticated.isAdmin()) {
            return new UsernamePasswordAuthenticationToken(auth.getName(), auth.getCredentials(), ADMIN);
        } else {
            return new UsernamePasswordAuthenticationToken(auth.getName(), auth.getCredentials(), USER);
        }
    }

    @Override
    public boolean supports(Class<?> type) {
        return true;
    }
}
