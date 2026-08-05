package np.gov.digital.citizen.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class NidEncryptionUtil {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    private final SecretKey secretKey;

    // Loads the AES-256 encryption key from environment variable ENCRYPTION_KEY.
//    public NidEncryptionUtil(@Value("${app.encryption.key}") String encryptionKey){
//        byte[] keyBytes = encryptionKey.getBytes(StandardCharsets.UTF_8);
//        if(keyBytes.length != 32){
//            throw new IllegalArgumentException(
//                    "ENCRYPTION_KEY must be exactly 32 character (256 bits). "+ "Current length: "+keyBytes.length
//            );
//        }
//        this.secretKey = new SecretKeySpec(keyBytes, "AES");
//    }
    public NidEncryptionUtil(
            @Value("${app.encryption.key:12345678901234567890123456789012}")
            String encryptionKey) {

        byte[] keyBytes = encryptionKey.getBytes(StandardCharsets.UTF_8);

        if (keyBytes.length != 32) {
            throw new IllegalArgumentException(
                    "ENCRYPTION_KEY must be exactly 32 characters. Current length: "
                            + keyBytes.length
            );
        }

        this.secretKey = new SecretKeySpec(keyBytes, "AES");
    }

    // ENCRYPT
    // Encrypts plaintext NID using AES-256/GCM.
    public String encrypt(String plaintext){
        try{
            // Generate a fresh random IV for every encryption
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            // Prepend IV to ciphertext so we can extract it during decryption
            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + ciphertext.length);
            byteBuffer.put(iv);
            byteBuffer.put(ciphertext);

            return Base64.getEncoder().encodeToString(byteBuffer.array());
        } catch (Exception e){
            throw new RuntimeException("Failed to encrypt NID", e);
        }
    }

    // HASH — for duplicate detection
    public String hash(String plaintext) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(plaintext.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash NID", e);
        }
    }

    // DECRYPT
    // Decrypts an AES-256/GCM encrypted NID.
    public String decrypt(String encryptedBase64) {
        try {
            byte[] decoded = Base64.getDecoder().decode(encryptedBase64);

            // Extract IV (first 12 bytes) and ciphertext (remainder)
            ByteBuffer byteBuffer = ByteBuffer.wrap(decoded);
            byte[] iv = new byte[GCM_IV_LENGTH];
            byteBuffer.get(iv);
            byte[] ciphertext = new byte[byteBuffer.remaining()];
            byteBuffer.get(ciphertext);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            byte[] plaintext = cipher.doFinal(ciphertext);
            return new String(plaintext, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt NID", e);
        }
    }

    // CITIZENSHIP NUMBER SANITIZER
    public String normalizeCitizenshipNo(String rawCitizenshipNo) {
        if (rawCitizenshipNo == null) return null;
        return rawCitizenshipNo.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
    }

    // PRIVATE HELPERS
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
