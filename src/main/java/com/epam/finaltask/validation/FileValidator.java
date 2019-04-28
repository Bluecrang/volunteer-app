package com.epam.finaltask.validation;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class FileValidator {

    private static final Logger logger = LogManager.getLogger();

    public boolean validate(String filename) {

        if (filename == null) {
            logger.log(Level.WARN, "filename argument is null, validation result is false");
            return false;
        }

        logger.log(Level.DEBUG, "argument " + filename);
        File file = new File(filename);

        boolean result = file.exists() && file.isFile() && file.canRead() && (file.length() != 0);
        logger.log(Level.DEBUG, "file exists: " + file.exists());
        logger.log(Level.DEBUG, "isFile: " + file.isFile());
        logger.log(Level.DEBUG, "canRead: " + file.canRead());
        logger.log(Level.DEBUG, "length !=0 : " + (file.length() != 0));
        logger.log(Level.DEBUG, "absolute path:" + file.getAbsolutePath());
        logger.log(Level.INFO, "file validation result is " + result);
        return result;
    }
}
