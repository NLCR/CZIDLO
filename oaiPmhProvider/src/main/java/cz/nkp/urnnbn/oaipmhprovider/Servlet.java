/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider;

import cz.nkp.urnnbn.oaipmhprovider.response.OaiErrorResponse;
import cz.nkp.urnnbn.oaipmhprovider.response.OaiVerbResponse;
import cz.nkp.urnnbn.oaipmhprovider.response.listRequests.ResumptionTokenManager;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.dom4j.Document;

/**
 *
 * @author Martin Řehánek (rehan at mzk.cz)
 */
public class Servlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(Servlet.class.getName());

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * 
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     * @throws ServletException
     *             if a servlet-specific error occurs
     * @throws IOException
     *             if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.log(Level.INFO, "{0}?{1}", new Object[] { request.getRequestURI(), request.getQueryString() });
        response.setContentType("text/xml;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            Map<String, String[]> parameterMap = getParameterMap(request);
            Document responseDoc = null;
            String verbStr = null;
            try {
                verbStr = getVerbStr(parameterMap);
                parameterMap.remove("verb");
                OaiVerbResponse verbResponse = VerbFactory.getVerbResponse(verbStr, parameterMap);
                responseDoc = verbResponse.build();
            } catch (OaiException ex) {
                OaiErrorResponse errorResponse = new OaiErrorResponse(verbStr, parameterMap, ex.getCode(), ex.getMessage());
                responseDoc = errorResponse.build();
                logger.log(Level.WARNING, "{0}: {1}", new Object[] { ex.getCode().toString(), ex.getMessage() });
            }
            printDoc(out, responseDoc);
            ResumptionTokenManager.clearOldResumptionTokens();
        } finally {
            out.close();
        }
    }

    private static String getVerbStr(Map<String, String[]> parameters) throws OaiException {
        String[] values = parameters.get("verb");
        if (values == null) {
            throw new OaiException(ErrorCode.badVerb, "Missing verb");
        }
        if (values.length != 1) {
            throw new OaiException(ErrorCode.badVerb, "Multiple verbs(" + values.length + ")");
        }
        return values[0];
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     * 
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     * @throws ServletException
     *             if a servlet-specific error occurs
     * @throws IOException
     *             if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * 
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     * @throws ServletException
     *             if a servlet-specific error occurs
     * @throws IOException
     *             if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     * 
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "OAI-PMH Provider";
    }// </editor-fold>

    private void printDoc(PrintWriter out, Document responseDoc) {
        // TODO: optimalizovat
        String string = responseDoc.asXML().toString();
        out.print(string);
    }

    private Map<String, String[]> getParameterMap(HttpServletRequest request) {
        return new HashMap<String, String[]>(request.getParameterMap());
    }
}
