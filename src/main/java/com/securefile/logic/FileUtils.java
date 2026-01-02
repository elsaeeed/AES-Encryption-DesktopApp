package com.securefile.logic;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;


public final class FileUtils {

    private FileUtils() {}

    public static String getFileExtension(String fileName) {
        if (fileName == null) return "";
        int i = fileName.lastIndexOf('.');
        return (i > 0) ? fileName.substring(i) : "";
    }

    public static void validateFile(File file) {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null.");
        }
        if (!file.exists()) {
            throw new IllegalArgumentException("The selected file not exists.");
        }
    }


    public static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("File name cannot be blank.");
        }
        try {
            Paths.get(name);
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException("The file name contains invalid characters.");
        }
    }

    public static void validateFolder(String folderPath) {
        if (folderPath == null || folderPath.isBlank()) {
            throw new IllegalArgumentException("Destination folder is missing.");
        }
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            throw new IllegalArgumentException("Destination folder is invalid or does not exist.");
        }
    }
}
