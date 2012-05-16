/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider.response;

import cz.nkp.urnnbn.oaipmhprovider.ErrorCode;
import java.io.IOException;
import java.util.Map;
import org.dom4j.Document;
import org.dom4j.Element;

/**
 *
 * @author Martin Řehánek (rehan at mzk.cz)
 */
public class OaiErrorResponse extends OaiResponse {

    private final ErrorCode code;
    private final String message;

    public OaiErrorResponse(String verbStr, Map<String, String[]> parameterMap, ErrorCode code, String message) throws IOException {
        super(verbStr, parameterMap);
        this.code = code;
        this.message = message;
    }

    public Document build() throws IOException {
        buildOaiHeader();
        addErrorElement();
        return doc;
    }

    private void addErrorElement() {
        Element errorEl = doc.getRootElement().addElement("error");
        errorEl.addAttribute("code", code.toString());
        errorEl.addText(message);
    }
}
