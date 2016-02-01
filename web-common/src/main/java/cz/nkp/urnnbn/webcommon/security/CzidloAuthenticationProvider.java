/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.webcommon.security;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.services.AuthenticationService;
import cz.nkp.urnnbn.services.Services;

/**
 * 
 * @author Martin Řehánek
 */
public class CzidloAuthenticationProvider implements AuthenticationProvider {

    private static final Logger logger = Logger.getLogger(CzidloAuthenticationProvider.class.getName());
    static final List<GrantedAuthority> ADMIN = new ArrayList<GrantedAuthority>();
    static final List<GrantedAuthority> USER = new ArrayList<GrantedAuthority>();

    static {
        ADMIN.add(new GrantedAuthorityImpl("ROLE_ADMIN"));
        USER.add(new GrantedAuthorityImpl("ROLE_USER"));
    }

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        String login = ((String) auth.getPrincipal());
        String password = ((String) auth.getCredentials());
        AuthenticationService ser = Services.instanceOf().authenticationService();
        User autheticated = ser.autheticatedUserOrNull(login, password);
        if (autheticated == null) {
            throw new BadCredentialsException("Bad Credentials");
        } else {
            MemoryPasswordsStorage.instanceOf().storePassword(login, password);
        }
        if (autheticated.isAdmin()) {
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
