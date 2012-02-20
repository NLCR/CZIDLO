/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.rest;

import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.impl.DatabaseConnectorFactory;
import cz.nkp.urnnbn.services.AuthenticationService;
import cz.nkp.urnnbn.services.impl.AuthenticationServiceImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

/**
 *
 * @author Martin Řehánek
 */
public class MyAuthenticationProvider implements AuthenticationProvider {

    private static final Logger logger = Logger.getLogger(MyAuthenticationProvider.class.getName());
    static final List<GrantedAuthority> ADMIN = new ArrayList<GrantedAuthority>();
    static final List<GrantedAuthority> USER = new ArrayList<GrantedAuthority>();
    private static DatabaseConnector connector = DatabaseConnectorFactory.getConnector();

    static {
        ADMIN.add(new GrantedAuthorityImpl("ROLE_ADMIN"));
        USER.add(new GrantedAuthorityImpl("ROLE_USER"));
    }

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        User user = new User();
        user.setLogin((String) auth.getPrincipal());
        user.setPassword((String) auth.getCredentials());
        logger.log(Level.INFO, "provided:{0}", user.toString());
//        logger.log(Level.INFO, "password:{0}", user.getPassword());
        AuthenticationService ser = new AuthenticationServiceImpl(connector);
        User autheticated = ser.autheticatedUserOrNull(user);
        if (autheticated == null) {
            throw new BadCredentialsException("Bad Credentials");
        } else {
            logger.log(Level.INFO, "authenticated:{0}", autheticated.toString());
        }
        if (autheticated.isAdmin()) {
            return new UsernamePasswordAuthenticationToken(auth.getName(),
                    auth.getCredentials(), ADMIN);
        } else {
            return new UsernamePasswordAuthenticationToken(auth.getName(),
                    auth.getCredentials(), USER);
        }
    }

    @Override
    public boolean supports(Class<?> type) {
        return true;
    }
}
