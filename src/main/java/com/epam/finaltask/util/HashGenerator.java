package com.epam.finaltask.util;

import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashGenerator {

    private static final Logger logger = LogManager.getLogger();

    public String hash(String data, String salt, String algorithm) {
        //todo validate
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            String initialValue = data + salt;
            byte[] hash = messageDigest.digest(initialValue.getBytes(StandardCharsets.UTF_8));
            return Hex.encodeHexString(hash);
        } catch (NoSuchAlgorithmException e) {
            String message = "Hashing algorithm is not found";
            logger.log(Level.FATAL, message, e);
            throw new RuntimeException(message, e);
        }
    }
}
