/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.webcommon.security;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.access.event.AbstractAuthorizationEvent;
import org.springframework.security.access.event.AuthorizationFailureEvent;
import org.springframework.security.access.event.AuthorizedEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.core.Authentication;

/**
 *
 * @author Martin Řehánek 
 */
public class SecurityListener implements ApplicationListener<ApplicationEvent> {

    static final Logger logger = Logger.getLogger(SecurityListener.class.getName());

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof AbstractAuthenticationEvent) {
          Authentication authentication = ((AbstractAuthenticationEvent) event).getAuthentication();
            if (event instanceof AbstractAuthenticationFailureEvent) {
                logger.log(Level.WARNING, "{0}: login={1}: {2}", new Object[]{event.getClass().getSimpleName(), authentication.getName(), authentication.getDetails().toString()});
            } else {
                logger.log(Level.FINE, "{0}: login={1}", new Object[]{event.getClass().getSimpleName(), authentication.getName()});
            }
        } else if (event instanceof AbstractAuthorizationEvent) {
            Object source = ((AbstractAuthorizationEvent) event).getSource();
            if (event instanceof AuthorizationFailureEvent) {
                String login = ((AuthorizationFailureEvent) event).getAuthentication().getName();
                logger.log(Level.WARNING, "{0}: login={1}: {2}", new Object[]{event.getClass().getSimpleName(), login, source.toString()});
            } else if (event instanceof AuthorizedEvent) {
                String login = ((AuthorizedEvent) event).getAuthentication().getName();
                logger.log(Level.FINE, "{0}: login={1}: {2}", new Object[]{event.getClass().getSimpleName(), login, source.toString()});
            } else {
                logger.log(Level.FINE, "{0}: {1}", new Object[]{event.getClass().getSimpleName(), source.toString()});
            }
        }
    }
}
