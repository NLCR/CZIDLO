package cz.nkp.urnnbn.databaseUpgrader;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.impl.DatabaseConnectorFactory;
import cz.nkp.urnnbn.core.persistence.impl.DatabaseDriver;
import cz.nkp.urnnbn.utils.CryptoUtils;

public class Main {

    private static final String USAGE = "USAGE: java -jar databaseUpgrader.jar HOST PORT DATABASE LOGIN PASSWORD";
    private static final String TABLE_NAME = "useraccount";
    private static final String ATTR_ID = "id";
    private static final String ATTR_LOGIN = "login";
    private static final String ATTR_PASS = "passHash";
    private static final String ATTR_SALT = "passSalt";

    /**
     * @param args
     */
    public static void main(String[] args) {
        // generateSaltAndHash("admin");
        try {
            if (args.length != 5) {
                throw new Exception("illegal number of parameters");
            }
            String host = args[0];
            int port = parsePort(args[1]);
            String database = args[2];
            String login = args[3];
            String password = args[4];
            DatabaseConnector connector = DatabaseConnectorFactory.getConnector(DatabaseDriver.POSTGRES, host, database, port, login, password);
            transformPaswordsToHashes(connector.getConnection());
        } catch (Throwable e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            System.err.println(USAGE);
        }
    }

    private static void generateSaltAndHash(String pass) {
        try {
            String salt = CryptoUtils.generateSalt();
            String passwordHash = CryptoUtils.createSha256Hash(pass, salt);
            System.out.println("password: " + pass);
            System.out.println("salt: " + salt);
            System.out.println("hash: " + passwordHash);
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static int parsePort(String string) throws Exception {
        try {
            int result = Integer.valueOf(string);
            if (result <= 0) {
                throw new IllegalArgumentException("port must be positive number");
            }
            return result;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("port is not a number");
        }
    }

    private static void transformPaswordsToHashes(Connection connection) throws SQLException, NoSuchAlgorithmException {
        Statement stm = connection.createStatement();
        ResultSet results = stm.executeQuery("SELECT " + ATTR_ID + "," + ATTR_LOGIN + "," + ATTR_PASS + " from " + TABLE_NAME);
        int counter = 0;
        while (results.next()) {
            int id = results.getInt(1);
            String login = results.getString(2);
            String pass = results.getString(3);
            fixAccount(id, pass, connection);
            counter++;
        }
        System.out.println("Fixed " + counter + " table records");
    }

    private static void fixAccount(int id, String pass, Connection connection) throws NoSuchAlgorithmException, SQLException {
        String salt = CryptoUtils.generateSalt();
        // System.out.println("salt:" + salt + " (" + salt.length() + ")");
        String passwordHash = CryptoUtils.createSha256Hash(pass, salt);
        // System.out.println("hash:" + passwordHash + " (" +
        // passwordHash.length() + ")");
        updateDatabaseRecord(id, salt, passwordHash, connection);
    }

    private static void updateDatabaseRecord(int id, String salt, String passwordHash, Connection connection) throws SQLException {
        PreparedStatement stm = connection.prepareStatement("UPDATE " + TABLE_NAME + " SET " + ATTR_PASS + "= ?, " + ATTR_SALT + "=? WHERE "
                + ATTR_ID + "=?");
        stm.setString(1, passwordHash);
        stm.setString(2, salt);
        stm.setInt(3, id);
        int updated = stm.executeUpdate();
        if (updated == 0) {
            throw new RuntimeException("Failed to update account record");
        }
    }

}
