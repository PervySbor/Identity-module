package identity.module.utils;

import identity.module.exceptions.FailedToHashException;
import identity.module.utils.config.ConfigReader;


import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class SecurityManager {

    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 128;
    private static final byte[] SECRET_KEY;
    static {
        SECRET_KEY = ConfigReader.getStringValue("ENCRYPTION_PASSWORD").getBytes();
    }

    public static String hashString(String str)
        throws FailedToHashException{
        try {
            KeySpec spec = new PBEKeySpec((str).toCharArray(), SECRET_KEY, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e){
            throw (FailedToHashException) new FailedToHashException("").initCause(e);
        }
    }

    public static String hashJWT(String header, String payload) throws NoSuchAlgorithmException, InvalidKeyException {
        String data = header + "." + payload;
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY, "HmacSHA256");
        mac.init(keySpec);

        byte[] hmacBytes = mac.doFinal(data.getBytes());
        String signature = Base64.getUrlEncoder().encodeToString(hmacBytes);
        data = Base64.getUrlEncoder().encodeToString(data.getBytes());
        return data + "." + signature;
    }


//    public static String encryptPassword(String password) throws FailedToEncryptException {
//        try {
//            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
//            Cipher cipher = Cipher.getInstance("AES");
//            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
//            byte[] encryptedBytes = cipher.doFinal(password.getBytes());
//            return Base64.getEncoder().encodeToString(encryptedBytes); // Кодирование для удобного хранения
//        } catch (Exception e) {
//            throw (FailedToEncryptException) new FailedToEncryptException("").initCause(e);
//        }
//    }
//
//    public static String decryptPassword(String encryptedPassword) throws FailedToDecryptException {
//        try {
//        SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
//        Cipher cipher = Cipher.getInstance("AES");
//        cipher.init(Cipher.DECRYPT_MODE, secretKey);
//        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedPassword));
//        return new String(decryptedBytes);
//    } catch (Exception e) {
//        throw (FailedToDecryptException) new FailedToDecryptException("").initCause(e);
//    }
//    }
}
