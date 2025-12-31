package com.securefile.service;

import java.io.File;
import java.io.IOException;

public class AppService {
    private final EncryptionService encryptionService;
    private File inputFile;
    private String outputPath;

    public AppService(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }


    public void encryptFile() {
        encryptionService.encrypt(inputFile, getOutputPath());
    }

    public void decryptFile() {
        encryptionService.decrypt(inputFile, getOutputPath());
    }


    public void setInputFile(File inputFile) {
        this.inputFile = inputFile;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        File file = new File(outputPath);

        try {
            if(file.getCanonicalFile().equals(inputFile.getCanonicalFile()))
                throw new IllegalArgumentException("Output must differ from input, Please choose a different path or change the file name");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.outputPath = outputPath;
    }
}
