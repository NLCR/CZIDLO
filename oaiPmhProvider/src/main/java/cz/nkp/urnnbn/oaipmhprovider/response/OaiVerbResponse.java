/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider.response;

import cz.nkp.urnnbn.oaipmhprovider.OaiException;
import cz.nkp.urnnbn.oaipmhprovider.tools.ArgumentsChecker;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

/**
 *
 * @author Martin Řehánek (rehan at mzk.cz)
 */
public abstract class OaiVerbResponse extends OaiResponse {

    protected static final Logger logger = Logger.getLogger(OaiVerbResponse.class.getName());
    protected Element rootEl;

    public OaiVerbResponse(String verbStr, Map<String, String[]> parameters) throws IOException {
        super(verbStr, parameters);
    }

    public Document build() throws OaiException, IOException {
        checkParameters();
        logger.fine("parameters checked");
        buildVerbHeader();
        logger.fine("header built");
        createResponse();
        logger.fine("response built");
        return doc;
    }

    abstract String[] getRequiredArguments();

    abstract String[] getOptionalArguments();

    abstract String getExclusiveArgument();

    private void checkParameters() throws OaiException {
        String[] requiredArguments = getRequiredArguments();
        String[] optionalArguments = getOptionalArguments();
        String exclusiveArgument = getExclusiveArgument();
        ArgumentsChecker checker = new ArgumentsChecker(arguments, requiredArguments, optionalArguments, exclusiveArgument);
        checker.run();
    }

    String getArgumentValueIfPresent(String parameter) {
        String[] values = arguments.get(parameter);
        if (values != null) {
            return values[0];
        } else {
            return null;
        }
    }

    abstract void createResponse() throws OaiException, IOException;

    private void buildVerbHeader() throws IOException {
        buildOaiHeader();
        rootEl = doc.getRootElement().addElement(verbStr);
    }
}
