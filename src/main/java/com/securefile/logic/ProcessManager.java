package com.securefile.logic;

import com.securefile.service.AppService;
import com.securefile.service.AESEncryptionService;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

public class ProcessManager {
    private final AppService appService = new AppService(new AESEncryptionService());
    private String baseDestFolder;
    private String baseFileName;
    private File srcFile;


    public void setSrcFile(File srcFile) {
        validateFile(srcFile);
        this.srcFile = srcFile;
        appService.setInputFile(srcFile);
    }

    public void setDestFolder(String destFolder, String fileName) {
        validateName(fileName);
        baseDestFolder = destFolder;
        baseFileName = fileName;
    }

    public void executeEncryption() {
        String extension = getFileExtension(srcFile);

        // Final Path
        String finalPath = baseDestFolder + File.separator + baseFileName + extension + ".enc";
        appService.setOutputPath(finalPath);
        appService.encryptFile();
    }

    public void executeDecryption() {
        String srcName = srcFile.getName();

        String finalFileName = baseFileName;

        if (srcName.toLowerCase().endsWith(".enc")) {
            // Remove .enc from encrypted file name
            String strippedSrc = srcName.substring(0, srcName.length() - 4);
            finalFileName = finalFileName + getFileExtension(strippedSrc);
        }

        String finalPath = baseDestFolder + File.separator + finalFileName;
        appService.setOutputPath(finalPath);
        appService.decryptFile();
    }

    private void validateName(String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("Name can not be blank.");
        }
        // Check if file name is valid for the OS
        try {
            Paths.get(name);
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException("The output file name contains invalid characters.");
        }

    }

    private void validateFile(File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException("File no longer exists.");
        }
    }

    public String getFileSize(File file) {
        if (file.length() < 1024) {
            return file.length() + " Bytes";
        }
        return (file.length() / 1024) + " KB";
    }

    public String getFileExtension(File file) {
        return getFileExtension(file.getName());
    }

    private String getFileExtension(String fileName) {
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i);
        }
        return extension;
    }
}
