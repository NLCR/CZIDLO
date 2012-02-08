/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.rest;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * REST Web Service
 *
 * @author Martin Řehánek
 */
@Path("/user")
public class UserResource {

    @Context
    private UriInfo context;

    @GET
    @Produces("text/plain")
    public String getXml() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        //pokud nebude prihlasen, bude tam 'anonymousUser'
        return "<user>"
                + "<login>" + username + "</login>"
                + "</user>";
    }

    @PUT
    @Consumes("application/xml")
    public void putXml(String content) {
    }
}
