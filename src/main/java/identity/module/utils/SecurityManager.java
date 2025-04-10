package identity.module.utils;

import identity.module.exceptions.FailedToDecryptException;
import identity.module.exceptions.FailedToEncryptException;
import identity.module.exceptions.FatalException;
import identity.module.utils.config.ConfigReader;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Level;

public class SecurityManager {
    private static final String SECRET_KEY;
    static {
        SECRET_KEY = ConfigReader.getStringValue("ENCRYPTION_PASSWORD");
    }

    public static String encryptPassword(String password) throws FailedToEncryptException {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(password.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes); // Кодирование для удобного хранения
        } catch (Exception e) {
            throw (FailedToEncryptException) new FailedToEncryptException("").initCause(e);
        }
    }

    public static String decryptPassword(String encryptedPassword) throws FailedToDecryptException {
        try {
        SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedPassword));
        return new String(decryptedBytes);
    } catch (Exception e) {
        throw (FailedToDecryptException) new FailedToDecryptException("").initCause(e);
    }
    }
}
