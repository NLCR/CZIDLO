/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api.v2;

import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;

import cz.nkp.urnnbn.api.config.ApiModuleConfiguration;
import cz.nkp.urnnbn.api.v2_v3.AbstractDigitalInstanceResource;
import cz.nkp.urnnbn.api.v3.exceptions.ApiV3Exception;
import cz.nkp.urnnbn.api.v3.exceptions.InternalException;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.xml.commons.XsltXmlTransformer;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalInstanceResource extends AbstractDigitalInstanceResource {

    public DigitalInstanceResource(DigitalInstance instance) {
        super(instance);
    }

    @GET
    @Produces("application/xml")
    @Override
    public String getDigitalInstanceXmlRecord() {
        try {
            try {
                String apiV3XmlRecord = super.getDigitalInstanceApiV3XmlRecord();
                XsltXmlTransformer transformer = ApiModuleConfiguration.instanceOf().getGetDigInstResponseV3ToV2Transformer();
                return transformApiV3ToApiV2ResponseAsString(transformer, apiV3XmlRecord);
            } catch (WebApplicationException e) {
                throw e;
            } catch (Throwable e) {
                logger.log(Level.SEVERE, e.getMessage());
                throw new InternalException(e);
            }
        } catch (ApiV3Exception e) {
            throw new ApiV2Exception(e);
        }
    }

    @DELETE
    @Produces("application/xml")
    public String deactivateDigitalInstance(@Context HttpServletRequest req) {
        try {
            try {
                checkServerNotReadOnly();
                String login = req.getRemoteUser();
                String apiV3XmlRecord = super.deactivateDigitalInstance(login);
                XsltXmlTransformer transformer = ApiModuleConfiguration.instanceOf().getDeactivateDigInstResponseV3ToV2Transformer();
                return transformApiV3ToApiV2ResponseAsString(transformer, apiV3XmlRecord);
            } catch (WebApplicationException e) {
                throw e;
            } catch (Throwable e) {
                logger.log(Level.SEVERE, e.getMessage());
                throw new InternalException(e);
            }
        } catch (ApiV3Exception e) {
            throw new ApiV2Exception(e);
        }
    }
}
