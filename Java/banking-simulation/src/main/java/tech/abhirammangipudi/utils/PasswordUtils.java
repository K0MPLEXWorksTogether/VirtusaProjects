package tech.abhirammangipudi.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtils {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    private static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    public static String hashPassword(String password) {
        try {
            byte[] salt = generateSalt();

            PBEKeySpec spec = new PBEKeySpec(
                    password.toCharArray(),
                    salt,
                    ITERATIONS,
                    KEY_LENGTH);

            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();

            return ITERATIONS + ":" +
                    Base64.getEncoder().encodeToString(salt) + ":" +
                    Base64.getEncoder().encodeToString(hash);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public static boolean verifyPassword(String password, String stored) {
        try {
            String[] parts = stored.split(":");
            int iterations = Integer.parseInt(parts[0]);

            byte[] salt = Base64.getDecoder().decode(parts[1]);
            byte[] originalHash = Base64.getDecoder().decode(parts[2]);

            PBEKeySpec spec = new PBEKeySpec(
                    password.toCharArray(),
                    salt,
                    iterations,
                    originalHash.length * 8);

            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] testHash = factory.generateSecret(spec).getEncoded();

            return slowEquals(originalHash, testHash);

        } catch (Exception e) {
            return false;
        }
    }

    private static boolean slowEquals(byte[] a, byte[] b) {
        int diff = a.length ^ b.length;
        for (int i = 0; i < a.length && i < b.length; i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }
}