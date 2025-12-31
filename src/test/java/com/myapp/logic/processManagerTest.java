package com.myapp.logic;

import com.securefile.logic.ProcessManager;
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

    @TempDir
    Path tempDir; // Automatically creates and cleans up a temporary folder for tests

    @BeforeEach
    void setUp() {
        processManager = new ProcessManager();
    }

    @Test
    void GetFileSizeFormatting() throws IOException {
        Path file = tempDir.resolve("small.txt");
        Files.writeString(file, "Hello World"); // 11 bytes

        String size = processManager.getFileSize(file.toFile());
        assertEquals("11 Bytes", size);
    }

    @Test
    void SetDestFolderThrowsExceptionForBlankName() {
        assertThrows(IllegalArgumentException.class, () -> {
            processManager.setDestFolder(tempDir.toString(), "  ");
        }, "Should throw exception if filename is blank");
    }

    @Test
    void SetSrcFileThrowsExceptionForMissingFile() {
        File nonExistentFile = new File(tempDir.toFile(), "non_existent_file.txt");
        assertThrows(IllegalArgumentException.class, () -> {
            processManager.setSrcFile(nonExistentFile);
        }, "Should throw exception if source file doesn't exist");
    }

    @Test
    void SettersWorkWithValidInputs() throws IOException {
        // Create a dummy source file
        Path sourcePath = tempDir.resolve("input.data");
        Files.createFile(sourcePath);

        String destFolder = tempDir.toString();
        String outName = "encrypted_result";

        // Verify that these methods don't throw exceptions with valid data
        assertDoesNotThrow(() -> {
            processManager.setSrcFile(sourcePath.toFile());
            processManager.setDestFolder(destFolder, outName);
        });
    }

    @Test
    void testGetFileExtensionLogic() {
        assertEquals(".txt", processManager.getFileExtension(new File("test.txt")));
        assertEquals(".pdf", processManager.getFileExtension(new File("archive.tar.pdf")));
        assertEquals("", processManager.getFileExtension(new File("no_extension")));
    }

}