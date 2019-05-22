package com.epam.finaltask.validation;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * A validation class which can be used to check if file with chosen name exists and is not a directory.
 */
public class FileValidator {

    private static final Logger logger = LogManager.getLogger();

    /**
     * Checks if file with chosen name exists and is not directory.
     * @param filename name of the file
     * @return {@code true} is file exists and is not directory. If filename is null, returns {@code false}
     */
    public boolean validate(String filename) {

        if (filename == null) {
            logger.log(Level.WARN, "filename argument is null, validation result is false");
            return false;
        }
        logger.log(Level.DEBUG, "argument " + filename);
        File file = new File(filename);
        boolean result = file.exists() && file.isFile();
        logger.log(Level.INFO, filename + " validation result is " + result);
        return result;
    }
}
