package cz.nkp.urnnbn.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 
 * @author Martin Řehánek
 * 
 */
public class CryptoUtils {

    private static SecureRandom sr = null;

    public static String createSha256Hash(String pass, String salt) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] bytes = md.digest(pass.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    public static String generateSalt() throws NoSuchAlgorithmException {
        // Always use a SecureRandom generator
        if (sr == null) {
            sr = SecureRandom.getInstance("SHA1PRNG");
        }
        // Create array for salt
        byte[] salt = new byte[16];
        // Get a random salt
        sr.nextBytes(salt);
        // return salt
        return salt.toString();
    }

}
