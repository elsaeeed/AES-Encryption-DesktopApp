package com.securefile.service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;

public class KeyFactory {
    private static final Path KEYSTORE_PATH = Path.of("security.p12");
    // for production ready application, you must take Password from the user
    // or store it with in the OS.
    private static final Path PASS_FILE_PATH = Path.of(".store_pass"); //hidden file
    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 256;
    private static final String KEY_ALIAS = "main-secret-key";
    private static final String KEYSTORE_TYPE = "PKCS12";

    public static void generateAndSaveKey() throws Exception {
        char[] password = null;
        try {
            password = getStorePassword();
            // Generate the AES Key
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(KEY_SIZE);
            SecretKey secretKey = keyGen.generateKey();

            // Create a KeyStore (PKCS12 type)
            KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
            keyStore.load(null, password); // Initialize empty
            // Set the key into the KeyStore
            KeyStore.SecretKeyEntry entry = new KeyStore.SecretKeyEntry(secretKey);
            KeyStore.PasswordProtection protection = new KeyStore.PasswordProtection(password);
            keyStore.setEntry(KEY_ALIAS, entry, protection);
            // Save KeyStore to disk
            try (FileOutputStream fos = new FileOutputStream(KEYSTORE_PATH.toFile())) {
                keyStore.store(fos, password);
            }
            System.out.println("Secure KeyStore created at " + KEYSTORE_PATH.toAbsolutePath());
        }finally {
            if (password != null) {
                java.util.Arrays.fill(password, '0');
            }
        }
    }

    public static SecretKey getKey() {
        char[] password = null;
        try {
            password = getStorePassword();
            if (!Files.exists(KEYSTORE_PATH)) {
                generateAndSaveKey();
            }
            // Load the KeyStore
            KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
            try (FileInputStream fis = new FileInputStream(KEYSTORE_PATH.toFile())) {
                keyStore.load(fis, password);
            }
            // Retrieve the key
            var protection = new KeyStore.PasswordProtection(password);
            var entry = (KeyStore.SecretKeyEntry) keyStore.getEntry(KEY_ALIAS, protection);

            return entry.getSecretKey();
        } catch (Exception e) {
            throw new RuntimeException("Security Error: Unable to access secure KeyStore.", e);
        }finally {
            if (password != null) {
                java.util.Arrays.fill(password, '0');
            }
        }
    }

    private static char[] getStorePassword() throws IOException {
        if (!Files.exists(PASS_FILE_PATH)) {
            // Generate a random password for the first run
            String newPass = java.util.UUID.randomUUID().toString();
            Files.writeString(PASS_FILE_PATH, newPass);
            return newPass.toCharArray();
        }
        return Files.readString(PASS_FILE_PATH).trim().toCharArray();
    }
}

