/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.rest;

import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.dto.SourceDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.rest.exceptions.InternalException;
import cz.nkp.urnnbn.xml.builders.ArchiverBuilder;
import cz.nkp.urnnbn.xml.builders.DigitalDocumentBuilder;
import cz.nkp.urnnbn.xml.builders.DigitalInstanceBuilder;
import cz.nkp.urnnbn.xml.builders.DigitalDocumentIdentifiersBuilder;
import cz.nkp.urnnbn.xml.builders.IntelectualEntityBuilder;
import cz.nkp.urnnbn.xml.builders.RegistrarBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 * REST Web Service
 *
 * @author Martin Řehánek
 */
public class DigitalDocumentResource extends Resource {

    private static final String PARAM_ACTION = "action";
    private static final String PARAM_FORMAT = "format";
    private static final String PARAM_ADD_DIG_INST = "digitalInstances";
    private final DigitalDocument rep;
    private UrnNbn urn;

    public DigitalDocumentResource(DigitalDocument rep, UrnNbn urn) {
        this.rep = rep;
        this.urn = urn;
    }

    @GET
    @Produces("application/xml")
    public String resolve(
            @DefaultValue("decide") @QueryParam(PARAM_ACTION) String actionStr,
            @DefaultValue("html") @QueryParam(PARAM_FORMAT) String formatStr,
            @DefaultValue("true") @QueryParam(PARAM_ADD_DIG_INST) String addDigitalInstancesStr) {
        Action action = Parser.parseAction(actionStr, PARAM_ACTION);
        ResponseFormat format = Parser.parseResponseFormat(formatStr, PARAM_FORMAT);
        boolean addDigitalInstances = queryParamToBoolean(addDigitalInstancesStr, PARAM_ADD_DIG_INST, true);
        //TODO: pouzit format. Pokud tam bude nevhodna hodnota (RAW), vyhodit vyjimku
        switch (action) {
            case DECIDE://pokud pochazi z katalogu, pouzij redirect s tim,
            //ze pouzijes pouze DR patrici do DL registratora, ktery vlastni ten katalog
            //pokud se nenajde DR, tak se chovej jako pri SHOW
            case REDIRECT://pokus se o redirekt, asi bez preferenci
            //pokud se nenajde nic, tak zobraz dal.
            case SHOW://na zaklade format se rozhodni, jestli zobrazit v gwt nebo xml
        }

        try {
            if (urn == null) {
                urn = dataAccessService().urnByDigDocId(rep.getId());
            }
            DigitalDocumentIdentifiersBuilder digRepIdentifiersBuilder = digRepIdentifiersBuilder(rep.getId());
            List<DigitalInstanceBuilder> instancesBuilders = addDigitalInstances
                    ? instancesBuilders(rep) : null;
            RegistrarBuilder regBuilder = new RegistrarBuilder(dataAccessService().registrarById(rep.getRegistrarId()), null, null);
            ArchiverBuilder archBuilder = (rep.getRegistrarId() == rep.getArchiverId())
                    ? null : new ArchiverBuilder(dataAccessService().archiverById(rep.getArchiverId()));
            IntelectualEntityBuilder entityBuilder = entityBuilder(rep.getIntEntId());
            DigitalDocumentBuilder builder = new DigitalDocumentBuilder(rep, urn, digRepIdentifiersBuilder, instancesBuilders, regBuilder, archBuilder, entityBuilder);
            return builder.buildRootElement().toXML();
        } catch (DatabaseException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }

    private List<DigitalInstanceBuilder> instancesBuilders(DigitalDocument rep) throws DatabaseException {
        List<DigitalInstance> instances = dataAccessService().instancesByDigDocId(rep.getId());
        List<DigitalInstanceBuilder> result = new ArrayList<DigitalInstanceBuilder>(instances.size());
        for (DigitalInstance instance : instances) {
            DigitalInstanceBuilder builder = new DigitalInstanceBuilder(instance, null, null);
            result.add(builder);
        }
        return result;
    }

    private IntelectualEntityBuilder entityBuilder(long intEntId) throws DatabaseException {
        IntelectualEntity entity = dataAccessService().entityById(intEntId);
        List<IntEntIdentifier> ieIdentfiers = dataAccessService().intEntIdentifiersByIntEntId(intEntId);
        Publication pub = dataAccessService().publicationByIntEntId(intEntId);
        Originator originator = dataAccessService().originatorByIntEntId(intEntId);
        SourceDocument srcDoc = dataAccessService().sourceDocumentByIntEntId(intEntId);
        return IntelectualEntityBuilder.instanceOf(entity, ieIdentfiers, pub, originator, srcDoc);
    }

    @Path("/identifiers")
    public DigitalDocumentIdentifiersResource getIdentifiersResource() {
        return new DigitalDocumentIdentifiersResource(rep);
    }

    @Path("/digitalInstances")
    public DigitalInstancesResource getDigitalInstancesResource() {
        return new DigitalInstancesResource(rep);
    }
}
