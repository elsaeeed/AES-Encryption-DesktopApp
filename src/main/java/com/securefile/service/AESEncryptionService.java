package com.securefile.service;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;

public class AESEncryptionService implements EncryptionService {
    private final SecretKey secretKey = KeyFactory.getKey();
    private final String transformation = "AES/CBC/PKCS5Padding";
    private final String provider = "BC";

    static {
        // Register Bouncy Castle just once
        Security.addProvider(new BouncyCastleProvider());
    }

    @Override
    public void encrypt(File file, String outputDist){
        // 1. Create (IV) for CBC mode
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // 2. Initiate Cipher with Bouncy Castle("BC")
        try {
            Cipher cipher = Cipher.getInstance(transformation, provider);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

            try (FileInputStream fis = new FileInputStream(file);
                 FileOutputStream fos = new FileOutputStream(outputDist);
                 CipherOutputStream cos = new CipherOutputStream(fos, cipher)
            ) {
                // 3.Write IV at the beginning of the file
                fos.write(iv);
                fis.transferTo(cos);
            }
        }catch (Exception e) {
            throw new RuntimeException("Encryption failed: " + e.getMessage(), e);
        }
    }


    @Override
    public void decrypt(File file, String outputDist){
        try (FileInputStream fis = new FileInputStream(file)) {

            // 1. Read the IV
            byte[] iv = new byte[16];
            int ivBytes = fis.read(iv);
            if (ivBytes < 16) {
                throw new IOException("File is too short to be a valid encrypted file.");
            }
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // 2. Initialize Cipher for Decryption
            Cipher cipher =  Cipher.getInstance(transformation, provider);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

            try (CipherInputStream cis = new CipherInputStream(fis, cipher);
                 FileOutputStream fos = new FileOutputStream(outputDist)) {

                cis.transferTo(fos);
            }
        }catch (Exception e) {
            throw new RuntimeException("Decryption failed: " + e.getMessage(), e);
        }
    }

}