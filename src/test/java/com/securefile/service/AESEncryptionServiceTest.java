package com.securefile.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class AESEncryptionServiceTest {

    private AESEncryptionService service;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        service = new AESEncryptionService();
    }

    @Test
    void testEncryptionDecryptionRoundTrip() throws IOException {
        // 1. Prepare original data
        Path sourcePath = tempDir.resolve("original.txt");
        String originalContent = "TEST Security Task - Confidential Data 12345!";
        Files.writeString(sourcePath, originalContent);

        Path encryptedPath = tempDir.resolve("encrypted.txt");
        Path decryptedPath = tempDir.resolve("decrypted.txt");

        // 2. Execute Encryption
        service.encrypt(sourcePath.toFile(), encryptedPath.toString());

        assertTrue(Files.exists(encryptedPath), "Encrypted file should be created");

        byte[] originalBytes = Files.readAllBytes(sourcePath);
        byte[] encryptedBytes = Files.readAllBytes(encryptedPath);
        // cheack that the file is encrypted
        assertFalse(java.util.Arrays.equals(originalBytes, encryptedBytes), "Encrypted content should be different from original");
        assertTrue(encryptedBytes.length > originalBytes.length, "Encrypted file should be larger due to IV overhead");
        assertTrue(Files.size(encryptedPath) > Files.size(sourcePath), "Encrypted file should be larger due to IV overhead");

        // 3. Execute Decryption
        service.decrypt(encryptedPath.toFile(), decryptedPath.toString());

        // 4. Verify Integrity
        assertTrue(Files.exists(decryptedPath), "Decrypted file should be created");
        String recoveredContent = Files.readString(decryptedPath);
        assertEquals(originalContent, recoveredContent, "Decrypted content must match original exactly");
    }

    @Test
    void testDecryptThrowsExceptionForInvalidFile() throws IOException {
        // Create a file too small to contain a 16-byte IV
        Path tinyFile = tempDir.resolve("tiny.bin");
        Files.write(tinyFile, new byte[]{1, 2, 3});

        assertThrows(RuntimeException.class, () -> {
            service.decrypt(tinyFile.toFile(), tempDir.resolve("out.txt").toString());
        }, "Should throw exception if file is too small to be a valid encrypted file");
    }
}
