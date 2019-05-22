package com.epam.finaltask.validation;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Validation class. Provides way to check if string is image filename.
 */
public class ImageFilenameValidator {

    private static final Logger logger = LogManager.getLogger();

    /**
     * Supported extensions: .jpeg, .jpg, .png, .bmp
     */
    private static final String[] extensions = {"jpeg", "jpg", "png", "bmp"};
    private static final char FILE_EXTENSION_SEPARATOR = '.';

    /**
     * Checks if filename is the name of the file with image extension.
     * @param filename image file name to be validated
     * @return {@code true} if filename is image file name. Returns {@code null} if filename is null or empty
     */
    public boolean validate(String filename) {
        if (filename != null && !filename.isEmpty()) {
            int lastDotIndex = filename.lastIndexOf(FILE_EXTENSION_SEPARATOR);
            if (lastDotIndex != -1) {
                String extension = filename.substring(lastDotIndex + 1);
                for (String validExtension : extensions) {
                    if (extension.equals(validExtension)) {
                        logger.log(Level.INFO, "filename " + filename + " successfully validated");
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
