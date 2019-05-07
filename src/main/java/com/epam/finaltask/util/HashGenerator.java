package com.epam.finaltask.util;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashGenerator {

    private static final Logger logger = LogManager.getLogger();

    public String hash(String data, String salt, String algorithm) {
        String dataToUse;
        if (data != null) {
           dataToUse = data;
        } else {
            logger.log(Level.WARN, "data is null, blank string will be used as data");
            dataToUse = StringUtils.EMPTY;
        }

        String saltToUse;
        if (salt != null) {
            saltToUse = salt;
        } else {
            logger.log(Level.WARN, "salt is null, blank string will be used as salt");
            saltToUse = StringUtils.EMPTY;
        }

        if (algorithm == null) {
            String message = "Hashing algorithm is null";
            logger.log(Level.FATAL, message);
            throw new RuntimeException(message);
        }

        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            String initialValue = dataToUse + saltToUse;
            byte[] hash = messageDigest.digest(initialValue.getBytes(StandardCharsets.UTF_8));
            return Hex.encodeHexString(hash);
        } catch (NoSuchAlgorithmException e) {
            String message = "Hashing algorithm is not found";
            logger.log(Level.FATAL, message, e);
            throw new RuntimeException(message, e);
        }
    }
}
