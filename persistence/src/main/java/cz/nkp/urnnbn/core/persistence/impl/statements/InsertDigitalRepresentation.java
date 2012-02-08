/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.DigitalDocumentDAO;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 *
 * @author Martin Řehánek
 */
public class InsertDigitalRepresentation implements StatementWrapper {

    private final DigitalDocument representation;

    public InsertDigitalRepresentation(DigitalDocument representation) {
        this.representation = representation;
    }

    @Override
    public String preparedStatement() {
        return "INSERT into " + DigitalDocumentDAO.TABLE_NAME
                + "(" + DigitalDocumentDAO.ATTR_ID
                + "," + DigitalDocumentDAO.ATTR_INT_ENT_ID
                + "," + DigitalDocumentDAO.ATTR_REGISTRAR_ID
                + "," + DigitalDocumentDAO.ATTR_ARCHIVER_ID
                + "," + DigitalDocumentDAO.ATTR_CREATED
                + "," + DigitalDocumentDAO.ATTR_UPDATED
                + "," + DigitalDocumentDAO.ATTR_EXTENT
                + "," + DigitalDocumentDAO.ATTR_RESOLUTION
                + "," + DigitalDocumentDAO.ATTR_COLOR_DEPTH
                + "," + DigitalDocumentDAO.ATTR_FINANCED
                + "," + DigitalDocumentDAO.ATTR_CONTRACT_NUMBER
                + ") values(?,?,?,?,?,?,?,?,?,?,?)";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setLong(1, representation.getId());
            st.setLong(2, representation.getIntEntId());
            st.setLong(3, representation.getRegistrarId());
            st.setLong(4, representation.getArchiverId());
            Timestamp now = DateTimeUtils.nowTs();
            st.setTimestamp(5, now);
            st.setTimestamp(6, now);
            st.setString(7, representation.getExtent());
            st.setString(8, representation.getResolution());
            st.setString(9, representation.getColorDepth());
            st.setString(10, representation.getFinancedFrom());
            st.setString(11, representation.getContractNumber());
        } catch (SQLException e) {
            //chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}
