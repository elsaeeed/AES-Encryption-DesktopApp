package com.securefile.service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public class KeyFactory {
    private static final Path keyPath = Path.of("app.key");
    private static final String algorithm = "AES";
    private static final int keySize = 256;


    public static void generateAES256Key() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(algorithm);
        keyGen.init(keySize);
        SecretKey key = keyGen.generateKey();

        String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());

        Files.writeString(keyPath, base64Key);

        System.out.println("Key saved to " + keyPath.toAbsolutePath());
    }

    public static SecretKey getKey() {
        try {
            if (!Files.exists(keyPath)) {
                generateAES256Key();
            }
            String base64Key = Files.readString(keyPath);
            byte[] keyBytes = Base64.getDecoder().decode(base64Key);
            return new SecretKeySpec(keyBytes, algorithm);
        } catch (Exception e) {
            throw new RuntimeException("Security Error: Unable to initialize encryption key.", e);
        }
    }
}

