/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.exceptions;

/**
 *
 * @author Martin Řehánek
 */
public class RecordNotFoundException extends PersistenceException {

    private String tableName;

    public RecordNotFoundException(String tableName) {
        super("table:" + tableName);
        this.tableName = tableName;
    }

    public RecordNotFoundException() {
    }
//    private final String tableName;
//    private final Object identifier;
//
//    /**
//     * Creates a new instance of <code>RecordNotFoundException</code> without detail message.
//     */
//    public RecordNotFoundException(String tableName, Object identifier) {
//        this.tableName = tableName;
//        this.identifier = identifier;
//    }

    public String getTableName() {
        return tableName;
    }
}
