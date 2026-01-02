package com.securefile.logic;

import com.securefile.service.EncryptionService;

import java.io.File;



public class ProcessManager {
    private final EncryptionService encryptionService;
    private final String ENCRYPTION_EXTENTION = ".enc";
    private String destFolder;
    private String fileName;
    private File srcFile;

    public ProcessManager(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    public void executeEncryption() {
        validateAllDataExist();
        String finalPath = destFolder + File.separator + fileName + getFileExtension() + ENCRYPTION_EXTENTION;
        encryptionService.encrypt(srcFile, finalPath);
        reset();
    }

    public void executeDecryption() {
        validateAllDataExist();
        String finalFileName = fileName + getOriginalFileExtension();
        String finalPath = destFolder + File.separator + finalFileName;
        encryptionService.decrypt(srcFile, finalPath);
        reset();
    }

    public String getFileSize() {
        if (srcFile.length() < 1024) {
            return srcFile.length() + " Bytes";
        }
        return (srcFile.length() / 1024) + " KB";
    }

    public String suggestFileName() {
        String srcfileName = srcFile.getName();
        // name without extension
        int dotIndex = srcfileName.indexOf('.');
        if (dotIndex > 0) {
            return srcfileName.substring(0, dotIndex);
        }
        return srcfileName;
    }

    public String getFileExtension() {
        return FileUtils.getFileExtension(srcFile.getName());
    }


    public void validateAllDataExist() {
        FileUtils.validateFile(srcFile);
        FileUtils.validateFolder(destFolder);
        FileUtils.validateName(fileName);
    }


    private String getOriginalFileExtension() {
        int indexOfEncExt = srcFile.getName().lastIndexOf(ENCRYPTION_EXTENTION);
        String cleanSrcName = srcFile.getName().substring(0,indexOfEncExt);
        return FileUtils.getFileExtension(cleanSrcName);
    }

    private void reset() {
        destFolder = null;
        fileName = null;
        srcFile = null;
    }

    public void setSrcFile(File srcFile) {
        FileUtils.validateFile(srcFile);
        this.srcFile = srcFile;
    }

    public void setDestFolder(String destFolder) {
        FileUtils.validateFolder(destFolder);
        this.destFolder = destFolder;
    }

    public void setFileName(String fileName) {
        FileUtils.validateName(fileName);
        this.fileName = fileName;
    }
}
