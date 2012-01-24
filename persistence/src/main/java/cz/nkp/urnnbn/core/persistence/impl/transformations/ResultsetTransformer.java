/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.transformations;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Transforms ResultSet into DTO object
 * @author Martin Řehánek
 */
public interface ResultsetTransformer {

    public Object transform(ResultSet resultSet) throws SQLException;
}
