package com.securefile.logic;

import com.securefile.service.EncryptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ProcessManagerTest {

    private ProcessManager processManager;
    private MockEncryptionService mockService;
    
    @TempDir
    Path tempDir; // JUnit 5 built-in for handling temporary files

    @BeforeEach
    void setUp() {
        mockService = new MockEncryptionService();
        processManager = new ProcessManager(mockService);
    }

    @Test
    void testSuggestFileName() throws IOException {
        File file = createTestFile("myDocument.txt", 0);
        processManager.setSrcFile(file);
        
        assertEquals("myDocument", processManager.suggestFileName());
    }

    @Test
    void testGetFileSizeFormatting() throws IOException {
        File smallFile = createTestFile("small.txt", 500);
        processManager.setSrcFile(smallFile);
        assertEquals("500 Bytes", processManager.getFileSize());

        File largeFile = createTestFile("large.txt", 2048);
        processManager.setSrcFile(largeFile);
        assertEquals("2 KB", processManager.getFileSize());
    }

    @Test
    void testExecuteEncryptionBuildsCorrectPath() throws IOException {
        // Arrange
        File srcFile = createTestFile("data.txt", 100);
        String destFolder = tempDir.toString();
        String outputName = "secret";

        processManager.setSrcFile(srcFile);
        processManager.setDestFolder(destFolder);
        processManager.setFileName(outputName);

        // Act
        processManager.executeEncryption();

        // Assert: Path should be [folder]/secret.txt.enc
        String expectedPath = destFolder + File.separator + "secret.txt.enc";
        assertEquals(expectedPath, mockService.lastPathUsed);
        assertEquals(srcFile, mockService.lastFileUsed);
    }

    @Test
    void testExecuteDecryptionRestoresExtension() throws IOException {
        // Arrange: Encrypted files usually end in .txt.enc
        File encryptedFile = createTestFile("data.txt.enc", 100);
        String destFolder = tempDir.toString();
        String outputName = "restored";

        processManager.setSrcFile(encryptedFile);
        processManager.setDestFolder(destFolder);
        processManager.setFileName(outputName);

        // Act
        processManager.executeDecryption();

        // Assert: Path should strip .enc and keep .txt -> [folder]/restored.txt
        String expectedPath = destFolder + File.separator + "restored.txt";
        assertEquals(expectedPath, mockService.lastPathUsed);
    }

    @Test
    void testResetAfterExecution() throws IOException {
        File srcFile = createTestFile("test.txt", 10);
        processManager.setSrcFile(srcFile);
        processManager.setDestFolder(tempDir.toString());
        processManager.setFileName("any");

        processManager.executeEncryption();

        // After reset, calling validation should fail (as fields are null)
        assertThrows(RuntimeException.class, () -> processManager.validateAllDataExist());
    }

    // Helper to create real files on disk for FileUtils validation
    private File createTestFile(String name, int size) throws IOException {
        Path filePath = tempDir.resolve(name);
        byte[] content = new byte[size];
        Files.write(filePath, content);
        return filePath.toFile();
    }

    // Simple Mock implementation of the service
    private static class MockEncryptionService implements EncryptionService {
        File lastFileUsed;
        String lastPathUsed;

        @Override
        public void encrypt(File src, String destPath) {
            this.lastFileUsed = src;
            this.lastPathUsed = destPath;
        }

        @Override
        public void decrypt(File src, String destPath) {
            this.lastFileUsed = src;
            this.lastPathUsed = destPath;
        }
    }
}
