package com.securefile.service;

import java.io.File;

public interface EncryptionService {
    void encrypt(File file, String outputDist);
    void decrypt(File file, String outputDist);
}
