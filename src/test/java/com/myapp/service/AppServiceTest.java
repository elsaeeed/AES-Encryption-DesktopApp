package com.myapp.service;

import com.securefile.service.AppService;
import com.securefile.service.EncryptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class AppServiceTest {

    private AppService appService;
    private MockEncryptionService mockEncryptionService;
    private File dummyInput;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        mockEncryptionService = new MockEncryptionService();
        dummyInput = tempDir.resolve("input.txt").toFile();


        appService = new AppService(
                mockEncryptionService
        );
        appService.setInputFile(dummyInput);
        appService.setOutputPath(tempDir.resolve("output.dat").toString());
    }

    @Test
    void testSetOutputPathThrowsExceptionForSameFile() {
        assertThrows(IllegalArgumentException.class, () -> {
            appService.setOutputPath(dummyInput.getAbsolutePath());
        }, "Should throw exception when output path equals input path");
    }

    @Test
    void testDecryptFileCallsEncryptionService() {
        appService.decryptFile();
        assertTrue(mockEncryptionService.decryptCalled, "AppService should call decrypt on the EncryptionService");
    }

    // Mock class to isolate AppService.
    @Test
    void testEncryptFileCallsEncryptionService() {
        appService.encryptFile();
        assertTrue(mockEncryptionService.encryptCalled, "AppService should call encrypt on the EncryptionService");
    }
    private static class MockEncryptionService implements EncryptionService {

        boolean encryptCalled = false;
        boolean decryptCalled = false;

        @Override
        public void encrypt(File file, String outputDist) {
            encryptCalled = true;
        }

        @Override
        public void decrypt(File file, String outputDist) {
            decryptCalled = true;
        }
    }
}